package client;

import java.io.File;
import java.util.HashMap;

public class Morceau {

    private String titre;
    private HashMap<String, Voix> mapVoix;
    private File midiFile;

    Morceau(String titre, HashMap<String, Voix> mapVoix, File midiFile) {
        this.titre = titre;
        this.mapVoix = mapVoix;
        this.midiFile = midiFile;
    }

    public String getTitre() {
        return titre;
    }

    public HashMap<String, Voix> getMapVoix() {
        return mapVoix;
    }

    public File getMidiFile() {
        return midiFile;
    }
}
