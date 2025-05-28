package com.bolara.uno_client.controller;

import com.bolara.uno_client.StageManager;
import com.bolara.uno_client.config.Constants;
import com.bolara.uno_client.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.*;

import java.io.IOException;

public class SetReminderUIController {

    @FXML private TextField emailField;
    @FXML private TextField reminderField;
    @FXML private Label statusLabel;

    private final OkHttpClient client = new OkHttpClient();

    @FXML
    public void handleSubmit() {
        String email = emailField.getText().trim();
        String reminder = reminderField.getText().trim();

        if (email.isEmpty() || reminder.isEmpty()) {
            statusLabel.setText("Both fields are required.");
            return;
        }

        String json = String.format("""
            {
              "email": "%s",
              "passwordReminder": "%s"
            }
        """, email, reminder);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Constants.URL_BASE + "/password-reminder/set")
                .header("Cookie", SessionManager.getSessionCookie())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Error: " + e.getMessage()));
            }

            public void onResponse(Call call, Response response) throws IOException {
                String message = response.body().string();
                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        statusLabel.setText("✅ " + "Your password reminder is set successfully!");
                    } else {
                        statusLabel.setText("❌ Failed to set password reminder: " + message);
                    }

                });
            }
        });
    }

    @FXML
    private void handleBackToMenu() {
        StageManager.switchScene(Constants.SCENE_MENU);
    }

}
