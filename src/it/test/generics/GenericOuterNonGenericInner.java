package test.generics;

import java.util.List;


public class GenericOuterNonGenericInner<T extends List<BlaClass.Inner<String>>> {

  public void foo( Inner b, Inner.InnerInner c ) {
    System.out.println("bla: "+b);
  }

  public class Inner {

    public class InnerInner {

    }

  }

}
