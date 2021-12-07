package com.weather.discordweather.formatter;

import com.weather.discordweather.model.WeatherAlert;
import com.weather.discordweather.model.WeatherCondition;
import com.weather.discordweather.model.WeatherEmoji;
import com.weather.discordweather.model.WeatherForecast;
import com.weather.discordweather.model.WeatherRecord;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WeatherForecastFormatter {

  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a");
  private static final Map<Integer, WeatherEmoji> atmosphereEmojis = Map.ofEntries(
      Map.entry(711, WeatherEmoji.SMOKING),
      Map.entry(762, WeatherEmoji.VOLCANO),
      Map.entry(771, WeatherEmoji.WIND_BLOWING_FACE),
      Map.entry(781, WeatherEmoji.CLOUD_TORNADO)
  );

  public static String toDiscordString(WeatherForecast forecast) {
    // Build a String to be posted into Discord

    StringBuilder discordString = new StringBuilder();
    discordString.append(addSeparator());
    discordString.append(getFormattedLogistics(forecast));

    forecast.alerts().forEach(alert -> discordString.append(getFormattedAlert(alert)));

    discordString.append(getFormattedDailyForecast(forecast));

    forecast
        .weatherRecords()
        .stream()
        .map(weatherRecord -> getFormattedHourlyForecast(weatherRecord, forecast))
        .forEach(discordString::append);

    forecast.alerts().forEach(alert -> discordString.append(getFormattedAlertDescription(alert)));

    discordString.append(addSeparator());
    return discordString.toString();
  }

  private static String addSeparator() {
    return "===========================\n";
  }

  /**
   * Returns a String with the formatted location and date.
   *
   * @param forecast Forecast for the day
   * @return String in the format:
   * Location # Detroit, MI
   * Date     # Sat Nov 6
   */
  private static String getFormattedLogistics(WeatherForecast forecast) {
    return String.format(
        """
            :earth_americas: `%s`
            :date: `%s`

            """,
        forecast.location(),
        forecast.date().format(DateTimeFormatter.ofPattern("EE MMM d"))
    );
  }

  /**
   * Returns a String with the formatted alert.
   *
   * @param alert Weather alert
   * @return String in the format:
   * Warning    # Heat Advisory
   * Time Range # 12:00 PM - 3:00 PM
   */
  private static String getFormattedAlert(WeatherAlert alert) {
    return String.format(
        """
            :warning: `%s`
            `%s - %s`

            """,
        alert.event(),
        alert.start().format(timeFormat),
        alert.end().format(timeFormat)
    );
  }

  private static String getFormattedAlertDescription(WeatherAlert alert) {
    return String.format(
        """
            
            '%s'
            """,
        alert.description()
    );
  }

  /**
   * Return a String with the weather for the day.
   *
   * @param forecast Forecast for the day
   * @return A String in the format:
   * Weather Description    # Clear sky
   * High Temp. / Low Temp. # 90F / 60F
   * Humidity               # 80%
   * Sunrise                # 6:00 AM
   * Sunset                 # 7:00 PM
   */
  private static String getFormattedDailyForecast(WeatherForecast forecast) {
    return String.format(
        """
            %s `%s`
            :thermometer: `%d\u00B0F / %d\u00B0F`
            :sweat_drops: `%d%%`
            :sunrise: `%s`
            :city_dusk: `%s`

            """,
        getWeatherEmoji(forecast.condition(), true, 0.0f),
        forecast.condition().description(),
        forecast.highTemp(),
        forecast.lowTemp(),
        forecast.humidity(),
        forecast.sunrise().format(timeFormat),
        forecast.sunset().format(timeFormat)
    );
  }

  /**
   * Returns a String with the forecast for the hour.
   *
   * @param record Record containing the forecast for the hour
   * @param forecast The forecast for the day
   * @return A String in the format:
   * Time | Temp. #  6 AM | 60F
   *
   * The time is padded to align single digit times with double-digit times.
   */
  private static String getFormattedHourlyForecast(WeatherRecord record, WeatherForecast forecast) {
    return String.format(
        """
            %s `%5s | %d\u00B0F`
            """,
        getWeatherEmoji(
            record.condition(),
            record.time().isAfter(forecast.sunrise())
                && record.time().isBefore(forecast.sunset())
                || record.time().isAfter(forecast.sunrise().plusHours(24)),
            forecast.moonPhase()
        ),
        record.time().format(DateTimeFormatter.ofPattern("h a")),
        record.condition().temperature()
    );
  }

  /**
   * Return the appropriate moon emoji based on the phase
   *
   * @param moonPhase Moon phase based on One Call API response
   * @return A String representing the Discord emoji
   */
  private static String getMoonEmoji(float moonPhase) {
    if (moonPhase == 0.0f || moonPhase == 1.0f) {
      return WeatherEmoji.NEW_MOON.getEmoji();
    }
    if (moonPhase > 0.0f && moonPhase < 0.25f) {
      return WeatherEmoji.WAXING_CRESCENT_MOON.getEmoji();
    }
    if (moonPhase == 0.25f) {
      return WeatherEmoji.FIRST_QUARTER_MOON.getEmoji();
    }
    if (moonPhase > 0.25f && moonPhase < 0.5f) {
      return WeatherEmoji.WAXING_GIBBOUS_MOON.getEmoji();
    }
    if (moonPhase == 0.5f) {
      return WeatherEmoji.FULL_MOON.getEmoji();
    }
    if (moonPhase > 0.5f && moonPhase < 0.75f) {
      return WeatherEmoji.WANING_GIBBOUS_MOON.getEmoji();
    }
    if (moonPhase == 0.75f) {
      return WeatherEmoji.LAST_QUARTER_MOON.getEmoji();
    }
    return WeatherEmoji.WANING_CRESCENT_MOON.getEmoji();
  }

  /**
   * Parses the ID contained in {weather} to return the appropriate weather emoji.
   * Taken from https://openweathermap.org/weather-conditions#How-to-get-icon-URL.
   *
   * @param weather WeatherCondition to base the emoji off of
   * @param isDaytime Boolean to determine whether to use a daytime or nighttime emoji
   * @param moonPhase If nighttime, used to get the appropriate moon emoji
   * @return A String representation of the Discord emoji
   */
  private static String getWeatherEmoji(
      WeatherCondition weather,
      boolean isDaytime,
      float moonPhase
  ) {
    // Thunderstorm
    if (weather.id() >= 200 && weather.id() < 300) {
      return weather.id() >= 210 && weather.id() < 230
          ? WeatherEmoji.CLOUD_LIGHTNING.getEmoji()
          : WeatherEmoji.THUNDER_CLOUD_RAIN.getEmoji();
    }
    // Drizzle or rain
    if (weather.id() >= 300 && weather.id() < 600) {
      return WeatherEmoji.CLOUD_RAIN.getEmoji();
    }
    // Snow
    if (weather.id() >= 600 && weather.id() < 700) {
      return WeatherEmoji.CLOUD_WITH_SNOW.getEmoji();
    }
    // Atmosphere codes
    if (weather.id() >= 700 && weather.id() < 800) {
      return atmosphereEmojis.getOrDefault(weather.id(), WeatherEmoji.FOG).getEmoji();
    }
    // Clouds
    if (weather.id() > 800) {
      if (weather.description().equalsIgnoreCase("scattered clouds")) {
        return isDaytime ? WeatherEmoji.WHITE_SUN_CLOUD.getEmoji()
            : WeatherEmoji.FACE_IN_CLOUDS.getEmoji();
      }
      if (weather.description().equalsIgnoreCase("broken clouds") ||
          weather.description().equalsIgnoreCase("overcast clouds")) {
        return WeatherEmoji.CLOUD.getEmoji();
      }
      return isDaytime ? WeatherEmoji.PARTLY_SUNNY.getEmoji()
          : WeatherEmoji.FACE_IN_CLOUDS.getEmoji();
    }

    return isDaytime ? WeatherEmoji.SUNNY.getEmoji()
        : getMoonEmoji(moonPhase);
  }
}
