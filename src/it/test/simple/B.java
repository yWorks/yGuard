package test.simple;

/**
 * The type B.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class B extends A {

  private final Object privateField = "privateField";

    /**
     * G.
     */
    @Override
  public void g() {
    System.out.println( "B.g" );
  }
}
