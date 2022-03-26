package com.weather.discordweather.gateway;

import com.weather.discordweather.client.discord.DiscordClient;
import com.weather.discordweather.client.mapquest.MapQuestClient;
import com.weather.discordweather.client.mapquest.model.Coordinate;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.Geolocation;
import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.converter.WeatherForecastMapper;
import com.weather.discordweather.model.WeatherForecast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class WeatherForecastGateway {

  private final DiscordClient discordClient;
  private final MapQuestClient mapQuestClient;
  private final OpenWeatherMapClient openWeatherClient;
  private static final int DISCORD_CHARACTER_LIMIT = 2000;
  private static final String discordWebhookId = "896866700702662697";
  private static final String discordWebhookToken = "qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE";

  @Inject
  public WeatherForecastGateway(
      DiscordClient dc,
      MapQuestClient mc,
      OpenWeatherMapClient wc
  ) {
    discordClient = dc;
    mapQuestClient = mc;
    openWeatherClient = wc;
  }

  public Optional<WeatherForecast> getWeatherForecast(Double lat, Double lon, String location) {
    GeocodeResponse mapQuestResponse;
    OneCallResponse openWeatherResponse;

    if (!location.isEmpty()) {
      mapQuestResponse = mapQuestClient.forwardGeocode(stripSpaces(location));
      Optional<Coordinate> coordinates = extractCoordinates(mapQuestResponse);
      if (coordinates.isPresent()) {
        openWeatherResponse = openWeatherClient.getWeather(
            coordinates.get().lat(),
            coordinates.get().lng());
      } else {
        return Optional.empty();
      }
    } else {
      mapQuestResponse = mapQuestClient.reverseGeocode(lat, lon);
      openWeatherResponse = openWeatherClient.getWeather(lat, lon);
    }

    return WeatherForecastMapper.fromOpenWeatherMapAndMapQuest(
        openWeatherResponse,
        mapQuestResponse
    );
  }

  public GeocodeResponse forwardGeocode(String location) {
    return mapQuestClient.forwardGeocode(location);
  }

  public GeocodeResponse reverseGeocode(double lat, double lon) {
    return mapQuestClient.reverseGeocode(lat, lon);
  }

  public String executeWebhook(String forecast) {
    if (forecast.length() > 2000) {
      return paginateForecast(forecast).stream().map(
          page -> discordClient.executeWebhook(
              discordWebhookId,
              discordWebhookToken,
              page
          ))
          .reduce((first, second) -> second)
          .orElseThrow(IllegalStateException::new);
    }

    return discordClient.executeWebhook(
        discordWebhookId,
        discordWebhookToken,
        forecast
    );
  }

  private Optional<Coordinate> extractCoordinates(GeocodeResponse response) {
    if (response.results().isEmpty()) {
      return Optional.empty();
    }

    return response
        .results()
        .get(0)
        .locations()
        .stream()
        .map(Geolocation::latLng)
        .findFirst();
  }

  private List<String> paginateForecast(String forecast) {
    return forecast.lines().collect(
        () -> new ArrayList<>(Collections.singleton("")),
        (pages, line) -> {
          String lastPage = pages.get(pages.size() - 1);
          if (lastPage.length() + line.length() <= DISCORD_CHARACTER_LIMIT) {
            pages.set(pages.size() - 1, lastPage.concat(line));
          } else {
            pages.add(line);
          }
        },
        ArrayList::addAll);
  }

  private String stripSpaces(String location) {
    return location.replaceAll("\\s", "");
  }
}
