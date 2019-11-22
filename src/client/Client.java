package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client extends Application {

    private HashMap<String, Voix> allVoix = new HashMap<>();
    private ArrayList<Voix> activatedVoix = new ArrayList<>();

    private void serverCommunication(final String serverHost){
        Socket socketOfClient;
        BufferedWriter os;
        BufferedReader is;

        try {

            // Send a request to connect to the server is listening
            // on machine 'localhost' port 9999.
            socketOfClient = new Socket(serverHost, 9999);

            // Create output stream at the client (to send data to the server)
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));


            // Input stream at Client (Receive data from the server).
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }

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

    void createVoix(String chemin) {
        Scanner s = null;
        try {
            s = new Scanner(new File(chemin));
            while (s.hasNextLine()) {
                String ligne = s.nextLine();
                String[] tab = ligne.split(":");
                Voix v = new Voix(tab[0], tab[1], new Text());
                this.allVoix.put(v.getNom(), v);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } finally {
            s.close();
        }
    }

    void createParoles(String chemin) {
        Scanner s = null;
        try {
            s = new Scanner(new File(chemin));
            while (s.hasNextLine()) {
                String ligne = s.nextLine();
                String[] tab = ligne.split(":");
                Voix v = this.allVoix.get(tab[0]);
                v.addParole(new Parole(tab[2], Double.parseDouble(tab[1]), Double.parseDouble(tab[3])));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouve");
        } finally {
            s.close();
        }
    }

    @Override
    public void start(Stage stage) {

        //serverCommunication("localhost");
        createVoix("files/voix.txt");
        createParoles("files/paroles.txt");

        Voix voix1 = this.allVoix.get("Robert");
        Voix voix2 = allVoix.get("Clara");

        voix1.setFont(Color.BROWN, 20,20);
        voix2.setFont(Color.RED, 20,40);

        this.activatedVoix.add(allVoix.get("Robert"));
        this.activatedVoix.add(allVoix.get("Clara"));

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(createKeyFrame(this.activatedVoix));

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

        timeline.play();

    }

    public static void main(String[] args) {

        launch(args);
    }

}