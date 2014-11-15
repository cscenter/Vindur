package ru.csc.vindur.storage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

public class StorageLucene implements Storage {

	private static final String ID_FIELD_NAME = "id";
	private static final String VALUE_FIELD_NAME = "text";
	private final Directory luceneIndex;
	private final BitSetFabric bitSetFabric;
	private final AtomicInteger activeSearches = new AtomicInteger(0);
	private final AtomicInteger activeWrites = new AtomicInteger(0);
	private final AtomicInteger documentsCount = new AtomicInteger(0);
	private final WhitespaceAnalyzer analyzer;
	private IndexSearcher searcher;
	private IndexWriter indexWriter;
	private DirectoryReader indexReader;

	public StorageLucene(BitSetFabric bitSetFabric) {
		this.bitSetFabric = bitSetFabric;
		luceneIndex = new RAMDirectory();
		analyzer = new WhitespaceAnalyzer();
	}

	@Override
	public long size() {
		return documentsCount.longValue();
	}

	@Override
	public void add(int docId, Value value) {
		// TODO check synchronization
		try {
			Document newDocument = new Document();
			newDocument.add(new TextField(VALUE_FIELD_NAME, value.getValue(), Store.NO));
			newDocument.add(new IntField(ID_FIELD_NAME, docId, Store.YES));
			activeWrites.incrementAndGet();
			if (indexWriter == null) {
				synchronized (this) {
					if (indexWriter == null) {
						indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(Version.LATEST, analyzer));
					}
				}
			}
			indexWriter.addDocument(newDocument);
			documentsCount.incrementAndGet();
			activeWrites.decrementAndGet();
		} catch (IOException e) {
			// TODO investigate this
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method use classic Lucene query parser
	 * It search through default field where the Value is stored
	 * It supports many different searches
	 * @param match query string
	 * @return set of found document id's
	 * @see http://lucene.apache.org/core/4_0_0/queryparser/index.html
	 */
	@Override
	public BitSet findSet(String match) {
		// TODO check synchronization
		try {
			activeSearches.incrementAndGet();
			synchronized (this) {
				if (indexWriter != null && activeWrites.get() == 0) {
					indexWriter.close();
					indexWriter = null;
				}
				if (searcher != null && !indexReader.isCurrent()
						&& activeSearches.get() == 1) {
					indexReader.close();
					searcher = null;
				}

				if (searcher == null) {
					indexReader = DirectoryReader.open(luceneIndex);
					searcher = new IndexSearcher(indexReader);
				}
			}

			try {
				Query q = new QueryParser(VALUE_FIELD_NAME, analyzer).parse(match);
				BitSet result = bitSetFabric.newInstance();
				for (ScoreDoc doc : searcher.search(q, documentsCount.get()).scoreDocs) {
					IndexableField f = indexReader.document(doc.doc).getField(
							ID_FIELD_NAME);
					result.set(f.numericValue().intValue());
				}
				return result;
			}  finally {
				activeSearches.decrementAndGet();
			}
		} catch (IOException e) {
			// TODO investigate this
			throw new RuntimeException(e);
		} catch (ParseException e) {
			// TODO move this exception out
			throw new RuntimeException(e);
		}
	}
}
