package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import message.Message;

import javax.sound.midi.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client extends Application {

    private void serverCommunication(final String serverHost){
        Socket socketOfClient;
        BufferedWriter os;
        BufferedReader is;
        /*ObjectInputStream in;
        ObjectOutputStream out;
        Message m = new Message();*/
        try {

            // Send a request to connect to the server is listening
            // on machine 'localhost' port 9999.
            socketOfClient = new Socket(serverHost, 9999);
            // Create output stream at the client (to send data to the server)
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));


            //Input stream at Client (Receive data from the server).
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            /*System.out.println("Lecture 1");
            out = new ObjectOutputStream(new BufferedOutputStream(socketOfClient.getOutputStream()));
            in = new ObjectInputStream(new BufferedInputStream(socketOfClient.getInputStream()));*/



            /*System.out.println("Lecture");
            m = (Message) in.readObject();
            System.out.println("Taille : " + m.getTailleMidi());

            in.close();
            out.close();
            socketOfClient.close();*/

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        } /*catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }*/

        /*try {
            System.out.println("Lecture");
            m = (Message) in.readObject();
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }

        System.out.println("Taille : " + m.getTailleMidi());
        */

        try {

            // Write data to the output stream of the Client Socket.
            os.write("HELO");

            // End of line
            os.newLine();

            // Flush data.
            os.flush();
            os.write("I am Tom Cat");
            os.newLine();
            os.flush();
            os.write("QUIT");
            os.newLine();
            os.flush();

            // Read data sent from the server.
            // By reading the input stream of the Client Socket.
            String responseLine;
            while ((responseLine = is.readLine()) != null) {
                System.out.println("Server: " + responseLine);
                if (responseLine.contains("OK")) {
                    break;
                }
            }

            os.close();
            is.close();
            //in.close();
            //out.close();
            socketOfClient.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
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

        //serverCommunication("localhost");
        //createVoix("files/voix.txt");
        //createParoles("files/paroles.txt");
        Message m2= new Message();
        Morceau morceauTest = new Morceau();

        // Deserialisation du message recu
        ObjectInputStream ois = null;
        try {
            final FileInputStream fichier = new FileInputStream("files/message.ser");
            ois = new ObjectInputStream(fichier);
            m2 = (Message) ois.readObject();
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
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

        //
        long tailleMidi = m2.getTailleMidi();
        Parser parser = new Parser();
        parser.createMidAndPKST(m2.getBytesTotal(), tailleMidi);
        parser.createVoixParoles(morceauTest, "files/texte.pkst");

        //
        /*Text text1 = new Text();
        Text text2 = new Text();

        Voix voix1 = new Voix("Robert", "Chien", text1);
        Voix voix2 = new Voix("Clara", "Femme", text2);

        voix1.addParole(new Parole("waf waf waf", 5.2, 7.2));
        voix1.addParole(new Parole("waf", 8.2, 10.8));

        voix2.addParole(new Parole("Chanter c'est cool", 1.2, 4.0));
        voix2.addParole(new Parole("C'est super de chanter", 10.2, 12));

        voix1.setFont(Color.BROWN, 20,20);
        voix2.setFont(Color.RED, 20,40);

        HashMap<String, Voix> allVoix = new HashMap<>();
        allVoix.put(voix1.getNom(), voix1);
        allVoix.put(voix2.getNom(), voix2);*/
        // ----
        Voix voix1 = morceauTest.getVoix("Robert");
        Voix voix2 = morceauTest.getVoix("Clara");
        voix1.setFont(Color.BROWN, 20,20);
        voix2.setFont(Color.RED, 20,40);

        File midiFile = new File("files/midi.mid");
        //Morceau morceau = new Morceau("titre", allVoix, midiFile);
        morceauTest.setFile(midiFile);

        // TODO Ajouter processus pour choisir les Voix.
        ArrayList<Voix> activatedVoix = new ArrayList<>();
        activatedVoix.add(voix1);
        activatedVoix.add(voix2);
        // ----

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(createKeyFrame(activatedVoix));

        //Creating a Group object
        Group root = new Group(voix1.getFxText(), voix2.getFxText());

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

    public static void main(String[] args) {

        launch(args);
        //serverCommunication("localhost");
    }

}