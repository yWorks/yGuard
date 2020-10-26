package test.internalclasses;


/**
 * The type Internal class in b.
 */
class InternalClassInB {

  /**
   * Internal class method in b.
   */
  public void internalClassMethodInB() {
        System.out.println("internalClassMethodInB");
        new InternalClassInA().internalClassMethodInA();
    }

}
