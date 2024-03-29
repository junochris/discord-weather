package com.weather.discordweather.controller;

import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.formatter.WeatherForecastFormatter;
import com.weather.discordweather.gateway.WeatherForecastGateway;
import com.weather.discordweather.model.WeatherForecast;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

  private final WeatherForecastGateway gateway;

  @Inject
  public WeatherController(
      WeatherForecastGateway gateway
  ) {
    this.gateway = gateway;
  }

  @GetMapping("/weather")
  public ResponseEntity<WeatherForecast> weather(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon,
      @RequestParam(value = "location", required = false) String location
  ) {
    if (isInvalidInputs(lat, lon, location)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    return gateway
        .getWeatherForecast(lat, lon, location)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
  }

  @PostMapping("/weather")
  public ResponseEntity<String> postWeather(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon,
      @RequestParam(value = "location", required = false) String location
  ) {
    if (isInvalidInputs(lat, lon, location)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    return gateway.getWeatherForecast(lat, lon, location)
        .map(weatherForecast -> ResponseEntity.ok(
            gateway.executeWebhook(WeatherForecastFormatter.toDiscordString(weatherForecast))))
        .orElseGet(() -> ResponseEntity.ok(null));
  }

  @GetMapping("/geocode/forward")
  public ResponseEntity<GeocodeResponse> forwardGeocode(
      @RequestParam(value = "location", required = false) String location
  ) {
    return location.isEmpty()
        ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        : ResponseEntity.ok(gateway.forwardGeocode(location));
  }

  @GetMapping("/geocode/reverse")
  public ResponseEntity<GeocodeResponse> reverseGeocode(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon
  ) {
    return isInvalidLatitude(lat) || isInvalidLongitude(lon)
        ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        : ResponseEntity.ok(gateway.reverseGeocode(lat, lon));
  }

  private boolean isInvalidInputs(Double lat, Double lon, String location) {
    return areAllInputsIncluded(lat, lon, location) || isInvalidCoordinatesAndLocation(
        lat,
        lon,
        location);
  }

  private boolean areAllInputsIncluded(Double lat, Double lon, String location) {
    return lat != null && lon != null && !location.isEmpty();
  }

  private boolean isInvalidCoordinatesAndLocation(Double lat, Double lon, String location) {
    return (isInvalidLatitude(lat) || isInvalidLongitude(lon)) && location.isEmpty();
  }

  private boolean isInvalidLatitude(Double lat) {
    return lat == null || lat > 90 || lat < -90;
  }

  private boolean isInvalidLongitude(Double lon) {
    return lon == null || lon > 180 || lon < -180;
  }
}
