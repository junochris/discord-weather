package com.weather.discordweather.client.openweathermap.converter;

import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.HourlyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.model.WeatherCondition;
import com.weather.discordweather.model.WeatherForecast;
import com.weather.discordweather.model.WeatherRecord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OneCallResponseMapper {
  public static Optional<WeatherForecast> convert(OneCallResponse response) {
    if (!response.daily().isEmpty()) {
      DailyWeatherForecast currentDay = response.daily().get(0);
      ZoneOffset timezone = ZoneOffset.ofTotalSeconds(response.timezone_offset());

      // Collect alerts
      List<com.weather.discordweather.model.WeatherAlert> alerts = Collections.emptyList();
      if (currentDay.alerts().isPresent()) {
        alerts = currentDay.alerts().get().stream().map(alert -> {
          LocalDateTime startTime = LocalDateTime.ofEpochSecond(alert.start(), 0, timezone);
          LocalDateTime endTime = LocalDateTime.ofEpochSecond(alert.end(), 0, timezone);
          return new com.weather.discordweather.model.WeatherAlert(
              alert.event(),
              startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
              endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
          );
        }).collect(Collectors.toList());
      }

      String weatherCondition = "";
      if (currentDay.weather().size() > 0) {
        weatherCondition = currentDay.weather().get(0).description();
      }

      // Collect the next 6 temperatures in 3 hour intervals.
      List<WeatherRecord> records = IntStream
          .range(0, 18)
          .filter(i -> i % 3 == 0 && i < response.hourly().size())
          .mapToObj(i -> {
            HourlyWeatherForecast hour = response.hourly().get(i);
            return new WeatherRecord(
                LocalDateTime.ofEpochSecond(hour.dt(), 0, timezone),
                new WeatherCondition(hour.weather().get(0).main(), hour.temp())
            );
          }).collect(Collectors.toList());

      return Optional.of(new WeatherForecast(
          "",
          LocalDateTime.ofEpochSecond(currentDay.dt(), 0, timezone),
          Optional.of(alerts),
          weatherCondition,
          currentDay.temp().max(),
          currentDay.temp().min(),
          currentDay.humidity(),
          LocalDateTime.ofEpochSecond(currentDay.sunrise(), 0, timezone),
          LocalDateTime.ofEpochSecond(currentDay.sunset(), 0, timezone),
          records
      ));
    }

    return Optional.empty();
  }
}
