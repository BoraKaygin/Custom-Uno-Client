package com.bolara.uno_client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

public class LeaderboardEntry {
    private final StringProperty username;
    private final IntegerProperty score;

    @JsonCreator
    public LeaderboardEntry(@JsonProperty("username") String username,
                            @JsonProperty("score") int score) {
        this.username = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
    }


    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}
