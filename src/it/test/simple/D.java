package test.simple;

import test.external.ExtAII;

/**
 * The type D.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
class D extends C {

  private Object key;

  /**
   * Instantiates a new D.
   *
   * @param key the key
   */
  public D( Object key ) {
    this.key = key;
    System.out.println( "D.D: "+key );
  }

  /**
   * G.
   */
  @Override
  public void g() {
    System.out.println( "D.g" );
  }

  /**
   * Long name method.
   */
  public void longNameMethod() {
    System.out.println( "D.longNameMethod" );
  }

  /**
   * Gets ext aii.
   *
   * @return the ext aii
   */
  public static ExtAII getExtAII() {
     return null;
   }


}
