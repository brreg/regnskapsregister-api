package no.brreg.regnskap.jena;

import no.brreg.regnskap.generated.model.Regnskap;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
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
    private static final List<MimeType> SUPPORTED_SERIALIZE_MIMETYPES = Arrays.asList(JSON_LD_MIMETYPE, RDF_MIMETYPE, TURTLE_MIMETYPE);

    private static final int PARAM_PAIR_LENGTH = 2;


    public static RDFFormat mimeTypeToJenaFormat(final MimeType mimeType) {
        RDFFormat format = null;
        if (JSON_LD_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.JSONLD);
        } else if (RDF_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.RDFXML);
        } else if (TURTLE_MIMETYPE.equals(mimeType)) {
            format = new RDFFormat(Lang.TURTLE);
        }
        return format;
    }

    private static class MediaQuality {
        public MimeType mediaType = null;
        public double quality = 0.0;
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
            //mediaRange is either "type/subtype" or "type/subtype;parameter=token[;parameter=token]"
            String mediaRange = mediaRanges[mediaRangeIndex].trim();
            if (mediaRange==null || mediaRange.isEmpty()) {
                //This should not happen, but better safe than sorry
                continue;
            }
            MediaQuality mediaQuality = extractMediaQuality(mediaRange);          
            if (mediaQuality.mediaType==null) {
                continue;
            }

            //Collect highest quality value
            if (!acceptedMimeTypes.containsKey(mediaQuality.mediaType) || acceptedMimeTypes.get(mediaQuality.mediaType)<mediaQuality.quality) {
                acceptedMimeTypes.put(mediaQuality.mediaType, mediaQuality.quality);
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
        return negotiateHighestQualityMimeType(acceptedMimeTypes);
    }

    public static MediaQuality extractMediaQuality(String mediaRange) {
        MediaQuality mediaQuality = new MediaQuality();

        String[] parameters = mediaRange.split(";");
        if (parameters==null || parameters.length<=1) {
            mediaQuality.mediaType = MimeType.valueOf(mediaRange);
            mediaQuality.quality = 1.0;
        } else {
            //There are parameters present!
            mediaQuality.mediaType = MimeType.valueOf(parameters[0]);
            mediaQuality.quality = 1.0; //Default to 1.0, but check if quality is one of the parameters

            for (int acceptExtensionIndex=1; acceptExtensionIndex<parameters.length; acceptExtensionIndex++) {
                String acceptParams = parameters[acceptExtensionIndex].trim();
                if (acceptParams==null || acceptParams.isEmpty()) {
                    //This should not happen, but better safe than sorry
                    continue;
                }

                String[] acceptExtension = acceptParams.split("=");
                if (acceptExtension!=null && acceptExtension.length==PARAM_PAIR_LENGTH && "q".equals(acceptExtension[0].trim())) {
                    try {
                        mediaQuality.quality = Double.valueOf(acceptExtension[1]);
                    } catch (NumberFormatException e) {
                        mediaQuality.quality = 0.0;
                    }
                }
            }
        }
        return mediaQuality;
    }

    public static MimeType negotiateHighestQualityMimeType(Map<MimeType,Double> acceptedMimeTypes) {
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
        Resource regnskapResource = model.createResource(urls.createResourceUri(regnskap.getVirksomhet().getOrganisasjonsnummer(), regnskap.getId()));
        regnskapResource.addProperty(RDF.type, BR.Regnskap)
                        .addProperty(DCTerms.identifier, regnskap.getId().toString());
        addLiteralIfNotNull(regnskapResource, BR.avviklingsregnskap, regnskap.getAvviklingsregnskap());
        addLiteralIfNotNull(regnskapResource, SCHEMA.currency, regnskap.getValuta());
        if (regnskap.getOppstillingsplan() != null) {
            addLiteralIfNotNull(regnskapResource, BR.oppstillingsplan, regnskap.getOppstillingsplan().getValue());
        }

        //Revisjon
        if (regnskap.getRevisjon() != null) {
            Resource revisjonResource = model.createResource(BR.Revisjon);
            addLiteralIfNotNull(revisjonResource, BR.ikkeRevidertAarsregnskap, regnskap.getRevisjon().getIkkeRevidertAarsregnskap());
            addLiteralIfNotNull(revisjonResource, BR.fravalgRevisjon, regnskap.getRevisjon().getFravalgRevisjon());
            regnskapResource.addProperty(BR.revisjon, revisjonResource);
        }

        //Regnskapsprinsipper
        if (regnskap.getRegnkapsprinsipper() != null) {
            Resource regnskapsprinsipper = model.createResource(BR.Regnskapsprinsipper);
            addLiteralIfNotNull(regnskapsprinsipper, BR.smaaForetak, regnskap.getRegnkapsprinsipper().getSmaaForetak());
            if (regnskap.getRegnkapsprinsipper().getRegnskapsregler() != null) {
                addLiteralIfNotNull(regnskapsprinsipper, BR.regnskapsregler, regnskap.getRegnkapsprinsipper().getRegnskapsregler().getValue());
            }
            regnskapResource.addProperty(BR.regnskapsprinsipper, regnskapsprinsipper);
        }

        if (regnskap.getRegnskapsperiode() != null) {
            regnskapResource = addRegnskapsperiode(model, regnskapResource, regnskap.getRegnskapsperiode().getFraDato(), regnskap.getRegnskapsperiode().getTilDato());
        }

        regnskapResource = addVirksomhet(model, regnskapResource, regnskap, urls);

        if (regnskap.getEiendeler() != null) {
            Resource eiendeler = model.createResource(BR.Eiendeler);
            addLiteralIfNotNull(eiendeler, BR.goodwill, regnskap.getEiendeler().getGoodwill());
            addLiteralIfNotNull(eiendeler, BR.sumVarer, regnskap.getEiendeler().getSumVarer());
            addLiteralIfNotNull(eiendeler, BR.sumFordringer, regnskap.getEiendeler().getSumFordringer());
            addLiteralIfNotNull(eiendeler, BR.sumInvesteringer, regnskap.getEiendeler().getSumInvesteringer());
            addLiteralIfNotNull(eiendeler, BR.sumBankinnskuddOgKontanter, regnskap.getEiendeler().getSumBankinnskuddOgKontanter());
            Resource sumEiendeler = addLiteralIfNotNull(eiendeler, BR.sumEiendeler, regnskap.getEiendeler().getSumEiendeler());
            if (sumEiendeler != null) {
                if (regnskap.getEiendeler().getAnleggsmidler() != null) {
                    Resource anleggsmidler = model.createResource(BR.Anleggsmidler);
                    addLiteralIfNotNull(anleggsmidler, BR.sumAnleggsmidler, regnskap.getEiendeler().getAnleggsmidler().getSumAnleggsmidler());
                    sumEiendeler.addProperty(BR.anleggsmidler, anleggsmidler);
                }
                if (regnskap.getEiendeler().getOmloepsmidler() != null) {
                    Resource omloepsmidler = model.createResource(BR.Omloepsmidler);
                    addLiteralIfNotNull(omloepsmidler, BR.sumOmloepsmidler, regnskap.getEiendeler().getOmloepsmidler().getSumOmloepsmidler());
                    sumEiendeler.addProperty(BR.omloepsmidler, omloepsmidler);
                }
            }
            regnskapResource.addProperty(BR.eiendeler, eiendeler);
        }

        if (regnskap.getEgenkapitalGjeld() != null) {
            Resource egenkapitalGjeld = model.createResource(BR.EgenkapitalGjeld);
            Resource sumEgenkapitalGjeld = addLiteralIfNotNull(egenkapitalGjeld, BR.sumEgenkapitalGjeld, regnskap.getEgenkapitalGjeld().getSumEgenkapitalGjeld());
            if (sumEgenkapitalGjeld != null) {
                if (regnskap.getEgenkapitalGjeld().getEgenkapital() != null) {
                    Resource egenkapital = model.createResource(BR.Egenkapital);
                    Resource sumEgenkapital = addLiteralIfNotNull(egenkapital, BR.sumEgenkapital, regnskap.getEgenkapitalGjeld().getEgenkapital().getSumEgenkapital());
                    if (sumEgenkapital != null) {
                        if (regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital() != null) {
                            Resource innskuttEgenkapital = model.createResource(BR.InnskuttEgenkapital);
                            addLiteralIfNotNull(innskuttEgenkapital, BR.sumInnskuttEgenkaptial, regnskap.getEgenkapitalGjeld().getEgenkapital().getInnskuttEgenkapital().getSumInnskuttEgenkaptial());
                            sumEgenkapital.addProperty(BR.innskuttEgenkapital, innskuttEgenkapital);
                        }
                        if (regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital() != null) {
                            Resource opptjentEgenkapital = model.createResource(BR.OpptjentEgenkapital);
                            addLiteralIfNotNull(opptjentEgenkapital, BR.sumOpptjentEgenkapital, regnskap.getEgenkapitalGjeld().getEgenkapital().getOpptjentEgenkapital().getSumOpptjentEgenkapital());
                            sumEgenkapital.addProperty(BR.opptjentEgenkapital, opptjentEgenkapital);
                        }
                    }
                    sumEgenkapitalGjeld.addProperty(BR.egenkapital, egenkapital);
                }
                if (regnskap.getEgenkapitalGjeld().getGjeldOversikt() != null) {
                    Resource gjeldOversikt = model.createResource(BR.GjeldOversikt);
                    Resource sumGjeld = addLiteralIfNotNull(gjeldOversikt, BR.sumGjeld, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getSumGjeld());
                    if (sumGjeld != null) {
                        if (regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld() != null) {
                            Resource langsiktigGjeld = model.createResource(BR.LangsiktigGjeld);
                            addLiteralIfNotNull(langsiktigGjeld, BR.sumLangsiktigGjeld, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getLangsiktigGjeld().getSumLangsiktigGjeld());
                            sumGjeld.addProperty(BR.langsiktigGjeld, langsiktigGjeld);
                        }
                        if (regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld() != null) {
                            Resource kortsiktigGjeld = model.createResource(BR.KortsiktigGjeld);
                            addLiteralIfNotNull(kortsiktigGjeld, BR.sumKortsiktigGjeld, regnskap.getEgenkapitalGjeld().getGjeldOversikt().getKortsiktigGjeld().getSumKortsiktigGjeld());
                            sumGjeld.addProperty(BR.opptjentEgenkapital, kortsiktigGjeld);
                        }
                    }
                    sumEgenkapitalGjeld.addProperty(BR.gjeldOversikt, gjeldOversikt);
                }
            }
            regnskapResource.addProperty(BR.egenkapitalGjeld, egenkapitalGjeld);
        }

        if (regnskap.getResultatregnskapResultat() != null) {
            Resource resultatregnskapResultat = model.createResource(BR.ResultatregnskapResultat);
            addLiteralIfNotNull(resultatregnskapResultat, BR.aarsresultat, regnskap.getResultatregnskapResultat().getAarsresultat());
            addLiteralIfNotNull(resultatregnskapResultat, BR.totalresultat, regnskap.getResultatregnskapResultat().getTotalresultat());
            addLiteralIfNotNull(resultatregnskapResultat, BR.ordinaertResultatFoerSkattekostnad, regnskap.getResultatregnskapResultat().getOrdinaertResultatFoerSkattekostnad());
            addLiteralIfNotNull(resultatregnskapResultat, BR.ordinaertResultatSkattekostnad, regnskap.getResultatregnskapResultat().getOrdinaertResultatSkattekostnad());
            addLiteralIfNotNull(resultatregnskapResultat, BR.ekstraordinaerePoster, regnskap.getResultatregnskapResultat().getEkstraordinaerePoster());
            addLiteralIfNotNull(resultatregnskapResultat, BR.skattekostnadEkstraordinaertResultat, regnskap.getResultatregnskapResultat().getSkattekostnadEkstraordinaertResultat());
            if (regnskap.getResultatregnskapResultat().getDriftsresultat() != null) {
                Resource driftsresultat = model.createResource(BR.Driftsresultat);
                Resource sumDriftsresultat = addLiteralIfNotNull(driftsresultat, BR.sumDriftsresultat, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsresultat());
                if (sumDriftsresultat != null) {
                    if (regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter() != null) {
                        Resource driftsinntekter = model.createResource(BR.Driftsinntekter);
                        addLiteralIfNotNull(driftsinntekter, BR.salgsinntekter, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSalgsinntekter());
                        addLiteralIfNotNull(driftsinntekter, BR.sumDriftsinntekter, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftsinntekter().getSumDriftsinntekter());
                        sumDriftsresultat.addProperty(BR.driftsinntekter, driftsinntekter);
                    }
                    if (regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad() != null) {
                        Resource driftskostnad = model.createResource(BR.Driftskostnad);
                        addLiteralIfNotNull(driftskostnad, BR.loennskostnad, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getLoennskostnad());
                        addLiteralIfNotNull(driftskostnad, BR.sumDriftskostnad, regnskap.getResultatregnskapResultat().getDriftsresultat().getDriftskostnad().getSumDriftskostnad());
                        sumDriftsresultat.addProperty(BR.driftskostnad, driftskostnad);
                    }
                }
                resultatregnskapResultat.addProperty(BR.driftsresultat, driftsresultat);
            }
            if (regnskap.getResultatregnskapResultat().getFinansresultat() != null) {
                Resource finansresultat = model.createResource(BR.Finansresultat);
                Resource nettoFinans = addLiteralIfNotNull(finansresultat, BR.nettoFinans, regnskap.getResultatregnskapResultat().getFinansresultat().getNettoFinans());
                if (nettoFinans != null) {
                    if (regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt() != null) {
                        Resource finansinntekt = model.createResource(BR.Finansinntekt);
                        addLiteralIfNotNull(finansinntekt, BR.sumFinansinntekter, regnskap.getResultatregnskapResultat().getFinansresultat().getFinansinntekt().getSumFinansinntekter());
                        nettoFinans.addProperty(BR.finansinntekt, finansinntekt);
                    }
                    if (regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad() != null) {
                        Resource finanskostnad = model.createResource(BR.Finanskostnad);
                        addLiteralIfNotNull(finanskostnad, BR.rentekostnadSammeKonsern, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getRentekostnadSammeKonsern());
                        addLiteralIfNotNull(finanskostnad, BR.annenRentekostnad, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getAnnenRentekostnad());
                        addLiteralIfNotNull(finanskostnad, BR.sumFinanskostnad, regnskap.getResultatregnskapResultat().getFinansresultat().getFinanskostnad().getSumFinanskostnad());
                        nettoFinans.addProperty(BR.finanskostnad, finanskostnad);
                    }
                }
                resultatregnskapResultat.addProperty(BR.finansresultat, finansresultat);
            }
            regnskapResource.addProperty(BR.resultatregnskapResultat, resultatregnskapResultat);
        }
        return model;
    }

    private static Resource addLiteralIfNotNull(final Resource resource, final Property property, final Object value) {
        if (value != null) {
            return resource.addLiteral(property, value);
        }
        return null;
    }

    public static String modelToString(final Model model, final RDFFormat format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            model.write(baos, format.toString());
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
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

    public static boolean jenaCanSerialize(final MimeType mimetype) {
        return mimetype!=null && SUPPORTED_SERIALIZE_MIMETYPES.contains(mimetype);
    }

}
