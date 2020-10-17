package test.simple;

import test.external.ExtAI;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * The type A.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class A implements ExtAI {

  /**
   * The Afield.
   */
  public int afield = 200;

  /**
   * G.
   */
  public void g() {
    System.out.println("A.g");
  }

  /**
   * The O.
   */
  Object o = new AbstractAction("name") {

    public void actionPerformed( ActionEvent e ) {
      System.out.println("A.actionPerformed");
    }
  };

  private void privateMethod() {
    System.out.println("A.privateMethod");
  }

  /**
   * External method.
   */
  public void externalMethod() {
    System.out.println("A.externalMethod");
  }

  /**
   * Throws method.
   *
   * @throws IllegalArgumentException the illegal argument exception
   */
  public void throwsMethod() throws IllegalArgumentException {
    throw new IllegalArgumentException("lala");
  }
}
