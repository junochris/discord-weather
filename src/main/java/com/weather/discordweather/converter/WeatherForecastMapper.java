package com.weather.discordweather.converter;

import static java.lang.Math.round;

import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.GeocodeResult;
import com.weather.discordweather.client.mapquest.model.Geolocation;
import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.HourlyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.model.WeatherAlert;
import com.weather.discordweather.model.WeatherCondition;
import com.weather.discordweather.model.WeatherForecast;
import com.weather.discordweather.model.WeatherRecord;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeatherForecastMapper {

  private static final int THREE_HOURS = 3;
  private static final int EIGHTEEN_HOURS = 18;
  private static final int WEATHER_ID_CLEAR_SKY = 800;
  private static final int DEFAULT_WEATHER_ID = WEATHER_ID_CLEAR_SKY;

  public static Optional<WeatherForecast> fromOpenWeatherMapAndMapQuest(
      OneCallResponse weather,
      GeocodeResponse geolocation
  ) {
    if (weather.daily().isEmpty()) {
      return Optional.empty();
    }

    DailyWeatherForecast currentDay = weather.daily().get(0);
    ZoneOffset timezone = ZoneOffset.ofTotalSeconds(weather.timezone_offset());

    return Optional.of(
        new WeatherForecast(
            getLocation(geolocation),
            LocalDateTime.ofEpochSecond(currentDay.dt(), 0, timezone),
            collectAlerts(weather, timezone),
            getCurrentWeatherCondition(currentDay),
            round(currentDay.temp().max()),
            round(currentDay.temp().min()),
            currentDay.humidity(),
            LocalDateTime.ofEpochSecond(currentDay.sunrise(), 0, timezone),
            LocalDateTime.ofEpochSecond(currentDay.sunset(), 0, timezone),
            currentDay.moon_phase(),
            collectNext6TemperaturesEvery3Hours(weather.hourly(), timezone)
        )
    );
  }

  private static String capitalizeFirstLetter(String description) {
    return description.substring(0, 1)
        .toUpperCase() + description.substring(1);
  }

  private static List<WeatherAlert> collectAlerts(
      OneCallResponse response,
      ZoneOffset timezone
  ) {
    if (response.alerts().isPresent()) {
      return response.alerts().get().stream().map(
          alert -> new com.weather.discordweather.model.WeatherAlert(
              alert.event(),
              LocalDateTime.ofEpochSecond(alert.start(), 0, timezone),
              LocalDateTime.ofEpochSecond(alert.end(), 0, timezone),
              alert.description()
          )).collect(Collectors.toUnmodifiableList());
    }
    return Collections.emptyList();
  }

  private static List<WeatherRecord> collectNext6TemperaturesEvery3Hours(
      List<HourlyWeatherForecast> hourlyWeatherForecasts,
      ZoneOffset timezone
  ) {
    return IntStream
        .range(0, EIGHTEEN_HOURS)
        .filter(i -> i % THREE_HOURS == 0 && i < hourlyWeatherForecasts.size())
        .mapToObj(i -> {
          HourlyWeatherForecast hour = hourlyWeatherForecasts.get(i);
          return new WeatherRecord(
              LocalDateTime.ofEpochSecond(hour.dt(), 0, timezone),
              new WeatherCondition(
                  hour.weather().get(0).id(),
                  hour.weather().get(0).description(),
                  round(hour.temp())
              )
          );
        }).collect(Collectors.toUnmodifiableList());
  }

  private static WeatherCondition getCurrentWeatherCondition(DailyWeatherForecast currentDay) {
    int weatherId = DEFAULT_WEATHER_ID;
    String weatherCondition = "";
    if (!currentDay.weather().isEmpty()) {
      weatherId = currentDay.weather().get(0).id();
      weatherCondition = currentDay.weather().get(0).description();
      if (!weatherCondition.isEmpty()) {
        weatherCondition = capitalizeFirstLetter(weatherCondition);
      }
    }
    return new WeatherCondition(weatherId, weatherCondition, 0);
  }

  private static String getLocation(GeocodeResponse response) {
    if (!response.results().isEmpty()) {
      GeocodeResult result = response.results().get(0);
      if (!result.locations().isEmpty()) {
        Geolocation geolocation = result.locations().get(0);
        return String.format(
            "%s, %s",
            geolocation.adminArea5(),
            geolocation.adminArea1().equalsIgnoreCase("US")
                ? geolocation.adminArea3()
                : geolocation.adminArea1()
        );
      }
    }
    return "";
  }
}
