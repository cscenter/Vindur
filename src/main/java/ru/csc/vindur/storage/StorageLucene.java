package ru.csc.vindur.storage;

import java.io.IOException;
import java.util.function.Supplier;

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
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

public class StorageLucene implements ExactStorage {

	private static final String ID_FIELD_NAME = "id";
	private static final String VALUE_FIELD_NAME = "text";
	private final Directory luceneIndex;
	private final WhitespaceAnalyzer analyzer;
    private final Supplier<BitSet> bitSetSupplier;
	private int documentsCount = 0;
	private IndexSearcher searcher;
	private IndexWriter indexWriter;
	private DirectoryReader indexReader;

	public StorageLucene(Supplier<BitSet> bitSetSupplier) {
		this.bitSetSupplier = bitSetSupplier;
		luceneIndex = new RAMDirectory();
		analyzer = new WhitespaceAnalyzer();
		try {
			indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(Version.LATEST, analyzer));
		} catch (IOException e) {
			// cannot happen because of using RAMDirectory as index
			throw new RuntimeException(e);
		}
	}

	@Override
	public long size() {
		return documentsCount;
	}

	@Override
	public void add(int docId, Value value) {
		try {
			Document newDocument = new Document();
			newDocument.add(new TextField(VALUE_FIELD_NAME, value.getValue(), Store.NO));
			newDocument.add(new IntField(ID_FIELD_NAME, docId, Store.YES));
			if (indexWriter == null) {
				indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(Version.LATEST, analyzer));
			}
			indexWriter.addDocument(newDocument);
			documentsCount += 1;
		} catch (IOException e) {
			// cannot happen because of using RAMDirectory as index
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method use classic Lucene query parser
	 * It search through default field where the Value is stored
	 * It supports many different searches
	 * @param request query string
	 * @return set of found document id's
	 * @throws ParseException when request is not correct query string
	 * @see http://lucene.apache.org/core/4_0_0/queryparser/index.html
	 */
	@Override
	public ROBitSet findSet(String request)
    {
		try {
			if (indexWriter != null) {
				indexWriter.close();
				indexWriter = null;
			}
			if (searcher != null && !indexReader.isCurrent()) {
				indexReader.close();
				searcher = null;
			}

			if (searcher == null) {
				indexReader = DirectoryReader.open(luceneIndex);
				searcher = new IndexSearcher(indexReader);
			}

			Query q = new QueryParser(VALUE_FIELD_NAME, analyzer).parse(request);
			BitSet result = bitSetSupplier.get();
			for (ScoreDoc doc : searcher.search(q, documentsCount).scoreDocs) {
				IndexableField f = indexReader.document(doc.doc).getField(
						ID_FIELD_NAME);
				result.set(f.numericValue().intValue());
			}
			return result;
		}
        catch (Exception e)
        {
			throw new RuntimeException(e);
		}
	}
		
}
