package com.bolara.uno_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(
        List<Hand> players,
        List<Card> drawPile,
        List<Card> discardPile,
        int currentTurn,
        int direction,
        Game.GameState state,
        int drawTwoStack,
        int hasPlayedDrawFour,
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

    public boolean topCardisWild() {
        return getTopCard() != null && getTopCard().color() == Card.Color.WILD;
    }
}
