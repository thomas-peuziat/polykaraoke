import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;



public class ReadFileTest {
    public static void main(String[] args) {
        String chemin = "files/";

        long tailleMidi=0; // Nombre de bytes du fichier midi

        byte[] bytesMidi = null; //Tableau contenant les bytes correspondants au fichier midi
        byte[] bytesTxt = null; //Tableau contenant les bytes correspondants au fichier texte
        byte[] bytesTotal; // Tableau contenant tous les bytes


        Scanner s = null;

        PrintWriter fluxSortieParoles = null;
        String titre;

        // Creation d'un fichier (en bytes) dans lequel seront stockes le midi et le texte
        try {
            //Ouverture d'un flux d'ecriture
            FileOutputStream dest =new FileOutputStream(chemin + "total");
            //Copie des bytes des fichiers midi et texte dans le nouveau fichier
            tailleMidi = Files.copy(Paths.get(chemin + "medley.mid"), dest);
            Files.copy(Paths.get(chemin + "texte.txt"), dest);
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } catch (IOException e) {
            System.out.println("Fichier non trouve");
        }

        // Recuperation des bytes correspondants au midi et au texte et
        try {
            // Recuperation de tous les bytes dans un tableau
            bytesTotal = Files.readAllBytes(new File(chemin+"total").toPath());
            bytesMidi = new byte[(int) tailleMidi];

            //Copie des bytes correspondants au fichier midi
            for (int i=0; i < bytesMidi.length; i++) {
                bytesMidi[i] = bytesTotal[i];
            }
            //Copie des bytes restants
            bytesTxt = new byte[bytesTotal.length - (int)tailleMidi];
            for (int j=0; j < bytesTxt.length; j++) {
                bytesTxt[j] = bytesTotal[j+(int)tailleMidi];
            }

            //Creation des fichiers midi et texte correspondants
            OutputStream midi = new FileOutputStream(new File (chemin+"midi.mid"));
            OutputStream txt = new FileOutputStream(new File (chemin+"texteSortie.txt"));
            // Ecriture des bytes dans chaque fichier
            midi.write(bytesMidi);
            txt.write(bytesTxt);

            // Fermeture des flux
            midi.close();
            txt.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }


        // Parsing du fichier texte pour obtenir le titre et les paroles
        try {
            // Scanneur avec un delimiteur
            s = new Scanner(new File(chemin+"texteSortie.txt")).useDelimiter("\\n*/////\\n*");
            // Creation du fichier pour les paroles
            fluxSortieParoles = new PrintWriter(new FileOutputStream(chemin+"paroles.txt"));
            //Recup titre
            titre = s.next();
            // Recup paroles
            fluxSortieParoles.print(s.next());
            System.out.println("Titre : "+titre);
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } finally {
            s.close();
            fluxSortieParoles.close();
        }

    }

}