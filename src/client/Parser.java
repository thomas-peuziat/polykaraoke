package client;

import javafx.scene.text.Text;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class Parser {

    void createMidAndPKST(byte[] bytesTotal, long tailleMidi, String musicName) {
        byte[] bytesMidi = null; //Tableau contenant les bytes correspondants au fichier midi
        byte[] bytesTxt = null; //Tableau contenant les bytes correspondants au fichier texte
        //byte[] bytesTotal; // Tableau contenant tous les bytes

        //File fileTotal = new File(inputPath);
        //String directory = fileTotal.getParent() + "/";
        String directory = "files/client/" + musicName;

        // Recuperation des bytes correspondants au midi et au texte et
        try {
            // Recuperation de tous les bytes dans un tableau
            //bytesTotal = Files.readAllBytes(fileTotal.toPath());
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

            new File(directory).mkdir();

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

    void createVoixParoles (Morceau m, String chemin) {
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
                    Voix v = m.getVoix(tab[0]);
                    v.addParole(new Parole(tab[2], Double.parseDouble(tab[1]), Double.parseDouble(tab[3])));
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

    // Revoir cette partie de "création de fichiers paroles et voix"
    // et la partie "parsing des fichiers paroles et voix"
    // car il est inutile de transformer le fichier .pkst en sous fichier pour ensuite re-parser ces fichiers.
//
//        // Parsing du fichier texte pour obtenir le titre et les paroles
//        Scanner s = null;
//        PrintWriter fluxSortieParoles = null;
//        PrintWriter fluxSortieVoix = null;
//        try {
//            // Scanneur avec un delimiteur
//            s = new Scanner(new File(directory + "texteSortie.txt")).useDelimiter("\\n*/////\\n*");
//            // Creation du fichier pour les paroles
//            fluxSortieParoles = new PrintWriter(new FileOutputStream(chemin + "paroles.txt"));
//            // Creation du fichier pour les voix
//            fluxSortieVoix = new PrintWriter(new FileOutputStream(chemin + "voix.txt"));
//            //Recup titre
//            String titre = s.next();
//            // Recup voix
//            fluxSortieVoix.print(s.next());
//            // Recup paroles
//            fluxSortieParoles.print(s.next());
//        } catch (FileNotFoundException e) {
//            System.out.println("Fichier non trouve");
//        } finally {
//            s.close();
//            fluxSortieParoles.close();
//            fluxSortieVoix.close();
//        }
//
//
//    void createVoix(String chemin) {
//        Scanner s = null;
//        try {
//            s = new Scanner(new File(chemin));
//            while (s.hasNextLine()) {
//                String ligne = s.nextLine();
//                String[] tab = ligne.split(":");
//                Voix v = new Voix(tab[0], tab[1], new Text());
//                this.mapVoix.put(v.getNom(), v);
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("Fichier non trouve");
//        } finally {
//            s.close();
//        }
//    }
//
//    void createParoles(String chemin) {
//        Scanner s = null;
//        try {
//            s = new Scanner(new File(chemin));
//            while (s.hasNextLine()) {
//                String ligne = s.nextLine();
//                String[] tab = ligne.split(":");
//                Voix v = this.mapVoix.get(tab[0]);
//                v.addParole(new Parole(tab[2], Double.parseDouble(tab[1]), Double.parseDouble(tab[3])));
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("Fichier non trouve");
//        } finally {
//            s.close();
//        }
//    }
}
