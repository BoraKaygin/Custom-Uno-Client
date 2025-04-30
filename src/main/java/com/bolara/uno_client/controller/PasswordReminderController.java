package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PasswordReminderController {
    @FXML
    private TextField emailField;

    @FXML
    private Label reminderLabel;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleGetReminder() {
        String email = emailField.getText();

        if (email.isBlank()) {
            statusLabel.setText("Email cannot be blank");
            return;
        }

        // JSON-based request
        String reminder = authService.getPasswordReminder(email);

        if (reminder == null || reminder.isEmpty()) {
            reminderLabel.setText(""); // Clear previous reminder
            statusLabel.setText("No reminder found for this email: " + email);
        } else {
            reminderLabel.setText(reminder);
            statusLabel.setText(""); // Clear previous error
        }
    }

    @FXML
    private void openLogin() {
        StageManager.switchScene(Constants.SCENE_LOGIN);
    }
}
