package com.weather.discordweather.model;

import java.time.LocalDateTime;

public record WeatherAlert(
    String event,
    LocalDateTime start,
    LocalDateTime end
) {}
