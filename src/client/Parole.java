package client;

class Parole {
    private String phrase;
    private double timestampSecondStart;
    private double timestampSecondStop;
    private String technique;
    private static boolean techniquesActivées = true;

    Parole(String phrase, double timestampSecondStart, double timestampSecondStop, String technique) {
        this.phrase = phrase;
        this.timestampSecondStart = timestampSecondStart;
        this.timestampSecondStop = timestampSecondStop;
        this.technique = technique;
    }

    String getPhrase() {
        return phrase;
    }

    String getTechnique() {
        return technique;
    }

    double getTimestampSecondStart() {
        return timestampSecondStart;
    }

    double getTimestampSecondStop() {
        return timestampSecondStop;
    }

    static void setTechniquesActivées (boolean b) {
        techniquesActivées = b;
    }

    static boolean getTechniquesActivées () {
        return techniquesActivées;
    }
}
