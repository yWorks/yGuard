package com.yworks.yshrink.ant;

import com.yworks.yguard.common.ShrinkBag;
import com.yworks.yguard.common.ant.*;
import com.yworks.yguard.common.ant.AttributesSection;
import com.yworks.yguard.obf.Version;
import com.yworks.yguard.obf.classfile.ClassConstants;
import com.yworks.yshrink.YShrink;
import com.yworks.yshrink.ant.filters.*;
import com.yworks.yshrink.util.Logger;
import com.yworks.yshrink.util.XmlLogger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class ShrinkTask extends YGuardBaseTask {
  
  private File logFile = new File( "yshrinklog.xml" );

  private boolean createStubs = false;

  private String digests = "SHA-1,MD5";

  private EntryPointsSection entryPointsSection;
  public ShrinkTask() {
    super();
  }

  public ShrinkTask( boolean mode ) {
    super( mode );
  }

  @Override
  public void execute() throws BuildException {

    getProject().log(this,"yGuard Shrinker v" + Version.getVersion() + " - http://www.yworks.com/products/yguard", Project.MSG_INFO);
    super.execute();

    Logger xmlLogger = new XmlLogger( getLogWriter() );
    Logger antLogger = new AntLogger(getProject(), this);

    final EntryPointFilters epfs = new EntryPointFilters();

    List<com.yworks.yguard.common.ant.AttributesSection> attributesSections = entryPointsSection != null ? entryPointsSection.getAttributesSections() : this.attributesSections;

    if ( entryPointsSection != null ) {

      epfs.setExclude( entryPointsSection );

      List<MethodSection> methodSections = entryPointsSection.getMethodSections();
      List<FieldSection> fieldSections = entryPointsSection.getFieldSections();
      List<ClassSection> classSections = entryPointsSection.getClassSections();

      if ( methodSections.size() > 0 ) {
        MethodFilter mf = new MethodFilter( getProject() );
        for ( MethodSection ms : methodSections ) {
          mf.addMethodSection( ms );
        }
        epfs.addEntryPointFilter( mf );
      }

      if ( fieldSections.size() > 0 ) {
        FieldFilter ff = new FieldFilter( getProject() );
        for ( FieldSection fs : fieldSections ) {
          ff.addFieldSection( fs );
        }
        epfs.addEntryPointFilter( ff );
      }

      if ( classSections.size() > 0 ) {
        ClassFilter cf = new ClassFilter( getProject() );
        for ( ClassSection cs : classSections ) {
          cf.addClassSection( cs );
        }
        epfs.addEntryPointFilter( cf );
      }

      AttributeFilter attributeFilter = new AttributeFilter(getProject());
      if (entryPointsSection.isRiAnn()){
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_RuntimeInvisibleAnnotations);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isRiPann()){
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_RuntimeInvisibleParameterAnnotations);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isRvAnn()){
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_RuntimeVisibleAnnotations);
        attributeFilter.addAttributesSection(as);
      }
       if (entryPointsSection.isRvPann()){
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_RuntimeVisibleParameterAnnotations);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isSource()) {
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_SourceFile);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isLtable()) {
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_LineNumberTable);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isLttable()) {
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_LocalVariableTypeTable);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isVtable()) {
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_LocalVariableTable);
        attributeFilter.addAttributesSection(as);
      }
      if (entryPointsSection.isDebugExtension()) {
        AttributesSection as = new AttributesSection();
        as.setName(ClassConstants.ATTR_SourceDebug);
        attributeFilter.addAttributesSection(as);
      }
      epfs.addEntryPointFilter(attributeFilter);

      // Never remove package-info annotation class
      ClassFilter classFilter = new ClassFilter(getProject());
      ClassSection classSection = new ClassSection();
      PatternSet patternSet = new PatternSet();
      patternSet.setIncludes("**/package-info");
      classSection.addPatternSet(patternSet, TypePatternSet.Type.NAME);
      classFilter.addClassSection(classSection);

      epfs.addEntryPointFilter(classFilter);
    }

    if ( null != attributesSections && attributesSections.size() > 0 ) {
      AttributeFilter af = new AttributeFilter( getProject() );
      for ( com.yworks.yguard.common.ant.AttributesSection as : attributesSections ) {
        af.addAttributesSection( as );
      }
      epfs.addEntryPointFilter( af );
    }

    if ( pairs == null ) {
      throw new BuildException( "no files to shrink" );
    } else {
      boolean containsInOutPair = false;
      boolean containsEntryPointJar = false;
      for ( ShrinkBag shrinkBag : pairs ) {
        if ( shrinkBag.isEntryPointJar() ) {

          EntryPointJarFilter epjf = new EntryPointJarFilter( (EntryPointJar) shrinkBag );
          epfs.addEntryPointFilter( epjf );

          containsEntryPointJar = true;
        } else {
          containsInOutPair = true;
        }
      }

      if ( ! containsInOutPair ) {
        throw new BuildException( "no files to shrink" );
      }

      if ( ( ! containsEntryPointJar ) && ( null == entryPointsSection ) ) {
        Logger.log( "no entrypoints given - using class access public and protected on all inoutpairs." );
        entryPointsSection = new EntryPointsSection( this );
        ClassFilter cf = new ClassFilter( getProject() );
        ClassSection cs = new ClassSection();
        cs.setAccess( "protected" );
        cf.addClassSection( cs );
        epfs.addEntryPointFilter( cf );
        epfs.setExclude( entryPointsSection );
      }
    }

    ResourceCpResolver resolver = null;

    if ( resourceClassPath != null ) {
      resolver = new ResourceCpResolver( resourceClassPath, this );
    }

    if (properties.containsKey("digests")) {
      setDigests((String) properties.get("digests"));
    }

    final YShrink yShrink = new YShrink( createStubs, digests );

    //epfs.addEntryPointFilter( new SerializationFilter( getProject() ) );

    try {

      yShrink.doShrinkPairs( pairs, epfs, resolver );
    } catch ( RuntimeException rte ) {
      if ( rte.getMessage() != null ) {
        Logger.err( rte.getMessage(), rte );
      }
      throw new BuildException( "yShrink encountered an unknown problem!", rte );
    } catch ( Throwable e ) {
      if ( e.getMessage() != null ) {
        Logger.err( e.getMessage(), e );
      } else {
          Logger.err(e.getClass().getName(), e);
      }
      throw new BuildException( "yShrink encountered an unknown severe problem!", e );

    } finally {
      try {
        resolver.close();
      } catch (Exception e) {
        // can't do nothing about it
      }
      xmlLogger.close();
      antLogger.close();
    }
  }

  private PrintWriter getLogWriter() {
    PrintWriter log = null;
    if ( logFile != null ) {
      try {
        if ( logFile.getName().endsWith( ".gz" ) ) {
          log = new PrintWriter(
              new BufferedWriter(
                  new OutputStreamWriter(
                      new GZIPOutputStream(
                          new FileOutputStream( logFile )
                      )
                  )
              )
          );
        } else {
          log = new PrintWriter( new BufferedWriter( new FileWriter( logFile ) ) );
        }
      } catch ( IOException ioe ) {
        getProject().log( this, "Could not create logfile: " + ioe, Project.MSG_ERR );
        log = new PrintWriter( System.out );
      }
    } else {
      log = new PrintWriter( System.out );
    }
    return log;
  }

  public boolean getCreateStubs() {
    return createStubs;
  }

  public void setCreateStubs( boolean createStubs ) {
    this.createStubs = createStubs;
  }

  public String getDigests() {
    return digests;
  }

  public void setDigests( String digests ) {
    this.digests = digests;
  }

  public void setLogFile( File file ) {
    this.logFile = file;
  }

  /**
   * Used by ant to handle the nested <code>entryPoint</code> element.
   *
   * @return an EntryPointsSection instance
   */
  public EntryPointsSection createEntryPoints() {
    if ( this.entryPointsSection != null ) {
      throw new IllegalArgumentException( "Only one entrypoints or expose element allowed!" );
    }
    this.entryPointsSection = new EntryPointsSection( this );
    return entryPointsSection;
  }

  /**
   * not for ant, used if the ShrinkTask is created 'artificially'.
   *
   * @param eps
   */
  public void setEntryPointsExternally( EntryPointsSection eps ) {
    this.entryPointsSection = eps;
  }

  public EntryPointsSection createExpose() {
    return createEntryPoints();
  }

  public Exclude createKeep() {
    return createExpose();
  }

  public void addAttributesSections( List<com.yworks.yguard.common.ant.AttributesSection> attributesSections ) {
    if ( null != entryPointsSection ) {
      for ( com.yworks.yguard.common.ant.AttributesSection attributesSection : attributesSections ) {
        entryPointsSection.addConfiguredAttribute( attributesSection );
      }
    } else {
      if( null != this.attributesSections ) {
        this.attributesSections.addAll(attributesSections);
      } else {
        this.attributesSections = attributesSections;
      }
    }
  }

  public void addConfiguredEntrypointjar( final EntryPointJar entrypointjar ) {
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    pairs.add( entrypointjar );
  }
}
