/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;
import com.yworks.yguard.obf.*;
import java.lang.reflect.Modifier;
import com.yworks.yguard.Conversion;
import com.yworks.yguard.ParseException;

/**
 * This is a representation of the data in a Java class-file (*.class).
 * A ClassFile instance representing a *.class file can be generated
 * using the static create(DataInput) method, manipulated using various
 * operators, and persisted back using the write(DataOutput) method.
 *
 * @author Mark Welsh
 */
public class ClassFile implements ClassConstants
{
    // Constants -------------------------------------------------------------
    /**
     * The constant SEP_REGULAR.
     */
    public static final String SEP_REGULAR = "/";
    /**
     * The constant SEP_INNER.
     */
    public static final String SEP_INNER = "$";
    /**
     * The constant LOG_DANGER_HEADER1.
     */
    public static final String LOG_DANGER_HEADER1 = "Methods are called which may break in obfuscated version at runtime.";
    /**
     * The constant LOG_DANGER_HEADER2.
     */
    public static final String LOG_DANGER_HEADER2 = "Please review your source code to ensure that the dangerous methods are not intended";
    /**
     * The constant LOG_DANGER_HEADER3.
     */
    public static final String LOG_DANGER_HEADER3 = "to act on classes which have been obfuscated.";
    private static final String[] SEMI_DANGEROUS_CLASS_SIMPLENAME_DESCRIPTOR_ARRAY = {
        "forName(Ljava/lang/String;)Ljava/lang/Class;",
        "forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
    };
    private static final String[] DANGEROUS_CLASS_SIMPLENAME_DESCRIPTOR_ARRAY = {
        "forName(Ljava/lang/String;)Ljava/lang/Class;",
        "forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
        "getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
        "getField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
        "getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
        "getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"
    };
    private static final String LOG_DANGER_CLASS_PRE = "    Your class ";
    private static final String LOG_DANGER_CLASS_MID = " calls the java.lang.Class method ";
    private static final String[] DANGEROUS_CLASSLOADER_SIMPLENAME_DESCRIPTOR_ARRAY = {
        "defineClass(Ljava/lang/String;[BII)Ljava/lang/Class;",
        "findLoadedClass(Ljava/lang/String;)Ljava/lang/Class;",
        "findSystemClass(Ljava/lang/String;)Ljava/lang/Class;",
        "loadClass(Ljava/lang/String;)Ljava/lang/Class;",
        "loadClass(Ljava/lang/String;Z)Ljava/lang/Class;"
    };
    private static final String LOG_DANGER_CLASSLOADER_PRE = "    Your class ";
    private static final String LOG_DANGER_CLASSLOADER_MID = " calls the java.lang.ClassLoader method ";

    /**
     * {@link java.lang.runtime.ObjectMethods} method.
     */
    private static final int BM_TYPE_OM = 3;
    /**
     * {@link java.lang.invoke.StringConcatFactory} method.
     */
    private static final int BM_TYPE_SCF = 2;
    /**
     * {@link java.lang.invoke.LambdaMetafactory} method.
     */
    private static final int BM_TYPE_LMF = 1;
    /**
     * Unknown bootstrap method, throw exception.
     */
    private static final int BM_TYPE_UNKNOWN = 0;

    // Fields ----------------------------------------------------------------
    private int u4magic;
    private int u2minorVersion;
    private int u2majorVersion;
    private ConstantPool constantPool;
    private int u2accessFlags;
    private int u2thisClass;
    private int u2superClass;
    private int u2interfacesCount;
    private int u2interfaces[];
    private int u2fieldsCount;
    private FieldInfo fields[];
    private int u2methodsCount;
    private MethodInfo methods[];
    private int u2attributesCount;
    private AttrInfo attributes[];

    private boolean isUnkAttrGone = false;

    private static boolean writeIdString = false;
    private static CpInfo cpIdString = null;


    // Class Methods ---------------------------------------------------------

    /**
     * Define a constant String to include in every output class file.
     *
     * @param id the id
     */
    public static void defineIdString(String id)
    {
        if (id != null) {
            writeIdString = true;
            cpIdString = new Utf8CpInfo(id);
        } else {
            writeIdString = false;
            cpIdString = null;
        }
    }

    /**
     * Create a new ClassFile from the class file format data in the DataInput
     * stream.
     *
     * @param din the din
     * @return the class file
     * @throws IOException if class file is corrupt or incomplete
     */
    public static ClassFile create(DataInput din) throws java.io.IOException
    {
        if (din == null) throw new NullPointerException("No input stream was provided.");
        ClassFile cf = new ClassFile();
        cf.read(din);
        return cf;
    }

    /**
     * Parse a method or field descriptor into a list of parameter names (for methods)
     * and a return type, in same format as the Class.forName() method returns .
     *
     * @param descriptor the descriptor
     * @return the string [ ]
     */
    public static String[] parseDescriptor(String descriptor)
    {
        return parseDescriptor(descriptor, false);
    }

