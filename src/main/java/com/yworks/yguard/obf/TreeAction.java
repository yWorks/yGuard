/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

import java.io.*;
import java.util.*;

/**
 * Set of actions to be performed by a tree walker
 *
 * @author      Mark Welsh
 */
public class TreeAction
{
    public void packageAction(Pk pk)  {defaultAction(pk);}
    public void classAction(Cl cl)  {defaultAction(cl);}
    public void methodAction(Md md)  {defaultAction(md);}
    public void fieldAction(Fd fd)  {defaultAction(fd);}
    public void defaultAction(TreeItem ti)  {}
}

