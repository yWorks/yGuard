/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf;

import com.yworks.yguard.Conversion;
import com.yworks.yguard.obf.classfile.ClassConstants;
import com.yworks.yguard.obf.classfile.ClassFile;
import com.yworks.yguard.obf.classfile.FieldInfo;
import com.yworks.yguard.obf.classfile.Logger;
import com.yworks.yguard.obf.classfile.MethodInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Tree item representing a class or interface.
 *
 * @author Mark Welsh
 */
public class Cl extends PkCl implements NameListUp, NameListDown {
  private boolean sourceFileMappingSet;

  /**
   * Gets attributes to keep.
   *
   * @return the attributes to keep
   */
  public Set getAttributesToKeep() {
    return attributesToKeep;
  }

  /**
   * The interface Class resolver.
   */
  public interface ClassResolver extends AutoCloseable {
    /**
     * Resolve class.
     *
     *
     *@param className the class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    Class resolve( String className ) throws ClassNotFoundException;
  }

  private static final class DefaultClassResolver implements ClassResolver {
    public Class resolve( String className ) throws ClassNotFoundException {
      return Class.forName(className, false, this.getClass().getClassLoader());
    }

    @Override
    public void close() throws Exception {
    }
  }

  private static boolean pedantic = false;

  // Constants -------------------------------------------------------------
  private static ClassResolver resolver;

  static {
    resolver = new DefaultClassResolver();
  }

  /**
   * Get class resolver class resolver.
   *
   * @return the class resolver
   */
  public static ClassResolver getClassResolver() {
    return resolver;
  }

  /**
   * Set pedantic.
   *
   *
   *@param val the val
   */
  public static void setPedantic( boolean val ) {
    pedantic = val;
  }

  /**
   * Set class resolver.
   *
   *
   *@param res the res
   */
  public static void setClassResolver( ClassResolver res ) {
    if (res != null) {
      resolver = res;
    } else {
      resolver = new DefaultClassResolver();
    }
  }

  // Fields ----------------------------------------------------------------
  private final Hashtable mds = new Hashtable(); // Owns a list of methods
  private final Hashtable fds = new Hashtable(); // Owns a list of fields
  private boolean isResolved = false; // Has the class been resolved already?
  private boolean isScanned = false; // Has the class been scanned already?
  private final String superClass; // Our superclass name
  private final String[] superInterfaces; // Names of implemented interfaces
  private final boolean isInnerClass; // Is this an inner class?
  private final ObfuscationConfig obfuscationConfig;
  private String sourceFileMapping;
  private int classFileAccess;
  private LineNumberTableMapper lineNumberTableMapper;
  private final Vector nameListUps = new Vector(); // NameListUp interfaces for super-class/interfaces
  private final Vector nameListDowns = new Vector(); // NameListDown interfaces for derived class/interfaces
  /**
   * The constant nameSpace.
   */
  public static int nameSpace = 0;
  private static NameMaker methodNameMaker;
  private static NameMaker fieldNameMaker;
  private final Map innerClassModifiers = new HashMap();
  private final Set attributesToKeep = new HashSet();
//    private boolean isPublic = true;

  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------

  /**
   * Ctor.
   *@param parent the parent
   *
   *
   *@param isInnerClass      the is inner class
   *
   *@param name              the name
   *
   *@param superClass        the super class
   *
   *@param superInterfaces   the super interfaces
   *
   *@param modifiers         the modifiers
   *
   *@param obfuscationConfig the obfuscation config
   */
  public Cl( TreeItem parent, boolean isInnerClass, String name, String superClass, String[] superInterfaces,
             int modifiers, ObfuscationConfig obfuscationConfig ) {
    super(parent, name);
    this.superClass = superClass;
    this.superInterfaces = superInterfaces;
    this.isInnerClass = isInnerClass;
    this.obfuscationConfig = obfuscationConfig;
    this.access = modifiers;
    if (parent == null || name.equals("")) {
      System.err.println("Internal error: class must have parent and name");
    }
    if (parent instanceof Cl) {
      sep = ClassFile.SEP_INNER;
    }

    // Do not obfuscate anonymous inner classes
    if (isInnerClass && Character.isDigit(name.charAt(0))) {
      setOutName(getInName());
    }
  }

  /**
   * Set class file access.
   *
   *
   *@param classFileAccess the class file access
   */
  void setClassFileAccess( int classFileAccess ) {
    this.classFileAccess = classFileAccess;
  }

