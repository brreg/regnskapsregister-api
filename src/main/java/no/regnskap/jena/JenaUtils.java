package no.regnskap.jena;

import no.regnskap.generated.model.Regnskap;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.ORG;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JenaUtils {

    public static final MimeType JSON_MIMETYPE    = MimeType.valueOf("application/json");
    public static final MimeType JSON_LD_MIMETYPE = MimeType.valueOf("application/ld+json");
    public static final MimeType RDF_MIMETYPE     = MimeType.valueOf("application/rdf+xml");
    public static final MimeType TURTLE_MIMETYPE  = MimeType.valueOf("text/turtle");
    private static final List<MimeType> SUPPORTED_MIMETYPES = Arrays.asList(JSON_MIMETYPE, JSON_LD_MIMETYPE, RDF_MIMETYPE, TURTLE_MIMETYPE);

    private static final int PARAM_PAIR_LENGTH = 2;


    public static RDFFormat mimeTypeToJenaFormat(final MimeType mimeType) {
        RDFFormat format = null;
        if (JSON_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.RDFJSON);
        } else if (JSON_LD_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.JSONLD);
        } else if (RDF_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.RDFXML);
        } else if (TURTLE_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.TURTLE);
        }
        return format;
    }

    public static MimeType negotiateMimeType(final String acceptHeader) {
        if (acceptHeader==null || acceptHeader.isEmpty()) {
            return null;
        }

        //This will contain mime-types and matching quality. <URL: https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1 >
        Map<MimeType,Double> acceptedMimeTypes = new HashMap<>();

        //Accept:-header is comma-separated mediaRange
        String[] mediaRanges = acceptHeader.split(",");
        for (int mediaRangeIndex=0; mediaRangeIndex<mediaRanges.length; mediaRangeIndex++) {
            MimeType mediaType = null;
            double quality = 0.0;

            //mediaRange is either "type/subtype" or "type/subtype;parameter=token[;parameter=token]"
            String mediaRange = mediaRanges[mediaRangeIndex].trim();
            if (mediaRange==null || mediaRange.isEmpty()) {
                //This should not happen, but better safe than sorry
                continue;
            }

            String[] parameters = mediaRange.split(";");
            if (parameters==null || parameters.length<=1) {
                mediaType = MimeType.valueOf(mediaRange);
                quality = 1.0;
            } else {
                //There are parameters present!
                mediaType = MimeType.valueOf(parameters[0]);
                quality = 1.0; //Default to 1.0, but check if quality is one of the parameters

                for (int acceptExtensionIndex=1; acceptExtensionIndex<parameters.length; acceptExtensionIndex++) {
                    String acceptParams = parameters[acceptExtensionIndex].trim();
                    if (acceptParams==null || acceptParams.isEmpty()) {
                        //This should not happen, but better safe than sorry
                        continue;
                    }

                    String[] acceptExtension = acceptParams.split("=");
                    if (acceptExtension!=null && acceptExtension.length==PARAM_PAIR_LENGTH && "q".equals(acceptExtension[0].trim())) {
                        try {
                            quality = Double.valueOf(acceptExtension[1]);
                        } catch (NumberFormatException e) {
                            quality = 0.0;
                        }
                    }
                }
            }

            if (mediaType==null) {
                continue;
            }

            //Collect highest quality value
            if (!acceptedMimeTypes.containsKey(mediaType) || acceptedMimeTypes.get(mediaType)<quality) {
                acceptedMimeTypes.put(mediaType, quality);
            }
        }

        //Check if any compatible wildcard type/subtypes have higher quality
        for (MimeType acceptedMimeType : acceptedMimeTypes.keySet()) {
            if (!acceptedMimeType.isConcrete()) {
                double acceptedQuality = acceptedMimeTypes.get(acceptedMimeType);
                for (Map.Entry<MimeType, Double> entry : acceptedMimeTypes.entrySet()) {
                    if (entry.getKey().isConcrete() &&
                            entry.getKey().isCompatibleWith(acceptedMimeType) &&
                            entry.getValue() < acceptedQuality) {
                        entry.setValue(acceptedQuality);
                    }
                }
            }
        }

        //acceptedMimeTypes now contains all concrete and wildcard mimetypes, and their quality value

        //Find the highest quality mimetype matching our list of supported mimetypes.
        double negotiatedQuality = 0.0;
        MimeType negotiatedMimeType = null;
        for (MimeType supportedMimeType : SUPPORTED_MIMETYPES) {
            for (Map.Entry<MimeType, Double> entry : acceptedMimeTypes.entrySet()) {
                if (entry.getKey().isCompatibleWith(supportedMimeType) &&
                        (negotiatedMimeType==null || entry.getValue()>negotiatedQuality)) {
                    negotiatedMimeType = supportedMimeType;
                    negotiatedQuality = entry.getValue();
                }
            }
        }

        return negotiatedMimeType;
    }

    public static Model createJenaResponse(final List<Regnskap> regnskapList, final ExternalUrls urls) {
        Model model = createRegnskapModel(urls);
        for (Regnskap regnskap : regnskapList) {
            model = addRegnskap(model, regnskap, urls);
        }
        return model;
    }

    public static Model createJenaResponse(final Regnskap regnskap, final ExternalUrls urls) {
        Model model = createRegnskapModel(urls);
        return addRegnskap(model, regnskap, urls);
    }

    private static Model createRegnskapModel(ExternalUrls urls) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("dct", DCTerms.getURI());
        model.setNsPrefix("br", BR.uri);
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("org", ORG.getURI());
        model.setNsPrefix("schema", SCHEMA.getURI());
        return model;
    }

    private static Model addRegnskap(final Model model, final Regnskap regnskap, final ExternalUrls urls) {
        Resource regnskapResource = model.createResource(urls.getSelf() + regnskap.getId());
        regnskapResource.addProperty(RDF.type, BR.Regnskap)
            .addProperty(DCTerms.identifier, regnskap.getId())
            .addLiteral(BR.avviklingsregnskap, regnskap.getAvviklingsregnskap())
            .addLiteral(BR.oppstillingsplan, regnskap.getOppstillingsplan().getValue());

        if (regnskap.getValuta() != null) {
            regnskapResource.addLiteral(SCHEMA.currency, regnskap.getValuta());
        }

        regnskapResource.addProperty(BR.revisjon, model.createResource(BR.Revisjon)
            .addLiteral(BR.ikkeRevidertAarsregnskap, regnskap.getRevisjon().getIkkeRevidertAarsregnskap()));

        regnskapResource.addProperty(BR.regnskapsprinsipper, model.createResource(BR.Regnskapsprinsipper)
            .addLiteral(BR.smaaForetak, regnskap.getRegnkapsprinsipper().getSmaaForetak())
            .addLiteral(BR.regnskapsregler, regnskap.getRegnkapsprinsipper().getRegnskapsregler().getValue()));

        regnskapResource = addRegnskapsperiode(model, regnskapResource, regnskap.getRegnskapsperiode().getFraDato(), regnskap.getRegnskapsperiode().getTilDato());

        regnskapResource = addVirksomhet(model, regnskapResource, regnskap, urls);

        regnskapResource.addProperty(BR.eiendeler, model.createResource(BR.Eiendeler)
            .addLiteral(BR.goodwill, bigDecimalOrZero(regnskap.getEiendeler().getGoodwill()))
            .addLiteral(BR.sumVarer, bigDecimalOrZero(regnskap.getEiendeler().getSumVarer()))
            .addLiteral(BR.sumFordringer, bigDecimalOrZero(regnskap.getEiendeler().getSumFordringer()))
            .addLiteral(BR.sumInvesteringer, bigDecimalOrZero(regnskap.getEiendeler().getSumInvesteringer()))
            .addLiteral(BR.sumBankinnskuddOgKontanter, bigDecimalOrZero(regnskap.getEiendeler().getSumBankinnskuddOgKontanter()))
            .addLiteral(BR.sumEiendeler, bigDecimalOrZero(regnskap.getEiendeler().getSumEiendeler()))
            .addProperty(BR.anleggsmidler, model.createResource(BR.Anleggsmidler)
                .addLiteral(BR.sumAnleggsmidler, bigDecimalOrZero(regnskap.getEiendeler().getAnleggsmidler().getSumAnleggsmidler())))
            .addProperty(BR.omloepsmidler, model.createResource(BR.Omloepsmidler)
                .addLiteral(BR.sumOmloepsmidler, bigDecimalOrZero(regnskap.getEiendeler().getOmloepsmidler().getSumOmloepsmidler()))));

        regnskapResource.addProperty(BR.egenkapitalGjeld, model.createResource(BR.EgenkapitalGjeld)
            .addLiteral(BR.sumEgenkapitalGjeld, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getSumEgenkapitalGjeld()))
            .addProperty(BR.egenkapital, model.createResource(BR.Egenkapital)
                .addLiteral(BR.sumEgenkapital, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital()))
                .addProperty(BR.innskuttEgenkapital, model.createResource(BR.InnskuttEgenkapital)
                    .addLiteral(BR.sumInnskuttEgenkaptial, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial())))
                .addProperty(BR.opptjentEgenkapital, model.createResource(BR.OpptjentEgenkapital)
                    .addLiteral(BR.sumOpptjentEgenkapital, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital()))))
            .addProperty(BR.gjeldOversikt, model.createResource(BR.GjeldOversikt)
                .addLiteral(BR.sumGjeld, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld()))
                .addProperty(BR.langsiktigGjeld, model.createResource(BR.LangsiktigGjeld)
                    .addLiteral(BR.sumLangsiktigGjeld, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld())))
                .addProperty(BR.kortsiktigGjeld, model.createResource(BR.KortsiktigGjeld)
                    .addLiteral(BR.sumKortsiktigGjeld, bigDecimalOrZero(regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld())))));

        regnskapResource.addProperty(BR.resultatregnskapResultat, model.createResource(BR.ResultatregnskapResultat)
            .addLiteral(BR.aarsresultat, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getAarsresultat()))
            .addLiteral(BR.totalresultat, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getTotalresultat()))
            .addLiteral(BR.ordinaertResultatFoerSkattekostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad()))
            .addLiteral(BR.ordinaertResultatSkattekostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getOrdinaertResultatSkattekostnad()))
            .addLiteral(BR.ekstraordinaerePoster, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getEkstraordinaerePoster()))
            .addLiteral(BR.skattekostnadEkstraordinaertResultat, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getSkattekostnadEkstraordinaertResultat()))
            .addProperty(BR.driftsresultat, model.createResource(BR.Driftsresultat)
                .addLiteral(BR.sumDriftsresultat, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat()))
                .addProperty(BR.driftsinntekter, model.createResource(BR.Driftsinntekter)
                    .addLiteral(BR.salgsinntekter, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSalgsinntekter()))
                    .addLiteral(BR.sumDriftsinntekter, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter())))
                .addProperty(BR.driftskostnad, model.createResource(BR.Driftskostnad)
                    .addLiteral(BR.loennskostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getLoennskostnad()))
                    .addLiteral(BR.sumDriftskostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad()))))
            .addProperty(BR.finansresultat, model.createResource(BR.Finansresultat)
                .addLiteral(BR.nettoFinans, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans()))
                .addProperty(BR.finansinntekt, model.createResource(BR.Finansinntekt)
                    .addLiteral(BR.sumFinansinntekter, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter())))
                .addProperty(BR.finanskostnad, model.createResource(BR.Finanskostnad)
                    .addLiteral(BR.rentekostnadSammeKonsern, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern()))
                    .addLiteral(BR.annenRentekostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad()))
                    .addLiteral(BR.sumFinanskostnad, bigDecimalOrZero(regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad())))));

        return model;
    }

    public static String modelToString(final Model model, final RDFFormat format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            model.write(baos, format.toString());
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static BigDecimal bigDecimalOrZero(final BigDecimal value) {
        return (value == null) ? BigDecimal.ZERO : value;
    }

    private static Resource addRegnskapsperiode(final Model model, final Resource resource, final LocalDate fraDato, final LocalDate tilDato) {
        if (fraDato==null && tilDato==null) {
            return resource;
        }

        Resource potResource = model.createResource(DCTerms.PeriodOfTime);
        if (fraDato != null) {
            potResource.addLiteral(SCHEMA.startDate, model.createTypedLiteral(toXSDDate(fraDato), XSDDateType.XSDdate));
        }
        if (tilDato != null) {
            potResource.addLiteral(SCHEMA.endDate, model.createTypedLiteral(toXSDDate(tilDato), XSDDateType.XSDdate));
        }
        resource.addProperty(BR.regnskapsperiode, potResource);
        return resource;
    }

    private static Resource addVirksomhet(final Model model, final Resource resource, final Regnskap regnskap, final ExternalUrls urls) {
        Resource virksomhetResource = model.createResource(BR.Virksomhet);

        if (regnskap.getVirksomhet().getOrganisasjonsnummer() != null) {
            virksomhetResource.addProperty(ORG.organization, model.createResource(urls.getOrganizationCatalogue() + regnskap.getVirksomhet().getOrganisasjonsnummer()));
            virksomhetResource.addLiteral(BR.organisasjonsnummer, regnskap.getVirksomhet().getOrganisasjonsnummer());
        }
        if (regnskap.getVirksomhet().getOrganisasjonsform() != null) {
            virksomhetResource.addLiteral(BR.organisasjonsform, regnskap.getVirksomhet().getOrganisasjonsform());
        }
        if (regnskap.getVirksomhet().getMorselskap() != null) {
            virksomhetResource.addLiteral(BR.morselskap, regnskap.getVirksomhet().getMorselskap());
        }

        resource.addProperty(BR.virksomhet, virksomhetResource);
        return resource;
    }

    public static XSDDateTime toXSDDate(final LocalDate localDate) {
        int[] o = new int[9];
        o[0] = localDate.getYear();
        o[1] = localDate.getMonthValue();
        o[2] = localDate.getDayOfMonth();
        return new XSDDateTime(o, XSDDateTime.YEAR_MASK|XSDDateTime.MONTH_MASK|XSDDateTime.DAY_MASK);
    }

}
