package com.weather.discordweather.openweathermap;

import java.util.List;

public class OneCallResponse {
  private WeatherEntry current;
  private List<WeatherEntry> hourly;
  private List<WeatherEntry> daily;
}
