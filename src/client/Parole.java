package client;

class Parole {
    private String phrase;
    private double timestampSecondStart;
    private double timestampSecondStop;

    Parole(String phrase, double timestampSecondStart, double timestampSecondStop) {
        this.phrase = phrase;
        this.timestampSecondStart = timestampSecondStart;
        this.timestampSecondStop = timestampSecondStop;
    }

    String getPhrase() {
        return phrase;
    }

    double getTimestampSecondStart() {
        return timestampSecondStart;
    }

    public double getTimestampSecondStop() {
        return timestampSecondStop;
    }
}
