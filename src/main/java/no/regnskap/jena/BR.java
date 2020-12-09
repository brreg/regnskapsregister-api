package no.regnskap.jena;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;


public class BR {

    static final String uri = "https://github.com/Informasjonsforvaltning/regnskapsregister-api/blob/develop/src/main/resources/ontology/regnskapsregister.owl#";

    private static final Model model = ModelFactory.createDefaultModel();

    public static final Resource Regnskap = model.createResource(uri + "Regnskap");
    public static final Resource Revisjon = model.createResource(uri + "Revisjon");
    public static final Resource Regnskapsprinsipper = model.createResource(uri + "Regnskapsprinsipper");
    public static final Resource Virksomhet = model.createResource(uri + "Virksomhet");

    public static final Resource Eiendeler = model.createResource(uri + "Eiendeler");
    public static final Resource Anleggsmidler = model.createResource(uri + "Anleggsmidler");
    public static final Resource Omloepsmidler = model.createResource(uri + "Omloepsmidler");

    public static final Resource EgenkapitalGjeld = model.createResource(uri + "EgenkapitalGjeld");
    public static final Resource Egenkapital = model.createResource(uri + "Egenkapital");
    public static final Resource InnskuttEgenkapital = model.createResource(uri + "InnskuttEgenkapital");
    public static final Resource OpptjentEgenkapital = model.createResource(uri + "OpptjentEgenkapital");
    public static final Resource GjeldOversikt = model.createResource(uri + "GjeldOversikt");
    public static final Resource LangsiktigGjeld = model.createResource(uri + "LangsiktigGjeld");
    public static final Resource KortsiktigGjeld = model.createResource(uri + "KortsiktigGjeld");

    public static final Resource ResultatregnskapResultat = model.createResource(uri + "ResultatregnskapResultat");
    public static final Resource Driftsresultat = model.createResource(uri + "Driftsresultat");
    public static final Resource Driftsinntekter = model.createResource(uri + "Driftsinntekter");
    public static final Resource Driftskostnad = model.createResource(uri + "Driftskostnad");
    public static final Resource Finansresultat = model.createResource(uri + "Finansresultat");
    public static final Resource Finansinntekt = model.createResource(uri + "Finansinntekt");
    public static final Resource Finanskostnad = model.createResource(uri + "Finanskostnad");

    public static final Property avviklingsregnskap = model.createProperty(uri, "avviklingsregnskap");
    public static final Property oppstillingsplan = model.createProperty(uri, "oppstillingsplan");

    public static final Property revisjon = model.createProperty(uri, "revisjon");
    public static final Property ikkeRevidertAarsregnskap = model.createProperty(uri, "ikkeRevidertAarsregnskap");
    public static final Property fravalgRevisjon = model.createProperty(uri, "fravalgRevisjon");

    public static final Property regnskapsperiode = model.createProperty(uri, "regnskapsperiode");

    public static final Property regnskapsprinsipper = model.createProperty(uri, "regnskapsprinsipper");
    public static final Property smaaForetak = model.createProperty(uri, "smaaForetak");
    public static final Property regnskapsregler = model.createProperty(uri, "regnskapsregler");

    public static final Property virksomhet = model.createProperty(uri, "virksomhet");
    public static final Property organisasjonsnummer = model.createProperty(uri, "organisasjonsnummer");
    public static final Property organisasjonsform = model.createProperty(uri, "organisasjonsform");
    public static final Property morselskap = model.createProperty(uri, "morselskap");

    public static final Property eiendeler = model.createProperty(uri, "eiendeler");
    public static final Property sumEiendeler = model.createProperty(uri, "sumEiendeler");
    public static final Property anleggsmidler = model.createProperty(uri, "anleggsmidler");
    public static final Property sumAnleggsmidler = model.createProperty(uri, "sumAnleggsmidler");
    public static final Property omloepsmidler = model.createProperty(uri, "omloepsmidler");
    public static final Property sumOmloepsmidler = model.createProperty(uri, "sumOmloepsmidler");

