/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author may be contacted at yguard@yworks.com
 *
 * Java and all Java-based marks are trademarks or registered
 * trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
 */
package com.yworks.yguard.obf;

import com.yworks.yguard.Conversion;
import com.yworks.yguard.ParseException;
import com.yworks.yguard.obf.classfile.ClassConstants;
import com.yworks.yguard.obf.classfile.ClassFile;
import com.yworks.yguard.obf.classfile.ClassItemInfo;
import com.yworks.yguard.obf.classfile.FieldInfo;
import com.yworks.yguard.obf.classfile.LineNumberTableAttrInfo;
import com.yworks.yguard.obf.classfile.Logger;
import com.yworks.yguard.obf.classfile.MethodInfo;
import com.yworks.yguard.obf.classfile.NameMapper;

import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Tree structure of package levels, classes, methods and fields used for obfuscation.
 *
 * @author      Mark Welsh
 */
public class ClassTree implements NameMapper
{
    // Constants -------------------------------------------------------------
    public static final char PACKAGE_LEVEL = '/';
    public static final char CLASS_LEVEL = '$';
    public static final char METHOD_FIELD_LEVEL = '/';

    // Fields ----------------------------------------------------------------
    private Vector retainAttrs = new Vector();  // List of attributes to retain
    private Pk root = null;   // Root package in database (Java default package)

    // Class methods ---------------------------------------------------------
    /** Return a fully qualified name broken into package/class segments. */
    public static Enumeration getNameEnum(String name)
    {
        Vector vec = new Vector();
        String nameOrig = name;
        while (!name.equals(""))
        {
            int posP = name.indexOf(PACKAGE_LEVEL);
            int posC = name.indexOf(CLASS_LEVEL);
            Cons cons = null;
            if (posP == -1 && posC == 0)
            {
              // this is the rare case when a toplevel class name starts with a dollar sign ('$')
              // currently GSon has this in its library and causes problems with yGuard.
              int innerClassIndex = name.indexOf(CLASS_LEVEL, 1);
              int endIndex = innerClassIndex > 0 ? innerClassIndex : name.length();
              cons = new Cons(new Character(CLASS_LEVEL), name.substring(0, endIndex));
              name = name.substring(endIndex);
            }
            if (posP == -1 && posC == -1)
            {
                cons = new Cons(new Character(CLASS_LEVEL), name);
                name = "";
            }
            if (posP == -1 && posC > 0)
            {
                cons = new Cons(new Character(CLASS_LEVEL), name.substring(0, posC));
                //fixes retroguard bug, where
                // 'ClassName$$InnerClassName' leads to a runtimeerror
                while ((posC+1 < name.length()) && (name.charAt(posC+1) == CLASS_LEVEL)) posC++;
                name = name.substring(posC + 1, name.length());
            }
            if (posP != -1 && posC == -1)
            {
                cons = new Cons(new Character(PACKAGE_LEVEL), name.substring(0, posP));
                name = name.substring(posP + 1, name.length());
            }
            if (posP != -1 && posC != -1)
            {
                if (posP < posC)
                {
                    cons = new Cons(new Character(PACKAGE_LEVEL), name.substring(0, posP));
                    name = name.substring(posP + 1, name.length());
                }
                else
                {
                    throw new IllegalArgumentException("Invalid fully qualified name (a): " +
                                          nameOrig);
                }
            }
            if (((String)cons.cdr).equals(""))
            {
                throw new IllegalArgumentException("Invalid fully qualified name (b): " +
                                      nameOrig);
            }
            vec.addElement(cons);
        }
        return vec.elements();
    }


    // Instance Methods ------------------------------------------------------
    /** Ctor. */
    public ClassTree()
    {
        root = Pk.createRoot(this);
    }

    /** Return the root node. */
    public Pk getRoot() {return root;}

    /**
     * finds tree items by looking for name components only...
     */
    public TreeItem findTreeItem(String[] nameParts){
      TreeItem tmp = root;
      for (int i = 0; tmp != null && i < nameParts.length; i++){
        String name = nameParts[i];
        tmp = findSubItem(tmp, name);
      }
      return tmp;
    }

    /**
     * walks the tree of TreeItems in order to find a class forName
     */
    public Cl findClassForName(String name){
      int dindex = name.indexOf('$');
      String innerClass = null;
      if (dindex>0){
        innerClass = name.substring(dindex+1);
        name = name.substring(0, dindex);
      }
      int pindex = name.lastIndexOf('.');
      String packageName = null;
      if (pindex>0){
        packageName = name.substring(0, pindex);
        name = name.substring(pindex+1);
      }
      Pk pk = root;
      if (packageName != null){
        for (StringTokenizer st = new StringTokenizer(packageName, ".", false); st.hasMoreTokens();){
          String token = st.nextToken();
          pk = findPackage(pk, token);
          if (pk == null) return null;
        }
      }
      Cl cl = findClass(pk, name);
      if (cl != null && innerClass != null){
        for (StringTokenizer st = new StringTokenizer(innerClass, "$", false); st.hasMoreTokens();){
          String token = st.nextToken();
          cl = findClass(cl, token);
          if (cl == null) return null;
        }
      }
      return cl;
    }

    private Pk findPackage(TreeItem parent, String pName){
      if (parent instanceof Pk){
        for (Enumeration enumeration = ((Pk)parent).getPackageEnum(); enumeration.hasMoreElements();){
          Pk subPk = (Pk) enumeration.nextElement();
          if (subPk.getInName().equals(pName)){
            return subPk;
          }
        }
      }
      return null;
    }

    private Cl findClass(PkCl parent, String pName){
      for (Enumeration enumeration = ((PkCl)parent).getClassEnum(); enumeration.hasMoreElements();){
        Cl cl = (Cl) enumeration.nextElement();
        if (cl.getInName().equals(pName)){
          return cl;
        }
      }
      return null;
    }

