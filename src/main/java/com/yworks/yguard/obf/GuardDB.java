/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

import com.yworks.util.Version;
import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.ArchiveWriter;
import com.yworks.util.abstractjar.Entry;
import com.yworks.util.abstractjar.Factory;
import com.yworks.yguard.Conversion;
import com.yworks.yguard.ObfuscationListener;
import com.yworks.yguard.ParseException;
import com.yworks.yguard.obf.classfile.ClassConstants;
import com.yworks.yguard.obf.classfile.ClassFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

/**
 * Classfile database for obfuscation.
 *
 * @author Mark Welsh
 */
public class GuardDB implements ClassConstants
{
  // Constants -------------------------------------------------------------
  private static final String STREAM_NAME_MANIFEST = "META-INF/MANIFEST.MF";
  private static final String MANIFEST_NAME_TAG = "Name";
  private static final String MANIFEST_DIGESTALG_TAG = "Digest-Algorithms";
  private static final String CLASS_EXT = ".class";
  private static final String SIGNATURE_PREFIX = "META-INF/";
  private static final String SIGNATURE_EXT = ".SF";
  private static final String LOG_MEMORY_USED = "  Memory in use after class data structure built: ";
  private static final String LOG_MEMORY_TOTAL = "  Total memory available                        : ";
  private static final String LOG_MEMORY_BYTES = " bytes";
  private static final String WARNING_SCRIPT_ENTRY_ABSENT = "<!-- WARNING - identifier from script file not found in JAR: ";
  private static final String ERROR_CORRUPT_CLASS = "<!-- ERROR - corrupt class file: ";


  // Fields ----------------------------------------------------------------
  private Archive[] inJar;          // JAR file for obfuscation
  private Manifest[] oldManifest;   // MANIFEST.MF
  private Manifest[] newManifest;   // MANIFEST.MF
  private ClassTree classTree;    // Tree of packages, classes. methods, fields
  private boolean hasMap = false;

  /** Utility field holding list of Listeners. */
  private transient java.util.ArrayList listenerList;

  /** Holds value of property replaceClassNameStrings. */
  private boolean replaceClassNameStrings;

  /** Holds value of property pedantic. */
  private boolean pedantic;

  private ResourceHandler resourceHandler;
  private String[] digestStrings;

  // Has the mapping been generated already?

  // Class Methods ---------------------------------------------------------

  // Instance Methods ------------------------------------------------------

  /**
   * A classfile database for obfuscation.
   *
   * @param inFile the in file
   * @throws IOException the io exception
   */
  public GuardDB(File[] inFile) throws java.io.IOException
  {
    inJar = new Archive[inFile.length];
    for(int i = 0; i < inFile.length; i++)
      inJar[i] = Factory.newArchive(inFile[i]);
  }

  /** Close input JAR file and log-file at GC-time. */
  protected void finalize() throws java.io.IOException
  {
    close();
  }

  /**
   * Sets resource handler.
   *
   * @param handler the handler
   */
  public void setResourceHandler(ResourceHandler handler)
  {
    resourceHandler = handler;
  }

  /**
   * Gets out name.
   *
   * @param inName the in name
   * @return the out name
   */
  public String getOutName(String inName)
  {
    return classTree.getOutName(inName);
  }

  /**
   * Go through database marking certain entities for retention, while
   * maintaining polymorphic integrity.
   *
   * @param rgsEntries the rgs entries
   * @param log        the log
   * @throws IOException the io exception
   */
  public void retain(Collection rgsEntries, PrintWriter log)throws java.io.IOException
  {

    // Build database if not already done, or if a mapping has already been generated
    if (classTree == null || hasMap)
    {
      hasMap = false;
      buildClassTree(log);
    }

    // look for obfuscation annotations that indicate retention
    retainByAnnotation();


    // Enumerate the entries in the RGS script
    retainByRule(rgsEntries, log);
  }

