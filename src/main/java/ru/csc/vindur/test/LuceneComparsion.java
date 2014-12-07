package ru.csc.vindur.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

import com.google.common.base.Stopwatch;


/**
 * @author Andrey Kokorev Created on 07.12.2014.
 */
public class LuceneComparsion {
    private static final Logger LOG = LoggerFactory
            .getLogger(TestExecutor.class);
    private static final int DOC_NUM = 100000;
    private static final int QUERY_NUM = 1000;
    private static final int QUERY_PARTS = 5;

    public void run()
    {
        SimpleTestBuilder test;
        TestExecutor te;

        LOG.info("Warm up Vindur");
        // Warm up
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30).init();

        te = new TestExecutor(new Engine.EngineBuilder(EWAHBitSet::new)
                .setStorages(test.getTypes()).setExecutor(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(SimpleTest.querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        LOG.info("Complex/EWH test");
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30).init();

        te = new TestExecutor(new Engine.EngineBuilder(EWAHBitSet::new)
                .setStorages(test.getTypes()).setExecutor(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(SimpleTest.querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30).init();

        Stopwatch watch = Stopwatch.createUnstarted();

        Analyzer analyzer = new StandardAnalyzer(Version.LATEST);
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
                analyzer);
        try {
            LOG.info("Loading data into lucene");
            IndexWriter writer = new IndexWriter(directory, config);
            watch.start();

            luceneLoadDocs(writer, test, DOC_NUM);
            writer.close();

            watch.stop();
            LOG.info("Data loaded");
            LOG.info("Loading time: {} millis", watch.elapsed(TimeUnit.MILLISECONDS));

            LOG.info("Warm up lucene");
            lucenePerformSearch(analyzer, test, directory, watch);

            watch.reset();
            LOG.info("Search data in lucene");
            long resultCount = lucenePerformSearch(analyzer, test, directory, watch);

            LOG.info("Found {} results", resultCount);
            LOG.info("Search finished in {} millis", watch.elapsed(TimeUnit.MILLISECONDS));
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long lucenePerformSearch(Analyzer analyzer, TestBuilder test, Directory directory, Stopwatch watch)
            throws IOException
    {
        watch.start();
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        watch.stop();

        long resultCount = 0;
        Supplier<org.apache.lucene.search.Query> querySupplier = luceneQuerySupplier(analyzer, test, QUERY_PARTS);
        for(int i = 0; i < QUERY_NUM; i++)
        {
            org.apache.lucene.search.Query query = querySupplier.get();

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

        for (int i = 0; i < docNumber; i++) {
            Map<String, List<Object>> generated = supplier.get();
            Document doc = new Document();
            for (String attr : generated.keySet()) {
                switch (types.get(attr)) {
                case INTEGER:
                case RANGE_INTEGER:
                    for (Object val : generated.get(attr))
                        doc.add(new IntField(attr, (int) val, Field.Store.YES));
                    break;
                case STRING:
                case RANGE_STRING:
                case LUCENE_STRING:
                default:
                    for (Object val : generated.get(attr))
                        doc.add(new StringField(attr, (String) val,
                                Field.Store.YES));
                    break;
                }
            }
            writer.addDocument(doc);
        }
    }

    private Supplier<org.apache.lucene.search.Query> luceneQuerySupplier(
            Analyzer analyzer, final TestBuilder test, int partsInQuery) {
        return () -> {
            String[] fields = new String[partsInQuery];
            String[] queries = new String[partsInQuery];

            int i = 0;
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(),
                    partsInQuery)) {
                Object val = RandomUtils.gaussianRandomElement(
                        test.getValues(attr), 0.5, 1.0 / 6);
                fields[i] = attr;
                queries[i] = val.toString();
                i++;
            }
            try {
                return MultiFieldQueryParser.parse(queries, fields, analyzer);
            } catch (ParseException e) {
                LOG.error("Query parse exception {}", e.getLocalizedMessage());
                return null;
            } catch (IllegalArgumentException e) {
                LOG.error("Query illegal argument exception {}",
                        e.getLocalizedMessage());
                return null;
            }
        };
    }

    public static void main(String[] args) {
        new LuceneComparsion().run();
    }
}
