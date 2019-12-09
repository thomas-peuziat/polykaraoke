package client;

import java.io.File;
import java.util.HashMap;

public class Morceau {

    private String titre;
    private HashMap<String, Voix> mapVoix;
    private File midiFile;

    Morceau () {
        this.titre = "";
        this.mapVoix = new HashMap<>();
        this.midiFile = null;
    }

    Morceau(String titre, HashMap<String, Voix> mapVoix, File midiFile) {
        this.titre = titre;
        this.mapVoix = mapVoix;
        this.midiFile = midiFile;
    }

    void setTitre (String t) {
        this.titre = t;
    }

    void setFile (File f) {
        this.midiFile = f;
    }

    void addVoix (Voix v) {
        this.mapVoix.put(v.getNom(), v);
    }

    Voix getVoix (String cle) {
        return this.mapVoix.get(cle);
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

    public File getFile() {
        return midiFile;
    }
}
