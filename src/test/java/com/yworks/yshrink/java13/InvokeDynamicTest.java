package com.yworks.yshrink.java13;

import com.yworks.common.ant.InOutPair;
import com.yworks.yshrink.YShrink;
import com.yworks.yshrink.ant.filters.AllMainMethodsFilter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class InvokeDynamicTest {
  @Rule
  public TestName name = new TestName();

  @Test
  public void StringConcatFactoryTest() {
    // StringConcatFactory bootstrap methods are used only in Java 8 and newer
    final String testTypeName = "com.yworks.yshrink.java13.StringConcatFactoryTest";

    final String testMethodName = "void run(java.io.PrintStream)";

    // look for java source code that will be compiled with StringConcatFactory
    // bootstrap methods
    final String fileName = "StringConcatFactoryTest.txt";
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);

    // compile the java source code
    final com.yworks.util.Compiler compiler = com.yworks.util.Compiler.newCompiler();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(List.of(compiler.newUrlSource(testTypeName, source)), baos);

    try {
      // store resulting bytecode in temporary files and ...
      File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
      File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");
      Files.write(inTmp.toPath(), baos.toByteArray());

      // Run shrinker
      YShrink yShrink = new YShrink(false, "SHA-1,MD5");
      InOutPair inOutPair = new InOutPair();
      inOutPair.setIn(inTmp);
      inOutPair.setOut(outTmp);
      yShrink.doShrinkPairs(List.of(inOutPair), new AllMainMethodsFilter(), null);

      //   load shrinked class and run test method
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final ClassLoader cl = URLClassLoader.newInstance(new URL[]{outTmp.toURI().toURL()});
      final Class shrinkedType = Class.forName("com.yworks.yshrink.java13.StringConcatFactoryTest", true, cl);
      final Method run =  shrinkedType.getMethod("run", PrintStream.class);
      run.invoke(null, new PrintStream(output));

      //   check test method output
      assertEquals(
              "Wrong test output",
              String.format("hello from concat factory%n", System.lineSeparator()),
              output.toString());

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void LambdaMetaFactoryTest() {
    // LambdaMetaFactory bootstrap methods are used only in Java 8 and newer
    final String testTypeName = "com.yworks.yshrink.java13.LambdaMetaFactoryTest";

    final String testMethodName = "void run(java.io.PrintStream)";

    // look for java source code that will be compiled with StringConcatFactory
    // bootstrap methods
    final String fileName = "LambdaMetaFactoryTest.txt";
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);

    // compile the java source code
    final com.yworks.util.Compiler compiler = com.yworks.util.Compiler.newCompiler();

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(List.of(compiler.newUrlSource(testTypeName, source)), baos);

    try {
      // store resulting bytecode in temporary files and ...
      File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
      File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");
      Files.write(inTmp.toPath(), baos.toByteArray());

      // Run shrinker
      YShrink yShrink = new YShrink(false, "SHA-1,MD5");
      InOutPair inOutPair = new InOutPair();
      inOutPair.setIn(inTmp);
      inOutPair.setOut(outTmp);
      yShrink.doShrinkPairs(List.of(inOutPair), new AllMainMethodsFilter(), null);

      //   load shrinked class and run test method
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      final ClassLoader cl = URLClassLoader.newInstance(new URL[]{outTmp.toURI().toURL()});
      final Class shrinkedType = Class.forName("com.yworks.yshrink.java13.LambdaMetaFactoryTest", true, cl);
      final Method run =  shrinkedType.getMethod("run", PrintStream.class);
      run.invoke(null, new PrintStream(output));

      //   check test method output
      assertEquals(
              "Wrong test output",
              String.format("Hello from lambda invoke%n", System.lineSeparator()),
              output.toString());

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
