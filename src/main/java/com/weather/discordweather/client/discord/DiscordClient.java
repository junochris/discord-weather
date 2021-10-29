package com.weather.discordweather.client.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.discordweather.client.discord.model.ExecuteWebhookRequest;

import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class DiscordClient {
  public String postContent(String forecast) {
    ExecuteWebhookRequest webhookRequest = new ExecuteWebhookRequest(forecast);
    ObjectMapper mapper = new ObjectMapper();
    String forecastJson;
    try {
      forecastJson = mapper.writeValueAsString(webhookRequest);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://discord.com/api/webhooks/896866700702662697/qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE"
        ))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(forecastJson))
        .build();

    String responseBody;
    try {
      responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return responseBody;
  }
}
