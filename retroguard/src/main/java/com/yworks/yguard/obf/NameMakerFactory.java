/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  muellese
 */
public abstract class NameMakerFactory
{
  
  /** Holds value of property instance. */
  private static NameMakerFactory instance = new DefaultNameMakerFactory();
  
  /** Creates a new instance of NameMakerFactory */
  protected NameMakerFactory()
  {}
  
  /** Getter for property instance.
   * @return Value of property instance.
   *
   */
  public static NameMakerFactory getInstance()
  {
    return instance;
  }
  
  /** Setter for property instance.
   * @param _instance New value of property instance.
   *
   */
  protected void setInstance(NameMakerFactory _instance)
  {
    instance = _instance;
  }
  
  public abstract NameMaker getPackageNameMaker(String[] reservedNames, String packageName);

  public abstract NameMaker getClassNameMaker(String[] reservedNames, String packageName);
  
  public abstract NameMaker getInnerClassNameMaker(String[] reservedNames, String packageName);
  
  public abstract NameMaker getMethodNameMaker(String[] reservedNames, String fqClassName);

  public abstract NameMaker getFieldNameMaker(String[] reservedNames, String fqClassName);
  
  public static class DefaultNameMakerFactory extends NameMakerFactory{
    
    private Map classNameMap = new HashMap();
    private Map fieldNameMap = new HashMap();
    private Map methodNameMap = new HashMap();
    private Map innerClassNameMap = new HashMap();
    private Map packageNameMap = new HashMap();
    
    public NameMaker getClassNameMaker(String[] reservedNames, String fqClassName)
    {
      NameMaker res = (NameMaker) classNameMap.get(fqClassName);
      if (res == null){
        res = createClassNameMaker(reservedNames, fqClassName);
        classNameMap.put(fqClassName, res);
      } 
      return res;
    }
    
    protected NameMaker createClassNameMaker(String[] reservedNames, String fqClassName){
      return new KeywordNameMaker(reservedNames); 
    }
    
    public NameMaker getFieldNameMaker(String[] reservedNames, String fqClassName)
    {
      NameMaker res = (NameMaker) fieldNameMap.get(fqClassName);
      if (res == null){
        res = createFieldNameMaker(reservedNames, fqClassName);
        fieldNameMap.put(fqClassName, res);
      } 
      return res;
    }

    protected NameMaker createFieldNameMaker(String[] reservedNames, String fqClassName){
      return new KeywordNameMaker(reservedNames, false, true); 
    }
    
    public NameMaker getInnerClassNameMaker(String[] reservedNames, String fqInnerClassName)
    {
      NameMaker res = (NameMaker) innerClassNameMap.get(fqInnerClassName);
      if (res == null){
        res = createInnerClassNameMaker(reservedNames, fqInnerClassName);
        innerClassNameMap.put(fqInnerClassName, res);
      } 
      return res;
    }
    
    protected NameMaker createInnerClassNameMaker(final String[] reservedNames, String fqInnerClassName){
      final NameMaker inner = new KeywordNameMaker(null);
      //JBuilder7 incompatability workaround
      return new NameMaker(){
        public String nextName(String sig){
          while(true){
            String name = '_' + inner.nextName(sig);
            if (reservedNames == null || !Tools.isInArray(name, reservedNames)){
              return name;
            }
          }
        }
      };
    }

    public NameMaker getMethodNameMaker(String[] reservedNames, String fqClassName)
    {
      NameMaker res = (NameMaker) methodNameMap.get(fqClassName);
      if (res == null){
        res = createMethodNameMaker(reservedNames, fqClassName);
        methodNameMap.put(fqClassName, res);
      } 
      return res;
    }

    protected NameMaker createMethodNameMaker(String[] reservedNames, String fqClassName){
      return new KeywordNameMaker(reservedNames, false, true); 
    }
    
    public NameMaker getPackageNameMaker(String[] reservedNames, String packageName)
    {
      NameMaker res = (NameMaker) packageNameMap.get(packageName);
      if (res == null){
        res = createPackageNameMaker(reservedNames, packageName);
        packageNameMap.put(packageName, res);
      } 
      return res;
    }
    
    protected NameMaker createPackageNameMaker(String[] reservedNames, String packageName){
      return new KeywordNameMaker(reservedNames); 
    }
    
    public String toString(){
      return "default";
    }
  }
}
