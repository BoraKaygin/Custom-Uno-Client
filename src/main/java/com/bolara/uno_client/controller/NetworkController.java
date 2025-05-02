package com.bolara.uno_client.controller;

import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.CreateGameResponse;
import com.bolara.uno_client.dto.JoinGameRequest;
import com.bolara.uno_client.session.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO: Might have to handle case where the session expires.
public class NetworkController {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String createGame() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to create game.");
                System.err.println("Response status code: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                return null;
            }
            String gameId = mapper.readValue(response.body(), CreateGameResponse.class).gameId();
            System.out.println("Game created successfully with ID: " + gameId);
            return gameId;
        } catch (Exception e) {
            System.err.println("Exception while creating game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean joinGame(String gameId) {
        try {
            String username = SessionManager.getUsername();
            if (username == null || username.isBlank()) {
                System.err.println("No username available in session.");
                return false;
            }

            JoinGameRequest joinDTO = new JoinGameRequest(username);
            String requestBody = mapper.writeValueAsString(joinDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId + "/join"))
                    .header("Content-Type", Constants.CT_APP_JSON)
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Joined game successfully.");
                return true;
            } else {
                System.err.println("Failed to join game.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception during joinGame: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean startGame(String gameId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId + "/start"))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Game started successfully.");
                return true;
            } else {
                System.err.println("Failed to start game. Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception during startGame: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean addComputerPlayer(String gameId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId + "/add-computer"))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Computer player added successfully.");
                return true;
            } else {
                System.err.println("Failed to add computer player.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception during addComputerPlayer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
