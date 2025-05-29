package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.Hand;
import com.bolara.uno_client.dto.PlayerGameView;
import com.bolara.uno_client.game.GameManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.bolara.uno_client.dto.Card;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class GameUIMultiplayerController {

    @FXML
    private HBox topHand;
    @FXML
    private VBox leftHand;
    @FXML
    private VBox rightHand;
    @FXML
    private HBox bottomHand;

    @FXML
    private ImageView directionArrow;
    @FXML
    private ImageView topCardImage;
    @FXML
    private ImageView deckImage;

    @FXML
    private Label bottomUnoLabel;
    @FXML
    private Label topUnoLabel;
    @FXML
    private Label leftUnoLabel;
    @FXML
    private Label rightUnoLabel;

    @FXML
    private Label bottomNameLabel;
    @FXML
    private Label topNameLabel;
    @FXML
    private Label leftNameLabel;
    @FXML
    private Label rightNameLabel;

    @FXML private Button bottomCallUnoButton;
    @FXML private Button leftCallUnoButton;
    @FXML private Button topCallUnoButton;
    @FXML private Button rightCallUnoButton;

    @FXML private Button bottomChallengeButton;
    @FXML private Button leftChallengeButton;
    @FXML private Button topChallengeButton;
    @FXML private Button rightChallengeButton;


    @FXML
    private javafx.scene.shape.Rectangle currentColorBox;

    private GameManager gameManager;

    private int playerIndex = -1;

    private boolean promptUp = false;
    private boolean challengeActive = true;  // Tracks if challenge is still valid


    @FXML
    public void initialize() {
        // Load arrow and deck image
        Image arrowImg = new Image(getClass().getResource("/assets/arrow.png").toExternalForm());
        Image deck = new Image(getClass().getResource("/assets/deck.png").toExternalForm());
        Image backCard = new Image(getClass().getResource("/assets/back.png").toExternalForm());
        directionArrow.setImage(arrowImg);
        deckImage.setImage(deck);

        gameManager = GameManager.getInstance();
        gameManager.startPollingPlayerView(this::onUpdate);
        PlayerGameView gameView = gameManager.getPlayerGameView();
        playerIndex = gameManager.getPlayerIndex();

        List<PlayerGameView.PlayerInfo> players = gameView.players();
        int playerCount = players.size();

        bottomNameLabel.setText(players.getFirst().username());
        leftNameLabel.setText(players.get(1).username());
        if (playerCount >= 3) {
            topNameLabel.setText(players.get(2).username());
        }
        if (playerCount == 4) {
            rightNameLabel.setText(players.get(3).username());
        }

        if (players.size() < 3) {
            topHand.setVisible(false);
            topNameLabel.setVisible(false);
            topUnoLabel.setVisible(false);
        }
        if (players.size() < 4) {
            rightHand.setVisible(false);
            rightNameLabel.setVisible(false);
            rightUnoLabel.setVisible(false);
        }
    }

    private void onUpdate(PlayerGameView gameView) {
        Platform.runLater(() -> {
            if (gameView.topCardisWild() && !promptUp) {
                promptUp = true;
                Card topCard = gameView.topCard();
                topCard = promptColorSelection(topCard);
                gameManager.setTopCardColor(topCard.color());
            }
            if (!gameView.topCardisWild()) {
                promptUp = false;
            }
            if (gameView.state() == Game.GameState.FINISHED) {
                gameManager.stopPolling();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Over");
                    alert.setHeaderText("🎉 Game Finished!");
                    String winner = gameView.winnerUsername() != null ? gameView.winnerUsername() : "No one";
                    alert.setContentText("Winner: " + winner);

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

                    dialogPane.getStyleClass().add("custom-alert");
                    alert.showAndWait();
                });
                return; // Prevent updating hands after game ends
            }

            updateDirectionArrow(gameView.direction());
            showTopCard(gameView);
            updateUnoIndicators(gameView.players());
            highlightCurrentTurn(gameView.currentTurn());

            // Clear existing cards
            bottomHand.getChildren().clear();
            topHand.getChildren().clear();
            leftHand.getChildren().clear();
            rightHand.getChildren().clear();

            int playerCount = gameView.players().size();
            setHand(bottomHand, 0, gameView);
            setHand(leftHand, 1, gameView);
            if (playerCount >= 3){
                setHand(topHand, 2, gameView);
            }
            if (playerCount == 4) {
                setHand(rightHand, 3, gameView);
            }


            adjustHorizontalSpacing(bottomHand);
            adjustHorizontalSpacing(topHand);
            adjustVerticalSpacing(leftHand);
            adjustVerticalSpacing(rightHand);

            List<PlayerGameView.PlayerInfo> players = gameView.players();

            for (int i = 0; i < players.size(); i++) {
                PlayerGameView.PlayerInfo player = players.get(i);

                boolean isLocal = (i == playerIndex);
                boolean showUno = isLocal && !player.hasCalledUno();

                switch (i) {
                    case 0 -> bottomCallUnoButton.setVisible(showUno);
                    case 1 -> leftCallUnoButton.setVisible(showUno);
                    case 2 -> topCallUnoButton.setVisible(showUno);
                    case 3 -> rightCallUnoButton.setVisible(showUno);
                }
            }

            Card topCard = gameView.topCard();
            boolean isWildDrawFour = topCard != null && topCard.type() == Card.Type.WILD_DRAW_FOUR;

            // Only show challenge button if it's this player's turn, top card is Wild Draw Four,
            // and challenge hasn't been used yet
            if (isWildDrawFour && gameView.currentTurn() == playerIndex && challengeActive) {
                showOnlyLocalPlayerChallengeButton();
            } else {
                hideAllChallengeButtons();
            }


        });



    }

    private void setHand(Pane hand, int handNo, PlayerGameView gameView) {
        if (handNo == playerIndex) {
            Hand player = gameView.playerHand();
            for (Card card : player.cards()) {
                hand.getChildren().add(createColoredCard(card, hand));
            }
        } else {
            int handCount = gameView.players().get(handNo).cardCount();
            for (int i = 0; i < handCount; i++) {
                hand.getChildren().add(createBackCard());
            }
        }
    }

    private void adjustHorizontalSpacing(HBox handBox) {
        int cardCount = handBox.getChildren().size();
        double maxSpacing = 10;
        double minSpacing = -10;

        double spacing = Math.max(minSpacing, maxSpacing - cardCount);
        handBox.setSpacing(spacing);
    }
    private void adjustVerticalSpacing(VBox handBox) {
        int cardCount = handBox.getChildren().size();
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


    private ImageView createBackCard() {
        Image backImage = new Image(getClass().getResource("/assets/back.png").toExternalForm());
        ImageView view = new ImageView(backImage);
        view.setFitWidth(50);
        view.setPreserveRatio(true);
        view.setRotate(new Random().nextDouble(-5, 5)); // adds some realism
        return view;
    };

    private ImageView createColoredCard(Card card, Pane playerPane) {
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

        // Add click handler
        view.setOnMouseClicked(e -> {
            Card clickedCard = (Card) view.getUserData();
            System.out.println("Card clicked: " + clickedCard);
            int index = getCardIndex(clickedCard, playerPane);
            System.out.println("Card index: " + index);

            if (clickedCard.color() == Card.Color.WILD) {
                clickedCard = promptColorSelection(clickedCard);
            }
            gameManager.playCard(playerIndex, index, clickedCard.color());
        });

        return view;
    }

    public int getCardIndex(Card card, Pane playerPane) {
        for (int i = 0; i < playerPane.getChildren().size(); i++) {
            ImageView view = (ImageView) playerPane.getChildren().get(i);
            if (view.getUserData().equals(card)) {
                return i;
            }
        }
        return -1; // Not found
    }

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
        bottomNameLabel.getStyleClass().remove("highlight-turn");
        topNameLabel.getStyleClass().remove("highlight-turn");
        leftNameLabel.getStyleClass().remove("highlight-turn");
        rightNameLabel.getStyleClass().remove("highlight-turn");

        // Add highlight to current player
        switch (currentTurn) {
            case 0 -> bottomNameLabel.getStyleClass().add("highlight-turn");
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

    private void updateUnoIndicators(List<PlayerGameView.PlayerInfo> hands) {
        bottomUnoLabel.setVisible(hands.get(0).hasCalledUno());
        leftUnoLabel.setVisible(hands.get(1).hasCalledUno());
        if (hands.size() >= 3) {
            topUnoLabel.setVisible(hands.get(2).hasCalledUno());
        }
        if (hands.size() == 4) {
            rightUnoLabel.setVisible(hands.get(3).hasCalledUno());
        }
    }

    private void showTopCard(PlayerGameView gameView) {
        if (gameView.topCard() == null) return;

        Card topCard = gameView.topCard();

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

    @FXML
    private void handleCallUno() {
        gameManager.callUno(playerIndex);  // assumes GameManager handles the REST call
    }

    @FXML
    private void handleChallengeDrawFour() {
        challengeActive = false;
        gameManager.challengeDrawFour(playerIndex);  // notify backend
        hideAllChallengeButtons();                   // hide after clicking
    }

    private void showOnlyLocalPlayerChallengeButton() {
        switch (playerIndex) {
            case 0 -> bottomChallengeButton.setVisible(true);
            case 1 -> leftChallengeButton.setVisible(true);
            case 2 -> topChallengeButton.setVisible(true);
            case 3 -> rightChallengeButton.setVisible(true);
        }
    }

    private void hideAllChallengeButtons() {
        bottomChallengeButton.setVisible(false);
        leftChallengeButton.setVisible(false);
        topChallengeButton.setVisible(false);
        rightChallengeButton.setVisible(false);
    }
}
