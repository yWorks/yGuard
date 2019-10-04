package com.yworks.yguard;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests {@link YGuardLogParser#translate(String)} and
 * {@link YGuardLogParser#translate(YGuardLogParser.MyStackTraceElement)}
 * behavior.
 * @author Thomas Behr
 */
public class LogParserTest {
  @Test
  public void testCommonMappings() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      // obfuscated input
      new String[] {
        "A.A.A.A",
        "A.A.A.B$C",
        "\tat A.A.A.A.A()",
        "\tat A.A.A.B$C.A()",
      },
      // expected deobfuscated output
      new String[] {
        "com.yworks.test.Test",
        "com.yworks.test.EnclosingClass$InnerClass",
        "\tat com.yworks.test.Test.run(Test.java:0)",
        "\tat com.yworks.test.EnclosingClass$InnerClass.run(EnclosingClass.java:0)",
      });
  }

  @Test
  public void testInvalidMappings() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      // obfuscated input
      new String[] {
        "A.A.A.D",
        "yguard.A.A.A.D",
        "\tat A.A.A.D.A()",
        "\tat yguard.A.A.A.D.A()",
      },
      // expected deobfuscated output
      new String[] {
        "com.yworks.test.D",
        "yguard.A.A.A.D",
        "\tat com.yworks.test.D.A()",
        "\tat yguard.A.A.A.D.A()",
      });
    deobfuscate(
      insert(MAPPINGS, "yguard"),
      // obfuscated input
      new String[] {
        "A.A.A.D",
        "yguard.A.A.A.D",
        "\tat A.A.A.D.A()",
        "\tat yguard.A.A.A.D.A()",
      },
      // expected deobfuscated output
      new String[] {
        "A.A.A.D",
        "yguard.A.A.A.D",
        "\tat A.A.A.D.A()",
        "\tat yguard.A.A.A.D.A()",
      });
  }

  @Test
  public void testLeadingDollarQualifiedName() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      // obfuscated input
      new String[] {
        "A.A.A.$A",
        "A.A.A.$A$$C",
      },
      // expected deobfuscated output
      new String[] {
        "com.yworks.test.DollarSign",
        "com.yworks.test.DollarSign$InnerClass",
      });
  }

  @Test
  public void testLeadingDollarStacktraceEntry() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      // obfuscated input
      new String[] {
        "\tat A.A.A.$A.$A()",
        "\tat A.A.A.$A$$C.$A()",
      },
      // expected deobfuscated output
      new String[] {
        "\tat com.yworks.test.DollarSign.run(DollarSign.java:0)",
        "\tat com.yworks.test.DollarSign$InnerClass.run(DollarSign.java:0)",
      });
  }

  @Test
  public void testModuleQualifiedName() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      new String[] {
        "yfiles.test/A.A.A.A",
        "yfiles.test@10.0.1/A.A.A.A",
        "app/yfiles.test@10.0.1/A.A.A.A",
        "app//A.A.A.A",
      },
      new String[] {
        "yfiles.test/com.yworks.test.Test",
        "yfiles.test@10.0.1/com.yworks.test.Test",
        "app/yfiles.test@10.0.1/com.yworks.test.Test",
        "app//com.yworks.test.Test",
      });
  }

  @Test
  public void testModuleStacktraceEntry() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      new String[] {
        "\tat yfiles.test/A.A.A.A.A()",
        "\tat yfiles.test/A.A.A.A.A(Unknown Source)",
        "\tat yfiles.test@10.0.1/A.A.A.A.A()",
        "\tat yfiles.test@10.0.1/A.A.A.A.A(Unknown Source)",
        "\tat app/yfiles.test@10.0.1/A.A.A.A.A()",
        "\tat app/yfiles.test@10.0.1/A.A.A.A.A(Unknown Source)",
        "\tat app//A.A.A.A.A()",
        "\tat app//A.A.A.A.A(Unknown Source)",
      },
      new String[] {
        "\tat yfiles.test/com.yworks.test.Test.run(Test.java:0)",
        "\tat yfiles.test/com.yworks.test.Test.run(Test.java:0)",
        "\tat yfiles.test@10.0.1/com.yworks.test.Test.run(Test.java:0)",
        "\tat yfiles.test@10.0.1/com.yworks.test.Test.run(Test.java:0)",
        "\tat app/yfiles.test@10.0.1/com.yworks.test.Test.run(Test.java:0)",
        "\tat app/yfiles.test@10.0.1/com.yworks.test.Test.run(Test.java:0)",
        "\tat app//com.yworks.test.Test.run(Test.java:0)",
        "\tat app//com.yworks.test.Test.run(Test.java:0)",
      });
  }

  @Test
  public void testOverloadQualifiedName() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      new String[] {
        "A.A.A.A.B",
        "A.A.A.A.Z",
      },
      new String[] {
        "com.yworks.test.Test.isEnabled|setEnabled(boolean)",
        "com.yworks.test.Test.Z",
      });
  }

  @Test
  public void testOverloadStacktraceEntry() throws Exception {
    deobfuscate(
      insert(MAPPINGS, ""),
      new String[] {
        "\tat A.A.A.A.B()",
      },
      new String[] {
        "\tat com.yworks.test.Test.isEnabled|setEnabled(boolean)(Test.java:0)",
      });
  }

  @Test
  public void testPrefixedQualifiedName() throws Exception {
    deobfuscate(
      insert(MAPPINGS, "yguard/"),
      // obfuscated input
      new String[] {
        "yguard.A.A.A.A",
        "yguard.A.A.A.B$C",
      },
      // expected deobfuscated output
      new String[] {
        "com.yworks.test.Test",
        "com.yworks.test.EnclosingClass$InnerClass",
      });
  }

  @Test
  public void testPrefixedStacktraceEntry() throws Exception {
    deobfuscate(
      insert(MAPPINGS, "yguard/"),
      new String[] {
        "\tat yguard.A.A.A.A.A()",
        "\tat yguard.A.A.A.B$C.A()",
      },
      new String[] {
        "\tat com.yworks.test.Test.run(Test.java:0)",
        "\tat com.yworks.test.EnclosingClass$InnerClass.run(EnclosingClass.java:0)",
      });
  }


  private static void deobfuscate(
          final String mappings,
          final String[] input,
          final String[] expected
  ) throws Exception {
    assertEquals("Invalid expected line count", input.length, expected.length);

    final YGuardLogParser parser = new YGuardLogParser();
    parser.parse(new InputSource(new StringReader(mappings)));

    final String[] out = parser.translate(input);
//    final StringBuffer sb = new StringBuffer();
//    for (int i = 0; i < out.length; ++i) {
//      sb.append(out[i]).append('\n');
//    }
//    System.out.println(sb.toString());

    assertEquals("Invalid result line count", input.length, out.length);
    for (int i = 0; i < out.length; ++i) {
      assertEquals("Invalid result", expected[i], out[i]);
    }
  }

  private static String insert( final String s, final String prefix ) {
    return s.replaceAll("\\[PREFIX\\]", prefix);
  }


  private static final String MAPPINGS =
      "<yguard version=\"1.5\">\n" +
      "  <expose>\n" +
      "  </expose>\n" +
      "  <map>\n" +
      "    <package name=\"com\" map=\"[PREFIX]A\"/>\n" +
      "    <package name=\"com.yworks\" map=\"A\"/>\n" +
      "    <package name=\"com.yworks.test\" map=\"A\"/>\n" +
      "    <class name=\"com.yworks.test.Test\" map=\"A\"/>\n" +
      "    <method class=\"com.yworks.test.Test\" name=\"void run()\" map=\"A\"/>\n" +
      "    <method class=\"com.yworks.test.Test\" name=\"boolean isEnabled()\" map=\"B\"/>\n" +
      "    <method class=\"com.yworks.test.Test\" name=\"void setEnabled(boolean)\" map=\"B\"/>\n" +
      "    <class name=\"com.yworks.test.EnclosingClass\" map=\"B\"/>\n" +
      "    <class name=\"com.yworks.test.EnclosingClass$InnerClass\" map=\"C\"/>\n" +
      "    <method class=\"com.yworks.test.EnclosingClass$InnerClass\" name=\"void run()\" map=\"A\"/>\n" +
      "    <class name=\"com.yworks.test.DollarSign\" map=\"$A\"/>\n" +
      "    <method class=\"com.yworks.test.DollarSign\" name=\"void run()\" map=\"$A\"/>\n" +
      "    <class name=\"com.yworks.test.DollarSign$InnerClass\" map=\"$C\"/>\n" +
      "    <method class=\"com.yworks.test.DollarSign$InnerClass\" name=\"void run()\" map=\"$A\"/>\n" +
      "  </map>\n" +
      "</yguard>\n";
}
