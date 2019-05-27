package no.regnskap.integration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import no.regnskap.model.RegnskapDB;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static no.regnskap.TestDatabaseValues.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ServiceTest {
    private static MongoCollection<RegnskapDB> mongoCollection;

    @ClassRule
    public static GenericContainer mongo = new GenericContainer(DOCKER_IMAGE)
        .withExposedPorts(MONGO_PORT);

    @BeforeClass
    public static void mongoSetup() {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClient mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getMappedPort(MONGO_PORT));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME).withDocumentClass(RegnskapDB.class);

        mongoCollection.insertOne(regnskap2017);
    }

    @Test
    public void simpleMongoDbTest() {
        RegnskapDB doc2 = mongoCollection.find().first();
        Assert.assertEquals("A record can be inserted into and retrieved from MongoDB", regnskap2017.getRegnaar(), doc2.getRegnaar());
    }
}
