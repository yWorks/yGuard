package com.yworks.yguard.obf;

import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.Entry;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.ObfuscatorTask.ReplaceContentPolicy;
import com.yworks.yguard.ObfuscatorTask.ReplacePathPolicy;

import org.apache.tools.ant.Project;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests adjusting resource file contents. 
 *
 * @author Thomas Behr
 */
public class AdjustTest extends AbstractObfuscationTest {
  @Test
  public void testContentPattern() throws Exception {
    final Pattern dot = Pattern.compile(newContentPattern("."));
    testContentPatternImpl(dot, new String[][] {
      {
        "classname = com.yworks.yguard.YGuardTask",
        "com.yworks.yguard.YGuardTask"
      },
      {
        "<ref classname=\"com.yworks.yguard.YGuardTask\">",
        "com.yworks.yguard.YGuardTask"
      },
      {
        "classname = com.yworks.yguard.ObfuscatorTask$TranslateJavaClass",
        "com.yworks.yguard.ObfuscatorTask$TranslateJavaClass"
      },
      {
        "<ref classname=\"com.yworks.yguard.ObfuscatorTask$TranslateJavaClass$ResourceAdjuster\">",
        "com.yworks.yguard.ObfuscatorTask$TranslateJavaClass$ResourceAdjuster"
      },
      {
        "classname = com.yworks.yguard.ObfuscatorTask$TranslateJavaClass.ResourceAdjuster",
        "com.yworks.yguard.ObfuscatorTask$TranslateJavaClass.ResourceAdjuster"
      },
    });

    final Pattern slash = Pattern.compile(newContentPattern("/"));
    testContentPatternImpl(slash, new String[][] {
      {
        "resourcename = com/yworks/yguard/YGuardTask.properties",
        "com/yworks/yguard/YGuardTask.properties"
      },
    });
  }

  private void testContentPatternImpl(
    final Pattern pattern, final String[][] testCases
  ) throws Exception {
    for (int i = 0, n = testCases.length; i < n; ++i) {
      final String input = testCases[i][0];
      final String expected = testCases[i][1];

      final Matcher matcher = pattern.matcher(input);
      final boolean found = matcher.find();
      if (expected == null) {
        assertFalse("Found match in " + input + '.', found);
      } else {
        assertTrue("No match found in " + input + '.', found);

        final String match = input.substring(matcher.start(), matcher.end());
        assertEquals("Unexpected match.", expected, match);
      }
    }
  }


  @Test
  public void testReplaceJavaNamesStrict() throws Exception {
    final Properties expected = new Properties();

    // adjusted values
    expected.setProperty("javaclassname1",
      // input: com.yworks.yguard.obf.ClassWithResources
      "A.A.A.A.A");
    expected.setProperty("javaclassname2",
      // input: com.yworks.yguard.obf.ClassWithResources$NestedClass
      "A.A.A.A.A$_A");

    expected.setProperty("javaresourcename",
      // input: com.yworks.yguard.obf.GlobalResources
      "com.yworks.yguard.obf.GlobalResources");

    // ignored values
    expected.setProperty("javaclassname3",
      // input: com.yworks.yguard.obf.ClassWithResources.NestedClass
      "com.yworks.yguard.obf.ClassWithResources.NestedClass");

    expected.setProperty("pathclassname1",
     "com/yworks/yguard/obf/ClassWithResources");
    expected.setProperty("pathclassname2",
     "com/yworks/yguard/obf/ClassWithResources$NestedClass");

    expected.setProperty("pathresourcename1",
      "com/yworks/yguard/obf/ClassWithResources.properties");
    expected.setProperty("pathresourcename2",
      "com/yworks/yguard/obf/ClassWithResources_en.properties");
    expected.setProperty("pathresourcename3",
      "com/yworks/yguard/obf/GlobalResources");
    expected.setProperty("pathresourcename4",
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.strict, ".");
  }

