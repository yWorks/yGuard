package com.yworks.yguard.obf;

/**
 * Encapsulation of annotation configuration data
 */
public class ObfuscationConfig {

    /**
     * The constant annotationClassName.
     */
    public static String annotationClassName = "com/yworks/util/annotation/Obfuscation";

    /**
     * The Apply to members.
     */
    public final boolean applyToMembers;
    /**
     * The Exclude.
     */
    public final boolean exclude;

    /**
     * The constant DEFAULT.
     */
    public static final ObfuscationConfig DEFAULT = new ObfuscationConfig(false, false);

    /**
     * Instantiates a new Obfuscation config.
     *
     *
		 * @param exclude        the exclude
     *
		 * @param applyToMembers the apply to members
     */
    public ObfuscationConfig(boolean exclude, boolean applyToMembers) {
    this.applyToMembers = applyToMembers;
    this.exclude = exclude;
  }
}
