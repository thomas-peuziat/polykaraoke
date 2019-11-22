package client;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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

    void clearText(){
        this.fxText.setText("[" + this.nom + "] : ");
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

    public void setFont (Color c, double x, double y) {
        this.fxText.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        this.fxText.setX(x);
        this.fxText.setY(y);
        this.fxText.setFill(c);
    }
}
