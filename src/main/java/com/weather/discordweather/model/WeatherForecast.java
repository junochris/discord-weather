package com.weather.discordweather.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record WeatherForecast(
    String location,
    LocalDateTime date,

    List<WeatherAlert> alerts,

    WeatherCondition condition,
    int highTemp,
    int lowTemp,
    int humidity,
    LocalDateTime sunrise,
    LocalDateTime sunset,

    List<WeatherRecord> weatherRecords
) {
  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");

  public String toDiscordString() {
    // Build a String to be posted into Discord

    // Location (Detroit, MI)
    // Date (Sat Nov 6)
    StringBuilder discordString = new StringBuilder();
    discordString.append(String.format(
        """
            **%s**
            %s
                        
            """,
        location,
        date.format(DateTimeFormatter.ofPattern("EE MMM d"))
    ));

    // Heat Advisory
    // 12:00 PM - 3:00 PM
    alerts.forEach(alert -> discordString.append(String.format(
        """
            :warning: %s
            %s - %s

            """,
        alert.event(),
        alert.start().format(timeFormat),
        alert.end().format(timeFormat)
    )));

    // Clear sky
    // 90F / 60F
    // 80%
    // 6:00 AM
    // 7:00 PM
    discordString.append(String.format(
        """
            %s %s
            :thermometer: %d\u00B0F / %d\u00B0F
            :sweat_drops: %d%%
            :sunrise: %s
            :city_dusk: %s

            """,
        getWeatherEmoji(condition, true),
        condition.description(),
        highTemp,
        lowTemp,
        humidity,
        sunrise.format(timeFormat),
        sunset.format(timeFormat)
    ));

    // 6 AM 60F
    // 9 AM 70F ...
    weatherRecords.forEach(weatherRecord -> discordString
        .append(String.format(
                """
                    %s `%5s | %d\u00B0F`
                    """,
                getWeatherEmoji(
                    weatherRecord.condition(),
                    weatherRecord.time().isAfter(sunrise) && weatherRecord.time()
                        .isBefore(sunset) || weatherRecord.time().isAfter(sunrise.plusHours(24))
                ),
                weatherRecord.time().format(DateTimeFormatter.ofPattern("h a")),
                weatherRecord.condition().temperature()
            )
        )
    );

    return discordString.toString();
  }

  private String getWeatherEmoji(WeatherCondition weather, boolean isDaytime) {
    // Parse weather ID. Taken from https://openweathermap.org/weather-conditions#How-to-get-icon-URL

    // Thunderstorm
    if (weather.id() >= 200 && weather.id() < 300) {
      if (weather.id() >= 210 && weather.id() < 230) {
        return ":cloud_lightning:";
      }
      return ":thunder_cloud_rain:";
    }
    // Drizzle or rain
    else if (weather.id() >= 300 && weather.id() <= 500) {
      return ":cloud_rain:";
    }
    // Snow
    else if (weather.id() >= 600 && weather.id() < 700) {
      return ":cloud_with_snow:";
    }
    // Atmosphere codes
    else if (weather.id() >= 700 && weather.id() < 800) {
      if (weather.id() == 711) {
        return ":smoking:"; // Smoke
      } else if (weather.id() == 762) {
        return ":volcano:"; // Volcanic ash
      } else if (weather.id() == 771) {
        return ":wind_blowing_face:"; // Squalls
      } else if (weather.id() == 781) {
        return ":cloud_tornado:"; // Tornado
      }
      return ":fog:";
    }
    // Clouds
    else if (weather.id() > 800) {
      if (weather.description().equalsIgnoreCase("scattered clouds")) {
        return isDaytime ? ":white_sun_cloud:" : ":face_in_clouds:";
      }
      if (weather.description().equalsIgnoreCase("broken clouds") ||
          weather.description().equalsIgnoreCase("overcast clouds")) {
        return ":cloud:";
      }
      return isDaytime ? ":partly_sunny:" : ":face_in_clouds:";
    }

    return isDaytime ? ":sunny:" : ":waning_gibbous_moon:";
  }
}
