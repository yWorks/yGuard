/*
 * CollectionFilter.java
 *
 * Created on October 24, 2002, 5:01 PM
 */

package com.yworks.util;

/**
 *
 * @author  Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public class CollectionFilter implements Filter
{
  
  /** Holds value of property collection. */
  private java.util.Collection collection;
  
  /** Creates a new instance of CollectionFilter */
  public CollectionFilter(java.util.Collection col)
  {
    this.collection = col;
  }
  
  public boolean accepts(Object o)
  {
    return collection != null && collection.contains(o);
  }
  
  /** Getter for property collection.
   * @return Value of property collection.
   */
  public java.util.Collection getCollection()
  {
    return this.collection;
  }
  
  /** Setter for property collection.
   * @param collection New value of property collection.
   */
  public void setCollection(java.util.Collection collection)
  {
    this.collection = collection;
  }
  
}
