package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import javafx.fxml.FXML;

public class GameController {

    @FXML
    private void handleBackToMenu() {
        StageManager.switchScene(Constants.SCENE_MENU);
    }
}
