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

/*
@FXML
private void handleCardClick(int cardIndex) {
    int playerIndex = ...; // get this from game state (e.g. your hand is at index 0)
    String declaredColor = null;

    // Only declare color for wild cards
    Card selected = hand.cards().get(cardIndex);
    if (selected.type() == Card.Type.WILD_CHANGE_COLOR || selected.type() == Card.Type.WILD_DRAW_FOUR) {
        // You can show a dialog or hardcode for testing
        declaredColor = "RED"; // or use a color picker
    }

    boolean success = gameManager.playCard(playerIndex, cardIndex, declaredColor);
    if (!success) {
        // show message to user
        System.err.println("Invalid move.");
    }
}

@FXML
private void handleDrawCard() {
    int playerIndex = getYourPlayerIndex(); // this depends on your logic

    boolean success = gameManager.drawCard(playerIndex);
    if (!success) {
        System.err.println("Failed to draw card. Maybe you still have playable cards.");
    }
}

@FXML
private void handleExitGame() {
    boolean success = gameManager.removeGame();
    if (success) {
        System.out.println("Returned to main menu.");
        // TODO: Navigate to main menu scene
    } else {
        System.err.println("Failed to remove game.");
    }
}
*/
