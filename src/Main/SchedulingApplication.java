package Main;

import Data.Paths;

import Models.User;
import Utils.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Creates initial scene/stage and loads main login form
 */
public class SchedulingApplication extends Application {
    // for tracking stage to switch scenes
    public static Stage currentStage;
    public static String lastScene;
    // for referencing throughout use of application
    private static User user;

    /**
     * This function takes a scene path and switches the scene to said path and provides easier use and more readable
     * code for doing so
     * @param scenePath provided path to change scene with
     * @throws IOException in case scene can't be found or generated
     */
    public static void switchScenes(String scenePath) throws IOException {
        Parent newView = FXMLLoader.load(Objects.requireNonNull(SchedulingApplication.class.getResource(scenePath)));
        Scene newScene = new Scene(newView);
        Stage stage = getCurrentStage();
        stage.setScene(newScene);
        stage.show();
    }

    public static void main(String[] args) {
        Database.dbConnect();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Paths.mainLoginPath)));
        Scene scene = new Scene(root);
        currentStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // getters and setters for scene switching and user tracking
    public static Stage getCurrentStage() { return currentStage; }
    public static String getLastScene() { return lastScene; }
    public static void setLastScene(String scene) { lastScene = scene; }
    public static User getUser() { return user; }
    public static void setUser(User user) { SchedulingApplication.user = user; }
}
