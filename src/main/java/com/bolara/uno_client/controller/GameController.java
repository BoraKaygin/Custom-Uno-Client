package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.Hand;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.Card;
import com.bolara.uno_client.dto.Card.Type;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.Random;

public class GameController {

    @FXML private HBox topHand;
    @FXML private VBox leftHand;
    @FXML private VBox rightHand;
    @FXML private HBox playerHand;

    @FXML private ImageView directionArrow;
    @FXML private ImageView topCardImage;
    @FXML private ImageView deckImage;

    @FXML private Label playerUnoLabel;
    @FXML private Label topUnoLabel;
    @FXML private Label leftUnoLabel;
    @FXML private Label rightUnoLabel;

    @FXML private Label playerNameLabel;
    @FXML private Label topNameLabel;
    @FXML private Label leftNameLabel;
    @FXML private Label rightNameLabel;

    @FXML
    public void initialize() {
        // Load arrow and deck image
        Image arrowImg = new Image(getClass().getResource("/assets/arrow.png").toExternalForm());
        Image deck = new Image(getClass().getResource("/assets/deck.png").toExternalForm());
        directionArrow.setImage(arrowImg);
        deckImage.setImage(deck);

        // Create dummy discard pile with some cards
        List<Card> discardPile = List.of(
                new Card(Card.Color.GREEN, Type.SKIP, -1),
                new Card(Card.Color.RED, Type.NUMBER, 6),
                new Card(Card.Color.BLUE, Type.NUMBER, 5) // top card
        );

        // Create dummy hands (player, top, left, right)
        Hand player = new Hand("you", List.of(), true);
        Hand cpu1 = new Hand("bot1", List.of(), true);
        Hand cpu2 = new Hand("bot2", List.of(), true);
        Hand cpu3 = new Hand("bot3", List.of(), true);

        List<Hand> dummyHands = List.of(player, cpu1, cpu2, cpu3);

        // Create a dummy game object to simulate testing
        Game game = new Game(
                dummyHands, // players
                List.of(), // draw pile
                discardPile, // discard pile
                3,         // current turn
                -1,         // or -1 for testing
                Game.GameState.IN_PROGRESS,
                0,   // draw two stack
                null       // winner
        );

        updateDirectionArrow(game.direction());

        for (int i = 0; i < 7; i++) {
            playerHand.getChildren().add(createColoredCard(randomCard(), true));
            topHand.getChildren().add(createColoredCard(randomCard(), false));
            leftHand.getChildren().add(createColoredCard(randomCard(), false));
            rightHand.getChildren().add(createColoredCard(randomCard(), false));

        }
        showTopCard(game);
        updateUnoIndicators(game.players());
        highlightCurrentTurn(game.currentTurn());

    }

    private static <T> T random(T[] array) {
        return array[new Random().nextInt(array.length)];
    }
    private Card randomCard() {
        Card.Type[] types = {
                Type.NUMBER, Type.SKIP, Type.REVERSE, Type.DRAW_TWO,
                Type.WILD_CHANGE_COLOR, Type.WILD_DRAW_FOUR
        };

        Card.Type type = random(types);

        // Wild cards get special WILD color
        Card.Color color = (type == Type.WILD_CHANGE_COLOR || type == Type.WILD_DRAW_FOUR)
                ? Card.Color.WILD
                : random(new Card.Color[]{Card.Color.RED, Card.Color.BLUE, Card.Color.GREEN, Card.Color.YELLOW});

        int number = (type == Type.NUMBER) ? new Random().nextInt(10) : -1;

        return new Card(color, type, number);
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

                if (clickedCard.type() == Card.Type.WILD_CHANGE_COLOR) {
                    promptColorSelection(clickedCard);
                }

            });
        }

        return view;
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatSkip() {
        Card cheatCard = new Card(Card.Color.RED, Type.SKIP, -1);
        simulateCardClick(cheatCard);
    }


    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatReverse() {
        Card cheatCard = new Card(Card.Color.RED, Type.REVERSE, -1);
        simulateCardClick(cheatCard);
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatDrawTwo() {
        Card cheatCard = new Card(Card.Color.RED, Type.DRAW_TWO, -1);
        simulateCardClick(cheatCard);
    }

    @FXML  // here, instead of red, put the game's current color (top card of the discard pile)
    private void handleCheatWild() {
        Card cheatCard = new Card(Card.Color.WILD, Type.WILD_CHANGE_COLOR, -1);
        simulateCardClick(cheatCard);
    }

    @FXML
    private void handleCheatWildDrawFour() {
        Card cheatCard = new Card(Card.Color.WILD, Type.WILD_DRAW_FOUR, -1);
        simulateCardClick(cheatCard);
    }

    private void simulateCardClick(Card card) {
        System.out.println("Cheat played: " + card);

        if (card.color() == Card.Color.WILD && card.type() == Type.WILD_CHANGE_COLOR) {
            promptColorSelection(card);
        } else {
            // Simulate playing the card directly
            System.out.println("Simulated play of: " + card);
        }
    }

    private void promptColorSelection(Card clickedCard) {
        List<String> choices = List.of("RED", "BLUE", "GREEN", "YELLOW");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("RED", choices);
        dialog.setTitle("Choose a Color");
        dialog.setHeaderText("You played a Wild card.");
        dialog.setContentText("Pick a color:");

        dialog.showAndWait().ifPresent(color -> {
            System.out.println("You selected: " + color);

            // TODO: Now proceed with updating game state and sending move to backend (if ready)
            // For now, just simulate card color assignment:
            Card.Color chosenColor = Card.Color.valueOf(color);
            System.out.println("Card played with chosen color: " + chosenColor);
        });
    }

    @FXML
    private void handleDrawCard() {
        System.out.println("Deck clicked!");
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
        topUnoLabel.setVisible(hands.get(1).calledUno());
        leftUnoLabel.setVisible(hands.get(2).calledUno());
        rightUnoLabel.setVisible(hands.get(3).calledUno());
    }

    private String random(String[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    private void showTopCard(Game game) {
        if (game.discardPile().isEmpty()) return;

        Card topCard = game.discardPile().get(game.discardPile().size() - 1);

        String valueImage = switch (topCard.type()) {
            case NUMBER -> "_" + topCard.number() + ".png";
            case SKIP -> "_skip.png";
            case REVERSE -> "_reverse.png";
            case DRAW_TWO -> "_draw2.png";
            case WILD_CHANGE_COLOR -> "wild.png";
            case WILD_DRAW_FOUR -> "wild_draw.png";
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
        System.out.println("Top card image set: " + combined.getWidth() + "x" + combined.getHeight());
        topCardImage.setImage(combined);
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
        StageManager.switchScene(Constants.SCENE_MENU);
    }
}
