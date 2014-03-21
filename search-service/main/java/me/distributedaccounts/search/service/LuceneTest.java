package me.distributedaccounts.search.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class LuceneTest {
    private static final Version version = Version.LUCENE_47;

    private static final String accountIdFieldName = "accountId";
    private static final String descriptionFieldName = "description";

    public static void main(String[] args) throws IOException, ParseException {
        // Store the index in memory:
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(version);

        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");

        IndexWriterConfig config = new IndexWriterConfig(version, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        String text = "This is the text to be indexed.";
        String accountId = "vjeran";

        Document doc = new Document();
        doc.add(new Field(descriptionFieldName, text, TextField.TYPE_STORED));
        doc.add(new Field(accountIdFieldName, accountId, TextField.TYPE_STORED));

        indexWriter.addDocument(doc);
        indexWriter.close();

        // Now search the index:
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        // Parse a simple query that searches for "text":
        QueryParser queryParser = new QueryParser(version, descriptionFieldName, analyzer);
        Query query = queryParser.parse("text");
        ScoreDoc[] hits = indexSearcher.search(query, null, 1000).scoreDocs;

//        assertEquals(1, hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = indexSearcher.doc(hits[i].doc);
//            assertEquals("This is the text to be indexed.", hitDoc.get(descriptionFieldName));
        }

        directoryReader.close();
        directory.close();
    }
}
