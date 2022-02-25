/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

import com.yworks.yguard.ParseException;
import com.yworks.yguard.Conversion;
import com.yworks.yguard.obf.Tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of an attribute. Specific attributes have their representations
 * sub-classed from this.
 *
 * @author Mark Welsh
 */
public class AttrInfo implements ClassConstants
{
  // Constants -------------------------------------------------------------
  /**
   * The constant CONSTANT_FIELD_SIZE.
   */
  public static final int CONSTANT_FIELD_SIZE = 6;


  // Fields ----------------------------------------------------------------
  private int u2attrNameIndex;
  /**
   * The length of the attribute in bytes.
   */
  protected int u4attrLength;
  private byte info[];

  /**
   * The Owner.
   */
  protected ClassFile owner;


  // Class Methods ---------------------------------------------------------

  /**
   * Create a new AttrInfo from the data passed.
   *
   * @param din the din
   * @param cf  the cf
   * @return the attr info
   * @throws IOException if class file is corrupt or incomplete
   */
  public static AttrInfo create(DataInput din, ClassFile cf) throws java.io.IOException
  {
     if (din == null) throw new NullPointerException("No input stream was provided.");

     // Instantiate based on attribute name
     AttrInfo ai = null;
     int attrNameIndex = din.readUnsignedShort();
     int attrLength = din.readInt();

     CpInfo cpInfo = cf.getCpEntry(attrNameIndex);
     if (cpInfo instanceof Utf8CpInfo)
     {
       String attrName = ((Utf8CpInfo)cpInfo).getString();
       if (attrName.equals(ATTR_Code))
       {
         ai = new CodeAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_ConstantValue))
       {
         ai = new ConstantValueAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Exceptions))
       {
         ai = new ExceptionsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_StackMapTable))
       {
         ai = new StackMapTableAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_LineNumberTable))
       {
         ai = new LineNumberTableAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_SourceFile))
       {
         ai = new SourceFileAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_LocalVariableTable))
       {
         ai = new LocalVariableTableAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_InnerClasses))
       {
         ai = new InnerClassesAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Synthetic))
       {
         ai = new SyntheticAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Deprecated))
       {
         ai = new DeprecatedAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Signature)){
         ai = new SignatureAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_LocalVariableTypeTable)){
         ai = new LocalVariableTypeTableAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_EnclosingMethod)){
         ai = new EnclosingMethodAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeVisibleAnnotations)){
         ai = new RuntimeVisibleAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeVisibleTypeAnnotations)){
         ai = new RuntimeVisibleTypeAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeInvisibleAnnotations)){
         ai = new RuntimeInvisibleAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeInvisibleTypeAnnotations)){
         ai = new RuntimeInvisibleTypeAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeVisibleParameterAnnotations)){
         ai = new RuntimeVisibleParameterAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_RuntimeInvisibleParameterAnnotations)){
         ai = new RuntimeInvisibleParameterAnnotationsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_AnnotationDefault)){
         ai = new AnnotationDefaultAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_BootstrapMethods)){
         ai = new BootstrapMethodsAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Bridge) && (attrLength==0)){
         ai = new AttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Enum) && (attrLength==0)){
         ai = new AttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (attrName.equals(ATTR_Varargs) && (attrLength==0)){
         ai = new AttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_MethodParameters.equals(attrName)) {
         ai = new MethodParametersAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_Module.equals(attrName)) {
         ai = new ModuleAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_ModulePackages.equals(attrName)) {
         ai = new ModulePackagesAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_ModuleMainClass.equals(attrName)) {
         ai = new ModuleMainClassAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_NestHost.equals(attrName)) {
         ai = new NestHostAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_NestMembers.equals(attrName)) {
         ai = new NestMembersAttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_SourceDebugExtension.equals(attrName)) {
         ai = new AttrInfo(cf, attrNameIndex, attrLength);
       }
       else if (ATTR_Record.equals(attrName)) {
         ai = new RecordAttrInfo(cf, attrNameIndex, attrLength);
       }
       else {
         if ( attrLength > 0 ) {
           Logger.getInstance().warning( "Unrecognized attribute '" + attrName + "' in " + Conversion.toJavaClass( cf.getName() ) );
         }
         ai = new AttrInfo( cf, attrNameIndex, attrLength );
       }
     }
     else
     {
       throw new ParseException("Inconsistent reference to Constant Pool.");
     }

      ai.readInfo(din);
      return ai;
    }


  /**
   * Instantiates a new Attr info.
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
  protected AttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    owner = cf;
    u2attrNameIndex = attrNameIndex;
    u4attrLength = attrLength;
  }

  // Instance Methods ------------------------------------------------------
  /**
   * Get attr name index int.
   *
   * @return the int
   */
  protected int getAttrNameIndex() {
    return u2attrNameIndex;
  }

  /**
   * Return the length of the attribute in bytes; over-ride this in sub-classes.
   *
   * @return the attr info length
   */
  protected int getAttrInfoLength()
  {
    return u4attrLength;
  }

  /**
   * Return the String name of the attribute; over-ride this in sub-classes.
   *
   * @return the attr name
   */
  protected String getAttrName()
  {
    return ATTR_Unknown;
  }

  /**
   * Trim attributes from the classfile except those in the String[].
   *
   * @param keepAttrs the keep attrs
   */
  protected void trimAttrsExcept(String[] keepAttrs)  {}

  /**
   * Check for Utf8 references to constant pool and mark them.
   *
   * @param pool the pool
   */
  protected void markUtf8Refs(ConstantPool pool)
  {
    pool.incRefCount(u2attrNameIndex);
    markUtf8RefsInInfo(pool);
  }

  /**
   * Check for Utf8 references in the 'info' data to the constant pool and
   * mark them; over-ride this in sub-classes.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo(ConstantPool pool)  {}

  /**
   * Read the data following the header; over-ride this in sub-classes.
   *
   * @param din the din
   * @throws IOException the io exception
   */
  protected void readInfo(DataInput din) throws java.io.IOException
  {
    info = new byte[u4attrLength];
    din.readFully(info);
  }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public final void write(DataOutput dout) throws java.io.IOException
  {
    if (dout == null) throw new IOException("No output stream was provided.");
    dout.writeShort(u2attrNameIndex);
    dout.writeInt(getAttrInfoLength());
    writeInfo(dout);
  }

  /**
   * Export data following the header to a DataOutput stream; over-ride this in sub-classes.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void writeInfo(DataOutput dout) throws java.io.IOException
  {
    dout.write(info);
  }

  public String toString() {
    return getAttrName() + "[" + getAttrInfoLength() + "]";
  }


  /**
   * Returns the attributes whose names are contained in the specified array.
   * @param attributes the set of attributes to filter.
   * @param acceptedAttrs the names of attributes to retain.
   * @return the retained attributes.
   */
  static AttrInfo[] filter(AttrInfo[] attributes, String[] acceptedAttrs) {
    if (attributes == null) {
      return null;
    }

    // traverse all attributes, removing all except those on 'keep' list
    int attrsToKeepCount = 0;
    for (int i = 0, n = attributes.length; i < n; ++i) {
      if (Tools.isInArray(attributes[i].getAttrName(), acceptedAttrs)) {
        ++attrsToKeepCount;
        attributes[i].trimAttrsExcept(acceptedAttrs);
      } else {
        attributes[i] = null;
      }
    }

    AttrInfo[] attrsToKeep = new AttrInfo[attrsToKeepCount];
    for (int i = 0, j = 0, n = attributes.length; i < n; ++i) {
      if (attributes[i] != null) {
        attrsToKeep[j++] = attributes[i];
      }
    }
    return attrsToKeep;
  }
}
