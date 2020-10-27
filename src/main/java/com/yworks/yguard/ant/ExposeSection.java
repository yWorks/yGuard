package com.yworks.yguard.ant;

import com.yworks.yguard.ObfuscatorTask;
import com.yworks.common.ant.Exclude;
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

  /**
   * Instantiates a new Expose section.
   *
   * @param task the task
   */
  public ExposeSection( ObfuscatorTask task ) {
    super( task );
  }

  /**
   * Add pattern set.
   *
   * @param ps the ps
   */
  public void addPatternSet( PatternSet ps ) {
    patterns.add( ps );
  }

  /**
   * Create method method section.
   *
   * @return the method section
   */
  public MethodSection createMethod() {
    MethodSection ms = new MethodSection();
    this.methods.add( ms );
    return ms;
  }

  /**
   * Create field field section.
   *
   * @return the field section
   */
  public FieldSection createField() {
    FieldSection fs = new FieldSection();
    this.fields.add( fs );
    return fs;
  }

  /**
   * Create class class section.
   *
   * @return the class section
   */
  public ClassSection createClass() {
    ClassSection cs = new ClassSection( task );
    this.classes.add( cs );
    return cs;
  }

  /**
   * Create package package section.
   *
   * @return the package section
   */
  public PackageSection createPackage() {
    PackageSection ps = new PackageSection();
    this.packages.add( ps );
    return ps;
  }

  /**
   * Create attribute attributes section.
   *
   * @return the attributes section
   */
  public AttributesSection createAttribute() {
    AttributesSection as = new AttributesSection( task );
    attributes.add( as );
    return as;
  }

  /**
   * Create line number table line number table section.
   *
   * @return the line number table section
   */
  public LineNumberTableSection createLineNumberTable() {
    LineNumberTableSection lns = new LineNumberTableSection( task );
    lineNumberTables.add( lns );
    return lns;
  }

  /**
   * Create source file source file section.
   *
   * @return the source file section
   */
  public SourceFileSection createSourceFile() {
    SourceFileSection sfs = new SourceFileSection( task );
    sourceFiles.add( sfs );
    return sfs;
  }

  /**
   * Create entries collection.
   *
   * @param srcJars the src jars
   * @return the collection
   * @throws IOException the io exception
   */
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

  /**
   * Gets classes.
   *
   * @return the classes
   */
  public List getClasses() {
    return classes;
  }

  /**
   * Gets packages.
   *
   * @return the packages
   */
  public List getPackages() {
    return packages;
  }

  /**
   * Gets patterns.
   *
   * @return the patterns
   */
  public List getPatterns() {
    return patterns;
  }

  /**
   * Gets methods.
   *
   * @return the methods
   */
  public List getMethods() {
    return methods;
  }

  /**
   * Gets fields.
   *
   * @return the fields
   */
  public List getFields() {
    return fields;
  }

  /**
   * Gets attributes.
   *
   * @return the attributes
   */
  public List getAttributes() {
    return attributes;
  }

  /**
   * Gets line number tables.
   *
   * @return the line number tables
   */
  public List getLineNumberTables() {
    return lineNumberTables;
  }

  /**
   * Gets source files.
   *
   * @return the source files
   */
  public List getSourceFiles() {
    return sourceFiles;
  }

}
