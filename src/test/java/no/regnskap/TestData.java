package no.regnskap;

import no.regnskap.generated.model.*;
import no.regnskap.model.RegnskapDB;
import no.regnskap.model.RegnskapFieldsDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestData {
    private static LocalDate startOf2018 = LocalDate.of(2018, 1, 1);
    private static LocalDate endOf2018 = LocalDate.of(2018, 12, 31);

    public static Regnskap regnskap = new Regnskap()
        .id("id")
        .avviklingsregnskap(true)
        .valuta("valutakode")
        .oppstillingsplan(Regnskap.OppstillingsplanEnum.fromValue("store"))
        .revisjon(
            new Revisjon()
                .ikkeRevidertAarsregnskap(true))
        .regnskapsperiode(
            new Tidsperiode()
                .fraDato(startOf2018)
                .tilDato(endOf2018))
        .regnkapsprinsipper(
            new Regnskapsprinsipper()
                .smaaForetak(true)
                .regskapsregler(null))
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

    public static RegnskapDB regnskapDB = createRegnskapDB();

    public static List<RegnskapDB> databaseList = createDatabaseList();

    private static RegnskapDB createRegnskapDB() {
        RegnskapDB tmpRegnskapDB = new RegnskapDB(
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
        );
        tmpRegnskapDB.setId("id");

        return tmpRegnskapDB;
    }

    private static List<RegnskapDB> createDatabaseList() {
        List<RegnskapDB> list = new ArrayList<>();
        list.add(regnskapDB);
        return list;
    }
}
