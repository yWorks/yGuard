package com.yworks.example;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
  public static void main(String[] args) {
    List<Integer> integers = new ArrayList<>();
    integers.add(1);
    integers.add(2);
    integers.add(3);
    Functional<Integer> functional = new Functional<>();
    assert(functional.sum(integers) == 6);
  }
}