  @Test
  public void testReplaceJavaNamesLenient() throws Exception {
    final Properties expected = new Properties();

    // adjusted values
    expected.setProperty("javaclassname1",
      // input: com.yworks.yguard.obf.ClassWithResources
      "A.A.A.A.A");
    expected.setProperty("javaclassname2",
      // input: com.yworks.yguard.obf.ClassWithResources$NestedClass
      "A.A.A.A.A$_A");

    expected.setProperty("javaclassname3",
      // input: com.yworks.yguard.obf.ClassWithResources.NestedClass
      "A.A.A.A.A._A");

    expected.setProperty("javaresourcename",
      // input: com.yworks.yguard.obf.GlobalResources
      "A.A.A.A.GlobalResources");

    // ignored values
    expected.setProperty("pathclassname1",
      "com/yworks/yguard/obf/ClassWithResources");
    expected.setProperty("pathclassname2",
      "com/yworks/yguard/obf/ClassWithResources$NestedClass");

    expected.setProperty("pathresourcename1",
      "com/yworks/yguard/obf/ClassWithResources.properties");
    expected.setProperty("pathresourcename2",
      "com/yworks/yguard/obf/ClassWithResources_en.properties");
    expected.setProperty("pathresourcename3",
      "com/yworks/yguard/obf/GlobalResources");
    expected.setProperty("pathresourcename4",
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.lenient, ".");
  }

