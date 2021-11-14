package com.weather.discordweather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class SpringConfiguration {

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newHttpClient();
  }
}
