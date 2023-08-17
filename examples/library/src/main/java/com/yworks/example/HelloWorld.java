package com.yworks.example;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
  public static void run() {
    new HelloWorld().runImpl();
  }

  private void runImpl() {
    List<Double> numbers = new ArrayList<Double>();
    numbers.add(1.0);
    numbers.add(2.0);
    numbers.add(3.0);
    Functional functional = new Functional();
    _assert(functional.sum(numbers), 6);
  }

  private static void _assert(double actual, double expected) {
    if (actual != expected) {
      throw new RuntimeException("Assertion failed: actual " + actual + " != expected " + expected);
    }
  }
}
