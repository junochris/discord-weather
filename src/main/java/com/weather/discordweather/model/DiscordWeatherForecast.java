package com.weather.discordweather.model;

import java.util.List;

public record DiscordWeatherForecast(
    String location,
    String date,

    List<DiscordWeatherAlert> alerts,

    String weatherCondition,
    float highTemp,
    float lowTemp,
    int humidity,
    String sunrise,
    String sunset,

    float temp6Am,
    float temp9Am,
    float temp12Pm,
    float temp3Pm,
    float temp6Pm,
    float temp9pm
) {}
