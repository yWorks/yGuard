package com.yworks.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HelloWorld {
  public static void main(String args[]) {
    HelloWorld helloWorld = new HelloWorld();
    String propertiesFileName = String.format("%s.properties", helloWorld.getClass().getSimpleName());

    InputStream inputStream = helloWorld.getClass().getResourceAsStream(propertiesFileName);
    if (inputStream != null) {
      Properties properties = new Properties();
      try {
        properties.load(inputStream);
        System.out.println(String.format("Printing MESSAGE from properties: %s", properties.getProperty("MESSAGE")));
      } catch (IOException exception) {
        System.err.println(exception.toString());
      }
    } else {
      System.err.println(String.format("Could not load file %s", propertiesFileName));
    }
  }
}
