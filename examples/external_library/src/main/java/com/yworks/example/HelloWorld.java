package com.yworks.example;

import com.google.gson.Gson;

import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {
    String document = "[\"hello\",\"world\",\"using\",\"a\",\"JSON\",\"deserializer\"]";
    Gson gson = new Gson();
    String[] elements = gson.fromJson(document, String[].class);
    System.out.println(Arrays.asList(elements));
  }
}
