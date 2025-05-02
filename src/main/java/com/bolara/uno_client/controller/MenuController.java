package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void handleStartGame() {
        // TODO: Navigate to actual game scene
        //System.out.println("Start Game clicked");
        StageManager.switchScene(Constants.SCENE_GAME);
    }

    @FXML
    private void handleLeaderboard() {
        StageManager.switchScene(Constants.SCENE_LEADERBOARD);
    }

    @FXML
    private void handleLogout() {
        StageManager.switchScene(Constants.SCENE_LOGIN);
    }
}
