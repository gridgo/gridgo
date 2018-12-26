package io.gridgo.example.mongodb_vertx;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.Document;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoHelper {

    private static final int NUM_RECORDS = 10;

    private static final String[] NAMES = new String[] { "Peter", "Mary", "Sophie", "Bob", "Mark" };

    public void prepareDatabase(MongoClient mongo, String databaseName, String collectionName)
            throws InterruptedException {
        var latch = new CountDownLatch(1);
        var db = mongo.getDatabase(databaseName);
        var collection = db.getCollection(collectionName);
        collection.drop((a, b) -> {
            log.info("Drop collection completed");
            db.createCollection("testCols", (result, throwable) -> {
                log.info("Create collection completed");
                populateDummyData(collection);
                latch.countDown();
            });
        });
        latch.await();
    }

    private void populateDummyData(MongoCollection<Document> collection) {
        var latch = new CountDownLatch(1);

        collection.insertMany(createInsertDocuments(), (result, ex) -> {
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException caught when populating dummy data. Exiting now...");
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    private List<Document> createInsertDocuments() {
        return IntStream.range(1, NUM_RECORDS) //
                        .mapToObj(this::createDummyDocument) //
                        .collect(Collectors.toList());
    }

    private Document createDummyDocument(int i) {
        return new Document("key", i).append("name", createRandomName());
    }

    private String createRandomName() {
        double ran = Math.random();
        int random = (int) ((NAMES.length - 1) * ran);
        return NAMES[random];
    }
}
