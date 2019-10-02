package com.yworks.yguard.ant;

import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.common.ant.Exclude;
import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.obf.classfile.ClassConstants;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ZipFileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Used by ant to handle the <code>expose</code> element.
 */
public class ExposeSection extends Exclude {

  private List classes = new ArrayList( 5 );
  private List packages = new ArrayList( 5 );
  private List patterns = new ArrayList( 5 );
  private List methods = new ArrayList( 5 );
  private List fields = new ArrayList( 5 );
  private List attributes = new ArrayList( 5 );
  private List lineNumberTables = new ArrayList( 5 );
  private List sourceFiles = new ArrayList( 5 );

  public ExposeSection( ObfuscatorTask task ) {
    super( task );
  }

  public void addPatternSet( PatternSet ps ) {
    patterns.add( ps );
  }

  public MethodSection createMethod() {
    MethodSection ms = new MethodSection();
    this.methods.add( ms );
    return ms;
  }

  public FieldSection createField() {
    FieldSection fs = new FieldSection();
    this.fields.add( fs );
    return fs;
  }

  public ClassSection createClass() {
    ClassSection cs = new ClassSection( task );
    this.classes.add( cs );
    return cs;
  }

  public PackageSection createPackage() {
    PackageSection ps = new PackageSection();
    this.packages.add( ps );
    return ps;
  }

  public AttributesSection createAttribute() {
    AttributesSection as = new AttributesSection( task );
    attributes.add( as );
    return as;
  }

  public LineNumberTableSection createLineNumberTable() {
    LineNumberTableSection lns = new LineNumberTableSection( task );
    lineNumberTables.add( lns );
    return lns;
  }

  public SourceFileSection createSourceFile() {
    SourceFileSection sfs = new SourceFileSection( task );
    sourceFiles.add( sfs );
    return sfs;
  }

  public Collection createEntries( Collection srcJars ) throws IOException {
    Collection entries = new ArrayList( 20 );
    if ( source ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_SourceFile ) );
    }
    if ( vtable ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_LocalVariableTable ) );
    }
    if ( ltable ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_LineNumberTable ) );
    }
    if ( lttable ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_LocalVariableTypeTable ) );
    }
    if ( rvAnn ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeVisibleAnnotations ) );
    }
    if ( rvTypeAnn ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeVisibleTypeAnnotations ) );
    }
    if ( riAnn ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeInvisibleAnnotations ) );
    }
    if ( riTypeAnn ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations ) );
    }
    if ( rvPann ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeVisibleParameterAnnotations ) );
    }
    if ( riPann ) {
      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR_RuntimeInvisibleParameterAnnotations ) );
    }
//    if ( debugExtension ) {
//      entries.add( new YGuardRule( YGuardRule.TYPE_ATTR, ClassConstants.ATTR))
//    }

    for ( Iterator it = srcJars.iterator(); it.hasNext(); ) {
      File file = (File) it.next();
      ZipFileSet zipFile = new ZipFileSet();
      zipFile.setProject( task.getProject() );
      zipFile.setSrc( file );
      for ( Iterator it2 = classes.iterator(); it2.hasNext(); ) {
        ClassSection cs = (ClassSection) it2.next();
        if ( cs.getName() == null && cs.getExtends() == null && cs.getImplements() == null) { 
          cs.addEntries( entries, zipFile );
        }
      }
      for ( Iterator it2 = methods.iterator(); it2.hasNext(); ) {
        MethodSection ms = (MethodSection) it2.next();
        if ( ms.getClassName() == null ) {
          ms.addEntries( entries, zipFile );
        }
      }
      for ( Iterator it2 = fields.iterator(); it2.hasNext(); ) {
        FieldSection fs = (FieldSection) it2.next();
        if ( fs.getClassName() == null ) {
          fs.addEntries( entries, zipFile );
        }
      }
      for ( Iterator it2 = attributes.iterator(); it2.hasNext(); ) {
        AttributesSection as = (AttributesSection) it2.next();
        if ( as.getAttributes() != null ) {
          as.addEntries( entries, zipFile );
        }
      }

      for ( Iterator it2 = lineNumberTables.iterator(); it2.hasNext(); ) {
        LineNumberTableSection lt = (LineNumberTableSection) it2.next();
        lt.addEntries( entries, zipFile );
      }

      for ( Iterator it2 = sourceFiles.iterator(); it2.hasNext(); ) {
        SourceFileSection sfs = (SourceFileSection) it2.next();
        sfs.addEntries( entries, zipFile );
      }
      for ( Iterator it2 = packages.iterator(); it2.hasNext(); ) {
        PackageSection ps = (PackageSection) it2.next();
        ps.addEntries( entries, zipFile );
      }
    }
    for ( Iterator it = classes.iterator(); it.hasNext(); ) {
      ClassSection cs = (ClassSection) it.next();
      if ( cs.getName() != null ) {
        cs.addEntries( entries, cs.getName() );
      }
    }
    for ( Iterator it = methods.iterator(); it.hasNext(); ) {
      MethodSection ms = (MethodSection) it.next();
      if ( ms.getClassName() != null ) {
        ms.addEntries( entries, ms.getClassName() );
      }
    }
    for ( Iterator it = fields.iterator(); it.hasNext(); ) {
      FieldSection fs = (FieldSection) it.next();
      if ( fs.getClassName() != null ) {
        fs.addEntries( entries, fs.getClassName() );
      }
    }

    if ( task instanceof ObfuscatorTask ) {
      ((ObfuscatorTask)task).addInheritanceEntries( entries );
    }


    return entries;
  }

  public List getClasses() {
    return classes;
  }

  public List getPackages() {
    return packages;
  }

  public List getPatterns() {
    return patterns;
  }

  public List getMethods() {
    return methods;
  }

  public List getFields() {
    return fields;
  }

  public List getAttributes() {
    return attributes;
  }

  public List getLineNumberTables() {
    return lineNumberTables;
  }

  public List getSourceFiles() {
    return sourceFiles;
  }

}
