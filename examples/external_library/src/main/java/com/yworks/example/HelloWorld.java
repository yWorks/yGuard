package com.yworks.example;

import com.google.gson.Gson;

import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {
    new HelloWorld().process("[\"Hello\",\"World\",\"using\",\"a\",\"JSON\",\"deserializer\"]");
  }

  private void process(String document) {
    System.out.println(Arrays.asList(parseJson(document)));
  }

  private String[] parseJson(String document) {
    Gson gson = new Gson();
    return gson.fromJson(document, String[].class);
  }
}
