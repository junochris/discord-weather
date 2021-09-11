package com.weather.discordweather.openweathermap;

import java.util.List;

public class WeatherEntry {
    private int datetime;
    private int sunrise;
    private int sunset;
    private float temp;
    private float feelsLike;
    private int humidity;
    private int uvi;
    private float windSpeed;
    private List<WeatherCondition> weather;
}