    public static final Property egenkapitalGjeld = model.createProperty(uri, "egenkapitalGjeld");
    public static final Property goodwill = model.createProperty(uri, "goodwill");
    public static final Property sumVarer = model.createProperty(uri, "sumVarer");
    public static final Property sumFordringer = model.createProperty(uri, "sumFordringer");
    public static final Property sumInvesteringer = model.createProperty(uri, "sumInvesteringer");
    public static final Property sumBankinnskuddOgKontanter = model.createProperty(uri, "sumBankinnskuddOgKontanter");
    public static final Property sumEgenkapitalGjeld = model.createProperty(uri, "sumEgenkapitalGjeld");
    public static final Property egenkapital = model.createProperty(uri, "egenkapital");
    public static final Property sumEgenkapital = model.createProperty(uri, "sumEgenkapital");
    public static final Property innskuttEgenkapital = model.createProperty(uri, "innskuttEgenkapital");
    public static final Property sumInnskuttEgenkaptial = model.createProperty(uri, "sumInnskuttEgenkaptial");
    public static final Property opptjentEgenkapital = model.createProperty(uri, "opptjentEgenkapital");
    public static final Property sumOpptjentEgenkapital = model.createProperty(uri, "sumOpptjentEgenkapital");
    public static final Property gjeldOversikt = model.createProperty(uri, "gjeldOversikt");
    public static final Property sumGjeld = model.createProperty(uri, "sumGjeld");
    public static final Property langsiktigGjeld = model.createProperty(uri, "langsiktigGjeld");
    public static final Property sumLangsiktigGjeld = model.createProperty(uri, "sumLangsiktigGjeld");
    public static final Property kortsiktigGjeld = model.createProperty(uri, "kortsiktigGjeld");
    public static final Property sumKortsiktigGjeld = model.createProperty(uri, "sumKortsiktigGjeld");

    public static final Property resultatregnskapResultat = model.createProperty(uri, "resultatregnskapResultat");
    public static final Property aarsresultat = model.createProperty(uri, "aarsresultat");
    public static final Property totalresultat = model.createProperty(uri, "totalresultat");
    public static final Property ordinaertResultatFoerSkattekostnad = model.createProperty(uri, "ordinaertResultatFoerSkattekostnad");
    public static final Property ordinaertResultatSkattekostnad = model.createProperty(uri, "ordinaertResultatSkattekostnad");
    public static final Property ekstraordinaerePoster = model.createProperty(uri, "ekstraordinaerePoster");
    public static final Property skattekostnadEkstraordinaertResultat = model.createProperty(uri, "skattekostnadEkstraordinaertResultat");
    public static final Property driftsresultat = model.createProperty(uri, "driftsresultat");
    public static final Property sumDriftsresultat = model.createProperty(uri, "sumDriftsresultat");
    public static final Property driftsinntekter = model.createProperty(uri, "driftsinntekter");
    public static final Property salgsinntekter = model.createProperty(uri, "salgsinntekter");
    public static final Property sumDriftsinntekter = model.createProperty(uri, "sumDriftsinntekter");
    public static final Property driftskostnad = model.createProperty(uri, "driftskostnad");
    public static final Property loennskostnad = model.createProperty(uri, "loennskostnad");
    public static final Property sumDriftskostnad = model.createProperty(uri, "sumDriftskostnad");
    public static final Property finansresultat = model.createProperty(uri, "finansresultat");
    public static final Property nettoFinans = model.createProperty(uri, "nettoFinans");
    public static final Property finansinntekt = model.createProperty(uri, "finansinntekt");
    public static final Property sumFinansinntekter = model.createProperty(uri, "sumFinansinntekter");
    public static final Property finanskostnad = model.createProperty(uri, "finanskostnad");
    public static final Property rentekostnadSammeKonsern = model.createProperty(uri, "rentekostnadSammeKonsern");
    public static final Property annenRentekostnad = model.createProperty(uri, "annenRentekostnad");
    public static final Property sumFinanskostnad = model.createProperty(uri, "sumFinanskostnad");

}