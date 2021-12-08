package com.weather.discordweather.client.openweathermap.model;

public record WeatherAlert(
    String event,
    int start,
    int end,
    String description
) {}