    private TreeItem findSubItem(TreeItem parent, String childName){
      if (parent instanceof Pk){
        for (Enumeration enumeration = ((Pk)parent).getPackageEnum(); enumeration.hasMoreElements();){
          Pk subPk = (Pk) enumeration.nextElement();
          if (subPk.getInName().equals(childName)){
            return subPk;
          }
        }
        for (Enumeration enumeration = ((Pk)parent).getClassEnum(); enumeration.hasMoreElements();){
          Cl cl = (Cl) enumeration.nextElement();
          if (cl.getInName().equals(childName)){
            return cl;
          }
        }
      }
      if (parent instanceof Cl){
        for (Enumeration enumeration = ((Cl)parent).getClassEnum(); enumeration.hasMoreElements();){
          Cl cl = (Cl) enumeration.nextElement();
          if (cl.getInName().equals(childName)){
            return cl;
          }
        }
        return null;
      }
      return null;
    }

    /** Update the path of the passed filename, if that path corresponds to a package. */
    public String getOutName(String inName)
    {
        try
        {
            TreeItem ti = root;
            StringBuffer sb = new StringBuffer();
            for (Enumeration nameEnum = getNameEnum(inName); nameEnum.hasMoreElements(); )
            {
                Cons nameSegment = (Cons)nameEnum.nextElement();
                char tag = ((Character)nameSegment.car).charValue();
                String name = (String)nameSegment.cdr;
                switch (tag)
                {
                case PACKAGE_LEVEL:
                    if (ti != null)
                    {
                        ti = ((Pk)ti).getPackage(name);
                        if (ti != null)
                        {
                            sb.append(ti.getOutName());
                        }
                        else
                        {
                            sb.append(name);
                        }
                    }
                    else
                    {
                        sb.append(name);
                    }
                    sb.append(PACKAGE_LEVEL);
                    break;

                case CLASS_LEVEL:
                    sb.append(name);
                    return sb.toString();

                default:
                    throw new RuntimeException("Internal error: illegal package/class name tag");
                }
            }
        }
        catch (Exception e)
        {
            // Just drop through and return the original name
        }
        return inName;
    }

    /** Add a classfile's package, class, method and field entries to database. */
    public void addClassFile(ClassFile cf)
    {
        // Add the fully qualified class name
        TreeItem ti = root;
        char parentTag = PACKAGE_LEVEL;
        for (Enumeration nameEnum = getNameEnum(cf.getName()); nameEnum.hasMoreElements(); )
        {
            Cons nameSegment = (Cons)nameEnum.nextElement();
            char tag = ((Character)nameSegment.car).charValue();
            String name = (String)nameSegment.cdr;
            switch (tag)
            {
            case PACKAGE_LEVEL:
                ti = ((Pk)ti).addPackage(name);
                break;

            case CLASS_LEVEL:
                // If this is an inner class, just add placeholder classes up the tree
                if (nameEnum.hasMoreElements())
                {
                    ti = ((PkCl)ti).addPlaceholderClass(name);
                }
                else
                {
                  Object[] classInfo = {
                      name, cf.getSuper(), cf.getInterfaces(), cf.getModifiers(), ClassItemInfo.getObfuscationConfig(cf.getAttributes())
                  };
                  Cl cl =((PkCl)ti).addClass(classInfo);
                  cl.setInnerClassModifiers(cf.getInnerClassModifiers());
                  cl.setClassFileAccess(cf.getClassFileAccess());
                  ti = cl;
                }
                break;

            default:
                throw new ParseException("Internal error: illegal package/class name tag");
            }
            parentTag = tag;
        }

        // We must have a class before adding methods and fields
        if (ti instanceof Cl)
        {
            Cl cl = (Cl)ti;
            cl.access = cf.getModifiers();

            // Add the class's methods to the database
            for (Enumeration enumeration = cf.getMethodEnum(); enumeration.hasMoreElements(); )
            {
              cl.addMethod((MethodInfo) enumeration.nextElement());
            }

            // Add the class's fields to the database
            for (Enumeration enumeration = cf.getFieldEnum(); enumeration.hasMoreElements(); )
            {
              cl.addField((FieldInfo) enumeration.nextElement());
            }
        }
        else
        {
            throw new ParseException("Inconsistent class file.");
        }
    }

    /** Mark an attribute type for retention. */
    public void retainAttribute(String name)
    {
        retainAttrs.addElement(name);
    }

    private boolean modifierMatch(int level, int mods){
        if (level == YGuardRule.LEVEL_NONE) return false;
        if (Modifier.isPublic(mods)){
            return (level & YGuardRule.PUBLIC) == YGuardRule.PUBLIC;
        }
        if (Modifier.isProtected(mods)){
            return (level & YGuardRule.PROTECTED) == YGuardRule.PROTECTED;
        }
        if (Modifier.isPrivate(mods)){
            return (level & YGuardRule.PRIVATE) == YGuardRule.PRIVATE;
        }
        // package friendly left only
        return (level & YGuardRule.FRIENDLY) == YGuardRule.FRIENDLY;
    }

