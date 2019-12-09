package client;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

class Voix {
    private ArrayList<Parole> listParoles;
    private String nom;
    private String type;
    private Text fxText;

    Voix(String nom, String type, Text fxText) {
        this.nom = nom;
        this.type = type;
        this.fxText = fxText;
        this.listParoles = new ArrayList<>();
    }

    void addParole(Parole parole){
        this.listParoles.add(parole);
    }

    void updateText(Parole parole){
        if (parole.getTechnique().equals("") || !(Parole.getTechniquesActivées())) {
            this.fxText.setText("[" + this.nom + "] : " + parole.getPhrase());
        } else {
            this.fxText.setText("[" + this.nom + "] [" + parole.getTechnique() + "] : " + parole.getPhrase());
        }
    }

    void clearText(){
        this.fxText.setText("[" + this.nom + "] : ");
    }

    Text getFxText() {
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

    void setFont (Color c, double x, double y) {
        this.fxText.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        this.fxText.setX(x);
        this.fxText.setY(y);
        this.fxText.setFill(c);
    }
    
}
