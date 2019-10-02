package test.simple;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class B extends A {

  private final Object privateField = "privateField";

  @Override
  public void g() {
    System.out.println( "B.g" );
  }
}