    /**
     * Parse a method or field descriptor into a list of parameter names (for methods)
     * and a return type, in same format as the Class.forName() method returns .
     *
     * @param descriptor the descriptor
     * @param isDisplay  the is display
     * @return the string [ ]
     */
    public static String[] parseDescriptor(String descriptor, boolean isDisplay)
    {
        // Check for field descriptor
        String[] names = null;
        if (descriptor.charAt(0) != '(')
        {
            names = new String[1];
            names[0] = descriptor;
        }
        else
        {
            // Method descriptor
            Vector namesVec = new Vector();
            descriptor = descriptor.substring(1);
            String type = "";
            while (descriptor.length() > 0)
            {
                switch (descriptor.charAt(0))
                {
                case '[':
                    type = type + "[";
                    descriptor = descriptor.substring(1);
                    break;

                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'S':
                case 'Z':
                case 'V':
                    namesVec.addElement(type + descriptor.substring(0, 1));
                    descriptor = descriptor.substring(1);
                    type = "";
                    break;

                case ')':
                    descriptor = descriptor.substring(1);
                    break;

                case 'L':
                    {
                        int pos = descriptor.indexOf(';') + 1;
                        namesVec.addElement(type + descriptor.substring(0, pos));
                        descriptor = descriptor.substring(pos);
                        type = "";
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Illegal field or method descriptor: " + descriptor);
                }
            }
            names = new String[namesVec.size()];
            for (int i = 0; i < names.length; i++)
            {
                names[i] = (String)namesVec.elementAt(i);
            }
        }

        // Translate the names from JVM to Class.forName() format.
        String[] translatedNames = new String[names.length];
        for (int i = 0; i < names.length; i++)
        {
            translatedNames[i] = translateType(names[i], isDisplay);
        }
        return translatedNames;
    }

    /**
     * Translate a type specifier from the internal JVM convention to the Class.forName() one.
     *
     * @param inName    the in name
     * @param isDisplay the is display
     * @return the string
     */
    public static String translateType(String inName, boolean isDisplay)
    {
        String outName = null;
        switch (inName.charAt(0))
        {
        case '[': // For array types, Class.forName() inconsistently uses the internal type name
                  // but with '/' --> '.'
            if (!isDisplay)
            {
                // return the Class.forName() form
                outName = translate(inName);
            }
            else
            {
                // return the pretty display form
                outName = translateType(inName.substring(1), true) + "[]";
            }
            break;

        case 'B':
            outName = Byte.TYPE.getName();
            break;

        case 'C':
            outName = Character.TYPE.getName();
            break;

        case 'D':
            outName = Double.TYPE.getName();
            break;

        case 'F':
            outName = Float.TYPE.getName();
            break;

        case 'I':
            outName = Integer.TYPE.getName();
            break;

        case 'J':
            outName = Long.TYPE.getName();
            break;

        case 'S':
            outName = Short.TYPE.getName();
            break;

        case 'Z':
            outName = Boolean.TYPE.getName();
            break;

        case 'V':
            outName = Void.TYPE.getName();
            break;

        case 'L':
            {
                int pos = inName.indexOf(';');
                outName = translate(inName.substring(1, inName.indexOf(';')));
            }
            break;

        default:
            throw new IllegalArgumentException("Illegal field or method name: " + inName);
        }
        return outName;
    }

    /**
     * Translate a class name from the internal '/' convention to the regular '.' one.
     *
     * @param name the name
     * @return the string
     */
    public static String translate(String name)
    {
        return name.replace('/', '.');
    }


    // Instance Methods ------------------------------------------------------
    // Private constructor.
    private ClassFile() {}

    // Import the class data to internal representation.
    private void read(DataInput din) throws java.io.IOException
    {
        // Read the class file
        u4magic = din.readInt();
        u2minorVersion = din.readUnsignedShort();
        u2majorVersion = din.readUnsignedShort();

        // Check this is a valid classfile that we can handle
        if (u4magic != MAGIC)
        {
            throw new IOException("Invalid magic number in class file.");
        }
        if (u2majorVersion > MAJOR_VERSION)
        {
            throw new IOException("Incompatible version number for class file format: " + u2majorVersion+"."+u2minorVersion);
        }


        int u2constantPoolCount = din.readUnsignedShort();
        CpInfo[] cpInfo = new CpInfo[u2constantPoolCount];
        // Fill the constant pool, recalling the zero entry
        // is not persisted, nor are the entries following a Long or Double
        for (int i = 1; i < u2constantPoolCount; i++)
        {
            cpInfo[i] = CpInfo.create(din);
            if ((cpInfo[i] instanceof LongCpInfo) ||
                (cpInfo[i] instanceof DoubleCpInfo))
            {
                i++;
            }
            // todo: remove once remapping CONSTANT_Dynamic_info is supported
            else if (cpInfo[i] instanceof DynamicCpInfo) {
                throw new IOException("Unsupported tag type in constant pool: dynamic");
            }
        }
        constantPool = new ConstantPool(this, cpInfo);

        u2accessFlags = din.readUnsignedShort();
        u2thisClass = din.readUnsignedShort();
        u2superClass = din.readUnsignedShort();
        u2interfacesCount = din.readUnsignedShort();
        u2interfaces = new int[u2interfacesCount];
        for (int i = 0; i < u2interfacesCount; i++)
        {
            u2interfaces[i] = din.readUnsignedShort();
        }
        u2fieldsCount = din.readUnsignedShort();
        fields = new FieldInfo[u2fieldsCount];
        for (int i = 0; i < u2fieldsCount; i++)
        {
            fields[i] = FieldInfo.create(din, this);
        }
        u2methodsCount = din.readUnsignedShort();
        methods = new MethodInfo[u2methodsCount];
        for (int i = 0; i < u2methodsCount; i++)
        {
            methods[i] = MethodInfo.create(din, this);
        }
        u2attributesCount = din.readUnsignedShort();
        attributes = new AttrInfo[u2attributesCount];
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i] = AttrInfo.create(din, this);
        }
    }

    /**
     * Get class file access int.
     *
     * @return the int
     */
    public int getClassFileAccess() {
      return u2accessFlags;
    }

    /**
     * Get modifiers int.
     *
     * @return the int
     */
    public int getModifiers() {
      int mods = 0;
      if ((u2accessFlags & 0x0001) == 0x0001) mods |= Modifier.PUBLIC;
      if ((u2accessFlags & 0x0010) == 0x0010) mods |= Modifier.FINAL;
      if ((u2accessFlags & 0x0200) == 0x0200) mods |= Modifier.INTERFACE;
      if ((u2accessFlags & 0x0400) == 0x0400) mods |= Modifier.ABSTRACT;
      return mods;
    }

    /**
     * Return the name of this classfile.
     *
     * @return the name
     */
    public String getName()
    {
        return toName(u2thisClass);
    }

    /**
     * Return the name of this class's superclass.
     *
     * @return the super
     */
    public String getSuper()
    {
        // This may be java/lang/Object, in which case there is no super
        return (u2superClass == 0) ? null : toName(u2superClass);
    }

    /**
     * Return the names of this class's interfaces.
     *
     * @return the string [ ]
     */
    public String[] getInterfaces()
    {
        String[] interfaces = new String[u2interfacesCount];
        for (int i = 0; i < u2interfacesCount; i++)
        {
            interfaces[i] = toName(u2interfaces[i]);
        }
        return interfaces;
    }

    // Convert a CP index to a class name.
    private String toName(int u2index) 
    {
        CpInfo classEntry = getCpEntry(u2index);
        if (classEntry instanceof ClassCpInfo)
        {
            CpInfo nameEntry = getCpEntry(((ClassCpInfo)classEntry).getNameIndex());
            if (nameEntry instanceof Utf8CpInfo)
            {
                return ((Utf8CpInfo)nameEntry).getString();
            }
            else
            {
                throw new ParseException("Inconsistent Constant Pool in class file.");
            }
        }
        else
        {
            throw new ParseException("Inconsistent Constant Pool in class file.");
        }
    }

    /**
     * Return an enumeration of method name/descriptor pairs.
     *
     * @return the method enum
     */
    public Enumeration getMethodEnum()
    {
        Vector vec = new Vector();
        for (int i = 0; i < methods.length; i++)
        {
            vec.addElement(methods[i]);
        }
        return vec.elements();
    }

    /**
     * Return an enumeration of field name/descriptor pairs.
     *
     * @return the field enum
     */
    public Enumeration getFieldEnum()
    {
        Vector vec = new Vector();
        for (int i = 0; i < fields.length; i++)
        {
          vec.addElement(fields[i]);
        }
        return vec.elements();
    }

    /**
     * Lookup the entry in the constant pool and return as an Object.
     *
     * @param cpIndex the cp index
     * @return the cp entry
     */
    public CpInfo getCpEntry(int cpIndex)
    {
        return constantPool.getCpEntry(cpIndex);
    }

    private String getUtf8(int cpIndex) {
        return ((Utf8CpInfo) getCpEntry(cpIndex)).getString();
    }

    /**
     * Gets constant pool.
     *
     * @return the constant pool
     */
    public ConstantPool getConstantPool() {
        return constantPool;
    }

    /**
     * Check for methods which can break the obfuscated code, and log them to a String[].
     *
     * @param replaceClassNameStrings the replace class name strings
     * @return the string [ ]
     */
    public String[] logDangerousMethods(boolean replaceClassNameStrings)
    {
        Vector warningVec = new Vector();

        // Need only check CONSTANT_Methodref entries of constant pool since
        // dangerous methods belong to classes 'Class' and 'ClassLoader', not to interfaces.
        for (Enumeration enumeration = constantPool.elements(); enumeration.hasMoreElements(); )
        {
            Object o = enumeration.nextElement();
            if (o instanceof MethodrefCpInfo)
            {
                // Get the method class name, simple name and descriptor
                MethodrefCpInfo entry = (MethodrefCpInfo)o;
                ClassCpInfo classEntry = (ClassCpInfo)getCpEntry(entry.getClassIndex());
                String className = ((Utf8CpInfo)getCpEntry(classEntry.getNameIndex())).getString();
                NameAndTypeCpInfo ntEntry = (NameAndTypeCpInfo)getCpEntry(entry.getNameAndTypeIndex());
                String name = ((Utf8CpInfo)getCpEntry(ntEntry.getNameIndex())).getString();
                String descriptor = ((Utf8CpInfo)getCpEntry(ntEntry.getDescriptorIndex())).getString();

                // Check if this is on the proscribed list
                if (className.equals("java/lang/Class") &&
                    Tools.isInArray(name + descriptor, DANGEROUS_CLASS_SIMPLENAME_DESCRIPTOR_ARRAY))
                {
                  if (replaceClassNameStrings){
                    if (!Tools.isInArray(name+descriptor, SEMI_DANGEROUS_CLASS_SIMPLENAME_DESCRIPTOR_ARRAY)){
                      String jMethod = Conversion.toJavaMethod(name, descriptor);
                        warningVec.addElement(LOG_DANGER_CLASS_PRE + Conversion.toJavaClass(getName()) + LOG_DANGER_CLASS_MID + jMethod);
                    }
                  }
                }
                else if (Tools.isInArray(name + descriptor, DANGEROUS_CLASSLOADER_SIMPLENAME_DESCRIPTOR_ARRAY))
                {
                  String jMethod = Conversion.toJavaMethod(name, descriptor);
                    warningVec.addElement(LOG_DANGER_CLASSLOADER_PRE + Conversion.toJavaClass(getName()) + LOG_DANGER_CLASSLOADER_MID + jMethod);
                } else if ("class$(Ljava/lang/String;)Ljava/lang/Class;".equals(name+descriptor)){
                  if (!replaceClassNameStrings){
                     warningVec.addElement(LOG_DANGER_CLASS_PRE + Conversion.toJavaClass(getName()) +" seems to be using the '.class' construct!");
                  }
                }
            }
        }

        // Copy any warnings to a String[]
        String[] warnings = new String[warningVec.size()];
        for (int i = 0; i < warnings.length; i++)
        {
            warnings[i] = (String)warningVec.elementAt(i);
        }
        return warnings;
    }

    /** Check for methods which can break the obfuscated code, and log them. */
    private static boolean hasHeader = false;

    /**
     * Reset danger header.
     */
    public static void resetDangerHeader() {
      hasHeader = false;
    }

    /**
     * Log dangerous methods.
     *
     * @param log                     the log
     * @param replaceClassNameStrings the replace class name strings
     */
    public void logDangerousMethods(PrintWriter log, boolean replaceClassNameStrings)
    {
        // Get any warnings and print them to the logfile
        String[] warnings = logDangerousMethods(replaceClassNameStrings);
        if (warnings != null && warnings.length > 0)
        {
            if (!hasHeader)
            {
                log.println("<!-- WARNING");
                log.println(LOG_DANGER_HEADER1);
                log.println(LOG_DANGER_HEADER2);
                log.println(LOG_DANGER_HEADER3);
                
                Logger logger = Logger.getInstance();
                logger.warning(LOG_DANGER_HEADER1+'\n'+
                               LOG_DANGER_HEADER2+'\n'+
                               LOG_DANGER_HEADER3+'\n'+
                               "See the logfile for a list of these classes and methods.");
                
                log.println("-->");
                hasHeader = true;
            }
            if (warnings.length > 0){
              log.println("<!--");
              for (int i = 0; i < warnings.length; i++)
              {
                  log.println(" " + warnings[i]);
              }
              log.println("-->");
            }
        }
    }

    /**
     * Check for direct references to Utf8 constant pool entries.
     *
     * @param pool the pool
     */
    public void markUtf8Refs(ConstantPool pool)
    {
        try
        {
            // Check for references to Utf8 from outside the constant pool
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].markUtf8Refs(pool);
            }
            for (int i = 0; i < methods.length; i++)
            {
                methods[i].markUtf8Refs(pool); // also checks Code/LVT attrs here
            }
            for (int i = 0; i < attributes.length; i++)
            {
                attributes[i].markUtf8Refs(pool); // checks InnerClasses, SourceFile and all attr names
            }

            // Now check for references from other CP entries
            for (Enumeration enumeration = pool.elements(); enumeration.hasMoreElements(); )
            {
                Object o = enumeration.nextElement();
                if (o instanceof NameAndTypeCpInfo ||
                    o instanceof AbstractTypeCpInfo ||
                    o instanceof MethodTypeCpInfo ||
                    o instanceof StringCpInfo)
                {
                    ((CpInfo)o).markUtf8Refs(pool);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ParseException("Inconsistent reference to constant pool.");
        }
    }

    /**
     * Check for direct references to NameAndType constant pool entries.
     *
     * @param pool the pool
     */
    public void markNTRefs(ConstantPool pool)
    {
        try
        {
            // Now check the method and field CP entries
            for (Enumeration enumeration = pool.elements(); enumeration.hasMoreElements(); )
            {
                Object o = enumeration.nextElement();
                if (o instanceof RefCpInfo ||
                    o instanceof AbstractDynamicCpInfo)
                {
                    ((CpInfo)o).markNTRefs(pool);
                } 
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new ParseException("Inconsistent reference to constant pool.");
        }
    }

    /**
     * Trim attributes from the classfile ('Code', 'Exceptions', 'ConstantValue'
     * are preserved, all others except the list in the String[] are killed).
     *
     * @param extraAttrs the extra attrs
     */
    public void trimAttrsExcept(String[] extraAttrs)
    {
        // Merge additional attributes with required list
        String[] keepAttrs = REQUIRED_ATTRS;
        if (extraAttrs != null && extraAttrs.length > 0)
        {
            String[] tmp = new String[keepAttrs.length + extraAttrs.length];
            System.arraycopy(keepAttrs, 0, tmp, 0, keepAttrs.length);
            System.arraycopy(extraAttrs, 0, tmp, keepAttrs.length, extraAttrs.length);
            keepAttrs = tmp;
        }

        // Traverse all attributes, removing all except those on 'keep' list
        for (int i = 0; i < fields.length; i++)
        {
            fields[i].trimAttrsExcept(keepAttrs);
        }
        for (int i = 0; i < methods.length; i++)
        {
            methods[i].trimAttrsExcept(keepAttrs);
        }
        for (int i = 0; i < attributes.length; i++)
        {
            if (Tools.isInArray(attributes[i].getAttrName(), keepAttrs))
            {
                attributes[i].trimAttrsExcept(keepAttrs);
            }
            else
            {
                attributes[i] = null;
            }
        }

        // Delete the marked attributes
        AttrInfo[] left = new AttrInfo[attributes.length];
        int j = 0;
        for (int i = 0; i < attributes.length; i++)
        {
            if (attributes[i] != null)
            {
                left[j++] = attributes[i];
            }
        }
        attributes = new AttrInfo[j];
        System.arraycopy(left, 0, attributes, 0, j);
        u2attributesCount = j;

        // Signal that unknown attributes are gone
        isUnkAttrGone = true;

        // Update the constant pool reference counts
        constantPool.updateRefCount();
    }

    /**
     * Gets inner class modifiers.
     *
     * @return the inner class modifiers
     */
    public Map getInnerClassModifiers()  {
      Map map = new HashMap();
        for (int i = 0; i < u2attributesCount; i++)
        {
            AttrInfo attrInfo = attributes[i];
            if (attrInfo instanceof InnerClassesAttrInfo)
            {
                InnerClassesInfo[] info = ((InnerClassesAttrInfo)attrInfo).getInfo();
                for (int j = 0; j < info.length; j++)
                {
                  InnerClassesInfo ici = info[j];
                  int index = info[j].getInnerNameIndex();
                  if (index == 0){ // unnamed inner class
                    continue;
                  }
                  CpInfo cpInfo = getCpEntry(info[j].getInnerNameIndex());
                  if (cpInfo instanceof Utf8CpInfo)
                  {
                      Utf8CpInfo utf = (Utf8CpInfo)cpInfo;
                      String origClass = utf.getString();
                      map.put(origClass, new Integer(ici.getModifiers()));
                  }
                }
            }
        }
        return map;
    }

    /**
     * Trim attributes from the classfile ('Code', 'Exceptions', 'ConstantValue'
     * are preserved, all others are killed).
     */
    public void trimAttrs() {
      trimAttrsExcept(null);
    }
    
    private boolean containsDotClassMethodReference(){
      // Need only check CONSTANT_Methodref entries of constant pool since
        // dangerous methods belong to classes 'Class' and 'ClassLoader', not to interfaces.
        for (Enumeration enumeration = constantPool.elements(); enumeration.hasMoreElements(); )
        {
            Object o = enumeration.nextElement();
            if (o instanceof MethodrefCpInfo)
            {
                // Get the method class name, simple name and descriptor
                MethodrefCpInfo entry = (MethodrefCpInfo)o;
                ClassCpInfo classEntry = (ClassCpInfo)getCpEntry(entry.getClassIndex());
                String className = ((Utf8CpInfo)getCpEntry(classEntry.getNameIndex())).getString();
                NameAndTypeCpInfo ntEntry = (NameAndTypeCpInfo)getCpEntry(entry.getNameAndTypeIndex());
                String name = ((Utf8CpInfo)getCpEntry(ntEntry.getNameIndex())).getString();

                if (name.equals("class$")){
                  String descriptor = ((Utf8CpInfo)getCpEntry(ntEntry.getDescriptorIndex())).getString();
                  if (descriptor.equals("(Ljava/lang/String;)Ljava/lang/Class;")){
                    return true;
                  }
                }
            }
        }
        return false;
    }
    
    private boolean containsClassMethodReference(String cName, String des){
      // Need only check CONSTANT_Methodref entries of constant pool since
        // dangerous methods belong to classes 'Class' and 'ClassLoader', not to interfaces.
        for (Enumeration enumeration = constantPool.elements(); enumeration.hasMoreElements(); )
        {
            Object o = enumeration.nextElement();
            if (o instanceof MethodrefCpInfo)
            {
                // Get the method class name, simple name and descriptor
                MethodrefCpInfo entry = (MethodrefCpInfo)o;
                ClassCpInfo classEntry = (ClassCpInfo)getCpEntry(entry.getClassIndex());
                String className = ((Utf8CpInfo)getCpEntry(classEntry.getNameIndex())).getString();
                NameAndTypeCpInfo ntEntry = (NameAndTypeCpInfo)getCpEntry(entry.getNameAndTypeIndex());
                String name = ((Utf8CpInfo)getCpEntry(ntEntry.getNameIndex())).getString();
                String descriptor = ((Utf8CpInfo)getCpEntry(ntEntry.getDescriptorIndex())).getString();

                // Check if this is on the proscribed list
                if (className.equals(cName) && (name+descriptor).equals(des)){
                  return true;
                }
            }
        }
        return false;
    }

    /**
     * Remap the entities in the specified ClassFile.
     *
     * @param nm                      the nm
     * @param replaceClassNameStrings the replace class name strings
     * @param log                     the log
     */
    public void remap(NameMapper nm, boolean replaceClassNameStrings, PrintWriter log)
    {
        // Remap all the package/interface/class/method/field names
        //
        String thisClassName = ((Utf8CpInfo)getCpEntry(((ClassCpInfo)getCpEntry(u2thisClass)).getNameIndex())).getString();

        // Remove unnecessary attributes from the class
        final String[] attributesToKeep = nm.getAttrsToKeep(thisClassName);
        if (attributesToKeep.length > 0)
        {
            trimAttrsExcept(attributesToKeep);
        }
        else
        {
            trimAttrs();
        }

        // Remap the 'inner name' reference of the 'InnerClasses' attribute
        for (int i = 0; i < u2attributesCount; i++)
        {
            AttrInfo attrInfo = attributes[i];
            if (attrInfo instanceof RuntimeVisibleAnnotationsAttrInfo){
              remapAnnotations((RuntimeVisibleAnnotationsAttrInfo)attrInfo, nm);
            } else if (attrInfo instanceof InnerClassesAttrInfo) {
                // For each inner class referemce,
                InnerClassesInfo[] info = ((InnerClassesAttrInfo)attrInfo).getInfo();
                for (int j = 0; j < info.length; j++)
                {
                    // Get the 'inner name' (it is a CONSTANT_Utf8)
                    CpInfo cpInfo = getCpEntry(info[j].getInnerNameIndex());
                    if (cpInfo instanceof Utf8CpInfo)
                    {
                        // Get the remapped class name
                        Utf8CpInfo utf = (Utf8CpInfo)cpInfo;
                        String origClass = utf.getString();

                        // Only remap non-anonymous classes (anon are "")
                        if (!origClass.equals(""))
                        {
                            // Get the full inner class name
                            ClassCpInfo innerClassInfo = (ClassCpInfo)getCpEntry(info[j].getInnerClassIndex());
                            String innerClassName = ((Utf8CpInfo)getCpEntry(innerClassInfo.getNameIndex())).getString();

                            // It is the remapped simple name that must be stored, so truncate it
                            String remapClass = nm.mapClass(innerClassName);
                            remapClass = remapClass.substring(remapClass.lastIndexOf('$') + 1);
                            int remapIndex = constantPool.remapUtf8To(remapClass, info[j].getInnerNameIndex());
                            info[j].setInnerNameIndex(remapIndex);
                        }
                    }
                }
            } else if (attrInfo instanceof EnclosingMethodAttrInfo){
              EnclosingMethodAttrInfo eam = (EnclosingMethodAttrInfo) attrInfo;
              
              // get the class name of the enclosing file:
              CpInfo cpi = getCpEntry(eam.getClassIndex());
              if (cpi instanceof ClassCpInfo){
                ClassCpInfo ccpi = (ClassCpInfo) cpi;
                cpi = getCpEntry(ccpi.getNameIndex());
                if (cpi instanceof Utf8CpInfo){
                  Utf8CpInfo utf = (Utf8CpInfo) cpi;
                  String origClass = utf.getString();

                  // do not remap the ClassCpInfo now, it will be remapped automatically, later!
                  String remapClass = nm.mapClass(origClass);

                  // if NT > 0 there is a valid NT to be remapped
                  if (eam.getNameAndTypeIndex() > 0) {
                    cpi = getCpEntry(eam.getNameAndTypeIndex());
                    if (cpi instanceof NameAndTypeCpInfo){
                      NameAndTypeCpInfo nameTypeInfo = (NameAndTypeCpInfo) cpi;
                      Utf8CpInfo refUtf = (Utf8CpInfo)getCpEntry(nameTypeInfo.getNameIndex());
                      Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(nameTypeInfo.getDescriptorIndex());
                      String origMethodName = refUtf.getString();
                      String origDescriptor = descUtf.getString();
                      String remapRef = nm.mapMethod(origClass, origMethodName, origDescriptor);
                      String remapDesc = nm.mapDescriptor(descUtf.getString());
                      eam.setNameAndTypeIndex(remapNT(refUtf, remapRef, descUtf, remapDesc, nameTypeInfo, eam.getNameAndTypeIndex()));
                    }
                  }
                }
              }
            } else if (attrInfo instanceof SignatureAttrInfo){
              remapSignature(nm, (SignatureAttrInfo) attrInfo);
            } else if (attrInfo instanceof SourceFileAttrInfo) {
              SourceFileAttrInfo source = (SourceFileAttrInfo) attrInfo;
              CpInfo cpInfo = getCpEntry(source.getSourceFileIndex());
              if (cpInfo instanceof Utf8CpInfo){
                Utf8CpInfo utf = (Utf8CpInfo) cpInfo;
                String origName = utf.getString();
                if (origName != null && origName.length() > 0){
                  String newName = nm.mapSourceFile(thisClassName, origName);
                  if (!origName.equals(newName)){
                    if (newName == null || newName.length() < 1){
                      AttrInfo[] newAttributes = new AttrInfo[attributes.length - 1];
                      System.arraycopy(attributes, 0, newAttributes, 0, i);
                      if (newAttributes.length > i){
                        System.arraycopy(attributes, i + 1, newAttributes, i, newAttributes.length - i);
                      }
                      attributes = newAttributes;
                      u2attributesCount--;
                      i--;
                      constantPool.decRefCount(source.getAttrNameIndex());
                      utf.decRefCount();
                    } else {
                      int remapIndex = constantPool.remapUtf8To(newName, source.getSourceFileIndex());
                      source.setSourceFileIndex(remapIndex);
                    }
                  }
                }
              }
//            } else if (attrInfo instanceof ModuleAttrInfo) {
                // should not be necessary to adjust anything because the
                // attribute references
                //   CONSTANT_Class_Info,
                //   CONSTANT_Module_Info, and
                //   CONSTANT_Package_Info
                // structures
//            } else if (attrInfo instanceof ModuleMainClassAttrInfo) {
                // should not be necessary to adjust anything because the
                // attribute references a
                //   CONSTANT_Class_Info
                // structure
//            } else if (attrInfo instanceof ModulePackagesAttrInfo) {
                // should not be necessary to adjust anything because the
                // attribute references
                //   CONSTANT_Package_Info
                // structures
            } else if (attrInfo instanceof RecordAttrInfo) {
              final RecordAttrInfo record = (RecordAttrInfo) attrInfo;
              final RecordComponent[] components = record.getComponents();
              for (int j = 0, n = components.length; j < n; ++j) {
                final int nameIndex = components[j].getNameIndex();
                final Utf8CpInfo nameUtf = (Utf8CpInfo) getCpEntry(nameIndex);
                final String remapName = nm.mapField(thisClassName, nameUtf.getString());
                final int remapNameIndex = constantPool.remapUtf8To(remapName, nameIndex);
                components[j].setNameIndex(remapNameIndex);

                final int descIndex = components[j].getDescriptorIndex();
                final Utf8CpInfo descUtf = (Utf8CpInfo) getCpEntry(descIndex);
                final String remapDesc = nm.mapDescriptor(descUtf.getString());
                final int remapDescIndex = constantPool.remapUtf8To(remapDesc, descIndex);
                components[j].setDescriptorIndex(remapDescIndex);

                // attributes:
                // - Signature
                //   Nothing to do.
                //   Such a signature is always a simple name that does not
                //   need to be changed.

                // - RuntimeVisibleAnnotations
                // - RuntimeInvisibleAnnotations
                final AttrInfo[] attributes = components[j].getAttributes();
                for (int k = 0; k < attributes.length; ++k) {
                  if (attributes[k] instanceof RuntimeVisibleAnnotationsAttrInfo) {
                    remapAnnotations((RuntimeVisibleAnnotationsAttrInfo) attributes[k], nm);
                  }
                }

                // - RuntimeVisibleTypeAnnotations
                // - RuntimeInvisibleTypeAnnotations
                //   Currently not supported because obfuscating type
                //   annotations requires adjusting code blocks. 
              }
            }
        }

        // Remap the 'name' and 'descriptor' references of the 'LocalVariableTable'
        // attribute, in the 'Code' attribute of method structures.
        for (int i = 0; i < u2methodsCount; i++)
        {
            for (int j = 0; j < methods[i].u2attributesCount; j++)
            {
                final String methodName = methods[i].getName();
                final String descriptor = methods[i].getDescriptor();
                AttrInfo attrInfo = methods[i].attributes[j];
                
                if (attrInfo instanceof AnnotationDefaultAttrInfo){
                  remapAnnotationDefault((AnnotationDefaultAttrInfo)attrInfo, nm);
                } else if (attrInfo instanceof RuntimeVisibleAnnotationsAttrInfo){
                  remapAnnotations((RuntimeVisibleAnnotationsAttrInfo)attrInfo, nm);
                } else if (attrInfo instanceof RuntimeVisibleParameterAnnotationsAttrInfo){
                  remapAnnotations((RuntimeVisibleParameterAnnotationsAttrInfo)attrInfo, nm);
                } else if (attrInfo instanceof SignatureAttrInfo){
                  remapSignature(nm, (SignatureAttrInfo) attrInfo);
                } else if (attrInfo instanceof CodeAttrInfo) {
                    CodeAttrInfo codeAttrInfo = (CodeAttrInfo)attrInfo;
                    for (int k = 0; k < codeAttrInfo.u2attributesCount; k++)
                    {
                        AttrInfo innerAttrInfo = codeAttrInfo.attributes[k];
                        if (innerAttrInfo instanceof LocalVariableTableAttrInfo)
                        {
                            LocalVariableTableAttrInfo lvtAttrInfo = (LocalVariableTableAttrInfo)innerAttrInfo;
                            LocalVariableInfo[] lvts = lvtAttrInfo.getLocalVariableTable();
                            for (int m = 0; m < lvts.length; m++)
                            {
                                // Remap name
                                Utf8CpInfo nameUtf = (Utf8CpInfo)getCpEntry(lvts[m].getNameIndex());
                                String remapName = nm.mapLocalVariable(thisClassName, methodName, descriptor, nameUtf.getString());
                                if (remapName == null || remapName.length() < 1){
                                  constantPool.decRefCount(lvts[m].getNameIndex());
                                  constantPool.decRefCount(lvts[m].getDescriptorIndex());
                                  LocalVariableInfo[] newArray = new LocalVariableInfo[lvts.length - 1];
                                  System.arraycopy(lvts, 0, newArray, 0, m);
                                  if (newArray.length > m ){
                                    System.arraycopy(lvts, m + 1, newArray, m, newArray.length - m);
                                  }
                                  lvts = newArray;
                                  lvtAttrInfo.setLocalVariableTable(lvts);
                                  m--;
                                } else {
                                  lvts[m].setNameIndex(constantPool.remapUtf8To(remapName, lvts[m].getNameIndex()));

                                  // Remap descriptor
                                  Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(lvts[m].getDescriptorIndex());
                                  String remapDesc = nm.mapDescriptor(descUtf.getString());
                                  lvts[m].setDescriptorIndex(constantPool.remapUtf8To(remapDesc, lvts[m].getDescriptorIndex()));
                                }
                            }
                        } else if (innerAttrInfo instanceof LocalVariableTypeTableAttrInfo){
                            LocalVariableTypeTableAttrInfo lvttAttrInfo = (LocalVariableTypeTableAttrInfo) innerAttrInfo;
                            LocalVariableTypeInfo[] lvts = lvttAttrInfo.getLocalVariableTypeTable();
                            for (int m = 0; m < lvts.length; m++){
                              // Remap name
                              Utf8CpInfo nameUtf = (Utf8CpInfo)getCpEntry(lvts[m].getNameIndex());
                              String remapName = nm.mapLocalVariable(thisClassName, methodName, descriptor, nameUtf.getString());
                              if (remapName == null || remapName.length() < 1){
                                constantPool.decRefCount(lvts[m].getNameIndex());
                                constantPool.decRefCount(lvts[m].getSignatureIndex());
                                LocalVariableTypeInfo[] newArray = new LocalVariableTypeInfo[lvts.length - 1];
                                System.arraycopy(lvts, 0, newArray, 0, m);
                                if (newArray.length > m ){
                                  System.arraycopy(lvts, m + 1, newArray, m, newArray.length - m);
                                }
                                lvts = newArray;
                                lvttAttrInfo.setLocalVariableTypeTable(lvts);
                                m--;
                              } else {
                                lvts[m].setNameIndex(constantPool.remapUtf8To(remapName, lvts[m].getNameIndex()));

                                // Remap descriptor
                                Utf8CpInfo signatureUtf = (Utf8CpInfo)getCpEntry(lvts[m].getSignatureIndex());
                                String remapSig = nm.mapSignature(signatureUtf.getString());
                                lvts[m].setSignatureIndex(constantPool.remapUtf8To(remapSig, lvts[m].getSignatureIndex()));
                              }
                            }
                        } else if (innerAttrInfo instanceof LineNumberTableAttrInfo) {
                           LineNumberTableAttrInfo ltai = (LineNumberTableAttrInfo) innerAttrInfo;
                           if (!nm.mapLineNumberTable(thisClassName, methodName, descriptor, ltai)){
                              AttrInfo[] newAtt = new AttrInfo[codeAttrInfo.u2attributesCount - 1];
                              System.arraycopy(codeAttrInfo.attributes, 0, newAtt, 0, k);
                              if (newAtt.length > k ){
                                System.arraycopy(codeAttrInfo.attributes, k + 1, newAtt, k, newAtt.length - k);
                              }
                              codeAttrInfo.attributes = newAtt;
                              codeAttrInfo.u2attributesCount--;
                              k--;
                           }
                        }
                    }
                }
            }
        }

        // Go through all of class's fields and methods mapping 'name' and 'descriptor' references
        for (int i = 0; i < u2fieldsCount; i++)
        {
            // Remap field 'name', unless it is 'Synthetic'
            FieldInfo field = fields[i];
            Utf8CpInfo nameUtf = (Utf8CpInfo)getCpEntry(field.getNameIndex());
            if (!field.isSynthetic() || nameUtf.getString().startsWith("class$"))
            {
                String remapName = nm.mapField(thisClassName, nameUtf.getString());
                field.setNameIndex(constantPool.remapUtf8To(remapName, field.getNameIndex()));
            }

            for (int j = 0; j < field.u2attributesCount; j++){
              AttrInfo attrInfo = field.attributes[j];
              if (attrInfo instanceof RuntimeVisibleAnnotationsAttrInfo){
                remapAnnotations((RuntimeVisibleAnnotationsAttrInfo)attrInfo, nm);
              } else if (attrInfo instanceof SignatureAttrInfo){
                remapSignature(nm, (SignatureAttrInfo) attrInfo);
              } 
            }

            // Remap field 'descriptor'
            Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(field.getDescriptorIndex());
            String remapDesc = nm.mapDescriptor(descUtf.getString());
            field.setDescriptorIndex(constantPool.remapUtf8To(remapDesc, field.getDescriptorIndex()));
        }
        for (int i = 0; i < u2methodsCount; i++)
        {
            // Remap method 'name', unless it is 'Synthetic'
            MethodInfo method = methods[i];
            Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(method.getDescriptorIndex());
            if (!method.isSynthetic())
            {
                Utf8CpInfo nameUtf = (Utf8CpInfo)getCpEntry(method.getNameIndex());
                String remapName = nm.mapMethod(thisClassName, nameUtf.getString(), descUtf.getString());
                method.setNameIndex(constantPool.remapUtf8To(remapName, method.getNameIndex()));
            }

            // Remap method 'descriptor'
            String remapDesc = nm.mapDescriptor(descUtf.getString());
            method.setDescriptorIndex(constantPool.remapUtf8To(remapDesc, method.getDescriptorIndex()));
        }
        
        // check whether .class constructs of Class.forName calls reside in the code..
        if (replaceClassNameStrings && nm instanceof ClassTree) 
//          && 
//          (containsClassMethodReference("java/lang/Class","forName(Ljava/lang/String;)Ljava/lang/Class;")) ||
//          (containsClassMethodReference("java/lang/Class","forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;")) ||
//          (containsDotClassMethodReference()))
        {
            this.replaceConstantPoolStrings((ClassTree)nm);
        }

      final LinkedHashSet ombIndicies = new LinkedHashSet();
      int currentCpLength = constantPool.length(); // constant pool can be extended (never contracted) during loop
        for (int i = 0; i < currentCpLength; i++) {
            CpInfo cpInfo = getCpEntry(i);
            if (cpInfo != null) {
                // If this is an entry that references Descriptors and/or names, adjust them correspondingly
                if (cpInfo instanceof InvokeDynamicCpInfo) {
                    InvokeDynamicCpInfo id = (InvokeDynamicCpInfo) cpInfo;

                    final BootstrapMethod bm = getBootstrapMethod(id);
                    switch (getType(bm)) {
                        case BM_TYPE_LMF: {
                            NameAndTypeCpInfo nameTypeInfo = (NameAndTypeCpInfo) getCpEntry(id.getNameAndTypeIndex());
                            Utf8CpInfo refUtf = (Utf8CpInfo) getCpEntry(nameTypeInfo.getNameIndex());
                            Utf8CpInfo descUtf = (Utf8CpInfo) getCpEntry(nameTypeInfo.getDescriptorIndex());

                            final String descriptor = descUtf.getString();
                            String className = descriptor.substring(descriptor.indexOf(")L") + 2, descriptor.length() - 1);

                            // find out the method descriptor of the method
                            MethodTypeCpInfo methodTypeInfo = (MethodTypeCpInfo) getCpEntry(bm.getBootstrapArguments()[0]);
                            Utf8CpInfo samMethodDescriptor = (Utf8CpInfo) getCpEntry(methodTypeInfo.getU2descriptorIndex());
                            // now find the mapping of the method
                            String remapName = nm.mapMethod(className, refUtf.getString(), samMethodDescriptor.getString());
                            String remapDesc = nm.mapDescriptor(descUtf.getString());

                            id.setNameAndTypeIndex(remapNT(refUtf, remapName, descUtf, remapDesc, nameTypeInfo, id.getNameAndTypeIndex()));
                        }   break;
                        case BM_TYPE_SCF: {
                            final int idx = id.getNameAndTypeIndex();

                            final NameAndTypeCpInfo ntInfo = (NameAndTypeCpInfo) getCpEntry(idx);
                            final Utf8CpInfo refUtf = (Utf8CpInfo) getCpEntry(ntInfo.getNameIndex());
                            final Utf8CpInfo descUtf = (Utf8CpInfo) getCpEntry(ntInfo.getDescriptorIndex());

                            final String remapDesc = nm.mapDescriptor(descUtf.getString());

                            id.setNameAndTypeIndex(remapNT(refUtf, refUtf.getString(), descUtf, remapDesc, ntInfo, idx));
                        }   break;
                        case BM_TYPE_OM: {
                            ombIndicies.add(Integer.valueOf(id.getBootstrapMethodAttrIndex()));

                            final int idx = id.getNameAndTypeIndex();

                            final NameAndTypeCpInfo ntInfo = (NameAndTypeCpInfo) getCpEntry(idx);
                            final Utf8CpInfo refUtf = (Utf8CpInfo) getCpEntry(ntInfo.getNameIndex());
                            final Utf8CpInfo descUtf = (Utf8CpInfo) getCpEntry(ntInfo.getDescriptorIndex());

                            final String remapDesc = nm.mapDescriptor(descUtf.getString());

                            id.setNameAndTypeIndex(remapNT(refUtf, refUtf.getString(), descUtf, remapDesc, ntInfo, idx));
                        }   break;
                        default:
                            final String sig = getBootstrapMethodSignature(bm);
                            throw new IllegalArgumentException("Unrecognized bootstrap method: " + sig);
                    }

                }
            }
        }
        if (!ombIndicies.isEmpty()) {
          final BootstrapMethodsAttrInfo attr = getBootstrapMethodAttribute();
          for (Iterator it = ombIndicies.iterator(); it.hasNext(); ) {
            final BootstrapMethod bm = attr.getBootstrapMethods()[((Integer) it.next()).intValue()];

            final StringCpInfo namesInfo = (StringCpInfo) getCpEntry(bm.getBootstrapArguments()[1]);
            final Utf8CpInfo names = (Utf8CpInfo) getCpEntry(namesInfo.getStringIndex());
            final StringBuilder sb = new StringBuilder();
            final String delim = ";";
            for (StringTokenizer st = new StringTokenizer(names.getString(), delim, true); st.hasMoreTokens();) {
              final String origName = st.nextToken();
              if (delim.equals(origName)) {
                sb.append(delim);
              } else {
                sb.append(nm.mapField(thisClassName, origName));
              }
            }
            final String remapNames = sb.toString();
            final int remapNamesIndex = constantPool.addUtf8Entry(remapNames);
            final StringCpInfo remapNamesInfo = new StringCpInfo();
            remapNamesInfo.setStringIndex(remapNamesIndex);
            final int remapNamesInfoIndex = constantPool.addEntry(remapNamesInfo);
            bm.getBootstrapArguments()[1] = remapNamesInfoIndex;
          }
        }

        // Remap all field/method names and descriptors in the constant pool (depends on class names)
        currentCpLength = constantPool.length(); // constant pool can be extended (never contracted) during loop
        for (int i = 0; i < currentCpLength; i++)
        {
            CpInfo cpInfo = getCpEntry(i);
            if (cpInfo != null)
            {
              // If this is an entry that references Descriptors and/or names, adjust them correspondingly
                if (cpInfo instanceof MethodTypeCpInfo){
                  MethodTypeCpInfo mt = (MethodTypeCpInfo) cpInfo;
                  Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(mt.getU2descriptorIndex());
                  String remapDesc = nm.mapDescriptor(descUtf.getString());
                  mt.setU2descriptorIndex(constantPool.remapUtf8To(remapDesc, mt.getU2descriptorIndex()));
                } else
                if (cpInfo instanceof RefCpInfo)
                {
                    // Get the unmodified class name
                    ClassCpInfo classInfo = (ClassCpInfo)getCpEntry(((RefCpInfo)cpInfo).getClassIndex());
                    Utf8CpInfo classUtf = (Utf8CpInfo)getCpEntry(classInfo.getNameIndex());
                    String className = classUtf.getString();

                    // Get the current N&T reference and its 'name' and 'descriptor' utf's
                    int ntIndex = ((RefCpInfo)cpInfo).getNameAndTypeIndex();
                    
                    NameAndTypeCpInfo nameTypeInfo = (NameAndTypeCpInfo)getCpEntry(ntIndex);
                    Utf8CpInfo refUtf = (Utf8CpInfo)getCpEntry(nameTypeInfo.getNameIndex());
                    Utf8CpInfo descUtf = (Utf8CpInfo)getCpEntry(nameTypeInfo.getDescriptorIndex());

                    // Get the remapped versions of 'name' and 'descriptor'
                    String remapRef;
                    if (cpInfo instanceof FieldrefCpInfo)
                    {
                        remapRef = nm.mapField(className, refUtf.getString());
                        
                        // check if this is a compiler generated field
                        // supporting the JDK1.2-or-later '.class' construct
                        if (refUtf.getString().startsWith("class$"))
                        {
                          if (!replaceClassNameStrings){
                            String internalClassName = refUtf.getString().substring(6);
                            String realClassName = internalClassName.replace('$', '.');
                            internalClassName = internalClassName.replace('$','/');
                            String map = nm.mapClass(internalClassName);
                            if (map != null && !internalClassName.equals(map)){
                              String warning = realClassName +
                                          " shouldn't be obfuscated: it is most likely referenced as " + realClassName + ".class from " + 
                                          Conversion.toJavaClass(thisClassName);
                              Logger.getInstance().warning(warning);
                              log.println("<!-- WARNING: " + warning + " -->");
                            }
                          } 
                        }
                    }
                    else
                    {
                      remapRef = nm.mapMethod(className, refUtf.getString(), descUtf.getString());
                    }
                    String remapDesc = nm.mapDescriptor(descUtf.getString());
                    ((RefCpInfo)cpInfo).setNameAndTypeIndex(remapNT(refUtf, remapRef, descUtf, remapDesc, nameTypeInfo, ((RefCpInfo)cpInfo).getNameAndTypeIndex()));
                } 
            }
        }

        // Finally, remap all class references to Utf
        for (int i = 0; i < constantPool.length(); i++)
        {
            CpInfo cpInfo = getCpEntry(i);
            if (cpInfo != null)
            {
                // If this is CONSTANT_Class, remap the class-name Utf8 entry
                if (cpInfo instanceof ClassCpInfo)
                {
                    ClassCpInfo classInfo = (ClassCpInfo)cpInfo;
                    Utf8CpInfo utf = (Utf8CpInfo)getCpEntry(classInfo.getNameIndex());
                    String remapClass = nm.mapClass(utf.getString());
                    int remapIndex = constantPool.remapUtf8To(remapClass, classInfo.getNameIndex());
                    classInfo.setNameIndex(remapIndex);
                } else if (cpInfo instanceof PackageCpInfo) {
                    final PackageCpInfo info = (PackageCpInfo) cpInfo;
                    final int pnIdx = info.getNameIndex();
                    final CpInfo pnInfo = getCpEntry(pnIdx);
                    if (pnInfo instanceof Utf8CpInfo) {
                        final String oldName = ((Utf8CpInfo) pnInfo).getString();
                        final String newName = nm.mapPackage(oldName);
                        info.setNameIndex(constantPool.remapUtf8To(newName, pnIdx));
                    }
                }
            }
        }
    }

    private BootstrapMethodsAttrInfo getBootstrapMethodAttribute() {
      for (int i = 0; i < attributes.length; i++) {
        AttrInfo attribute = attributes[i];
        if (attribute instanceof BootstrapMethodsAttrInfo) {
          return (BootstrapMethodsAttrInfo) attribute;
        }
      }
      throw new RuntimeException("No BootstrapMethod attribute in class file");
    }

    private int getType(BootstrapMethod method) {
      final String sig = getBootstrapMethodSignature(method);
      if ("java/lang/invoke/StringConcatFactory#makeConcat(...)".equals(sig)) {
        return BM_TYPE_SCF;
      } else if ("java/lang/invoke/StringConcatFactory#makeConcatWithConstants(...)".equals(sig)) {
        return BM_TYPE_SCF;
      } else if ("java/lang/invoke/LambdaMetafactory#metafactory(...)".equals(sig)) {
        return BM_TYPE_LMF;
      } else if ("java/lang/invoke/LambdaMetafactory#altMetafactory(...)".equals(sig)) {
        return BM_TYPE_LMF;
      } else if ("java/lang/runtime/ObjectMethods#bootstrap(...)".equals(sig)) {
        return BM_TYPE_OM;
      } else {
        return BM_TYPE_UNKNOWN;
      }
    }

    private BootstrapMethod getBootstrapMethod(InvokeDynamicCpInfo info) {
      BootstrapMethodsAttrInfo bmInfo = getBootstrapMethodAttribute();
      return bmInfo.getBootstrapMethods()[info.getBootstrapMethodAttrIndex()];
    }

    private String getBootstrapMethodSignature(BootstrapMethod method) {
      final int mhIdx = method.getBootstrapMethodRef();
      final MethodHandleCpInfo mhInfo = (MethodHandleCpInfo) getCpEntry(mhIdx);
      final RefCpInfo mrInfo = (RefCpInfo) getCpEntry(mhInfo.getReferenceIndex());
      final ClassCpInfo cpInfo = (ClassCpInfo) getCpEntry(mrInfo.getClassIndex());
      final String className = getUtf8(cpInfo.getNameIndex());
      final NameAndTypeCpInfo ntInfo = (NameAndTypeCpInfo) getCpEntry(mrInfo.getNameAndTypeIndex());
      final String memberName = getUtf8(ntInfo.getNameIndex());
      return className + '#' + memberName + getReferenceKindSuffix(mhInfo.getReferenceKind());
    }

    private static String getReferenceKindSuffix(int referenceKind) {
      switch (referenceKind) {
        case REF_getField:
        case REF_getStatic:
        case REF_putField:
        case REF_putStatic:
          return "";

        case REF_invokeVirtual:
        case REF_invokeStatic:
        case REF_invokeSpecial:
        case REF_newInvokeSpecial:
        case REF_invokeInterface:
          return "(...)";

        default:
          throw new IllegalArgumentException("Invalid reference kind: " + referenceKind);
      }
    }

    private void remapAnnotationDefault(AnnotationDefaultAttrInfo annotationDefault, NameMapper nm){
      remapElementValue(annotationDefault.elementValue, nm);
    }
    
    private void remapAnnotations(RuntimeVisibleAnnotationsAttrInfo annotation, NameMapper nm){
      final AnnotationInfo[] annotations = annotation.getAnnotations();
      if (annotations != null){
        for (int i = 0; i < annotations.length; i++){
          remapAnnotation(annotations[i], nm);
        }
      }
    }
    
    private void remapAnnotations(RuntimeVisibleParameterAnnotationsAttrInfo annotation, NameMapper nm){
      final ParameterAnnotationInfo[] annotations = annotation.getParameterAnnotations();
      if (annotations != null){
        for (int i = 0; i < annotations.length; i++){
          final ParameterAnnotationInfo info = annotations[i];
          final AnnotationInfo[] a = info.getAnnotations();
          if (a != null) {
            for (int j = 0; j < a.length; j++){
              remapAnnotation(a[j], nm);
            }
          }
        }
      }
    }
    
    private void remapAnnotation(AnnotationInfo annotation, NameMapper nm){
      CpInfo info = getCpEntry(annotation.u2typeIndex);
      if (info instanceof Utf8CpInfo){
        Utf8CpInfo utf = (Utf8CpInfo) info;
        String s = utf.getString();
        if (s.length() > 2 && s.charAt(0) == 'L' && s.charAt(s.length() - 1) == ';'){
          String fqn = s.substring(1, s.length() - 1);
          String newFqn = nm.mapClass(fqn);
          if (!fqn.equals(newFqn)){
            annotation.u2typeIndex = constantPool.remapUtf8To('L' + newFqn + ';', annotation.u2typeIndex);
          }
          final ElementValuePairInfo[] evp = annotation.getElementValuePairs();
          if (evp != null){
            for (int i = 0; i < evp.length; i++){
              final ElementValuePairInfo elementValuePair = evp[i];
              utf = (Utf8CpInfo) getCpEntry(elementValuePair.u2ElementNameIndex);
              String remapName = nm.mapAnnotationField(fqn, utf.getString());
              if (!remapName.equals(utf.getString())){
                elementValuePair.u2ElementNameIndex = constantPool.remapUtf8To(remapName, elementValuePair.u2ElementNameIndex);
              }
              final ElementValueInfo elementValue = elementValuePair.elementValue;
              remapElementValue(elementValue, nm);
            }
          }
        }
      }
    }
    
    private void remapElementValue(ElementValueInfo elementValue, NameMapper nm){
      switch (elementValue.u1Tag)
      {
        case 'B':
        case 'C':
        case 'D':
        case 'F':
        case 'I':
        case 'J':
        case 'S':
        case 'Z':
        case 's':
          // do nothing, this is a constant
          break;
        case 'e':
        // remap the type...
        {
          Utf8CpInfo utf = (Utf8CpInfo) getCpEntry(elementValue.u2typeNameIndex);
          String name = utf.getString();
          String remapName = nm.mapDescriptor(name);
          elementValue.u2typeNameIndex = constantPool.remapUtf8To(remapName, elementValue.u2typeNameIndex);
        }
        // leave the constant value in u2constNameIndex
        break;
        case 'c':
        {
          Utf8CpInfo utf = (Utf8CpInfo) getCpEntry(elementValue.u2cpIndex);
          String name = utf.getString();
          String remapName = nm.mapDescriptor(name);
          elementValue.u2cpIndex = constantPool.remapUtf8To(remapName, elementValue.u2cpIndex);
        }
        break;
        case '@':
          remapAnnotation(elementValue.nestedAnnotation, nm);
          break;
        case '[':
          for (int j = 0; j < elementValue.arrayValues.length; j++)
          {
            final ElementValueInfo evi = elementValue.arrayValues[j];
            remapElementValue(evi, nm);
          }
          break;
        default:
          throw new RuntimeException("Unknown type tag in annotation!");
      }
    }
    
    private void remapSignature(NameMapper nm, SignatureAttrInfo signature){
      CpInfo cpInfo = getCpEntry(signature.getSignatureIndex());
      if (cpInfo instanceof Utf8CpInfo){
        Utf8CpInfo utf = (Utf8CpInfo) cpInfo;
        String sig = utf.getString();
        String remapSignature = nm.mapSignature(sig);
        if (!sig.equals(remapSignature)){
          int remapIndex = constantPool.remapUtf8To(remapSignature, signature.getSignatureIndex());
          signature.setSignatureIndex(remapIndex);
        }
      }
    }
    
    private int remapNT(Utf8CpInfo refUtf, String remapRef, Utf8CpInfo descUtf, String remapDesc, NameAndTypeCpInfo nameTypeInfo, int nameAndTypeIndex){
      // If a remap is required, make a new N&T (increment ref count on 'name' and
      // 'descriptor', decrement original N&T's ref count, set new N&T ref count to 1),
      // remap new N&T's utf's
      if (!remapRef.equals(refUtf.getString()) || !remapDesc.equals(descUtf.getString()))
      {
        // Get the new N&T guy
        NameAndTypeCpInfo newNameTypeInfo;
        if (nameTypeInfo.getRefCount() == 1)
        {
          newNameTypeInfo = nameTypeInfo;
        }
        else
        {
          // Create the new N&T info
          newNameTypeInfo = (NameAndTypeCpInfo)nameTypeInfo.clone();
          
          // Adjust its reference counts of its utf's
          ((CpInfo)getCpEntry(newNameTypeInfo.getNameIndex())).incRefCount();
          ((CpInfo)getCpEntry(newNameTypeInfo.getDescriptorIndex())).incRefCount();
          
          // Append it to the Constant Pool, and
          // point the RefCpInfo entry to the new N&T data
          nameAndTypeIndex = constantPool.addEntry(newNameTypeInfo);
          
          // Adjust reference counts from RefCpInfo
          newNameTypeInfo.incRefCount();
          nameTypeInfo.decRefCount();
        }
        
        // Remap the 'name' and 'descriptor' utf's in N&T
        newNameTypeInfo.setNameIndex(constantPool.remapUtf8To(remapRef, newNameTypeInfo.getNameIndex()));
        newNameTypeInfo.setDescriptorIndex(constantPool.remapUtf8To(remapDesc, newNameTypeInfo.getDescriptorIndex()));
      }
      return nameAndTypeIndex;
    }

    /**
     * goes through the constantpool, identifies classnamestrings and replaces
     * them appropriately if necessary
     */
    private void replaceConstantPoolStrings(ClassTree ct){
      for (Enumeration enumeration = constantPool.elements(); enumeration.hasMoreElements();){
        CpInfo cpi = (CpInfo) enumeration.nextElement();
        if (cpi instanceof Utf8CpInfo){
          Utf8CpInfo ui = (Utf8CpInfo) cpi;
          String s = ui.getString();
          boolean jikes = false;
          if (s.length()>5 && s.startsWith("[L") && s.endsWith(";")){
            s = s.substring(2, s.length()-1);
            jikes = true;
          }
          if (s.length()>2 && Character.isJavaIdentifierPart(s.charAt(s.length()-1)) &&
            s.indexOf(' ')<0 && s.indexOf('.')>0){
            Cl cl = ct.findClassForName(s);
            if (cl != null){
              if (!cl.getFullInName().equals(cl.getFullOutName())){
                if (jikes){
                  ui.setString("[L"+cl.getFullOutName().replace('/','.')+";");
                } else {
                  ui.setString(cl.getFullOutName().replace('/','.'));
                }
              }
            }
          }
        }
      }
    }

    /**
     * Export the representation to a DataOutput stream.
     *
     * @param dout the dout
     * @throws IOException the io exception
     */
    public void write(DataOutput dout) throws java.io.IOException
    {
        if (dout == null) throw new NullPointerException("No output stream was provided.");
        dout.writeInt(u4magic);
        dout.writeShort(u2minorVersion);
        dout.writeShort(u2majorVersion);
        dout.writeShort(constantPool.length() + (writeIdString ? 1 : 0));
        for (Enumeration enumeration = constantPool.elements(); enumeration.hasMoreElements(); )
        {
            CpInfo cpInfo = (CpInfo)enumeration.nextElement();
            if (cpInfo != null)
            {
                cpInfo.write(dout);
            }
        }
        if (writeIdString) {
            cpIdString.write(dout);
        }
        dout.writeShort(u2accessFlags);
        dout.writeShort(u2thisClass);
        dout.writeShort(u2superClass);
        dout.writeShort(u2interfacesCount);
        for (int i = 0; i < u2interfacesCount; i++)
        {
            dout.writeShort(u2interfaces[i]);
        }
        dout.writeShort(u2fieldsCount);
        for (int i = 0; i < u2fieldsCount; i++)
        {
            fields[i].write(dout);
        }
        dout.writeShort(u2methodsCount);
        for (int i = 0; i < u2methodsCount; i++)
        {
            methods[i].write(dout);
        }
        dout.writeShort(u2attributesCount);
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i].write(dout);
        }
    }

    /**
     * Dump the content of the class file to the specified file (used for debugging).
     *
     * @param pw the pw
     */
    public void dump(PrintWriter pw)
    {
        pw.println("_____________________________________________________________________");
        pw.println("CLASS: " + getName());
        pw.println("Magic: " + Integer.toHexString(u4magic));
        pw.println("Minor version: " + Integer.toHexString(u2minorVersion));
        pw.println("Major version: " + Integer.toHexString(u2majorVersion));
        pw.println();
        pw.println("CP length: " + Integer.toHexString(constantPool.length()));
        for (int i = 0; i < constantPool.length(); i++)
        {
            CpInfo cpInfo = (CpInfo)constantPool.getCpEntry(i);
            if (cpInfo != null)
            {
                cpInfo.dump(pw, this, i);
            }
        }
        pw.println("Access: " + Integer.toHexString(u2accessFlags));
        pw.println("This class: " + getName());
        pw.println("Superclass: " + getSuper());
        pw.println("Interfaces count: " + Integer.toHexString(u2interfacesCount));
        for (int i = 0; i < u2interfacesCount; i++)
        {
            CpInfo info = getCpEntry(u2interfaces[i]);
            if (info == null)
            {
                pw.println("  Interface " + Integer.toHexString(i) + ": (null)");
            }
            else
            {
                pw.println("  Interface " + Integer.toHexString(i) + ": " + ((Utf8CpInfo)getCpEntry(((ClassCpInfo)info).getNameIndex())).getString());
            }
        }
        pw.println("Fields count: " + Integer.toHexString(u2fieldsCount));
        for (int i = 0; i < u2fieldsCount; i++)
        {
            ClassItemInfo info = fields[i];
            if (info == null)
            {
                pw.println("  Field " + Integer.toHexString(i) + ": (null)");
            }
            else
            {
                pw.println("  Field " + Integer.toHexString(i) + ": " + ((Utf8CpInfo)getCpEntry(info.getNameIndex())).getString() + " " + ((Utf8CpInfo)getCpEntry(info.getDescriptorIndex())).getString());
            }
            pw.println("    Attrs count: " + Integer.toHexString(info.u2attributesCount));
            for (int j = 0; j < info.u2attributesCount; j++)
            {
                pw.println(info.attributes[j]);
            }
        }
        pw.println("Methods count: " + Integer.toHexString(u2methodsCount));
        for (int i = 0; i < u2methodsCount; i++)
        {
            ClassItemInfo info = methods[i];
            if (info == null)
            {
                pw.println("  Method " + Integer.toHexString(i) + ": (null)");
            }
            else
            {
                pw.println("  Method " + Integer.toHexString(i) + ": " + ((Utf8CpInfo)getCpEntry(info.getNameIndex())).getString() + " " + ((Utf8CpInfo)getCpEntry(info.getDescriptorIndex())).getString() + " " + Integer.toHexString(info.getAccessFlags()));
            }
            pw.println("    Attrs count: " + Integer.toHexString(info.u2attributesCount));
            for (int j = 0; j < info.u2attributesCount; j++)
            {
                if (info.attributes[j] instanceof CodeAttrInfo){
                  pw.println(info.attributes[j]);
                  CodeAttrInfo cai = (CodeAttrInfo) info.attributes[j];
                  for (int k = 0; k < cai.u2attributesCount; k++){
                    pw.println(cai.attributes[k]);
                  }
                } else {
                  pw.println(info.attributes[j]);
                }
            }
        }
        pw.println("Attrs count: " + Integer.toHexString(u2attributesCount));
        for (int i = 0; i < u2attributesCount; i++)
        {
            pw.println(attributes[i]);
        }
    }

  /**
   * Get attributes attr info [ ].
   *
   * @return the attr info [ ]
   */
  public AttrInfo[] getAttributes() {
    return attributes;
  }

  /**
   * Gets u 2 attributes count.
   *
   * @return the u 2 attributes count
   */
  public int getU2attributesCount() {
    return u2attributesCount;
  }

  /**
   * Returns the module name if this class file represents a "module-info"
   * class and the empty string otherwise.
   *
   * @return the string
   */
  public String findModuleName() {
    for (int i = 0; i < attributes.length; ++i) {
      if (attributes[i] instanceof ModuleAttrInfo) {
        final int mIdx = ((ModuleAttrInfo) attributes[i]).getModuleNameIndex();
        final CpInfo mInfo = constantPool.getCpEntry(mIdx);
        if (mInfo instanceof ModuleCpInfo) {
          final int nIdx = ((ModuleCpInfo) mInfo).getNameIndex();
          final CpInfo nInfo = constantPool.getCpEntry(nIdx);
          if (nInfo instanceof Utf8CpInfo) {
            return ((Utf8CpInfo) nInfo).getString();
          }
        }
      }
    }
    return "";
  }
}