  private void retainByAnnotation() {
    classTree.walkTree(new TreeAction(){

      private ObfuscationConfig getApplyingObfuscationConfig(Cl cl){
        ObfuscationConfig obfuscationConfig = cl.getObfuscationConfig();
        if (cl.getObfuscationConfig() != null && obfuscationConfig.applyToMembers){
          return obfuscationConfig;
        }
        Cl currentCl = cl;
        // walk to the first class in the parent hierarchy that has applyToMembers set
        while (currentCl.isInnerClass()){
          TreeItem parent = currentCl.getParent();
          if (parent instanceof Cl){
            currentCl = (Cl) parent;
            ObfuscationConfig parentConfig = currentCl.getObfuscationConfig();
            if (parentConfig != null && parentConfig.applyToMembers) {
              return parentConfig;
            }
          } else {
            // if not a cl than stop
            return null;
          }
        }
        // we didn't find anything
        return null;
      }

      @Override
      public void classAction(Cl cl) {
        super.classAction(cl);

        // iterate over the annotations of the class to see if one specifies the obfuscation
        ObfuscationConfig config = cl.getObfuscationConfig();

        if (config != null) {
          if (config.exclude){
            classTree.retainClass(cl.getFullInName(), YGuardRule.LEVEL_PRIVATE, YGuardRule.LEVEL_NONE, YGuardRule.LEVEL_NONE, true);
          }
        } else {
          // no annotation, check parent hierarchy
          ObfuscationConfig parentConfig = getApplyingObfuscationConfig(cl);
          if (parentConfig != null && parentConfig.exclude){
            // a parent has annotation that applies his config to members which is: exclude
            classTree.retainClass(cl.getFullInName(), YGuardRule.LEVEL_PRIVATE, YGuardRule.LEVEL_NONE, YGuardRule.LEVEL_NONE, true);
          }
        }
      }

      @Override
      public void methodAction(Md md) {
        super.methodAction(md);
        // iterate over the annotations of the method to see if one specifies the obfuscation
        ObfuscationConfig config = md.getObfuscationConfig();

        // annotation at method overrides parent annotation
        if (config != null){
          if (config.exclude) {
            classTree.retainMethod(md.getFullInName(), md.getDescriptor());
          }
        } else {
          // no annotation, check parent hierarchy
          ObfuscationConfig parentConfig = getApplyingObfuscationConfig((Cl) md.getParent());
          if (parentConfig != null && parentConfig.exclude){
            // a parent has annotation that applies his config to members which is: exclude
            classTree.retainMethod(md.getFullInName(), md.getDescriptor());
          }
        }
      }

      @Override
      public void fieldAction(Fd fd) {
        super.fieldAction(fd);
        // iterate over the annotations of the field to see if one specifies the obfuscation
        ObfuscationConfig config = fd.getObfuscationConfig();

        // annotation at field overrides parent annotation
        if (config != null){
          if (config.exclude) {
            classTree.retainField(fd.getFullInName());
          }
        } else {
          // no annotation, check parent hierarchy
          ObfuscationConfig parentConfig = getApplyingObfuscationConfig((Cl) fd.getParent());
          if (parentConfig != null && parentConfig.exclude){
            // a parent has annotation that applies his config to members which is: exclude
            classTree.retainField(fd.getFullInName());
          }
        }
      }
    });
  }

