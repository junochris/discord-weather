package com.weather.discordweather.controller;

import com.weather.discordweather.gateway.WeatherForecastGateway;
import com.weather.discordweather.model.Geolocation;
import com.weather.discordweather.model.WeatherForecast;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Optional;

@RestController
public class WeatherController {
  private final WeatherForecastGateway gateway;

  @Inject
  public WeatherController(
      WeatherForecastGateway gate) {
    gateway = gate;
  }

  @GetMapping("/weather")
  public ResponseEntity<WeatherForecast> weather(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon) {
    if (lat == null || lon == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else if (lat > 90 || lat < -90) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else if (lon > 180 || lon < -180) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      Optional<WeatherForecast> forecast = gateway.getWeatherForecast(lat, lon);
      return forecast.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }
  }

  @PostMapping("/weather")
  public void weather(
      @RequestParam String forecast) {
    gateway.postContent(forecast);
  }

  @GetMapping("/geocode/forward")
    public ResponseEntity<Geolocation> forwardGeocode(
        @RequestParam(value = "location", required = false) String location) {
    if (location.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      return gateway.forwardGeocode(location).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }
  }

  @GetMapping("/geocode/reverse")
  public ResponseEntity<Geolocation> reverseGeocode(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon) {
    if (lat == null || lon == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else if (lat > 90 || lat < -90) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else if (lon > 180 || lon < -180) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      return gateway.reverseGeocode(lat, lon).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }
  }
}