    /** Mark a class/interface type (and possibly methods and fields defined in class) for retention. */
    public void retainClass(String name, int classLevel, int methodLevel, int fieldLevel, boolean retainHierarchy)
    {

        // Mark the class (or classes, if this is a wildcarded specifier)
        for (Enumeration clEnum = getClEnum(name, classLevel); clEnum.hasMoreElements(); )
        {
            Cl classItem = (Cl)clEnum.nextElement();
            if(retainHierarchy && classLevel != YGuardRule.LEVEL_NONE) retainHierarchy(classItem);
            // Retain methods if requested
            if (methodLevel != YGuardRule.LEVEL_NONE)
            {
                for (Enumeration enumeration = classItem.getMethodEnum(); enumeration.hasMoreElements(); )
                {
                    Md md = (Md)enumeration.nextElement();
                    if (modifierMatch(methodLevel, md.getModifiers()))
                    {
                        md.setOutName(md.getInName());
                        md.setFromScript();
                    }
                }
                // do super classes and interfaces...
                if ((methodLevel & (YGuardRule.PUBLIC | YGuardRule.PROTECTED | YGuardRule.FRIENDLY)) != 0
                  ||(fieldLevel & (YGuardRule.PUBLIC | YGuardRule.PROTECTED | YGuardRule.FRIENDLY)) != 0){
                  int mask = YGuardRule.PRIVATE;
                  int ml = methodLevel & ~mask;
                  int fl = fieldLevel & ~mask;
                  int cl = classLevel & ~mask;
                  String[] interfaces = classItem.getInterfaces();
                  if (interfaces != null){
                    for (int i = 0; i < interfaces.length; i++){
                      String interfaceClass = interfaces[i];
                      retainClass(interfaceClass, cl, ml, fl, false);
                    }
                  }
                  String superClass = classItem.getSuperClass();
                  if (superClass != null){
                    // staying in package?!
                    if (!superClass.startsWith(classItem.getParent().getFullInName())){
                      mask |= YGuardRule.FRIENDLY;
                      ml = methodLevel & ~mask;
                      fl = fieldLevel & ~mask;
                      cl = classLevel & ~mask;
                    }
                    retainClass(superClass, cl, ml, fl, false);
                  }
                }
            }

            // Retain fields if requested
            if (fieldLevel != YGuardRule.LEVEL_NONE)
            {
                for (Enumeration enumeration = classItem.getFieldEnum(); enumeration.hasMoreElements(); )
                {
                    Fd fd = (Fd)enumeration.nextElement();
                    if (modifierMatch(fieldLevel, fd.getModifiers()))
                    {
                        fd.setOutName(fd.getInName());
                        fd.setFromScript();
                    }
                }
            }
        }
    }

    /** Mark a method type for retention. */
    public void retainMethod(String name, String descriptor)
    {
        for (Enumeration enumeration = getMdEnum(name, descriptor);
             enumeration.hasMoreElements(); ) {
            Md md = (Md)enumeration.nextElement();
            md.setOutName(md.getInName());
            md.setFromScript();
        }
    }

    /** Mark a field type for retention. */
    public void retainField(String name)
    {
        for (Enumeration enumeration = getFdEnum(name); enumeration.hasMoreElements(); ) {
            Fd fd = (Fd)enumeration.nextElement();
            fd.setOutName(fd.getInName());
            fd.setFromScript();
        }
    }

    /** Mark a package for retention, and specify its new name. */
    public void retainPackageMap(String name, String obfName)
    {
        retainItemMap(getPk(name), obfName);
    }

    /** Mark a class/interface type for retention, and specify its new name. */
    public void retainClassMap(String name, String obfName)
    {
        retainItemMap(getCl(name), obfName);
    }

    /** Mark a method type for retention, and specify its new name. */
    public void retainMethodMap(String name, String descriptor,
                                String obfName)
    {
        retainItemMap(getMd(name, descriptor), obfName);
    }

    /** Mark a field type for retention, and specify its new name. */
    public void retainFieldMap(String name, String obfName)
    {
        retainItemMap(getFd(name), obfName);
    }

    // Mark an item for retention, and specify its new name.
    private void retainItemMap(TreeItem item, String obfName)
    {
        if (!item.isFixed())
        {
            item.setOutName(obfName);
            item.setFromScriptMap();
        } else {
          if (!item.getOutName().equals(obfName)){
            item.setOutName(obfName);
            item.setFromScriptMap();
            // do warning
            Logger.getInstance().warning("'" + item.getFullInName() + "' will be remapped to '" + obfName + "' according to mapping rule!");
          }
        }
//        if (!item.isFixed())
//        {
//          item.setFromScriptMap();
//        }
//        item.setOutName(obfName);
    }

    /** Traverse the class tree, generating obfuscated names within each namespace. */
    public void generateNames()
    {
        walkTree(new TreeAction() {
            public void packageAction(Pk pk)  {pk.generateNames();}
            public void classAction(Cl cl)  {cl.generateNames();}
        });
    }

    /** Resolve the polymorphic dependencies of each class. */
    public void resolveClasses() throws ClassNotFoundException
    {
        walkTree(new TreeAction() {
            public void classAction(Cl cl)  {cl.resetResolve();}
        });
        walkTree(new TreeAction() {
            public void classAction(Cl cl)  {cl.setupNameListDowns();}
        });
        Cl.nameSpace = 0;
        final ClassNotFoundException[] ex = new ClassNotFoundException[1];
        try{
          walkTree(new TreeAction() {
              public void classAction(Cl cl)  {
                try{
                  cl.resolveOptimally();
                } catch (ClassNotFoundException cnfe){
                  ex[0] = cnfe;
                  throw new RuntimeException();
                }
              }
          });
        } catch (RuntimeException rte){
          if (ex[0] != null){
            throw ex[0];
          } else {
            throw rte;
          }
        }
    }

    /** Return a list of attributes marked to keep. */
    public String[] getAttrsToKeep()
    {
        String[] attrs = new String[retainAttrs.size()];
        for (int i = 0; i < attrs.length; i++)
        {
            attrs[i] = (String)retainAttrs.elementAt(i);
        }
        return attrs;
    }

    /** Get classes in tree from the fully qualified name
        (can be wildcarded). */
    public Enumeration getClEnum(String fullName)
    {
       return getClEnum(fullName, YGuardRule.LEVEL_PRIVATE);
    }