  private void retainByRule(Collection rgsEntries, PrintWriter log) {
    for (Iterator it = rgsEntries.iterator(); it.hasNext();)
    {
      YGuardRule entry = (YGuardRule)it.next();
      try
      {
        switch (entry.type)
        {
          case YGuardRule.TYPE_LINE_NUMBER_MAPPER:
            classTree.retainLineNumberTable(entry.name,  entry.lineNumberTableMapper);
            break;
          case YGuardRule.TYPE_SOURCE_ATTRIBUTE_MAP:
            classTree.retainSourceFileAttributeMap(entry.name, entry.obfName);
            break;
          case YGuardRule.TYPE_ATTR:
            classTree.retainAttribute(entry.name);
            break;
          case YGuardRule.TYPE_ATTR2:
            classTree.retainAttributeForClass(entry.descriptor, entry.name);
            break;
          case YGuardRule.TYPE_CLASS:
            classTree.retainClass(entry.name, entry.retainClasses, entry.retainMethods, entry.retainFields, true);
            break;
          case YGuardRule.TYPE_METHOD:
            classTree.retainMethod(entry.name, entry.descriptor);
            break;
          case YGuardRule.TYPE_PACKAGE:
            classTree.retainPackage(entry.name);
            break;
          case YGuardRule.TYPE_FIELD:
            classTree.retainField(entry.name);
            break;
          case YGuardRule.TYPE_PACKAGE_MAP:
            classTree.retainPackageMap(entry.name, entry.obfName);
            break;
          case YGuardRule.TYPE_CLASS_MAP:
            classTree.retainClassMap(entry.name, entry.obfName);
            break;
          case YGuardRule.TYPE_METHOD_MAP:
            classTree.retainMethodMap(entry.name, entry.descriptor,
            entry.obfName);
            break;
          case YGuardRule.TYPE_FIELD_MAP:
            classTree.retainFieldMap(entry.name, entry.obfName);
            break;
          default:
            throw new ParseException("Illegal type: " + entry.type);
        }
      }
      catch (RuntimeException e)
      {
        // DEBUG
        // e.printStackTrace();
        log.println(WARNING_SCRIPT_ENTRY_ABSENT + entry.name + " -->");
      }
    }
  }

