package com.bolara.uno_client.dto;

import java.util.List;

public record Game(
        List<Hand> players,
        List<Card> drawPile,
        List<Card> discardPile,
        int currentTurn,
        int direction,
        Game.GameState state,
        int drawTwoStack,
        String winnerUsername
) {
    public enum GameState {
        WAITING_FOR_PLAYERS,
        IN_PROGRESS,
        FINISHED
    }

    public Card getTopCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.getLast();
    }
}


