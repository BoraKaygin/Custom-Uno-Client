package com.bolara.uno_client.service;

import com.bolara.uno_client.HttpClientWrapper;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.RegistrationRequest;
import com.bolara.uno_client.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    HttpClient client = HttpClient.newHttpClient();

    public String register(String username, String password, String email, String reminder) {
        try {
            String requestBody = new RegistrationRequest.Builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .passwordReminder(reminder)
                    .build()
                    .toJson();

            HttpResponse<String> response = HttpClientWrapper.sendBasicPostRequest(Constants.URL_REGISTER, requestBody, Constants.CT_APP_JSON);

            JsonNode jsonNode = HttpClientWrapper.objectMapper.readTree(response.body());
            return jsonNode.get("message").asText();
        } catch (Exception e) {
            System.err.println("Error while registering: " + e.getMessage());
            return "Registration failed";
        }
    }

    public Pair<Boolean, String> login(String username, String password) {
        try {
            String requestBody = "username=" + username + "&password=" + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(Constants.URL_LOGIN))
                    .header("Content-Type", Constants.CT_URL_ENCODED)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String location = response.headers().firstValue("Location").orElse("");

            if (response.statusCode() != 302 || location.contains("error")) {
                return new Pair<>(false, "Login failed: Invalid credentials");
            }

            String setCookieHeader = response.headers().firstValue("Set-Cookie").orElse(null);
            if (setCookieHeader != null) {
                String sessionId = setCookieHeader.split(";")[0]; // Extract JSESSIONID=...
                SessionManager.setSessionCookie(sessionId);
                SessionManager.setUsername(username);
            } else {
                System.err.println("Set-Cookie header not found");
                return new Pair<>(false, "Login failed: No session cookie");
            }

            return new Pair<>(true, "Login successful");

        } catch (Exception e) {
            System.err.println("Error while logging in: " + e.getMessage());
            e.printStackTrace();
            return new Pair<>(false, "Login failed due to exception");
        }
    }


    public Pair<Boolean, String> logout() {
        try {
            HttpResponse<String> response = HttpClientWrapper.sendBasicPostRequest(Constants.URL_LOGOUT, "", Constants.CT_URL_ENCODED);
            if (response.statusCode() != 302) {
                System.err.println("Logout failed: " + response.body());
                return new Pair<Boolean, String>(false, "Logout failed");
            } else {
                return new Pair<Boolean, String>(true, "Logout successful");
            }
        } catch (Exception e) {
            System.err.println("Error while logging out: " + e.getMessage());
            return new Pair<Boolean, String>(false, "Logout failed");
        }
    }

    public String getPasswordReminder(String email) {
        try {
            String requestBody = "{\"email\":\"" + email + "\"}";

            HttpResponse<String> response = HttpClientWrapper.sendBasicPostRequest(
                    Constants.URL_PASSWORD_REMINDER_GET, requestBody, Constants.CT_APP_JSON);

            if (response.statusCode() != 200) {
                System.err.println("Failed to fetch reminder: " + response.body());
                return null;
            }

            System.out.println("Response body: " + response.body());
            String responseBody = response.body();
            JsonNode json = HttpClientWrapper.objectMapper.readTree(responseBody);
            return json.has("message") ? json.get("message").asText() : null;

        } catch (Exception e) {
            System.err.println("Error while retrieving password reminder: " + e.getMessage());
            return null;
        }
    }

    public String requestPasswordReset(String email) {
        try {
            String requestBody = "{\"email\":\"" + email + "\"}";

            HttpResponse<String> response = HttpClientWrapper.sendBasicPostRequest(
                    Constants.URL_PASSWORD_RESET_REQUEST,
                    requestBody,
                    Constants.CT_APP_JSON
            );

            if (response.statusCode() != 200) {
                return "Reset request failed: " + response.body();
            }

            JsonNode json = HttpClientWrapper.objectMapper.readTree(response.body());
            return json.get("message").asText();

        } catch (Exception e) {
            System.err.println("Error during password reset request: " + e.getMessage());
            return "An error occurred.";
        }
    }

    public String resetPassword(String token, String newPassword) {
        try {
            String requestBody = String.format(
                    "{\"token\":\"%s\", \"newPassword\":\"%s\"}",
                    token, newPassword
            );

            HttpResponse<String> response = HttpClientWrapper.sendBasicPostRequest(
                    Constants.URL_PASSWORD_RESET_CONFIRM,
                    requestBody,
                    Constants.CT_APP_JSON
            );

            if (response.statusCode() != 200) {
                return "Reset failed: " + response.body();
            }

            JsonNode json = HttpClientWrapper.objectMapper.readTree(response.body());
            return json.get("message").asText();

        } catch (Exception e) {
            System.err.println("Error during password reset confirm: " + e.getMessage());
            return "An error occurred.";
        }
    }


}
