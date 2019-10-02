package test.simple;

import test.external.ExtAI;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class A implements ExtAI {

  public int afield = 200;

  public void g() {
    System.out.println( "A.g" );
  }

  Object o = new AbstractAction( "name" ){

    public void actionPerformed( ActionEvent e ) {
      System.out.println( "A.actionPerformed" );
    }
  };

  private void privateMethod() {
    System.out.println( "A.privateMethod" );
  }

  public void externalMethod() {
    System.out.println( "A.externalMethod" );
  }

  public void throwsMethod() throws IllegalArgumentException {
    throw new IllegalArgumentException( "lala" );
  }
}
