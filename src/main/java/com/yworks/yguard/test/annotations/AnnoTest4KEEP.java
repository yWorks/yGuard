package com.yworks.yguard.test.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Use inheritance to keep
 */
@Obfuscation(applyToMembers = true, exclude = true)
public class AnnoTest4KEEP {

  public boolean publicBoolField1KEEP;

  @Obfuscation( exclude = false)
  public boolean publicBoolField1OBFUSCATE;

  protected boolean protectedBoolField2KEEP;

  @Obfuscation( exclude = false)
  protected boolean protectedBoolField2OBFUSCATE;

  private boolean privateBoolField3KEEP;

  @Obfuscation( exclude = false)
  private boolean privateBoolField3OBFUSCATE;

  public AnnoTest4KEEP(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  public AnnoTest4KEEP(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP, int bla) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  public boolean isPublicBoolField1KEEP() {
    return publicBoolField1KEEP;
  }

  public void setPublicBoolField1KEEP(boolean publicBoolField1KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
  }

  private boolean isProtectedBoolField2KEEP() {
    return protectedBoolField2KEEP;
  }

  private void setProtectedBoolField2KEEP(boolean protectedBoolField2KEEP) {
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
  }

  protected boolean isPrivateBoolField3KEEP() {
    return privateBoolField3KEEP;
  }

  protected void setPrivateBoolField3KEEP(boolean privateBoolField3KEEP) {
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  @Obfuscation( exclude = false)
  public boolean isPublicBoolField1OBFUSCATE() {
    return publicBoolField1OBFUSCATE;
  }

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

  @Obfuscation( exclude = false)
  protected boolean isPrivateBoolField3OBFUSCATE() {
    return privateBoolField3OBFUSCATE;
  }

  @Obfuscation( exclude = false)
  protected void setPrivateBoolField3OBFUSCATE(boolean privateBoolField3OBFUSCATE) {
    this.privateBoolField3OBFUSCATE = privateBoolField3OBFUSCATE;
  }
}
