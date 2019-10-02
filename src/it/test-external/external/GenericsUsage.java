package external;

import test.generics.BlaClass;
import test.generics.GenericOuter;
import test.generics.GenericOuterNonGenericInner;

import java.util.List;

public class GenericsUsage {

  public static void main( String[] args ) {
    GenericOuter<List<BlaClass.Inner<String>>> genericOuter = new GenericOuter<List<BlaClass.Inner<String>>>();
    //genericOuter.foo(inner, innerinner);

    GenericOuter<List<BlaClass.Inner<String>>>.Inner<GenericOuter.Inner.InnerInner> inner = genericOuter.new Inner<GenericOuter.Inner.InnerInner>();

    genericOuter.foo(inner);

    GenericOuterNonGenericInner<List<BlaClass.Inner<String>>> outerNonGenericInner =
        new GenericOuterNonGenericInner<List<BlaClass.Inner<String>>>();

    outerNonGenericInner.foo(null,null);

  }

}
