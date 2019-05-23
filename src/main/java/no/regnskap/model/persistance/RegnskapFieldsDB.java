package no.regnskap.model.persistance;

import no.regnskap.generated.model.EgenkapitalGjeld;
import no.regnskap.generated.model.Eiendeler;
import no.regnskap.generated.model.ResultatregnskapResultat;
import org.springframework.data.mongodb.core.mapping.Field;

public class RegnskapFieldsDB {
    private Eiendeler eiendeler;
    @Field("egenkapital_gjeld")
    private EgenkapitalGjeld egenkapitalGjeld;
    @Field("resultatregnskap_resultat")
    private ResultatregnskapResultat resultatregnskapResultat;

    public Eiendeler getEiendeler() {
        return eiendeler;
    }

    public void setEiendeler(Eiendeler eiendeler) {
        this.eiendeler = eiendeler;
    }

    public EgenkapitalGjeld getEgenkapitalGjeld() {
        return egenkapitalGjeld;
    }

    public void setEgenkapitalGjeld(EgenkapitalGjeld egenkapitalGjeld) {
        this.egenkapitalGjeld = egenkapitalGjeld;
    }

    public ResultatregnskapResultat getResultatregnskapResultat() {
        return resultatregnskapResultat;
    }

    public void setResultatregnskapResultat(ResultatregnskapResultat resultatregnskapResultat) {
        this.resultatregnskapResultat = resultatregnskapResultat;
    }
}
