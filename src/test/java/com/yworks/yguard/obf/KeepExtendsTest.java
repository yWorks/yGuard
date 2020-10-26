package com.yworks.yguard.obf;

import com.yworks.util.Compiler;
import com.yworks.yshrink.YShrinkModel;
import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.InOutPair;
import com.yworks.yshrink.YShrinkModelImpl;
import com.yworks.yshrink.core.URLCpResolver;
import com.yworks.yshrink.model.Model;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
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
    /**
     * The Name.
     */
    @Rule
  public TestName name = new TestName();

    /**
     * Test extends.
     *
     * @throws Exception the exception
     */
    @Test
  public void testExtends() throws Exception {
    impl(new TypeStruct("asm/AbstractBaseClass.txt", "com.yworks.ext.test.AbstractBaseClass"),
         new TypeStruct("asm/Impl.txt", "com.yworks.impl.test.Impl"),
         new TypeStruct("asm/Main.txt", "com.yworks.impl.test.Main"),
         new TypeStruct("asm/Sample.txt", "com.yworks.impl.test.Sample"));
  }

    /**
     * Test nests.
     *
     * @throws Exception the exception
     */
    @Test
  public void testNests() throws Exception {
    impl(new TypeStruct("asm/OuterClass.txt", "com.yworks.yguard.obf.asm.OuterClass"));
  }

  private void impl( final TypeStruct... types ) throws Exception {
    assertTrue("Invalid Java version", 11 <= getMajorVersion());


    final com.yworks.util.Compiler compiler = Compiler.newCompiler();

    // resolve java source code and create corresponding source model items
    final ArrayList sources = new ArrayList();

    final Class resolver = getClass();
    for (int i = 0; i < types.length; ++i) {
      final URL source = resolver.getResource(types[i].fileName);
      assertNotNull("Could not resolve " + types[i].fileName + '.', source);

      sources.add(compiler.newUrlSource(types[i].typeName, source));
    }


    // compile the java source code
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);


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
      model.setClassResolver(new URLCpResolver(new URL[] {inTmp.toURI().toURL()}));

      wrapper.createSimpleModel(bags);
    } finally {

      // clean up and remove temporary files
      inTmp.delete();
      outTmp.delete();
    }
  }

  private static Model getModel( final YShrinkModel wrapper ) throws Exception {
    final Class c = wrapper.getClass();
    final Field f = c.getDeclaredField("model");
    f.setAccessible(true);
    return (Model) f.get(wrapper);
  }



  private static final class TypeStruct {
      /**
       * The File name.
       */
      final String fileName;
      /**
       * The Type name.
       */
      final String typeName;

      /**
       * Instantiates a new Type struct.
       *
       * @param fileName the file name
       * @param typeName the type name
       */
      TypeStruct( final String fileName, final String typeName ) {
      this.fileName = fileName;
      this.typeName = typeName;
    }
  }
}
