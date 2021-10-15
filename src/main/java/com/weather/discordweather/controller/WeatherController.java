package com.weather.discordweather.controller;

import com.weather.discordweather.client.OpenWeatherMapClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class WeatherController {
  private OpenWeatherMapClient service;

  @Inject
  public WeatherController(OpenWeatherMapClient ws) {
    service = ws;
  }

  @GetMapping("/weather")
  public ResponseEntity<String> weather(
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon) {
    if (lat == null || lon == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Put in lat and lon u heathen.");
    } else if (lat > 90 || lat < -90) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid latitude");
    } else if (lon > 180 || lon < -180) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid longitude");
    } else {
      return ResponseEntity.ok(service.getWeather(lat, lon));
    }
  }
}
