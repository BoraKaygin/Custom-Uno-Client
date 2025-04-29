package com.bolara.uno_client.dto;

public record RegistrationRequest(
    String username,
    String password,
    String email,
    String passwordReminder
) {
    public static class Builder {
        private String username;
        private String password;
        private String email;
        private String passwordReminder;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordReminder(String reminder) {
            this.passwordReminder = reminder;
            return this;
        }

        public RegistrationRequest build() {
            return new RegistrationRequest(username, password, email, passwordReminder);
        }
    }

    public String toJson() {
        return String.format(
            "{\"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\", \"passwordReminder\":\"%s\"}",
            username, password, email, passwordReminder
        );
    }
}