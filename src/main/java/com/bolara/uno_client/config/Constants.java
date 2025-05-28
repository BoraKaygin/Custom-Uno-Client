package com.bolara.uno_client.config;

public class Constants {
    public static final String URL_BASE = "https://ceng453-20242-group13-backend.onrender.com";

    public static final String URL_LOGIN = URL_BASE + "/login";
    public static final String URL_LOGOUT = URL_BASE + "/logout";
    public static final String URL_REGISTER = URL_BASE + "/register";
    public static final String URL_PASSWORD_REMINDER_GET = URL_BASE + "/password-reminder/get";
    // TODO: also add password reminder set
    public static final String URL_PASSWORD_RESET_REQUEST = URL_BASE + "/password-reset/request";
    public static final String URL_PASSWORD_RESET_CONFIRM = URL_BASE + "/password-reset/confirm";
    public static final String URL_LEADERBOARD_WEEK = URL_BASE + "/leaderboard/week";
    public static final String URL_LEADERBOARD_MONTH = URL_BASE + "/leaderboard/month";
    public static final String URL_LEADERBOARD_ALL = URL_BASE + "/leaderboard/";

    public static final String URL_GAME = URL_BASE + "/games";

    public static final String CT_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String CT_APP_JSON = "application/json";

    public static final String SCENE_REGISTER = "/register.fxml";
    public static final String SCENE_LOGIN = "/login.fxml";
    public static final String SCENE_PASSWORD_REMINDER = "/password_reminder.fxml";
    public static final String SCENE_PASSWORD_RESET = "/password_reset.fxml";
    public static final String SCENE_LEADERBOARD = "/leaderboard.fxml";
    public static final String SCENE_MENU = "/menu.fxml";
    public static final String SCENE_GAME = "/game.fxml";
    public static final String SCENE_SET_REMINDER = "/set_reminder.fxml";

    public static final int WindowWidth = 1600;
    public static final int WindowHeight = 900;
}
