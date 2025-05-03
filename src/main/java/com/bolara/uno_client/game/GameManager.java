package com.bolara.uno_client.game;

import com.bolara.uno_client.controller.NetworkController;
import com.bolara.uno_client.dto.Card;
import com.bolara.uno_client.dto.Game;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GameManager {
    private static GameManager instance = null;
    private String gameId;
    private Game game;
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
        instance.stopPolling();
        instance.removeGame();
        instance = null;
    }

    public void createSinglePlayerGame() {
        instance.gameId = createGame();
        boolean joined = joinGame(gameId);
        assert joined;
        for (int i = 0; i < 3; i++) {
            addComputerPlayer();
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

    private boolean joinGame(String gameId) {
        boolean joined = NetworkController.joinGame(gameId);
        if (!joined) {
            System.err.println("Failed to join game.");
        }
        return joined;
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

    public boolean playCard(int playerIndex, int cardIndex, Card.Color declaredColor) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot play card.");
            return false;
        }
        boolean played = NetworkController.playCard(gameId, playerIndex, cardIndex, declaredColor);
        if (!played) {
            System.err.println("Failed to play card.");
        }
        return played;
    }

    public boolean playCheatCard(int playerIndex, Card card) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot play cheat card.");
            return false;
        }
        boolean played = NetworkController.playCheatCard(gameId, playerIndex, card);
        if (!played) {
            System.err.println("Failed to play cheat card.");
        }
        return played;
    }

    public boolean setTopCardColor(Card.Color color) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot set top card color.");
            return false;
        }
        return NetworkController.setTopCardColor(gameId, color);
    }

    public boolean drawCard(int playerIndex) {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot draw card.");
            return false;
        }
        boolean drawn = NetworkController.drawCard(gameId, playerIndex);
        if (!drawn) {
            System.err.println("Failed to draw card.");
        }
        return drawn;
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

    public void startPolling(Consumer<Game> onUpdate) {
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
                    instance.game = fetchedGame;
                    onUpdate.accept(fetchedGame);
                }
            }
        }, 0, 1000); // In milliseconds. Set this to 2000 later
    }

    public void stopPolling() {
        if (pollingTimer != null) {
            pollingTimer.cancel();
            pollingTimer = null;
        }
    }

    public boolean removeGame() {
        if (gameId == null) {
            System.err.println("Game ID is null. Cannot remove game.");
            return false;
        }
        boolean removed = NetworkController.removeGame(gameId);
        if (!removed) {
            System.err.println("Failed to remove game.");
        }
        return removed;
    }
}