    /** Get classes in tree from the fully qualified name
        (can be wildcarded). */
    public Enumeration getClEnum(String fullName, final int classMode)
    {
        final Vector vec = new Vector();

        // Wildcarded?
        // Then return list of all classes (including inner classes) in package
        if (fullName.indexOf('*') != -1) {
            // Recursive?
            if (fullName.indexOf('!') == 0) {
                final String fName = fullName.substring(1);
                walkTree(new TreeAction() {
                    public void classAction(Cl cl)  {
                        if (cl.isWildcardMatch(fName) && modifierMatch(classMode, cl.getModifiers())) {
                            vec.addElement(cl);
                        }
                    }
                });
            }
            else
            {
                // non-recursive
                final String fName = fullName;
                walkTree(new TreeAction() {
                    public void classAction(Cl cl)  {
                        if (cl.isNRWildcardMatch(fName) && modifierMatch(classMode, cl.getModifiers())) {
                            vec.addElement(cl);
                        }
                    }
                });
            }
        }
        else
        {
            // Single class
            Cl cl = getCl(fullName);
            if (cl != null )
            {
              int mods = cl.getModifiers();
              if (cl.isInnerClass()){
                Cl outer = (Cl) cl.getParent();
              }
              boolean match = modifierMatch(classMode, cl.getModifiers());
              if (match || classMode == YGuardRule.LEVEL_NONE ){ //(RW)
                vec.addElement(cl);
              }
            }
        }
        return vec.elements();
    }

    /** Get methods in tree from the fully qualified, and possibly
        wildcarded, name. */
    public Enumeration getMdEnum(String fullName,
                                 String descriptor)
    {
        final Vector vec = new Vector();
        final String fDesc = descriptor;
        if (fullName.indexOf('*') != -1 ||
            descriptor.indexOf('*') != -1) {
            // Recursive?
            if (fullName.indexOf('!') == 0) {
                final String fName = fullName.substring(1);
                // recursive wildcarding
                walkTree(new TreeAction() {
                    public void methodAction(Md md)  {
                        if (md.isWildcardMatch(fName, fDesc)) {
                            vec.addElement(md);
                        }
                    }
                });
            }
            else
            {
                final String fName = fullName;
                // non-recursive wildcarding
                walkTree(new TreeAction() {
                    public void methodAction(Md md)  {
                        if (md.isNRWildcardMatch(fName, fDesc)) {
                            vec.addElement(md);
                        }
                    }
                });
            }
        } else {
            Md md = getMd(fullName, descriptor);
            if (md != null) {
                vec.addElement(md);
            }
        }
        return vec.elements();
    }

    /** Get fields in tree from the fully qualified, and possibly
        wildcarded, name. */
    public Enumeration getFdEnum(String fullName)
    {
        final Vector vec = new Vector();
        if (fullName.indexOf('*') != -1) {
            // Recursive?
            if (fullName.indexOf('!') == 0) {
                // recursive wildcarding
                final String fName = fullName.substring(1);
                walkTree(new TreeAction() {
                    public void fieldAction(Fd fd)  {
                        if (fd.isWildcardMatch(fName)) {
                            vec.addElement(fd);
                        }
                    }
                });
            }
            else
            {
                // non-recursive wildcarding
                final String fName = fullName;
                walkTree(new TreeAction() {
                    public void fieldAction(Fd fd)  {
                        if (fd.isNRWildcardMatch(fName)) {
                            vec.addElement(fd);
                        }
                    }
                });
            }
        } else {
            Fd fd = getFd(fullName);
            if (fd != null) {
                vec.addElement(fd);
            }
        }
        return vec.elements();
    }

    /** Get class in tree from the fully qualified name, returning null if name not found. */
    public Cl getCl(String fullName)
    {
        TreeItem ti = root;
        for (Enumeration nameEnum = getNameEnum(fullName); nameEnum.hasMoreElements(); )
        {
            Cons nameSegment = (Cons)nameEnum.nextElement();
            char tag = ((Character)nameSegment.car).charValue();
            String name = (String)nameSegment.cdr;
            switch (tag)
            {
            case PACKAGE_LEVEL:
                ti = ((Pk)ti).getPackage(name);
                break;

            case CLASS_LEVEL:
                ti = ((PkCl)ti).getClass(name);
                break;

            default:
                throw new ParseException("Internal error: illegal package/class name tag");
            }

            // If the name is not in the database, return null
            if (ti == null)
            {
                return null;
            }
        }

        // It is an error if we do not end up with a class or interface
        if (!(ti instanceof Cl))
        {
            throw new ParseException("Inconsistent class or interface name.");
        }
        return (Cl)ti;
    }

    /** Get package in tree from the fully qualified name, returning null if name not found. */
    public Pk getPk(String fullName)
    {
        TreeItem ti = root;
        for (Enumeration nameEnum = getNameEnum(fullName); nameEnum.hasMoreElements(); )
        {
            Cons nameSegment = (Cons)nameEnum.nextElement();
            String name = (String)nameSegment.cdr;
            ti = ((Pk)ti).getPackage(name);

            // If the name is not in the database, return null
            if (ti == null)
            {
                return null;
            }
            // It is an error if we do not end up with a package
            if (!(ti instanceof Pk))
            {
                throw new ParseException("Inconsistent package.");
            }
        }
        return (Pk)ti;
    }

    /** Get method in tree from the fully qualified name. */
    public Md getMd(String fullName, String descriptor)
    {
        // Split into class and method names
        int pos = fullName.lastIndexOf(METHOD_FIELD_LEVEL);
        Cl cl = getCl(fullName.substring(0, pos));
        return cl.getMethod(fullName.substring(pos + 1), descriptor);
    }

    /** Get field in tree from the fully qualified name. */
    public Fd getFd(String fullName)
    {
        // Split into class and field names
        int pos = fullName.lastIndexOf(METHOD_FIELD_LEVEL);
        Cl cl = getCl(fullName.substring(0, pos));
        return cl.getField(fullName.substring(pos + 1));
    }

    public String[] getAttrsToKeep(String className) {
      Cl cl = getCl(className);
      if (cl != null){
        Set attrs = cl.getAttributesToKeep();
        if (attrs != null && attrs.size() > 0){
          String[] other = getAttrsToKeep();
          Set tmp = new HashSet(attrs);
          for (int i = 0; i < other.length; i++) {
            tmp.add(other[i]);
          }
          return (String[]) tmp.toArray(new String[tmp.size()]);
        } else {
          return getAttrsToKeep();
        }
      } else {
        return getAttrsToKeep();
      }
    }

    public String mapLocalVariable(String thisClassName, String methodName, String descriptor, String string) {
      return string;
    }

