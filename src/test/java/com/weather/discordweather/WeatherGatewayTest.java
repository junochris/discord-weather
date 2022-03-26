package com.weather.discordweather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import com.weather.discordweather.client.discord.DiscordClient;
import com.weather.discordweather.client.mapquest.MapQuestClient;
import com.weather.discordweather.client.mapquest.model.Coordinate;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.GeocodeResult;
import com.weather.discordweather.client.mapquest.model.Geolocation;
import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.model.CurrentWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.client.openweathermap.model.TemperatureForecast;
import com.weather.discordweather.client.openweathermap.model.WeatherAlert;
import com.weather.discordweather.client.openweathermap.model.WeatherCondition;
import com.weather.discordweather.converter.WeatherForecastMapper;
import com.weather.discordweather.formatter.WeatherForecastFormatter;
import com.weather.discordweather.gateway.WeatherForecastGateway;
import com.weather.discordweather.model.WeatherForecast;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeatherGatewayTest {

  private AutoCloseable closeable;
  @Mock
  private DiscordClient discordClient;
  @Mock
  private MapQuestClient mapQuestClient;
  @Mock
  private OpenWeatherMapClient openWeatherMapClient;

  @BeforeEach
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  @Nested
  @DisplayName("executeWebhook")
  public class ExecuteWebhookSuite {

    @Test
    @DisplayName("verifies paginateForecast is called twice")
    public void executeWebhookLengthyForecast() {
      OneCallResponse callResponse = new OneCallResponse(
          1,
          new CurrentWeatherForecast(1635447600, 1635429842, 1635469291, 63.9f, 59,
              List.of(new WeatherCondition(800, "clear sky"))
          ),
          Collections.emptyList(),
          List.of(new DailyWeatherForecast(
              1635447600,
              1635429842,
              1635469291,
              1.0f,
              new TemperatureForecast(76.93f, 63.9f),
              36,
              List.of(new WeatherCondition(800, "clear sky"), new WeatherCondition(31, ""))
          )),
          Optional.of(List.of(
              new WeatherAlert("Winter Storm Watch", 1635429842, 1635469291, """
                  '...WINTER STORM WATCH IN EFFECT FROM LATE TONIGHT THROUGH LATE
                  THURSDAY NIGHT...
                  * WHAT...Heavy snow possible. Plan on difficult travel conditions.
                  Total snow accumulations of 3 to 7 inches, with localized
                  amounts 12 inches or more are possible. Areas of southwest
                  winds 25 to 35 mph with local gusts to 55 mph in the San
                  Bernardino Mountains.
                  * WHERE...Above 5,000 feet in the San Bernardino County
                  Mountains, Riverside County Mountains and San Diego County
                  Mountains.
                  * WHEN...From late tonight through late Thursday night.
                  * ADDITIONAL DETAILS...Significant reductions in visibility are
                  possible.'
                  """),
              new WeatherAlert("Flood Watch", 1635429842, 1635469291, """
                  '...FLOOD WATCH REMAINS IN EFFECT THROUGH THURSDAY AFTERNOON...
                  * WHAT...Flooding caused by excessive rainfall possible.
                  * WHERE...A portion of Southern California, including the following
                  areas, Orange County Coastal Areas, Orange County Inland Areas,
                  Riverside County Mountains, San Bernardino County Mountains, San
                  Bernardino and Riverside County Valleys-The Inland Empire, San
                  Diego County Coastal Areas, San Diego County Mountains, San Diego
                  County Valleys, San Gorgonio Pass Near Banning and Santa Ana
                  Mountains and Foothills.
                  * WHEN...Through Thursday afternoon.
                  * IMPACTS...Excessive runoff may result in flooding of small creeks,
                  streams, and low-lying and flood-prone locations. Flooding may
                  occur in poor drainage and urban areas. Flash flooding and debris
                  flows are possible, especially near the recent burn scars.
                  * ADDITIONAL DETAILS...
                  - The most likely time for locally heavy rainfall is tonight
                  into early Thursday with the most likely area somewhere from
                  the coast to the mountains for Orange and southwestern San
                  Bernardino Counties westward. There is the potential for a
                  portion of this area or areas nearby to receive several hours
                  of locally heavy rainfall for tonight into early Thursday. In
                  the mountains, snow melt could add to the runoff below 5500
                  to 6000 feet. Rainfall for Orange, southwestern San
                  Bernardino Counties is expected to range from around 2 inches
                  at
                  the coast to 5 to 7 inches in the mountains with locally
                  greater
                  amounts. This will transition to 1 to 2 inch at the coast to
                  1.5
                  to 2 inches in the mountains for the southern half of San
                  Diego
                  County.
                  - http://www.weather.gov/safety/flood'
                  """))
          ));

      GeocodeResponse geocodeResponse = new GeocodeResponse(
          List.of(new GeocodeResult(
              List.of(new Geolocation(
                  "Detroit",
                  "MI",
                  "US",
                  new Coordinate(100.0, -100.0)
              ))
          ))
      );

      Optional<WeatherForecast> forecast = WeatherForecastMapper.fromOpenWeatherMapAndMapQuest(
          callResponse,
          geocodeResponse
      );

      Mockito.when(discordClient.executeWebhook(anyString(), anyString(), anyString())).thenReturn("");

      assertThat(forecast).isNotEmpty();
      String formattedForecast = WeatherForecastFormatter.toDiscordString(forecast.get());
      var gateway = new WeatherForecastGateway(discordClient, mapQuestClient, openWeatherMapClient);
      gateway.executeWebhook(formattedForecast);
      Mockito.verify(discordClient, times(2)).executeWebhook(anyString(), anyString(), anyString());
    }
  }
}
