package com.yworks.example;

public class HelloWorld {
    private static String companyName() {
      return "yWorks";
    }

    public static void main(String[] args) {
        System.out.println("Hello World from " + companyName() + '.');
    }
}