  /**
   * Gets line number table mapper.
   *
   * @return the line number table mapper
   */
  public LineNumberTableMapper getLineNumberTableMapper() {
    return lineNumberTableMapper;
  }

  /**
   * Sets line number table mapper.
   *
   *
   *@param lineNumberTableMapper the line number table mapper
   */
  public void setLineNumberTableMapper( LineNumberTableMapper lineNumberTableMapper ) {
    this.lineNumberTableMapper = lineNumberTableMapper;
  }

  /**
   * Gets source file mapping.
   *
   * @return the source file mapping
   */
  public String getSourceFileMapping() {
    return sourceFileMapping;
  }

  /**
   * Sets source file mapping.
   *
   *
   *@param sourceFileMapping the source file mapping
   */
  public void setSourceFileMapping( String sourceFileMapping ) {
    this.sourceFileMappingSet = true;
    this.sourceFileMapping = sourceFileMapping;
  }

  /**
   * Is source file mapping set boolean.
   *
   * @return the boolean
   */
  public boolean isSourceFileMappingSet() {
    return sourceFileMappingSet;
  }

  /**
   * Get super class string.
   *
   * @return the string
   */
  public String getSuperClass() {
    return this.superClass;
  }

  /**
   * Get interfaces string [ ].
   *
   * @return the string [ ]
   */
  public String[] getInterfaces() {
    return this.superInterfaces;
  }

  /**
   * Set inner class modifiers.
   *
   *
   *@param map the map
   */
  public void setInnerClassModifiers( Map map ) {
    this.innerClassModifiers.putAll(map);
  }

  /**
   * Get inner class modifier int.
   *
   *
   *@param fqn the fqn
   * @return the int
   */
  public int getInnerClassModifier( String fqn ) {
    Integer i = (Integer) innerClassModifiers.get(fqn);
    if (i == null) {
      return Modifier.PRIVATE;
    } else {
      return i.intValue();
    }
  }

  /**
   * Is this an inner class?  @return the boolean
   */
  public boolean isInnerClass() {
    return isInnerClass;
  }

  /**
   * Get a method by name.
   *@param name the name
   *
   *
   *@param descriptor the descriptor
   * @return the method
   */
  public Md getMethod( String name, String descriptor ) {
    return (Md) mds.get(name + descriptor);
  }

  /**
   * Get a field by name.
   *@param name the name
   *
   * @return the field
   */
  public Fd getField( String name ) {
    return (Fd) fds.get(name);
  }

  /**
   * Get an Enumeration of methods.  @return the method enum
   */
  public Enumeration getMethodEnum() {
    return mds.elements();
  }

  /**
   * Get an Enumeration of fields.  @return the field enum
   */
  public Enumeration getFieldEnum() {
    return fds.elements();
  }

  /**
   * Is this class's name a match to the wildcard pattern?
   *@param pattern the pattern
   *
   * @return the boolean
   */
  public boolean isWildcardMatch( String pattern ) {
    return isMatch(pattern, getFullInName());
  }

  /**
   * Is this class's name a non-recursive match to the wildcard pattern?
   *@param pattern the pattern
   *
   * @return the boolean
   */
  public boolean isNRWildcardMatch( String pattern ) {
    return isNRMatch(pattern, getFullInName());
  }

  /**
   * Does this class have the specified class in its super chain?
   *@param queryName the query name
   *
   * @return the boolean
   * @throws ClassNotFoundException the class not found exception
   */
  public boolean hasAsSuper( String queryName ) throws ClassNotFoundException {
    // Special case: we are java/lang/Object
    if (superClass == null) {
      return false;
    }

    try {
      if (superClass.equals(queryName)) {
        return true;
      } else {
        Cl superClassItem = classTree.getCl(superClass);
        if (superClassItem != null) {
          return superClassItem.hasAsSuper(queryName);
        } else {
          Class extSuper = resolver.resolve(ClassFile.translate(superClass));
          while (extSuper != null) {
            if (extSuper.getName().equals(ClassFile.translate(queryName))) {
              return true;
            }
            extSuper = extSuper.getSuperclass();
          }
          return false;
        }
      }
    } catch (ClassNotFoundException cnfe) {
      if (pedantic) {
        throw cnfe;
      } else {
        return false;
      }
    }
  }

