package test.generics;

import java.util.List;

public class GenericOuter<T extends List<BlaClass.Inner<String>>> {

//  public void foo( Inner<Inner.InnerInner>.InnerInner<Inner<String>> b ) {
//    System.out.println("bla: "+b);
//  }

  public void foo( Inner<Inner.InnerInner> b ) {
    System.out.println("bla: "+b);
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

  public class Inner<K> {

    public void innerFoo() {
      new InnerInner();
    }

    public class InnerInner<K> {

    }

  }

}
