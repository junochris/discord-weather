package com.weather.discordweather;

import com.weather.discordweather.client.OpenWeatherMapClient;

public class MockOpenWeatherMapClient extends OpenWeatherMapClient {
  @Override
  public String getWeather(Double lat, Double lon) {
    return "{message: You're good!}";
  }
}
