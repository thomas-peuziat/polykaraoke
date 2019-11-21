import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class AffichageParoles extends Application {
    @Override
    public void start(Stage stage) {
        //Creating a Text object
        Text text = new Text();

        //Setting font to the text
        text.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));

        //setting the position of the text
        text.setX(50);
        text.setY(130);

        //Setting the color
        text.setFill(Color.BROWN);

        //Setting the Stroke
        text.setStrokeWidth(2);

        // Setting the stroke color
        text.setStroke(Color.BLUE);

        //Setting the text to be added.
        text.setText("Hi how are you");

        //Creating a Group object
        Group root = new Group(text);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 300);

        //Setting title to the Stage
        stage.setTitle("Setting font to the text");

        //Adding scene to the stage
        stage.setScene(scene);



        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(9.48),
                        actionEvent -> doSomething(text, "I got a feeling")),
                new KeyFrame(
                        Duration.seconds(13.65),
                        actionEvent -> doSomething(text, "That tonight′s gona be a good night")));

        //Displaying the contents of the stage
        stage.show();

        timeline.play();

    }

    private void doSomething(Text text, String newText) {
        text.setText(newText);
    }

    public static void main(String[] args){

        launch(args);

    }
}      