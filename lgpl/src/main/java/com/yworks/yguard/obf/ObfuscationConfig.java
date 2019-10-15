package com.yworks.yguard.obf;

/**
 * Encapsulation of annotation configuration data
 */
public class ObfuscationConfig {

  public static String annotationClassName = "com/yworks/util/annotation/Obfuscation";

  public final boolean applyToMembers;
  public final boolean exclude;

  public static final ObfuscationConfig DEFAULT = new ObfuscationConfig(false, false);

  public ObfuscationConfig(boolean exclude, boolean applyToMembers) {
    this.applyToMembers = applyToMembers;
    this.exclude = exclude;
  }
}
