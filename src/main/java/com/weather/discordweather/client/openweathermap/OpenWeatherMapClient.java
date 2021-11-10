package com.weather.discordweather.client.openweathermap;

import com.weather.discordweather.util.JsonUtils;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class OpenWeatherMapClient {
  private final HttpClient httpClient;

  @Inject
  public OpenWeatherMapClient() {
    httpClient = HttpClient.newHttpClient();
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
                .queryParam("appid", "56fa105ab7ae0ad13f69f4587f72065c")
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
          OneCallResponse.class);
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
