package com.yworks.example;

import java.util.List;

/**
 * Offers functional utilities based on list folding
 * 
*@param <T> - a number type to fold
 * @deprecated You should use java.util.functional and stream.reduce in Java 8
 */
@Deprecated
public class Functional<T extends Number> {
  protected T fold( Reducible<T> func, List<T> list) {
    if (list.size() > 1) {
      return func.call(list.get(0), fold(func, list.subList(1, list.size() - 1)));
    } else {
      return list.get(0);
    }
  }

  // This will be obfuscated
  private double addDouble(double a, double b) {
    return a+b;
  }

  protected T sum(List<T> list) {
    class SumReducible<T extends Number> implements Reducible<T> {
      public T call(T a, T b) {
        Double sum = addDouble(a.doubleValue(), b.doubleValue());
        return (T) sum;
      }
    }

    return fold(new SumReducible<T>(), list);
  }
}