  /**
   * Remap each class based on the remap database, and remove attributes.
   *
   * @param out              the out
   * @param fileFilter       the file filter
   * @param log              the log
   * @param conserveManifest the conserve manifest
   * @throws IOException            the io exception
   * @throws ClassNotFoundException the class not found exception
   */
  public void remapTo(File[] out,
    Filter fileFilter,
    PrintWriter log,
    boolean conserveManifest
    ) throws java.io.IOException, ClassNotFoundException
  {
    // Build database if not already done
    if (classTree == null)
    {
      buildClassTree(log);
    }

    // Generate map table if not already done
    if (!hasMap)
    {
      createMap(log);
    }

    oldManifest = new Manifest[out.length];
    newManifest = new Manifest[out.length];
    parseManifest();

    StringBuffer replaceNameLog = new StringBuffer();
    StringBuffer replaceContentsLog = new StringBuffer();

    ArchiveWriter outJar = null;
    // Open the entry and prepare to process it
    DataInputStream inStream = null;
    for(int i = 0; i < inJar.length; i++)
    {
      outJar = null;
      //store the whole jar in memory, I known this might be alot, but anyway
      //this is the best option, if you want to create correct jar files...
      List jarEntries = new ArrayList();
      try
      {
        // Go through the input Jar, removing attributes and remapping the Constant Pool
        // for each class file. Other files are copied through unchanged, except for manifest
        // and any signature files - these are deleted and the manifest is regenerated.
        Enumeration<Entry> entries = inJar[i].getEntries();
        fireObfuscatingJar(inJar[i].getName(), out[i].getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        while (entries.hasMoreElements())
        {
          // Get the next entry from the input Jar
          Entry inEntry = (Entry) entries.nextElement();

          // Ignore directories
          if (inEntry.isDirectory())
          {
            continue;
          }

          inStream = new DataInputStream(
            new BufferedInputStream(
            inJar[i].getInputStream(inEntry)));
          String inName = inEntry.getName();
          if (inName.endsWith(CLASS_EXT))
          {
            if (fileFilter == null || fileFilter.accepts(inName)){
              // Write the obfuscated version of the class to the output Jar
              ClassFile cf = ClassFile.create(inStream);
              fireObfuscatingClass(Conversion.toJavaClass(cf.getName()));
              cf.remap(classTree, replaceClassNameStrings, log);
              JarEntry outEntry = new JarEntry(cf.getName() + CLASS_EXT);

              DataOutputStream classOutputStream;
              if (digestStrings == null){
                digestStrings = new String[]{"SHA-1", "MD5"};
              }
              MessageDigest[] digests = new MessageDigest[digestStrings.length];
              // Create an OutputStream piped through a number of digest generators for the manifest
              classOutputStream = fillDigests(baos, digestStrings, digests);

              // Dump the classfile, while creating the digests
              cf.write(classOutputStream);
              classOutputStream.flush();
              jarEntries.add(new Object[]{outEntry, baos.toByteArray()});
              baos.reset();
              // Now update the manifest entry for the class with new name and new digests
              updateManifest(i, inName, cf.getName() + CLASS_EXT, digests);
            }
          }
          else if (STREAM_NAME_MANIFEST.equals(inName.toUpperCase()) ||
            (inName.length() > (SIGNATURE_PREFIX.length() + 1 + SIGNATURE_EXT.length()) &&
            inName.indexOf(SIGNATURE_PREFIX) != -1 &&
            inName.substring(inName.length() - SIGNATURE_EXT.length(), inName.length()).equals(SIGNATURE_EXT)))
          {
            // Don't pass through the manifest or signature files
            continue;
          }
          else
          {
            // Copy the non-class entry through unchanged
            long size = inEntry.getSize();
            if (size != -1)
            {

              if (digestStrings == null){
                digestStrings = new String[]{"SHA-1", "MD5"};
              }
              MessageDigest[] digests = new MessageDigest[digestStrings.length];
              DataOutputStream dataOutputStream = fillDigests(baos, digestStrings, digests);

              String outName;

              StringBuffer outNameBuffer = new StringBuffer(80);

              if(resourceHandler != null && resourceHandler.filterName(inName, outNameBuffer))
              {
                outName = outNameBuffer.toString();
                if(!outName.equals(inName))
                {
                  replaceNameLog.append("  <resource name=\"");
                  replaceNameLog.append(ClassTree.toUtf8XmlString(inName));
                  replaceNameLog.append("\" map=\"");
                  replaceNameLog.append(ClassTree.toUtf8XmlString(outName));
                  replaceNameLog.append("\"/>\n");
                }
              }
              else
              {
                // This is a "workaround" because classTree is not supposed
                // to work with resource files and "chops off" everything after $ into a new segment.
                // For resource files however this behaviour is wrong.
                // NOTE: It may be better to investigate getOutName but this works like a charm
                String appendName = "";
                if (inName.contains("$")) appendName = inName.substring(inName.lastIndexOf("/"));
                outName = classTree.getOutName(inName);
                if (appendName.length() > 0) {
                  outName = outName.replace(outName.substring(outName.lastIndexOf("/")), appendName);
                }
              }

              if(resourceHandler == null || !resourceHandler.filterContent(inStream, dataOutputStream, inName))
              {
                byte[] bytes = new byte[(int)size];
                inStream.readFully(bytes);

                // outName = classTree.getOutName(inName);
                // Dump the data, while creating the digests
                dataOutputStream.write(bytes, 0, bytes.length);
              }
              else
              {
                replaceContentsLog.append("  <resource name=\"");
                replaceContentsLog.append(ClassTree.toUtf8XmlString(inName));
                replaceContentsLog.append("\"/>\n");
              }

              dataOutputStream.flush();
              JarEntry outEntry = new JarEntry(outName);


              jarEntries.add(new Object[]{outEntry, baos.toByteArray()});
              baos.reset();
              // Now update the manifest entry for the entry with new name and new digests
              updateManifest(i , inName, outName, digests);
            }
          }
        }

        if (conserveManifest){
          outJar = Factory.newArchiveWriter(out[i], oldManifest[i]);
        } else {
          outJar = Factory.newArchiveWriter(out[i], newManifest[i]);
        }
        if (Version.getJarComment() != null) {
          outJar.setComment(Version.getJarComment());
        }

        // sort the entries in ascending order
        Collections.sort(jarEntries, new Comparator(){
          public int compare(Object a, Object b){
            Object[] array1 = (Object[]) a;
            JarEntry entry1 = (JarEntry) array1[0];
            Object[] array2 = (Object[]) b;
            JarEntry entry2 = (JarEntry) array2[0];
            return entry1.getName().compareTo(entry2.getName());
          }
        });
        // Finally, write the big bunch of data
        Set directoriesWritten = new HashSet();
        for (int j = 0; j < jarEntries.size(); j++){
          Object[] array = (Object[]) jarEntries.get(j);
          JarEntry entry = (JarEntry) array[0];
          String name = entry.getName();
          // make sure the directory entries are written to the jar file
          if (!entry.isDirectory()){
            int index = 0;
            while ((index = name.indexOf("/", index + 1))>= 0){
              String directory = name.substring(0, index+1);
              if (!directoriesWritten.contains(directory)){
                directoriesWritten.add(directory);
                outJar.addDirectory(directory);
              }
            }
          }
          // write the entry itself
          outJar.addFile(entry.getName(), (byte[]) array[1]);
        }

      }
      catch (Exception e)
      {
        // Log exceptions before exiting
        log.println();
        log.println("<!-- An exception has occured.");
        if (e instanceof java.util.zip.ZipException){
          log.println("This is most likely due to a duplicate .class file in your jar!");
          log.println("Please check that there are no out-of-date or backup duplicate .class files in your jar!");
        }
        log.println(e.toString());
        e.printStackTrace(log);
        log.println("-->");
        throw new IOException("An error ('"+e.getMessage()+"') occured during the remapping! See the log!)");
      }
      finally
      {
        inJar[i].close();
        if (inStream != null)
        {
          inStream.close();
        }
        if (outJar != null)
        {
          outJar.close();
        }
      }
    }
    // Write the mapping table to the log file
    classTree.dump(log);
    if(replaceContentsLog.length() > 0 || replaceNameLog.length() > 0)
    {
      log.println("<!--");
      if(replaceNameLog.length() > 0)
      {
        log.println("\n<adjust replaceName=\"true\">");
        log.print(replaceNameLog);
        log.println("</adjust>");
      }
      if(replaceContentsLog.length() > 0)
      {
        log.println("\n<adjust replaceContents=\"true\">");
        log.print(replaceContentsLog);
        log.println("</adjust>");
      }
      log.println("-->");
    }

  }

  private DataOutputStream fillDigests(ByteArrayOutputStream baos, String[] digestStrings, MessageDigest[] digests) throws NoSuchAlgorithmException {

    OutputStream stream = baos;

    for (int i = 0; i < digestStrings.length; i++) {
      String digestString = digestStrings[i];
      MessageDigest digest = MessageDigest.getInstance(digestString);
      digests[i] = digest;
      stream = new DigestOutputStream(stream, digest);
    }
    return new DataOutputStream(stream);
  }

  /**
   * Close input JAR file.  @throws IOException the io exception
   *
   * @throws IOException the io exception
   */
  public void close() throws java.io.IOException
  {
    for(int i = 0; i < inJar.length; i++)
    {
      if (inJar[i] != null)
      {
        inJar[i].close();
        inJar[i] = null;
      }
    }
  }

  // Parse the RFC822-style MANIFEST.MF file
  private void parseManifest()throws java.io.IOException
  {
    for(int i = 0; i < oldManifest.length; i++)
    {
      // The manifest file is the first in the jar and is called
      // (case insensitively) 'MANIFEST.MF'
      oldManifest[i] = inJar[i].getManifest();

      if (oldManifest[i] == null){
        oldManifest[i] = new Manifest();
      }

      // Create a fresh manifest, with a version header
      newManifest[i] = new Manifest();

      // copy all main attributes
      for (Iterator it = oldManifest[i].getMainAttributes().entrySet().iterator(); it.hasNext();) {
        Map.Entry entry = (Map.Entry) it.next();
        Attributes.Name name = (Attributes.Name) entry.getKey();
        String value = (String) entry.getValue();
        if (resourceHandler != null) {
          name = new Attributes.Name(resourceHandler.filterString(name.toString(), "META-INF/MANIFEST.MF"));
          value = resourceHandler.filterString(value, "META-INF/MANIFEST.MF");
        }
        newManifest[i].getMainAttributes().putValue(name.toString(), value);
      }

      newManifest[i].getMainAttributes().putValue("Created-by", "yGuard Bytecode Obfuscator " + Version.getVersion());

      // copy all directory entries
      for (Iterator it = oldManifest[i].getEntries().entrySet().iterator();
            it.hasNext();){
         Map.Entry entry = (Map.Entry) it.next();
         String name = (String) entry.getKey();
         if (name.endsWith("/")){
           newManifest[i].getEntries().put(name, (Attributes) entry.getValue());
         }
      }
    }
  }

  // Update an entry in the manifest file
  private void updateManifest(int manifestIndex, String inName, String outName, MessageDigest[] digests)
  {
    // Create fresh section for entry, and enter "Name" header

    Manifest nm = newManifest[manifestIndex];
    Manifest om = oldManifest[manifestIndex];

    Attributes oldAtts = om.getAttributes(inName);
    Attributes newAtts = new Attributes();
    //newAtts.putValue(MANIFEST_NAME_TAG, outName);

    // copy over non-name and none digest entries
    if (oldAtts != null){
      for(Iterator it = oldAtts.entrySet().iterator(); it.hasNext();){
        Map.Entry entry = (Map.Entry) it.next();
        Object key = entry.getKey();
        String name = key.toString();
        if (!name.equalsIgnoreCase(MANIFEST_NAME_TAG) &&
            name.indexOf("Digest") == -1){
          newAtts.remove(name);
          newAtts.putValue(name, (String)entry.getValue());
        }
      }
    }

    // Create fresh digest entries in the new section
    if (digests != null && digests.length > 0)
    {
      // Digest-Algorithms header
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < digests.length; i++)
      {
        sb.append(digests[i].getAlgorithm());
        if (i < digests.length -1){
          sb.append(", ");
        }
      }
      newAtts.remove(MANIFEST_DIGESTALG_TAG);
      newAtts.putValue(MANIFEST_DIGESTALG_TAG, sb.toString());

      // *-Digest headers
      for (int i = 0; i < digests.length; i++)
      {
        newAtts.remove(digests[i].getAlgorithm() + "-Digest");
        newAtts.putValue(digests[i].getAlgorithm() + "-Digest", Tools.toBase64(digests[i].digest()));
      }
    }

    if (!newAtts.isEmpty()) {
      // Append the new section to the new manifest
      nm.getEntries().put(outName, newAtts);
    }
  }

