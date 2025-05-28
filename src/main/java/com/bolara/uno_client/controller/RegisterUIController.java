package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterUIController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField reminderField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String reminder = reminderField.getText();

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            statusLabel.setText("Username, email and password cannot be blank");
            return;
        }

        String response = authService.register(username, password, email, reminder);
        statusLabel.setText(response);
    }

    @FXML
    private void openLogin() {
        StageManager.switchScene(Constants.SCENE_LOGIN);
    }
}
