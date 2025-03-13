package com.vlasttenei.telegram.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

public class ApiClient {
    private static final String BASE_URL = "https://equipment.test.calloftheshadows.com";
    private final HttpClient client;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void addThingToBag(String userId, String thingId) {
        try {
            String requestBody = String.format("[\"%s\"]", thingId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/internal/" + userId + "/bag/add-things"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("API запрос завершился с ошибкой. Код: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении API запроса: " + e.getMessage(), e);
        }
    }

    // Пример использования:
    // ApiClient apiClient = new ApiClient();
    // apiClient.addThingToBag("7517277660", "thing-deathbloom");
} 