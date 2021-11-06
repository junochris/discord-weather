package com.weather.discordweather.client.mapquest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;

import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class MapQuestClient {
  public GeocodeResponse forwardGeocode(String location) {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            String.format("http://www.mapquestapi.com/geocoding/v1/reverse?location=%s", location)))
        .build();

    String responseBody;
    try {
      responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    GeocodeResponse response;
    try {
      response = mapper.readValue(responseBody, GeocodeResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return response;
  }

  public GeocodeResponse reverseGeocode(double lat, double lon) {
    HttpClient client = HttpClient.newHttpClient();

    String location = String.format("%s,%s", lat, lon);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            String.format("http://www.mapquestapi.com/geocoding/v1/reverse?%s", location)))
        .build();

    String responseBody;
    try {
      responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    GeocodeResponse response;
    try {
      response = mapper.readValue(responseBody, GeocodeResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return response;
  }
}
