package com.weather.discordweather.model;

import java.time.LocalDateTime;

public record WeatherRecord(
    LocalDateTime time,
    WeatherCondition condition
) {}
