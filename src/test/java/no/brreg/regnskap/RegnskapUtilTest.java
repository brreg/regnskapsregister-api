package no.brreg.regnskap;

import no.brreg.regnskap.generated.model.Tidsperiode;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RegnskapUtilTest {

    @Test
    public void testForYearIsInsidePeriod() {
        Tidsperiode period = new Tidsperiode()
                                .fraDato(LocalDate.of(2020,1,1))
                                .tilDato(LocalDate.of(2020,12,31));
        
        assertEquals(true, RegnskapUtil.forYear(period, 2020));
    }


    @Test
    public void testForYearIsOutsidePeriod() {
        Tidsperiode period = new Tidsperiode()
                                .fraDato(LocalDate.of(2020,1,1))
                                .tilDato(LocalDate.of(2020,12,31));

        assertEquals(false, RegnskapUtil.forYear(period, 2019));
    }    
}
