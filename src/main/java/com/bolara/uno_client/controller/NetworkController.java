package com.bolara.uno_client.controller;

import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.*;
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

    public static int joinGame(String gameId) {
        try {
            String username = SessionManager.getUsername();
            if (username == null || username.isBlank()) {
                System.err.println("No username available in session.");
                return -1;
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
                return mapper.readValue(response.body(), JoinGameResponse.class).addedPlayerIndex();
            } else {
                System.err.println("Failed to join game.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return -1;
            }

        } catch (Exception e) {
            System.err.println("Exception during joinGame: " + e.getMessage());
            e.printStackTrace();
            return -1;
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
                System.err.println("Failed to start game.");
                System.err.println("Status:" + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception during startGame: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean playCard(String gameId, int playerIndex, int cardIndex, Card.Color declaredColor) {
        try {
            StringBuilder uri = new StringBuilder(Constants.URL_GAME)
                    .append("/").append(gameId)
                    .append("/play")
                    .append("?playerIndex=").append(playerIndex)
                    .append("&cardIndex=").append(cardIndex);

            if (declaredColor != null) {
                uri.append("&declaredColor=").append(declaredColor);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri.toString()))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Card played successfully.");
                return true;
            } else {
                System.err.println("Failed to play card.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception while playing card: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean playCheatCard(String gameId, int playerIndex, Card card) {
        try {
            PlayCheatCardRequest requestObj = new PlayCheatCardRequest(playerIndex, card);
            String requestBody = mapper.writeValueAsString(requestObj);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId + "/cheat"))
                    .header("Content-Type", Constants.CT_APP_JSON)
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Cheat card played successfully.");
                return true;
            } else {
                System.err.println("Failed to play cheat card.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception during playCheatCard: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setTopCardColor(String gameId, Card.Color declaredColor) {
        try {
            StringBuilder uri = new StringBuilder(Constants.URL_GAME)
                    .append("/").append(gameId)
                    .append("/set-top-color")
                    .append("?declaredColor=").append(declaredColor);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri.toString()))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Cookie", SessionManager.getSessionCookie())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Top card color set successfully.");
                return true;
            } else {
                System.err.println("Failed to set top card color.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Body: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception while setting top card color: " + e.getMessage());
            return false;
        }
    }


    public static boolean drawCard(String gameId, int playerIndex) {
        try {
            String uri = Constants.URL_GAME + "/" + gameId + "/draw?playerIndex=" + playerIndex;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Card drawn successfully.");
                return true;
            } else {
                System.err.println("Failed to draw card.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception while drawing card: " + e.getMessage());
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

    public static Game getGame(String gameId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), Game.class);
            } else {
                System.err.println("Failed to fetch game.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception during fetchGame: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerGameView getPlayerGameView(String gameId, int playerIndex) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.URL_GAME + "/" + gameId + "/player/" + playerIndex))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), PlayerGameView.class);
            } else {
                System.err.println("Failed to fetch player game view.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Exception during getPlayerGameView: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean removeGame(String gameId) {
        try {
            String uri = Constants.URL_GAME + "/" + gameId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Cookie", SessionManager.getSessionCookie())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Game removed successfully.");
                return true;
            } else {
                System.err.println("Failed to remove game.");
                System.err.println("Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Exception while removing game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
