package com.yworks.yshrink.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Annotation usage.
 *
 * @author schroede
 */
public class AnnotationUsage {
  private String descriptor;

  private List<String> fieldUsages = new ArrayList<String>();

    /**
     * Instantiates a new Annotation usage.
     *
     * @param descriptor the descriptor
     */
    public AnnotationUsage(String descriptor) {
    this.descriptor = descriptor;
  }

    /**
     * Add field usage.
     *
     * @param name the name
     */
    public void addFieldUsage(String name) {
    if (name != null) {
      fieldUsages.add(name);
    }
  }

    /**
     * Gets descriptor.
     *
     * @return the descriptor
     */
    public String getDescriptor() {
    return descriptor;
  }

    /**
     * Gets field usages.
     *
     * @return the field usages
     */
    public List<String> getFieldUsages() {
    return fieldUsages;
  }
}
