package no.regnskap;

import com.google.common.collect.ImmutableMap;
import no.regnskap.generated.model.*;
import no.regnskap.model.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestData {
    private static final String MONGO_USER = "testuser";
    private static final String MONGO_PASSWORD = "testpassword";
    private static final String MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1";
    public static final int MONGO_PORT = 27017;
    public static final String DATABASE_NAME = "regnskapAPI";

    public static final Map<String, String> MONGO_ENV_VALUES = ImmutableMap.of(
        "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
        "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD);


    public static String buildMongoURI(String host, int port, boolean withDbName) {
        String uri = "mongodb://" + MONGO_USER + ":" + MONGO_PASSWORD + "@" + host + ":" + port + "/";

        if(withDbName) {
            uri += DATABASE_NAME;
        }

        return uri + MONGO_AUTH;
    }

    public static ObjectId GENERATED_ID_0 = ObjectId.get();
    public static ObjectId GENERATED_ID_1 = ObjectId.get();
    public static ObjectId GENERATED_ID_2 = ObjectId.get();

    public static Regnskap REGNSKAP_2018 = createRegnskap(GENERATED_ID_2, 2018);
    public static Regnskap REGNSKAP_2017 = createRegnskap(GENERATED_ID_0, 2017);

    private static Regnskap createRegnskap(ObjectId id, int year) {
        return new Regnskap()
            .id(id.toHexString())
            .avviklingsregnskap(true)
            .valuta("valutakode")
            .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue("store"))
            .revisjon(
                new Revisjon()
                    .ikkeRevidertAarsregnskap(true))
            .regnskapsperiode(
                new Tidsperiode()
                    .fraDato(LocalDate.of(year, 1, 1))
                    .tilDato(LocalDate.of(year, 12, 31)))
            .regnkapsprinsipper(
                new Regnskapsprinsipper()
                    .smaaForetak(true)
                    .regnskapsregler(null))
            .virksomhet(
                new Virksomhet()
                    .organisasjonsnummer("orgnummer")
                    .organisasjonsform("orgform")
                    .morselskap(true))
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
    }

    public static List<Regnskap> EMPTY_REGNSKAP_LIST = new ArrayList<>();

    public static List<Regnskap> REGNSKAP_LIST = Collections.singletonList(REGNSKAP_2018);

    public static List<RegnskapDB> EMPTY_DB_REGNSKAP_LIST = new ArrayList<>();

    public static final RegnskapDB DB_REGNSKAP_2017 = createRegnskapDB(GENERATED_ID_0, 2017, "0");
    public static final RegnskapDB DB_REGNSKAP_2018_FIRST = createRegnskapDB(GENERATED_ID_1, 2018, "1");
    public static final RegnskapDB DB_REGNSKAP_2018_SECOND = createRegnskapDB(GENERATED_ID_2, 2018, "2");

    public static List<RegnskapDB> DB_REGNSKAP_LIST = createDatabaseList();

    private static RegnskapDB createRegnskapDB(ObjectId id, int year, String journalnr) {
        RegnskapDB tmpRegnskapDB = new RegnskapDB();
        tmpRegnskapDB.setOrgnr("orgnummer");
        tmpRegnskapDB.setOrgform("orgform");
        tmpRegnskapDB.setRegnskapstype("S");
        tmpRegnskapDB.setOppstillingsplanVersjonsnr("oppstillingsplanVersjonsnr");
        tmpRegnskapDB.setValutakode("valutakode");
        tmpRegnskapDB.setStartdato(LocalDate.of(year, 1, 1));
        tmpRegnskapDB.setAvslutningsdato(LocalDate.of(year, 12, 31));
        tmpRegnskapDB.setMottakstype("mottakstype");
        tmpRegnskapDB.setAvviklingsregnskap(true);
        tmpRegnskapDB.setBistandRegnskapsforer(true);
        tmpRegnskapDB.setJournalnr(journalnr);
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
        list.add(DB_REGNSKAP_2017);
        list.add(DB_REGNSKAP_2018_SECOND);
        list.add(DB_REGNSKAP_2018_FIRST);
        return list;
    }
}
