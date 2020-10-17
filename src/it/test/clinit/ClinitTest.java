package test.clinit;

/**
 * The type Clinit test.
 *
 * @author schroede
 */
public class ClinitTest {

    /**
     * The enum Test.
     */
    public enum Test {
        /**
         * A test.
         */
        a,
        /**
         * B test.
         */
        b};

    /**
     * Foo.
     */
    public void foo(){
    switch (Test.a) {
      case a:
        System.out.println("a");
        break;
      case b:
        System.out.println("a");
        break;
    }
  }
}
