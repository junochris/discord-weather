package com.weather.discordweather;

import com.weather.discordweather.controller.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiscordWeatherApplicationTests {

  @Autowired
  private WeatherController controller;

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }
}
