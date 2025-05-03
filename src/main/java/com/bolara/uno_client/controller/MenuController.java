package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Pair;

public class MenuController {

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
}
