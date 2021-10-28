package com.weather.discordweather.client.openweathermap.model;

import java.util.List;
import java.util.Optional;

public record HourlyWeatherForecast(
    int dt,
    float temp,
    float feels_like,
    int humidity,
    float uvi,
    Optional<Float> wind_speed,
    List<WeatherCondition> weather,
    List<WeatherAlert> alerts
) {}
