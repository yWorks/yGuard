package com.yworks.yguard.obf;

import com.yworks.util.Compiler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Tests if {@code MethodParameters} bytecode attributes are properly handled.
 *
 * @author Thomas Behr
 */
public class MethodParametersTest extends AbstractObfuscationTest {
    /**
     * The Name.
     */
    @Rule
  public TestName name = new TestName();
    /**
     * The Opcodes asm.
     */
    static final int OPCODES_ASM = Opcodes.ASM7;

    /**
     * Test simple method parameters.
     *
     * @throws Exception the exception
     */
    @Test
  public void testSimpleMethodParameters() throws Exception {
    impl(false);
  }

    /**
     * Test retain method parameters.
     *
     * @throws Exception the exception
     */
    @Test
  public void testRetainMethodParameters() throws Exception {
    impl(true);
  }

  private void impl( final boolean retainAttr ) throws Exception {
    // The compiler implementation is guaranteed to work only for Java 11 and up
    assertTrue("Invalid Java version", 11 <= getMajorVersion());


    final String testTypeName = "com.yworks.yguard.obf.SimpleMethodParametersTest";

    // look for java source code that will be compiled with MethodParameters
    final String fileName = "SimpleMethodParametersTest.txt";
    final URL source = getClass().getResource(fileName);
    assertNotNull("Could not resolve " + fileName + '.', source);


    // compile the java source code
    final com.yworks.util.Compiler compiler = Compiler.newCompiler();
    compiler.addOption("-parameters");

    final ArrayList sources = new ArrayList();
    sources.add(compiler.newUrlSource(testTypeName, source));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);


    // store resulting bytecode in temporary files and ...
    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      assertMethodParameters(inTmp, true);

      final ByteArrayOutputStream errOut = new ByteArrayOutputStream();
      System.setErr(new PrintStream(errOut));

      final ArrayList rules = new ArrayList();
      if (retainAttr) {
        rules.add(new YGuardRule(
                YGuardRule.TYPE_ATTR2,
                "MethodParameters",
                testTypeName.replace('.', '/')));
      }

      // ... run obfuscator
      final StringWriter log = new StringWriter();
      final PrintWriter pw = new PrintWriter(log);
      final GuardDB db = new GuardDB(new File[]{inTmp});
      db.setDigests(new String[0]);
      db.retain(rules, pw);
      db.remapTo(new File[] {outTmp}, null, pw, false);
      db.close();

      final String s = new String(errOut.toByteArray());
      final StringTokenizer st = new StringTokenizer(s, "\n");
      assertEquals("\n" + s, 0, st.countTokens());

