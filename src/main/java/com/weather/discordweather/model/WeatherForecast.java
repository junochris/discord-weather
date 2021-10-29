package com.weather.discordweather.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record WeatherForecast(
    String location,
    LocalDateTime date,

    Optional<List<WeatherAlert>> alerts,

    String weatherCondition,
    float highTemp,
    float lowTemp,
    int humidity,
    LocalDateTime sunrise,
    LocalDateTime sunset,

    List<WeatherRecord> weatherRecords
) {}
