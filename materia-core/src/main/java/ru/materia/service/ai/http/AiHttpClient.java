
package ru.materia.service.ai.http;

import ru.materia.service.ai.exception.AiApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;

public class AiHttpClient {

    private final HttpClient client;

    public AiHttpClient() {
        this(HttpClient.newHttpClient());
    }
    public AiHttpClient(HttpClient client) {
        this.client = Objects.requireNonNull(client);
    }
    // Обычный POST-запрос
    public String sendPost(String url, String jsonBody)
            throws IOException, InterruptedException {
        return sendPost(url, jsonBody, Map.of());
    }
    // POST-запрос для Gemini
    public String sendPost(String url, String jsonBody, String apiKey)
            throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException(
                    "API-ключ не может быть пустым"
            );
        }
        return sendPost(url, jsonBody, Map.of("x-goog-api-key", apiKey));
    }
    // Универсальный POST-запрос
    public String sendPost(String url, String jsonBody, Map<String, String> headers
    ) throws IOException, InterruptedException {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL не может быть пустым");
        }
        if (jsonBody == null || jsonBody.isBlank()) {
            throw new IllegalArgumentException("JSON-тело не может быть пустым");
        }

        if (headers == null) {
            throw new IllegalArgumentException("Заголовки не могут быть null");
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (name == null || name.isBlank() || value == null || value.isBlank()) {
                throw new IllegalArgumentException("Название и значение заголовка не должны быть пустыми");
            }
            builder.setHeader(name, value);
        }
        builder.setHeader("Content-Type", "application/json");
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new AiApiException(statusCode, "Ошибка AI API: " + response.body());
        }
        return response.body();
    }
}
