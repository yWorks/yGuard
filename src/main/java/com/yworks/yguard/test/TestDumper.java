/*
 * TestDumper.java
 *
 * Created on April 20, 2005, 12:11 PM
 */

package com.yworks.yguard.test;

import com.yworks.yguard.obf.classfile.ClassFile;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author muellese
 */
public class TestDumper
{
  
  /** Creates a new instance of TestDumper */
  public TestDumper()
  {
  }
  
  public static void main(String... args) throws IOException {
    PrintWriter pw = new PrintWriter(System.out);
    ClassFile.create(new DataInputStream(ClassLoader.getSystemResourceAsStream("com/yworks/yguard/test/Generics$1.class"))).dump(pw);
    pw.flush();
  }
  
}
