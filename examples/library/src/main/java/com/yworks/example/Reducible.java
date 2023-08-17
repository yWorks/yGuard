package com.yworks.example;

/**
 * A a binary operator for two {@link Double} values.
 * @param <T> the type of the operator's operands and return value.
 * @deprecated Use {@link java.util.stream.Stream#reduce} instead.
 */
@Deprecated
public interface Reducible<T> {
  public T call(T a, T b);
}
