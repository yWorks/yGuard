package com.yworks.yguard.obf;

import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.InOutPair;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.YGuardTask;
import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.ExposeSection;
import com.yworks.yshrink.YShrinkModel;
import com.yworks.yshrink.YShrinkModelImpl;
import com.yworks.yshrink.core.ClassResolver;
import com.yworks.yshrink.model.Model;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests if {@code extends} attributes of yGuard's &lt;keep&gt; elements can
 * be processed.
 * <p>
 * Using the {@code extends} attribute results in analyzing classes with ASM.
 * </p>
 *
 * @author Thomas Behr
 */
public class KeepExtendsTest extends AbstractObfuscationTest {
  @Rule
  public TestName name = new TestName();

  @Test
  public void testTaskKeepsExtends() throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());

    final TypeStruct[] types = {
      new TypeStruct("asm/AbstractBaseClass.txt", "com.yworks.ext.test.AbstractBaseClass"),
      new TypeStruct("asm/Impl.txt", "com.yworks.impl.test.Impl"),
      new TypeStruct("asm/Main.txt", "com.yworks.impl.test.Main"),
      new TypeStruct("asm/Sample.txt", "com.yworks.impl.test.Sample")
    };

    final ByteArrayOutputStream baos = compile(getClass(), types);

    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      final YGuardTask task = createTask(inTmp, outTmp);

      final ObfuscatorTask rename = task.createRename();
      final ExposeSection keep = (ExposeSection) rename.createKeep();
      final ClassSection keepClass = keep.createClass();
      keepClass.setExtends("com.yworks.ext.test.AbstractBaseClass");

      task.execute();

      final List<String> entries = listClassEntries(outTmp);
      assertNotNull(entries);
      assertTrue("com/yworks/ext/test/AbstractBaseClass.class should exist",
        entries.contains("com/yworks/ext/test/AbstractBaseClass.class"));
      assertTrue("com/yworks/impl/test/Impl.class should exist",
        entries.contains("com/yworks/impl/test/Impl.class"));
      assertFalse("com/yworks/impl/test/Main.class should not exist",
        entries.contains("com/yworks/impl/test/Main.class"));
      assertFalse("com/yworks/impl/test/Sample.class should not exist",
        entries.contains("com/yworks/impl/test/Sample.class"));
    } finally {
      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }

  @Test
  public void testTaskKeepsImplements() throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());

    final TypeStruct[] types = {
      new TypeStruct("asm/AnInterface.txt", "com.yworks.ext.test.AnInterface"),
      new TypeStruct("asm/AnInterfaceImpl.txt", "com.yworks.impl.test.AnInterfaceImpl"),
      new TypeStruct("asm/AnInterfaceUsage.txt", "com.yworks.impl.test.AnInterfaceUsage"),
    };

    final ByteArrayOutputStream baos = compile(getClass(), types);

    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);

      final YGuardTask task = createTask(inTmp, outTmp);

      final ObfuscatorTask rename = task.createRename();
      final ExposeSection keep = (ExposeSection) rename.createKeep();
      final ClassSection keepClass = keep.createClass();
      keepClass.setImplements("com.yworks.ext.test.AnInterface");

      task.execute();

      final List<String> entries = listClassEntries(outTmp);
      assertNotNull(entries);
      assertTrue("com/yworks/ext/test/AnInterface.class should exist",
        entries.contains("com/yworks/ext/test/AnInterface.class"));
      assertTrue("com/yworks/impl/test/AnInterfaceImpl.class should exist",
        entries.contains("com/yworks/impl/test/AnInterfaceImpl.class"));
      assertFalse("com/yworks/impl/test/AnInterfaceUsage.class should not exist",
        entries.contains("com/yworks/impl/test/AnInterfaceUsage.class"));
    } finally {
      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }

  @Test
  public void testExtends() throws Exception {
    impl(new TypeStruct("asm/AbstractBaseClass.txt", "com.yworks.ext.test.AbstractBaseClass"),
         new TypeStruct("asm/Impl.txt", "com.yworks.impl.test.Impl"),
         new TypeStruct("asm/Main.txt", "com.yworks.impl.test.Main"),
         new TypeStruct("asm/Sample.txt", "com.yworks.impl.test.Sample"));
  }

  @Test
  public void testNests() throws Exception {
    impl(new TypeStruct("asm/OuterClass.txt", "com.yworks.yguard.obf.asm.OuterClass"));
  }

  @Test
  public void testPermittedSubclasses() throws Exception {
    // PermittedSubclasses attribute is used only in Java 17 and newer
    if (17 <= getMajorVersion()) {
      impl(new TypeStruct("asm/SealedClassImpl.txt", "com.yworks.yguard.obf.asm.SealedClassImpl"),
           new TypeStruct("asm/SealedSerializableClass.txt", "com.yworks.yguard.obf.asm.SealedSerializableClass"),
           new TypeStruct("asm/SimpleClass.txt", "com.yworks.yguard.obf.asm.SimpleClass"));
    } else {
      System.err.println("Run test with Java 17 or newer.");
    }
  }

  private void impl( final TypeStruct... types ) throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());

    // compile the java source code
    final ByteArrayOutputStream baos = compile(getClass(), types);

    // store resulting bytecode in temporary files and ...
    final File inTmp = File.createTempFile(name.getMethodName() + "_in_", ".jar");
    final File outTmp = File.createTempFile(name.getMethodName() + "_out_", ".jar");

    try {
      write(baos.toByteArray(), inTmp);


      // ... create a shrinker model to trigger ASM byte code analysis 
      final InOutPair pair = new InOutPair();
      pair.setIn(inTmp);
      pair.setOut(outTmp);
      final ArrayList<ShrinkBag> bags = new ArrayList<ShrinkBag>();
      bags.add(pair);

      final YShrinkModel wrapper =  new YShrinkModelImpl();
      final Model model = getModel(wrapper);
      model.setClassResolver(new UrlResolver(inTmp.toURI().toURL()));

      wrapper.createSimpleModel(bags);
    } finally {

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }

  private static Model getModel( final YShrinkModel wrapper ) throws Exception {
    final Class<?> c = wrapper.getClass();
    final Field f = c.getDeclaredField("model");
    f.setAccessible(true);
    return (Model) f.get(wrapper);
  }

  private static YGuardTask createTask( final File inTmp, final File outTmp ) {
    final Project project = new Project();
    project.init();

    final YGuardTask task = new YGuardTask();
    task.setProject(project);
    task.setTaskType("yguard");
    task.setTaskName("yguard");
    task.setOwningTarget(new Target());

    final ShrinkBag pair = task.createInOutPair();
    pair.setIn(inTmp);
    pair.setOut(outTmp);

    return task;
  }

  private static List<String> listClassEntries( final File file ) throws Exception {
    final ArrayList<String> result = new ArrayList<>();
    try (JarFile archive = new JarFile(file)) {
      for (Enumeration<JarEntry> entries = archive.entries();
           entries.hasMoreElements();) {
        final JarEntry entry = entries.nextElement();
        if (entry.isDirectory()) {
          continue;
        }

        final String name = entry.getName();
        if (name.toLowerCase().endsWith(".class")) {
          result.add(name);
        }
      }
    }
    return result;
  }

  private static final class UrlResolver implements ClassResolver {
    private URLClassLoader urlClassLoader;

    UrlResolver( final URL url ) {
      urlClassLoader = URLClassLoader.newInstance(new URL[]{url});
    }

    @Override
    public Class resolve( final String className ) throws ClassNotFoundException {
      try {
        return Class.forName( className, false, urlClassLoader );
      } catch ( NoClassDefFoundError ncdfe ) {
        String message = ncdfe.getMessage();
        if ( message == null || message.equals( className ) ) {
          message = className;
        } else {
          message = message + "[" + className + "]";
        }
        throw new ClassNotFoundException( message, ncdfe );
      } catch ( LinkageError le ) {
        throw new ClassNotFoundException( className, le );
      }
    }

    @Override
    public void close() throws Exception {
      urlClassLoader.close();
    }
  }
}
