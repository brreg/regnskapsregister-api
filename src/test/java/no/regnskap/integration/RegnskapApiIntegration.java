package no.regnskap.integration;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import no.regnskap.model.RegnskapDB;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;
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
@TestPropertySource(locations="classpath:test.properties")
public class RegnskapApiIntegration {
    private static File testComposeFile = createTmpComposeFile();
    private final static Logger logger = LoggerFactory.getLogger(RegnskapApiIntegration.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    private static DockerComposeContainer compose;

    @BeforeClass
    public static void setup() {
        if (testComposeFile != null && testComposeFile.exists()) {
            compose = new DockerComposeContainer<>(testComposeFile)
                .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
                .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/ready").forStatusCode(200))
                .withTailChildContainers(true)
                .withPull(false)
                .withLocalCompose(true)
                .withLogConsumer(MONGO_SERVICE_NAME, mongoLog)
                .withLogConsumer(API_SERVICE_NAME, apiLog);

            compose.start();
        } else {
            logger.debug("Unable to start containers, missing test-compose.yml");
        }
/*
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        ServerAddress serverAddress = new ServerAddress(compose.getServiceHost(API_SERVICE_NAME, API_PORT), compose.getServicePort(API_SERVICE_NAME, API_PORT));
        MongoCredential credentials = MongoCredential.createScramSha1Credential(MONGO_USER, DATABASE_NAME, MONGO_PASSWORD);
        MongoClientOptions options = MongoClientOptions.builder().build();
        MongoClient mongoClient = new MongoClient(serverAddress, credentials, options);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<RegnskapDB> mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME).withDocumentClass(RegnskapDB.class);

        mongoCollection.insertOne(regnskap2017);
        mongoCollection.insertOne(regnskap2018);*/
    }

    @AfterClass
    public static void teardown() {
        if (testComposeFile != null && testComposeFile.exists()) {
            compose.stop();

            logger.debug("Delete temporary test-compose.yml: " + testComposeFile.delete());
        } else {
            logger.debug("Teardown skipped, missing test-compose.yml");
        }
    }

    @Test
    public void pingTest() throws Exception {
        String response = simpleGet(buildRegnskapURL("/ping"));
        Assert.assertEquals("RegnskapAPI is available", "pong", response);
    }

    @Test
    @Ignore
    public void getByOrgnrTest() throws Exception {
        String response = simpleGet(buildRegnskapURL("/regnskap?orgNummer=orgnummer"));
        Assert.assertEquals(EXPECTED_RESPONSE_ORGNR, response);
    }

    @Test
    @Ignore
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

    private static File createTmpComposeFile() {
        try {
            File tmpComposeFile = File.createTempFile("test-compose", ".yml");
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
}
