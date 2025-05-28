package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.util.Pair;
import javafx.scene.control.DialogPane;


public class MenuUIController {

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleCreateGame() {
        StageManager.switchScene(Constants.SCENE_GAME);
    }

    @FXML
    private void handleLeaderboard() {
        StageManager.switchScene(Constants.SCENE_LEADERBOARD);
    }

    @FXML
    private void handleLogout() {
        Pair<Boolean, String> response = authService.logout();
        statusLabel.setText(response.getValue());
        if (response.getKey() == true) {
            StageManager.switchScene(Constants.SCENE_LOGIN);
        }
    }

    @FXML
    private void openSetReminder() {
        StageManager.switchScene(Constants.SCENE_SET_REMINDER);
    }

    @FXML
    private void handleCreateLobby() {
        System.out.println("Create Lobby button clicked");
        // createMultiplayerGame()
        // GameUIMultiplayerController oluştur ama onda single playerdaki gibi create game yapma
        //
        StageManager.switchScene(Constants.SCENE_LOBBY);

    }

    @FXML
    private void handleJoinLobby() {
        System.out.println("Join Lobby button clicked");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join Lobby");
        dialog.setHeaderText("Enter Lobby ID");
        dialog.setContentText("Lobby ID:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        // Show dialog and wait for input
        dialog.showAndWait().ifPresent(lobbyId -> {
            System.out.println("User entered lobby ID: " + lobbyId);
            // TODO: Call backend to join this lobby, then switch scene
            // joinMultiplayerGame()
            // StageManager.switchScene(Constants.SCENE_LOBBY);

        });
    }


}
