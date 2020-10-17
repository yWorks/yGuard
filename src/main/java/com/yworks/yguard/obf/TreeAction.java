/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf;

/**
 * Set of actions to be performed by a tree walker
 *
 * @author Mark Welsh
 */
public class TreeAction {
  /**
   * Package action.
   *
   *
   *@param pk the pk
   */
  public void packageAction( Pk pk ) {
    defaultAction(pk);
  }

  /**
   * Class action.
   *
   *
   *@param cl the cl
   */
  public void classAction( Cl cl ) {
    defaultAction(cl);
  }

  /**
   * Method action.
   *
   *
   *@param md the md
   */
  public void methodAction( Md md ) {
    defaultAction(md);
  }

  /**
   * Field action.
   *
   *
   *@param fd the fd
   */
  public void fieldAction( Fd fd ) {
    defaultAction(fd);
  }

  /**
   * Default action.
   *
   *
   *@param ti the ti
   */
  public void defaultAction( TreeItem ti ) {
  }
}