  /** Add an inner class. */
  public Cl addClass( Object[] classInfo ) {
    return addClass(true, classInfo);
  }

  /**
   * Add an inner class, used when copying inner classes from a placeholder.
   *@param cl the cl
   *
   * @return the cl
   */
  public Cl addClass( Cl cl ) {
    cls.put(cl.getInName(), cl);
    return cl;
  }

  /** Add a placeholder class. */
  public Cl addPlaceholderClass( String name ) {
    return addPlaceholderClass(true, name);
  }

  /**
   * Add a method.
   *
   *
   *@param methodInfo the method info
   * @return the md
   */
  public Md addMethod( MethodInfo methodInfo ) {
    boolean isSynthetic = methodInfo.isSynthetic();
    String name = methodInfo.getName();
    String descriptor = methodInfo.getDescriptor();
    int access = methodInfo.getAccessFlags();
    // Exclude the <init> and <clinit> methods
    if (name.charAt(0) == '<') {
      return null;
    }
    Md md = getMethod(name, descriptor);
    if (md == null) {
      md = new Md(this, isSynthetic, name, descriptor, access, methodInfo.getObfuscationConfig());
      mds.put(name + descriptor, md);
    }
    // Exclude the public ... valueOf(String) and values() methods of the Enum classes.
    final int PublicStatic = ClassConstants.ACC_PUBLIC | ClassConstants.ACC_STATIC;
    if ((this.classFileAccess & ClassConstants.ACC_ENUM) == ClassConstants.ACC_ENUM &&
        ((access & PublicStatic) == PublicStatic)) {
      final String desc = "(Ljava/lang/String;)L" + getFullInName() + ';';
      if ("valueOf".equals(name) && desc.equals(descriptor)) {
        md.setOutName(name);
      } else if ("values".equals(name) && descriptor.equals("()[L" + getFullInName() + ';')) {
        md.setOutName(name);
      }
    }
    return md;
  }

  /**
   * Add a field.
   *@param fieldInfo the field info
   *
   * @return the fd
   */
  public Fd addField( FieldInfo fieldInfo ) {
    boolean isSynthetic = fieldInfo.isSynthetic();
    String name = fieldInfo.getName();
    String descriptor = fieldInfo.getDescriptor();
    int access = fieldInfo.getAccessFlags();
    Fd fd = getField(name);
    if (fd == null) {
      fd = new Fd(this, isSynthetic, name, descriptor, access, fieldInfo.getObfuscationConfig());
      fds.put(name, fd);
    }
    return fd;
  }

  /**
   * Prepare for resolve of a class entry by resetting flags.
   */
  public void resetResolve() {
    isScanned = false;
    isResolved = false;
    nameListDowns.removeAllElements();
  }

  /**
   * Set up reverse list of reserved names prior to resolving classes.
   */
  public void setupNameListDowns() {
    // Special case: we are java/lang/Object
    if (superClass == null) {
      return;
    }

    // Add this class as a NameListDown to the super and each interface, if they are in the JAR
    Cl superClassItem = classTree.getCl(superClass);
    if (superClassItem != null) {
      superClassItem.nameListDowns.addElement(this);
    }
    for (int i = 0; i < superInterfaces.length; i++) {
      Cl interfaceItem = classTree.getCl(superInterfaces[i]);
      if (interfaceItem != null) {
        interfaceItem.nameListDowns.addElement(this);
      }
    }
  }

  /**
   * Resolve a class entry - set obfuscation permissions based on super class and interfaces.
   * Overload method and field names maximally.
   *
   * @throws ClassNotFoundException the class not found exception
   */
  public void resolveOptimally() throws ClassNotFoundException {
    // Already processed, then do nothing

    if (!isResolved) {
      // Get lists of method and field names in inheritance namespace
      Vector methods = new Vector();
      Vector fields = new Vector();
      scanNameSpaceExcept(null, methods, fields);
      String[] methodNames = new String[methods.size()];
      for (int i = 0; i < methodNames.length; i++) {
        methodNames[i] = (String) methods.elementAt(i);
      }
      String[] fieldNames = new String[fields.size()];
      for (int i = 0; i < fieldNames.length; i++) {
        fieldNames[i] = (String) fields.elementAt(i);
      }

      NameMakerFactory nmf = NameMakerFactory.getInstance();

      // Create new name-makers for the namespace
      methodNameMaker = nmf.getMethodNameMaker(methodNames, getFullInName());
      fieldNameMaker = nmf.getFieldNameMaker(fieldNames, getFullInName());

      // Resolve a full name space
      resolveNameSpaceExcept(null);

      // and move to next
      nameSpace++;
    }
  }

