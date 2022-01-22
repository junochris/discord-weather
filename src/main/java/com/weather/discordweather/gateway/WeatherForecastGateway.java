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
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class WeatherForecastGateway {

  private final DiscordClient discordClient;
  private final MapQuestClient mapQuestClient;
  private final OpenWeatherMapClient openWeatherClient;

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
      return paginateForecast(forecast);
    }

    return discordClient.executeWebhook(
        "896866700702662697",
        "qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE",
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

  private String paginateForecast(String forecast) {
    while (forecast.length() > 2000) {
      int i = forecast.lastIndexOf('\n', 2000);
      discordClient.executeWebhook(
          "896866700702662697",
          "qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE",
          forecast.substring(0, i)
      );
      forecast = forecast.substring(i + 1);
    }
    return discordClient.executeWebhook(
        "896866700702662697",
        "qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE",
        forecast
    );
  }

  private String stripSpaces(String location) {
    return location.replaceAll("\\s", "");
  }
}
