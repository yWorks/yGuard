package test.generics;

import java.util.List;


/**
 * The type Generic outer non generic inner.
 *
 * @param <T> the type parameter
 */
public class GenericOuterNonGenericInner<T extends List<BlaClass.Inner<String>>> {

  /**
   * Foo.
   *
   * @param b the b
   * @param c the c
   */
  public void foo( Inner b, Inner.InnerInner c ) {
    System.out.println("bla: "+b);
  }

  /**
   * The type Inner.
   */
  public class Inner {

    /**
     * The type Inner inner.
     */
    public class InnerInner {

    }

  }

}
