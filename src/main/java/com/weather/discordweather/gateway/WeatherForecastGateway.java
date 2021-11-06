package com.weather.discordweather.gateway;

import com.weather.discordweather.client.discord.DiscordClient;
import com.weather.discordweather.client.mapquest.MapQuestClient;
import com.weather.discordweather.client.mapquest.converter.GeocodeResponseMapper;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.converter.OneCallResponseMapper;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.model.Geolocation;
import com.weather.discordweather.model.WeatherForecast;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
public class WeatherForecastGateway {
  private final DiscordClient discordService;
  private final MapQuestClient mapQuestService;
  private final OpenWeatherMapClient openWeatherService;

  @Inject
  public WeatherForecastGateway(
      DiscordClient ds,
      MapQuestClient ms,
      OpenWeatherMapClient ws) {
    discordService = ds;
    mapQuestService = ms;
    openWeatherService = ws;
  }

  public Optional<WeatherForecast> getWeatherForecast(double lat, double lon) {
    OneCallResponse openWeatherResponse = openWeatherService.getWeather(lat, lon);
    GeocodeResponse mapQuestResponse = mapQuestService.reverseGeocode(lat, lon);

    Optional<Geolocation> location = GeocodeResponseMapper.convert(mapQuestResponse);

    if (location.isPresent()) {
      String fullLocation = location.get().country().equalsIgnoreCase("US") ?
          String.format("%s, %s", location.get().city(), location.get().state()) :
          String.format("%s, %s", location.get().city(), location.get().country());

      return OneCallResponseMapper.convert(openWeatherResponse, fullLocation);
    }

    return Optional.empty();
  }

  public Optional<Geolocation> forwardGeocode(String location) {
    return GeocodeResponseMapper.convert(mapQuestService.forwardGeocode(location));
  }

  public Optional<Geolocation> reverseGeocode(double lat, double lon) {
    return GeocodeResponseMapper.convert(mapQuestService.reverseGeocode(lat, lon));
  }

  public void postContent(String report) {
    discordService.postContent(report);
  }
}
