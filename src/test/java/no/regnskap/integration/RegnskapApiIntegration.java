package no.regnskap.integration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import no.regnskap.model.RegnskapDB;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static no.regnskap.TestData.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RunWith(MockitoJUnitRunner.class)
public class RegnskapApiIntegration {
    private static MongoCollection<RegnskapDB> mongoCollection;
    private static String regnskapRootURL;

    @ClassRule
    public static DockerComposeContainer compose = new DockerComposeContainer<>(new File("src/test/resources/test-compose.yml"))
        .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
        .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ping").forStatusCode(200))
        .withPull(false);

    @BeforeClass
    public static void mongoSetup() {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClient mongoClient = new MongoClient(compose.getServiceHost(MONGO_SERVICE_NAME, MONGO_PORT), compose.getServicePort(MONGO_SERVICE_NAME, MONGO_PORT));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME).withDocumentClass(RegnskapDB.class);

        mongoCollection.insertOne(regnskap2017);

        regnskapRootURL = "http://" + compose.getServiceHost(API_SERVICE_NAME, API_PORT) + ":" + compose.getServicePort(API_SERVICE_NAME, API_PORT);
    }

    @Test
    public void simpleMongoDbTest() {
        RegnskapDB doc2 = mongoCollection.find().first();
        Assert.assertEquals("An inserted record can be retrieved from MongoDB", regnskap2017.getRegnaar(), doc2.getRegnaar());
    }

    @Test
    public void pingTest() throws Exception{
        String address = regnskapRootURL + "/ping";

        URL obj = new URL(address);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String firstLine = reader.readLine();
        reader.close();

        Assert.assertEquals("RegnskapAPI is available", "pong", firstLine);
    }
}