  // Create a classfile database.
  private void buildClassTree(PrintWriter log)throws java.io.IOException
  {
    // Go through the input Jar, adding each class file to the database
    classTree = new ClassTree();
    classTree.setPedantic(isPedantic());
    classTree.setReplaceClassNameStrings(replaceClassNameStrings);
    ClassFile.resetDangerHeader();
    
    Map parsedClasses = new HashMap();
    for(int i = 0; i < inJar.length; i++)
    {
      Enumeration entries = inJar[i].getEntries();
      fireParsingJar(inJar[i].getName());
      while (entries.hasMoreElements())
      {
        // Get the next entry from the input Jar
        Entry inEntry = (Entry)entries.nextElement();
        String name = inEntry.getName();
        if (name.endsWith(CLASS_EXT))
        {
          fireParsingClass(Conversion.toJavaClass(name));
          // Create a full internal representation of the class file
          DataInputStream inStream = new DataInputStream(
          new BufferedInputStream(
          inJar[i].getInputStream(inEntry)));
          ClassFile cf = null;
          try
          {
            cf = ClassFile.create(inStream);
          }
          catch (Exception e)
          {
            log.println(ERROR_CORRUPT_CLASS + createJarName(inJar[i], name) + " -->");
            e.printStackTrace(log);
            throw new ParseException( e );
          }
          finally
          {
            inStream.close();
          }

          if (cf != null){
            final String cfn = cf.getName();
            final String key =
                    "module-info".equals(cfn) ? createModuleKey(cf) : cfn;

            Object[] old = (Object[]) parsedClasses.get(key);
            if (old != null){
              int jarIndex = ((Integer)old[0]).intValue();
              String warning = "yGuard detected a duplicate class definition " +
                "for \n    " + Conversion.toJavaClass(cfn) +
              "\n    [" + createJarName(inJar[jarIndex], old[1].toString()) + "] in \n    [" +
                createJarName(inJar[i], name) + "]";
              log.write("<!-- \n" + warning + "\n-->\n");
              if (jarIndex == i){
                throw new IOException(warning + "\nPlease remove inappropriate duplicates first!");
              } else {
                if (pedantic){
                  throw new IOException(warning + "\nMake sure these files are of the same version!");
                } 
              }
            } else {
              parsedClasses.put(key, new Object[]{new Integer(i), name});
            }

            // Check the classfile for references to 'dangerous' methods
            cf.logDangerousMethods(log, replaceClassNameStrings);
            classTree.addClassFile(cf);
          }

        }
      }
    }

    // set the java access modifiers from the containing class (muellese)
    final ClassTree ct = classTree;
    ct.walkTree(new TreeAction()
    {
      public void classAction(Cl cl)
      {
        if (cl.isInnerClass())
        {
          Cl parent = (Cl) cl.getParent();
          cl.access = parent.getInnerClassModifier(cl.getInName());
        }
      }
    });
  }

