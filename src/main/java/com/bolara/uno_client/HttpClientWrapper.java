package com.bolara.uno_client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO: Refactor this, breaks abstraction and causes coupling.
public class HttpClientWrapper {
    private static final HttpClient client = HttpClient.newHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponse<String> sendBasicPostRequest(String url, String body, String contentType) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    public static HttpClient getClient() {
        return client;
    }
}
