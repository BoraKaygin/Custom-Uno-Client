package com.bolara.uno_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Card(Card.Color color, Card.Type type, int number) {
    public enum Color { RED, BLUE, GREEN, YELLOW, WILD }
    public enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD_CHANGE_COLOR, WILD_DRAW_FOUR }
}