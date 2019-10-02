package test.clinit;

/** @author schroede */
public class ClinitTest {

  public enum Test { a, b};

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