  // Get lists of method and field names in inheritance namespace
  private void scanNameSpaceExcept( Cl ignoreCl, Vector methods,
                                    Vector fields ) throws ClassNotFoundException {
//      System.out.println("Scan: "+getInName());

    // Special case: we are java/lang/Object
    if (superClass == null) {
      return;
    }

    // Traverse one step in each direction in name space, scanning
    if (!isScanned) {
      // First step up to super classes, scanning them
      Cl superCl = classTree.getCl(superClass);
      if (superCl != null) // internal to JAR
      {
        if (superCl != ignoreCl) {
          superCl.scanNameSpaceExcept(this, methods, fields);
        }
      } else // external to JAR
      {
        scanExtSupers(superClass, methods, fields);
      }
      for (int i = 0; i < superInterfaces.length; i++) {
        Cl interfaceItem = classTree.getCl(superInterfaces[i]);
        if (interfaceItem != null) {
          if (interfaceItem != ignoreCl) {
            interfaceItem.scanNameSpaceExcept(this, methods, fields);
          }
        } else { // external to JAR
          scanExtSupers(superInterfaces[i], methods, fields);
        }
      }

      // Next, scan ourself
      if (!isScanned) {
        scanThis(methods, fields);

        // Signal class has been scanned
        isScanned = true;
      }

      // Finally step down to derived classes, resolving them
      for (Enumeration clEnum = nameListDowns.elements(); clEnum.hasMoreElements(); ) {
        Cl cl = (Cl) clEnum.nextElement();
        if (cl != ignoreCl) {
          cl.scanNameSpaceExcept(this, methods, fields);
        }
      }
    }
  }


  // Get lists of method and field names in inheritance namespace
  private void scanExtSupers( String name, Vector methods,
                              Vector fields ) throws ClassNotFoundException {
    try {
      Class extClass = resolver.resolve(ClassFile.translate(name));
      scanExtSupers(extClass, methods, fields);
    } catch (ClassNotFoundException cnfe) {
      if (pedantic) {
        throw cnfe;
      } else {
        Logger.getInstance().warningToLogfile("Unresolved external dependency: " + Conversion.toJavaClass(name) + " not found!");
        Logger.getInstance().setUnresolved();
      }
    }
  }

  // Get lists of method and field names in inheritance namespace
  private void scanExtSupers( Class extClass, Vector methods,
                              Vector fields ) throws ClassNotFoundException {
    // Get public methods and fields from supers and interfaces up the tree
    Method[] allPubMethods = extClass.getMethods();
    if (allPubMethods != null) {
      for (int i = 0; i < allPubMethods.length; i++) {
        String methodName = allPubMethods[i].getName();
        if (methods.indexOf(methodName) == -1) {
          methods.addElement(methodName);
        }
      }
    }
    Field[] allPubFields = extClass.getFields();
    if (allPubFields != null) {
      for (int i = 0; i < allPubFields.length; i++) {
        String fieldName = allPubFields[i].getName();
        if (fields.indexOf(fieldName) == -1) {
          fields.addElement(fieldName);
        }
      }
    }

    // Go up the super hierarchy, adding all non-public methods/fields
    while (extClass != null) {
      Method[] allClassMethods = extClass.getDeclaredMethods();
      if (allClassMethods != null) {
        for (int i = 0; i < allClassMethods.length; i++) {
          if (!Modifier.isPublic(allClassMethods[i].getModifiers())) {
            String methodName = allClassMethods[i].getName();
            if (methods.indexOf(methodName) == -1) {
              methods.addElement(methodName);
            }
          }
        }
      }
      Field[] allClassFields = extClass.getDeclaredFields();
      if (allClassFields != null) {
        for (int i = 0; i < allClassFields.length; i++) {
          if (!Modifier.isPublic(allClassFields[i].getModifiers())) {
            String fieldName = allClassFields[i].getName();
            if (fields.indexOf(fieldName) == -1) {
              fields.addElement(fieldName);
            }
          }
        }
      }
      extClass = extClass.getSuperclass();
    }
  }

