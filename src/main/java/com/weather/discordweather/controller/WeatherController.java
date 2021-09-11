package com.weather.discordweather.controller;

import com.weather.discordweather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;

@RestController
public class WeatherController {
    private WeatherService service;

    @Inject
    public WeatherController(WeatherService ws) {
       service = ws;
    }

    @GetMapping("/weather")
    public ResponseEntity<String> weather(@RequestParam(value = "lat", required = false) Double lat,
                          @RequestParam(value = "lon", required = false) Double lon) {
        if (lat == null || lon == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Put in lat and lon u heathen.");
        } else {
            return ResponseEntity.ok(service.getWeather(lat, lon));
        }
    }
}
