package com.yworks.yguard.obf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests that obfuscated records still work through
 * {@code java.lang.reflect.RecordComponent}.
 * <p>
 * Each component of a record has a {@code Signature} for its generic type and
 * the name of its accessor method. Both have to be renamed consistently with
 * the rest of the class, or {@code RecordComponent.getGenericType()} throws
 * {@code TypeNotPresentException} and {@code RecordComponent.getAccessor()}
 * returns {@code null}. Reflective libraries and record serialization rely on
 * both.
 * </p>
 */
public class RecordsTest extends AbstractObfuscationTest {
  @Rule
  public TestName name = new TestName();

  static final int OPCODES_ASM = Opcodes.ASM9;

  private static final String TYPE_NAME = "com.yworks.yguard.obf.RecordComponents";
  private static final String RUN_METHOD = "void run(java.io.PrintStream)";

  /**
   * The output of {@code RecordComponents.run} for the unobfuscated records.
   * Values that differ per component show if an accessor belongs to another
   * component.
   */
  private static final String EXPECTED = String.format(
    "0: 2 components%n" +
    "0.0 java.util.List<Item> = [item item]%n" +
    "0.1 java.lang.String = box%n" +
    "1: 2 components%n" +
    "1.0 java.lang.String = left%n" +
    "1.1 java.lang.String = right%n" +
    "2: 2 components%n" +
    "2.0 type variable bounded by Item = Item value%n" +
    "2.1 java.util.Map<java.lang.String, java.util.List<type variable bounded by Item>> = {key=[item item]}%n" +
    "3: 1 components%n" +
    "3.0 java.lang.String = trimmed%n" +
    "4: 1 components%n" +
    "4.0 java.lang.String = person%n" +
    "5: 1 components%n" +
    "5.0 java.lang.String = alias%n" +
    "6: 3 components%n" +
    "6.0 int = 7%n" +
    "6.1 double = 2.5%n" +
    "6.2 long[] = [1, 2]%n" +
    "7: 0 components%n" +
    "8: 2 components%n" +
    "8.0 Pair = Pair value%n" +
    "8.1 Box[] = []%n");

  /**
   * Checks the test records themselves, so that a failure of the obfuscated
   * variant cannot stem from the test.
   *
   * @throws Exception the exception
   */
  @Test
  public void testUnobfuscatedRecords() throws Exception {
    if (16 <= getMajorVersion()) {
      final File in = compileRecords();
      try {
        assertEquals("Wrong test output", EXPECTED, run(in, TYPE_NAME, "run"));
      } finally {
        in.delete();
      }
    } else {
      System.err.println("Run test with Java 16 or newer.");
    }
  }

  /**
   * Tests that the generic types and accessors of obfuscated record
   * components can be used through reflection.
   *
   * @throws Exception the exception
   */
  @Test
  public void testObfuscatedRecordsSupportReflection() throws Exception {
    if (16 <= getMajorVersion()) {
      final Obfuscated result = obfuscate(new ArrayList());
      try {
        assertEquals("Wrong test output", EXPECTED, result.run());
      } finally {
        result.delete();
      }
    } else {
      System.err.println("Run test with Java 16 or newer.");
    }
  }

  /**
   * Tests that every obfuscated record component names an accessor of its
   * type and has the same generic signature as that accessor.
   *
   * @throws Exception the exception
   */
  @Test
  public void testObfuscatedRecordComponentAttributes() throws Exception {
    if (16 <= getMajorVersion()) {
      final Obfuscated result = obfuscate(new ArrayList());
      try {
        final List<ClassModel> records = readRecords(result.out);
        assertEquals("Wrong number of records", 9, records.size());

        int signatures = 0;
        for (ClassModel record : records) {
          for (ComponentModel component : record.components) {
            assertComponentMatchesAccessor(record, component);
            if (component.signature != null) {
              ++signatures;
              assertFalse(
                "Original class name in signature of " + record.name + '#' + component.name +
                ": " + component.signature,
                component.signature.contains("RecordComponents$"));
            }
          }
        }
        assertEquals("Wrong number of generic components", 3, signatures);
      } finally {
        result.delete();
      }
    } else {
      System.err.println("Run test with Java 16 or newer.");
    }
  }

