package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Keep everything
 */
@Obfuscation
public class AnnoTestKEEP {

    /**
     * The Bool 1 keep.
     */
    public boolean bool1KEEP;

    /**
     * The Bool 2 keep.
     */
    protected boolean bool2KEEP;

  private boolean bool3KEEP;

    /**
     * Instantiates a new Anno test keep.
     *
     * @param bool1KEEP the bool 1 keep
     * @param bool2KEEP the bool 2 keep
     * @param bool3KEEP the bool 3 keep
     */
    public AnnoTestKEEP(boolean bool1KEEP, boolean bool2KEEP, boolean bool3KEEP) {
    this.bool1KEEP = bool1KEEP;
    this.bool2KEEP = bool2KEEP;
    this.bool3KEEP = bool3KEEP;
  }

    /**
     * Is bool 1 keep boolean.
     *
     * @return the boolean
     */
    public boolean isBool1KEEP() {
    return bool1KEEP;
  }

    /**
     * Sets bool 1 keep.
     *
     * @param bool1KEEP the bool 1 keep
     */
    public void setBool1KEEP(boolean bool1KEEP) {
    this.bool1KEEP = bool1KEEP;
  }

    /**
     * Is bool 2 keep boolean.
     *
     * @return the boolean
     */
    protected boolean isBool2KEEP() {
    return bool2KEEP;
  }

    /**
     * Sets bool 2 keep.
     *
     * @param bool2KEEP the bool 2 keep
     */
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
