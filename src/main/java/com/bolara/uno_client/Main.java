package com.bolara.uno_client;

import com.bolara.uno_client.config.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.stage = primaryStage;
        primaryStage.setTitle("UNO Game");

        StageManager.switchScene(Constants.SCENE_LOGIN);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
