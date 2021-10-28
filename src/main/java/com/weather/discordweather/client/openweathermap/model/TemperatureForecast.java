package com.weather.discordweather.client.openweathermap.model;

import java.util.Optional;

public record TemperatureForecast(
    float day,
    Optional<Float> min,
    Optional<Float> max,
    float night,
    float eve,
    float morn
) {}
