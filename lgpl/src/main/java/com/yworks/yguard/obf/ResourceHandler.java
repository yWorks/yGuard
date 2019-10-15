/*
 * ResourceHandler.java
 *
 * Created on June 26, 2003, 3:51 PM
 */

package com.yworks.yguard.obf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author  wiese
 */
public interface ResourceHandler
{
  public boolean filterName(String inputName, StringBuffer outputName);
  public boolean filterContent(InputStream in, OutputStream out, String resourceName) throws IOException;
  public String filterString(String in, String resourceName) throws IOException;
}
