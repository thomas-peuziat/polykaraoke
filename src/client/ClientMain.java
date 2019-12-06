package client;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import message.Message;

import javax.sound.midi.Sequencer;
import java.util.AbstractMap;
import java.util.ArrayList;

public class ClientMain extends Application {

    public void start(Stage stage) {

        Client client = new Client("./files/client/", 9999);
        AbstractMap.SimpleEntry<String, Message> pair = new AbstractMap.SimpleEntry<>(client.serverCommunication("localhost"));

        Morceau morceau = new Morceau();
        float tempo = client.choixTempo();
        System.out.println("Tempo : " + tempo);
        client.parse(pair, tempo, morceau);

        //Creating a Group object
        Group root = new Group();

        ArrayList<Voix> activatedVoix = client.gestionVoix(morceau, root);
        client.gestionTechniques();

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
