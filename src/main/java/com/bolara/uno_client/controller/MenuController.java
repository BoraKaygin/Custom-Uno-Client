package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.CreateGameResponse;
import com.bolara.uno_client.game.GameManager;
import com.bolara.uno_client.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.bolara.uno_client.controller.NetworkController.joinGame;

public class MenuController {

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private String gameId = null;

    @FXML
    private void handleCreateGame() {
        // TODO: Navigate to actual game scene
        System.out.println("Start Game clicked");
        GameManager gameManager = GameManager.getInstance();
        gameManager.createSinglePlayerGame();
    }

    @FXML
    private void handleLeaderboard() {
        StageManager.switchScene(Constants.SCENE_LEADERBOARD);
    }

    @FXML
    private void handleLogout() {
        Pair<Boolean,String> response = authService.logout();
        statusLabel.setText(response.getValue());
        if (response.getKey() == true) {
            StageManager.switchScene(Constants.SCENE_LOGIN);
        }
    }
}
