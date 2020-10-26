package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Use inheritance to keep
 */
@Obfuscation(applyToMembers = true, exclude = true)
public class AnnoTest4KEEP {

    /**
     * The Public bool field 1 keep.
     */
    public boolean publicBoolField1KEEP;

    /**
     * The Public bool field 1 obfuscate.
     */
    @Obfuscation( exclude = false)
  public boolean publicBoolField1OBFUSCATE;

    /**
     * The Protected bool field 2 keep.
     */
    protected boolean protectedBoolField2KEEP;

    /**
     * The Protected bool field 2 obfuscate.
     */
    @Obfuscation( exclude = false)
  protected boolean protectedBoolField2OBFUSCATE;

  private boolean privateBoolField3KEEP;

  @Obfuscation( exclude = false)
  private boolean privateBoolField3OBFUSCATE;

    /**
     * Instantiates a new Anno test 4 keep.
     *
     * @param publicBoolField1KEEP    the public bool field 1 keep
     * @param protectedBoolField2KEEP the protected bool field 2 keep
     * @param privateBoolField3KEEP   the private bool field 3 keep
     */
    public AnnoTest4KEEP(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

    /**
     * Instantiates a new Anno test 4 keep.
     *
     * @param publicBoolField1KEEP    the public bool field 1 keep
     * @param protectedBoolField2KEEP the protected bool field 2 keep
     * @param privateBoolField3KEEP   the private bool field 3 keep
     * @param bla                     the bla
     */
    public AnnoTest4KEEP(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP, int bla) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

    /**
     * Is public bool field 1 keep boolean.
     *
     * @return the boolean
     */
    public boolean isPublicBoolField1KEEP() {
    return publicBoolField1KEEP;
  }

    /**
     * Sets public bool field 1 keep.
     *
     * @param publicBoolField1KEEP the public bool field 1 keep
     */
    public void setPublicBoolField1KEEP(boolean publicBoolField1KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
  }

  private boolean isProtectedBoolField2KEEP() {
    return protectedBoolField2KEEP;
  }

  private void setProtectedBoolField2KEEP(boolean protectedBoolField2KEEP) {
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
  }

    /**
     * Is private bool field 3 keep boolean.
     *
     * @return the boolean
     */
    protected boolean isPrivateBoolField3KEEP() {
    return privateBoolField3KEEP;
  }

    /**
     * Sets private bool field 3 keep.
     *
     * @param privateBoolField3KEEP the private bool field 3 keep
     */
    protected void setPrivateBoolField3KEEP(boolean privateBoolField3KEEP) {
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

    /**
     * Is public bool field 1 obfuscate boolean.
     *
     * @return the boolean
     */
    @Obfuscation( exclude = false)
  public boolean isPublicBoolField1OBFUSCATE() {
    return publicBoolField1OBFUSCATE;
  }

    /**
     * Sets public bool field 1 obfuscate.
     *
     * @param publicBoolField1OBFUSCATE the public bool field 1 obfuscate
     */
    @Obfuscation( exclude = false)
  public void setPublicBoolField1OBFUSCATE(boolean publicBoolField1OBFUSCATE) {
    this.publicBoolField1OBFUSCATE = publicBoolField1OBFUSCATE;
  }

  @Obfuscation( exclude = false)
  private boolean isProtectedBoolField2OBFUSCATE() {
    return protectedBoolField2OBFUSCATE;
  }

  @Obfuscation( exclude = false)
  private void setProtectedBoolField2OBFUSCATE(boolean protectedBoolField2OBFUSCATE) {
    this.protectedBoolField2OBFUSCATE = protectedBoolField2OBFUSCATE;
  }

    /**
     * Is private bool field 3 obfuscate boolean.
     *
     * @return the boolean
     */
    @Obfuscation( exclude = false)
  protected boolean isPrivateBoolField3OBFUSCATE() {
    return privateBoolField3OBFUSCATE;
  }

    /**
     * Sets private bool field 3 obfuscate.
     *
     * @param privateBoolField3OBFUSCATE the private bool field 3 obfuscate
     */
    @Obfuscation( exclude = false)
  protected void setPrivateBoolField3OBFUSCATE(boolean privateBoolField3OBFUSCATE) {
    this.privateBoolField3OBFUSCATE = privateBoolField3OBFUSCATE;
  }
}
