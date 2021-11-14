package com.weather.discordweather.client.mapquest.model;

public record Geolocation(
    String adminArea5,
    String adminArea3,
    String adminArea1,
    Coordinate latLng
) {}
