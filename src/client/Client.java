package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import message.Message;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client extends Application {
    Scanner sc = new Scanner(System.in);
    private AbstractMap.SimpleEntry<String, Message> serverCommunication(final String serverHost){
        Socket socketOfClient;
        ObjectInputStream in;
        ObjectOutputStream out;
        Message m = null;
        String morceauChoisi = null;
        try {
            socketOfClient = new Socket(serverHost, 9999);

            out = new ObjectOutputStream(socketOfClient.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socketOfClient.getInputStream());

            System.out.println("Client a cree les flux");

            System.out.println(in.readUTF());

            // Permettre à l'utilisateur d'écrire ce qu'il veut, à la place de "medley"
            morceauChoisi = sc.nextLine();
            out.writeUTF(morceauChoisi);
            out.flush();

            Object objetRecu = in.readObject();

            m = (Message) objetRecu;
            System.out.println("Client recoit: " + "Taille :"+ m.getTailleMidi());

            String musicPath = "./files/client/" + morceauChoisi;
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

    private ArrayList<KeyFrame> createKeyFrame(ArrayList<Voix> activatedVoix){
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


    private Sequencer launchMidi(File midiFile, float tempoFactor) {
        Sequencer sequencer = null;
        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setTempoFactor(tempoFactor);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }

        return sequencer;
    }

    @Override
    public void start(Stage stage) {
        AbstractMap.SimpleEntry<String, Message> pair = new AbstractMap.SimpleEntry<>(serverCommunication("localhost"));
        Message msgRecu = pair.getValue();
        String musicName = pair.getKey();
        Morceau morceau = new Morceau();

        long tailleMidi = msgRecu.getTailleMidi();
        Parser parser = new Parser();
        parser.createMidAndPKST(msgRecu.getBytesTotal(), tailleMidi, musicName);
        String musicFilePath = "files/client/" + musicName + "/" + musicName;
        parser.createVoixParoles(morceau, musicFilePath + ".pkst");

        File midiFile = new File(musicFilePath + ".mid");
        morceau.setFile(midiFile);

        // Recuperation de toutes les voix
        ArrayList<Voix> tabVoix = new ArrayList<>();
        for(Map.Entry<String,Voix> v : morceau.getMapVoix().entrySet()){
            tabVoix.add(v.getValue());
        }

        // Recuperation des noms pour toutes les voix
        ArrayList<String> activated = new ArrayList<>();
        for (Voix v : tabVoix) {
            activated.add(v.getNom());
        }

        System.out.println("Voulez-vous choisir les voix a ne pas afficher? (oui/non) :");
        String str = sc.nextLine();
        while (!str.equals("oui") && !str.equals("non")) {
            System.out.println("Veuillez repondre par oui ou non svp :");
            str = sc.nextLine();
        }
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
                if (s.equals("fin") ) {
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

        for (int i=0; i<activatedVoix.size(); i++) {
            activatedVoix.get(i).setFont(couleurs[i], 20, 20 + i*20);
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(createKeyFrame(activatedVoix));

        //Creating a Group object
        Group root = new Group();
        for (Voix v : tabVoix) {
            root.getChildren().add(v.getFxText());
        }
        //Creating a scene object
        Scene scene = new Scene(root, 600, 400);

        //Setting title to the Stage
        stage.setTitle("PolyKaraoke");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        Sequencer sequencer = launchMidi(midiFile, 1);
        timeline.play();

        // TODO Ajouter processus pour mettre en pause

    }

    @Override
    public void stop(){
        System.exit(0);
    }

    public static void main(String[] args) {

        launch(args);
        //serverCommunication("localhost");
    }

}