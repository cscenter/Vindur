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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

public class StorageLucene extends StorageBase<String, Query>
{

    private static final String ID_FIELD_NAME = "id";
    private static final String VALUE_FIELD_NAME = "text";
    private final Directory luceneIndex;
    private final WhitespaceAnalyzer analyzer;
    private final Supplier<BitSet> bitSetSupplier;
    private IndexSearcher searcher;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;

    public StorageLucene(Supplier<BitSet> bitSetSupplier)
    {
    	super(String.class, Query.class);
        this.bitSetSupplier = bitSetSupplier;
        luceneIndex = new RAMDirectory();
        analyzer = new WhitespaceAnalyzer();
        try
        {
            indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(Version.LATEST, analyzer));
        }
        catch (IOException e)
        {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(int docId, String value)
    {
        try
        {
            Document newDocument = new Document();
            newDocument.add(new TextField(VALUE_FIELD_NAME, value, Store.NO));
            newDocument.add(new IntField(ID_FIELD_NAME, docId, Store.YES));
            if (indexWriter == null)
            {
                indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(Version.LATEST, analyzer));
            }
            indexWriter.addDocument(newDocument);
           	incrementDocumentsCount();
        }
        catch (IOException e)
        {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        }
    }

    @Override
    public ROBitSet findSet(Query q)
    {
        try
        {
            if (indexWriter != null)
            {
                indexWriter.close();
                indexWriter = null;
            }
            if (searcher != null && !indexReader.isCurrent())
            {
                indexReader.close();
                searcher = null;
            }

            if (searcher == null)
            {
                indexReader = DirectoryReader.open(luceneIndex);
                searcher = new IndexSearcher(indexReader);
            }

            BitSet result = bitSetSupplier.get();
            for (ScoreDoc doc : searcher.search(q, documentsCount()).scoreDocs)
            {
                IndexableField f = indexReader.document(doc.doc).getField(
                        ID_FIELD_NAME);
                result.set(f.numericValue().intValue());
            }
            return result;
        }
        catch (IOException e)
        {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        }
    }

	@Override
	public boolean checkValue(String value, Query request) {
		// TODO Auto-generated method stub
		return false;
	}
}
