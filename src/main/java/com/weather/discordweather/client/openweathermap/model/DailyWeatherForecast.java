package com.weather.discordweather.client.openweathermap.model;

import java.util.List;
import java.util.Optional;

public record DailyWeatherForecast(
    int dt,
    int sunrise,
    int sunset,
    TemperatureForecast temp,
    int humidity,
    List<WeatherCondition> weather,
    Optional<List<WeatherAlert>> alerts
) {}
