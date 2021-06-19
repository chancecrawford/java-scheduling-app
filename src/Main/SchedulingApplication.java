package Main;

import Data.Paths;

import Utils.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Creates initial scene/stage and loads main form
 */
public class SchedulingApplication extends Application {

    public static Stage currentStage;

    public static void switchScenes(String scenePath) throws IOException {
        Parent newView = FXMLLoader.load(Objects.requireNonNull(SchedulingApplication.class.getResource(scenePath)));
        Scene newScene = new Scene(newView);
        Stage stage = getCurrentStage();
        stage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Database.dbConnect();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Paths.mainLoginPath)));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getCurrentStage() { return currentStage; }
}
