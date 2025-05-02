package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Username and password cannot be blank");
            return;
        }

        String response = authService.login(username, password);
        statusLabel.setText(response);
        StageManager.switchScene(Constants.SCENE_MENU);

    }

    @FXML
    private void openRegister() {
        StageManager.switchScene(Constants.SCENE_REGISTER);
    }

    @FXML
    private void openReminder() { StageManager.switchScene(Constants.SCENE_PASSWORD_REMINDER); }

    @FXML
    private void openPasswordReset() { StageManager.switchScene(Constants.SCENE_PASSWORD_RESET); }

    @FXML
    private void openLeaderboard() { StageManager.switchScene(Constants.SCENE_LEADERBOARD); }


}
