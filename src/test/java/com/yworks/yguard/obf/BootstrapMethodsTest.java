package com.yworks.yguard.obf;

import com.yworks.util.Compiler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests if {@link java.lang.invoke.StringConcatFactory} bootstrap methods are
 * properly obfuscated.
 *
 * @author Thomas Behr
 */
public class BootstrapMethodsTest extends AbstractObfuscationTest {
  /**
   * The Name.
   */
  @Rule
  public TestName name = new TestName();

  /**
   * Test lambda meta factory.
   *
   * @throws Exception the exception
   */
  @Test
  public void testLambdaMetaFactory() throws Exception {
    // LambdaMetaFactory bootstrap methods are used only in Java 8 and newer
    assertTrue("Invalid Java version", 8 <= getMajorVersion());

    runBootstrapMethodsTest(
      "com.yworks.yguard.obf.LambdaMetaFactoryTest",
      "void run(java.io.PrintStream)",
      "LambdaMetaFactoryTest.txt",
      String.format("implementation%n"));
  }

  /**
   * Test string concat factory.
   *
   * @throws Exception the exception
   */
  @Test
  public void testStringConcatFactory() throws Exception {
    // StringConcatFactory bootstrap methods are used only in Java 11 and newer
    assertTrue("Invalid Java version", 11 <= getMajorVersion());

    runBootstrapMethodsTest(
      "com.yworks.yguard.obf.StringConcatFactoryTest",
      "void run(java.io.PrintStream)",
      "StringConcatFactoryTest.txt",
      String.format("Hello world!%n1 < 2%n"));
  }

  @Test
  public void testSwitchBootstraps_enumSwitch() throws Exception {
    // SwitchBootstraps.enumSwitch bootstrap method is used only in Java 21 and
    // newer
    if (21 <= getMajorVersion()) {
      runBootstrapMethodsTest(
        "com.yworks.yguard.obf.SwitchBootstraps_enumSwitch",
        "void run(java.io.PrintStream)",
        "SwitchBootstraps_enumSwitch.txt",
        String.format("It is heads.%n"));
    } else {
      System.err.println("Run test with Java 21 or newer.");
    }
  }

  @Test
  public void testSwitchBootstraps_typeSwitch() throws Exception {
    // SwitchBootstraps.typeSwitch bootstrap method is used only in Java 21 and
    // newer
    if (21 <= getMajorVersion()) {
      runBootstrapMethodsTest(
        "com.yworks.yguard.obf.SwitchBootstraps_typeSwitch",
        "void run(java.io.PrintStream)",
        "SwitchBootstraps_typeSwitch.txt",
        String.format("yes%n"));
    } else {
      System.err.println("Run test with Java 21 or newer.");
    }
  }

  @Test
  public void testConstantBootstraps() throws Exception {
    // ConstantBootstraps.invoke bootstrap method is used only in Java 21 and
    // newer
    if (21 <= getMajorVersion()) {
      runBootstrapMethodsTest(
        "com.yworks.yguard.obf.ConstantBootstraps",
        "void run(java.io.PrintStream)",
        "ConstantBootstraps.txt",
        String.format("yes%n"));
    } else {
      System.err.println("Run test with Java 21 or newer.");
    }
  }


  private void runBootstrapMethodsTest(
    final String testTypeName,
    final String testMethodName,
    final String fileName,
    final String expected
  ) throws Exception {
    // look for java source code that will be compiled with bootstrap methods
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);


    // compile the java source code
    final com.yworks.util.Compiler compiler = Compiler.newCompiler();

    final ArrayList sources = new ArrayList();
    sources.add(compiler.newUrlSource(testTypeName, source));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);


    // store the resulting bytecode in temporary files and ...
    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      // ... run obfuscator
      final StringWriter log = new StringWriter();
      final GuardDB db = new GuardDB(new File[]{inTmp});
      db.setDigests(new String[0]);
//      db.setReplaceClassNameStrings(true);
      db.remapTo(new File[] {outTmp}, null, new PrintWriter(log), false);
      db.close();


      // finally, check if the obfuscated class(es) still work(s) as intended
      //   determine obfuscated names
      final Mapper mapper = Mapper.newInstance(log.toString());

      final String mtn = mapper.getTypeName(testTypeName);
      assertNotNull("Could not find mapping for class " + testTypeName, mtn);

      final String mmn = mapper.getMethodName(testTypeName, testMethodName);
      assertNotNull("Could not find mapping for method " + testTypeName + '#' + testMethodName, mmn);

      //   load obfuscated class and run test method
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final URLClassLoader cl = URLClassLoader.newInstance(new URL[]{outTmp.toURI().toURL()});
      try {
        final Class obfType = Class.forName(mtn, true, cl);
        final Method run = obfType.getMethod(mmn, PrintStream.class);
        run.invoke(null, new PrintStream(output));
      } finally {
        cl.close();
      }

      //   check test method output
      assertEquals(
        "Wrong test output",
        expected,
        new String(output.toByteArray(), "UTF-8"));
    } finally {

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }
}
