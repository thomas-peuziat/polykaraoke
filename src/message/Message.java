package message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Message implements Serializable {
    long tailleMidi;
    byte[] bytesTotal;

    public long getTailleMidi() {
        return this.tailleMidi;
    }
    public byte[] getBytesTotal () {
        return this.bytesTotal;
    }

    public void setTailleMidi (long t) {
        this.tailleMidi = t;
    }
    public void setBytesTotal (byte[] tab) {
        this.bytesTotal = tab;
    }

    public void createTabBytes(String cheminMidi, String cheminTexte, String cheminTotal) {
        //String chemin = "files/";
        byte[] bytesTotal;
        long tailleMidi = 0; // Nombre de bytes du fichier midi

        // Creation d'un fichier (en bytes) dans lequel seront stockes le midi et le texte
        try {
            //Ouverture d'un flux d'ecriture
            FileOutputStream dest = new FileOutputStream(cheminTotal);
            //Copie des bytes des fichiers midi et texte dans le nouveau fichier
            tailleMidi = Files.copy(Paths.get(cheminMidi), dest);
            setTailleMidi(tailleMidi);
            Files.copy(Paths.get(cheminTexte), dest);
            bytesTotal = Files.readAllBytes(new File(cheminTotal).toPath());
            setBytesTotal(bytesTotal);
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } catch (IOException e) {
            System.out.println("Fichier non trouve");
        }
    }
}