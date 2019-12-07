package client;

import javafx.animation.KeyFrame;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import message.Message;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

class Client {
    private Scanner sc;
    private String nom;
    private String morceauChoisi;
    private String directoryPath; // "./files/client/"
    private int port;

    Client(String directoryPath, int port) {
        this.sc = new Scanner(System.in);
        this.port = port;
        this.directoryPath = directoryPath;
        new File(directoryPath).mkdirs();
    }

    public String getNom() {
        return nom;
    }

    public String getMorceauChoisi() {
        return morceauChoisi;
    }

    AbstractMap.SimpleEntry<String, Message> serverCommunication(final String serverHost) {
        Socket socketOfClient;
        ObjectInputStream in;
        ObjectOutputStream out;
        Message m = null;
        String morceauChoisi = null;
        try {
            socketOfClient = new Socket(serverHost, port);

            out = new ObjectOutputStream(socketOfClient.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socketOfClient.getInputStream());

            System.out.println("Connexion acceptée.");

            System.out.println(in.readUTF());

            // L'utilisateur rentre son nom
            nom = sc.nextLine();
            out.writeUTF(nom);
            out.flush();

            System.out.println(in.readUTF());

            String[] morceauxDisponibles = (String[]) in.readObject();

            morceauChoisi = sc.nextLine();
            //Verification saisie
            while (!checkIfExists(morceauxDisponibles, morceauChoisi)) {
                System.out.println("Veuillez saisir un nom de morceau valide : ");
                morceauChoisi = sc.nextLine();
            }
            out.writeUTF(morceauChoisi);
            this.morceauChoisi = morceauChoisi;
            out.flush();

            Object objetRecu = in.readObject();

            m = (Message) objetRecu;

            String musicPath = directoryPath + morceauChoisi;
            new File(musicPath).mkdirs();

            in.close();
            out.close();
            socketOfClient.close();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }

        return new AbstractMap.SimpleEntry<>(morceauChoisi, m);
    }

    ArrayList<KeyFrame> createKeyFrame(ArrayList<Voix> activatedVoix) {
        ArrayList<KeyFrame> listKeyFrames = new ArrayList<>();

        for (Voix voix : activatedVoix) {
            ArrayList<Parole> listParoles = new ArrayList<>(voix.getListParoles());
            for (Parole parole : listParoles) {
                KeyFrame keyframe = new KeyFrame(
                        Duration.seconds(parole.getTimestampSecondStart()),
                        actionEvent -> voix.updateText(parole));
                listKeyFrames.add(keyframe);

                keyframe = new KeyFrame(
                        Duration.seconds(parole.getTimestampSecondStop()),
                        actionEvent -> voix.clearText());
                listKeyFrames.add(keyframe);
            }
        }
        return listKeyFrames;
    }


    Sequencer initSequencer(File midiFile, float tempoFactor) {
        Sequencer sequencer = null;
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setTempoFactor(tempoFactor);
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }

