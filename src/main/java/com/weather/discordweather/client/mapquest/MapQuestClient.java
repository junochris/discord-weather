package com.weather.discordweather.client.mapquest;

import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.util.FileUtils;
import com.weather.discordweather.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class MapQuestClient {

  @Autowired
  private final HttpClient httpClient;
  private final String apiKeyLocation = "src/main/resources/MapQuestApiKey.txt";

  @Inject
  public MapQuestClient(HttpClient client) {
    httpClient = client;
  }

  public GeocodeResponse forwardGeocode(String location) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(
            URI.create(
                getUriBuilder()
                    .path("/geocoding/v1/address")
                    .queryParam("key", FileUtils.getFileContents(apiKeyLocation))
                    .queryParam("location", location)
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
          GeocodeResponse.class
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public GeocodeResponse reverseGeocode(double lat, double lon) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(
            URI.create(
                getUriBuilder()
                    .path("/geocoding/v1/reverse")
                    .queryParam("key", FileUtils.getFileContents(apiKeyLocation))
                    .queryParam("location", String.format("%s,%s", lat, lon))
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
          GeocodeResponse.class
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private UriComponentsBuilder getUriBuilder() {
    return UriComponentsBuilder.newInstance()
        .scheme("http")
        .host("www.mapquestapi.com");
  }
}
