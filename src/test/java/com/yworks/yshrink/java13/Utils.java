package com.yworks.yshrink.java13;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods.
 *
 * @author Thomas Behr
 */
class Utils {
  private Utils() {
  }


  static <T> List<T> listOf( T item ) {
    final ArrayList<T> list = new ArrayList<T>(1);
    list.add(item);
    return list;
  }
}