    /** Mapping for fully qualified class name.
     *  @see NameMapper#mapClass */
    public String mapClass(String className)
    {
//      System.out.println("map class " + className);
        // Check for array -- requires special handling
        if (className.length() > 0 && className.charAt(0) == '[') {
            StringBuffer newName = new StringBuffer();
            int i = 0;
            while (i < className.length()) {
                char ch = className.charAt(i++);
                switch (ch) {
                case '[':
                case ';':
                    newName.append(ch);
                    break;

                case 'L':
                    newName.append(ch);
                    int pos = className.indexOf(';', i);
                    if (pos < 0) {
                        throw new ParseException("Invalid class name encountered: " + className);
                    }
                    newName.append(mapClass(className.substring(i, pos)));
                    i = pos;
                    break;

                default:
                    return className;
                }
            }
            return newName.toString();
        } else {
            Cl cl = getCl(className);
            if (cl == null){
              try {
                Class aClass = Cl.getClassResolver().resolve(Conversion.toJavaClass(className));
                // ok class exists...
                return className;
              } catch (ClassNotFoundException e) {
                if (pedantic){
                  throw new NoSuchMappingException("Class "+Conversion.toJavaClass(className));
                } else {
                  Logger.getInstance().warningToLogfile("Unresolved external dependency: "+Conversion.toJavaClass(className)+
                                     " not found!");
                  Logger.getInstance().setUnresolved();
                  return className;
                }
              }
            }
            return cl.getFullOutName();
        }
    }

    /** Mapping for method name, of fully qualified class.
     *  @see NameMapper#mapMethod */
    public String mapMethod(String className, String methodName, String descriptor)
    {
      // check if the className is an array...
      if (className.startsWith("[") && className.endsWith(";")){
        int count = 0;
        while (className.charAt(count) == '['){
          count++;
        }
        if (className.charAt(count) == 'L'){
          className = className.substring(count + 1, className.length() - 1);
        }
      }
      Cl cl = getCl(className);
      if (cl != null && cl.getMethod(methodName, descriptor) != null)
      {
        return cl.getMethod(methodName, descriptor).getOutName();
      }
      else
      {
        if (cl == null)
        {
          try {
            Class aClass = Cl.getClassResolver().resolve(Conversion.toJavaClass(className));
          } catch (ClassNotFoundException e) {
            if (pedantic){
              throw new NoSuchMappingException("Class "+Conversion.toJavaClass(className));
            } else {
              Logger.getInstance().warningToLogfile( "No mapping found: " + Conversion.toJavaClass( className ) );
            }
          }
          // method is not in database use unobfuscated name...
          return methodName;
        }
        else
        {
          try
          {
//              System.out.println("Try: "+cl.getFieldObfNameUp(fieldName));
            String result =  cl.getMethodOutNameUp(methodName,descriptor);
            if (result != null)
              return result;
          }
          catch (Exception ex)
          {
            System.out.println(ex);
            //System.out.println("ME: Error: Try not succeeded");
          }
          if ((!methodName.equals("<init>") &&
               (!methodName.equals("<clinit>")))){
            if (pedantic){
              throw new NoSuchMappingException("Method "+Conversion.toJavaClass(className)+"."+methodName);
            } else {
              Logger.getInstance().error("Method "+Conversion.toJavaClass(className)+"."+methodName+
                                 " could not be mapped !\n Probably broken code! Try rebuilding from source!");
              return methodName;
            }
          }
          return methodName;
        }
      }
    }

    /** Mapping for annotation field/method name, of fully qualified class.
     *  @see NameMapper#mapAnnotationField */
    public String mapAnnotationField(String className, String methodName)
    {
        Cl cl = getCl(className);
        if (cl != null)
        {
          for (Enumeration enumeration = cl.getMethodEnum(); enumeration.hasMoreElements();){
            Md md = (Md) enumeration.nextElement();
            if (md.getInName().equals(methodName)){
              return md.getOutName();
            }
          }
          // actually this should not happen - is this an exception?!
          return methodName;
        }
        else
        {
          // method is not in database use unobfuscated name...
          return methodName;
        }
    }

    /** Mapping for field name, of fully qualified class.
     *  @see NameMapper#mapField */
    public String mapField(String className, String fieldName)
    {
//        System.out.println("Map "+className+"."+fieldName);
        Cl cl = getCl(className);
        if ((cl != null) && (cl.getField(fieldName) != null))
        {
          //special .class construct name mapping....
          if (fieldName.startsWith("class$")  && isReplaceClassNameStrings()){
            String realClassName = fieldName.substring(6);
            List nameParts = new ArrayList(20);
            for (StringTokenizer st = new StringTokenizer(realClassName, "$", false); st.hasMoreTokens();){
              nameParts.add(st.nextToken());
            }
            String[] names = new String[nameParts.size()];
            nameParts.toArray(names);
            TreeItem ti = findTreeItem(names);
            if (ti instanceof Cl){
              Fd fd = cl.getField(fieldName);
              String newClassName = mapClass(ti.getFullInName());
              String outName = "class$"+newClassName.replace('/','$');
              fd.setOutName(outName);
              return outName;
            }
          }
//        System.out.println("Standard Field Map");
          return cl.getField(fieldName).getOutName();
        }
        else
        {
          if (cl == null)
          {
//            System.out.println("Error: "+className+
//                               " class not found !");
            return fieldName;
          }
          else
          {
//            System.out.println("ERROR: "+className+"."+fieldName+
//                               " cannot be mapped !");
            try
            {
//              System.out.println("Try: "+cl.getFieldObfNameUp(fieldName));
              String result = cl.getFieldOutNameUp(fieldName);
              if (result != null)
                return result;
            }
            catch (Exception ex)
            {
//              System.out.println("Try not succeeded");
            }
            if (!fieldName.equals("this")) {
              if (pedantic){
                throw new NoSuchMappingException("Field "+className+"."+fieldName);
              } else {
                Logger.getInstance().error("Field "+className+"."+fieldName+
                   " could not be mapped !\n Probably broken code! Try rebuilding from source!");
              }
            }
            return fieldName;
          }
        }


//        return cl != null && cl.getField(fieldName) != null ?
//                    cl.getField(fieldName).getOutName() :
//                    fieldName;
    }

