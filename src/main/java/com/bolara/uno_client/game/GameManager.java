package com.bolara.uno_client.game;

import com.bolara.uno_client.controller.NetworkController;
import com.bolara.uno_client.dto.Card;
import com.bolara.uno_client.dto.Game;
import com.bolara.uno_client.dto.PlayerGameView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GameManager {
    private static GameManager instance = null;
    private String gameId = null;
    private int playerIndex = -1;
    private Timer pollingTimer;


    private GameManager() {
        // Private constructor to prevent instantiation
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void resetInstance() {
        stopPolling();
        removeGame();
        instance = null;
    }

    public String getGameId() {
        return gameId;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void createSinglePlayerGame() {
        gameId = createGame();
        assert gameId != null;
        playerIndex = joinGame(gameId);
        assert playerIndex >= 0;
        for (int i = 0; i < 3; i++) {
            addComputerPlayer();
        }
        boolean started = startGame();
        assert started;
    }

    public void createMultiplayerGame() {
        gameId = createGame();
        assert gameId != null;
        playerIndex = joinGame(gameId);
        assert playerIndex >= 0;
    }

    public void joinMultiplayerGame(String gameId) {
        playerIndex = joinGame(gameId);
        assert playerIndex >= 0;
        this.gameId = gameId;
    }

    public void startMultiplayerGame() {
        if (playerIndex != 0) {
            System.err.println("Cannot start game. Player is not host.");
            return;
        }
        boolean started = startGame();
        assert started;
    }

    private String createGame() {
        String gameId = NetworkController.createGame();
        if (gameId == null) {
            System.err.println("Failed to create game.");
            return null;
        }
        return gameId;
    }

    private int joinGame(String gameId) {
        int playerIndex = NetworkController.joinGame(gameId);
        if (playerIndex < 0) {
            System.err.println("Failed to join game.");
        }
        return playerIndex;
    }

    private boolean startGame() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot start game.");
            return false;
        }
        boolean started = NetworkController.startGame(gameId);
        if (!started) {
            System.err.println("Failed to start game.");
            return false;
        }
        return true;
    }

    public void playCard(int playerIndex, int cardIndex, Card.Color declaredColor) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot play card.");
            return;
        }
        boolean played = NetworkController.playCard(gameId, playerIndex, cardIndex, declaredColor);
        if (!played) {
            System.err.println("Failed to play card.");
        }
    }

    public void playCheatCard(int playerIndex, Card card) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot play cheat card.");
            return;
        }
        boolean played = NetworkController.playCheatCard(gameId, playerIndex, card);
        if (!played) {
            System.err.println("Failed to play cheat card.");
        }
    }

    public void setTopCardColor(Card.Color color) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot set top card color.");
            return;
        }
        NetworkController.setTopCardColor(gameId, color);
    }

    public void drawCard(int playerIndex) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot draw card.");
            return;
        }
        boolean drawn = NetworkController.drawCard(gameId, playerIndex);
        if (!drawn) {
            System.err.println("Failed to draw card.");
        }
    }

    private void addComputerPlayer() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot add computer player.");
            return;
        }
        boolean added = NetworkController.addComputerPlayer(gameId);
        if (!added) {
            System.err.println("Failed to add computer player.");
        }
    }

    public Game getGame() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot get game.");
            return null;
        }
        Game game = NetworkController.getGame(gameId);
        if (game == null) {
            System.err.println("Failed to get game.");
            return null;
        }
        return game;
    }

    public PlayerGameView getPlayerGameView() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot get player game view.");
            return null;
        }
        PlayerGameView playerGameView = NetworkController.getPlayerGameView(gameId, playerIndex);
        if (playerGameView == null) {
            System.err.println("Failed to get player game view.");
            return null;
        }
        return playerGameView;
    }

    public void startPollingPlayerView(Consumer<PlayerGameView> onUpdate) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot start polling player view.");
            return;
        }

        if (pollingTimer != null) {
            pollingTimer.cancel();
        }

        pollingTimer = new Timer(true);

        pollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PlayerGameView fetchedView = getPlayerGameView();
                if (fetchedView != null) {
                    onUpdate.accept(fetchedView);
                }
            }
        }, 0, 2000);
    }

    public void startPollingGame(Consumer<Game> onUpdate) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot start polling.");
            return;
        }

        if (pollingTimer != null) {
            pollingTimer.cancel();
        }

        pollingTimer = new Timer(true);

        pollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Game fetchedGame = getGame();
                if (fetchedGame != null) {
                    onUpdate.accept(fetchedGame);
                }
            }
        }, 0, 2000);
    }

    public void stopPolling() {
        if (pollingTimer != null) {
            pollingTimer.cancel();
            pollingTimer = null;
        }
    }

    public void removeGame() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot remove game.");
            return;
        }
        boolean removed = NetworkController.removeGame(gameId);
        if (!removed) {
            System.err.println("Failed to remove game.");
        }
    }
}
