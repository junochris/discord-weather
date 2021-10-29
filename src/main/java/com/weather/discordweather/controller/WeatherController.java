package com.weather.discordweather.controller;

import com.weather.discordweather.client.discord.DiscordClient;
import com.weather.discordweather.client.openweathermap.OpenWeatherMapClient;
import com.weather.discordweather.client.openweathermap.converter.OneCallResponseMapper;
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
  private final DiscordClient discordService;
  private final OpenWeatherMapClient openWeatherService;

  @Inject
  public WeatherController(
      DiscordClient ds,
      OpenWeatherMapClient ws) {
    discordService = ds;
    openWeatherService = ws;
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
      Optional<WeatherForecast> forecast = OneCallResponseMapper.convert(openWeatherService.getWeather(lat, lon));
      return forecast.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }
  }

  @PostMapping("/weather")
  public void weather(
      @RequestParam String forecast) {
    discordService.postContent(forecast);
  }
}
