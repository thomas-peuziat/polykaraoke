package client;

class Parole {
    private String phrase;
    private double timestampSecond;

    Parole(String phrase, double timestampSecond) {
        this.phrase = phrase;
        this.timestampSecond = timestampSecond;
    }

    String getPhrase() {
        return phrase;
    }

    double getTimestampSecond() {
        return timestampSecond;
    }
}
