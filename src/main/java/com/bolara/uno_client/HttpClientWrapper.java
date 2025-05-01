package com.bolara.uno_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientWrapper {
    private static final HttpClient client = HttpClient.newHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponse<String> sendRequest(String url, String body, String contentType) throws IOException, InterruptedException {
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
