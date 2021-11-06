package com.weather.discordweather.client.mapquest.converter;

import com.weather.discordweather.client.mapquest.model.GeocodeResponse;
import com.weather.discordweather.client.mapquest.model.GeocodeResult;
import com.weather.discordweather.client.mapquest.model.Geolocation;

import java.util.Optional;

public class GeocodeResponseMapper {
  public static Optional<com.weather.discordweather.model.Geolocation> convert(GeocodeResponse response) {
    if (!response.results().isEmpty()) {
      GeocodeResult result = response.results().get(0);
      if (!result.locations().isEmpty()) {
        Geolocation geolocation = result.locations().get(0);
        return Optional.of(new com.weather.discordweather.model.Geolocation(
            geolocation.adminArea5(),
            geolocation.adminArea3(),
            geolocation.adminArea1(),
            geolocation.latLng().lat(),
            geolocation.latLng().lng()));
      }
    }

    return Optional.empty();
  }
}
