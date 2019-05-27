package no.regnskap.integration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

public class ServiceTest {

    private static final int MONGO_PORT = 27017;

    @ClassRule
    public static GenericContainer mongo = new GenericContainer("mongo:latest")
        .withExposedPorts(MONGO_PORT);


    @Test
    public void simpleMongoDbTest() {
        MongoClient mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getMappedPort(MONGO_PORT));
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("testCollection");

        Document doc = new Document("name", "foo")
            .append("value", 1);
        collection.insertOne(doc);

        Document doc2 = collection.find(new Document("name", "foo")).first();
        Assert.assertEquals("A record can be inserted into and retrieved from MongoDB", 1, doc2.get("value"));
    }
}
