package com.weather.discordweather;

import com.weather.discordweather.client.mapquest.converter.GeocodeResponseMapper;
import com.weather.discordweather.client.mapquest.model.Coordinate;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.GeocodeResult;
import com.weather.discordweather.client.mapquest.model.Geolocation;
import com.weather.discordweather.client.openweathermap.converter.OneCallResponseMapper;
import com.weather.discordweather.client.openweathermap.model.CurrentWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.client.openweathermap.model.TemperatureForecast;
import com.weather.discordweather.client.openweathermap.model.WeatherCondition;
import com.weather.discordweather.controller.WeatherController;
import com.weather.discordweather.gateway.WeatherForecastGateway;
import com.weather.discordweather.model.WeatherForecast;
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
  private WeatherForecastGateway gateway;

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
      var controller = new WeatherController(gateway);
      var missingLat = controller.weather(null, -100.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLat.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is missing")
    public void getWeatherMissingLong() {
      var controller = new WeatherController(gateway);
      var missingLong = controller.weather(33.0, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLong.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are missing")
    public void getWeatherMissingBoth() {
      var controller = new WeatherController(gateway);
      var missingBoth = controller.weather(null, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingBoth.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the latitude is invalid")
    public void getWeatherInvalidLatitude() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(91.0, 150.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is invalid")
    public void getWeatherInvalidLongitude() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(50.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are invalid")
    public void getWeatherInvalidBoth() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(100.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns OK if latitude and longitude are valid")
    public void getWeatherValid() {
      OneCallResponse callResponse = new OneCallResponse(
          1,
          new CurrentWeatherForecast(1635447600, 1635429842, 1635469291, 63.9f, 59,
              List.of(new WeatherCondition("Clear", "clear sky")),
              Collections.emptyList()),
          Collections.emptyList(),
          List.of(new DailyWeatherForecast(1635447600,
              1635429842,
              1635469291,
              new TemperatureForecast(76.93f, 63.9f),
              36,
              List.of(new WeatherCondition("Clear", "clear sky")),
              Optional.of(Collections.emptyList()))));
      Optional<WeatherForecast> forecast = OneCallResponseMapper.convert(callResponse, "Detroit, MI");
      assertThat(forecast).isNotEmpty();
      Mockito.when(gateway.getWeatherForecast(33.0, 80.0)).thenReturn(forecast);
      var controller = new WeatherController(gateway);
      var response = controller.weather(33.0, 80.0);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(forecast.get());
    }
  }

  @Nested
  @DisplayName("reverseGeocode")
  public class ReverseGeocodeSuite {
    @Test
    @DisplayName("returns an error if latitude is missing")
    public void reverseGeocodeMissingLat() {
      var controller = new WeatherController(gateway);
      var missingLat = controller.weather(null, -100.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLat.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is missing")
    public void reverseGeocodeMissingLong() {
      var controller = new WeatherController(gateway);
      var missingLong = controller.weather(33.0, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLong.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are missing")
    public void reverseGeocodeMissingBoth() {
      var controller = new WeatherController(gateway);
      var missingBoth = controller.weather(null, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingBoth.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the latitude is invalid")
    public void reverseGeocodeInvalidLatitude() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(91.0, 150.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if the longitude is invalid")
    public void reverseGeocodeInvalidLongitude() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(50.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns an error if both latitude and longitude are invalid")
    public void reverseGeocodeInvalidBoth() {
      var controller = new WeatherController(gateway);
      var response = controller.weather(100.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns OK if latitude and longitude are valid")
    public void reverseGeocodeValid() {
      GeocodeResponse geocodeResponse = new GeocodeResponse(
          List.of(new GeocodeResult(
              List.of(new Geolocation(
                  "Detroit",
                  "MI",
                  "US",
                  new Coordinate(33.0f, 80.0f)))
          ))
      );
      Optional<com.weather.discordweather.model.Geolocation> location = GeocodeResponseMapper.convert(geocodeResponse);
      assertThat(location).isNotEmpty();
      Mockito.when(gateway.reverseGeocode(33.0, 80.0)).thenReturn(location);
      var controller = new WeatherController(gateway);
      var response = controller.reverseGeocode(33.0, 80.0);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(location.get());
    }
  }
}
