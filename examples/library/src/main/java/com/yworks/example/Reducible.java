package com.yworks.example;


/**
 * A Callable of type T that takes two T as an argument and reduces them
 * 
*@param <T> - the type to apply callable to
 * @deprecated java.util.function should be used instead if Java 8 is available
 */
@Deprecated
public interface Reducible<T> {
  public T call(T a, T b);
}
