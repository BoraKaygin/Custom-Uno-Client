package com.bolara.uno_client.game;

import com.bolara.uno_client.controller.NetworkController;

public class GameManager {
    private static GameManager instance = null;
    boolean gameStarted = false;
    boolean gameEnded = false;

    private GameManager() {
        // Private constructor to prevent instantiation
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private String gameId;

    public void createSinglePlayerGame() {
        gameId = createGame();
        joinGame(gameId);
        for (int i = 0; i < 3; i++) {
            addComputerPlayer();
        }
        gameStarted = startGame();
    }

    private String createGame() {
        String gameId = NetworkController.createGame();
        if (gameId == null) {
            System.err.println("Failed to create game.");
            return null;
        }
        return gameId;
    }

    private void joinGame(String gameId) {
        boolean joined = NetworkController.joinGame(gameId);
        if (!joined) {
            System.err.println("Failed to join game.");
        }
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
}
