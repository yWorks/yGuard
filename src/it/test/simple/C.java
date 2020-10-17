package test.simple;

/**
 * The type C.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class C extends B {

  private final Object privateField = "private field";
    /**
     * The Friendly field.
     */
    final Object friendlyField = "friendly field";


    /**
     * G.
     */
    @Override
  public void g() {
    System.out.println( "C.g" );
  }
}
