package com.yworks.yguard.test.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Keep everything
 */
@Obfuscation
public class AnnoTestKEEP {

  public boolean bool1KEEP;

  protected boolean bool2KEEP;

  private boolean bool3KEEP;

  public AnnoTestKEEP(boolean bool1KEEP, boolean bool2KEEP, boolean bool3KEEP) {
    this.bool1KEEP = bool1KEEP;
    this.bool2KEEP = bool2KEEP;
    this.bool3KEEP = bool3KEEP;
  }

  public boolean isBool1KEEP() {
    return bool1KEEP;
  }

  public void setBool1KEEP(boolean bool1KEEP) {
    this.bool1KEEP = bool1KEEP;
  }

  protected boolean isBool2KEEP() {
    return bool2KEEP;
  }

  protected void setBool2KEEP(boolean bool2KEEP) {
    this.bool2KEEP = bool2KEEP;
  }

  private boolean isBool3KEEP() {
    return bool3KEEP;
  }

  private void setBool3KEEP(boolean bool3KEEP) {
    this.bool3KEEP = bool3KEEP;
  }
}