  // Add method and field names from this class to the lists
  private void scanThis( Vector methods, Vector fields ) {
    for (Enumeration mdEnum = mds.elements(); mdEnum.hasMoreElements(); ) {
      Md md = (Md) mdEnum.nextElement();
      if (md.isFixed()) {
        String name = md.getOutName();
        if (methods.indexOf(name) == -1) {
          methods.addElement(name);
        }
      }
    }
    for (Enumeration fdEnum = fds.elements(); fdEnum.hasMoreElements(); ) {
      Fd fd = (Fd) fdEnum.nextElement();
      if (fd.isFixed()) {
        String name = fd.getOutName();
        if (fields.indexOf(name) == -1) {
          fields.addElement(name);
        }
      }
    }
  }

  // Resolve an entire inheritance name space optimally.
  private void resolveNameSpaceExcept( Cl ignoreCl ) throws ClassNotFoundException {
    // Special case: we are java/lang/Object
    if (superClass == null) {
      return;
    }

    // Traverse one step in each direction in name space, resolving
    if (!isResolved) {
      // First step up to super classes, resolving them, since we depend on them
      Cl superCl = classTree.getCl(superClass);
      if (superCl != null && superCl != ignoreCl) {
        superCl.resolveNameSpaceExcept(this);
      }
      for (int i = 0; i < superInterfaces.length; i++) {
        Cl interfaceItem = classTree.getCl(superInterfaces[i]);
        if (interfaceItem != null && interfaceItem != ignoreCl) {
          interfaceItem.resolveNameSpaceExcept(this);
        }
      }

      // Next, resolve ourself
      if (!isResolved) {
//              System.out.println("Resolve: "+getInName());
//              System.out.println("fds: "+fds);              
        resolveThis();

        // Signal class has been processed
        isResolved = true;
      }

      // Finally step down to derived classes, resolving them
      for (Enumeration clEnum = nameListDowns.elements(); clEnum.hasMoreElements(); ) {
        Cl cl = (Cl) clEnum.nextElement();
        if (cl != ignoreCl) {
          cl.resolveNameSpaceExcept(this);
        }
      }
    }
  }

  // For each super interface and the super class, if it is outside DB, use reflection
  // to merge its list of public/protected methods/fields --
  // while for those in the DB, resolve to get the name-mapping lists
  private void resolveThis() throws ClassNotFoundException {
    // Special case: we are java/lang/Object
    if (superClass == null) {
      return;
    }

    Cl superClassItem = classTree.getCl(superClass);
    nameListUps.addElement(superClassItem != null ?
                           superClassItem :
                           getExtNameListUp(superClass));
    for (int i = 0; i < superInterfaces.length; i++) {
      Cl interfaceItem = classTree.getCl(superInterfaces[i]);
      nameListUps.addElement(interfaceItem != null ?
                             interfaceItem :
                             getExtNameListUp(superInterfaces[i]));
    }

    // Run through each method/field in this class checking for reservations and
    // obfuscating accordingly
    nextMethod:
    for (Enumeration mdEnum = mds.elements(); mdEnum.hasMoreElements(); ) {
      Md md = (Md) mdEnum.nextElement();
      if (!md.isFixed()) {
        //muellese: private should never make any problems
        if (!Modifier.isPrivate(md.getModifiers())) {
          // Check for name reservation via derived classes
          for (Enumeration nlEnum = nameListDowns.elements(); nlEnum.hasMoreElements(); ) {
            String theOutName = ((NameListDown) nlEnum.nextElement()).getMethodObfNameDown(this, md.getInName(),
                                                                                           md.getDescriptor());
            if (theOutName != null) {
              md.setOutName(theOutName);
              continue nextMethod;
            }
          }
          // Check for name reservation via super classes
          for (Enumeration nlEnum = nameListUps.elements(); nlEnum.hasMoreElements(); ) {
            String theOutName = ((NameListUp) nlEnum.nextElement()).getMethodOutNameUp(md.getInName(),
                                                                                       md.getDescriptor());
            if (theOutName != null) {
              md.setOutName(theOutName);
              continue nextMethod;
            }
          }
        }
        // If no other restrictions, obfuscate it
        md.setOutName(methodNameMaker.nextName(md.getDescriptor()));
      } else {
        if (Modifier.isNative(md.access)) {
          // native method, check if hierarchy is fixed, too, otherwise - this will break JNI calls
          if (!md.getParent().getFullOutName().equals(md.getParent().getFullInName())) {
            Logger.getInstance().warning(
                    "Method " + md.getOutName() + " is native but " + md.getParent().getFullInName() + " is not kept/exposed.");
          }
        }
      }
    }
    nextField:
    for (Enumeration fdEnum = fds.elements(); fdEnum.hasMoreElements(); ) {
      Fd fd = (Fd) fdEnum.nextElement();
      if (!fd.isFixed()) {
        //muellese: private should never make any problems
        if (!Modifier.isPrivate(fd.getModifiers())) {
          // Check for name reservation via derived classes
          for (Enumeration nlEnum = nameListDowns.elements(); nlEnum.hasMoreElements(); ) {
            String theOutName = ((NameListDown) nlEnum.nextElement()).getFieldObfNameDown(this, fd.getInName());
            if (theOutName != null) {
              fd.setOutName(theOutName);
              continue nextField;
            }
          }
          // Check for name reservation via super classes
          for (Enumeration nlEnum = nameListUps.elements(); nlEnum.hasMoreElements(); ) {
            String superOutName = ((NameListUp) nlEnum.nextElement()).getFieldOutNameUp(fd.getInName());
            if (superOutName != null) {
              fd.setOutName(superOutName);
              continue nextField;
            }
          }
        }
        // If no other restrictions, obfuscate it
        fd.setOutName(fieldNameMaker.nextName(null));
      }
    }
  }

