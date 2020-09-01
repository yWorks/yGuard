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
package com.yworks.yguard.obf.classfile;

import java.io.*;
import com.yworks.yguard.ParseException;
import com.yworks.yguard.Conversion;

/**
 * Representation of an attribute. Specific attributes have their representations
 * sub-classed from this.
 *
 * @author      Mark Welsh
 */
public class AttrInfo implements ClassConstants
{
    // Constants -------------------------------------------------------------
    public static final int CONSTANT_FIELD_SIZE = 6;


    // Fields ----------------------------------------------------------------
    private int u2attrNameIndex;
    protected int u4attrLength;
    private byte info[];

    protected ClassFile owner;


    // Class Methods ---------------------------------------------------------
    /**
     * Create a new AttrInfo from the data passed.
     *
     * @throws IOException if class file is corrupt or incomplete
     */
    public static AttrInfo create(DataInput din, ClassFile cf) throws java.io.IOException
    {
        if (din == null) throw new NullPointerException("No input stream was provided.");

        // Instantiate based on attribute name
        AttrInfo ai = null;
        int attrNameIndex = din.readUnsignedShort();
        int attrLength = din.readInt();
//      byte[] buffer = new byte[attrLength];
//      din.readFully(buffer);

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
                ai = new AttrInfo( cf, attrNameIndex, attrLength);
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

//      ai.readInfo(new DataInputStream(new ByteArrayInputStream(buffer)));
        ai.readInfo(din);
        return ai;
    }


    // Instance Methods ------------------------------------------------------
    protected AttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        owner = cf;
        u2attrNameIndex = attrNameIndex;
        u4attrLength = attrLength;
    }

    protected int getAttrNameIndex(){
      return u2attrNameIndex;
    }

    /** Return the length in bytes of the attribute; over-ride this in sub-classes. */
    protected int getAttrInfoLength()
    {
        return u4attrLength;
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName()
    {
        return ATTR_Unknown;
    }

    /**
     * Trim attributes from the classfile except those in the String[].
     */
    protected void trimAttrsExcept(String[] keepAttrs)  {}

    /** Check for Utf8 references to constant pool and mark them. */
    protected void markUtf8Refs(ConstantPool pool)
    {
        pool.incRefCount(u2attrNameIndex);
        markUtf8RefsInInfo(pool);
    }

    /**
     * Check for Utf8 references in the 'info' data to the constant pool and
     * mark them; over-ride this in sub-classes.
     */
    protected void markUtf8RefsInInfo(ConstantPool pool)  {}

    /** Read the data following the header; over-ride this in sub-classes. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        info = new byte[u4attrLength];
        din.readFully(info);
    }

    /** Export the representation to a DataOutput stream. */
    public final void write(DataOutput dout) throws java.io.IOException
    {
        if (dout == null) throw new IOException("No output stream was provided.");
        dout.writeShort(u2attrNameIndex);
        dout.writeInt(getAttrInfoLength());
        writeInfo(dout);
    }

    /** Export data following the header to a DataOutput stream; over-ride this in sub-classes. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.write(info);
    }

    public String toString(){
      return getAttrName() + "[" + getAttrInfoLength() + "]";
    }
}
