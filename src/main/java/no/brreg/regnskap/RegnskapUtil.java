package no.brreg.regnskap;

import no.brreg.regnskap.generated.model.Tidsperiode;


public class RegnskapUtil {

    public static boolean forYear(final Tidsperiode tidsperiode, final int year) {
        return (tidsperiode!=null &&
                (tidsperiode.getFraDato()==null || tidsperiode.getFraDato().getYear()<=year) &&
                (tidsperiode.getTilDato()==null || tidsperiode.getTilDato().getYear()>=year));
    }

}
