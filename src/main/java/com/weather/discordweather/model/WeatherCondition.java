package com.weather.discordweather.model;

public record WeatherCondition(
    int id,
    String description,
    int temperature
) {}
