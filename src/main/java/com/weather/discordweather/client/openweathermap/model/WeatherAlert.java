package com.weather.discordweather.client.openweathermap.model;

import java.util.List;

public record WeatherAlert(
    String senderName,
    String event,
    int start,
    int end,
    String description,
    List<String> tags
) {}