package ru.csc.vindur.test.comparison;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.TinyExecutor;
import ru.csc.vindur.storage.*;

import com.google.common.base.Stopwatch;
import ru.csc.vindur.test.SimpleTest;
import ru.csc.vindur.test.TestBuilder;
import ru.csc.vindur.test.TestExecutor;
import ru.csc.vindur.test.TunableTestBuilder;
import ru.csc.vindur.test.utils.RandomUtils;


/**
 * @author Andrey Kokorev Created on 07.12.2014.
 */
public class LuceneComparsion {
    private static final Logger LOG = LoggerFactory.getLogger(TestExecutor.class);
    private static final int DOC_NUM = 100000;
    private static final int QUERY_NUM = 10000;
    private static final int QUERY_PARTS = 1;

    public void run()
    {
        TunableTestBuilder test;
        TestExecutor te;

//        LOG.info("Warm up Vindur");
//        test = TunableTestBuilder.build()
//                .storage("S1", StorageType.STRING, 30, 1.0)
//                .storage("S2", StorageType.STRING, 50, 1.0)
//                .storage("S3", StorageType.STRING, 50, 1.0)
//                .storage("S4", StorageType.STRING, 50, 1.0)
//                .storage("S5", StorageType.STRING, 50, 1.0)
//                .storage("S6", StorageType.STRING, 50, 1.0)
//                .storage("S7", StorageType.STRING, 50, 1.0)
//                .storage("S8", StorageType.STRING, 50, 1.0)
//         .init();
//        te = new TestExecutor(test.buildEngine(new DumbExecutor()));
//        te.setDocumentSupplier(SimpleTest.docSupplier(test));
//        te.setQuerySupplier(querySupplier(test, QUERY_PARTS));
//        te.execute(DOC_NUM, QUERY_NUM);

        LOG.info("Complex/EWH test");
        test = TunableTestBuilder.build()
//                .storage("S1", StorageType.STRING, 2000, 1.0) //category
//                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
//                .storage("S3", StorageType.STRING, 2, 1.0) //sex
//                .storage("S4", StorageType.STRING, 10, 1.0) //age
//                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
//                .storage("S6", StorageType.STRING, 100, 0.2) //?
//                .storage("S7", StorageType.STRING, 1000, 0.01)
//                .storage("S8", StorageType.STRING, 1000, 0.001)
                .storage("I1", StorageType.RANGE_STRING, 20000, 1.0) //price
//                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
//                .storage("L1", StorageType.LUCENE_STRING, DOC_NUM, 0.8) //desc
                .init();

        Engine en = test.buildEngine(new TinyExecutor());
        te = new TestExecutor(en);
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(querySupplier(test, QUERY_PARTS));
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
            luceneLoadDocs(writer, en);
            writer.commit();
            writer.close();
            watch.stop();
           LOG.info("Loading time: {} millis", watch.elapsed(TimeUnit.MILLISECONDS));

//            LOG.info("Warm up lucene");
//            lucenePerformSearch(analyzer, test, directory, watch, QUERY_NUM);

            watch.reset();
            LOG.info("Search data in lucene");
            long resultCount = lucenePerformSearch(analyzer, test, directory, watch);

            LOG.info("Found {} results", resultCount);
            LOG.info("Search finished in {} millis", watch.elapsed(TimeUnit.MILLISECONDS));
            LOG.info("Average request time {} millis", watch.elapsed(TimeUnit.MILLISECONDS) * 1.0 / QUERY_NUM);
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static List<Query> stqueries = new ArrayList<>();

    static Supplier<Query> querySupplier(final TestBuilder test,
                                         int partsInQuery) {
        return () -> {
            Query query = Query.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(),
                    partsInQuery))
            {
                StorageType type = test.getTypes().get(attr);
                Object val=null;
                switch (type)
                {
                    case STRING:
                    case INTEGER:
                        val =  RandomUtils.gaussianRandomElement(
                                 test.getValues(attr), 0.5, 1.0 / 6);
                        break;
                    case RANGE_INTEGER:
                    case RANGE_STRING:
                        Comparable v1 = (Comparable)RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        Comparable v2 = (Comparable)RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        val = StorageRange.range(min(v1, v2), max(v1, v2));
                        break;
                    case LUCENE_STRING:
                        Object o = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        try
                        {
                            val = new QueryParser("text", new WhitespaceAnalyzer()).parse(o.toString());
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                query.query(attr, val);
            }
            stqueries.add(query);
            return query;
        };
    }


    private long lucenePerformSearch(Analyzer analyzer, TestBuilder test,
                                     Directory directory, Stopwatch watch)
            throws IOException
    {
        watch.start();
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        watch.stop();

        long resultCount = 0;
        for(int i = 0; i < stqueries.size(); i++)
        {
            org.apache.lucene.search.Query query = this.query(stqueries.get(i),analyzer);

//            LOG.info("query {} -> {}", stqueries.get(i).toString(), query.toString());

            watch.start();
            TopDocs hits = isearcher.search(query, null, DOC_NUM);
            watch.stop();
//            if (hits.scoreDocs.length>0)
//            {
//                LOG.info("result {}", isearcher.doc(hits.scoreDocs[0].doc));
//                for (ScoreDoc sd :  hits.scoreDocs)
//                    LOG.info( ireader.document( sd.doc ).toString());
//
//            }
//            else
//              LOG.info("result none");

            resultCount += hits.totalHits;
        }
        ireader.close();

        return resultCount;
    }

    private void luceneLoadDocs(IndexWriter writer, Engine engine) throws IOException
    {
        for (int i = 0; i < engine.getDocumentCount(); i++)
        {
            ru.csc.vindur.document.Document generated = engine.getDocument(i);
            Document doc = new Document();
            for (String attr : generated.getAttributes())
            {
                Storage s = engine.getStorages().get(attr);

                for (Object val : generated.getValues(attr))
                  {
                     if (val instanceof Integer)
                     {
                         doc.add(new StringField(attr, val.toString(), Field.Store.YES));
//                         LOG.error("add int {}",val);
                     }
                     else {
                         doc.add(new StringField(attr, (String) val, Field.Store.YES));
//                         LOG.error("add string {}",val);
                     }
                 }
            }
            writer.addDocument(doc);
        }
    }

    private org.apache.lucene.search.Query query(Query vq, Analyzer analyzer)
    {
        String query="";
        QueryParser parser = new QueryParser("test",analyzer);

        for (Map.Entry<String, Object> en : vq.getQueryParts().entrySet())
        {
           Object v = en.getValue();
           String sq="";
           if (v instanceof RangeRequest)
           {
             RangeRequest r = (RangeRequest)v;
             sq=en.getKey()+":["+r.getLowBound()+" TO "+r.getUpperBound()+"]";
           }
           else if (v instanceof Integer)
           {
               Integer i = (Integer)v;
               sq=en.getKey()+":"+i.toString();
           }
           else if (v instanceof org.apache.lucene.search.Query)
           {
               org.apache.lucene.search.Query q = (org.apache.lucene.search.Query)v;
               sq = q.toString();
           }
           else
            sq=en.getKey()+":"+v;

          query = query+" AND " + sq;
        }

        query = query.substring(5);

        try {
            return parser.parse(query);
        } catch (Exception e) {
            LOG.error("Query parse exception", e);
        }
        return null;
    }

    private static Comparable max (Comparable a, Comparable b)
    {
        if (a.compareTo(b)>=0) return a;
        return b;
    }

    private static Comparable min (Comparable a, Comparable b)
    {
        if (a.compareTo(b)<0) return a;
        return b;
    }


    public static void main(String[] args) {
        new LuceneComparsion().run();
    }
}
