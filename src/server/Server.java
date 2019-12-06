package server;

import message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Server {

    private String unprocessedPath;
    private String availablePath;
    private String statsPath;
    private int port;

    Server(String unprocessedPath, String availablePath, String statsPath, int port) {
        this.unprocessedPath = unprocessedPath;
        this.availablePath = availablePath;
        this.statsPath = statsPath;
        this.port = port;

        new File(unprocessedPath).mkdirs();
        new File(availablePath).mkdirs();
        new File(statsPath).mkdirs();
    }

    private void updateStatistics(String statsPath, String clientName, String morceauName){
        try {
            // Ouverture d'un flux d'ecriture
            FileOutputStream dest = new FileOutputStream(statsPath, true);
            dest.write((clientName + ":" + morceauName + "\r\n").getBytes());
            dest.close();
        } catch (IOException e) {
            System.out.println("Fichier non trouvé");
        }
    }

    void readStatistics(String statsPath) {
        Scanner scanner = null;
        HashMap<String, Integer> statsClient = new HashMap<>();
        HashMap<String, Integer> statsMorceau = new HashMap<>();

        try {
            scanner = new Scanner(new File(statsPath));
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String[] tab = ligne.split(":");

                if (statsClient.containsKey(tab[0])){
                    statsClient.put(tab[0], statsClient.get(tab[0])+1);
                } else {
                    statsClient.put(tab[0], 1);
                }

                if (statsMorceau.containsKey(tab[1])){
                    statsMorceau.put(tab[1], statsMorceau.get(tab[1])+1);
                } else {
                    statsMorceau.put(tab[1], 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }

        Integer maxIntClient = 0;
        String maxStringClient = "";
        Integer maxIntMorceau = 0;
        String maxStringMorceau = "";

        System.out.println("Nom client : nombre écoutes");
        for (Map.Entry<String, Integer> entry : statsClient.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            if (entry.getValue() > maxIntClient) {
                maxIntClient = entry.getValue();
                maxStringClient = entry.getKey();
            }
        }

        System.out.println("\nNom morceau : nombre écoutes");
        for (Map.Entry<String, Integer> entry : statsMorceau.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            if (entry.getValue() > maxIntMorceau) {
                maxIntMorceau = entry.getValue();
                maxStringMorceau = entry.getKey();
            }
        }

        System.out.println("Client écoutant le plus de morceau : " + maxStringClient + " avec " + maxIntClient + " écoutes.");
        System.out.println("Morceau le plus écouté : " + maxStringMorceau + " avec " + maxIntMorceau + " écoutes.");
    }

    String[] getSubdirectoriesUnprocessedPath() {
        // Permet d'avoir la liste des sous dossiers de unprocessedPath
        File file = new File(unprocessedPath);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return directories;
    }

    void processMusics(String[] directories) {
        // Pour chaque sous dossiers, on va créer un fichier .ser
        for (String musicName : directories) {
            Message m = new Message();
            String musicDirectory = musicName + "/";
            m.createTabBytes(unprocessedPath + musicDirectory + musicName + ".mid", unprocessedPath  + musicDirectory + musicName + ".pkst", unprocessedPath + musicDirectory + musicName + "");

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
    }

    void communicationLoop(){
        ServerSocket listener = null;
        Socket socketOfServer;
        ObjectInputStream in;
        ObjectOutputStream out;

        try {
            listener = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        while(true) {
            try {
                System.out.println("En attente de client...");
                socketOfServer = listener.accept();
                System.out.println("Un client a été accepté!\n");

                out = new ObjectOutputStream(socketOfServer.getOutputStream());
                out.flush();

                in = new ObjectInputStream(socketOfServer.getInputStream());

                out.writeUTF("Bienvenue sur PolyKaraoke. Veuillez entrer votre nom : ");
                out.flush();

                String nomClient = in.readUTF();
                String[] subdirectories = getSubdirectoriesUnprocessedPath();

                out.writeUTF("Voici les morceaux disponibles : " + Arrays.toString(subdirectories) + ".\nQuel morceau souhaitez-vous écouter ? (Ecrivez le nom du morceau) : ");
                out.flush();

                //Envoi de la liste des morceaux disponibles
                out.writeObject(subdirectories);
                out.flush();

                String morceauChoisi = in.readUTF();

                System.out.println("Client : " + nomClient + " et morceau choisi : " + morceauChoisi);


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

                    updateStatistics(statsPath, nomClient, morceauChoisi);
                    readStatistics(statsPath);

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
