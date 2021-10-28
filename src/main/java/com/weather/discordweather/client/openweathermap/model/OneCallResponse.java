package com.weather.discordweather.client.openweathermap.model;

import java.util.List;

public record OneCallResponse(
    int timezone_offset,
    CurrentWeatherForecast current,
    List<HourlyWeatherForecast> hourly,
    List<DailyWeatherForecast> daily
) {}