    /** Mapping for signatures (used for generics in 1.5).
     *  @see NameMapper#mapSignature
     */
    public String mapSignature(String signature){
        // Pass everything through unchanged, except for the String between
        // 'L' and ';' -- this is passed through mapClass(String)

//      System.out.println( "signature: "+signature );

        StringBuffer classString = new StringBuffer();

        StringBuffer newSignature = new StringBuffer();
        int i = 0;
        while (i < signature.length())
        {
            char ch = signature.charAt(i++);
            switch (ch)
            {
            case '[':
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'V':
            case '(':
            case ')':
              case '+':
              case ':':
              case '-':
              case '*':
                newSignature.append(ch);
                break;
            case ';':
                newSignature.append(ch);
                classString.setLength( 0 );
              break;
            case 'T':
            {   // Template name
                newSignature.append(ch);
                int pos = signature.indexOf(';', i);
                if (pos < 0)
                {
                    throw new ParseException("Invalid signature string encountered.");
                }
                newSignature.append(signature.substring(i, pos));
                i = pos;
                break;
            }
            case '<':
            {
              // formal parameters
              newSignature.append(ch);
              while (true){
                int first = i;
                while (signature.charAt(i) !=  ':'){
                  i++;
                }
                String templateName = signature.substring(first, i);
                newSignature.append(templateName);

                while (signature.charAt(i) == ':'){
                  newSignature.append(':');
                  i++;
                  int firstPos = i;
                  int bracketCount = 0;
                  while (!(bracketCount == 0 && signature.charAt(i) == ';')){
                    if (signature.charAt(i) == '<') {
                      bracketCount++;
                    } else if (signature.charAt(i) == '>'){
                      bracketCount--;
                    }
                    i++;
                  }
                  i++;
                  newSignature.append(mapSignature(signature.substring(firstPos, i)));
                }
                if (signature.charAt(i) == '>'){
                  newSignature.append('>');
                  i++;
                  break;
                }
              }
                break;
            }

              case '^':
              {
                newSignature.append(ch);
                if (signature.charAt(i) == 'T'){
                  // identifier
                  while (signature.charAt(i) !=  ';'){
                    newSignature.append(signature.charAt(i));
                    i++;
                  }
                  continue;
                } else if (signature.charAt(i) == 'L'){
                  // class
                  int first = i;
                  int bracketCount = 0;
                  while (signature.charAt(i) != ';' || bracketCount != 0){
                    char c = signature.charAt(i);
                    if (c == '<'){
                      bracketCount++;
                    } else if (c == '>'){
                      bracketCount--;
                    }
                    i++;
                  }
                  i++;
                  String classSig = signature.substring(first, i);
                  newSignature.append(mapSignature(classSig));
                } else {
                  throw new IllegalStateException("Could not map signature " + signature);
                }
              }
              break;
            case 'L':
            case '.':  // inner class
            {
                newSignature.append(ch);
                int pos = signature.indexOf(';', i);
                int bracketPos = signature.indexOf('<', i);
                if (bracketPos >= i && bracketPos < pos){
                  // found a bracket - find the matching one..
                  int bracketCount = 0;
                  int closingBracket = signature.length();
                  for (int walker = bracketPos + 1; walker < signature.length(); walker++){
                    char c = signature.charAt(walker);
                    if (c == '<'){
                      bracketCount++;
                    } else if (c == '>'){
                      if (bracketCount == 0){
                        closingBracket = walker;
                        break;
                      } else {
                        bracketCount--;
                      }
                    }
                  }
                  // TODO check!!!!
                  pos = closingBracket + 1;
//                  pos = signature.indexOf(';', closingBracket);
                  String templateArg = signature.substring(bracketPos + 1, closingBracket);
                  String classNamePart = signature.substring( i, bracketPos );
                  if (ch == '.'){ // inner class part - translate to class file name
                    appendInnerClass(classString, newSignature, classNamePart);
                  } else { // toplevel class 'L'
                    classString.append( classNamePart );
                    newSignature.append(mapClass( classString.toString() ));
                  }
                  newSignature.append('<');
                  newSignature.append(mapSignature(templateArg));
                  newSignature.append('>');
                  i = pos;
                } else {

                  // no generics to parse
                  
                  if (pos < 0)
                  {
                      throw new ParseException("Invalid signature string encountered: " + signature);
                  }

                  String classNamePart = signature.substring( i, pos );
                  if (ch == '.'){ // inner class part - translate to class file name
                    appendInnerClass(classString,newSignature,classNamePart);
                  } else {
                    classString.append( classNamePart );
                    newSignature.append(mapClass( classString.toString() ));
                  }

                  i = pos;
                }
                break;
            }
            default:
                throw new ParseException("Invalid signature string encountered: " +signature + " parsing char " + ch);
            }
        }
        return newSignature.toString();
    }

  private void appendInnerClass(StringBuffer classString, StringBuffer newSignature, String classNamePart) {
    classString.append( '$' );
    classString.append( classNamePart );
    String className = classString.toString();
    // do basically the same that mapClass() does, but return the last part only.
    String result = getClassNamePart(classNamePart, className);
    newSignature.append(result);
  }


