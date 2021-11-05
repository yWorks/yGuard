package com.yworks.yguard.obf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The interface Resource handler.
 *
 * @author wiese
 */
public interface ResourceHandler
{
  /**
   * Filter name boolean.
   *
   * @param inputName  the input name
   * @param outputName the output name
   * @return the boolean
   */
  public boolean filterName(String inputName, StringBuffer outputName);

  /**
   * Filter content boolean.
   *
   * @param in           the in
   * @param out          the out
   * @param resourceName the resource name
   * @return the boolean
   * @throws IOException the io exception
   */
  public boolean filterContent(InputStream in, OutputStream out, String resourceName) throws IOException;

  /**
   * Filter string string.
   *
   * @param in           the in
   * @param resourceName the resource name
   * @return the string
   * @throws IOException the io exception
   */
  public String filterString(String in, String resourceName) throws IOException;
}
