package test.simple;

import test.external.ExtAII;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
class D extends C {

  private Object key;

  public D( Object key ) {
    this.key = key;
    System.out.println( "D.D: "+key );
  }

  @Override
  public void g() {
    System.out.println( "D.g" );
  }

  public void longNameMethod() {
    System.out.println( "D.longNameMethod" );
  }

   public static ExtAII getExtAII() {
     return null;
   }


}
