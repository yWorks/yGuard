package com.yworks.common.ant;

import com.yworks.common.ShrinkBag;
import com.yworks.common.ResourcePolicy;
import com.yworks.yguard.ant.Property;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class YGuardBaseTask extends Task {

  protected static final boolean MODE_STANDALONE = false;
  protected static final boolean MODE_NESTED = true;

  protected final boolean mode;

  protected List<ShrinkBag> pairs;
  protected Path resourceClassPath;
  protected List<AttributesSection> attributesSections;
  protected Map properties = new HashMap();

  public YGuardBaseTask() {
    mode = MODE_STANDALONE;
  }

  public YGuardBaseTask( boolean mode ) {
    this.mode = mode;
  }

  public AttributesSection createAttribute() {
    if( attributesSections == null ) attributesSections = new ArrayList<AttributesSection>();
    AttributesSection as = new AttributesSection();
    attributesSections.add( as );
    return as;
  }

  public ShrinkBag createInOutPair() {
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    ShrinkBag pair = new InOutPair();
    pairs.add( pair );
    return pair;
  }

  public void addConfiguredInOutPairs(InOutPairSection section){
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    pairs.addAll(section.createShrinkBags(getProject()));
  }

  public void addConfiguredInOutPair( final ShrinkBag pair ) {
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    pairs.add( pair );
  }

  public Path createExternalClasses() {
    if ( this.resourceClassPath != null ) {
      throw new IllegalArgumentException( "Only one externalclasses element allowed!" );
    }
    this.resourceClassPath = new Path( getProject() );
    return this.resourceClassPath;
  }

  public void setResourceClassPath( Path path ) {
    this.resourceClassPath = path;
  }

  public abstract Exclude createKeep();

  public abstract void addAttributesSections( List<AttributesSection> attributesSections );

  public void addConfiguredProperty(Property p){
    properties.put(p.getName(), p.getValue());
  }

  public static final class InOutPairSection {
    private FileSet set;
    private Mapper mapper;
    private ResourcePolicy resources = ResourcePolicy.COPY;

    public void setResources( String resourcesStr ) {

      try {
        resources = ResourcePolicy.valueOf( resourcesStr.trim().toUpperCase() );
      } catch ( IllegalArgumentException e ) {
        throw new BuildException( "Invalid resource policy: " + resourcesStr );
      }
    }

    public InOutPairSection() {
    }

    public void addConfiguredFileSet(FileSet set){
      this.set = set;
    }

    public void add(Mapper mapper){
      this.mapper = mapper;
    }

    public List<ShrinkBag> createShrinkBags(Project project){
      if (mapper == null){
        Mapper.MapperType type = new Mapper.MapperType();
        type.setValue("glob");
        mapper = new Mapper(project);
        mapper.setType(type);
        mapper.setFrom("*.jar");
        mapper.setTo("*_obf.jar");
      }
      ArrayList<ShrinkBag> result = new ArrayList<ShrinkBag>();
      DirectoryScanner directoryScanner = set.getDirectoryScanner(project);
      String[] files = directoryScanner.getIncludedFiles();
      for (int i = 0; i < files.length; i++) {
        String inFile = files[i];
        String[] outFile = mapper.getImplementation().mapFileName(inFile);
        if (outFile == null || outFile.length < 1 ||outFile[0].equals(inFile)){
          throw new BuildException("Cannot obfuscate " + inFile +" using that mapping");
        }
        InOutPair pair = new InOutPair();
        pair.resources = resources;
        pair.setIn(FileUtils.newFileUtils().resolveFile(directoryScanner.getBasedir(), inFile));
        pair.setOut(FileUtils.newFileUtils().resolveFile(directoryScanner.getBasedir(), outFile[0]));
        result.add(pair);
      }
      return result;
    }
  }
}
