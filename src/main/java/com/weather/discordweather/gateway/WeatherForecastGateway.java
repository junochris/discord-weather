package com.weather.discordweather.gateway;

import com.weather.discordweather.client.discord.DiscordClient;
import com.weather.discordweather.client.mapquest.MapQuestClient;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.converter.WeatherForecastMapper;
import com.weather.discordweather.model.WeatherForecast;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
public class WeatherForecastGateway {
  private final DiscordClient discordClient;
  private final MapQuestClient mapQuestClient;
  private final OpenWeatherMapClient openWeatherClient;

  @Inject
  public WeatherForecastGateway(
      DiscordClient dc,
      MapQuestClient mc,
      OpenWeatherMapClient wc) {
    discordClient = dc;
    mapQuestClient = mc;
    openWeatherClient = wc;
  }

  public Optional<WeatherForecast> getWeatherForecast(double lat, double lon) {
    OneCallResponse openWeatherResponse = openWeatherClient.getWeather(lat, lon);
    GeocodeResponse mapQuestResponse = mapQuestClient.reverseGeocode(lat, lon);
    return WeatherForecastMapper.fromOpenWeatherMapAndMapQuest(openWeatherResponse, mapQuestResponse);
  }

  public GeocodeResponse forwardGeocode(String location) {
    return mapQuestClient.forwardGeocode(location);
  }

  public GeocodeResponse reverseGeocode(double lat, double lon) {
    return mapQuestClient.reverseGeocode(lat, lon);
  }

  public String executeWebhook(String forecast) {
    return discordClient.executeWebhook(
        "896866700702662697",
        "qcvv8ihtrGTPjZswASiJaOsy-qMua58DkgAb-XA39WAG5D1FxFDz1EGJ53FavFz-GjTE",
        forecast);
  }
}
