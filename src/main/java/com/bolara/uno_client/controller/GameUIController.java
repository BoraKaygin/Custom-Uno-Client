package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.Hand;
import com.bolara.uno_client.game.GameManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.bolara.uno_client.dto.Card;
import com.bolara.uno_client.dto.Card.Type;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class GameUIController {

    @FXML
    private HBox topHand;
    @FXML
    private VBox leftHand;
    @FXML
    private VBox rightHand;
    @FXML
    private HBox playerHand;

    @FXML
    private ImageView directionArrow;
    @FXML
    private ImageView topCardImage;
    @FXML
    private ImageView deckImage;

    @FXML
    private Label playerUnoLabel;
    @FXML
    private Label topUnoLabel;
    @FXML
    private Label leftUnoLabel;
    @FXML
    private Label rightUnoLabel;

    @FXML
    private Label playerNameLabel;
    @FXML
    private Label topNameLabel;
    @FXML
    private Label leftNameLabel;
    @FXML
    private Label rightNameLabel;

    @FXML
    private javafx.scene.shape.Rectangle currentColorBox;

    private GameManager gameManager;

    private int playerIndex = 0;

    private boolean promptUp = false;

    @FXML
    public void initialize() {
        // Load arrow and deck image
        Image arrowImg = new Image(getClass().getResource("/assets/arrow.png").toExternalForm());
        Image deck = new Image(getClass().getResource("/assets/deck.png").toExternalForm());
        directionArrow.setImage(arrowImg);
        deckImage.setImage(deck);

        gameManager = GameManager.getInstance();
        gameManager.createSinglePlayerGame();
        gameManager.startPollingGame(this::onUpdate);
        Game game = gameManager.getGame();
        playerIndex = gameManager.getPlayerIndex();

        Hand player = game.players().get(0);
        for (int i = 0; i < player.cards().size(); i++) {
            playerHand.getChildren().add(createColoredCard(player.cards().get(i), true));
        }
        Hand top = game.players().get(2);
        for (int i = 0; i < top.cards().size(); i++) {
            topHand.getChildren().add(createColoredCard(top.cards().get(i), false));
        }
        Hand left = game.players().get(1);
        for (int i = 0; i < left.cards().size(); i++) {
            leftHand.getChildren().add(createColoredCard(left.cards().get(i), false));
        }
        Hand right = game.players().get(3);
        for (int i = 0; i < right.cards().size(); i++) {
            rightHand.getChildren().add(createColoredCard(right.cards().get(i), false));
        }


        updateDirectionArrow(game.direction());
        showTopCard(game);
        updateUnoIndicators(game.players());
        highlightCurrentTurn(game.currentTurn());

    }

    private void onUpdate(Game game) {
        Platform.runLater(() -> {
            if (game.topCardisWild() && !promptUp) {
                promptUp = true;
                Card topCard = game.discardPile().getLast();
                topCard = promptColorSelection(topCard);
                gameManager.setTopCardColor(topCard.color());
            }
            if (!game.topCardisWild()) {
                promptUp = false;
            }
            if (game.state() == Game.GameState.FINISHED) {
                gameManager.stopPolling();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Over");
                    alert.setHeaderText("🎉 Game Finished!");
                    String winner = game.winnerUsername() != null ? game.winnerUsername() : "No one";
                    alert.setContentText("Winner: " + winner);

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

                    dialogPane.getStyleClass().add("custom-alert");
                    alert.showAndWait();
                    //gameManager.resetInstance(); // it removes the game object, maybe we should save the

                    // Optionally, go back to menu after closing
                    // StageManager.switchScene(Constants.SCENE_MENU);
                });
                return; // Prevent updating hands after game ends
            }

            updateDirectionArrow(game.direction());
            showTopCard(game);
            updateUnoIndicators(game.players());
            highlightCurrentTurn(game.currentTurn());

            // Clear existing cards
            playerHand.getChildren().clear();
            topHand.getChildren().clear();
            leftHand.getChildren().clear();
            rightHand.getChildren().clear();

            // Add updated cards
            Hand player = game.players().get(0);
            for (Card card : player.cards()) {
                playerHand.getChildren().add(createColoredCard(card, true));
            }

            Hand top = game.players().get(2);
            for (Card card : top.cards()) {
                topHand.getChildren().add(createColoredCard(card, false));
            }

            Hand left = game.players().get(1);
            for (Card card : left.cards()) {
                leftHand.getChildren().add(createColoredCard(card, false));
            }

            Hand right = game.players().get(3);
            for (Card card : right.cards()) {
                rightHand.getChildren().add(createColoredCard(card, false));
            }
            adjustHorizontalSpacing(playerHand, player.cards());
            adjustHorizontalSpacing(topHand, top.cards());

            adjustVerticalSpacing(leftHand, left.cards());
            adjustVerticalSpacing(rightHand, right.cards());

        });
    }

    private void adjustHorizontalSpacing(HBox handBox, List<Card> cards) {
        int cardCount = cards.size();
        double maxSpacing = 10;
        double minSpacing = -10;

        double spacing = Math.max(minSpacing, maxSpacing - cardCount);
        handBox.setSpacing(spacing);
    }
    private void adjustVerticalSpacing(VBox handBox, List<Card> cards) {
        int cardCount = cards.size();

        double spacing;
        if (cardCount < 8) {
            // For small hands, more separation (e.g. -25 to -35)
            spacing = -30;  // e.g. 7 cards → -32
        } else if (cardCount < 10) {
            spacing = -38;
        } else if (cardCount < 15) {
            // For larger hands, tighter spacing
            spacing = -48;
        } else if (cardCount < 17) {
            spacing = -55;
        } else {
            spacing = -56;
        }
        handBox.setSpacing(spacing);
    }




    private static <T> T random(T[] array) {
        return array[new Random().nextInt(array.length)];
    }

    private ImageView createColoredCard(Card card, boolean isPlayerCard) {
        String valueImage = switch (card.type()) {
            case NUMBER -> "_" + card.number() + ".png";
            case SKIP -> "_skip.png";
            case REVERSE -> "_reverse.png";
            case DRAW_TWO -> "_draw2.png";
            case WILD_CHANGE_COLOR -> "_wild.png";
            case WILD_DRAW_FOUR -> "_wild_draw.png";
        };

        String baseImage = switch (card.color()) {
            case RED -> "red_base.png";
            case BLUE -> "blue_base.png";
            case GREEN -> "green_base.png";
            case YELLOW -> "yellow_base.png";
            case WILD -> null;
        };

        Image image = (baseImage != null)
                ? combineCardLayers(valueImage, baseImage)
                : new Image(getClass().getResource("/assets/" + valueImage).toExternalForm());

        ImageView view = new ImageView(image);
        view.setFitWidth(50);
        view.setPreserveRatio(true);

        // Add random twist
        view.setRotate(new Random().nextDouble(-5, 5));

        // Store card info
        view.setUserData(card);

        // Add click handler if this card belongs to the player
        if (isPlayerCard) {
            view.setOnMouseClicked(e -> {
                Card clickedCard = (Card) view.getUserData();
                System.out.println("Card clicked: " + clickedCard);
                int index = getCardIndex(clickedCard);
                System.out.println("Card index: " + index);

                if (clickedCard.color() == Card.Color.WILD) {
                    clickedCard = promptColorSelection(clickedCard);
                }
                gameManager.playCard(playerIndex, index, clickedCard.color());
            });
        }

        return view;
    }

    public int getCardIndex(Card card) {
        for (int i = 0; i < playerHand.getChildren().size(); i++) {
            ImageView view = (ImageView) playerHand.getChildren().get(i);
            if (view.getUserData().equals(card)) {
                return i;
            }
        }
        return -1; // Not found
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatSkip() {
        Card.Color color = Objects.requireNonNull(gameManager.getGame().getTopCard()).color();
        Card cheatCard = new Card(color, Type.SKIP, -1);
        gameManager.playCheatCard(playerIndex, cheatCard);
    }


    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatReverse() {
        Card.Color color = Objects.requireNonNull(gameManager.getGame().getTopCard()).color();
        Card cheatCard = new Card(color, Type.REVERSE, -1);
        gameManager.playCheatCard(playerIndex, cheatCard);
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatDrawTwo() {
        Card.Color color = Objects.requireNonNull(gameManager.getGame().getTopCard()).color();
        Card cheatCard = new Card(color, Type.DRAW_TWO, -1);
        gameManager.playCheatCard(playerIndex, cheatCard);
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatWild() {
        ;
        Card cheatCard = new Card(Card.Color.WILD, Type.WILD_CHANGE_COLOR, -1);
        cheatCard = promptColorSelection(cheatCard);
        gameManager.playCheatCard(playerIndex, cheatCard);
    }

    @FXML
    private void handleCheatWildDrawFour() {
        Card cheatCard = new Card(Card.Color.WILD, Type.WILD_DRAW_FOUR, -1);
        cheatCard = promptColorSelection(cheatCard);
        gameManager.playCheatCard(playerIndex, cheatCard);
    }

//    private void simulateCardClick(Card card) {
//        System.out.println("Cheat played: " + card);
//
//        if (card.color() == Card.Color.WILD) {
//            promptColorSelection(card);
//        } else {
//            // Simulate playing the card directly
//            System.out.println("Simulated play of: " + card);
//        }
//    }

    private Card promptColorSelection(Card clickedCard) {
        List<Card.Color> choices = List.of(Card.Color.RED, Card.Color.BLUE, Card.Color.GREEN, Card.Color.YELLOW);
        ChoiceDialog<Card.Color> dialog = new ChoiceDialog<>(Card.Color.RED, choices);
        dialog.setTitle("Choose a Color");
        dialog.setHeaderText("You played a Wild card.");
        dialog.setContentText("Pick a color:");
        AtomicReference<Card> result = new AtomicReference<>();

        dialog.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        dialog.showAndWait().ifPresent(color -> {
            System.out.println("You selected: " + color);
            result.set(new Card(color, clickedCard.type(), -1));
            System.out.println("Card played with chosen color: " + color);
        });
        return result.get();
    }

    @FXML
    private void handleDrawCard() {
        System.out.println("Deck clicked!");
        gameManager.drawCard(playerIndex);
    }

    private void highlightCurrentTurn(int currentTurn) {
        // Clear all highlights first
        playerNameLabel.getStyleClass().remove("highlight-turn");
        topNameLabel.getStyleClass().remove("highlight-turn");
        leftNameLabel.getStyleClass().remove("highlight-turn");
        rightNameLabel.getStyleClass().remove("highlight-turn");

        // Add highlight to current player
        switch (currentTurn) {
            case 0 -> playerNameLabel.getStyleClass().add("highlight-turn");
            case 1 -> leftNameLabel.getStyleClass().add("highlight-turn");
            case 2 -> topNameLabel.getStyleClass().add("highlight-turn");
            case 3 -> rightNameLabel.getStyleClass().add("highlight-turn");
        }
    }


    private void updateDirectionArrow(int direction) {
        if (direction == -1) {
            directionArrow.setScaleX(-1); // Reverse direction
        } else {
            directionArrow.setScaleX(1); // Normal direction
        }
    }

    private void updateUnoIndicators(List<Hand> hands) {
        if (hands.size() < 4) return; // Safety

        playerUnoLabel.setVisible(hands.get(0).calledUno());
        leftUnoLabel.setVisible(hands.get(1).calledUno());
        topUnoLabel.setVisible(hands.get(2).calledUno());
        rightUnoLabel.setVisible(hands.get(3).calledUno());
    }

    private void showTopCard(Game game) {
        if (game.discardPile().isEmpty()) return;

        Card topCard = game.discardPile().get(game.discardPile().size() - 1);

        String valueImage = switch (topCard.type()) {
            case NUMBER -> "_" + topCard.number() + ".png";
            case SKIP -> "_skip.png";
            case REVERSE -> "_reverse.png";
            case DRAW_TWO -> "_draw2.png";
            case WILD_CHANGE_COLOR -> "_wild.png";
            case WILD_DRAW_FOUR -> "_wild_draw.png";
        };

        String baseImage = switch (topCard.color()) {
            case RED -> "red_base.png";
            case BLUE -> "blue_base.png";
            case GREEN -> "green_base.png";
            case YELLOW -> "yellow_base.png";
            case WILD -> null; // wild cards don't need base
        };

        Image combined;
        if (baseImage != null) {
            // Build combined base + value image
            combined = combineCardLayers(valueImage, baseImage);
        } else {
            // Use value image directly (wild cards)
            combined = new Image(getClass().getResource("/assets/" + valueImage).toExternalForm());
        }

        if (combined == null) {
            System.err.println("⚠️ Failed to combine top card image");
        }
        //System.out.println("Top card image set: " + combined.getWidth() + "x" + combined.getHeight());
        topCardImage.setImage(combined);

        // Set color box fill
        Color fxColor = switch (topCard.color()) {
            case RED -> Color.RED;
            case BLUE -> Color.DODGERBLUE;
            case GREEN -> Color.GREEN;
            case YELLOW -> Color.GOLD;
            case WILD -> Color.GRAY;
        };
        currentColorBox.setFill(fxColor);
    }

    private Image combineCardLayers(String valueImage, String baseImage) {
        URL baseUrl = getClass().getResource("/assets/" + baseImage);
        URL valueUrl = getClass().getResource("/assets/" + valueImage);

        if (baseUrl == null || valueUrl == null) {
            System.err.println("Missing asset: " + baseImage + " or " + valueImage);
            return null;
        }

        Image base = new Image(baseUrl.toExternalForm());
        Image value = new Image(valueUrl.toExternalForm());

        Canvas canvas = new Canvas(base.getWidth(), base.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(base, 0, 0);
        gc.drawImage(value, 0, 0);

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        return canvas.snapshot(sp, null);
    }

    @FXML
    private void handleBackToMenu() {
        gameManager.resetInstance();
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

{
            System.out.println("\n========== GAME STATE ==========");

            System.out.println("State         : " + game.state());
            System.out.println("Current Turn  : " + game.players().get(game.currentTurn()).username());
            System.out.println("Direction     : " + (game.direction() == 1 ? "Clockwise" : "Counter-clockwise"));
            System.out.println("Draw2 Stack   : " + game.drawTwoStack());
            System.out.println("Winner        : " + (game.winnerUsername() != null ? game.winnerUsername() : "None"));

            System.out.println("\n--- Player Hands ---");
            for (int i = 0; i < game.players().size(); i++) {
                var hand = game.players().get(i);
                String playerLabel = (i == game.currentTurn() ? "👉 " : "   ") + hand.username();
                System.out.println(playerLabel + " (" + hand.cards().size() + " cards):");

                for (Card c : hand.cards()) {
                    System.out.printf("   - %-6s %-20s %s\n", c.color(), c.type(), (c.number() >= 0 ? c.number() : ""));
                }

                System.out.println();
            }

            System.out.println("--- Top Discard Card ---");
            if (!game.discardPile().isEmpty()) {
                Card top = game.discardPile().get(game.discardPile().size() - 1);
                System.out.printf("Color: %-6s Type: %-20s Number: %s\n", top.color(), top.type(), top.number() >= 0 ? top.number() : "");
            }

            System.out.println("========== END STATE ==========\n");
        }
*/
