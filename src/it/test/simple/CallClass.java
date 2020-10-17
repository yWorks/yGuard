package test.simple;

import test.Dummy2;
import test.external.ExtAII;
import test.annot.RequestForEnhancement;

import java.util.ResourceBundle;
import java.util.Locale;
import java.net.MalformedURLException;


/**
 * The type Call class.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class CallClass {

  private native void nativeMethod();

  /**
   * Call method.
   *
   * @RequestForEnhancement( id = 2868724,   synopsis = "Enable time-travel",   engineer = "Mr. Peabody",   date     = "4/1/3007" )
   */
  public static void callMethod() {
    //new CallClass().nativeMethod();
    C c = new C();
    c.g();
    D d = new D(Constants.NODE_ID_DPKEY);

    ExtAII extaii = D.getExtAII();

    AbstractB ab = new ConcreteA();
    ab.g();

    A a = new A();
    System.out.println("A.field: " + A.field);

    System.out.println("d.afield: " + d.afield);

    ResourceBundle bundle = ResourceBundle.getBundle("test.simple.MyResourceBundleClass", new Locale("fr"));
    String message = bundle.getString("MESSAGE");
    System.out.println("MESSAGE: " + message);

//    YGraphMLIOHandler ioHandler = new YGraphMLIOHandler ();
//    ioHandler.canRead();


    //Generic.GenericInner<String> genericInner = new Generic.GenericInner<String>();

    //CallClass.testGenericInner( genericInner );

  }

//  public static void testGenericInner( Generic.GenericInner inner ) {
//    System.out.println( "CallClass.testGenericInner" );
//  }

  /**
   * The interface Generic interface.
   *
   * @param <E> the type parameter
   */
  public static interface GenericInterface<E extends Exception> {
    /**
     * Method.
     *
     * @throws E the e
     */
    void method() throws E;
  }

  /**
   * The type My exception.
   *
   * @param <T> the type parameter
   */
  public static class MyException<T> extends Object {

  }

  /**
   * The interface Einfacher.
   */
  public static interface Einfacher {
  }

  /**
   * The type My class.
   */
  public static class MyClass implements GenericInterface<RuntimeException> {

    public void method() throws RuntimeException {
      throw new RuntimeException("Haha");
    }


//    public <T extends MyException<Object>> void method3(T t) {
//    }

    /**
     * Method 2 blah.
     *
     * @param <T>    the type parameter
     * @param <K>    the type parameter
     * @param <Blah> the type parameter
     * @return the blah
     * @throws T the t
     * @throws K the k
     */
    public <T extends Exception, K extends Throwable, Blah extends GenericInterface<Exception> & Runnable & Einfacher> Blah method2() throws T, K {
      throw (T) new MalformedURLException();
    }
  }

  private enum MyEnum {
    /**
     * Enum 1 my enum.
     */
    ENUM_1,
    /**
     * Enum 2 my enum.
     */
    ENUM_2,
    /**
     * Enum 3 my enum.
     */
    ENUM_3;
  }


  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  @RequestForEnhancement(
          id = 2868724,
          synopsis = "Enable time-travel",
          engineer = "Mr. Peabody",
          date = "4/1/3007"
  )
  public static void main( String[] args ) {
    callMethod();

//    for (MyEnum e : EnumSet.allOf(MyEnum.class)) {
//      System.out.println("enumval: "+e);
//    }

    Dummy2.throwSomething();

  }

}
