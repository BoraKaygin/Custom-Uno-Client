package com.bolara.uno_client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LobbyUIController {

    @FXML
    private Text lobbyCodeText;

    @FXML
    private ListView<String> playerListView;

    @FXML
    public void initialize() {
        // Placeholder for now
        lobbyCodeText.setText("Lobby Code: ABC123");
        playerListView.getItems().add("Waiting for players...");
    }

    @FXML
    private void onStartGameClicked() {
        // Will be implemented once lobby logic is done
        System.out.println("Start Game clicked.");
    }

    @FXML
    private void onBackToMenuClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) playerListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
