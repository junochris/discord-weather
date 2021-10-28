package com.weather.discordweather.model;

import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.HourlyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.client.openweathermap.model.WeatherAlert;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


public class DiscordWeatherForecastConverter {
  public static DiscordWeatherForecast convert(OneCallResponse response) {
    if (response.daily().size() > 0) {
      DailyWeatherForecast currentDay = response.daily().get(0);
      ZoneOffset timezone = ZoneOffset.ofTotalSeconds(response.timezone_offset());
      LocalDateTime date = LocalDateTime.ofEpochSecond(currentDay.dt(), 0, timezone);
      OffsetDateTime offsetDate = date.atOffset(timezone);

      // Collect alerts
      List<DiscordWeatherAlert> alerts = Collections.emptyList();
      if (currentDay.alerts() != null) {
        for (WeatherAlert alert : currentDay.alerts()) {
          LocalDateTime startTime = LocalDateTime.ofEpochSecond(alert.start(), 0, timezone);
          LocalDateTime endTime = LocalDateTime.ofEpochSecond(alert.end(), 0, timezone);
          DiscordWeatherAlert discordWeatherAlert = new DiscordWeatherAlert(
              alert.event(),
              startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
              endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
          );
          alerts.add(discordWeatherAlert);
        }
      }

      String weatherCondition = "";
      if (currentDay.weather().size() > 0) {
        weatherCondition = currentDay.weather().get(0).description();
      }

      // Collect temperatures at specified times.
      float temp6Am = 0f;
      float temp9Am = 0f;
      float temp12Pm = 0f;
      float temp3Pm = 0f;
      float temp6Pm = 0f;
      float temp9Pm = 0f;
      for (HourlyWeatherForecast hour : response.hourly()) {
        // OpenWeatherMap returns the datetime at noon for daily forecasts.
        if (offsetDate.minusHours(6).toEpochSecond() == hour.dt()) {
          temp6Am = hour.temp();
        } else if (offsetDate.minusHours(3).toEpochSecond() == hour.dt()) {
          temp9Am = hour.temp();
        } else if (offsetDate.toEpochSecond() == hour.dt()) {
          temp12Pm = hour.temp();
        } else if (offsetDate.plusHours(3).toEpochSecond() == hour.dt()) {
          temp3Pm = hour.temp();
        } else if (offsetDate.plusHours(6).toEpochSecond() == hour.dt()) {
          temp6Pm = hour.temp();
        } else if (offsetDate.plusHours(9).toEpochSecond() == hour.dt()) {
          temp9Pm = hour.temp();
        }
      }

      DiscordWeatherForecast forecast = new DiscordWeatherForecast(
          "",
          date.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
          alerts,
          weatherCondition,
          currentDay.temp().max(),
          currentDay.temp().min(),
          currentDay.humidity(),
          LocalDateTime.ofEpochSecond(currentDay.sunrise(), 0, timezone).format(DateTimeFormatter.ofPattern("h:mm a")),
          LocalDateTime.ofEpochSecond(currentDay.sunset(), 0, timezone).format(DateTimeFormatter.ofPattern("h:mm a")),
          temp6Am,
          temp9Am,
          temp12Pm,
          temp3Pm,
          temp6Pm,
          temp9Pm
      );

      return forecast;
    }

    return null;
  }
}