        return sequencer;
    }

    ArrayList<Voix> gestionVoix(Morceau morceau, Group root) {
        // Recuperation de toutes les voix
        ArrayList<Voix> tabVoix = new ArrayList<>();
        for (Map.Entry<String, Voix> v : morceau.getMapVoix().entrySet()) {
            tabVoix.add(v.getValue());
        }

        // Recuperation des noms pour toutes les voix
        ArrayList<String> activated = new ArrayList<>();
        for (Voix v : tabVoix) {
            activated.add(v.getNom());
        }

        System.out.println("Voulez-vous choisir les voix a ne pas afficher? (oui/non) :");
        String str = demandeOuiNon();
        if (str.equals("oui")) {
            boolean fini = false;
            while (!fini) {
                System.out.println("Voici la liste des voix disponibles :" + activated);
                System.out.println("Ecrivez le nom que vous ne voulez pas afficher (fin pour valider) :");
                String s = sc.nextLine();
                while (!activated.contains(s) && !s.equals("fin")) {
                    System.out.println("Veuillez donner un nom present dans le tableau svp :");
                    s = sc.nextLine();
                }
                if (s.equals("fin")) {
                    fini = true;
                } else {
                    activated.remove(s);
                }
            }
        }

        ArrayList<Voix> activatedVoix = new ArrayList<>();
        for (String s : activated) {
            activatedVoix.add(morceau.getVoix(s));
        }
        // Tableau contenant des couleurs
        Color[] couleurs = {Color.BROWN, Color.RED, Color.GRAY, Color.GREEN, Color.YELLOW};

        for (int i = 0; i < activatedVoix.size(); i++) {
            activatedVoix.get(i).setFont(couleurs[i], 20, 100 + i * 40);
        }

        //Group root = new Group();
        for (Voix v : tabVoix) {
            root.getChildren().add(v.getFxText());
        }

        return activatedVoix;
    }

    float choixTempo() {
        float tempo = 0;
        System.out.println("Voulez-vous modifier le tempo? (oui/non) :");
        String str = demandeOuiNon();
        if (str.equals("oui")) {
            System.out.println("Ecrivez la nouvelle valeur :");
            String s = sc.nextLine();
            boolean reussi = false;
            while (!reussi) {
                try {
                    tempo = Float.parseFloat(s.trim());
                    reussi = true;
                } catch (NumberFormatException nfe) {
                    System.err.println("NumberFormatException: " + nfe.getMessage());
                    System.out.println("Veuillez saisir une valeur reelle au bon format:");
                    s = sc.nextLine();
                }
            }
            return tempo;
        } else {
            return 1;
        }

    }

    void parse(AbstractMap.SimpleEntry<String, Message> pair, float tempo, Morceau morceau) {
        Message msgRecu = pair.getValue();
        String musicName = pair.getKey();
        long tailleMidi = msgRecu.getTailleMidi();

        Parser parser = new Parser();
        parser.createMidAndPKST(msgRecu.getBytesTotal(), tailleMidi, musicName);
        String musicFilePath = directoryPath + musicName + "/" + musicName;
        parser.createVoixParoles(morceau, musicFilePath + ".pkst", tempo);

        File midiFile = new File(musicFilePath + ".mid");
        morceau.setFile(midiFile);

    }

    void gestionTechniques() {
        System.out.println("Voulez-vous desactiver les informations sur les techniques vocales? (oui/non) :");
        String str = demandeOuiNon();
        if (str.equals("oui")) {
            Parole.setTechniquesActivées(false);
        }
    }

    ArrayList<KeyFrame> gestionEffets(Group group, Sequencer sequencer) {
        ArrayList<KeyFrame> listKeyFrames = new ArrayList<>();
        float beatPerSecond = (sequencer.getTempoInBPM() * sequencer.getTempoFactor()) / 60;

        System.out.println("Voulez-vous desactiver les animations ? (oui/non) :");
        String str = demandeOuiNon();

        if (str.equals("non")) {
            Circle circle = new Circle(1700, 600, 20, Color.RED);
            group.getChildren().add(circle);

            KeyFrame keyframeOnA = new KeyFrame(
                    Duration.seconds(0),
                    actionEvent -> {
                        circle.setRadius(20);
                    });
            listKeyFrames.add(keyframeOnA);


            KeyFrame keyframeOnB = new KeyFrame(
                    Duration.seconds(1 / beatPerSecond),
                    actionEvent -> {
                        circle.setRadius(40);
                    });
            listKeyFrames.add(keyframeOnB);
        }

        KeyFrame keyframeOffB = new KeyFrame(
                Duration.seconds(2 / beatPerSecond),
                actionEvent -> {
                });
        listKeyFrames.add(keyframeOffB);

        return listKeyFrames;
    }

    private String demandeOuiNon() {
        String str = sc.nextLine();
        while (!str.equals("oui") && !str.equals("non")) {
            System.out.println("Veuillez repondre par oui ou non svp :");
            str = sc.nextLine();
        }
        return str;
    }

    private static boolean checkIfExists(String[] myStringArray, String stringToLocate) {
        for (String element : myStringArray) {
            if (element.equals(stringToLocate)) {
                return true;
            }
        }
        return false;
    }
}