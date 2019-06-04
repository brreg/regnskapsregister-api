/*package no.regnskap.integration;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import no.regnskap.model.RegnskapDB;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static no.regnskap.TestData.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RunWith(MockitoJUnitRunner.class)
@Ignore //ignore service test until it is working on Jenkins
@TestPropertySource(locations="classpath:test.properties")
public class RegnskapApiIntegration {
    private final static Logger logger = LoggerFactory.getLogger(RegnskapApiIntegration.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    private static DockerComposeContainer compose;

    @ClassRule
    public static TemporaryFolder tmpFolder = new TemporaryFolder();

    @BeforeClass
    public static void setup() {
        File testComposeFile = createTestComposeFile();
        if (testComposeFile != null) {
            compose = new DockerComposeContainer<>(testComposeFile)
                .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
                .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ping").forStatusCode(200))
                .withPull(false)
                .withLogConsumer(MONGO_SERVICE_NAME, mongoLog)
                .withLogConsumer(API_SERVICE_NAME, apiLog);

            compose.start();
        } else {
            logger.debug("Unable to create temporary test-compose.yml");
        }

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        ServerAddress serverAddress = new ServerAddress(compose.getServiceHost(API_SERVICE_NAME, API_PORT), compose.getServicePort(API_SERVICE_NAME, API_PORT));
        MongoCredential credentials = MongoCredential.createScramSha1Credential(MONGO_USER, DATABASE_NAME, MONGO_PASSWORD);
        MongoClientOptions options = MongoClientOptions.builder().build();
        MongoClient mongoClient = new MongoClient(serverAddress, credentials, options);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<RegnskapDB> mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME).withDocumentClass(RegnskapDB.class);

        mongoCollection.insertOne(regnskap2017);
        mongoCollection.insertOne(regnskap2018);
    }

    @AfterClass
    public static void teardown() {
        compose.stop();
    }

    @Test
    public void pingTest() throws Exception {
        String response = simpleGet(buildRegnskapURL("/ping"));
        Assert.assertEquals("RegnskapAPI is available", "pong", response);
    }

    @Test
    public void getByOrgnrTest() throws Exception {
        String response = simpleGet(buildRegnskapURL("/regnskap?orgNummer=orgnummer"));
        Assert.assertEquals(EXPECTED_RESPONSE_ORGNR, response);
    }

    @Test
    public void getById() throws Exception {
        String response2018 = simpleGet(buildRegnskapURL("/regnskaps/" + GENERATED_ID_0.toHexString()));
        String response2017 = simpleGet(buildRegnskapURL("/regnskaps/" + GENERATED_ID_1.toHexString()));
        Assert.assertEquals(buildExpectedDatabaseResponse(GENERATED_ID_0, 2018), response2018);
        Assert.assertEquals(buildExpectedDatabaseResponse(GENERATED_ID_1, 2017), response2017);
    }

    private String simpleGet(URL address) throws Exception {
        HttpURLConnection con = (HttpURLConnection) address.openConnection();
        con.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            reader.lines().forEach(line -> content.append(line));
        }

        return content.toString();
    }

    private URL buildRegnskapURL(String address) throws MalformedURLException {
        return new URL("http", compose.getServiceHost(API_SERVICE_NAME, API_PORT), compose.getServicePort(API_SERVICE_NAME, API_PORT), address);
    }

    private static File createTestComposeFile() {
        try {
            File tmpComposeFile = tmpFolder.newFile("test-compose.yml");

            InputStream testCompseStream = IOUtils.toInputStream(TEST_COMPOSE, Charset.defaultCharset());

            try (FileOutputStream outputStream = new FileOutputStream(tmpComposeFile)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = testCompseStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }

            return tmpComposeFile;
        } catch (IOException e) {
            return null;
        }
    }
}*/