      // ... finally check obfuscation result
      assertMethodParameters(outTmp, retainAttr);
    } finally {

      if (!retainAttr) {
        inTmp.delete();
        outTmp.delete();
      }
    }
  }

  private void assertMethodParameters(
          final File jar, final boolean expected
  ) throws Exception {
    final Boolean unexpectedValue = expected ? Boolean.FALSE : Boolean.TRUE;
    String unexpected = null;

    final JarInputStream jis = new JarInputStream(new FileInputStream(jar));
    try {
      for (JarEntry entry = jis.getNextJarEntry();
           entry != null && unexpected == null;
           entry = jis.getNextJarEntry()) {
        if (!entry.isDirectory() &&
            entry.getName().toLowerCase().endsWith(".class")) {
          final Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
          new ClassReader(jis).accept(new MethodParameterVisitor(result), 0);
          unexpected = getKeyFor(result, unexpectedValue);
        }
      }
    } finally {
      try {
        jis.close();
      } catch (Exception ex) {
        // ignore
      }
    }

    if (unexpected != null) {
      assertEquals(
              (expected ? "Missing " : "Unexpected ") +
              "MethodParameters attribute for " + unexpected,
              expected,
              !expected);
    }
  }

  private static <K, V> K getKeyFor( final Map<K, V> map, final V value ) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (value == null ? entry.getValue() == null : value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }


  private static final class MethodParameterVisitor extends ClassVisitor {
      /**
       * The Prefix.
       */
      String prefix;
      /**
       * The Has method parameters attribute.
       */
      final Map<String, Boolean> hasMethodParametersAttribute;

      /**
       * Instantiates a new Method parameter visitor.
       *
       * 
		 * @param hasMethodParametersAttribute the has method parameters attribute
       */
      MethodParameterVisitor( final Map<String, Boolean> hasMethodParametersAttribute ) {
      super(OPCODES_ASM);
      this.prefix = "";
      this.hasMethodParametersAttribute = hasMethodParametersAttribute;
    }

    @Override
    public void visit(
            final int version,
            final int access,
            final String name,
            final String signature,
            final String superName,
            final String[] interfaces
    ) {
      if (name != null && name.length() > 0) {
        prefix = name.replace('/', '.') + '#';
      }
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions
    ) {
      if (descriptor != null && !descriptor.startsWith("()")) {
        final String methodId = newMethodId(name, descriptor);
        hasMethodParametersAttribute.put(methodId, Boolean.FALSE);
        return new ParameterVisitor(methodId, hasMethodParametersAttribute);
      } else {
        return null;
      }
    }

    private String newMethodId( final String name, final String descriptor ) {
      final StringBuilder sb = new StringBuilder();
      sb.append(prefix).append(name);
      return new SignatureBuilder().appendSignature(sb, descriptor).toString();
    }
  }

  private static final class ParameterVisitor extends MethodVisitor {
    private final String methodId;
    private final Map<String, Boolean> hasMethodParametersAttribute;

      /**
       * Instantiates a new Parameter visitor.
       *
       * 
		 * @param methodId                     the method id
       * 
		 * @param hasMethodParametersAttribute the has method parameters attribute
       */
      ParameterVisitor(
            final String methodId,
            final Map<String, Boolean> hasMethodParametersAttribute
    ) {
      super(OPCODES_ASM);
      this.methodId = methodId;
      this.hasMethodParametersAttribute = hasMethodParametersAttribute;
    }

    @Override
    public void visitParameter( final String name, final int access ) {
      hasMethodParametersAttribute.put(methodId, Boolean.TRUE);
    }
  }


  /**
   * Converts a binary method descriptor into a human-readable method signature.
   */
  private static final class SignatureBuilder {
    private int dim;
    private boolean type;
    private String del;

      /**
       * Instantiates a new Signature builder.
       */
      SignatureBuilder() {
      dim = 0;
      type = false;
      del = "";
    }

      /**
       * Append signature string builder.
       *
       * 
		 * @param sb         the sb
       * 
		 * @param descriptor the descriptor
       * 
		 * @return the string builder
       */
      StringBuilder appendSignature(
            final StringBuilder sb, final String descriptor
    ) {
      dim = 0;
      type = false;
      del = "";

      for (int i = 0, n = descriptor.length(); i < n; ++i) {
        final char c = descriptor.charAt(i);
        if (type) {
          switch (c) {
            case '/':
              sb.append('.');
              break;
            case ';':
              type = false;
              appendArrayAndReset(sb);
              break;
            default:
              sb.append(c);
              break;
          }
        } else {
          switch (c) {
            case '(':
              sb.append('(');
              break;
            case ')':
              sb.append(')');
              i = Integer.MAX_VALUE - 1;
              break;
            case '[':
              ++dim;
              break;
            case 'L':
              sb.append(del);
              type = true;
              break;
            case 'B':
              appendPrimitive(sb, "byte");
              break;
            case 'C':
              appendPrimitive(sb, "char");
              break;
            case 'D':
              appendPrimitive(sb, "double");
              break;
            case 'F':
              appendPrimitive(sb, "float");
              break;
            case 'I':
              appendPrimitive(sb, "int");
              break;
            case 'J':
              appendPrimitive(sb, "long");
              break;
            case 'S':
              appendPrimitive(sb, "short");
              break;
            case 'Z':
              appendPrimitive(sb, "boolean");
              break;
          }
        }
      }
      return sb;
    }

    private StringBuilder appendPrimitive(
            final StringBuilder sb, final String typeName
    ) {
      sb.append(del).append(typeName);
      appendArrayAndReset(sb);
      return sb;
    }

    private StringBuilder appendArrayAndReset(
            final StringBuilder sb
    ) {
      appendArray(sb, dim);
      dim = 0;
      del = ",";
      return sb;
    }

    private static StringBuilder appendArray(
            final StringBuilder sb, final int dim
    ) {
      for (int i = 0; i < dim; ++i) {
        sb.append("[]");
      }
      return sb;
    }
  }
}
