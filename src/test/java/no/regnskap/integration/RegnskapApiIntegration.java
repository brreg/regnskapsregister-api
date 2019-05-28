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
import java.net.MalformedURLException;
import java.net.URL;

import static no.regnskap.TestData.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RunWith(MockitoJUnitRunner.class)
public class RegnskapApiIntegration {
    private static URL regnskapRootURL;

    @ClassRule
    public static DockerComposeContainer compose = new DockerComposeContainer<>(new File("src/test/resources/test-compose.yml"))
        .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
        .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ping").forStatusCode(200))
        .withPull(false);

    @BeforeClass
    public static void mongoSetup() throws MalformedURLException {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClient mongoClient = new MongoClient(compose.getServiceHost(MONGO_SERVICE_NAME, MONGO_PORT), compose.getServicePort(MONGO_SERVICE_NAME, MONGO_PORT));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<RegnskapDB> mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME).withDocumentClass(RegnskapDB.class);

        mongoCollection.insertOne(regnskap2017);
        mongoCollection.insertOne(regnskap2018);

        regnskapRootURL = new URL("http://" + compose.getServiceHost(API_SERVICE_NAME, API_PORT) + ":" + compose.getServicePort(API_SERVICE_NAME, API_PORT));
    }

    @Test
    public void pingTest() throws Exception {
        String response = simpleGet(new URL(regnskapRootURL + "/ping"));
        Assert.assertEquals("RegnskapAPI is available", "pong", response);
    }

    @Test
    public void getByOrgnrTest() throws Exception {
        String response = simpleGet(new URL(regnskapRootURL + "/regnskap?orgNummer=orgnummer"));
        Assert.assertEquals(EXPECTED_RESPONSE_ORGNR, response);
    }

    @Test
    public void getById() throws Exception {
        String response2018 = simpleGet(new URL(regnskapRootURL + "/regnskaps/" + GENERATED_ID_0.toHexString()));
        String response2017 = simpleGet(new URL(regnskapRootURL + "/regnskaps/" + GENERATED_ID_1.toHexString()));
        Assert.assertEquals(buildExpectedDatabaseResponse(GENERATED_ID_0, 2018), response2018);
        Assert.assertEquals(buildExpectedDatabaseResponse(GENERATED_ID_1, 2017), response2017);
    }

    private String simpleGet(URL address) throws Exception {
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            content.append(inputLine);
        }
        reader.close();

        return content.toString();
    }
}
