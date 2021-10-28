package com.weather.discordweather.model;

public record DiscordWeatherAlert(
    String event,
    String start,
    String end
) {}
