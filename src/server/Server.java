package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

import message.Message;

public class Server {
    public static void main(String[] args) {

        String unprocessedPath = "./files/server/unprocessed/";
        String availablePath = "./files/server/available/";
        new File(unprocessedPath).mkdirs();
        new File(availablePath).mkdirs();

        // Permet d'avoir la liste des sous dossiers de unprocessedPath
        File file = new File(unprocessedPath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        // Pour chaque sous dossiers, on va créer un fichier .ser
        // On pourrait ne pas faire de fichier .ser et juste créer une liste de Message.
        for (String musicName : directories) {
            Message m = new Message();
            String musicDirectory = musicName + "/";
            m.createTabBytes(unprocessedPath + musicDirectory + musicName + ".mid", unprocessedPath  + musicDirectory + musicName + ".pkst", unprocessedPath + musicDirectory + musicName + "");
            System.out.println("Taille :"+ m.getTailleMidi());

            ObjectOutputStream oos = null;

            try {
                final FileOutputStream fichier = new FileOutputStream(availablePath + musicName +".ser");
                oos = new ObjectOutputStream(fichier);
                oos.writeObject(m);
                oos.flush();
            } catch (final java.io.IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) {
                        oos.flush();
                        oos.close();
                    }
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        ServerSocket listener = null;
        Socket socketOfServer;
        ObjectInputStream in;
        ObjectOutputStream out;

        try {
            listener = new ServerSocket(9999);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        while(true) {
            try {
                System.out.println("Server is waiting to accept user...");
                socketOfServer = listener.accept();
                System.out.println("Accept a client!");

                out = new ObjectOutputStream(socketOfServer.getOutputStream());
                out.flush();

                in = new ObjectInputStream(socketOfServer.getInputStream());

                out.writeUTF("Bienvenue sur PolyKaraoke. Voici les morceaux disponibles : " + Arrays.toString(directories) + ".\nQuel morceau souhaitez-vous écouter ? (Ecrivez le nom du morceau) : ");
                out.flush();

                String morceauChoisi = in.readUTF();
                System.out.println("Morceau choisi : " + morceauChoisi);

                // Deserialisation du message choisi
                ObjectInputStream ois = null;
                Message msgSended = new Message();
                try {
                    final FileInputStream fichier = new FileInputStream(availablePath + morceauChoisi + ".ser");
                    ois = new ObjectInputStream(fichier);
                    msgSended = (Message) ois.readObject();

                    // Envoi du message choisi
                    out.writeObject(msgSended);
                    out.flush();

                    System.out.println(morceauChoisi + " envoyé");

                } catch (final IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ois != null) {
                            ois.close();
                        }
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }

                in.close();
                out.close();
                socketOfServer.close();

            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }
}