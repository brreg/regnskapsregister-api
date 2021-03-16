package no.brreg.regnskap;

import com.google.common.collect.ImmutableMap;
import no.brreg.regnskap.generated.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


public class TestData {
    public static final String POSTGRES_DB_NAME = "postgres";
    public static final String POSTGRES_USER = "postgres";
    public static final String POSTGRES_PASSWORD = "postgres";

    public static final String SFTP_USER = "sftpuser";
    public static final String SFTP_PWD = "sftppwd";
    public static final int SFTP_PORT = 22;
    private static final String SFTP_DIRECTORY = "sftpdir";
    public static final String SFTP_DIR = "/" + SFTP_DIRECTORY;
    public static final Map<String, String> SFTP_ENV_VALUES =
        ImmutableMap.of("SFTP_USERS", SFTP_USER + ":" + SFTP_PWD + ":::" + SFTP_DIRECTORY);

    public static final String TEST_ORGNR_1 = "123456789";
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2016S = createRegnskap(TEST_ORGNR_1, 201601, 2016, Regnskapstype.SELSKAP);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2017S = createRegnskap(TEST_ORGNR_1, 201701, 2017, Regnskapstype.SELSKAP);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2018_1S = createRegnskap(TEST_ORGNR_1, 201801, 2018, Regnskapstype.SELSKAP);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2018_2S = createRegnskap(TEST_ORGNR_1, 201802, 2018, Regnskapstype.SELSKAP);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2018_3K = createRegnskap(TEST_ORGNR_1, 201803, 2018, Regnskapstype.KONSERN);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2019_1S = createRegnskap(TEST_ORGNR_1, 201901 /*Test: Use same for Selskap and Konsern*/, 2019, Regnskapstype.SELSKAP);
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2019_2K = createRegnskap(TEST_ORGNR_1, 201901 /*Test: Use same for Selskap and Konsern*/, 2019, Regnskapstype.KONSERN);

    public static final int TEST_SELSKAP_VARER = 100;
    public static final int TEST_KONSERN_VARER = 101;
    public static final int TEST_SELSKAP_FORDRINGER = 102;
    public static final int TEST_KONSERN_INVESTERINGER = 103;

    public static final String TEST_ORGNR_2 = "333444555";
    public static final no.brreg.regnskap.generated.model.Regnskap REGNSKAP_2_2016S = createRegnskap(TEST_ORGNR_2, 201601, 2016, Regnskapstype.SELSKAP);
   


    private static no.brreg.regnskap.generated.model.Regnskap createRegnskap(String orgnr, final Integer id, final int year, final Regnskapstype regnskapsType) {
        no.brreg.regnskap.generated.model.Regnskap regnskap = new no.brreg.regnskap.generated.model.Regnskap()
            .id(id)
            .journalnr(Integer.toString(id))
            .avviklingsregnskap(true)
            .valuta("valutakode")
            .oppstillingsplan(no.brreg.regnskap.generated.model.Regnskap.OppstillingsplanEnum.fromValue("store"))
            .regnskapstype(regnskapsType)
            .revisjon(
                new Revisjon()
                    .ikkeRevidertAarsregnskap(true)
                    .fravalgRevisjon(true))
            .regnskapsperiode(
                new Tidsperiode()
                    .fraDato(LocalDate.of(year, 1, 1))
                    .tilDato(LocalDate.of(year, 12, 31)))
            .regnkapsprinsipper(
                new Regnskapsprinsipper()
                    .smaaForetak(true)
                    .regnskapsregler(Regnskapsprinsipper.RegnskapsreglerEnum.REGNSKAPSLOVENALMINNELIGREGLER))
            .virksomhet(
                new Virksomhet()
                    .organisasjonsnummer(orgnr)
                    .organisasjonsform("AS")
                    .morselskap(true))
            .egenkapitalGjeld(egenkapitalGjeldWithValues(year))
            .eiendeler(eiendelerWithValues(year))
            .resultatregnskapResultat(resultatregnskapResultatWithValues(year));

        if (regnskapsType == Regnskapstype.SELSKAP) {
            regnskap.getEiendeler().sumVarer(BigDecimal.valueOf(TEST_SELSKAP_VARER))
                                   .sumFordringer(BigDecimal.valueOf(TEST_SELSKAP_FORDRINGER));
        } else {
            regnskap.getEiendeler().sumVarer(BigDecimal.valueOf(TEST_KONSERN_VARER))
                                   .sumInvesteringer(BigDecimal.valueOf(TEST_KONSERN_INVESTERINGER));
        }

        return regnskap;
    }

