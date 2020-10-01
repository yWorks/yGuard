package com.yworks.yshrink;

import com.yworks.yguard.ant.PatternMatchedClassesSection;
import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.EntryPointsSection;
import com.yworks.common.ant.TypePatternSet;
import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yshrink.ant.ClassSection;
import com.yworks.yshrink.ant.FieldSection;
import com.yworks.yshrink.ant.MethodSection;
import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.yshrink.ant.ShrinkTask;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class YShrinkInvokerImpl implements YShrinkInvoker {

  final ShrinkTask shrinkTask;

  EntryPointsSection eps;

  public YShrinkInvokerImpl() {
    shrinkTask = new ShrinkTask();
    eps = new EntryPointsSection( shrinkTask );
  }

  public void setEntyPoints( EntryPointsSection eps ) {
    this.eps = eps;
  }

  public void setLogFile( File shrinkLog ) {
    shrinkTask.setLogFile( shrinkLog );
  }

  public void setContext(Task task) {
    shrinkTask.setProject(task.getProject());
    shrinkTask.setOwningTarget(task.getOwningTarget());
    shrinkTask.setTaskName(task.getTaskName());
    shrinkTask.setLocation(task.getLocation());
    shrinkTask.setDescription(task.getDescription());
    shrinkTask.init();
  }

  public void execute() {
    shrinkTask.setEntryPointsExternally( eps );
    shrinkTask.execute();
  }

  public void addPair( ShrinkBag pair ) {
    shrinkTask.addConfiguredInOutPair( pair );
  }

  public void setResourceClassPath( Path path ) {
    shrinkTask.setResourceClassPath( path );
  }

  public void addClassSection( com.yworks.yguard.ant.ClassSection cs ) {

    ClassSection yShrinkCS = new ClassSection();

    addPatternSets( cs, yShrinkCS, "name" );

    yShrinkCS.setClasses( YShrinkInvokerImpl.convertAccess( cs.getClassMode() ).name() );
    yShrinkCS.setFields( YShrinkInvokerImpl.convertAccess( cs.getFieldMode() ).name() );
    yShrinkCS.setMethods( YShrinkInvokerImpl.convertAccess( cs.getMethodMode() ).name() );

    if ( null != cs.getName() ) {
      yShrinkCS.setName( cs.getName() );
    }
    eps.addConfiguredClass( yShrinkCS );
  }

  public void addMethodSection( com.yworks.yguard.ant.MethodSection ms ) {

    MethodSection yShrinkMS = new MethodSection();

    addPatternSets( ms, yShrinkMS, "class" );

    yShrinkMS.setName( ms.getName() );
    yShrinkMS.setClass( ms.getClassName() );

    eps.addConfiguredMethod( yShrinkMS );
  }

  public void addFieldSection( com.yworks.yguard.ant.FieldSection fs ) {

    FieldSection yShrinkFS = new FieldSection();

    addPatternSets( fs, yShrinkFS, "class" );

    yShrinkFS.setName( fs.getName() );
    yShrinkFS.setClass( fs.getClassName() );

    eps.addConfiguredField( yShrinkFS );
  }

  private void addPatternSets( PatternMatchedClassesSection yGuardSection,
                               com.yworks.common.ant.PatternMatchedSection yShrinkSection, String type ) {
    if ( null != yGuardSection.getPatternSets() ) {
      for ( PatternSet ps : (Iterable<? extends PatternSet>) yGuardSection.getPatternSets() ) {
        TypePatternSet tps = new TypePatternSet();
        tps.append( ps, shrinkTask.getProject() );
        tps.setType( type );
        yShrinkSection.addPatternSet( tps, tps.getType() );
      }
    }
  }

  private static PatternMatchedSection.Access convertAccess( int yGuardAccess ) {

    switch ( yGuardAccess ) {
      case YGuardRule.LEVEL_PRIVATE:
        return PatternMatchedSection.Access.PRIVATE;
      case YGuardRule.LEVEL_FRIENDLY:
        return PatternMatchedSection.Access.FRIENDLY;
      case YGuardRule.LEVEL_PROTECTED:
        return PatternMatchedSection.Access.PROTECTED;
      case YGuardRule.LEVEL_PUBLIC:
        return PatternMatchedSection.Access.PUBLIC;
      default:
        return PatternMatchedSection.Access.NONE;
    }
  }
}
