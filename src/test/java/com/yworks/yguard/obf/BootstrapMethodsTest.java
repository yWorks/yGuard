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
    final String testTypeName = "com.yworks.yguard.obf.LambdaMetaFactoryTest";
    assertTrue("Invalid Java version", 8 <= getMajorVersion());

    final String testMethodName = "void run(java.io.PrintStream)";

    // look for java source code that will be compiled with StringConcatFactory
    // bootstrap methods
    final String fileName = "LambdaMetaFactoryTest.txt";
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);

    // compile the java source code
    final com.yworks.util.Compiler compiler = Compiler.newCompiler();

    final ArrayList sources = new ArrayList();
    sources.add(compiler.newUrlSource(testTypeName, source));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);


    // store resulting bytecode in temporary files and ...
    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      // ... run obfuscator
      final StringWriter log = new StringWriter();
      final GuardDB db = new GuardDB(new File[]{inTmp});
      db.setDigests(new String[0]);
      db.remapTo(new File[] {outTmp}, null, new PrintWriter(log), false);
      db.close();


      // finally check if the obfuscated class(es) still work(s) as intended
      //   determine obfuscated names
      final Mapper mapper = Mapper.newInstance(log.toString());

      final String mtn = mapper.getTypeName(testTypeName);
      assertNotNull("Could not find mapping for class " + testTypeName, mtn);

      final String mmn = mapper.getMethodName(testTypeName, testMethodName);
      assertNotNull("Could not find mapping for method " + testTypeName + '#' + testMethodName, mmn);

      //   load obfuscated class and run test method
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final ClassLoader cl = URLClassLoader.newInstance(new URL[]{outTmp.toURI().toURL()});
      final Class obfType = Class.forName(mtn, true, cl);
      final Method run = obfType.getMethod(mmn, PrintStream.class);
      run.invoke(null, new PrintStream(output));

      //   check test method output
      assertEquals(
              "Wrong test output",
              String.format("implementation%n", System.lineSeparator()),
              new String(output.toByteArray(), "UTF-8"));
    } finally {

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
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


    final String testTypeName = "com.yworks.yguard.obf.StringConcatFactoryTest";
    final String testMethodName = "void run(java.io.PrintStream)";


    // look for java source code that will be compiled with StringConcatFactory
    // bootstrap methods
    final String fileName = "StringConcatFactoryTest.txt";
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);


    // compile the java source code
    final com.yworks.util.Compiler compiler = Compiler.newCompiler();

    final ArrayList sources = new ArrayList();
    sources.add(compiler.newUrlSource(testTypeName, source));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);


    // store resulting bytecode in temporary files and ...
    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      // ... run obfuscator
      final StringWriter log = new StringWriter();
      final GuardDB db = new GuardDB(new File[]{inTmp});
      db.setDigests(new String[0]);
      db.remapTo(new File[] {outTmp}, null, new PrintWriter(log), false);
      db.close();


      // finally check if the obfuscated class(es) still work(s) as intended
      //   determine obfuscated names
      final Mapper mapper = Mapper.newInstance(log.toString());

      final String mtn = mapper.getTypeName(testTypeName);
      assertNotNull("Could not find mapping for class " + testTypeName, mtn);

      final String mmn = mapper.getMethodName(testTypeName, testMethodName);
      assertNotNull("Could not find mapping for method " + testTypeName + '#' + testMethodName, mmn);

      //   load obfuscated class and run test method
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final ClassLoader cl = URLClassLoader.newInstance(new URL[]{outTmp.toURI().toURL()});
      final Class obfType = Class.forName(mtn, true, cl);
      final Method run = obfType.getMethod(mmn, PrintStream.class);
      run.invoke(null, new PrintStream(output));

      //   check test method output
      assertEquals(
              "Wrong test output",
              String.format("Hello world!%1$s1 < 2%1$s", System.lineSeparator()),
              new String(output.toByteArray(), "UTF-8"));
    } finally {

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }
}
