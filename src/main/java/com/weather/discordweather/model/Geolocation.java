package com.weather.discordweather.model;

public record Geolocation(
    String city,
    String state,
    String country,
    float lat,
    float lon
) {}
