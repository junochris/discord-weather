package com.weather.discordweather.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

  public static String getFileContents(String file) {
    try {
      return Files.readString(Path.of(file));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
