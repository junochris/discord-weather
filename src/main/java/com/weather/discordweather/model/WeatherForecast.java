package com.weather.discordweather.model;

import java.time.LocalDateTime;
import java.util.List;

public record WeatherForecast(
    String location,
    LocalDateTime date,

    List<WeatherAlert> alerts,

    WeatherCondition condition,
    int highTemp,
    int lowTemp,
    int humidity,
    LocalDateTime sunrise,
    LocalDateTime sunset,

    List<WeatherRecord> weatherRecords
) {}
