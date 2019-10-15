package com.yworks.yshrink.model;

import java.util.List;
import java.util.ArrayList;

/** @author schroede */
public class AnnotationUsage {
  private String descriptor;

  private List<String> fieldUsages = new ArrayList<String>();

  public AnnotationUsage(String descriptor) {
    this.descriptor = descriptor;
  }

  public void addFieldUsage(String name) {
    if (name != null) {
      fieldUsages.add(name);
    }
  }

  public String getDescriptor() {
    return descriptor;
  }

  public List<String> getFieldUsages() {
    return fieldUsages;
  }
}
