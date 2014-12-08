package ru.csc.vindur.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.EWAHBitArray;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.storage.StorageType;

import com.google.common.base.Stopwatch;
import ru.csc.vindur.test.utils.RandomUtils;


/**
 * @author Andrey Kokorev Created on 07.12.2014.
 */
public class LuceneComparsion {
    private static final Logger LOG = LoggerFactory
            .getLogger(TestExecutor.class);
    private static final int DOC_NUM = 500000;
    private static final int QUERY_NUM = 10000;
    private static final int QUERY_PARTS = 4;

    public void run()
    {
        TunableTestBuilder test;
        TestExecutor te;

        LOG.info("Warm up Vindur");
        test = TunableTestBuilder.build()
                .storage("S1", StorageType.STRING, 30, 1.0)
                .storage("S2", StorageType.STRING, 50, 1.0)
                .storage("S3", StorageType.STRING, 50, 1.0)
                .storage("S4", StorageType.STRING, 50, 1.0)
                .storage("S5", StorageType.STRING, 50, 1.0)
                .storage("S6", StorageType.STRING, 50, 1.0)
                .storage("S7", StorageType.STRING, 50, 1.0)
                .storage("S8", StorageType.STRING, 50, 1.0)
         .init();
        te = new TestExecutor(test.buildEngine(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(SimpleTest.querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        LOG.info("Complex/EWH test");
        test = TunableTestBuilder.build()
                .storage("S1", StorageType.STRING, 2000, 1.0) //category
                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
                .storage("S3", StorageType.STRING, 2, 1.0) //sex
                .storage("S4", StorageType.STRING, 10, 1.0) //age
                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
                .storage("S6", StorageType.STRING, 100, 0.2) //?
                .storage("S7", StorageType.STRING, 1000, 0.01)
                .storage("S8", StorageType.STRING, 1000, 0.001)
//                .storage("I1", StorageType.RANGE_INTEGER, 10000, 1.0) //price
//                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
//                .storage("L1", StorageType.LUCENE_STRING, DOC_NUM, 0.8) //desc
                .init();

        te = new TestExecutor(test.buildEngine(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(SimpleTest.querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        // LUCENE!
        Stopwatch watch = Stopwatch.createUnstarted();

        Analyzer analyzer = new WhitespaceAnalyzer(Version.LATEST);
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

        try {
            LOG.info("Loading data into lucene");
            IndexWriter writer = new IndexWriter(directory, config);
            watch.start();
            luceneLoadDocs(writer, test, DOC_NUM);
            writer.commit();
            writer.close();
            watch.stop();
            LOG.info("Loading time: {} millis", watch.elapsed(TimeUnit.MILLISECONDS));

            LOG.info("Warm up lucene");
            lucenePerformSearch(analyzer, test, directory, watch, QUERY_NUM);

            watch.reset();
            LOG.info("Search data in lucene");
            long resultCount = lucenePerformSearch(analyzer, test, directory, watch, QUERY_NUM);

            LOG.info("Found {} results", resultCount);
            LOG.info("Search finished in {} millis", watch.elapsed(TimeUnit.MILLISECONDS));
            LOG.info("Average request time {} millis", watch.elapsed(TimeUnit.MILLISECONDS) * 1.0 / QUERY_NUM);
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//todo сделать нормальные query
    static Supplier<Query> querySupplier(final TestBuilder test,
                                         int partsInQuery) {
        return () -> {
            Query query = Query.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(),
                    partsInQuery)) {
                Object val = RandomUtils.gaussianRandomElement(
                        test.getValues(attr), 0.5, 1.0 / 6);
                query.query(attr, val);
            }
            return query;
        };
    }










    private long lucenePerformSearch(Analyzer analyzer, TestBuilder test,
                                     Directory directory, Stopwatch watch, int queries)
            throws IOException
    {
        watch.start();
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        Supplier<Query> vqs = SimpleTest.querySupplier(test,QUERY_PARTS);

        watch.stop();

        long resultCount = 0;
        for(int i = 0; i < queries; i++)
        {
            org.apache.lucene.search.Query query = this.query(vqs.get(),analyzer);

            watch.start();
            ScoreDoc[] hits = isearcher.search(query, null, DOC_NUM).scoreDocs;
            watch.stop();

            resultCount += hits.length;
        }
        ireader.close();

        return resultCount;
    }

    private void luceneLoadDocs(IndexWriter writer, TestBuilder test,
            int docNumber) throws IOException
    {
        Map<String, StorageType> types = test.getTypes();
        Supplier<Map<String, List<Object>>> supplier = SimpleTest.docSupplier(test);

        for (int i = 0; i < docNumber; i++)
        {
            Map<String, List<Object>> generated = supplier.get();
            Document doc = new Document();
            for (String attr : generated.keySet())
            {
                switch (types.get(attr))
                {
                case INTEGER:
                case RANGE_INTEGER:
                    for (Object val : generated.get(attr))
                    {
                        doc.add(new IntField(attr, (int) val, Field.Store.YES));
                    }
                    break;
                case STRING:
                case RANGE_STRING:
                case LUCENE_STRING:
                default:
                    for (Object val : generated.get(attr))
                    {
                        doc.add(new StringField(attr, (String) val,
                                Field.Store.YES));
                    }
                    break;
                }
            }
            writer.addDocument(doc);
        }
    }


    //todo научиться нормально конвертировать!
    private org.apache.lucene.search.Query query(Query vq, Analyzer analyzer)
    {
        int partsInQuery = vq.getQueryParts().size();

        String[] fields = new String[partsInQuery];
        String[] queries = new String[partsInQuery];
        BooleanClause.Occur[] bOccur = new BooleanClause.Occur[partsInQuery];

        int i = 0;
        for (Map.Entry<String, Object> en : vq.getQueryParts().entrySet()) {
            fields[i] = en.getKey();
            queries[i] = en.getValue().toString();
            bOccur[i] = BooleanClause.Occur.MUST;
            i++;
        }
        try {
            return MultiFieldQueryParser.parse(queries, fields, bOccur, analyzer);
        } catch (Exception e) {
            LOG.error("Query parse exception", e);
        }
        return null;
    }


    public static void main(String[] args) {
        new LuceneComparsion().run();
    }
}
