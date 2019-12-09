package client;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import message.Message;

import javax.sound.midi.Sequencer;
import java.util.AbstractMap;
import java.util.ArrayList;

public class ClientMain extends Application {

    public void start(Stage stage) {

        Client client = new Client("./files/client/", 9999);
        AbstractMap.SimpleEntry<String, Message> pair = new AbstractMap.SimpleEntry<>(client.serverCommunication());

        Morceau morceau = new Morceau();
        float tempo = client.choixTempo();
        System.out.println("Tempo : " + tempo);
        client.parse(pair, tempo, morceau);

        //Creating a Group object
        Group root = new Group();

        ArrayList<Voix> activatedVoix = client.gestionVoix(morceau, root);
        client.gestionTechniques();

        Timeline timelineLyrics = new Timeline();
        timelineLyrics.getKeyFrames().addAll(client.createKeyFrame(activatedVoix));

        //Creating a scene object
        Scene scene = new Scene(root, 1900, 800, Color.web("#bdc3c7"));

        Sequencer sequencer = client.initSequencer(morceau.getMidiFile(), tempo);

        Timeline timelineanimation = new Timeline();
        timelineanimation.getKeyFrames().addAll(client.gestionEffets(root, sequencer));
        timelineanimation.setCycleCount(Animation.INDEFINITE);

        //Setting title to the Stage
        stage.setTitle("PolyKaraoke");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

        timelineLyrics.play();
        timelineanimation.play();
        sequencer.start();


    }

    @Override
    public void stop() {
        System.exit(0);
    }


    public static void main(String[] args) {

        launch(args);
    }
}