  private static String createJarName(Archive jar, String name){
    return "jar:"+jar.getName() + "|" + name;
  }

  private static String createModuleKey( final ClassFile cf ) {
    return "module-info:" + cf.findModuleName();
  }

  // Generate a mapping table for obfuscation.
  private void createMap(PrintWriter log) throws ClassNotFoundException
  {
    // Traverse the class tree, generating obfuscated names within
    // package and class namespaces
    classTree.generateNames();

    // Resolve the polymorphic dependencies of each class, generating
    // non-private method and field names for each namespace
    classTree.resolveClasses();

    // Signal that the namespace maps have been created
    hasMap = true;

    // Write the memory usage at this point to the log file
    Runtime rt = Runtime.getRuntime();
    rt.gc();
    log.println("<!--");
    log.println(LOG_MEMORY_USED + Long.toString(rt.totalMemory() - rt.freeMemory()) + LOG_MEMORY_BYTES);
    log.println(LOG_MEMORY_TOTAL + Long.toString(rt.totalMemory()) + LOG_MEMORY_BYTES);
    log.println("-->");

  }

  /**
   * Fire parsing jar.
   *
   * @param jar the jar
   */
  protected void fireParsingJar(String jar){
    if (listenerList == null) return;
    for (int i = 0, j = listenerList.size(); i < j; i++){
      ((ObfuscationListener)listenerList.get(i)).parsingJar(jar);
    }
  }

