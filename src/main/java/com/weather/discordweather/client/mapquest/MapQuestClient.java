package com.weather.discordweather.client.mapquest;

import com.weather.discordweather.util.JsonUtils;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class MapQuestClient {
  private final HttpClient httpClient;

  @Inject
  public MapQuestClient() {
    httpClient = HttpClient.newHttpClient();
  }

  public GeocodeResponse forwardGeocode(String location) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(
            URI.create(
              getUriBuilder()
                .path("/geocoding/v1/address")
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
          GeocodeResponse.class);
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
          GeocodeResponse.class);
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