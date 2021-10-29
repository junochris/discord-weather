package com.weather.discordweather.model;

public record WeatherAlert(
    String event,
    String start,
    String end
) {}
