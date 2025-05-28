package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.Hand;
import com.bolara.uno_client.dto.PlayerGameView;
import com.bolara.uno_client.game.GameManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


import java.util.List;

public class LobbyUIController {

    @FXML private Label lobbyCodeLabel;
    @FXML private ListView<String> playerListView;
    @FXML private Button startGameButton;

    private final GameManager gameManager = GameManager.getInstance();

    @FXML
    public void initialize() {
        lobbyCodeLabel.setText(gameManager.getGameId());

        if (gameManager.getPlayerIndex() == 0) {
            startGameButton.setVisible(true); // Only host sees start button
        }

        gameManager.startPollingPlayerView(this::onGameUpdate);
    }

    private void onGameUpdate(PlayerGameView gameView) {
        Platform.runLater(() -> {
            List<PlayerGameView.PlayerInfo> players = gameView.players();
            playerListView.getItems().setAll(players.stream().map(PlayerGameView.PlayerInfo::username).toList());

            if (gameView.state() == Game.GameState.IN_PROGRESS) {
                gameManager.stopPolling();
                StageManager.switchScene(Constants.SCENE_GAME_MULTIPLAYER);
            }
        });
    }

    @FXML
    private void handleStartGame() {
        gameManager.startMultiplayerGame();
    }

    @FXML
    private void handleBackToMenu() {
        gameManager.resetInstance();
        StageManager.switchScene(Constants.SCENE_MENU);
    }
    @FXML
    private void handleCopyLobbyId() {
        String lobbyId = lobbyCodeLabel.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(lobbyId);
        clipboard.setContent(content);
        System.out.println("Lobby ID copied: " + lobbyId);
    }

}
