package me.distributedaccounts.search.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AccountDescriptionIndexImpl implements AccountDescriptionIndex, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(AccountDescriptionIndexImpl.class);

    private File indexFile;

    private static final Version version = Version.LUCENE_47;
    private static final String accountIdFieldName = "accountId";
    private static final String descriptionFieldName = "description";

    private Directory directory;
    private Analyzer analyzer;

    @Override
    public void afterPropertiesSet() throws Exception {
        directory = FSDirectory.open(indexFile);
        analyzer = new StandardAnalyzer(version);
    }

    @Override
    public void destroy() throws Exception {
        if (directory != null) {
            directory.close();
        }
    }

    @Override
    public void addAccountDescription(String accountId, String description) {
        Objects.requireNonNull(accountId, "accountId is null");
        Objects.requireNonNull(description, "description is null");

        logger.debug("Adding account description to index: accountId=" + accountId + ", description=" + description);

        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(version, analyzer);
            indexWriter = new IndexWriter(directory, config);

            Document doc = convertAccountDataToDocument(accountId, description);
            indexWriter.addDocument(doc);

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private Document convertAccountDataToDocument(String accountId, String description) {
        Document doc = new Document();
        doc.add(new Field(descriptionFieldName, description, TextField.TYPE_STORED));
        doc.add(new Field(accountIdFieldName, accountId, TextField.TYPE_STORED));
        return doc;
    }

    @Override
    public void removeAccountDescription(String accountId) {
        logger.debug("Removing account description from index: accountId=" + accountId);
        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(version, analyzer);
            indexWriter = new IndexWriter(directory, config);

            indexWriter.deleteDocuments(new Term(accountIdFieldName, accountId));

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> findByDescription(String description) {
        Objects.requireNonNull(description, "description is null");
        logger.debug("Finding accounts by description: " + description);

        List<Map<String, Object>> accounts = new ArrayList<>();

        DirectoryReader directoryReader = null;
        try {
            directoryReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            // Parse a simple query that searches for description field value:
            QueryParser queryParser = new QueryParser(version, descriptionFieldName, analyzer);
            Query query = queryParser.parse(description);
            ScoreDoc[] hits = indexSearcher.search(query, null, 1000).scoreDocs;

            for (ScoreDoc hit : hits) {
                Document hitDoc = indexSearcher.doc(hit.doc);
                Map<String, Object> accountData = convertDocumentToAccountData(hitDoc);
                accounts.add(accountData);
            }

            return accounts;

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);

        } finally {
            if (directoryReader != null) {
                try {
                    directoryReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private Map<String, Object> convertDocumentToAccountData(Document doc) {
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", doc.getField(accountIdFieldName).stringValue());
        map.put("description", doc.getField(descriptionFieldName).stringValue());
        return map;
    }

    public void setIndexFile(File indexFile) {
        this.indexFile = indexFile;
    }
}
