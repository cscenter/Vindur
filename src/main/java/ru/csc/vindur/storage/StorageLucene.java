package ru.csc.vindur.storage;

import java.io.IOException;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.EWAHBitArray;
import ru.csc.vindur.bitset.ROBitArray;

public class StorageLucene extends Storage<String, String> {

    private static final String ID_FIELD_NAME = "id";
    private static final String VALUE_FIELD_NAME = "text";
    private static final WhitespaceAnalyzer ANALYZER = new WhitespaceAnalyzer();
    private static final QueryParser QUERY_PARSER = new QueryParser(
            VALUE_FIELD_NAME, ANALYZER);
    private final Directory luceneIndex;
    private IndexSearcher searcher;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;

    public StorageLucene() {
        super(String.class, String.class);
        luceneIndex = new RAMDirectory();
        try {
            indexWriter = new IndexWriter(luceneIndex, new IndexWriterConfig(
                    Version.LATEST, ANALYZER));
        } catch (IOException e) {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(int docId, String value) {
        try {
            Document newDocument = new Document();
            newDocument.add(new TextField(VALUE_FIELD_NAME, value, Store.NO));
            newDocument.add(new IntField(ID_FIELD_NAME, docId, Store.YES));
            if (indexWriter == null) {
                indexWriter = new IndexWriter(luceneIndex,
                        new IndexWriterConfig(Version.LATEST, ANALYZER));
            }
            indexWriter.addDocument(newDocument);
            incrementDocumentsCount();
        } catch (IOException e) {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        }
    }

    //todo: pass string with wildcards
    @Override
    public ROBitArray findSet(String sq) {
        try {
            createSeacher();
            Query q = new QueryParser(VALUE_FIELD_NAME,
                    new WhitespaceAnalyzer()).parse(sq);

            BitArray result = BitArray.create();
            for (ScoreDoc doc : searcher.search(q, documentsCount()).scoreDocs) {
                IndexableField f = indexReader.document(doc.doc).getField(
                        ID_FIELD_NAME);
                result.set(f.numericValue().intValue());
            }
            return result;
        } catch (IOException e) {
            // cannot happen because of using RAMDirectory as index
            throw new RuntimeException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new EWAHBitArray();
    }

    private void createSeacher() throws IOException {
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
    }

    //todo: pass string with wildcards, and change findSet()
    @Override
    public boolean checkValue(int docId, String value, String request)
    {
        return request.equals(value) || value.toLowerCase().contains(request.toLowerCase()) || value.matches(request);
    }

    /**
     * 
     * @param requestString
     *            as described in Lucene query documentation
     * @return generated query
     * @throws ParseException
     */
    public static Query query(String requestString)
            throws ParseException {
        return QUERY_PARSER.parse(requestString);
    }

    @Override
    public int getComplexity() {
        return 1000;
    }
}