  /**
   * Fire parsing class.
   *
   * @param className the class name
   */
  protected void fireParsingClass(String className){
    if (listenerList == null) return;
    for (int i = 0, j = listenerList.size(); i < j; i++){
      ((ObfuscationListener)listenerList.get(i)).parsingClass(className);
    }
  }

  /**
   * Fire obfuscating jar.
   *
   * @param inJar  the in jar
   * @param outJar the out jar
   */
  protected void fireObfuscatingJar(String inJar, String outJar){
    if (listenerList == null) return;
    for (int i = 0, j = listenerList.size(); i < j; i++){
      ((ObfuscationListener)listenerList.get(i)).obfuscatingJar(inJar, outJar);
    }
  }

  /**
   * Fire obfuscating class.
   *
   * @param className the class name
   */
  protected void fireObfuscatingClass(String className){
    if (listenerList == null) return;
    for (int i = 0, j = listenerList.size(); i < j; i++){
      ((ObfuscationListener)listenerList.get(i)).obfuscatingClass(className);
    }
  }

  /**
   * Registers Listener to receive events.
   *
   * @param listener The listener to register.
   */
  public synchronized void addListener(com.yworks.yguard.ObfuscationListener listener)
  {
    if (listenerList == null )
    {
      listenerList = new java.util.ArrayList();
    }
    listenerList.add(listener);
  }

