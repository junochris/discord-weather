package com.weather.discordweather.service;

import com.weather.discordweather.openweathermap.OneCallResponse;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class WeatherService {
    private HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .authenticator(Authenticator.getDefault())
            .build();

    public String getWeather(Double lat, Double lon) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=minutely&appid=56fa105ab7ae0ad13f69f4587f72065c", lat, lon)))
                .build();

        String future = "";
        try {
            future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .get();
        } catch (Exception e) {

        }

        return future;
    }
}
