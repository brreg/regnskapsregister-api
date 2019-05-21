package no.regnskap.model.persistance;

public class RegnskapFelt {
    private String feltkode;
    private long sum;

    public String getFeltkode() {
        return feltkode;
    }

    public void setFeltkode(String feltkode) {
        this.feltkode = feltkode;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }
}
