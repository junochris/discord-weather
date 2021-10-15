package com.weather.discordweather.client;

import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class OpenWeatherMapClient {

  public String getWeather(Double lat, Double lon) {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=minutely&appid=56fa105ab7ae0ad13f69f4587f72065c", lat, lon)))
        .build();

    String responseBody = "";
    try {
      responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .get();
    } catch (Exception e) {

    }

    return responseBody;
  }
}
