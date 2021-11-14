package com.weather.discordweather.client.discord;

import com.weather.discordweather.util.JsonUtils;
import com.weather.discordweather.client.discord.model.ExecuteWebhookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Named
public class DiscordClient {

  @Autowired
  private final HttpClient httpClient;

  @Inject
  public DiscordClient(HttpClient client) {
    httpClient = client;
  }

  public String executeWebhook(
      String webhookId,
      String webhookToken,
      String content
  ) {
    ExecuteWebhookRequest webhookRequest = new ExecuteWebhookRequest(content);
    String jsonContent = JsonUtils.getJsonStringFromObject(webhookRequest);

    // URI (URL) parameters
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("id", webhookId);
    urlParams.put("token", webhookToken);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(
            URI.create(
                getUriBuilder()
                    .path("/api/webhooks/{id}/{token}")
                    .buildAndExpand(
                        Map.ofEntries(
                            Map.entry("Id", webhookId),
                            Map.entry("token", webhookToken)
                        )
                    )
                    .toUriString()
            )
        )
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonContent))
        .build();

    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public UriComponentsBuilder getUriBuilder() {
    return UriComponentsBuilder.newInstance()
        .scheme("https")
        .host("discord.com");
  }
}
