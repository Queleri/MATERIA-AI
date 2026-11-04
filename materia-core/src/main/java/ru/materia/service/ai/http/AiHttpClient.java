package ru.materia.service.ai.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class AiHttpClient {
    private final HttpClient client = HttpClient.newHttpClient();
    public String sendPost(String url, String jsonBody) throws IOException, InterruptedException {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("URL не может быть null или состоять только из пробелов");
        if(jsonBody == null || jsonBody.isBlank())
            throw new IllegalArgumentException("Тело JSON не может быть null или состоять только из пробелов");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300)
            throw new RuntimeException("Ошибка http "+statusCode);
        return response.body();
    }
    //overloading
    public String sendPost(String url, String jsonBody, String apiKey) throws IOException, InterruptedException{
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("URL не может быть null или состоять из пробелов");
        if (jsonBody == null || jsonBody.isBlank())
            throw new IllegalArgumentException("JSON тело не может быть null или состоять из пробелов");
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("API ключ не может быть null или состоять из пробелов");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new RuntimeException("Ошибка HTTP: " + statusCode);
        }
        return response.body();
    }
}
