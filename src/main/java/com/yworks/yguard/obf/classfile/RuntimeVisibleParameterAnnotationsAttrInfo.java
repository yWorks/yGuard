package com.yworks.yguard.obf.classfile;

/**
 * The type Runtime visible parameter annotations attr info.
 *
 * @author muellese
 */
public class RuntimeVisibleParameterAnnotationsAttrInfo extends AttrInfo
{
  private int u1parameterCount;
  private ParameterAnnotationInfo[] annotations;

  /**
   * Creates a new instance of RuntimeVisibleParameterAnnotationsAttrInfo
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
  public RuntimeVisibleParameterAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Get parameter annotations parameter annotation info [ ].
   *
   * @return the parameter annotation info [ ]
   */
  protected ParameterAnnotationInfo[] getParameterAnnotations(){
    return annotations;
  }

  protected String getAttrName()
  {
    return ClassConstants.ATTR_RuntimeVisibleParameterAnnotations;
  }

  public void writeInfo(java.io.DataOutput dout) throws java.io.IOException
  {
    dout.writeByte(u1parameterCount);
    for (int i = 0; i < u1parameterCount; i++){
      annotations[i].write(dout);
    }
  }
  
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    for (int i = 0; i < u1parameterCount; i++){
      annotations[i].markUtf8RefsInInfo(pool);
    }
  }


  protected void readInfo(java.io.DataInput din) throws java.io.IOException
  {
    u1parameterCount = din.readUnsignedByte();
    annotations = new ParameterAnnotationInfo[u1parameterCount];
    for (int i = 0; i < u1parameterCount; i++){
      annotations[i] = ParameterAnnotationInfo.create(din);
    }
  }
}