    private static Eiendeler eiendelerWithValues(long baseValue) {
        return new Eiendeler()
            .goodwill(BigDecimal.valueOf(baseValue))
            //.sumVarer(BigDecimal.valueOf(baseValue + 1)) // Add this manually, for testing Selskap vs Konsern
            //.sumFordringer(BigDecimal.valueOf(baseValue + 2)) // Add this manually, for testing Selskap vs Konsern
            //.sumInvesteringer(BigDecimal.valueOf(baseValue + 3)) // Add this manually, for testing Selskap vs Konsern
            .sumBankinnskuddOgKontanter(BigDecimal.valueOf(baseValue + 4))
            .sumEiendeler(BigDecimal.valueOf(baseValue + 5))
            .anleggsmidler(new Anleggsmidler().sumAnleggsmidler(BigDecimal.valueOf(baseValue + 6)))
            .omloepsmidler(new Omloepsmidler().sumOmloepsmidler(BigDecimal.valueOf(baseValue + 7)));
    }

    private static EgenkapitalGjeld egenkapitalGjeldWithValues(long baseValue) {
        return new EgenkapitalGjeld()
            .sumEgenkapitalGjeld(BigDecimal.valueOf(baseValue + 8))
            .egenkapital(
                new Egenkapital()
                    .sumEgenkapital(BigDecimal.valueOf(baseValue + 9))
                    .innskuttEgenkapital(new InnskuttEgenkapital().sumInnskuttEgenkaptial(BigDecimal.valueOf(baseValue + 10)))
                    .opptjentEgenkapital(new OpptjentEgenkapital().sumOpptjentEgenkapital(BigDecimal.valueOf(baseValue + 11))))
            .gjeldOversikt(
                new Gjeld()
                    .sumGjeld(BigDecimal.valueOf(baseValue + 12))
                    .kortsiktigGjeld(new KortsiktigGjeld().sumKortsiktigGjeld(BigDecimal.valueOf(baseValue + 13)))
                    .langsiktigGjeld(new LangsiktigGjeld().sumLangsiktigGjeld(BigDecimal.valueOf(baseValue + 14))));
    }

    private static ResultatregnskapResultat resultatregnskapResultatWithValues(long baseValue) {
        return new ResultatregnskapResultat()
            .ordinaertResultatFoerSkattekostnad(BigDecimal.valueOf(baseValue + 15))
            .ordinaertResultatSkattekostnad(BigDecimal.valueOf(baseValue + 16))
            .ekstraordinaerePoster(BigDecimal.valueOf(baseValue + 17))
            .skattekostnadEkstraordinaertResultat(BigDecimal.valueOf(baseValue + 18))
            .aarsresultat(BigDecimal.valueOf(baseValue + 19))
            .totalresultat(BigDecimal.valueOf(baseValue + 20))
            .driftsresultat(
                new Driftsresultat()
                    .driftsresultat(BigDecimal.valueOf(baseValue + 21))
                    .driftsinntekter(new Driftsinntekter().salgsinntekter(BigDecimal.valueOf(baseValue + 22))
                                                          .sumDriftsinntekter(BigDecimal.valueOf(baseValue + 23)))
                    .driftskostnad(new Driftskostnad().loennskostnad(BigDecimal.valueOf(baseValue + 24))
                                                      .sumDriftskostnad(BigDecimal.valueOf(baseValue + 25))))
            .finansresultat(
                new Finansresultat()
                    .nettoFinans(BigDecimal.valueOf(baseValue + 26))
                    .finansinntekt(new Finansinntekt().sumFinansinntekter(BigDecimal.valueOf(baseValue + 27)))
                    .finanskostnad(new Finanskostnad().rentekostnadSammeKonsern(BigDecimal.valueOf(baseValue + 28))
                                                      .annenRentekostnad(BigDecimal.valueOf(baseValue + 29))
                                                      .sumFinanskostnad(BigDecimal.valueOf(baseValue + 30))));
    }

}
