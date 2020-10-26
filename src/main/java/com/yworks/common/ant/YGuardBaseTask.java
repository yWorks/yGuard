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
 * The type Y guard base task.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class YGuardBaseTask extends Task {

  /**
   * The constant MODE_STANDALONE.
   */
  protected static final boolean MODE_STANDALONE = false;
  /**
   * The constant MODE_NESTED.
   */
  protected static final boolean MODE_NESTED = true;

  /**
   * The Mode.
   */
  protected final boolean mode;

  /**
   * The Pairs.
   */
  protected List<ShrinkBag> pairs;
  /**
   * The Resource class path.
   */
  protected Path resourceClassPath;
  /**
   * The Attributes sections.
   */
  protected List<AttributesSection> attributesSections;
  /**
   * The Properties.
   */
  protected Map properties = new HashMap();

  /**
   * Instantiates a new Y guard base task.
   */
  public YGuardBaseTask() {
    mode = MODE_STANDALONE;
  }

  /**
   * Instantiates a new Y guard base task.
   *
   * @param mode the mode
   */
  public YGuardBaseTask( boolean mode ) {
    this.mode = mode;
  }

  /**
   * Create attribute attributes section.
   *
   * @return the attributes section
   */
  public AttributesSection createAttribute() {
    if( attributesSections == null ) attributesSections = new ArrayList<AttributesSection>();
    AttributesSection as = new AttributesSection();
    attributesSections.add( as );
    return as;
  }

  /**
   * Create in out pair shrink bag.
   *
   * @return the shrink bag
   */
  public ShrinkBag createInOutPair() {
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    ShrinkBag pair = new InOutPair();
    pairs.add( pair );
    return pair;
  }

  /**
   * Add configured in out pairs.
   *
   * @param section the section
   */
  public void addConfiguredInOutPairs(InOutPairSection section){
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    pairs.addAll(section.createShrinkBags(getProject()));
  }

  /**
   * Add configured in out pair.
   *
   * @param pair the pair
   */
  public void addConfiguredInOutPair( final ShrinkBag pair ) {
    if ( pairs == null ) pairs = new ArrayList<ShrinkBag>();
    pairs.add( pair );
  }

  /**
   * Create external classes path.
   *
   * @return the path
   */
  public Path createExternalClasses() {
    if ( this.resourceClassPath != null ) {
      throw new IllegalArgumentException( "Only one externalclasses element allowed!" );
    }
    this.resourceClassPath = new Path( getProject() );
    return this.resourceClassPath;
  }

  /**
   * Sets resource class path.
   *
   * @param path the path
   */
  public void setResourceClassPath( Path path ) {
    this.resourceClassPath = path;
  }

  /**
   * Create keep exclude.
   *
   * @return the exclude
   */
  public abstract Exclude createKeep();

  /**
   * Add attributes sections.
   *
   * @param attributesSections the attributes sections
   */
  public abstract void addAttributesSections( List<AttributesSection> attributesSections );

  /**
   * Add configured property.
   *
   * @param p the p
   */
  public void addConfiguredProperty(Property p){
    properties.put(p.getName(), p.getValue());
  }

  /**
   * The type In out pair section.
   */
  public static final class InOutPairSection {
    private FileSet set;
    private Mapper mapper;
    private ResourcePolicy resources = ResourcePolicy.COPY;

    /**
     * Sets resources.
     *
     * @param resourcesStr the resources str
     */
    public void setResources( String resourcesStr ) {

      try {
        resources = ResourcePolicy.valueOf( resourcesStr.trim().toUpperCase() );
      } catch ( IllegalArgumentException e ) {
        throw new BuildException( "Invalid resource policy: " + resourcesStr );
      }
    }

    /**
     * Instantiates a new In out pair section.
     */
    public InOutPairSection() {
    }

    /**
     * Add configured file set.
     *
     * @param set the set
     */
    public void addConfiguredFileSet(FileSet set){
      this.set = set;
    }

    /**
     * Add.
     *
     * @param mapper the mapper
     */
    public void add(Mapper mapper){
      this.mapper = mapper;
    }

    /**
     * Create shrink bags list.
     *
     * @param project the project
     * @return the list
     */
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