  private String getClassNamePart(String classNamePart, String className) {

    int j = className.indexOf(classNamePart);

    if( classNamePart.indexOf('.') != -1 && j > 0 ) { // nested inner classes?

        // e.g. classNamePart: a.b, className: g/h/j$a.b

        String outerClassName = className.substring(0,j-1);
        String retval = "";
        String currentClassName = outerClassName;

        StringBuilder innerClassName = new StringBuilder();
        for( int i=0; i<classNamePart.length(); i++ ) {
          char c = classNamePart.charAt(i);
          if(c == '.' ) {
            currentClassName = currentClassName + '$' + innerClassName;
            retval = appendOutName(retval, currentClassName);
            innerClassName = new StringBuilder();
          } else {
            innerClassName.append(c);
          }
        }
        currentClassName = currentClassName + '$' + innerClassName;
        retval = appendOutName(retval, currentClassName);
        return retval;
      
    } else {

      Cl cl = getCl(className);
      if (cl == null){
        try {
          Class aClass = Cl.getClassResolver().resolve(Conversion.toJavaClass(className));
          // ok class exists...
          return classNamePart;
        } catch (ClassNotFoundException e) {
          if (pedantic){
            throw new NoSuchMappingException("Class "+Conversion.toJavaClass(className));
          } else {
            Logger.getInstance().warningToLogfile("Unresolved external dependency: "+Conversion.toJavaClass(className)+
                               " not found!");
            Logger.getInstance().setUnresolved();
            return classNamePart;
          }
        }
      } else {
        return cl.getOutName();
      }
    }
	}

  private String appendOutName(String retval, String currentClassName) {
    Cl cl = getCl( currentClassName );
    if( null != cl ) {
      if( retval.length() > 0 ) {
        retval = retval + '.';
      }
      retval = retval + cl.getOutName();
    } else {
      try {
        Class aClass = Cl.getClassResolver().resolve(Conversion.toJavaClass(currentClassName));
        // ok class exists...
        retval = retval + "." + currentClassName;
      } catch (ClassNotFoundException e) {
        if (pedantic){
          throw new NoSuchMappingException("Class "+Conversion.toJavaClass(currentClassName));
        } else {
          Logger.getInstance().warningToLogfile("Unresolved external dependency: "+Conversion.toJavaClass(currentClassName)+
                             " not found!");
          Logger.getInstance().setUnresolved();
          retval = retval + "." + currentClassName;
        }
      }
    }
    return retval;
  }

  public String mapSourceFile(String className, String sourceFileName){
    final Cl cl = getCl(className);
    if (cl.isSourceFileMappingSet()){
      return cl.getSourceFileMapping();
    } else {
      return sourceFileName;
    }
  }

