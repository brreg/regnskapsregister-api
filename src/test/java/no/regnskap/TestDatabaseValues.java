package no.regnskap;

import no.regnskap.model.RegnskapDB;
import no.regnskap.model.RegnskapFieldsDB;

import java.time.LocalDate;

public class TestDatabaseValues {
    public static final String DOCKER_IMAGE = "mongo:latest";
    public static final int MONGO_PORT = 27017;
    public static final String DATABASE_NAME = "test";
    public static final String COLLECTION_NAME = "testRegnskap";

    private static LocalDate startOf2018 = LocalDate.of(2018, 1, 1);
    private static LocalDate endOf2018 = LocalDate.of(2018, 12, 31);

    public static final RegnskapDB regnskap2017 = createRegnskapDB(2017);
    public static final RegnskapDB regnskap2018 = createRegnskapDB(2018);

    private static RegnskapDB createRegnskapDB(int year) {
        RegnskapDB regnskapDB = new RegnskapDB();
        regnskapDB.setOrgnr("orgnummer");
        regnskapDB.setRegnaar(year);
        regnskapDB.setFields(new RegnskapFieldsDB());

        return regnskapDB;
    }
        /*
    );
    public static final RegnskapDB regnskap2018 = new RegnskapDB(
        "orgnummer",
        "regnskapstype",
        2018,
        "oppstillingsplanVersjonsnr",
        "valutakode",
        startOf2018,
        endOf2018,
        "mottakstype",
        true,
        true,
        "journalnr",
        LocalDate.now(),
        "orgform",
        true,
        true,
        false,
        false,
        true,
        true,
        "STORE",
        false,
        true,
        new RegnskapFieldsDB()
    );*/
}
