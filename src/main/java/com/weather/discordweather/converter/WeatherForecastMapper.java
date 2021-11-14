package com.weather.discordweather.converter;

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

import static java.lang.Math.round;

public class WeatherForecastMapper {

  private static final int THREE_HOURS = 3;
  private static final int EIGHTEEN_HOURS = 18;

  public static Optional<WeatherForecast> fromOpenWeatherMapAndMapQuest(
      OneCallResponse weather,
      GeocodeResponse geolocation
  ) {
    if (weather.daily().isEmpty()) {
      return Optional.empty();
    }

    // Get the location from the GeocodeResponse
    String location = "";
    if (!geolocation.results().isEmpty()) {
      GeocodeResult result = geolocation.results().get(0);
      if (!result.locations().isEmpty()) {
        Geolocation geocodeLocation = result.locations().get(0);
        location = String.format(
            "%s, %s",
            geocodeLocation.adminArea5(),
            geocodeLocation.adminArea1().equalsIgnoreCase("US")
                ? geocodeLocation.adminArea3()
                : geocodeLocation.adminArea1()
        );
      }
    }

    DailyWeatherForecast currentDay = weather.daily().get(0);
    ZoneOffset timezone = ZoneOffset.ofTotalSeconds(weather.timezone_offset());

    // Collect alerts
    List<WeatherAlert> alerts = Collections.emptyList();
    if (currentDay.alerts().isPresent()) {
      alerts = currentDay.alerts().get().stream().map(
          alert -> new com.weather.discordweather.model.WeatherAlert(
              alert.event(),
              LocalDateTime.ofEpochSecond(alert.start(), 0, timezone),
              LocalDateTime.ofEpochSecond(alert.end(), 0, timezone)
          )).collect(Collectors.toUnmodifiableList());
    }

    // Default id is clear sky
    int weatherId = 800;
    String weatherCondition = "";
    if (currentDay.weather().size() > 0) {
      weatherId = currentDay.weather().get(0).id();
      weatherCondition = currentDay.weather().get(0).description();
      if (!weatherCondition.isEmpty()) {
        // Capitalize the first letter
        weatherCondition = weatherCondition.substring(0, 1)
            .toUpperCase() + weatherCondition.substring(1);
      }
    }

    // Collect the next 6 temperatures in 3 hour intervals.
    List<WeatherRecord> records = IntStream
        .range(0, EIGHTEEN_HOURS)
        .filter(i -> i % THREE_HOURS == 0 && i < weather.hourly().size())
        .mapToObj(i -> {
          HourlyWeatherForecast hour = weather.hourly().get(i);
          return new WeatherRecord(
              LocalDateTime.ofEpochSecond(hour.dt(), 0, timezone),
              new WeatherCondition(
                  hour.weather().get(0).id(),
                  hour.weather().get(0).description(),
                  round(hour.temp())
              )
          );
        }).collect(Collectors.toUnmodifiableList());

    return Optional.of(
        new WeatherForecast(
            location,
            LocalDateTime.ofEpochSecond(currentDay.dt(), 0, timezone),
            alerts,
            new WeatherCondition(weatherId, weatherCondition, 0),
            round(currentDay.temp().max()),
            round(currentDay.temp().min()),
            currentDay.humidity(),
            LocalDateTime.ofEpochSecond(currentDay.sunrise(), 0, timezone),
            LocalDateTime.ofEpochSecond(currentDay.sunset(), 0, timezone),
            records
        )
    );
  }
}
