package com.bolara.uno_client.dto;

public record Card(Card.Color color, Card.Type type, int number) {
    public enum Color { RED, BLUE, GREEN, YELLOW, WILD };
    public enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD_CHANGE_COLOR, WILD_DRAW_FOUR }
}