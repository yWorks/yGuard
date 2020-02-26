package com.yworks.example;

public class HelloWorld {
    private static String companyName() {
      return "yWorks";
    }

    public static void main(String[] args) {
        System.out.println(String.format("Hello %s", companyName()));
    }
}
