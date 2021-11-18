package com.weather.discordweather.client.openweathermap.model;

import java.util.List;

public record CurrentWeatherForecast(
    int dt,
    int sunrise,
    int sunset,
    float temp,
    int humidity,
    List<WeatherCondition> weather
) {}
