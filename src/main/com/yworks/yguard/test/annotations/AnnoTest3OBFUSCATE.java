package com.yworks.yguard.test.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Use inheritance to obfuscate
 */
@Obfuscation(applyToMembers = true, exclude = false)
public class AnnoTest3OBFUSCATE {

  @Obfuscation( exclude = true)
  public boolean publicBoolField1KEEP;

  public boolean publicBoolField1OBFUSCATE;

  @Obfuscation( exclude = true)
  protected boolean protectedBoolField2KEEP;

  protected boolean protectedBoolField2OBFUSCATE;

  @Obfuscation( exclude = true)
  private boolean privateBoolField3KEEP;

  private boolean privateBoolField3OBFUSCATE;

  public AnnoTest3OBFUSCATE(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  public AnnoTest3OBFUSCATE(boolean publicBoolField1KEEP, boolean protectedBoolField2KEEP, boolean privateBoolField3KEEP, int bla) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  @Obfuscation( exclude = true)
  public boolean isPublicBoolField1KEEP() {
    return publicBoolField1KEEP;
  }

  @Obfuscation( exclude = true)
  public void setPublicBoolField1KEEP(boolean publicBoolField1KEEP) {
    this.publicBoolField1KEEP = publicBoolField1KEEP;
  }

  @Obfuscation( exclude = true)
  private boolean isProtectedBoolField2KEEP() {
    return protectedBoolField2KEEP;
  }

  @Obfuscation( exclude = true)
  private void setProtectedBoolField2KEEP(boolean protectedBoolField2KEEP) {
    this.protectedBoolField2KEEP = protectedBoolField2KEEP;
  }

  @Obfuscation( exclude = true)
  protected boolean isPrivateBoolField3KEEP() {
    return privateBoolField3KEEP;
  }

  @Obfuscation( exclude = true)
  protected void setPrivateBoolField3KEEP(boolean privateBoolField3KEEP) {
    this.privateBoolField3KEEP = privateBoolField3KEEP;
  }

  public boolean isPublicBoolField1OBFUSCATE() {
    return publicBoolField1OBFUSCATE;
  }

  public void setPublicBoolField1OBFUSCATE(boolean publicBoolField1OBFUSCATE) {
    this.publicBoolField1OBFUSCATE = publicBoolField1OBFUSCATE;
  }

  private boolean isProtectedBoolField2OBFUSCATE() {
    return protectedBoolField2OBFUSCATE;
  }

  private void setProtectedBoolField2OBFUSCATE(boolean protectedBoolField2OBFUSCATE) {
    this.protectedBoolField2OBFUSCATE = protectedBoolField2OBFUSCATE;
  }

  protected boolean isPrivateBoolField3OBFUSCATE() {
    return privateBoolField3OBFUSCATE;
  }

  protected void setPrivateBoolField3OBFUSCATE(boolean privateBoolField3OBFUSCATE) {
    this.privateBoolField3OBFUSCATE = privateBoolField3OBFUSCATE;
  }
}
