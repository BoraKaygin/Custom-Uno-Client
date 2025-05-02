package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;

public class GameController {

    @FXML private HBox topHand;
    @FXML private VBox leftHand;
    @FXML private VBox rightHand;
    @FXML private HBox playerHand;


    @FXML
    public void initialize() {
        StageManager.root.applyCss();
        StageManager.root.layout();
        StageManager.stage.setOnShown(e -> {
            StageManager.root.applyCss();
            StageManager.root.layout();
        });

        String[] valueImages = {
                "_0.png", "_1.png", "_2.png", "_3.png", "_4.png", "_5.png", "_6.png", "_7.png", "_8.png", "_9.png",
                "_reverse.png", "_skip.png", "_draw2.png"
        };

        String[] baseColors = {
                "red_base.png", "blue_base.png", "green_base.png", "yellow_base.png"
        };

        for (int i = 0; i < 7; i++) {
            playerHand.getChildren().add(createColoredCard(random(valueImages), random(baseColors)));
            topHand.getChildren().add(createColoredCard(random(valueImages), random(baseColors)));
            leftHand.getChildren().add(createColoredCard(random(valueImages), random(baseColors)));
            rightHand.getChildren().add(createColoredCard(random(valueImages), random(baseColors)));
        }

        Platform.runLater(() -> {
            StageManager.root.applyCss();
            StageManager.root.layout();
        });
        StageManager.root.applyCss();
        StageManager.root.layout();
    }

    private String random(String[] array) {
        return array[(int) (Math.random() * array.length)];
    }


    private ImageView createColoredCard(String valueImage, String baseImage) {
        URL baseUrl = getClass().getResource("/assets/" + baseImage);
        URL valueUrl = getClass().getResource("/assets/" + valueImage);

        if (baseUrl == null || valueUrl == null) {
            System.err.println("Missing asset: " + baseImage + " or " + valueImage);
            return new ImageView();
        }

        Image base = new Image(baseUrl.toExternalForm());
        Image value = new Image(valueUrl.toExternalForm());

        Canvas canvas = new Canvas(base.getWidth(), base.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(base, 0, 0);
        gc.drawImage(value, 0, 0);

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        Image combined = canvas.snapshot(sp, null);

        ImageView imageView = new ImageView(combined);
        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);
        imageView.setRotate((Math.random() - 0.5) * 10); // random tilt
        return imageView;
    }

    @FXML
    private void handleBackToMenu() {
        StageManager.switchScene(Constants.SCENE_MENU);
    }
}