package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.model.LeaderboardEntry;
import com.bolara.uno_client.HttpClientWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class LeaderboardUIController {

    @FXML
    private ComboBox<String> timeRangeBox;

    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> rankColumn;

    @FXML
    private TableColumn<LeaderboardEntry, String> usernameColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> scoreColumn;

    private final ObservableList<LeaderboardEntry> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        timeRangeBox.setItems(FXCollections.observableArrayList("Week", "Month", "All Time"));
        leaderboardTable.setItems(data);

        rankColumn.setCellValueFactory(cellData -> {
            int index = leaderboardTable.getItems().indexOf(cellData.getValue()) + 1;
            return new ReadOnlyObjectWrapper<>(index);
        });

        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());

    }

    @FXML
    private void handleTimeRangeChange() {
        String selection = timeRangeBox.getValue();
        String url;

        if ("Week".equals(selection)) {
            url = Constants.URL_LEADERBOARD_WEEK;
        } else if ("Month".equals(selection)) {
            url = Constants.URL_LEADERBOARD_MONTH;
        } else {
            url = Constants.URL_LEADERBOARD_ALL;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClientWrapper.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = HttpClientWrapper.objectMapper;
                List<LeaderboardEntry> entries = mapper.readValue(response.body(), new TypeReference<>() {});
                data.setAll(entries);
            } else {
                System.err.println("Failed to fetch leaderboard (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error while fetching leaderboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        StageManager.switchScene(Constants.SCENE_MENU);
    }
}
