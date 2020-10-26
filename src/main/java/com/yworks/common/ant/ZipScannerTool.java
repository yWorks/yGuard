/*
 * ZipScanner.java
 *
 * Created on October 15, 2002, 9:55 AM
 */

package com.yworks.common.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.ZipScanner;
import org.apache.tools.zip.ZipEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipInputStream;

/**
 * The type Zip scanner tool.
 *
 * @author muellese
 */
public class ZipScannerTool
{
  
  /** Creates a new instance of ZipScanner */
  private ZipScannerTool()
  {
  }

    /**
     * Get matches string [ ].
     *
     * @param fs      the fs
     * @param scanner the scanner
     * @return the string [ ]
     * @throws IOException the io exception
     */
    public static String[] getMatches(ZipFileSet fs, DirectoryScanner scanner) throws IOException{
    Collection result = getMatchedCollection(fs, scanner);
    return (String[])(result.toArray(new String[result.size()]));
  }

    /**
     * Gets matched collection.
     *
     * @param fs      the fs
     * @param scanner the scanner
     * @return the matched collection
     * @throws IOException the io exception
     */
    public static Collection getMatchedCollection(ZipFileSet fs, DirectoryScanner scanner) throws IOException{
    return getMatchedCollection(fs,scanner,"");
  }

    /**
     * Zip file set get src file.
     *
     * @param fs the fs
     * @return the file
     */
    public static File zipFileSetGetSrc(ZipFileSet fs) {
    Method ant15 = null;
    Method ant16 = null;
    try {
      ant15 = fs.getClass().getMethod("getSrc", new Class[]{});
    } catch (NoSuchMethodException nsme){
      try{
        ant16 = fs.getClass().getMethod("getSrc", new Class[]{Project.class});
      } catch (NoSuchMethodException nsme2){
        throw new BuildException("Could not determine getSrc method of ZipFileSet class");
      }
    }
    try {
      if (ant16 != null){
        return (File) ant16.invoke(fs, new Object[]{fs.getProject()});
      } else {
        return (File) ant15.invoke(fs, (Object[])null);
      }
    } catch (IllegalAccessException iaex){
      throw new BuildException("Could not invoke getSrc method of ZipFileSet class", iaex);
    } catch (InvocationTargetException itex){
      if (itex.getTargetException() instanceof BuildException){
        throw (BuildException) itex.getTargetException();
      } else {
        throw new BuildException("Internal error: getSrc invocation failed! "+itex.getTargetException().getMessage());
      }
    }
  }

    /**
     * Gets matched collection.
     *
     * @param fs      the fs
     * @param scanner the scanner
     * @param baseDir the base dir
     * @return the matched collection
     * @throws IOException the io exception
     */
    public static Collection getMatchedCollection(ZipFileSet fs, DirectoryScanner scanner, String baseDir) throws IOException{
    Collection result = new ArrayList(20);
    File zipSrc = zipFileSetGetSrc(fs);
    ZipScanner zipScanner = (ZipScanner) scanner;
    ZipEntry entry;
    java.util.zip.ZipEntry origEntry;
    ZipInputStream in = null;
    try {
        in = new ZipInputStream(new FileInputStream(zipSrc));
        while ((origEntry = in.getNextEntry()) != null) {
            entry = new ZipEntry(origEntry);
            String vPath = entry.getName();
            //System.out.println(vPath);
            if (zipScanner.match(vPath)) {
              result.add(vPath);
            }
        }
    } finally {
        if (in != null) {
            in.close();
        }
    }
    return result;
  }
  
}
