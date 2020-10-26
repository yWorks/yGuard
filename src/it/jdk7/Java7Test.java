package test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Java 7 test.
 */
public class Java7Test {

    /**
     * The entry point of application.
     *
     * 
		 * @param args the input arguments
     */
    public static void main(String[] args) {
    Java7Test java7Test = new Java7Test();
    java7Test.testStringSwitch();
    java7Test.testGenericTypeInference();
    java7Test.testUnderscoreInNumberLiteral();
    java7Test.testBinaryLiteral();
  }

    /**
     * Test string switch.
     */
    public void testStringSwitch() {

    String aString = "loremipsum";

    switch (aString) {
      case "notloremipsum":
        System.out.println("notloremipsum");
        break;
      case "loremipsum":
        System.out.println("isloremipsum");
        break;
      default:
        System.out.println("nothing");
        break;
    }
  }

    /**
     * Test multi catch.
     *
     * 
		 * @param source the source
     * 
		 * @param target the target
     */
    public void testMultiCatch(File source,File target) {
    try (InputStream fis = new FileInputStream(source);
         OutputStream fos = new FileOutputStream(target)) {

      byte[] buf = new byte[8192];

      int i;
      while ((i = fis.read(buf)) != -1) {
        fos.write(buf, 0, i);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

    /**
     * Test generic type inference.
     */
    public void testGenericTypeInference() {
    Map<String, List<String>> aMap = new HashMap<>();
    System.out.println("map = " + aMap);
  }

    /**
     * Test underscore in number literal.
     */
    public void testUnderscoreInNumberLiteral() {
    int aMillion = 1_000_000;
    System.out.println("aMillion = " + aMillion);
  }

    /**
     * Test binary literal.
     */
    public void testBinaryLiteral() {
    int binary = 0b1001_1001;
    System.out.println("binary = " + binary);
  }

}
