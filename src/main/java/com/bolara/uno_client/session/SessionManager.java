package com.bolara.uno_client.session;

public class SessionManager {
    private static String sessionCookie;
    private static String username;

    public static void setSessionCookie(String cookie) {
        sessionCookie = cookie;
    }

    public static String getSessionCookie() {
        return sessionCookie;
    }

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }
}
