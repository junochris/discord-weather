package com.weather.discordweather.client.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;

import javax.inject.Named;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Named
public class OpenWeatherMapClient {

  public OneCallResponse getWeather(double lat, double lon) {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&units=imperial&exclude=minutely&appid=56fa105ab7ae0ad13f69f4587f72065c", lat, lon)))
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
    mapper.registerModule(new Jdk8Module());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    OneCallResponse response;
    try {
      response = mapper.readValue(responseBody, OneCallResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return response;
  }
}
