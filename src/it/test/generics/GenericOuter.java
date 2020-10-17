package test.generics;

import java.util.List;

/**
 * The type Generic outer.
 *
 * @param <T> the type parameter
 */
public class GenericOuter<T extends List<BlaClass.Inner<String>>> {

//  public void foo( Inner<Inner.InnerInner>.InnerInner<Inner<String>> b ) {
//    System.out.println("bla: "+b);
//  }

  /**
   * Foo.
   *
   * @param b the b
   */
  public void foo( Inner<Inner.InnerInner> b ) {
    System.out.println("bla: " + b);
  }
//
//  public void baz(BlaClass.Inner<String> a) {
//    System.out.println("baz: "+a);
//  }

//  public void moo(BlaClass.Inner<Inner<String>.InnerInner<String>> a) {
//    System.out.println("moo: "+a);
//  }

//  public void foo( Inner a, Inner.InnerInner b ) {
//    new Inner();
//  }

  /**
   * The type Inner.
   *
   * @param <K> the type parameter
   */
  public class Inner<K> {

    /**
     * Inner foo.
     */
    public void innerFoo() {
      new InnerInner();
    }

    /**
     * The type Inner inner.
     *
     * @param <K> the type parameter
     */
    public class InnerInner<K> {

    }

  }

}
