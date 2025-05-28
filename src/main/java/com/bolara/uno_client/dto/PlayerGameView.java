package com.bolara.uno_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerGameView (
        Hand playerHand,
        List<PlayerInfo> players,
        Card topCard,
        int currentTurn,
        int direction,
        Game.GameState state,
        int drawTwoStack,
        String winnerUsername
) {

    public record PlayerInfo (
            String username,
            int cardCount,
            boolean hasCalledUno) {
    }

    public boolean topCardisWild() {
        return topCard != null && topCard.color() == Card.Color.WILD;
    }
} 