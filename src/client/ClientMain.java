package client;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import message.Message;

import javax.sound.midi.Sequencer;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientMain extends Application {
    //Scanner sc = new Scanner(System.in);
    Client client = new Client();
    public void start(Stage stage) {
        AbstractMap.SimpleEntry<String, Message> pair = new AbstractMap.SimpleEntry<>(client.serverCommunication("localhost"));

        Morceau morceau = new Morceau();
        float tempo = client.choixTempo();

        client.parse(pair, tempo, morceau);

        //Creating a Group object
        Group root = new Group();

        ArrayList<Voix> activatedVoix = client.gestionVoix(morceau, root);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(client.createKeyFrame(activatedVoix));

        //Creating a scene object
        Scene scene = new Scene(root, 800, 400);

        //Setting title to the Stage
        stage.setTitle("PolyKaraoke");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        Sequencer sequencer = client.launchMidi(morceau.getMidiFile(), tempo);
        timeline.play();

    }

    @Override
    public void stop(){
        System.exit(0);
    }


    public static void main(String[] args) {

        launch(args);
    }
}
