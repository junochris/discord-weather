package com.weather.discordweather.client.openweathermap.model;

import java.util.List;
import java.util.Optional;

public record DailyWeatherForecast(
    int dt,
    int sunrise,
    int sunset,
    TemperatureForecast temp,
    TemperatureForecast feels_like,
    int humidity,
    float uvi,
    Optional<Float> wind_speed,
    List<WeatherCondition> weather,
    List<WeatherAlert> alerts
) {}
