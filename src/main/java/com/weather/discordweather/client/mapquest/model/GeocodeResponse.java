package com.weather.discordweather.client.mapquest.model;

import java.util.List;

public record GeocodeResponse(
    List<GeocodeResult> results
) {}
