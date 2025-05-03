package com.bolara.uno_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Card(Card.Color color, Card.Type type, int number) {
    public enum Color { RED, BLUE, GREEN, YELLOW, WILD }
    public enum Type { NUMBER, SKIP, REVERSE, DRAW_TWO, WILD_CHANGE_COLOR, WILD_DRAW_FOUR }
}
/*
public class Card {
    public enum Color {RED, BLUE, GREEN, YELLOW, WILD}

    public enum Type {NUMBER, SKIP, REVERSE, DRAW_TWO, WILD_CHANGE_COLOR, WILD_DRAW_FOUR}

    private Color color;
    private Type type;
    private int number;

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    // Used for debugging/admin purposes
    @Override
    public String toString() {
        if (type == Type.NUMBER) {
            return color + " " + number;
        }
        return color + " " + type;
    }
}
*/
