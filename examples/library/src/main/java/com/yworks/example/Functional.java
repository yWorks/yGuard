package com.yworks.example;

import java.util.List;

/**
 * Offers functional utilities for folding lists.
 * @deprecated Use {@link java.util.stream.Stream#reduce} instead.
 */
@Deprecated
public class Functional {
  protected Double fold(Reducible<Double> func, List<Double> list) {
    return foldImpl(func, list, 0);
  }

  private Double foldImpl(Reducible<Double> func, List<Double> list, int idx ) {
    if (idx < list.size()) {
      return func.call(list.get(idx), foldImpl(func, list, idx + 1));
    } else {
      return Double.valueOf(0);
    }
  }

  protected Double sum(List<Double> list) {
    return fold(new SumReducible(), list);
  }

  private static class SumReducible implements Reducible<Double> {
    public Double call(Double a, Double b) {
      return Double.valueOf(addDouble(a.doubleValue(), b.doubleValue()));
    }

    private double addDouble(double a, double b) {
      return a+b;
    }
  }
}
