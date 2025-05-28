package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordResetUIController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField tokenField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleSendResetCode() {
        String email = emailField.getText();
        if (email.isBlank()) {
            statusLabel.setText("Please enter your email.");
            return;
        }

        String result = authService.requestPasswordReset(email);
        statusLabel.setText(result);
    }

    @FXML
    private void handleResetPassword() {
        String token = tokenField.getText();
        String newPassword = newPasswordField.getText();

        if (token.isBlank() || newPassword.isBlank()) {
            statusLabel.setText("Please enter both the token and new password.");
            return;
        }

        String result = authService.resetPassword(token, newPassword);
        statusLabel.setText(result);
    }

    @FXML
    private void openLogin() {
        StageManager.switchScene(Constants.SCENE_LOGIN);
    }
}