  @Test
  public void testReplacePathNamesStrict() throws Exception {
    final Properties expected = new Properties();

    // ignored values
    expected.setProperty("javaclassname1",
      "com.yworks.yguard.obf.ClassWithResources");
    expected.setProperty("javaclassname2",
      "com.yworks.yguard.obf.ClassWithResources$NestedClass");

    expected.setProperty("javaresourcename",
      "com.yworks.yguard.obf.GlobalResources");

    expected.setProperty("pathresourcename3",
      // input: com/yworks/yguard/obf/GlobalResources
      "com/yworks/yguard/obf/GlobalResources");
    expected.setProperty("pathresourcename4",
      // input: com/yworks/yguard/obf/GlobalResources.properties
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      // input: com/yworks/yguard/obf/GlobalResources$Fragment.properties
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");

    // adjusted values
    expected.setProperty("pathclassname1",
      // input: com/yworks/yguard/obf/ClassWithResources
      "A/A/A/A/A");
    expected.setProperty("pathclassname2",
      // input: com/yworks/yguard/obf/ClassWithResources$NestedClass
      "A/A/A/A/A$_A");

    expected.setProperty("pathresourcename1",
      // input: com/yworks/yguard/obf/ClassWithResources.properties
      "A/A/A/A/A.properties");
    expected.setProperty("pathresourcename2",
     // input: com/yworks/yguard/obf/ClassWithResources_en.properties
     "A/A/A/A/A_en.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.strict, "/");
  }

  @Test
  public void testReplacePathNamesLenient() throws Exception {
    final Properties expected = new Properties();

    // ignored values
    expected.setProperty("javaclassname1",
      "com.yworks.yguard.obf.ClassWithResources");
    expected.setProperty("javaclassname2",
      "com.yworks.yguard.obf.ClassWithResources$NestedClass");

    expected.setProperty("javaresourcename",
      "com.yworks.yguard.obf.GlobalResources");

    // adjusted values
    expected.setProperty("pathclassname1",
      // input: com/yworks/yguard/obf/ClassWithResources
      "A/A/A/A/A");
    expected.setProperty("pathclassname2",
      // input: com/yworks/yguard/obf/ClassWithResources$NestedClass
      "A/A/A/A/A$_A");

    expected.setProperty("pathresourcename1",
      // input: com/yworks/yguard/obf/ClassWithResources.properties
      "A/A/A/A/A.properties");
    expected.setProperty("pathresourcename2",
      // input: com/yworks/yguard/obf/ClassWithResources_en.properties
      "A/A/A/A/A_en.properties");
    expected.setProperty("pathresourcename3",
      // input: com/yworks/yguard/obf/GlobalResources
      "A/A/A/A/GlobalResources");
    expected.setProperty("pathresourcename4",
      // input: com/yworks/yguard/obf/GlobalResources.properties
      "A/A/A/A/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      // input: com/yworks/yguard/obf/GlobalResources$Fragment.properties
      "A/A/A/A/GlobalResources$Fragment.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.lenient, "/");
  }

  @Test
  public void testReplaceJavaAndPathNamesStrict() throws Exception {
    final Properties expected = new Properties();

    // ignored values
    expected.setProperty("javaresourcename",
      "com.yworks.yguard.obf.GlobalResources");

    expected.setProperty("pathresourcename3",
      // input: com/yworks/yguard/obf/GlobalResources
      "com/yworks/yguard/obf/GlobalResources");
    expected.setProperty("pathresourcename4",
      // input: com/yworks/yguard/obf/GlobalResources.properties
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      // input: com/yworks/yguard/obf/GlobalResources$Fragment.properties
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");

    // ajusted values
    expected.setProperty("javaclassname1",
      // input: com.yworks.yguard.obf.ClassWithResources
      "A.A.A.A.A");
    expected.setProperty("javaclassname2",
      // input: com.yworks.yguard.obf.ClassWithResources$NestedClass
      "A.A.A.A.A$_A");

    expected.setProperty("pathclassname1",
      // input: com/yworks/yguard/obf/ClassWithResources
      "A/A/A/A/A");
    expected.setProperty("pathclassname2",
      // input: com/yworks/yguard/obf/ClassWithResources$NestedClass
      "A/A/A/A/A$_A");

    expected.setProperty("pathresourcename1",
      // input: com/yworks/yguard/obf/ClassWithResources.properties
      "A/A/A/A/A.properties");
    expected.setProperty("pathresourcename2",
      // input: com/yworks/yguard/obf/ClassWithResources_en.properties
      "A/A/A/A/A_en.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.strict, "./");
  }

  @Test
  public void testReplaceJavaAndPathNamesLenient() throws Exception {
    final Properties expected = new Properties();

    expected.setProperty("javaclassname1",
      // input: com.yworks.yguard.obf.ClassWithResources
      "A.A.A.A.A");
    expected.setProperty("javaclassname2",
      // input: com.yworks.yguard.obf.ClassWithResources$NestedClass
      "A.A.A.A.A$_A");

    expected.setProperty("javaresourcename",
      // input: com.yworks.yguard.obf.GlobalResources
      "A.A.A.A.GlobalResources");

    expected.setProperty("pathclassname1",
      // input: com/yworks/yguard/obf/ClassWithResources
      "A/A/A/A/A");
    expected.setProperty("pathclassname2",
      // input: com/yworks/yguard/obf/ClassWithResources$NestedClass
      "A/A/A/A/A$_A");

    expected.setProperty("pathresourcename1",
      // input: com/yworks/yguard/obf/ClassWithResources.properties
      "A/A/A/A/A.properties");
    expected.setProperty("pathresourcename2",
     // input: com/yworks/yguard/obf/ClassWithResources_en.properties
     "A/A/A/A/A_en.properties");
    expected.setProperty("pathresourcename3",
      // input: com/yworks/yguard/obf/GlobalResources
      "A/A/A/A/GlobalResources");
    expected.setProperty("pathresourcename4",
      // input: com/yworks/yguard/obf/GlobalResources.properties
      "A/A/A/A/GlobalResources.properties");
    expected.setProperty("pathresourcename5",
      // input: com/yworks/yguard/obf/GlobalResources$Fragment.properties
      "A/A/A/A/GlobalResources$Fragment.properties");

    testReplaceContentImpl(expected, ReplaceContentPolicy.lenient, "./");
  }

  private void testReplaceContentImpl(
    final Properties expected,
    final ReplaceContentPolicy policy,
    final String sep
  ) throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());


    final Archive archive = newArchive();

    configureNameMakerFactory();
    final GuardDB db = newGuardDB(archive);

    final ObfuscatorTask task = new ObfuscatorTask();
    task.setProject(new Project());

    final ObfuscatorTask.AdjustSection adjust = task.createAdjust();
    adjust.setReplaceContentPolicy(policy);
    adjust.setReplaceContentSeparator(sep);
    addNonClassEntries(adjust, archive);

    final ResourceHandler adjuster = getResourceHandler(task, db);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Entry entry : asIterable(archive)) {
      final String name = entry.getName();
      if (name.endsWith(".properties")) {
        final boolean adjusted =
          adjuster.filterContent(archive.getInputStream(entry), baos, name);
        assertTrue("ResourceAdjuster ignored file " + name + '.', adjusted);

        final Properties properties = new Properties();
        properties.load(new StringReader(baos.toString("UTF-8")));
        for (Map.Entry<Object, Object> actual : properties.entrySet()) {
          final String key = (String) actual.getKey();
          final String expectedValue = expected.getProperty(key);
          if (expectedValue != null) {
            assertEquals(
              "ResourceAdjuster failed to adjust " + key + '.',
              expectedValue,
              actual.getValue());
          }
        }
      }
      baos.reset();
    }
  }

  private static void addNonClassEntries(
    final ObfuscatorTask.AdjustSection adjust, final Archive archive
  ) throws Exception {
    final HashSet<String> entries = new HashSet<>();
    for (Entry entry : asIterable(archive)) {
      final String name = entry.getName();
      if (!name.toLowerCase().endsWith(".class")) {
        entries.add(name);
      }
    }

    getField(adjust.getClass(), "entries").set(adjust, entries);
  }


  @Test
  public void testReplaceNameAndPath() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "A/A/A/A/A.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "A/A/A/A/A_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "A/A/A/A/A$_A.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "com/yworks/yguard/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/A.A.A.A.A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/A.A.A.A.A$_A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.file);
  }

