package no.regnskap;

import no.regnskap.generated.model.*;
import no.regnskap.model.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    private static final String MONGO_USER = "testuser";
    private static final String MONGO_PASSWORD = "testpassword";

    public static final String API_SERVICE_NAME = "regnskapsregister";
    public static final String MONGO_SERVICE_NAME = "mongodb";
    public static final int API_PORT = 8080;
    public static final int MONGO_PORT = 27017;
    public static final String DATABASE_NAME = "regnskapAPI";
    public static final String COLLECTION_NAME = "regnskap";

    public static String buildMongoURI(String host, int port) {
        return "mongodb://" + MONGO_USER + ":" + MONGO_PASSWORD + "@" + host + ":" + port + "/" + DATABASE_NAME + "?authSource=admin&authMechanism=SCRAM-SHA-1";
    }

    private static LocalDate startOfYear(int year) {
        return LocalDate.of(year, 1, 1);
    }
    private static LocalDate endOfYear(int year) {
        return LocalDate.of(year, 12, 31);
    }

    public static ObjectId GENERATED_ID_0 = ObjectId.get();
    public static ObjectId GENERATED_ID_1 = ObjectId.get();

    public static Regnskap regnskap = new Regnskap()
        .id(GENERATED_ID_0.toHexString())
        .avviklingsregnskap(true)
        .valuta("valutakode")
        .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue("store"))
        .revisjon(
            new Revisjon()
                .ikkeRevidertAarsregnskap(true))
        .regnskapsperiode(
            new Tidsperiode()
                .fraDato(startOfYear(2018))
                .tilDato(endOfYear(2018)))
        .regnkapsprinsipper(
            new Regnskapsprinsipper()
                .smaaForetak(true)
                .regnskapsregler(null))
        .virksomhet(
            new Virksomhet()
                .organisasjonsnummer("orgnummer")
                .organisasjonsform("orgform")
                .morselskap(true)
                .levertAarsregnskap(true)
                .navn(null))
        .egenkapitalGjeld(
            new EgenkapitalGjeld().egenkapital(
                new Egenkapital()
                    .innskuttEgenkapital(new InnskuttEgenkapital())
                    .opptjentEgenkapital(new OpptjentEgenkapital()))
                .gjeldOversikt(
                    new Gjeld()
                        .kortsiktigGjeld(new KortsiktigGjeld())
                        .langsiktigGjeld(new LangsiktigGjeld())))
        .eiendeler(
            new Eiendeler()
                .anleggsmidler(new Anleggsmidler())
                .omloepsmidler(new Omloepsmidler()))
        .resultatregnskapResultat(
            new ResultatregnskapResultat().driftsresultat(
                new Driftsresultat()
                    .driftsinntekter(new Driftsinntekter())
                    .driftskostnad(new Driftskostnad()))
                .finansresultat(
                    new Finansresultat()
                        .finansinntekt(new Finansinntekt())
                        .finanskostnad(new Finanskostnad())));

    public static List<Regnskap> emptyRegnskapList = new ArrayList<>();

    public static List<Regnskap> regnskapList = createRegnskapList();

    private static List<Regnskap> createRegnskapList() {
        List<Regnskap> list = new ArrayList<>();
        list.add(regnskap);
        return list;
    }

    public static List<RegnskapDB> emptyDatabaseList = new ArrayList<>();

    public static final RegnskapDB regnskap2018 = createRegnskapDB(GENERATED_ID_0, 2018);
    public static final RegnskapDB regnskap2017 = createRegnskapDB(GENERATED_ID_1, 2017);

    public static List<RegnskapDB> databaseList = createDatabaseList();

    private static RegnskapDB createRegnskapDB(ObjectId id, int year) {
        RegnskapDB tmpRegnskapDB = new RegnskapDB();
        tmpRegnskapDB.setOrgnr("orgnummer");
        tmpRegnskapDB.setOrgform("orgform");
        tmpRegnskapDB.setRegnskapstype("regnskapstype");
        tmpRegnskapDB.setOppstillingsplanVersjonsnr("oppstillingsplanVersjonsnr");
        tmpRegnskapDB.setValutakode("valutakode");
        tmpRegnskapDB.setStartdato(startOfYear(year));
        tmpRegnskapDB.setAvslutningsdato(endOfYear(year));
        tmpRegnskapDB.setMottakstype("mottakstype");
        tmpRegnskapDB.setAvviklingsregnskap(true);
        tmpRegnskapDB.setBistandRegnskapsforer(true);
        tmpRegnskapDB.setJournalnr("journalnr");
        tmpRegnskapDB.setMottattDato(LocalDate.now());
        tmpRegnskapDB.setFeilvaloer(true);
        tmpRegnskapDB.setFleksiblePoster(true);
        tmpRegnskapDB.setFravalgRevisjon(false);
        tmpRegnskapDB.setLandForLand(false);
        tmpRegnskapDB.setMorselskap(true);
        tmpRegnskapDB.setReglerSmaa(true);
        tmpRegnskapDB.setAarsregnskapstype("STORE");
        tmpRegnskapDB.setUtarbeidetRegnskapsforer(false);
        tmpRegnskapDB.setRevisorberetningIkkeLevert(true);
        tmpRegnskapDB.setFields(new RegnskapFieldsDB());

        tmpRegnskapDB.setRegnaar(year);

        tmpRegnskapDB.setId(id);

        return tmpRegnskapDB;
    }

    private static List<RegnskapDB> createDatabaseList() {
        List<RegnskapDB> list = new ArrayList<>();
        list.add(regnskap2018);
        return list;
    }

    public final static String EXPECTED_RESPONSE_ORGNR = "[" + buildExpectedDatabaseResponse(GENERATED_ID_1, 2017) + "," + buildExpectedDatabaseResponse(GENERATED_ID_0, 2018) + "]";

    public static String buildExpectedDatabaseResponse(ObjectId id, int year) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"id\":\"");
        stringBuilder.append(id.toHexString());
        stringBuilder.append("\",\"virksomhet\":{\"organisasjonsnummer\":\"orgnummer\",\"navn\":null,\"organisasjonsform\":\"orgform\",\"morselskap\":false,\"levertAarsregnskap\":true},\"regnskapsperiode\":{\"fraDato\":\"");
        stringBuilder.append(year);
        stringBuilder.append("-01-01\",\"tilDato\":\"");
        stringBuilder.append(year);
        stringBuilder.append("-12-31\"},\"valuta\":\"valutakode\",\"avviklingsregnskap\":true,\"oppstillingsplan\":\"store\",\"revisjon\":{\"ikkeRevidertAarsregnskap\":false},\"regnkapsprinsipper\":{\"smaaForetak\":false,\"regnskapsregler\":null},\"egenkapitalGjeld\":{\"sumEgenkapitalGjeld\":null,\"egenkapital\":{\"sumEgenkapital\":null,\"opptjentEgenkapital\":{\"sumOpptjentEgenkapital\":null},\"innskuttEgenkapital\":{\"sumInnskuttEgenkaptial\":null}},\"gjeldOversikt\":{\"sumGjeld\":null,\"kortsiktigGjeld\":{\"sumKortsiktigGjeld\":null},\"langsiktigGjeld\":{\"sumLangsiktigGjeld\":null}}},\"eiendeler\":{\"sumEiendeler\":null,\"omloepsmidler\":{\"sumOmloepsmidler\":null},\"anleggsmidler\":{\"sumAnleggsmidler\":null}},\"resultatregnskapResultat\":{\"ordinaertResultatFoerSkattekostnad\":null,\"aarsresultat\":null,\"totalresultat\":null,\"finansresultat\":{\"nettoFinans\":null,\"finansinntekt\":{\"sumFinansinntekter\":null},\"finanskostnad\":{\"sumFinanskostnad\":null}},\"driftsresultat\":{\"driftsresultat\":null,\"driftsinntekter\":{\"sumDriftsinntekter\":null},\"driftskostnad\":{\"sumDriftskostnad\":null}}}}");

        return stringBuilder.toString();
    }

    public static String TEST_COMPOSE = "version: \"3.2\"\n" +
        "\n" +
        "services:\n" +
        "  regnskapsregister:\n" +
        "    image: brreg/regnskapsregister-api:latest\n" +
        "    environment:\n" +
        "      - RRAPI_MONGO_USERNAME=" + MONGO_USER + "\n" +
        "      - RRAPI_MONGO_PASSWORD=" + MONGO_PASSWORD + "\n" +
        "    depends_on:\n" +
        "      - mongodb\n" +
        "\n" +
        "  mongodb:\n" +
        "    image: mongo:latest\n" +
        "    environment:\n" +
        "      - MONGO_INITDB_ROOT_USERNAME=" + MONGO_USER + "\n" +
        "      - MONGO_INITDB_ROOT_PASSWORD=" + MONGO_PASSWORD + "\n";
}
