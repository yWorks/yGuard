package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The type Type annotation info.
 *
 * @author mfk
 */
public class TypeAnnotationInfo {

  private int u1TargetType;

  private int u1TypeParameterIndex;
  private int u2SupertypeIndex;
  private int u1TypeBoundIndex;
  private int u1FormalParameterIndex;
  private int u2ThrowsTypeIndex;
  private int u2ExceptionTableIndex;
  private int u2Offset;
  private int u1TypeArgumentIndex;

  private LocalvarTarget localvarTarget;

  private TypePath targetPath;

  private int u2TypeIndex;
  private int u2NumElementValuePairs;
  private ElementValuePairInfo[] elementValuePairs;

  /**
   * Create type annotation info.
   *
   * @param din the din
   * @return the type annotation info
   * @throws IOException the io exception
   */
  public static TypeAnnotationInfo create(DataInput din) throws java.io.IOException {
    TypeAnnotationInfo an = new TypeAnnotationInfo();
    an.read(din);
    return an;
  }
  
  private void read(DataInput din) throws java.io.IOException {

    u1TargetType = din.readUnsignedByte();

    switch (u1TargetType) {
      //
      // https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20-400
      //
      case 0x00:
        // type parameter declaration of generic class or interface => type_parameter_target
      case 0x01:
        // type parameter declaration of generic method or constructor => type_parameter_target
        u1TypeParameterIndex = din.readUnsignedByte();
        break;
      case 0x10:
        // type in extends or implements clause of class declaration (including the direct superclass 
        // or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration
        // => supertype_target
        u2SupertypeIndex = din.readUnsignedShort();
        break;
      case 0x11:
        // type in bound of type parameter declaration of generic class or interface
        // => type_parameter_bound_target 
      case 0x12:
        // type in bound of type parameter declaration of generic method or constructor
        // => type_parameter_bound_target 
        u1TypeParameterIndex = din.readUnsignedByte();
        u1TypeBoundIndex = din.readUnsignedByte();
        break;
      case 0x13:
        // type in field declaration => empty_target
      case 0x14:  
        // return type of method, or type of newly constructed object => empty_target
      case 0x15:  
        // receiver type of method or constructor => empty_target
        break;
      case 0x16:
        // type in formal parameter declaration of method, constructor, or lambda expression => formal_parameter_target
        u1FormalParameterIndex = din.readUnsignedByte();
        break;
      case 0x17:
        // type in throws clause of method or constructor => throws_target
        u2ThrowsTypeIndex = din.readUnsignedShort();
        break;
      //  
      // https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.20-410
      //
      case 0x40:
        // type in local variable declaration => localvar_target
      case 0x41:
        // type in resource variable declaration => localvar_target
        localvarTarget = new LocalvarTarget();
        localvarTarget.readInfo(din);
        break;
      case 0x42:
        // type in exception parameter declaration	
        u2ExceptionTableIndex = din.readUnsignedShort();
        break;
      case 0x43:
        // type in instanceof expression => offset_target
      case 0x44:
        // type in new expression => offset_target
      case 0x45:
        // type in method reference expression using ::new => offset_target
      case 0x46:
        // type in method reference expression using ::Identifier => offset_target
        u2Offset = din.readUnsignedShort();
        break;
      case 0x47:
        // type in cast expression => type_argument_target
      case 0x48:
        // type argument for generic constructor in new expression or explicit constructor invocation statement => type_argument_target
      case 0x49:
        // type argument for generic method in method invocation expression => type_argument_target
      case 0x4A:
        // type argument for generic constructor in method reference expression using ::new => type_argument_target
      case 0x4B:
        // type argument for generic method in method reference expression using ::Identifier => type_argument_target
        u2Offset = din.readUnsignedShort();
        u1TypeArgumentIndex = din.readUnsignedByte();
        break;
      default:
        throw new IllegalArgumentException("Unkown annotation target type: 0x"+Integer.toHexString(u1TargetType)+"");
    }

    targetPath = new TypePath();
    targetPath.readInfo(din);

    u2TypeIndex = din.readUnsignedShort();
    u2NumElementValuePairs = din.readUnsignedShort();

    elementValuePairs = new ElementValuePairInfo[u2NumElementValuePairs];
    for (int i = 0; i < u2NumElementValuePairs; i++) {
      elementValuePairs[i] = ElementValuePairInfo.create(din);
    }
  }

