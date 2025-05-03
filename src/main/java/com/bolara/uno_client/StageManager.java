package com.bolara.uno_client;

import com.bolara.uno_client.config.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageManager {

    public static Stage stage;
    public static FXMLLoader loader;
    public static Parent root;
    public static Scene scene;

    public static void switchScene(String fxmlPath) {
        try {
            loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));
            root = loader.load();
            scene = new Scene(root); //, Constants.WindowWidth, Constants.WindowHeight);
            scene.getStylesheets().add(StageManager.class.getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + fxmlPath, e);
        }
    }
}
