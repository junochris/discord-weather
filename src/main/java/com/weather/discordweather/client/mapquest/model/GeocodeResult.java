package com.weather.discordweather.client.mapquest.model;

import java.util.List;

public record GeocodeResult(
    List<Geolocation> locations
) {}