  /**
   * Write.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write(DataOutput dout) throws java.io.IOException {

    dout.writeByte(u1TargetType);
    
    switch (u1TargetType) {
      case 0x00:
      case 0x01:
        dout.writeByte(u1TypeParameterIndex);
        break;
      case 0x10:
        dout.writeShort(u2SupertypeIndex);
        break;
      case 0x11:
      case 0x12:
        dout.writeByte(u1TypeParameterIndex);
        dout.writeByte(u1TypeBoundIndex);
        break;
      case 0x13:
      case 0x14:  
      case 0x15:
        break;
      case 0x16:
        dout.writeByte(u1FormalParameterIndex);
        break;
      case 0x17:
        dout.writeShort(u2ThrowsTypeIndex);
        break;
      case 0x40:
      case 0x41:
        localvarTarget.writeInfo(dout);
        break;
      case 0x42:
        dout.writeShort(u2ExceptionTableIndex);
        break;
      case 0x43:
      case 0x44:
      case 0x45:
      case 0x46:
        dout.writeShort(u2Offset);
        break;
      case 0x47:
      case 0x48:
      case 0x49:
      case 0x4A:
      case 0x4B:
        dout.writeShort(u2Offset);
        dout.writeByte(u1TypeArgumentIndex);
        break;
      default:
        throw new IllegalArgumentException("Unkown annotation target type: 0x"+Integer.toHexString(u1TargetType)+"");
    }

    targetPath.writeInfo(dout);
    
    dout.writeShort(u2TypeIndex);
    dout.writeShort(u2NumElementValuePairs);
    for (int i = 0; i < u2NumElementValuePairs; i++) {
      elementValuePairs[i].write(dout);
    }
  }

  /**
   * Mark utf 8 refs in info.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    pool.getCpEntry(u2TypeIndex).incRefCount();
    for (int i = 0; i < u2NumElementValuePairs; i++) {
      elementValuePairs[i].markUtf8RefsInInfo(pool);
    }
  }

  /**
   * The type Localvar target.
   */
  static class LocalvarTarget {

    private int u2TableLength;
    private LocalVarTargetVariableInfo[] table;

    /**
     * Read info.
     *
     * @param din the din
     * @throws IOException the io exception
     */
    protected void readInfo(DataInput din) throws IOException {
      u2TableLength = din.readUnsignedShort();
      table = new LocalVarTargetVariableInfo[u2TableLength];
      for (int i = 0; i < u2TableLength; i++) {
        table[i] = LocalVarTargetVariableInfo.create(din);
      }
    }

    /**
     * Write info.
     *
     * @param dout the dout
     * @throws IOException the io exception
     */
    public void writeInfo(DataOutput dout) throws java.io.IOException {
      dout.writeShort(u2TableLength);
      for (int i = 0; i < u2TableLength; i++) {
        table[i].write(dout);
      }
    }

    /**
     * The type Local var target variable info.
     */
    static class LocalVarTargetVariableInfo {
      private int u2startPc;
      private int u2length;
      private int u2index;

      /**
       * Create local var target variable info.
       *
       * @param din the din
       * @return the local var target variable info
       * @throws IOException the io exception
       */
      public static LocalVarTargetVariableInfo create(DataInput din) throws java.io.IOException {
        LocalVarTargetVariableInfo lvi = new LocalVarTargetVariableInfo();
        lvi.read(din);
        return lvi;
      }

      private void read(DataInput din) throws java.io.IOException {
        u2startPc = din.readUnsignedShort();
        u2length = din.readUnsignedShort();
        u2index = din.readUnsignedShort();
      }

      /**
       * Write.
       *
       * @param dout the dout
       * @throws IOException the io exception
       */
      public void write(DataOutput dout) throws java.io.IOException {
        dout.writeShort(u2startPc);
        dout.writeShort(u2length);
        dout.writeShort(u2index);
      }

    }
  }


  /**
   * The type Type path.
   */
  static class TypePath {
    private int u1PathLength;
    private PathEntry[] entries;

    /**
     * Read info.
     *
     * @param din the din
     * @throws IOException the io exception
     */
    protected void readInfo(DataInput din) throws IOException {
      u1PathLength = din.readUnsignedByte();
      entries = new PathEntry[u1PathLength];
      for (int i = 0; i < u1PathLength; i++) {
        entries[i] = PathEntry.create(din);
      }
    }

    /**
     * Write info.
     *
     * @param dout the dout
     * @throws IOException the io exception
     */
    public void writeInfo(DataOutput dout) throws java.io.IOException {
      dout.writeByte(u1PathLength);
      for (int i = 0; i < entries.length; i++) {
        entries[i].write(dout);
      }
    }

    /**
     * The type Path entry.
     */
    static class PathEntry {
      private int u1PathKind;
      private int u1TypeArgumentIndex;

      /**
       * Create path entry.
       *
       * @param din the din
       * @return the path entry
       * @throws IOException the io exception
       */
      public static PathEntry create(DataInput din) throws java.io.IOException {
        PathEntry lvi = new PathEntry();
        lvi.read(din);
        return lvi;
      }

      private void read(DataInput din) throws java.io.IOException {
        u1PathKind = din.readUnsignedByte();
        u1TypeArgumentIndex = din.readUnsignedByte();
      }

      /**
       * Write.
       *
       * @param dout the dout
       * @throws IOException the io exception
       */
      public void write(DataOutput dout) throws java.io.IOException {
        dout.writeByte(u1PathKind);
        dout.writeByte(u1TypeArgumentIndex);
      }
    }
  }
}
