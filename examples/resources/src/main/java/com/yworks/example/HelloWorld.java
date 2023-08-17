package com.yworks.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Demonstrates different approaches to resource and class loading.
 */
public class HelloWorld {
  public static void main( String[] args ) {
    HelloWorld helloWorld = new HelloWorld();
    helloWorld.printMessage();
    helloWorld.loadPlugin();
  }


  /**
   * Loads a resource file from a hard-coded paths.
   */
  private void printMessage() {
    String resourcePath = "/com/yworks/example/resources/HelloWorldMessage.txt";
    System.out.println("MESSAGE from resource file: " + readText(resourcePath));
  }

  private String readText( String resourcePath ) {
    try {
      return readTextImpl(getClass().getResourceAsStream(resourcePath));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.toString());
    }
  }

  private String readTextImpl( InputStream is ) throws IOException {
    StringBuilder sb = new StringBuilder();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String delimiter = "";
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(delimiter).append(line);
        delimiter = "\n";
      }
    }

    return sb.toString();
  }

  /**
   * Loads and instantiates a class whose qualified name is specified in a
   * property resource bundle.
   */
  private void loadPlugin() {
    ResourceBundle resources = ResourceBundle.getBundle(getClass().getName());
    String qualifiedName = resources.getString("PLUGIN");

    Object plugin = newInstance(qualifiedName);
    System.out.println("PLUGIN from properties file: " + plugin.toString());
  }

  private Object newInstance( String qualifiedName ) {
    try {
      Class<?> type = Class.forName(qualifiedName);
      return type.newInstance();
    } catch (ClassNotFoundException | InstantiationException  | IllegalAccessException e) {
      String message = "Could not create a new instance of " + qualifiedName + '.';
      throw new RuntimeException(message);
    }
  }
}
