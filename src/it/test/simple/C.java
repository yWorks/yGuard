package test.simple;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class C extends B {

  private final Object privateField = "private field";
  final Object friendlyField = "friendly field";


  @Override
  public void g() {
    System.out.println( "C.g" );
  }
}
