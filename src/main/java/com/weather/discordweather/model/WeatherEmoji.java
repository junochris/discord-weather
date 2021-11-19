package com.weather.discordweather.model;

public enum WeatherEmoji {
  CLOUD(":cloud:"),
  CLOUD_LIGHTNING(":cloud_lightning:"),
  CLOUD_RAIN(":cloud_rain:"),
  CLOUD_TORNADO(":cloud_tornado:"),
  CLOUD_WITH_SNOW(":cloud_with_snow:"),
  FACE_IN_CLOUDS(":face_in_clouds:"),
  FIRST_QUARTER_MOON(":first_quarter_moon:"),
  FOG(":fog:"),
  FULL_MOON(":full_moon:"),
  LAST_QUARTER_MOON(":last_quarter_moon:"),
  NEW_MOON(":new_moon:"),
  PARTLY_SUNNY(":partly_sunny:"),
  SMOKING(":smoking:"),
  SUNNY(":sunny:"),
  THUNDER_CLOUD_RAIN(":thunder_cloud_rain:"),
  VOLCANO(":volcano:"),
  WANING_CRESCENT_MOON(":waning_crescent_moon:"),
  WANING_GIBBOUS_MOON(":waning_gibbous_moon:"),
  WAXING_CRESCENT_MOON(":waxing_crescent_moon:"),
  WAXING_GIBBOUS_MOON(":waxing_gibbous_moon:"),
  WHITE_SUN_CLOUD(":white_sun_cloud:"),
  WIND_BLOWING_FACE(":wind_blowing_face:");

  private final String emoji;

  WeatherEmoji(String emoji) {
    this.emoji = emoji;
  }

  public String getEmoji() {
    return emoji;
  }
}