  /**
   * Removes Listener from the list of listeners.
   *
   * @param listener The listener to remove.
   */
  public synchronized void removeListener(com.yworks.yguard.ObfuscationListener listener)
  {
    if (listenerList != null )
    {
      listenerList.remove(listener);
    }
  }

  /**
   * Getter for property replaceClassNameStrings.
   *
   * @return Value of property replaceClassNameStrings.
   */
  public boolean isReplaceClassNameStrings()
  {
    return this.replaceClassNameStrings;
  }

  /**
   * Setter for property replaceClassNameStrings.
   *
   * @param replaceClassNameStrings New value of property replaceClassNameStrings.
   */
  public void setReplaceClassNameStrings(boolean replaceClassNameStrings)
  {
    this.replaceClassNameStrings = replaceClassNameStrings;
  }


  /**
   * Getter for property pedantic.
   *
   * @return Value of property pedantic.
   */
  public boolean isPedantic()
  {
    return this.pedantic;
  }

  /**
   * Setter for property pedantic.
   *
   * @param pedantic New value of property pedantic.
   */
  public void setPedantic(boolean pedantic)
  {
    this.pedantic = pedantic;
    Cl.setPedantic(pedantic);
  }


  /**
   * Returns the obfuscated file name of the java class.
   * The ending ".class" is omitted.
   *
   * @param javaClass the fully qualified name of an unobfuscated class.
   * @return the string
   */
  public String translateJavaFile(String javaClass)
  {
    Cl cl = classTree.findClassForName(javaClass.replace('/','.'));
    if(cl != null)
    {
      return cl.getFullOutName();
    }
    else
    {
      return javaClass;
    }
  }


  /**
   * Translate java class string.
   *
   * @param javaClass the java class
   * @return the string
   */
  public String translateJavaClass(String javaClass)
  {
    Cl cl = classTree.findClassForName(javaClass);
    if(cl != null)
    {
      return cl.getFullOutName().replace('/', '.');
    }
    else
    {
      return javaClass;
    }
  }

  /**
   * Tries to translate as many parts of items as possible.
   * E.g if com.yworks.example.test.Invalid cannot be resolved it will resolve in order
   * - com.yworks.example.test.Invalid
   * - com.yworks.example.test
   * - com.yworks.example
   * - com.yworks
   * - com
   * Depending on the number of items you can infer which parts have been remapped and which have not.
   *
   * @param items list of unmapped items
   * @return list of mapped items
   */
  public List<String> translateItem(String[] items) {
    List<String> mapped = new ArrayList<>();
    List<String> partialItems = Arrays.asList(items);
    TreeItem item = classTree.findTreeItem(items);
    while ((partialItems.size() > 0) && item == null) {
      partialItems = partialItems.subList(0, partialItems.size() - 1);
      String[] partialItemsArray = new String[partialItems.size()];
      partialItems.toArray(partialItemsArray);
      item = classTree.findTreeItem(partialItemsArray);
    }
    while (item != null) {
      mapped.add(item.getOutName());
      item = item.parent;
    }
    if (mapped.size() > 0) {
      // Ignore root node which is always empty
      mapped = mapped.subList(0, mapped.size() - 1);
      // Reverse insertion order
      Collections.reverse(mapped);
    }
    return mapped;
  }

  /**
   * Sets digests.
   *
   * @param digestStrings the digest strings
   */
  public void setDigests(String[] digestStrings) {
    this.digestStrings = digestStrings;
  }

  /**
   * Sets annotation class.
   *
   * @param annotationClass the annotation class
   */
  public void setAnnotationClass(String annotationClass) {
    ObfuscationConfig.annotationClassName = annotationClass;
  }
}