  /** Get output method name from list, or null if no mapping exists. */
  public String getMethodOutNameUp( String name, String descriptor ) throws ClassNotFoundException {
    // Check supers
    for (Enumeration enumeration = nameListUps.elements(); enumeration.hasMoreElements(); ) {
      String superOutName = ((NameListUp) enumeration.nextElement()).getMethodOutNameUp(name, descriptor);
      if (superOutName != null) {
        return superOutName;
      }
    }

    // Check self
    Md md = getMethod(name, descriptor);
    if (md != null && !Modifier.isPrivate(md.access)) {
      return md.getOutName();
    } else {
      return null;
    }
  }

  /** Get obfuscated method name from list, or null if no mapping exists. */
  public String getMethodObfNameUp( String name, String descriptor ) throws ClassNotFoundException {
    // Check supers
    for (Enumeration enumeration = nameListUps.elements(); enumeration.hasMoreElements(); ) {
      String superObfName = ((NameListUp) enumeration.nextElement()).getMethodObfNameUp(name, descriptor);
      if (superObfName != null) {
        return superObfName;
      }
    }

    // Check self
    Md md = getMethod(name, descriptor);
    if (md != null && !Modifier.isPrivate(md.access)) {
      return md.getObfName();
    } else {
      return null;
    }
  }

  /** Get output field name from list, or null if no mapping exists. */
  public String getFieldOutNameUp( String name ) throws ClassNotFoundException {
    // Check supers
    for (Enumeration enumeration = nameListUps.elements(); enumeration.hasMoreElements(); ) {
      String superOutName = ((NameListUp) enumeration.nextElement()).getFieldOutNameUp(name);
      if (superOutName != null) {
        return superOutName;
      }
    }

    // Check self
    Fd fd = getField(name);
    if (fd != null && !Modifier.isPrivate(fd.access)) {
      return fd.getOutName();
    } else {
      return null;
    }
  }

  /** Get obfuscated field name from list, or null if no mapping exists. */
  public String getFieldObfNameUp( String name ) throws ClassNotFoundException {
    // Check supers
    for (Enumeration enumeration = nameListUps.elements(); enumeration.hasMoreElements(); ) {
      String superObfName = ((NameListUp) enumeration.nextElement()).getFieldObfNameUp(name);
      if (superObfName != null) {
        return superObfName;
      }
    }

    // Check self
    Fd fd = getField(name);
    if (fd != null && !Modifier.isPrivate(fd.access)) {
      return fd.getObfName();
    } else {
      return null;
    }
  }