  @Test
  public void testReplaceName() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "com/yworks/yguard/obf/A.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "com/yworks/yguard/obf/A_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "com/yworks/yguard/obf/A$_A.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "com/yworks/yguard/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/A.A.A.A.A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/A.A.A.A.A$_A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.name);
  }

  @Test
  public void testReplacePath() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "A/A/A/A/ClassWithResources.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "A/A/A/A/ClassWithResources_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "A/A/A/A/ClassWithResources$NestedClass.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "A/A/A/A/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "A/A/A/A/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "A/A/A/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.path);
  }

  @Test
  public void testKeepNameAndPath() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "com/yworks/yguard/obf/ClassWithResources.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "com/yworks/yguard/obf/ClassWithResources_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "com/yworks/yguard/obf/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "com/yworks/yguard/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.none);
  }

  @Test
  public void testReplaceFileOrPath() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "A/A/A/A/A.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "A/A/A/A/A_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "A/A/A/A/A$_A.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "A/A/A/A/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "A/A/A/A/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "A/A/A/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/A.A.A.A.A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/A.A.A.A.A$_A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.fileorpath);
  }
  
  @Test
  public void testReplacePathLenient() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "A/A/A/A/A.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "A/A/A/A/A_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "A/A/A/A/A$_A.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "A/A/A/A/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "A/A/A/A/GlobalResources$Fragment.properties");
    expected.put(
      "com/yworks/yguard/other/OtherResources.properties",
      "A/A/A/other/OtherResources.properties");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources",
      "META-INF/services/A.A.A.A.A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment",
      "META-INF/services/A.A.A.A.A$Fragment");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass",
      "META-INF/services/A.A.A.A.A$_A");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources",
      "META-INF/services/A.A.A.A.GlobalResources");
    expected.put(
      "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment",
      "META-INF/services/A.A.A.A.GlobalResources$Fragment");

    testAdjustFilenamesImpl(expected, ReplacePathPolicy.lenient);
  }

  private void testAdjustFilenamesImpl(
    final Map<String, String> expected, final ReplacePathPolicy policy
  ) throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());


    final Archive archive = newArchive();

    configureNameMakerFactory();
    final GuardDB db = newGuardDB(archive);

    final ObfuscatorTask task = new ObfuscatorTask();
    task.setProject(new Project());

    final ObfuscatorTask.AdjustSection adjust = task.createAdjust();
    adjust.setReplacePathPolicy(policy);
    addNonClassEntries(adjust, archive);

    final ResourceHandler adjuster = getResourceHandler(task, db);

    final StringBuffer buffer = new StringBuffer();
    for (Entry entry : asIterable(archive)) {
      final String name = entry.getName();
      final String expectedName = expected.remove(name);
      if (expectedName != null) {
        final boolean adjusted = adjuster.filterName(name, buffer);
        assertTrue("ResourceAdjuster ignored file " + name + '.', adjusted);

        assertEquals(
          "ResourceAdjuster failed to rename " + name + '.',
          expectedName,
          buffer.toString());
      }
      buffer.setLength(0);
    }

    assertEquals("Archive did not contain all expected entries.", 0, expected.size());
  }

  @Test
  public void testGetOutName() throws Exception {
    final HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources.properties",
      "A/A/A/A/ClassWithResources.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources_en.properties",
      "A/A/A/A/ClassWithResources_en.properties");
    expected.put(
      "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties",
      "A/A/A/A/ClassWithResources$NestedClass.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources.properties",
      "A/A/A/A/GlobalResources.properties");
    expected.put(
      "com/yworks/yguard/obf/GlobalResources$Fragment.properties",
      "A/A/A/A/GlobalResources$Fragment.properties");

    testGetOutNameImpl(expected);
  }

  private void testGetOutNameImpl(
    final Map<String, String> expected
  ) throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());


    final Archive archive = newArchive();

    configureNameMakerFactory();
    final GuardDB db = newGuardDB(archive);

    for (Entry entry : asIterable(archive)) {
      final String name = entry.getName();
      final String expectedName = expected.get(name);
      if (expectedName != null) {
        assertEquals(
          "GuardDB failed to rename " + name + '.',
          expectedName,
          db.getOutName(name));
      }
    }
  }

  /*
   * #####################################################################
   * utility methods
   * #####################################################################
   */

  private GuardDB newGuardDB( final Archive archive ) throws Exception {
    final PrintWriter log = new PrintWriter(new StringWriter());
    final GuardDB db = new GuardDB(new File[0]);
    setArchive(db, archive);
    invoke(db, "buildClassTree", log);
    invoke(db, "createMap", log);
    return db;
  }

  private Archive newArchive() throws Exception {
    return newArchive(
      "compatibility.jar",
      Collections.singletonList(
        new TypeStruct(
          "adjust/ClassWithResources.txt",
          "com.yworks.yguard.obf.ClassWithResources")),
      Arrays.asList(
        new EntryStruct(
          "adjust/ClassWithResources.properties",
          "com/yworks/yguard/obf/ClassWithResources.properties"),
        new EntryStruct(
          null,
          "com/yworks/yguard/obf/ClassWithResources_en.properties"),
        new EntryStruct(
         null,
          "com/yworks/yguard/obf/ClassWithResources$NestedClass.properties"),
        new EntryStruct(
          "adjust/GlobalResources.properties",
          "com/yworks/yguard/obf/GlobalResources.properties"),
        new EntryStruct(
          null,
          "com/yworks/yguard/obf/GlobalResources$Fragment.properties"),
        new EntryStruct(
          null,
          "com/yworks/yguard/other/OtherResources.properties"),
        new EntryStruct(
          null,
          "META-INF/services/com.yworks.yguard.obf.ClassWithResources"),
        new EntryStruct(
          null,
          "META-INF/services/com.yworks.yguard.obf.ClassWithResources$Fragment"),
        new EntryStruct(
          null,
          "META-INF/services/com.yworks.yguard.obf.ClassWithResources$NestedClass"),
        new EntryStruct(
          null,
          "META-INF/services/com.yworks.yguard.obf.GlobalResources"),
        new EntryStruct(
          null,
          "META-INF/services/com.yworks.yguard.obf.GlobalResources$Fragment")));
  }

  private static void setArchive(
    final GuardDB db, final Archive archive
  ) throws Exception {
    getField(db.getClass(), "inJar").set(db, new Archive[] {archive});
  }


  private static void configureNameMakerFactory() throws Exception {
    final String qn = ObfuscatorTask.class.getName() + "$YGuardNameFactory";
    final Class<?> c = Class.forName(qn);
    final Constructor<?> ctor = c.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }


  private static ResourceHandler getResourceHandler(
          final ObfuscatorTask task, final GuardDB db
  ) throws Exception {
    final String mn = "newResourceAdjuster";
    final Method m = getMethod(task.getClass(), mn, GuardDB.class);
    return (ResourceHandler) m.invoke(task, db);
  }


  private static String newContentPattern(
    final String separator
  ) throws Exception {
    final String mn = "newContentPattern";
    final Method m = getMethod(ObfuscatorTask.class, mn, String.class);
    return (String) m.invoke(null, separator);
  }


  private static void invoke(
    final GuardDB db, final String methodName, final PrintWriter log
  ) throws Exception {
    getMethod(db.getClass(), methodName, PrintWriter.class).invoke(db, log);
  }

  private static Field getField(
    final Class<?> c, final String fieldName
  ) throws Exception {
    final Field f = c.getDeclaredField(fieldName);
    f.setAccessible(true);
    return f;
  }

  private static Method getMethod(
    final Class<?> c, final String methodName, final Class<?>... params
  ) throws Exception {
    final Method m = c.getDeclaredMethod(methodName, params);
    m.setAccessible(true);
    return m;
  }


  private static Iterable<Entry> asIterable( final Archive archive ) {
    return new ArchiveAdapter(archive);
  }



  private static class ArchiveAdapter implements Iterable<Entry> {
    private final Archive archive;

    ArchiveAdapter( final Archive archive ) {
      this.archive = archive;
    }

    @Override
    public Iterator<Entry> iterator() {
      return new EnumerationAdapter<Entry>(archive.getEntries());
    }
  }

  private static final class EnumerationAdapter<T> implements Iterator<T> {
    private final Enumeration<T> en;

    EnumerationAdapter( final Enumeration<T> en ) {
      this.en = en;
    }

    @Override
    public boolean hasNext() {
      return en.hasMoreElements();
    }

    @Override
    public T next() {
      return en.nextElement();
    }
  }
}