  public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo info) {
    final Cl cl = getCl(className);
    if (cl.getLineNumberTableMapper() != null){
      return cl.getLineNumberTableMapper().mapLineNumberTable(className, methodName, methodSignature, info);
    } else {
      return true;
    }
  }

    /**
     * Mapping for descriptor of field or method.
     * @see NameMapper#mapDescriptor
     */
    public String mapDescriptor(String descriptor)
    {
        // Pass everything through unchanged, except for the String between
        // 'L' and ';' -- this is passed through mapClass(String)
        StringBuffer newDesc = new StringBuffer();
        int i = 0;
        while (i < descriptor.length())
        {
            char ch = descriptor.charAt(i++);
            switch (ch)
            {
            case '[':
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'V':
            case '(':
            case ')':
            case ';':
                newDesc.append(ch);
                break;

            case 'L':
                newDesc.append(ch);
                int pos = descriptor.indexOf(';', i);
                if (pos < 0)
                {
                    throw new ParseException("Invalid descriptor string encountered.");
                }
                newDesc.append(mapClass(descriptor.substring(i, pos)));
                i = pos;
                break;

            default:
                throw new ParseException("Invalid descriptor string encountered.");
            }
        }
        return newDesc.toString();
    }

    /**
     * Mapping for package names.
     * @see NameMapper#mapPackage(String)
     */
    public String mapPackage( final String packageName ) {
        final Pk pk = getPk(packageName);
        return pk == null ? packageName : pk.getFullOutName();
    }

    /** Dump the content of the class tree to the specified file (used for logging). */
    public void dump(final PrintWriter log)
    {
        log.println("<expose>");
        walkTree(new TreeAction() {
            public void classAction(Cl cl) {
                final String name = cl.getFullInName();
                if (cl.isFromScript() && !"module-info".equals(name)) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(name));
                    log.println("  <class name=\"" + cla + "\"/>");
                }
            }
            public void methodAction(Md md) {
                if (md.isFromScript()) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(md.getParent().getFullInName()));
                    String method = toUtf8XmlString(Conversion.toJavaMethod(md.getInName(), md.getDescriptor()));
                    log.println("  <method class=\""+cla+"\" name=\"" + method + "\"/>");
                }
            }
            public void fieldAction(Fd fd) {
                if (fd.isFromScript()) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(fd.getParent().getFullInName()));
                    log.println("  <field class=\""+cla+"\" name=\"" + toUtf8XmlString(fd.getInName()) + "\"/>");
                }
            }
            public void packageAction(Pk pk) {
                // No action
            }
        });
        log.println("</expose>");
        log.println("<map>");
        walkTree(new TreeAction() {
            public void classAction(Cl cl) {
                final String name = cl.getFullInName();
                if (!cl.isFromScript() && !"module-info".equals(name)) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(name));
                    log.println("  <class name=\"" + toUtf8XmlString(cla) + "\" map=\"" + toUtf8XmlString(cl.getOutName()) + "\"/>");
                }
            }
            public void methodAction(Md md) {
                if (!md.isFromScript()) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(md.getParent().getFullInName()));
                    String method = toUtf8XmlString(Conversion.toJavaMethod(md.getInName(), md.getDescriptor()));
                    log.println("  <method class=\""+cla+"\" name=\"" + method + "\" map=\"" + toUtf8XmlString(md.getOutName()) + "\"/>");
                }
            }
            public void fieldAction(Fd fd) {
                if (!fd.isFromScript()) {
                    String cla = toUtf8XmlString(Conversion.toJavaClass(fd.getParent().getFullInName()));
                    log.println("  <field class=\""+cla+"\" name=\"" + toUtf8XmlString(fd.getInName()) + "\" map=\"" + toUtf8XmlString(fd.getOutName()) + "\"/>");
                }
            }
            public void packageAction(Pk pk) {
                if (!pk.isFromScript() && pk.getFullInName().length() > 0) {
                    String pa = toUtf8XmlString(Conversion.toJavaClass(pk.getFullInName()));
                    log.println("  <package name=\""+pa +"\" map=\"" + toUtf8XmlString(pk.getOutName()) + "\"/>");
                }
            }
        });
        log.println("</map>");
    }

    public static final String toUtf8XmlString(String s){
      boolean bad = false;
      for (int i = 0; i< s.length(); i++){
        char c = s.charAt(i);
        if ((c >= 0x80) || (c =='"') || (c == '<')){
          bad = true;
          break;
        }
      }
      if (bad){
        StringBuffer buf = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++){
          buf.append(toUtf8XmlChar(s.charAt(i)));
        }
        return buf.toString();
      } else {
        return s;
      }
    }

    private static final String toUtf8XmlChar(char c){
      if (c < 0x80){
        if (c == '"'){
          return "&#x22;";
        } else if (c == '<'){
          return "&#x3c;";
        }
        return new String(new char[]{c});
      }
      else if (c < 0x800)
      {
        StringBuffer buf = new StringBuffer(8);
        buf.append("&#x");
        buf.append(hex[(c >> 8) & 0xff]);
        buf.append(hex[c & 0xff]);
        buf.append(';');
        return buf.toString();
      }
      else
      {
        StringBuffer buf = new StringBuffer(10);
        buf.append("&#x");
        buf.append(hex[(c >> 16) & 0xff]);
        buf.append(hex[(c >> 8) & 0xff]);
        buf.append(hex[c & 0xff]);
        buf.append(';');
        return buf.toString();
      }
    }

    private static final String[] hex;
    static {
      hex = new String[256];
      for (int i = 0; i < 256; i++){
        hex[i] = toHex(i);
      }
    }

    private static final String hexChars = "0123456789abcdef";

    /** Holds value of property replaceClassNameStrings. */
    private boolean replaceClassNameStrings;

    /** Holds value of property pedantic. */
    private boolean pedantic;

    private static String toHex(int i){
      StringBuffer buf = new StringBuffer(2);
      buf.append(hexChars.charAt((i/16)&15));
      buf.append(hexChars.charAt(i&15));
      return buf.toString();
    }

    // Private Methods -------------------------------------------------------
    // Mark TreeItem and all parents for retention.
    private void retainHierarchy(TreeItem ti)
    {
        if (!ti.isFixed())
        {
            ti.setOutName(ti.getInName());
            ti.setFromScript();
        }
        if (ti.parent != null)
        {
            retainHierarchy(ti.parent);
        }
    }

    /** Walk the whole tree taking action once only on each package level, class, method and field. */
    public void walkTree(TreeAction ta)
    {
        walkTree(ta, root);
    }

    // Walk the tree which has TreeItem as its root taking action once only on each
    // package level, class, method and field.
    private void walkTree(TreeAction ta, TreeItem ti)
    {
        if (ti instanceof Pk)
        {
            Enumeration packageEnum = ((Pk)ti).getPackageEnum();
            ta.packageAction((Pk)ti);
            while (packageEnum.hasMoreElements())
            {
                walkTree(ta, (TreeItem)packageEnum.nextElement());
            }
        }
        if (ti instanceof PkCl)
        {
            Enumeration classEnum = ((PkCl)ti).getClassEnum();
            while (classEnum.hasMoreElements())
            {
                walkTree(ta, (TreeItem)classEnum.nextElement());
            }
        }
        if (ti instanceof Cl)
        {
            Enumeration fieldEnum = ((Cl)ti).getFieldEnum();
            Enumeration methodEnum = ((Cl)ti).getMethodEnum();
            ta.classAction((Cl)ti);
            while (fieldEnum.hasMoreElements())
            {
                ta.fieldAction((Fd)fieldEnum.nextElement());
            }
            while (methodEnum.hasMoreElements())
            {
                ta.methodAction((Md)methodEnum.nextElement());
            }
        }
    }

    /** Getter for property replaceClassNameStrings.
     * @return Value of property replaceClassNameStrings.
     *
     */
    public boolean isReplaceClassNameStrings()
    {
      return this.replaceClassNameStrings;
    }

    /** Setter for property replaceClassNameStrings.
     * @param replaceClassNameStrings New value of property replaceClassNameStrings.
     *
     */
    public void setReplaceClassNameStrings(boolean replaceClassNameStrings)
    {
      this.replaceClassNameStrings = replaceClassNameStrings;
    }

    /** Getter for property pedantic.
     * @return Value of property pedantic.
     *
     */
    public boolean isPedantic()
    {
      return this.pedantic;
    }

    /** Setter for property pedantic.
     * @param pedantic New value of property pedantic.
     *
     */
    public void setPedantic(boolean pedantic)
    {
      this.pedantic = pedantic;
    }

  public void retainSourceFileAttributeMap(String name, String obfName) {
    for (Enumeration clEnum = getClEnum(name); clEnum.hasMoreElements(); )
    {
      Cl classItem = (Cl)clEnum.nextElement();
      classItem.setSourceFileMapping(obfName);
      classItem.getAttributesToKeep().add(ClassConstants.ATTR_SourceFile);
    }
  }

  public void retainLineNumberTable(String name, final LineNumberTableMapper lineNumberTableMapper) {
    for (Enumeration clEnum = getClEnum(name); clEnum.hasMoreElements(); )
    {
      Cl classItem = (Cl)clEnum.nextElement();
      classItem.setLineNumberTableMapper(lineNumberTableMapper);
      classItem.getAttributesToKeep().add(ClassConstants.ATTR_LineNumberTable);
    }
  }

  public void retainAttributeForClass(String className, String attributeDescriptor) {
    for (Enumeration clEnum = getClEnum(className); clEnum.hasMoreElements(); )
    {
      Cl classItem = (Cl)clEnum.nextElement();
      final Set set = classItem.getAttributesToKeep();
      set.add(attributeDescriptor);
    }
  }

  public void retainPackage(String packageName) {
    retainHierarchy(getPk(packageName));
  }
}