  /** Is the method reserved because of its reservation down the class hierarchy? */
  public String getMethodObfNameDown( Cl caller, String name, String descriptor ) throws ClassNotFoundException {
    // Check ourself for an explicit 'do not obfuscate'
    Md md = getMethod(name, descriptor);
    if (md != null && md.isFixed()) {
      return md.getOutName();
    }

    // Check our supers, except for our caller (special case if we are java/lang/Object)
    String theObfName = null;
    if (superClass != null) {
      Cl superClassItem = classTree.getCl(superClass);
      if (superClassItem != caller) {
        NameListUp nl = superClassItem != null ? superClassItem : getExtNameListUp(superClass);
        theObfName = nl.getMethodObfNameUp(name, descriptor);
        if (theObfName != null) {
          return theObfName;
        }
      }
      for (int i = 0; i < superInterfaces.length; i++) {
        Cl interfaceItem = classTree.getCl(superInterfaces[i]);
        if (interfaceItem != caller) {
          NameListUp nl = interfaceItem != null ? interfaceItem : getExtNameListUp(superInterfaces[i]);
          theObfName = nl.getMethodObfNameUp(name, descriptor);
          if (theObfName != null) {
            return theObfName;
          }
        }
      }
    }

    // Check our derived classes
    for (Enumeration enumeration = nameListDowns.elements(); enumeration.hasMoreElements(); ) {
      theObfName = ((NameListDown) enumeration.nextElement()).getMethodObfNameDown(this, name, descriptor);
      if (theObfName != null) {
        return theObfName;
      }
    }

    // No reservation found
    return null;
  }

  /** Is the field reserved because of its reservation down the class hierarchy? */
  public String getFieldObfNameDown( Cl caller, String name ) throws ClassNotFoundException {
    // Check ourself for an explicit 'do not obfuscate'
    Fd fd = getField(name);
    if (fd != null && fd.isFixed()) {
      return fd.getOutName();
    }

    // Check our supers, except for our caller (special case if we are java/lang/Object)
    String theObfName = null;
    if (superClass != null) {
      Cl superClassItem = classTree.getCl(superClass);
      if (superClassItem != caller) {
        NameListUp nl = superClassItem != null ? superClassItem : getExtNameListUp(superClass);
        theObfName = nl.getFieldObfNameUp(name);
        if (theObfName != null) {
          return theObfName;
        }
      }
      for (int i = 0; i < superInterfaces.length; i++) {
        Cl interfaceItem = classTree.getCl(superInterfaces[i]);
        if (interfaceItem != caller) {
          NameListUp nl = interfaceItem != null ? interfaceItem : getExtNameListUp(superInterfaces[i]);
          theObfName = nl.getFieldObfNameUp(name);
          if (theObfName != null) {
            return theObfName;
          }
        }
      }
    }

    // Check our derived classes
    for (Enumeration enumeration = nameListDowns.elements(); enumeration.hasMoreElements(); ) {
      theObfName = ((NameListDown) enumeration.nextElement()).getFieldObfNameDown(this, name);
      if (theObfName != null) {
        return theObfName;
      }
    }

    // No reservation found
    return null;
  }

  // Construct, or retrieve from cache, the NameListUp object for an external class/interface
  private static final Hashtable extNameListUpCache = new Hashtable();

  private NameListUp getExtNameListUp( String name ) throws ClassNotFoundException {
    NameListUp nl = (NameListUp) extNameListUpCache.get(name);
    if (nl == null) {
      nl = new ExtNameListUp(name);
      extNameListUpCache.put(name, nl);
    }
    return nl;
  }

  /**
   * The type Ext name list up.
   */
// NameListUp for class/interface not in the database.
  class ExtNameListUp implements NameListUp {
    // Class's fully qualified name
    private Class extClass;
    private Method[] methods = null;

    /**
     * Instantiates a new Ext name list up.
     *
     *
     *@param name the name
     * @throws ClassNotFoundException the class not found exception
     */
// Ctor.
    public ExtNameListUp( String name ) throws ClassNotFoundException {
      try {
        extClass = resolver.resolve(ClassFile.translate(name));
      } catch (ClassNotFoundException cnfe) {
        if (pedantic) {
          throw cnfe;
        } else {
          Logger.getInstance().warningToLogfile("Unresolved external dependency: " + Conversion.toJavaClass(name) + " not found!");
          Logger.getInstance().setUnresolved();
        }
      }
    }

    /**
     * Instantiates a new Ext name list up.
     *
     *
     *@param extClass the ext class
     */
// Ctor.
    public ExtNameListUp( Class extClass ) {
      this.extClass = extClass;
    }