  /**
   * Tests a record whose class and member names are retained while the
   * classes its components refer to are obfuscated.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRetainedRecordReferringToObfuscatedClasses() throws Exception {
    if (16 <= getMajorVersion()) {
      final String boxName = "com/yworks/yguard/obf/RecordComponents$Box";
      final YGuardRule rule = new YGuardRule(YGuardRule.TYPE_CLASS, boxName);
      rule.retainClasses = YGuardRule.LEVEL_PRIVATE;
      rule.retainMethods = YGuardRule.LEVEL_PRIVATE;
      rule.retainFields = YGuardRule.LEVEL_PRIVATE;
      final ArrayList rules = new ArrayList();
      rules.add(rule);

      final Obfuscated result = obfuscate(rules);
      try {
        assertEquals("Wrong test output", EXPECTED, result.run());

        ClassModel box = null;
        for (ClassModel record : readRecords(result.out)) {
          if (boxName.equals(record.name)) {
            box = record;
          }
        }
        assertNotNull("Retained record " + boxName + " not found", box);
        assertEquals("Wrong number of components", 2, box.components.size());
        assertEquals("items", box.components.get(0).name);
        assertEquals("label", box.components.get(1).name);
        for (ComponentModel component : box.components) {
          assertComponentMatchesAccessor(box, component);
        }

        final String signature = box.components.get(0).signature;
        assertNotNull("Missing signature", signature);
        assertFalse(
          "Original class name in signature: " + signature,
          signature.contains("RecordComponents$Item"));
      } finally {
        result.delete();
      }
    } else {
      System.err.println("Run test with Java 16 or newer.");
    }
  }

  private static void assertComponentMatchesAccessor(
    final ClassModel record, final ComponentModel component
  ) {
    final String key = component.name + "()" + component.descriptor;
    assertTrue(
      "No accessor " + key + " in " + record.name + " for component " + component.name,
      record.methods.containsKey(key));
    final String accessorSignature = record.methods.get(key);
    assertEquals(
      "Signature of " + record.name + '#' + component.name,
      accessorSignature == null ? null : accessorSignature.substring(2),
      component.signature);
  }

  private File compileRecords() throws Exception {
    final ByteArrayOutputStream baos = compile(
      getClass(), new TypeStruct("RecordComponents.txt", TYPE_NAME));
    final File in = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    write(baos.toByteArray(), in);
    return in;
  }

  private Obfuscated obfuscate( final List rules ) throws Exception {
    final File in = compileRecords();
    final File out = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    final StringWriter log = new StringWriter();
    final PrintWriter pw = new PrintWriter(log);
    final GuardDB db = new GuardDB(new File[]{in});
    db.setDigests(new String[0]);
    if (!rules.isEmpty()) {
      db.retain(rules, pw);
    }
    db.remapTo(new File[]{out}, null, pw, false);
    db.close();
    pw.flush();

    return new Obfuscated(in, out, Mapper.newInstance(log.toString()));
  }

  private static String run(
    final File jar, final String typeName, final String methodName
  ) throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final URLClassLoader cl = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()});
    try {
      final Class type = Class.forName(typeName, true, cl);
      final Method run = type.getMethod(methodName, PrintStream.class);
      run.invoke(null, new PrintStream(output, true, "UTF-8"));
    } finally {
      cl.close();
    }
    return new String(output.toByteArray(), "UTF-8");
  }

  private static List<ClassModel> readRecords( final File jar ) throws Exception {
    final List<ClassModel> records = new ArrayList<ClassModel>();
    final JarInputStream jis = new JarInputStream(new FileInputStream(jar));
    try {
      for (JarEntry entry = jis.getNextJarEntry();
           entry != null;
           entry = jis.getNextJarEntry()) {
        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
          final ClassModel model = new ClassModel();
          new ClassReader(jis).accept(new ModelVisitor(model), 0);
          if (model.record) {
            records.add(model);
          }
        }
      }
    } finally {
      jis.close();
    }
    return records;
  }


  private static final class Obfuscated {
    final File in;
    final File out;
    final Mapper mapper;

    Obfuscated( final File in, final File out, final Mapper mapper ) {
      this.in = in;
      this.out = out;
      this.mapper = mapper;
    }

    String run() throws Exception {
      // the log maps only renamed names; retaining a nested record also
      // retains the name of the class that encloses it
      final String typeName = mapper.getTypeName(TYPE_NAME);
      final String methodName = mapper.getMethodName(TYPE_NAME, RUN_METHOD);
      return RecordsTest.run(
        out,
        typeName == null ? TYPE_NAME : typeName,
        methodName == null ? "run" : methodName);
    }

    void delete() {
      in.delete();
      out.delete();
    }
  }

  private static final class ClassModel {
    String name;
    boolean record;
    /** Generic signature, or null, by method name plus descriptor. */
    final Map<String, String> methods = new HashMap<String, String>();
    final List<ComponentModel> components = new ArrayList<ComponentModel>();
  }

  private static final class ComponentModel {
    final String name;
    final String descriptor;
    final String signature;

    ComponentModel( final String name, final String descriptor, final String signature ) {
      this.name = name;
      this.descriptor = descriptor;
      this.signature = signature;
    }
  }

  private static final class ModelVisitor extends ClassVisitor {
    private final ClassModel model;

    ModelVisitor( final ClassModel model ) {
      super(OPCODES_ASM);
      this.model = model;
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
      model.name = name;
      model.record = (access & Opcodes.ACC_RECORD) != 0;
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(
      final String name, final String descriptor, final String signature
    ) {
      model.components.add(new ComponentModel(name, descriptor, signature));
      return null;
    }

    @Override
    public MethodVisitor visitMethod(
      final int access,
      final String name,
      final String descriptor,
      final String signature,
      final String[] exceptions
    ) {
      if ((access & Opcodes.ACC_STATIC) == 0) {
        model.methods.put(name + descriptor, signature);
      }
      return null;
    }
  }
}
