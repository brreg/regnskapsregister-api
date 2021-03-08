package no.brreg.regnskap.model;

import no.brreg.regnskap.generated.model.*;


public class RegnskapFields {

    private Eiendeler eiendeler = new Eiendeler().omloepsmidler(new Omloepsmidler())
                                                 .anleggsmidler(new Anleggsmidler());
    private EgenkapitalGjeld egenkapitalGjeld = new EgenkapitalGjeld().egenkapital(new Egenkapital().opptjentEgenkapital(new OpptjentEgenkapital())
                                                                                                    .innskuttEgenkapital(new InnskuttEgenkapital()))
                                                                      .gjeldOversikt(new Gjeld().kortsiktigGjeld(new KortsiktigGjeld())
                                                                                                .langsiktigGjeld(new LangsiktigGjeld()));
    private ResultatregnskapResultat resultatregnskapResultat = new ResultatregnskapResultat().finansresultat(new Finansresultat().finansinntekt(new Finansinntekt())
                                                                                                                                  .finanskostnad(new Finanskostnad()))
                                                                                              .driftsresultat(new Driftsresultat().driftsinntekter(new Driftsinntekter())
                                                                                                                                  .driftskostnad(new Driftskostnad()));

    public Eiendeler getEiendeler() {
        return eiendeler;
    }

    public void setEiendeler(final Eiendeler eiendeler) {
        this.eiendeler = eiendeler;
    }

    public EgenkapitalGjeld getEgenkapitalGjeld() {
        return egenkapitalGjeld;
    }

    public void setEgenkapitalGjeld(final EgenkapitalGjeld egenkapitalGjeld) {
        this.egenkapitalGjeld = egenkapitalGjeld;
    }

    public ResultatregnskapResultat getResultatregnskapResultat() {
        return resultatregnskapResultat;
    }

    public void setResultatregnskapResultat(final ResultatregnskapResultat resultatregnskapResultat) {
        this.resultatregnskapResultat = resultatregnskapResultat;
    }

}
