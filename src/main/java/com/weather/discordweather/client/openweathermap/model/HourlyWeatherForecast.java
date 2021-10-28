package com.weather.discordweather.client.openweathermap.model;

import java.util.List;

public record HourlyWeatherForecast(
    int dt,
    float temp,
    int humidity,
    List<WeatherCondition> weather,
    List<WeatherAlert> alerts
) {}