    // Get obfuscated method name from list, or null if no mapping exists.
    public String getMethodObfNameUp( String name, String descriptor ) {
      return getMethodOutNameUp(name, descriptor);
    }

    // Get obfuscated method name from list, or null if no mapping exists.
    public String getMethodOutNameUp( String name, String descriptor ) {
      //RW
      if (extClass == null) {
        return name;
      }

      // Get list of public/protected methods
      if (methods == null) {
        methods = getAllDeclaredMethods(extClass);
        Vector pruned = new Vector();
        for (int i = 0; i < methods.length; i++) {
          int modifiers = methods[i].getModifiers();
          if (!Modifier.isPrivate(modifiers)) {
            pruned.addElement(methods[i]);
          }
        }
        methods = new Method[pruned.size()];
        for (int i = 0; i < methods.length; i++) {
          methods[i] = (Method) pruned.elementAt(i);
        }
      }

      // Check each public/protected class method against the named one
      nextMethod:
      for (int i = 0; i < methods.length; i++) {
        if (name.equals(methods[i].getName())) {
          String[] paramAndReturnNames = ClassFile.parseDescriptor(descriptor);
          Class[] paramTypes = methods[i].getParameterTypes();
          Class returnType = methods[i].getReturnType();
          if (paramAndReturnNames.length == paramTypes.length + 1) {
            for (int j = 0; j < paramAndReturnNames.length - 1; j++) {
              if (!paramAndReturnNames[j].equals(paramTypes[j].getName())) {
                continue nextMethod;
              }
            }
            String returnName = returnType.getName();
            if (!paramAndReturnNames[paramAndReturnNames.length - 1].equals(returnName)) {
              continue nextMethod;
            }

            // We have a match, and so the derived class method name must be made to match
            return name;
          }
        }
      }

      // Method is not present
      return null;
    }

    // Get obfuscated field name from list, or null if no mapping exists.
    public String getFieldObfNameUp( String name ) {
      return getFieldOutNameUp(name);
    }

    // Get obfuscated field name from list, or null if no mapping exists.
    public String getFieldOutNameUp( String name ) {
      if (extClass == null) {
        return name;
      }
      // Use reflection to check class for field
      Field field = getAllDeclaredField(extClass, name);
      if (field != null) {
        // Field must be public or protected
        int modifiers = field.getModifiers();
        if (!Modifier.isPrivate(modifiers)) {
          return name;
        }
      }

      // Field is not present
      return null;
    }

    // Get all methods (from supers too) regardless of access level
    private Method[] getAllDeclaredMethods( Class theClass ) {
      Vector ma = new Vector();
      int length = 0;

      // Get the public methods from all supers and interfaces up the tree
      Method[] allPubMethods = theClass.getMethods();
      ma.addElement(allPubMethods);
      length += allPubMethods.length;

      // Go up the super hierarchy, getting arrays of all methods (some redundancy
      // here, but that's okay)
      while (theClass != null) {
        Method[] methods = theClass.getDeclaredMethods();
        ma.addElement(methods);
        length += methods.length;
        theClass = theClass.getSuperclass();
      }

      // Merge the arrays
      Method[] allMethods = new Method[length];
      int pos = 0;
      for (Enumeration enumeration = ma.elements(); enumeration.hasMoreElements(); ) {
        Method[] methods = (Method[]) enumeration.nextElement();
        System.arraycopy(methods, 0, allMethods, pos, methods.length);
        pos += methods.length;
      }
      return allMethods;
    }

    // Get a specified field (from supers and interfaces too) regardless of access level
    private Field getAllDeclaredField( Class theClass, String name ) {
      Class origClass = theClass;

      // Check for field in supers
      while (theClass != null) {
        Field field = null;
        try {
          field = theClass.getDeclaredField(name);
        } catch (Exception e) {
          field = null;
        }
        if (field != null) {
          return field;
        }
        theClass = theClass.getSuperclass();
      }

      // Check for public field in supers and interfaces (some redundancy here,
      // but that's okay)
      try {
        return origClass.getField(name);
      } catch (SecurityException nsfe) {
        return null;
      } catch (NoSuchFieldException nsfe) {
        return null;
      }
    }
  }

  /**
   * Gets obfuscation config.
   *
   * @return the obfuscation config
   */
  public ObfuscationConfig getObfuscationConfig() {
    return obfuscationConfig;
  }
}

