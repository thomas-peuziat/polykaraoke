package client;

import java.util.ArrayList;
import javafx.scene.text.Text;

class Voix {
    private ArrayList<Parole> listParoles = new ArrayList<>();
    private String nom;
    private String type;
    private Text fxText;

    Voix(String nom, String type, Text fxText) {
        this.nom = nom;
        this.type = type;
        this.fxText = fxText;
    }

    void addParole(Parole parole){
        this.listParoles.add(parole);
    }

    void updateText(Parole parole){
        this.fxText.setText("[" + this.nom + "] : " + parole.getPhrase());
    }

    public Text getFxText() {
        return fxText;
    }

    String getNom() {
        return nom;
    }

    String getType() {
        return type;
    }

    ArrayList<Parole> getListParoles() {
        return listParoles;
    }
}
