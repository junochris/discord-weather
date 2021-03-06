package com.weather.discordweather.client.openweathermap;

import com.weather.discordweather.util.FileUtils;
import com.weather.discordweather.util.JsonUtils;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class OpenWeatherMapClient {

  @Autowired
  private final HttpClient httpClient;
  private final String apiKeyLocation = "src/main/resources/OneCallApiKey.txt";

  @Inject
  public OpenWeatherMapClient(HttpClient client) {
    httpClient = client;
  }

  public OneCallResponse getWeather(double lat, double lon) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(
            URI.create(
                getUriBuilder()
                    .path("/data/2.5/onecall")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("units", "imperial")
                    .queryParam("exclude", "minutely")
                    .queryParam("appid", FileUtils.getFileContents(apiKeyLocation))
                    .build()
                    .toUriString()
            )
        )
        .build();

    try {
      return JsonUtils.getObjectFromJsonString(
          httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .get(),
          OneCallResponse.class
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private UriComponentsBuilder getUriBuilder() {
    return UriComponentsBuilder.newInstance()
        .scheme("https")
        .host("api.openweathermap.org");
  }
}
