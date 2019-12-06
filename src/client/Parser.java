package client;

import javafx.scene.text.Text;

import java.io.*;
import java.util.Scanner;

public class Parser {

    void createMidAndPKST(byte[] bytesTotal, long tailleMidi, String musicName) {
        byte[] bytesMidi = null; //Tableau contenant les bytes correspondants au fichier midi
        byte[] bytesTxt = null; //Tableau contenant les bytes correspondants au fichier texte

        String directory = "files/client/" + musicName;

        // Recuperation des bytes correspondants au midi et au texte et
        try {
            // Recuperation de tous les bytes dans un tableau
            bytesMidi = new byte[(int) tailleMidi];

            //Copie des bytes correspondants au fichier midi
            for (int i = 0; i < bytesMidi.length; i++) {
                bytesMidi[i] = bytesTotal[i];
            }
            //Copie des bytes restants
            bytesTxt = new byte[bytesTotal.length - (int) tailleMidi];
            for (int j = 0; j < bytesTxt.length; j++) {
                bytesTxt[j] = bytesTotal[j + (int) tailleMidi];
            }

            //Creation des fichiers midi et texte correspondants

            new File(directory).mkdirs();

            OutputStream midi = new FileOutputStream(new File(directory + "/" + musicName + ".mid"));
            OutputStream txt = new FileOutputStream(new File(directory + "/" + musicName + ".pkst"));
            // Ecriture des bytes dans chaque fichier
            midi.write(bytesMidi);
            txt.write(bytesTxt);

            // Fermeture des flux
            midi.close();
            txt.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    void createVoixParoles (Morceau m, String chemin, float tempo) {
        Scanner s = null;

        try {
            // Scanneur avec un delimiteur
            s = new Scanner(new File(chemin)).useDelimiter("\\n*/////\\n*");
            m.setTitre(s.next());
            // Creation voix
            String voix = s.next();
            Scanner scannerVoix = null;
            try {
                scannerVoix = new Scanner(voix);
                while (scannerVoix.hasNextLine()) {
                    String ligne = scannerVoix.nextLine();
                    String[] tab = ligne.split(":");
                    Voix v = new Voix(tab[0], tab[1], new Text());
                    m.addVoix(v);
                }
            } finally {
                scannerVoix.close();
            }
            //Creation paroles
            String paroles = s.next();
            Scanner scannerParoles = null;
            try {
                scannerParoles = new Scanner(paroles);
                while (scannerParoles.hasNextLine()) {
                    String ligne = scannerParoles.nextLine();
                    String[] tab = ligne.split(":");
                    String technique= "";
                    if (tab.length==5) {
                        technique = tab[4];
                    }
                    Voix v = m.getVoix(tab[0]);
                    //On divise les timestamps par le tempo pour qu'ils soient toujours synchro
                    v.addParole(new Parole(tab[2], Double.parseDouble(tab[1])/tempo, Double.parseDouble(tab[3])/tempo, technique));
                }
            } finally {
                scannerParoles.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } finally {
            s.close();
        }
    }

}
