package com.weather.discordweather;

import com.weather.discordweather.client.mapquest.model.Coordinate;
import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.GeocodeResult;
import com.weather.discordweather.client.mapquest.model.Geolocation;
import com.weather.discordweather.client.openweathermap.model.CurrentWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.DailyWeatherForecast;
import com.weather.discordweather.client.openweathermap.model.OneCallResponse;
import com.weather.discordweather.client.openweathermap.model.TemperatureForecast;
import com.weather.discordweather.client.openweathermap.model.WeatherCondition;
import com.weather.discordweather.controller.WeatherController;
import com.weather.discordweather.converter.WeatherForecastMapper;
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
    @DisplayName("returns a 400 Bad Request if the latitude is missing")
    public void getWeatherMissingLat() {
      var missingLat = new WeatherController(gateway).weather(null, -100.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLat.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the longitude is missing")
    public void getWeatherMissingLong() {
      var missingLong = new WeatherController(gateway).weather(33.0, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLong.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if both latitude and longitude are missing")
    public void getWeatherMissingBoth() {
      var missingBoth = new WeatherController(gateway).weather(null, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingBoth.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the latitude is invalid")
    public void getWeatherInvalidLatitude() {
      var response = new WeatherController(gateway).weather(91.0, 150.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the longitude is invalid")
    public void getWeatherInvalidLongitude() {
      var response = new WeatherController(gateway).weather(50.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if both latitude and longitude are invalid")
    public void getWeatherInvalidBoth() {
      var response = new WeatherController(gateway).weather(100.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 200 OK if latitude and longitude are valid")
    public void getWeatherValid() {
      OneCallResponse callResponse = new OneCallResponse(
          1,
          new CurrentWeatherForecast(1635447600, 1635429842, 1635469291, 63.9f, 59,
              List.of(new WeatherCondition("Clear", "clear sky")),
              Collections.emptyList()
          ),
          Collections.emptyList(),
          List.of(new DailyWeatherForecast(
              1635447600,
              1635429842,
              1635469291,
              new TemperatureForecast(76.93f, 63.9f),
              36,
              List.of(new WeatherCondition("Clear", "clear sky")),
              Optional.of(Collections.emptyList())
          ))
      );

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
    @DisplayName("returns a 400 Bad Request if latitude is missing")
    public void reverseGeocodeMissingLat() {
      var missingLat = new WeatherController(gateway).reverseGeocode(null, -100.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLat.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the longitude is missing")
    public void reverseGeocodeMissingLong() {
      var missingLong = new WeatherController(gateway).reverseGeocode(33.0, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingLong.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if both latitude and longitude are missing")
    public void reverseGeocodeMissingBoth() {
      var missingBoth = new WeatherController(gateway).reverseGeocode(null, null);
      Mockito.verifyNoInteractions(gateway);
      assertThat(missingBoth.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the latitude is invalid")
    public void reverseGeocodeInvalidLatitude() {
      var response = new WeatherController(gateway).reverseGeocode(91.0, 150.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if the longitude is invalid")
    public void reverseGeocodeInvalidLongitude() {
      var response = new WeatherController(gateway).reverseGeocode(50.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 400 Bad Request if both latitude and longitude are invalid")
    public void reverseGeocodeInvalidBoth() {
      var response = new WeatherController(gateway).reverseGeocode(100.0, 190.0);
      Mockito.verifyNoInteractions(gateway);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("returns a 200 OK if latitude and longitude are valid")
    public void reverseGeocodeValid() {
      GeocodeResponse geocodeResponse = new GeocodeResponse(
          List.of(new GeocodeResult(
              List.of(new Geolocation(
                  "Detroit",
                  "MI",
                  "US",
                  new Coordinate(33.0f, 80.0f)
              ))
          ))
      );
      Mockito.when(gateway.reverseGeocode(33.0, 80.0)).thenReturn(geocodeResponse);
      var controller = new WeatherController(gateway);
      var response = controller.reverseGeocode(33.0, 80.0);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(geocodeResponse);
    }
  }
}
