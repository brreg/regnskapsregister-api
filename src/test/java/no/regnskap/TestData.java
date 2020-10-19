package no.regnskap;

import com.google.common.collect.ImmutableMap;
import no.regnskap.generated.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


public class TestData {
    public static final String POSTGRES_DB_NAME = "integration-tests-db";
    public static final String POSTGRES_USER = "testuser";
    public static final String POSTGRES_PASSWORD = "testpassword";

    public static final String SFTP_USER = "sftpuser";
    public static final String SFTP_PWD = "sftppwd";
    public static final int SFTP_PORT = 22;
    private static final String SFTP_DIRECTORY = "sftpdir";
    public static final String SFTP_DIR = "/" + SFTP_DIRECTORY;
    public static final Map<String, String> SFTP_ENV_VALUES =
        ImmutableMap.of("SFTP_USERS", SFTP_USER + ":" + SFTP_PWD + ":::" + SFTP_DIRECTORY);

    public static Integer ID_2018 = 12;
    public static no.regnskap.generated.model.Regnskap REGNSKAP_2018 = createRegnskap(ID_2018, 2018);

    private static no.regnskap.generated.model.Regnskap createRegnskap(Integer id, int year) {
        return new no.regnskap.generated.model.Regnskap()
            .id(id)
            .avviklingsregnskap(true)
            .valuta("valutakode")
            .oppstillingsplan(no.regnskap.generated.model.Regnskap.OppstillingsplanEnum.fromValue("store"))
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
                    .regnskapsregler(Regnskapsprinsipper.RegnskapsreglerEnum.REGNSKAPSLOVENALMINNELIGREGLER))
            .virksomhet(
                new Virksomhet()
                    .organisasjonsnummer("123456789")
                    .organisasjonsform("AS")
                    .morselskap(true))
            .egenkapitalGjeld(egenkapitalGjeldWithValues(year))
            .eiendeler(eiendelerWithValues(year))
            .resultatregnskapResultat(resultatregnskapResultatWithValues(year));
    }

    private static Eiendeler eiendelerWithValues(long baseValue) {
        return new Eiendeler()
            .goodwill(BigDecimal.valueOf(baseValue))
            .sumVarer(BigDecimal.valueOf(baseValue + 1))
            .sumFordringer(BigDecimal.valueOf(baseValue + 2))
            .sumInvesteringer(BigDecimal.valueOf(baseValue + 3))
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