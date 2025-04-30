package com.bolara.uno_client.service;

import com.bolara.uno_client.HttpClientWrapper;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.dto.RegistrationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;

public class AuthService {
    public String register(String username, String password, String email, String reminder) {
        try {
            String requestBody = new RegistrationRequest.Builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .passwordReminder(reminder)
                    .build()
                    .toJson();

            HttpResponse<String> response = HttpClientWrapper.sendRequest(Constants.URL_REGISTER, requestBody, Constants.CT_APP_JSON);

            JsonNode jsonNode = HttpClientWrapper.objectMapper.readTree(response.body());
            return jsonNode.get("message").asText();
        } catch (Exception e) {
            System.err.println("Error while registering: " + e.getMessage());
            return "Registration failed";
        }
    }

    public String login(String username, String password) {
        try {
            String requestBody = "username=" + username + "&password=" + password;

            HttpResponse<String> response = HttpClientWrapper.sendRequest(Constants.URL_LOGIN, requestBody, Constants.CT_URL_ENCODED);
            String location = response.headers().firstValue("Location").orElse(null);

            if (response.statusCode() != 302 || location == null) {
                System.err.println("Login failed: " + response.body());
                return "Login failed";
            }

            boolean success = location.equals(Constants.URL_BASE + "/");
            if (success) {
                return "Login successful";
            } else {
                return "Login failed: Bad Credentials";
            }
        } catch (Exception e) {
            System.err.println("Error while logging in: " + e.getMessage());
            return "Registration failed";
        }
    }

    public String getPasswordReminder(String email) {
        try {
            String requestBody = "{\"email\":\"" + email + "\"}";

            HttpResponse<String> response = HttpClientWrapper.sendRequest(
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

            HttpResponse<String> response = HttpClientWrapper.sendRequest(
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

            HttpResponse<String> response = HttpClientWrapper.sendRequest(
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
