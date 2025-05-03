package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.game.GameManager;
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
        // TODO: Navigate to actual game scene
        System.out.println("Start Game clicked");
        GameManager.resetInstance();
        GameManager gameManager = GameManager.getInstance();
        gameManager.createSinglePlayerGame();
        // TODO: Move to GameController
        gameManager.startPolling(game -> {
            System.out.println("----- Game State Polled -----");
            System.out.println(game);
        });
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
