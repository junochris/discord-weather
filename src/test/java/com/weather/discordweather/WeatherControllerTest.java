package com.weather.discordweather;

import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.model.CurrentWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.client.openweathermap.model.WeatherAlert;
import com.weather.discordweather.client.openweathermap.model.WeatherCondition;
import com.weather.discordweather.controller.WeatherController;
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
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

  private AutoCloseable closeable;
  @Mock
  private OpenWeatherMapClient client;

  @BeforeEach
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  @Nested
  @DisplayName("getWeather")
  public class GetWeatherSuite {
    @Test
    @DisplayName("returns an error if the latitude is missing")
    public void getWeatherMissingLat() {
      var controller = new WeatherController(client);
      var missingLat = controller.weather(null, -100.0);
      Mockito.verifyNoInteractions(client);
      assertThat(missingLat.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is missing")
    public void getWeatherMissingLong() {
      var controller = new WeatherController(client);
      var missingLong = controller.weather(33.0, null);
      Mockito.verifyNoInteractions(client);
      assertThat(missingLong.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are missing")
    public void getWeatherMissingBoth() {
      var controller = new WeatherController(client);
      var missingBoth = controller.weather(null, null);
      Mockito.verifyNoInteractions(client);
      assertThat(missingBoth.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the latitude is invalid")
    public void getWeatherInvalidLatitude() {
      var controller = new WeatherController(client);
      var response = controller.weather(91.0, 150.0);
      Mockito.verifyNoInteractions(client);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is invalid")
    public void getWeatherInvalidLongitude() {
      var controller = new WeatherController(client);
      var response = controller.weather(50.0, 190.0);
      Mockito.verifyNoInteractions(client);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are invalid")
    public void getWeatherInvalidBoth() {
      var controller = new WeatherController(client);
      var response = controller.weather(100.0, 190.0);
      Mockito.verifyNoInteractions(client);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns OK if latitude and longitude are valid")
    public void getWeatherValid() {
      OneCallResponse callResponse = new OneCallResponse(
          1,
          new CurrentWeatherForecast(1, 1, 1, 1.0f, 1.0f, 1, 1, Optional.of(1.0f),
              List.of(new WeatherCondition("Valid")),
              List.of(new WeatherAlert("Name", "Event", 1, 1, "Test", Collections.emptyList()))),
          Collections.emptyList(), Collections.emptyList());
      Mockito.when(client.getWeather(33.0, 80.0)).thenReturn(callResponse);
      var controller = new WeatherController(client);
      var response = controller.weather(33.0, 80.0);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(callResponse);
    }
  }
}
