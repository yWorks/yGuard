package com.yworks.yguard;

import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.AttributesSection;
import com.yworks.common.ant.EntryPointsSection;
import com.yworks.common.ant.Exclude;
import com.yworks.common.ant.InOutPair;
import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.YGuardBaseTask;
import com.yworks.common.ant.ZipScannerTool;
import com.yworks.util.CollectionFilter;
import com.yworks.util.Version;
import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.ExposeSection;
import com.yworks.yguard.ant.FieldSection;
import com.yworks.yguard.ant.MapParser;
import com.yworks.yguard.ant.Mappable;
import com.yworks.yguard.ant.MethodSection;
import com.yworks.yguard.ant.PackageSection;
import com.yworks.yguard.obf.Cl;
import com.yworks.yguard.obf.Cl.ClassResolver;
import com.yworks.yguard.obf.ClassTree;
import com.yworks.yguard.obf.Filter;
import com.yworks.yguard.obf.GuardDB;
import com.yworks.yguard.obf.LineNumberTableMapper;
import com.yworks.yguard.obf.NameMaker;
import com.yworks.yguard.obf.NameMakerFactory;
import com.yworks.yguard.obf.NoSuchMappingException;
import com.yworks.yguard.obf.ResourceHandler;
import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.obf.classfile.LineNumberInfo;
import com.yworks.yguard.obf.classfile.LineNumberTableAttrInfo;
import com.yworks.yguard.obf.classfile.Logger;
import com.yworks.yshrink.YShrinkInvoker;
import com.yworks.yshrink.YShrinkModel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * The main obfuscation Ant Task
 *
 * @author Sebastian Mueller, yWorks GmbH  (sebastian.mueller@yworks.com)
 */
public class ObfuscatorTask extends YGuardBaseTask {

  //private List pairs = new ArrayList();
  private String mainClass;
  private boolean conserveManifest = false;
  private File logFile = new File("yguardlog.xml");
  protected ExposeSection expose = null;
  protected List<AdjustSection> adjustSections = new ArrayList<AdjustSection>();
  protected MapSection map = null;
  protected PatchSection patch = null;
  //private Path resourceClassPath;

  // shrinking attributes
  private boolean doShrink = false;
  protected EntryPointsSection entryPoints = null;
  private File shrinkLog = null;
  private boolean useExposeAsEntryPoints = true;

  private static final String LOG_TITLE_PRE_VERSION = "  yGuard Bytecode Obfuscator, v";
  private static final String LOG_TITLE_POST_VERSION = ", a Product of yWorks GmbH - http://www.yworks.com";
  private static final String LOG_CREATED = "  Logfile created on ";
  private static final String LOG_INPUT_FILE = "  Jar file to be obfuscated:           ";
  private static final String LOG_OUTPUT_FILE = "  Target Jar file for obfuscated code: ";

  private static final String NO_SHRINKING_SUPPORT = "No shrinking support found.";
  private static final String DEPRECATED = "The obfuscate task is deprecated. Please use the new com.yworks.yguard.YGuardTask instead.";


  /**
   * Holds value of property replaceClassNameStrings.
   */
  private boolean replaceClassNameStrings = true;
  private File[] tempJars;
  private boolean needYShrinkModel;
  private YShrinkModel yShrinkModel;

  /**
   * Instantiates a new Obfuscator task.
   */
  public ObfuscatorTask() {
    super();
  }

  /**
   * Instantiates a new Obfuscator task.
   *
   * @param mode the mode
   */
  public ObfuscatorTask( boolean mode ) {
    super(mode);
  }

  private static String toNativePattern( String pattern ) {
    if (pattern.endsWith(".class")) {
      return pattern;
    } else {
      if (pattern.endsWith("**")) {
        return pattern.replace('.', '/') + "/*.class";
      } else if (pattern.endsWith("*")) {
        return pattern.replace('.', '/') + ".class";
      } else if (pattern.endsWith(".")) {
        return pattern.replace('.', '/') + "**/*.class";
      } else {
        return pattern.replace('.', '/') + ".class";
      }
    }
  }

  /**
   * To native pattern string [ ].
   *
   * @param patterns the patterns
   * @return the string [ ]
   */
  public static String[] toNativePattern( String[] patterns ) {
    if (patterns == null) {
      return new String[0];
    } else {
      String[] res = new String[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        res[i] = toNativePattern(patterns[i]);
      }
      return res;
    }
  }

  /**
   * To native class string.
   *
   * @param className the class name
   * @return the string
   */
  public static final String toNativeClass( String className ) {
    return className.replace('.', '/');
  }

  /**
   * To native method string [ ].
   *
   * @param javaMethod the java method
   * @return the string [ ]
   */
  public static final String[] toNativeMethod( String javaMethod ) {
    StringTokenizer tokenizer = new StringTokenizer(javaMethod, "(,[]) ", true);
    String tmp = tokenizer.nextToken();
    while (tmp.trim().length() == 0) {
      tmp = tokenizer.nextToken();
    }
    String returnType = tmp;
    tmp = tokenizer.nextToken();
    int retarraydim = 0;
    while (tmp.equals("[")) {
      tmp = tokenizer.nextToken();
      if (!tmp.equals("]")) {
        throw new IllegalArgumentException("']' expected but found " + tmp);
      }
      retarraydim++;
      tmp = tokenizer.nextToken();
    }
    if (tmp.trim().length() != 0) {
      throw new IllegalArgumentException("space expected but found " + tmp);
    }
    tmp = tokenizer.nextToken();
    while (tmp.trim().length() == 0) {
      tmp = tokenizer.nextToken();
    }
    String name = tmp;
    StringBuffer nativeMethod = new StringBuffer(30);
    nativeMethod.append('(');
    tmp = tokenizer.nextToken();
    while (tmp.trim().length() == 0) {
      tmp = tokenizer.nextToken();
    }
    if (!tmp.equals("(")) {
      throw new IllegalArgumentException("'(' expected but found " + tmp);
    }
    tmp = tokenizer.nextToken();
    while (!tmp.equals(")")) {
      while (tmp.trim().length() == 0) {
        tmp = tokenizer.nextToken();
      }
      String type = tmp;
      tmp = tokenizer.nextToken();
      while (tmp.trim().length() == 0) {
        tmp = tokenizer.nextToken();
      }
      int arraydim = 0;
      while (tmp.equals("[")) {
        tmp = tokenizer.nextToken();
        if (!tmp.equals("]")) {
          throw new IllegalArgumentException("']' expected but found " + tmp);
        }
        arraydim++;
        tmp = tokenizer.nextToken();
      }
      while (tmp.trim().length() == 0) {
        tmp = tokenizer.nextToken();
      }

      nativeMethod.append(toNativeType(type, arraydim));
      if (tmp.equals(",")) {
        tmp = tokenizer.nextToken();
        while (tmp.trim().length() == 0) {
          tmp = tokenizer.nextToken();
        }
        continue;
      }
    }
    nativeMethod.append(')');
    nativeMethod.append(toNativeType(returnType, retarraydim));
    String[] result = new String[]{name, nativeMethod.toString()};
    return result;
  }

  private static final String toNativeType( String type, int arraydim ) {
    StringBuffer nat = new StringBuffer(30);
    for (int i = 0; i < arraydim; i++) {
      nat.append('[');
    }
    if ("byte".equals(type)) {
      nat.append('B');
    } else if ("char".equals(type)) {
      nat.append('C');
    } else if ("double".equals(type)) {
      nat.append('D');
    } else if ("float".equals(type)) {
      nat.append('F');
    } else if ("int".equals(type)) {
      nat.append('I');
    } else if ("long".equals(type)) {
      nat.append('J');
    } else if ("short".equals(type)) {
      nat.append('S');
    } else if ("boolean".equals(type)) {
      nat.append('Z');
    } else if ("void".equals(type)) {
      nat.append('V');
    } else { //Lclassname;
      nat.append('L');
      nat.append(type.replace('.', '/'));
      nat.append(';');
    }
    return nat.toString();
  }

  /**
   * Sets need y shrink model.
   *
   * @param b the b
   */
  public void setNeedYShrinkModel( boolean b ) {
    this.needYShrinkModel = b;
  }

  /**
   * Used by ant to handle the <code>patch</code> element.
   */
  public final class PatchSection {
    private List patches = new ArrayList();

    /**
     * Add configured class.
     *
     * @param cs the cs
     */
    public void addConfiguredClass( ClassSection cs ) {
      patches.add(cs);
    }

    /**
     * Create entries collection.
     *
     * @param srcJars the src jars
     * @return the collection
     * @throws IOException the io exception
     */
    Collection createEntries( Collection srcJars ) throws IOException {
      Collection entries = new ArrayList(20);
      for (Iterator it = srcJars.iterator(); it.hasNext(); ) {
        File file = (File) it.next();
        ZipFileSet zipFile = new ZipFileSet();
        zipFile.setProject(getProject());
        zipFile.setSrc(file);
        for (Iterator it2 = patches.iterator(); it2.hasNext(); ) {
          ClassSection cs = (ClassSection) it2.next();
          if (cs.getName() == null) {
            cs.addEntries(entries, zipFile);
          } else {
            cs.addEntries(entries, cs.getName());
          }
        }
      }
      return entries;
    }
  }

  /**
   * Used by ant to handle the <code>classes</code>,
   * <CODE>methods</CODE> and <CODE>fields</CODE> attributes.
   */
  public static final class Modifiers extends EnumeratedAttribute {
    public String[] getValues() {
      return new String[]{"public", "protected", "friendly", "private", "none"};
    }

    private int myGetIndex() {
      String[] values = getValues();
      for (int i = 0; i < values.length; i++) {
        if (getValue().equals(values[i])) {
          return i;
        }
      }
      return -1;
    }

    /**
     * Get modifier value int.
     *
     * @return the int
     */
    public int getModifierValue() {
      switch (myGetIndex()) {
        default:
          return YGuardRule.LEVEL_NONE;
        case 0:
          return YGuardRule.LEVEL_PUBLIC;
        case 1:
          return YGuardRule.LEVEL_PROTECTED;
        case 2:
          return YGuardRule.LEVEL_FRIENDLY;
        case 3:
          return YGuardRule.LEVEL_PRIVATE;
        case 4:
          return YGuardRule.LEVEL_NONE;
      }
    }
  }


  /**
   * Used by ant to handle the <code>map</code> element.
   */
  public final class MapSection {
    private File logFile;
    private List mappables = new ArrayList();

    /**
     * Add configured package.
     *
     * @param ps the ps
     */
    public void addConfiguredPackage( PackageSection ps ) {
      mappables.add(ps);
    }

    /**
     * Add configured class.
     *
     * @param ps the ps
     */
    public void addConfiguredClass( ClassSection ps ) {
      mappables.add(ps);
    }

    /**
     * Add configured field.
     *
     * @param ps the ps
     */
    public void addConfiguredField( FieldSection ps ) {
      mappables.add(ps);
    }

    /**
     * Add configured method.
     *
     * @param ps the ps
     */
    public void addConfiguredMethod( MethodSection ps ) {
      mappables.add(ps);
    }

    /**
     * Set log file.
     *
     * @param logFile the log file
     */
    public void setLogFile( File logFile ) {
      this.logFile = logFile;
    }

    /**
     * Create entries collection.
     *
     * @param antproject the antproject
     * @param log        the log
     * @return the collection
     * @throws BuildException the build exception
     */
    Collection createEntries( Project antproject, PrintWriter log ) throws BuildException {
      Collection res;
      if (logFile != null) {
        try {
          SAXParserFactory f = SAXParserFactory.newInstance();
          f.setValidating(false);
          SAXParser parser = f.newSAXParser();
          XMLReader r = parser.getXMLReader();
          MapParser mp = new MapParser(ObfuscatorTask.this);
          r.setContentHandler(mp);
          Reader reader;
          if (logFile.getName().endsWith(".gz")) {
            reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(logFile)));
          } else {
            reader = new FileReader(logFile);
          }
          InputSource source = new InputSource(reader);
          antproject.log("Parsing logfile's " + logFile.getName() + " map elements...", Project.MSG_INFO);
          r.parse(source);
          reader.close();
          r = null;
          f = null;
          parser = null;
          res = mp.getEntries();
        } catch (ParserConfigurationException pxe) {
          throw new BuildException("Could configure xml parser!", pxe);
        } catch (SAXException pxe) {
          throw new BuildException("Error parsing xml logfile!" + pxe, pxe);
        } catch (IOException ioe) {
          throw new BuildException("Could not parse map from logfile!", ioe);
        }
      } else {
        res = new ArrayList(mappables.size());
      }
      for (Iterator it = mappables.iterator(); it.hasNext(); ) {
        Mappable m = (Mappable) it.next();
        m.addMapEntries(res);
      }
      return res;
    }
  }


  /**
   * Used by ant to handle the <code>adjust</code> element.
   */
  public static class AdjustSection extends ZipFileSet {
    private static final int REPLACE_CONTENT = 1;
    private static final int REPLACE_CONTENT_POLICY = 2;
    private static final int REPLACE_CONTENT_SEPARATOR = 4;
    private static final int REPLACE_NAME = 8;
    private static final int REPLACE_PATH = 16;
    private static final int REPLACE_PATH_POLICY = 32;

    private boolean replaceName = false;
    private boolean replacePath = true;
    private ReplacePathPolicy replacePathPolicy;
    private boolean replaceContent = false;
    private String replaceContentSeparator = "/";
    private ReplaceContentPolicy replaceContentPolicy;

    private Set entries;
    private int state;

    /**
     * Instantiates a new Adjust section.
     */
    public AdjustSection() {
    }

    /**
     * Determines if the jar entry with the given name has to be adjusted.
     *
     * @param name the name of the jar entry to check.
     * @return <code>true</code> if the jar entry with the given name will be
     * adjusted; <code>false</code> otherwise.
     */
    public boolean contains( String name ) {
      return entries.contains(name);
    }

    /**
     * Specifies if the contents of matched jar entries have to be adjusted.
     *
     * @param enabled if <code>true</code>, the contents of matched jar entries
     *                will be adjusted.
     */
    public void setReplaceContent( boolean enabled ) {
      this.replaceContent = enabled;
      this.state |= REPLACE_CONTENT;
    }

    /**
     * Determines if the contents of matched jar entries have to be adjusted.
     *
     * @return <code>true</code> if the contents of matched jar entries will be
     * adjusted; <code>false</code> otherwise.
     */
    public boolean getReplaceContent() {
      return replaceContent;
    }

    /**
     * Specifies the policy for adjusting the content of matched jar entries.
     *
     * @param policy the policy that determines if and how to adjust the
     *               content of matched jar entries.
     */
    public void setReplaceContentPolicy( final ReplaceContentPolicy policy ) {
      this.replaceContentPolicy = policy;
      this.state |= REPLACE_CONTENT_POLICY;
    }

    /**
     * Returns the policy for adjusting the content of matched jar entries.
     *
     * @return the policy that determines if and how to adjust the
     * content of matched jar entries.
     */
    public ReplaceContentPolicy getReplaceContentPolicy() {
      return replaceContentPolicy;
    }

    /**
     * Specifies the separator character for finding content class or
     * package identifiers of matched jar entries that have to be adjusted.
     *
     * @param separator either <code>/</code> or <code>.</code>.
     */
    public void setReplaceContentSeparator( String separator ) {
      this.replaceContentSeparator = separator;
      this.state |= REPLACE_CONTENT_SEPARATOR;
    }

    /**
     * Returns the separator character for finding content class or
     * package identifiers of matched jar entries that have to be adjusted.
     *
     * @return either <code>/</code> or <code>.</code>.
     */
    public String getReplaceContentSeparator() {
      return replaceContentSeparator;
    }

    /**
     * Specifies if the path of matched jar entries whose path and name match
     * the qualified name of a renamed class will be adjusted to match the
     * package name of the renamed class.
     * This property can only be used to prevent adjusting the path of
     * matched jar entries whose name is changed because of
     * {@link #setReplaceName(boolean)}.
     *
     * @param enabled if <code>false</code>, the path of jar entries that are
     *                renamed will not be changed.
     * @see #setReplaceName(boolean)
     */
    public void setReplacePath( boolean enabled ) {
      this.replacePath = enabled;
      this.state |= REPLACE_PATH;
    }

    /**
     * Determines if the path of matched jar entries whose path and name match
     * the qualified name of a renamed class will be adjusted to match the
     * package name of the renamed class.
     * This property can only be used to prevent adjusting the path of
     * matched jar entries whose name is changed because of
     * {@link #getReplaceName()}.
     *
     * @return <code>false</code> if the path of jar entries that are
     * renamed will not be changed; <code>true</code> otherwise.
     * @see #getReplaceName()
     */
    public boolean getReplacePath() {
      return replacePath;
    }

    /**
     * Determines if the path and name of matched jar entries whose path and name
     * match the qualified name of a renamed class will be adjusted to match the
     * qualified name of the renamed class.
     *
     * @return <code>true</code> if the path and name of matched jar entries
     * will be adjusted.
     * <p>
     * To adjust only the name of matched entries, but not their path,
     * set {@link #getReplacePath()} to <code>false</code>.
     * </p>
     * @see #getReplacePath()
     */
    public boolean getReplaceName() {
      return replaceName;
    }

    /**
     * Specifies if the path and name of matched jar entries whose path and name
     * match the qualified name of a renamed class will be adjusted to match the
     * qualified name of the renamed class.
     * <p>
     * To adjust only the name of matched entries, but not their path,
     * set {@link #setReplacePath(boolean)} to <code>false</code>.
     * </p>
     *
     * @param enabled if <code>true</code>, the path and name of matched jar
     *                entries will be adjusted.
     * @see #setReplacePath(boolean)
     */
    public void setReplaceName( boolean enabled ) {
      this.replaceName = enabled;
      this.state |= REPLACE_NAME;
    }

    /**
     * Specifies the policy for adjusting the path to and name of matched jar
     * entries.
     *
     * @param policy the policy that determines if and how to adjust the path
     *               to and name of matched jar entries.
     */
    public void setReplacePathPolicy( final ReplacePathPolicy policy ) {
      this.replacePathPolicy = policy;
      this.state |= REPLACE_PATH_POLICY;
    }

    /**
     * Returns the policy for adjusting the path to and name of matched jar
     * entries.
     *
     * @return the policy that determines if and how to adjust the path
     * to and name of matched jar entries.
     */
    public ReplacePathPolicy getReplacePathPolicy() {
      return replacePathPolicy;
    }

    /**
     * Initialize the set of jar entries that are matched by this adjust
     * section.
     * <p>
     * This method has to be called before {@link #contains(String)} can be
     * used.
     * </p>
     *
     * @param srcJars the set of jar entries to check for matches.
     */
    public void createEntries( Collection srcJars ) throws IOException {
      entries = new HashSet();
      for (Iterator iter = srcJars.iterator(); iter.hasNext(); ) {
        File file = (File) iter.next();
        setSrc(file);

        DirectoryScanner scanner = getDirectoryScanner(getProject());
        String[] includedFiles = ZipScannerTool.getMatches(this, scanner);

        for (int i = 0; i < includedFiles.length; i++) {
          entries.add(includedFiles[i]);
        }
      }
    }

    void prepare( final ObfuscatorTask task ) {
      prepareContentPolicy(task);
      prepareContentSeparator();
      preparePathPolicy(task);
    }

    private void prepareContentPolicy( final ObfuscatorTask task ) {
      final int state = this.state;
      final boolean newContent = isSet(state, REPLACE_CONTENT_POLICY);
      final boolean oldContent = isSet(state, REPLACE_CONTENT);
      if (newContent) {
        if (oldContent) {
          throw new BuildException(
                  "Invalid adjust configuration, cannot use replaceContent and " +
                  "replaceContentPolicy together. Use replaceContentPolicy only.",
                  getLocation());
        }
      } else {
        if (oldContent) {
          info(task,
               "replaceContent is deprecated, use replaceContentPolicy instead.");
        }
        setReplaceContentPolicy(getReplaceContent()
                                ? ReplaceContentPolicy.lenient : ReplaceContentPolicy.none);
      }
    }

    private void prepareContentSeparator() {
      final ReplaceContentPolicy cp = getReplaceContentPolicy();
      if (ReplaceContentPolicy.none != cp) {
        final String sep = getReplaceContentSeparator();
        if ("/.".equals(sep)) {
          setReplaceContentSeparator("./");
        } else if (!".".equals(sep) && !"/".equals(sep) && !"./".equals(sep)) {
          throw new BuildException(
                  "Invalid adjust replaceContentSeparator: " + sep, getLocation());
        }
      }
    }

    private void preparePathPolicy( final ObfuscatorTask task ) {
      final int state = this.state;
      final boolean newPath = isSet(state, REPLACE_PATH_POLICY);
      final boolean oldPath = isSet(state, REPLACE_PATH);
      final boolean oldName = isSet(state, REPLACE_NAME);
      if (newPath) {
        if (oldPath) {
          throw new BuildException(
                  "Invalid adjust configuration, cannot use replacePath and " +
                  "replacePathPolicy together. Use replacePathPolicy only.",
                  getLocation());
        }
        if (oldName) {
          throw new BuildException(
                  "Invalid adjust configuration, cannot use replaceName and " +
                  "replacePathPolicy together. Use replacePathPolicy only.",
                  getLocation());
        }
      } else {
        if (oldPath) {
          info(task,
               "replacePath is deprecated, use replacePathPolicy instead.");
        }
        if (oldName) {
          info(task,
               "replaceName is deprecated, use replacePathPolicy instead.");
        }

        if (getReplacePath()) {
          if (getReplaceName()) {
            setReplacePathPolicy(ReplacePathPolicy.file);
          } else {
            setReplacePathPolicy(ReplacePathPolicy.path);
          }
        } else {
          if (getReplaceName()) {
            setReplacePathPolicy(ReplacePathPolicy.name);
          } else {
            setReplacePathPolicy(ReplacePathPolicy.none);
          }
        }
      }
    }

    private static boolean isSet( final int mask, final int flag ) {
      return (mask & flag) == flag;
    }

    private void info( final ObfuscatorTask task, final String msg ) {
      task.getProject().log(task, addLocation(msg), Project.MSG_VERBOSE);
    }

    private String addLocation( final String msg ) {
      String _msg = "adjust";
      final int ln = getLineNumber();
      if (ln > 0) {
        _msg += ":" + ln;
      }
      _msg += ": " + msg;
      return _msg;
    }

    private int getLineNumber() {
      final Location l = getLocation();
      return l == null ? 0 : l.getLineNumber();
    }
  }

  /**
   * Used by ant to handle the nested <code>expose</code> element.
   *
   * @return an ExposeSection instance
   */
  public ExposeSection createExpose() {
    if (this.expose != null) {
      throw new IllegalArgumentException("Only one expose element allowed!");
    }
    this.expose = newExposeSection(this);
    return expose;
  }

  /**
   * Instantiates the expose section,
   * subclasses may provide custom implementations.
   *
   * @return a new ExposeSection instance
   */
  protected ExposeSection newExposeSection( ObfuscatorTask ot ) {
    return new ExposeSection(ot);
  }

  /**
   * Add excludes.
   *
   * @param entryPoints the entry points
   */
  public void addExcludes( EntryPointsSection entryPoints ) {
    if (null == this.expose) {
      createExpose();
    }
  }

  public Exclude createKeep() {
    return createExpose();
  }

  public void addAttributesSections( List<AttributesSection> attributesSections ) {
    if (null != expose) {
      List attributes = expose.getAttributes();
      for (AttributesSection attributesSection : attributesSections) {
        com.yworks.yguard.ant.AttributesSection asYGuard = new com.yworks.yguard.ant.AttributesSection(this);
        PatternSet patternSet = attributesSection.getPatternSet(TypePatternSet.Type.NAME);
        if (patternSet != null) {
          asYGuard.addConfiguredPatternSet(patternSet);
        }
        asYGuard.setName(attributesSection.getAttributesStr());
        attributes.add(asYGuard);
      }
    }
  }

  /**
   * Used by ant to handle the nested <code>adjust</code> element.
   *
   * @return an AdjustSection instance
   */
  public AdjustSection createAdjust() {
    AdjustSection adjust = newAdjustSection();
    adjust.setProject(this.getProject());
    adjustSections.add(adjust);
    return adjust;
  }

  /**
   * Instantiates an adjust section,
   * subclasses may provide custom implementations.
   *
   * @return a new AdjustSection instance
   */
  protected AdjustSection newAdjustSection() {
    return new AdjustSection();
  }

  /**
   * Used by ant to handle the nested <code>expose</code> element.
   *
   * @param ex the ex
   */
  public void addConfiguredExpose( ExposeSection ex ) {
    if (this.expose != null) {
      throw new IllegalArgumentException("Only one expose element allowed!");
    }
    this.expose = ex;
  }

  /**
   * Create entry points entry points section.
   *
   * @return the entry points section
   */
  public EntryPointsSection createEntryPoints() {
    return newEntryPointsSection(this);
  }

  /**
   * Instantiates an entry points section,
   * subclasses may provide custom implementations.
   *
   * @return the new entry points section
   */
  protected EntryPointsSection newEntryPointsSection( YGuardBaseTask bt ) {
    return new EntryPointsSection(bt);
  }

  /**
   * Used by ant to handle the nested <code>entrypoints</code> element.
   *
   * @param eps the eps
   */
  public void addConfiguredEntryPoints( EntryPointsSection eps ) {
    if (this.entryPoints != null) {
      throw new IllegalArgumentException("Only one entrypoints element allowed!");
    }
    this.entryPoints = eps;
  }

  /**
   * Used by ant to handle the nested <code>map</code> element.
   *
   * @return an instance of MapSection
   */
  public MapSection createMap() {
    if (this.map != null) {
      throw new IllegalArgumentException("Only one map element allowed!");
    }
    this.map = newMapSection();
    return map;
  }

  /**
   * Instantiates the nested <code>map</code> element,
   * subclasses may provide custom implementations.
   *
   * @return a new instance of MapSection
   */
  protected MapSection newMapSection() {
    return new MapSection();
  }

  /**
   * Used by ant to handle the nested <code>map</code> element.
   *
   * @param map the map
   */
  public void addConfiguredMap( MapSection map ) {
    if (this.map != null) {
      throw new IllegalArgumentException("Only one map element allowed!");
    }
    this.map = map;
  }

  /**
   * Used by ant to handle the nested <code>patch</code> element.
   *
   * @return an instance of PatchSection
   */
  public PatchSection createPatch() {
    if (this.patch != null) {
      throw new IllegalArgumentException("Only one patch element allowed!");
    }
    this.patch = newPatchSection();
    return patch;
  }

  /**
   * Instantiates the nested <code>patch</code> element,
   * subclasses may provide custom implementations.
   *
   * @return a new instance of PatchSection
   */
  protected PatchSection newPatchSection() {
    return new PatchSection();
  }

  /**
   * Used by ant to handle the nested <code>patch</code> element.
   *
   * @param patch the patch
   */
  public void addConfiguredPatch( PatchSection patch ) {
    if (this.patch != null) {
      throw new IllegalArgumentException("Only one patch element allowed!");
    }
    this.patch = patch;
  }


  /**
   * Used by ant to handle the <code>logfile</code> attribute.
   *
   * @param file the file
   */
  public void setLogFile( File file ) {
    this.logFile = file;
  }

  /**
   * Used by ant to handle the <code>conservemanifest</code> attribute.
   *
   * @param c the c
   */
  public void setConserveManifest( boolean c ) {
    this.conserveManifest = c;
  }

  /**
   * Used by ant to handle the <code>mainclass</code> attribute.
   *
   * @param mainClass the main class
   */
  public void setMainClass( String mainClass ) {
    this.mainClass = mainClass;
  }


  /**
   * Used by ant to handle the start the obfuscation process.
   */
  public void execute() throws BuildException {
    final String msg =
            "yGuard Obfuscator v" +
            Version.getVersion() +
            " - https://www.yworks.com/products/yguard";
    getProject().log(this, msg, Project.MSG_INFO);


    if (mode == MODE_STANDALONE) {
      getProject().log(this, DEPRECATED, Project.MSG_WARN);
    }


    try {
      for (AdjustSection section : adjustSections) {
        section.prepare(this);
      }
    } catch (BuildException be) {
      throw new BuildException(be.getMessage(), be.getLocation());
    }

    TaskLogger taskLogger = new TaskLogger();

    if (!(mode == MODE_STANDALONE)) {
      doShrink = false;
    }

    if (doShrink) {
      doShrink();
    }

    ResourceCpResolver resolver = null;
    if (resourceClassPath != null) {
      resolver = new ResourceCpResolver(resourceClassPath, this);
      Cl.setClassResolver(resolver);
    }

    YGuardNameFactory nameFactory = null;

    if (properties.containsKey("naming-scheme")
        || properties.containsKey("language-conformity")
        || properties.containsKey("overload-enabled")) {
      String ns = (String) properties.get("naming-scheme");
      String lc = (String) properties.get("language-conformity");

      int ilc = YGuardNameFactory.LEGAL;
      int ins = YGuardNameFactory.SMALL;

      if ("compatible".equalsIgnoreCase(lc)) {
        ilc = YGuardNameFactory.COMPATIBLE;
      } else if ("illegal".equalsIgnoreCase(lc)) {
        ilc = YGuardNameFactory.ILLEGAL;
      }
      if ("mix".equalsIgnoreCase(ns)) {
        ins = YGuardNameFactory.MIX;
      }
      if ("best".equalsIgnoreCase(ns)) {
        ins = YGuardNameFactory.BEST;
      }
      nameFactory = new YGuardNameFactory(ilc | ins);

      nameFactory.setPackagePrefix((String) properties.get("obfuscation-prefix"));
    } else {
      nameFactory = new YGuardNameFactory(YGuardNameFactory.LEGAL | YGuardNameFactory.SMALL);
      nameFactory.setPackagePrefix((String) properties.get("obfuscation-prefix"));
    }

    if (properties.containsKey("overload-enabled")) {
      String overload = (String) properties.get("overload-enabled");
      boolean overloadEnabled = true;
      if ("false".equalsIgnoreCase(overload) || "no".equalsIgnoreCase("overload")) {
        overloadEnabled = false;
      }
      nameFactory.setOverloadEnabled(overloadEnabled);
    }

    boolean pedantic = false;
    if (properties.containsKey("error-checking")) {
      String ed = (String) properties.get("error-checking");
      if ("pedantic".equalsIgnoreCase(ed)) {
        pedantic = true;
      }
    }

    boolean classVersionChecking = true;
    if (properties.containsKey("class-version-checking")) {
      String versionCheck = (String) properties.get("class-version-checking");
      if ("false".equals(versionCheck)) {
        getProject().log(this, "Class version checking disabled.", Project.MSG_WARN);
        getProject().log(this, "It's possible cause serious problem in obfuscated result.", Project.MSG_WARN);
        classVersionChecking = false;
      }
    }

    getProject().log(this, "Using NameMakerFactory: " + NameMakerFactory.getInstance(), Project.MSG_VERBOSE);

    if (pairs == null) {
      throw new BuildException("No in out pairs specified!");
    }
    Collection inFilesList = new ArrayList(pairs.size());
    File[] inFiles = new File[pairs.size()];
    File[] outFiles = new File[pairs.size()];
    for (int i = 0; i < pairs.size(); i++) {
      InOutPair pair = (InOutPair) pairs.get(i);
      if (pair.getIn() == null || !pair.getIn().canRead()) {
        throw new BuildException("Cannot open inoutpair.in " + pair.getIn());
      }
      inFiles[i] = pair.getIn();
      inFilesList.add(pair.getIn());
      if (pair.getOut() == null) {
        throw new BuildException("Must specify inoutpair.out!");
      }
      outFiles[i] = pair.getOut();
    }
    PrintWriter log = null;
    if (logFile != null) {
      try {
        if (logFile.getName().endsWith(".gz")) {
          log = new PrintWriter(
                  new BufferedWriter(
                          new OutputStreamWriter(
                                  new GZIPOutputStream(
                                          new FileOutputStream(logFile)
                                  )
                          )
                  )
          );
        } else {
          log = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
        }
        taskLogger.setWriter(log);
      } catch (IOException ioe) {
        getProject().log(this, "Could not create logfile: " + ioe, Project.MSG_ERR);
        log = new PrintWriter(System.out);
      }
    } else {
      log = new PrintWriter(System.out);
    }
    writeLogHeader(log, inFiles, outFiles);
    try {
      Collection rules = null;
      if (expose != null) {
        rules = expose.createEntries(inFilesList);
      } else {
        rules = new ArrayList(20);
      }

      if (mainClass != null) {
        String cn = toNativeClass(mainClass);
        rules.add(new YGuardRule(YGuardRule.TYPE_CLASS, cn));
        rules.add(new YGuardRule(YGuardRule.TYPE_METHOD, cn + "/main", "([Ljava/lang/String;)V"));
      }
      if (map != null) {
        Collection mapEntries = map.createEntries(getProject(), log);
        rules.addAll(mapEntries);
      }

      for (AdjustSection as : adjustSections) {
        as.createEntries(inFilesList);
      }

      if (properties.containsKey("expose-attributes")) {
        StringTokenizer st = new StringTokenizer((String) properties.get("expose-attributes"), ",", false);
        while (st.hasMoreTokens()) {
          String attribute = st.nextToken().trim();
          rules.add(new YGuardRule(YGuardRule.TYPE_ATTR, attribute));
          getProject().log(this, "Exposing attribute '" + attribute + "'", Project.MSG_VERBOSE);
        }
      }

      try {
        ObfuscatorTask.LogListener listener = new ObfuscatorTask.LogListener(getProject());
        Filter filter = null;
        if (patch != null) {
          getProject().log(this, "Patching...", Project.MSG_INFO);
          Collection patchfiles = patch.createEntries(inFilesList);
          //generate namelist of classes....
          Set names = new HashSet();
          for (Iterator it = patchfiles.iterator(); it.hasNext(); ) {
            YGuardRule entry = (YGuardRule) it.next();
            if (entry.type == YGuardRule.TYPE_CLASS) {
              names.add(entry.name + ".class");
            }
          }
          filter = new ClassFileFilter(new CollectionFilter(names));
        }
        GuardDB db = newGuardDB(inFiles);

        if (properties.containsKey("digests")) {
          String digests = (String) properties.get("digests");
          if (digests.trim().equalsIgnoreCase("none")) {
            db.setDigests(new String[0]);
          } else {
            db.setDigests(digests.split("\\s*,\\s*"));
          }
        }

        if (annotationClass != null) {
          db.setAnnotationClass(toNativeClass(annotationClass));
        }

        db.setResourceHandler(newResourceAdjuster(db));
        db.setPedantic(pedantic);
        db.setClassVersionChecking(classVersionChecking);
        db.setReplaceClassNameStrings(replaceClassNameStrings);
        db.addListener(listener);
        db.retain(rules, log);
        db.remapTo(outFiles, filter, log, conserveManifest);

        for (Iterator it = rules.iterator(); it.hasNext(); ) {
          ((YGuardRule) it.next()).logProperties(log);
        }

        db.close();
        Cl.setClassResolver(null);

        if (doShrink) {
          for (int i = 0; i < tempJars.length; i++) {
            if (null != tempJars[i]) {
              tempJars[i].delete();
            }
          }
        }

        if (!Logger.getInstance().isAllResolved()) {
          Logger.getInstance().warning("Not all dependencies could be resolved. Please see the logfile for details.");
        }

      } catch (NoSuchMappingException nsm) {
        throw new BuildException("yGuard was unable to determine the mapped name for " + nsm.getKey() + ".\n Probably broken code. Try recompiling from source!", nsm);
      } catch (ClassNotFoundException cnfe) {
        throw new BuildException("yGuard was unable to resolve a class (" + cnfe + ").\n Probably a missing external dependency.", cnfe);
      } catch (IOException ioe) {
        if (ioe.getMessage() != null) {
          getProject().log(this, ioe.getMessage(), Project.MSG_ERR);
        }
        throw new BuildException("yGuard encountered an IO problem!", ioe);
      } catch (ParseException pe) {
        throw new BuildException("yGuard encountered problems during parsing!", pe);
      } catch (RuntimeException rte) {
        if (rte.getMessage() != null) {
          getProject().log(this, rte.getMessage(), Project.MSG_ERR);
        }
        rte.printStackTrace();
        throw new BuildException("yGuard encountered an unknown problem!", rte);
      } finally {
        writeLogFooter(log);
        log.flush();
        log.close();
      }
    } catch (IOException ioe) {
      throw new BuildException("yGuard encountered an IO problem!", ioe);
    } finally {
      try {
        if (resolver != null) {
          resolver.close();
        }
      } catch (Exception e) {
        // can't do nothing about it
      }
    }
  }

  /**
   * Instantiates the classfile database for obfuscation,
   * subclasses may provide custom implementations.
   *
   * @return the new classfile database instance
   */
  protected GuardDB newGuardDB( File[] inFile ) throws IOException {
    return new GuardDB(inFile);
  }

  /**
   * Instantiates the type Resource adjuster,
   * subclasses may provide custom implementations.
   *
   * @return the new type Resource adjuster instance
   */
  protected ResourceAdjuster newResourceAdjuster( GuardDB db ) {
    return new ResourceAdjuster(db);
  }

  private void doShrink() {
    YShrinkInvoker yShrinkInvoker = null;

    try {
      yShrinkInvoker = (YShrinkInvoker) Class.forName("com.yworks.yshrink.YShrinkInvokerImpl").newInstance();
    } catch (InstantiationException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    } catch (IllegalAccessException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    } catch (ClassNotFoundException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    }

    if (null == yShrinkInvoker) {
      return;
    }

    yShrinkInvoker.setContext((Task) this);

    tempJars = new File[pairs.size()];
    File[] outJars = new File[pairs.size()];

    for (int i = 0; i < tempJars.length; i++) {
      try {
        tempJars[i] = File.createTempFile("tempJar_", "_shrinked.jar", new File(((InOutPair) pairs.get(i)).getOut().getParent()));
      } catch (IOException e) {
        getProject().log("Could not create tempfile for shrinking " + tempJars[i] + ".", Project.MSG_ERR);
        tempJars[i] = null;
      }

      if (null != tempJars[i]) {
        System.out.println("temp-jar: " + tempJars[i]);
        ShrinkBag pair = ((ShrinkBag) pairs.get(i));
        outJars[i] = pair.getOut();
        pair.setOut(tempJars[i]);
        yShrinkInvoker.addPair(pair);
      }
    }

    yShrinkInvoker.setResourceClassPath(resourceClassPath);

    if (shrinkLog != null) {
      yShrinkInvoker.setLogFile(shrinkLog);
    }

    if (null != entryPoints) {
      yShrinkInvoker.setEntyPoints(entryPoints);
    }

    if (null != expose && useExposeAsEntryPoints) {
      for (ClassSection cs : (List<ClassSection>) expose.getClasses()) {
        yShrinkInvoker.addClassSection(cs);
      }
      for (MethodSection ms : (List<MethodSection>) expose.getMethods()) {
        yShrinkInvoker.addMethodSection(ms);
      }
      for (FieldSection fs : (List<FieldSection>) expose.getFields()) {
        yShrinkInvoker.addFieldSection(fs);
      }
    }

    yShrinkInvoker.execute();

    for (int i = 0; i < tempJars.length; i++) {
      if (null != tempJars[i]) {
        InOutPair pair = ((InOutPair) pairs.get(i));
        pair.setIn(tempJars[i]);
        pair.setOut(outJars[i]);
      }
    }
  }

  /**
   * Add inheritance entries.
   *
   * @param entries the entries
   * @throws IOException the io exception
   */
  public void addInheritanceEntries( Collection entries ) throws IOException {

    if (!needYShrinkModel || expose == null) {
      return;
    }

    yShrinkModel = null;

    try {
      yShrinkModel = (YShrinkModel) Class.forName("com.yworks.yshrink.YShrinkModelImpl").newInstance();
    } catch (InstantiationException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    } catch (IllegalAccessException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    } catch (ClassNotFoundException e) {
      throw new BuildException(NO_SHRINKING_SUPPORT, e);
    }

    if (null == yShrinkModel) {
      return;
    }

    if (this.resourceClassPath != null) {
      yShrinkModel.setResourceClassPath(this.resourceClassPath, this);
    }

    yShrinkModel.createSimpleModel((List<ShrinkBag>) pairs);

    for (String className : yShrinkModel.getAllClassNames()) {

      Set<String> allAncestorClasses = yShrinkModel.getAllAncestorClasses(className);
      Set<String> allInterfaces = yShrinkModel.getAllImplementedInterfaces(className);

      for (ClassSection cs : (List<ClassSection>) expose.getClasses()) {

        if (null != cs.getExtends()) {
          String extendsName = cs.getExtends();
          if (extendsName.equals(className)) {
            //System.out.println( extendsName + " equals "+className );
            cs.addEntries(entries, className);
          } else {
            if (allAncestorClasses.contains(extendsName)) {
              cs.addEntries(entries, className);
              //System.out.println( extendsName + " extends "+className );
            }
          }
        }

        if (null != cs.getImplements()) {
          String interfaceName = cs.getImplements();
          if (interfaceName.equals(className)) {
            //System.out.println( interfaceName + " equals "+className );
            cs.addEntries(entries, className);
          } else {
            if (allInterfaces.contains(interfaceName)) {
              cs.addEntries(entries, className);
              //System.out.println( interfaceName + " implements "+className );
            }
          }
        }
      }
    }
  }

  /**
   * Sets shrink.
   *
   * @param doShrink the do shrink
   */
  public void setShrink( boolean doShrink ) {
    if (mode == MODE_STANDALONE) {
      this.doShrink = doShrink;
    } else {
      throw new BuildException(
              "The shrink attribute is not supported when the obfuscate task is nested inside a yguard task.\n Use a separate nested shrink task instead.");
    }
  }

  /**
   * Sets shrink log.
   *
   * @param shrinkLog the shrink log
   */
  public void setShrinkLog( File shrinkLog ) {
    this.shrinkLog = shrinkLog;
  }

  /**
   * Sets use expose as entry points.
   *
   * @param useExposeAsEntryPoints the use expose as entry points
   */
  public void setUseExposeAsEntryPoints( boolean useExposeAsEntryPoints ) {
    this.useExposeAsEntryPoints = useExposeAsEntryPoints;
  }

  /**
   * The type Resource adjuster.
   */
  protected class ResourceAdjuster implements ResourceHandler {
    /**
     * The Db.
     */
    protected final GuardDB db;

    /**
     * Instantiates a new Resource adjuster.
     *
     * @param db the db
     */
    protected ResourceAdjuster( final GuardDB db ) {
      this.db = db;
    }

    public boolean filterName( final String inName, final StringBuffer outName ) {
      for (AdjustSection as : adjustSections) {
        if (as.contains(inName)) {
          filterNameImpl(inName, outName, as);
          return true;
        }
      }
      return false;
    }

    private void filterNameImpl(
            final String inName, final StringBuffer outName, final AdjustSection as
    ) {
      outName.setLength(0);
      final ReplacePathPolicy policy = as.getReplacePathPolicy();
      if (ReplacePathPolicy.file == policy || ReplacePathPolicy.name == policy) {
        translateImpl(
                inName, outName,
                ResourceAdjusterUtils.newTranslateServiceFile(db, true),
                ResourceAdjusterUtils.newTranslateJavaFile(db, true));

        if (ReplacePathPolicy.name == policy) {
          String outPath = inName.substring(0, inName.lastIndexOf('/') + 1);
          String outFile = outName.toString();
          outFile = outFile.substring(outFile.lastIndexOf('/') + 1);
          outName.setLength(0);
          outName.append(outPath);
          outName.append(outFile);
        }
      } else {
        if (ReplacePathPolicy.fileorpath == policy) {
          translateImpl(
                  inName, outName,
                  ResourceAdjusterUtils.newTranslateServiceFile(db, true),
                  ResourceAdjusterUtils.newTranslateJavaFileOrPath(db));
        } else if (ReplacePathPolicy.path == policy) {
          outName.append(db.getOutName(inName));
        } else if (ReplacePathPolicy.lenient == policy) {
          translateImpl(
                  inName, outName,
                  ResourceAdjusterUtils.newTranslateServiceFile(db, false),
                  ResourceAdjusterUtils.newTranslateJavaFile(db, false));
        } else {
          outName.append(inName);
        }
      }
    }

    private void translateImpl(
            final String inName,
            final StringBuffer outName,
            final Function<String, String> mapService,
            final Function<String, String> mapOther
    ) {
      final String servicesPrefix = "META-INF/services/";
      if (inName.startsWith(servicesPrefix)) {
        // the file name of a service is a fully qualified class name
        final String cn = inName.substring(servicesPrefix.length());
        outName.append(servicesPrefix);
        // translateJavaFile returns a path name, replacing file separators
        // with dots converts that path name back into a qualified class
        // name (which is the required file name for a service)
        outName.append(mapService.apply(cn));
      } else {
        int index = 0;
        if (inName.endsWith(".properties")) {
          index = inName.indexOf('_');
        }
        if (index <= 0) {
          index = inName.indexOf('.');
        }
        String prefix = inName.substring(0, index);
        prefix = mapOther.apply(prefix);
        outName.append(prefix);
        outName.append(inName.substring(index));
      }
    }

    public boolean filterContent( InputStream in, OutputStream out, String resourceName ) throws IOException {
      for (AdjustSection as : adjustSections) {
        if (filterContentImpl(in, out, resourceName, as)) {
          return true;
        }
      }
      return false;
    }

    /**
     * Performs the content filtering for one Adjust section,
     * subclasses may provide custom implementations.
     *
     * @return {@code true} to terminate filtering once filtering performed
     */
    protected boolean filterContentImpl( InputStream in, OutputStream out, String resourceName, AdjustSection as ) throws IOException {
      final ReplaceContentPolicy policy = as.getReplaceContentPolicy();
      if (as.contains(resourceName) && ReplaceContentPolicy.none != policy) {
        final String sep = as.getReplaceContentSeparator();
        final Function<String, String> map =
                newTranslateMapping(db, sep, ReplaceContentPolicy.strict == policy);

        Writer writer = new OutputStreamWriter(out);
        newContentReplacer(sep).replace(new InputStreamReader(in), writer, map);
        writer.flush();
        return true;
      }
      return false;
    }

    public String filterString( String in, String resourceName ) throws IOException {
      StringBuffer result = new StringBuffer(in.length());
      newContentReplacer(".").replace(in, result, newTranslateJavaClass(db));
      return result.toString();
    }

    protected StringReplacer newContentReplacer( final String separator ) {
      return new StringReplacer(newContentPattern(separator));
    }
  }

  ;

  private static Function<String, String> newTranslateJavaClass( final GuardDB db ) {
    return ResourceAdjusterUtils.newTranslateJavaClass(db);
  }

  private static Function<String, String> newTranslateMapping(
          final GuardDB db, final String sep, final boolean strict
  ) {
    return ResourceAdjusterUtils.newTranslateMapping(db, sep, strict);
  }

  private static String newContentPattern( final String separator ) {
    return ResourceAdjusterUtils.newContentPattern(separator);
  }


  /**
   * Accepts classes and their nested classes.
   */
  private static final class ClassFileFilter implements Filter {
    private final com.yworks.util.Filter parent;

    /**
     * Instantiates a new Class file filter.
     *
     * @param parent the parent
     */
    ClassFileFilter( com.yworks.util.Filter parent ) {
      this.parent = parent;
    }

    public boolean accepts( Object o ) {
      String s = (String) o;
      if (s.endsWith(".class") && s.indexOf('$') != -1) {
        s = s.substring(0, s.indexOf('$')) + ".class";
      }
      return parent.accepts(s);
    }
  }

  private final class TaskLogger extends Logger {

    private PrintWriter writer;

    /**
     * Instantiates a new Task logger.
     */
    TaskLogger() {
      super();
    }

    /**
     * Sets writer.
     *
     * @param writer the writer
     */
    void setWriter( PrintWriter writer ) {
      this.writer = writer;
    }

    public void warning( String message ) {
      getProject().log(ObfuscatorTask.this, "WARNING: " + message, Project.MSG_WARN);

    }

    public void warningToLogfile( String message ) {
      if (null != writer) {
        writer.println("<!-- " + "WARNING: " + message + " -->");
      }
    }

    public void log( String message ) {
      getProject().log(ObfuscatorTask.this, message, Project.MSG_INFO);
    }

    public void error( String message ) {
      getProject().log(ObfuscatorTask.this, "ERROR: " + message, Project.MSG_ERR);
    }
  }

  private void writeLogHeader( PrintWriter log, File[] inFile, File[] outFile ) {
    log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    log.println("<yguard version=\"" + "1.5" + "\">");
    log.println("<!--");
    log.println(LOG_TITLE_PRE_VERSION + Version.getVersion() + LOG_TITLE_POST_VERSION);
    log.println();
    log.println(LOG_CREATED + new Date().toString());
    log.println();
    for (int i = 0; i < inFile.length; i++) {
      log.println(LOG_INPUT_FILE + inFile[i].getName());
      log.println(LOG_OUTPUT_FILE + outFile[i].getName());
      log.println();
    }
    log.println("-->");
  }

  private static final class LogListener implements ObfuscationListener {

    private Project p;

    /**
     * Instantiates a new Log listener.
     *
     * @param p the p
     */
    LogListener( Project p ) {
      this.p = p;
    }

    public void obfuscatingClass( String className ) {
      p.log("Obfuscating class " + className, Project.MSG_VERBOSE);
    }

    public void obfuscatingJar( String inJar, String outJar ) {
      p.log("Obfuscating Jar " + inJar + " to " + outJar);
    }

    public void parsingClass( String className ) {
      p.log("Parsing class " + className, Project.MSG_VERBOSE);
    }

    public void parsingJar( String jar ) {
      p.log("Parsing jar " + jar);
    }
  }

  private void writeLogFooter( PrintWriter log ) {
    log.println("</yguard>");
  }

  private static final class YGuardNameFactory extends NameMakerFactory.DefaultNameMakerFactory {
    private static String legalFirstChars;
    private static String legalChars;
    private static String crazylegalFirstChars;
    private static String crazylegalChars;
    private static String asciiFirstChars;
    private static String asciiChars;
    private static String asciiLowerChars;

    private static AtomicBoolean scrambled = new AtomicBoolean(false);

    private String packagePrefix;

    static {
      StringBuffer legalC = new StringBuffer(500);
      StringBuffer illegalC = new StringBuffer(500);
      StringBuffer crazyLegalC = new StringBuffer(500);
      StringBuffer asciiC = new StringBuffer(500);
      StringBuffer asciiLC = new StringBuffer(100);
      StringBuffer asciiUC = new StringBuffer(100);

      BitSet[] bs = null;

      try {
        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(ObfuscatorTask.class.getResourceAsStream("jdks.bits")));
        bs = (BitSet[]) ois.readObject();
        ois.close();
      } catch (IOException ioe) {
        throw new InternalError("Could not load valid character bitset!" + ioe.getMessage());
      } catch (ClassNotFoundException cnfe) {
        throw new InternalError("Could not load valid character bitset!" + cnfe.getMessage());
      }

      for (char i = 0; i < Character.MAX_VALUE; i++) {
        if (!bs[0].get((int) i)) {
          illegalC.append(i);
        } else {
          legalC.append(i);
          if (i > 255) {
            crazyLegalC.append(i);
          } else if (i < 128) {
            asciiC.append(i);
            if (Character.isLowerCase(i)) {
              asciiLC.append(i);
            } else {
              asciiUC.append(i);
            }
          }
        }
      }

      legalFirstChars = legalC.toString();
      crazylegalFirstChars = crazyLegalC.toString();
      asciiLowerChars = asciiLC.toString();
      asciiFirstChars = asciiC.toString();

      legalC.setLength(0);
      illegalC.setLength(0);
      crazyLegalC.setLength(0);
      for (char i = 0; i < Character.MAX_VALUE; i++) {

        if (!bs[1].get((int) i)) {
          illegalC.append(i);
        } else {
          legalC.append(i);
          if (i > 255) {
            crazyLegalC.append(i);
          } else if (i < 128) {
            asciiC.append(i);
          }
        }
      }
      legalChars = legalC.toString();
      crazylegalChars = crazyLegalC.toString();
      asciiChars = asciiC.toString();
    }

    /**
     * The Mode.
     */
    int mode;
    /**
     * The Legal.
     */
    static final int LEGAL = 0;
    /**
     * The Compatible.
     */
    static final int COMPATIBLE = 1;
    /**
     * The Illegal.
     */
    static final int ILLEGAL = 2;
    /**
     * The Small.
     */
    static final int SMALL = 4;
    /**
     * The Mix.
     */
    static final int MIX = 12;
    /**
     * The Best.
     */
    static final int BEST = 8;

    /**
     * The Overload enabled.
     */
    boolean overloadEnabled = true;

    /**
     * Instantiates a new Y guard name factory.
     */
    YGuardNameFactory() {
      this(LEGAL | SMALL);
    }

    /**
     * Instantiates a new Y guard name factory.
     *
     * @param mode the mode
     */
    YGuardNameFactory( int mode ) {
      super.setInstance(this);
      this.mode = mode;
    }

    private static void scramble() {
      if (scrambled.compareAndSet(false, true)) {
        asciiChars = scrambleChars(asciiChars);
        asciiFirstChars = scrambleChars(asciiFirstChars);
        legalChars = scrambleChars(legalChars);
        legalFirstChars = scrambleChars(legalFirstChars);
        crazylegalChars = scrambleChars(crazylegalChars);
        crazylegalFirstChars = scrambleChars(crazylegalFirstChars);
      }
    }

    private static String scrambleChars( String string ) {
      char[] chars = string.toCharArray();
      Random r = new Random();  // Random number generator

      for (int c = 0; c < chars.length; c++) {
        int randomPosition = r.nextInt(chars.length);
        char original = chars[c];
        char random = chars[randomPosition];
        chars[c] = random;
        chars[randomPosition] = original;
      }
      StringBuilder sb = new StringBuilder();
      for (char c : chars) {
        sb.append(c);
      }
      return sb.toString();
    }

    /**
     * Is overload enabled boolean.
     *
     * @return the boolean
     */
    public boolean isOverloadEnabled() {
      return overloadEnabled;
    }

    /**
     * Sets overload enabled.
     *
     * @param overloadEnabled the overload enabled
     */
    public void setOverloadEnabled( boolean overloadEnabled ) {
      this.overloadEnabled = overloadEnabled;
    }

    /**
     * Set package prefix.
     *
     * @param prefix the prefix
     */
    void setPackagePrefix( String prefix ) {
      this.packagePrefix = prefix;
      if (packagePrefix != null) {
        packagePrefix = packagePrefix.replace('.', '/') + '/';
      }
    }

    protected NameMaker createFieldNameMaker( String[] reservedNames, String fqClassName ) {
      switch (mode) {
        default:
        case COMPATIBLE + SMALL:
          LongNameMaker longNameMaker1 = new LongNameMaker(reservedNames,
                                                           asciiLowerChars, asciiLowerChars, 1);
          longNameMaker1.setOverloadEnabled(overloadEnabled);
          return longNameMaker1;
        case LEGAL + SMALL:
          LongNameMaker longNameMaker2 = new LongNameMaker(reservedNames,
                                                           legalFirstChars, legalChars, 1);
          longNameMaker2.setOverloadEnabled(overloadEnabled);
          return longNameMaker2;
        case COMPATIBLE + MIX:
        case LEGAL + MIX:
          AbstractNameMaker nm1 = new LongNameMaker(reservedNames, false, 6);
          AbstractNameMaker nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames);
          MixNameMaker mixNameMaker1 = new MixNameMaker(null, reservedNames, nm1,
                                                        3);
          MixNameMaker mnm = mixNameMaker1;
          mnm.add(nm2, 1);
          mnm.setOverloadEnabled(overloadEnabled);
          return mnm;
        case COMPATIBLE + BEST:
        case LEGAL + BEST:
          LongNameMaker longNameMaker4 = new LongNameMaker(reservedNames, false,
                                                           256);
          KeywordNameMaker keywordNameMaker1 = new KeywordNameMaker(reservedNames);
          CompoundNameMaker compoundNameMaker1 = new CompoundNameMaker(
                  longNameMaker4,
                  keywordNameMaker1);
          longNameMaker4.setOverloadEnabled(overloadEnabled);
          keywordNameMaker1.setOverloadEnabled(overloadEnabled);
          return compoundNameMaker1;
        case ILLEGAL + SMALL:
          LongNameMaker longNameMaker3 = new LongNameMaker(reservedNames,
                                                           crazylegalFirstChars, crazylegalChars, 1);
          longNameMaker3.setOverloadEnabled(overloadEnabled);
          return longNameMaker3;
        case ILLEGAL + MIX:
          nm1 = new LongNameMaker(reservedNames, false, 6);
          nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames,
                                                    KeywordNameMaker.KEYWORDS,
                                                    KeywordNameMaker.SPACER);
          MixNameMaker mixNameMaker2 = new MixNameMaker(null, reservedNames, nm1,
                                                        2);
          mixNameMaker2.add(nm2, 1);
          mixNameMaker2.setOverloadEnabled(overloadEnabled);
          return mixNameMaker2;
        case ILLEGAL + BEST:
          nm1 = new ObfuscatorTask.KeywordNameMaker(reservedNames,
                                                    KeywordNameMaker.KEYWORDS,
                                                    KeywordNameMaker.SPACER);
          nm2 = new LongNameMaker(reservedNames, false, 256);
          mnm = new MixNameMaker(null, reservedNames, nm1,
                                 1);
          mnm.add(nm2, 1);
          mnm.setOverloadEnabled(overloadEnabled);
          return mnm;
      }
    }

    protected NameMaker createMethodNameMaker( String[] reservedNames, String fqClassName ) {
      return createFieldNameMaker(reservedNames, fqClassName);
    }

    protected NameMaker createPackageNameMaker( String[] reservedNames, String packageName ) {
      boolean topLevel = packageName.length() < 1;
      switch (mode) {
        default:
        case COMPATIBLE + SMALL:
        case COMPATIBLE + MIX:
        case COMPATIBLE + BEST:
          if (topLevel && packagePrefix != null) {
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, asciiLowerChars, asciiLowerChars, 1));
          } else {
            return new LongNameMaker(reservedNames,
                                     asciiLowerChars, asciiLowerChars, 1);
          }
        case LEGAL + SMALL:
        case ILLEGAL + SMALL:
          if (topLevel && packagePrefix != null) {
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, asciiFirstChars, asciiChars, 1));
          } else {
            return new LongNameMaker(reservedNames,
                                     asciiFirstChars, asciiChars, 1);
          }
        case LEGAL + MIX:
        case ILLEGAL + MIX:
          AbstractNameMaker nm1 = new LongNameMaker(reservedNames,
                                                    asciiFirstChars, asciiChars, 1);
          AbstractNameMaker nm3 = new LongNameMaker(reservedNames, true, 256);
          AbstractNameMaker nm4 = new LongNameMaker(reservedNames, true, 4);
          AbstractNameMaker nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames);
          MixNameMaker mnm = new MixNameMaker(topLevel ? packagePrefix : null, reservedNames, nm1, 8);
          mnm.add(nm4, 4);
          mnm.add(nm2, 4);
          mnm.add(nm3, 1);
          return mnm;
        case LEGAL + BEST:
        case ILLEGAL + BEST:
          if (topLevel && packagePrefix != null) {
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, true, 256));
          } else {
            return new LongNameMaker(reservedNames, true, 256);
          }
      }
    }

    protected NameMaker createClassNameMaker( String[] reservedNames, String fqClassName ) {
      return createPackageNameMaker(reservedNames, fqClassName);
    }

    protected NameMaker createInnerClassNameMaker( String[] reservedNames, String fqInnerClassName ) {
      switch (mode) {
        default:
        case COMPATIBLE + SMALL:
        case COMPATIBLE + MIX:
        case COMPATIBLE + BEST:
          return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, asciiLowerChars, asciiLowerChars, 1));
        case LEGAL + SMALL:
          return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, asciiFirstChars, asciiChars, 1));
        case LEGAL + MIX:
          return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, true, 1));
        case LEGAL + BEST:
          return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, true, 4));
        case ILLEGAL + SMALL:
          return new LongNameMaker(reservedNames, asciiFirstChars, asciiChars, 1);
        case ILLEGAL + MIX:
          return new LongNameMaker(reservedNames, true, 1);
        case ILLEGAL + BEST:
          return new LongNameMaker(reservedNames, true, 10);
      }
    }

    public String toString() {
      switch (mode) {
        default:
          return "yGuardNameFactory [naming-scheme: default; language-conformity: default]";
        case COMPATIBLE + SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: compatible]";
        case COMPATIBLE + MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: compatible]";
        case COMPATIBLE + BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: compatible]";
        case LEGAL + SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: legal]";
        case LEGAL + MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: legal]";
        case LEGAL + BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: legal]";
        case ILLEGAL + SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: illegal]";
        case ILLEGAL + MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: illegal]";
        case ILLEGAL + BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: illegal]";
      }
    }
  }

  /**
   * The type Compound name maker.
   */
  static class CompoundNameMaker implements NameMaker {
    private NameMaker nm1, nm2;

    /**
     * Instantiates a new Compound name maker.
     *
     * @param nm1 the nm 1
     * @param nm2 the nm 2
     */
    CompoundNameMaker( NameMaker nm1, NameMaker nm2 ) {
      this.nm1 = nm1;
      this.nm2 = nm2;
    }

    public String nextName( String descriptor ) {
      return nm1.nextName(descriptor) + nm2.nextName(descriptor);
    }
  }

  /**
   * The type Mix name maker.
   */
  static class MixNameMaker extends AbstractNameMaker {

    /**
     * The Name makers.
     */
    List nameMakers = new ArrayList();
    /**
     * The Prefix.
     */
    final String prefix;

    /**
     * Instantiates a new Mix name maker.
     *
     * @param prefix        the prefix
     * @param reservedNames the reserved names
     * @param delegate      the delegate
     * @param count         the count
     */
    MixNameMaker( String prefix, String[] reservedNames, AbstractNameMaker delegate, int count ) {
      super(reservedNames, "O0", 1);
      add(delegate, count);
      this.prefix = prefix;
    }

    /**
     * Add.
     *
     * @param delegate the delegate
     * @param count    the count
     */
    void add( AbstractNameMaker delegate, int count ) {
      count = count < 1 ? 1 : count;
      for (int i = 0; i < count; i++) {
        nameMakers.add(delegate);
      }
      Collections.shuffle(nameMakers);
    }

    String generateName( int i ) {
      if (prefix != null) {
        return prefix + ((AbstractNameMaker) nameMakers.get(i % nameMakers.size())).generateName(i);
      } else {
        return ((AbstractNameMaker) nameMakers.get(i % nameMakers.size())).generateName(i);
      }
    }
  }

  /**
   * The type Long name maker.
   */
  static final class LongNameMaker extends AbstractNameMaker {
    /**
     * The Chars.
     */
    String chars;
    /**
     * The First chars.
     */
    String firstChars;

    /**
     * Instantiates a new Long name maker.
     *
     * @param reservedNames the reserved names
     */
    LongNameMaker( String[] reservedNames ) {
      this(reservedNames, false, 256);
    }

    /**
     * Instantiates a new Long name maker.
     *
     * @param reservedNames the reserved names
     * @param ascii         the ascii
     * @param length        the length
     */
    LongNameMaker( String[] reservedNames, boolean ascii, int length ) {
      this(reservedNames, ascii ? "Oo" : "Oo\u00D2\u00D3\u00D4\u00D5\u00D6\u00D8\u00F4\u00F5\u00F6\u00F8",
           ascii ? "Oo0" : "0Oo\u00D2\u00D3\u00D4\u00D5\u00D6\u00D8\u00F4\u00F5\u00F6\u00F8", length);
    }

    /**
     * Instantiates a new Long name maker.
     *
     * @param reservedNames the reserved names
     * @param firstChars    the first chars
     * @param chars         the chars
     * @param minLength     the min length
     */
    LongNameMaker( String[] reservedNames, String firstChars, String chars, int minLength ) {
      super(reservedNames, null, minLength);
      this.chars = chars;
      if (chars == null || chars.length() < 1) {
        throw new IllegalArgumentException("must specify at least one character!");
      }
      this.firstChars = firstChars;
      if (firstChars != null && firstChars.length() < 1) {
        this.firstChars = null;
      }
    }

    String generateName( int i ) {
      StringBuffer sb = new StringBuffer(20);
      int tmp = i;
      if (firstChars != null) {
        sb.append(firstChars.charAt(tmp % firstChars.length()));
        if (firstChars.length() > 1) {
          tmp = tmp / firstChars.length();
        } else {
          tmp--;
        }
      }
      while (tmp > 0) {
        sb.append(chars.charAt(tmp % chars.length()));
        if (chars.length() > 1) {
          tmp = tmp / chars.length();
        } else {
          tmp--;
        }
      }
      if (chars.length() > 1) {
        while (sb.length() < minLength) {
          sb.append(chars.charAt(0));
        }
      }
      return sb.toString();
    }
  }

  /**
   * The type Keyword name maker.
   */
  static final class KeywordNameMaker extends AbstractNameMaker {
    /**
     * The Keywords.
     */
    static final String[] KEYWORDS = new String[]{
            "this", "super", "new", "Object", "String", "class", "return", "void", "null", "int",
            "if", "float", "for", "do", "while", "public", "private", "interface",};
    /**
     * The Spacer.
     */
    static final String[] SPACER = new String[]{
            ".", "$", " ", "_",
            };

    /**
     * The Nospacer.
     */
    static final String[] NOSPACER = new String[]{""};

    /**
     * The Chars.
     */
    String chars;
    /**
     * The Key words.
     */
    String[] keyWords;
    /**
     * The Spacer.
     */
    String spacer[];

    /**
     * Instantiates a new Keyword name maker.
     *
     * @param reservedNames the reserved names
     */
    KeywordNameMaker( String[] reservedNames ) {
      this(reservedNames, KEYWORDS, NOSPACER);
    }

    /**
     * Instantiates a new Keyword name maker.
     *
     * @param reservedNames the reserved names
     * @param keyWords      the key words
     * @param spacer        the spacer
     */
    KeywordNameMaker( String[] reservedNames, String[] keyWords, String[] spacer ) {
      super(reservedNames, "Oo0", 0);
      this.keyWords = keyWords;
      this.spacer = spacer;
    }

    String generateName( int i ) {
      StringBuffer sb = new StringBuffer(30);
      int tmp = i;
      int sc = 0;
      while (tmp > 0) {
        sb.append(keyWords[tmp % keyWords.length]);
        tmp = tmp / keyWords.length;
        if (tmp > 0) {
          sb.append(spacer[sc % spacer.length]);
          sc++;
        }
      }
      return sb.toString();
    }
  }

  /**
   * The type Prefix name maker.
   */
  static final class PrefixNameMaker extends AbstractNameMaker {
    private String prefix;
    private AbstractNameMaker delegate;

    /**
     * Instantiates a new Prefix name maker.
     *
     * @param prefix        the prefix
     * @param reservedNames the reserved names
     * @param delegate      the delegate
     */
    PrefixNameMaker( String prefix, String[] reservedNames, AbstractNameMaker delegate ) {
      super(reservedNames, "O0", 1);
      this.prefix = prefix;
      this.delegate = delegate;
    }

    String generateName( int i ) {
      return prefix + delegate.generateName(i);
    }
  }

  /**
   * The type Abstract name maker.
   */
  static abstract class AbstractNameMaker implements NameMaker {
    /**
     * The Reserved names.
     */
    Set reservedNames;
    /**
     * The Count map.
     */
    Map countMap = new HashMap();
    /**
     * The Fill chars.
     */
    String fillChars;
    /**
     * The Min length.
     */
    int minLength;
    private static final String DUMMY = "(com.dummy.Dummy)";

    /**
     * The Overload enabled.
     */
    protected boolean overloadEnabled = true;
    private int counter = 1;

    /**
     * Is overload enabled boolean.
     *
     * @return the boolean
     */
    public boolean isOverloadEnabled() {
      return overloadEnabled;
    }

    /**
     * Sets overload enabled.
     *
     * @param overloadEnabled the overload enabled
     */
    public void setOverloadEnabled( boolean overloadEnabled ) {
      this.overloadEnabled = overloadEnabled;
    }


    /**
     * Instantiates a new Abstract name maker.
     *
     * @param reservedNames the reserved names
     */
    AbstractNameMaker( String[] reservedNames ) {
      this(reservedNames, "0o", 256);
    }

    /**
     * Instantiates a new Abstract name maker.
     *
     * @param reservedNames the reserved names
     * @param fillChars     the fill chars
     * @param minLength     the min length
     */
    AbstractNameMaker( String[] reservedNames, String fillChars, int minLength ) {
      if (reservedNames != null && reservedNames.length > 0) {
        this.reservedNames = new HashSet(Arrays.asList(reservedNames));
      } else {
        this.reservedNames = Collections.EMPTY_SET;
      }
      this.minLength = minLength;
      this.fillChars = fillChars != null ? fillChars : "0O";
    }

    /**
     * Return the next unique name for this namespace, differing only for identical arg-lists.
     */
    public String nextName( String descriptor ) {
      if (descriptor == null) {
        descriptor = DUMMY;
      }
      int j;

      if (overloadEnabled) {
        descriptor = descriptor.substring(0, descriptor.lastIndexOf(')'));
        Integer i = (Integer) countMap.get(descriptor);
        if (i == null) {
          i = new Integer(1);
        }
        j = i.intValue();
      } else {
        j = counter;
      }
      String result = null;
      StringBuffer sb = new StringBuffer(minLength > 10 ? minLength + 20 : 20);
      do {
        sb.setLength(0);
        String name = generateName(j);
        sb.append(name);
        if (sb.length() < minLength) {
          while (sb.length() < minLength) {
            sb.append(fillChars);
          }
          sb.setLength(minLength);
        }
        result = sb.toString();
        j++;
      } while (reservedNames.contains(result));
      if (overloadEnabled) {
        countMap.put(descriptor, new Integer(j));
      } else {
        counter = j;
      }
      return result;
    }

    /**
     * Generate name string.
     *
     * @param i the
     * @return the string
     */
    abstract String generateName( int i );
  }

  /**
   * The type Resource cp resolver.
   */
  static final class ResourceCpResolver implements ClassResolver {
    /**
     * The Resource.
     */
    Path resource;
    /**
     * The Url class loader.
     */
    URLClassLoader urlClassLoader;

    /**
     * Instantiates a new Resource cp resolver.
     *
     * @param resources the resources
     * @param target    the target
     */
    ResourceCpResolver( Path resources, Task target ) {
      this.resource = resources;
      String[] list = resources.list();
      List listUrls = new ArrayList();
      for (int i = 0; i < list.length; i++) {
        try {
          URL url = new File(list[i]).toURL();
          listUrls.add(url);
        } catch (MalformedURLException mfue) {
          target.getProject().log(target, "Could not resolve resource: " + mfue, Project.MSG_WARN);
        }
      }
      URL[] urls = new URL[listUrls.size()];
      listUrls.toArray(urls);
      urlClassLoader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());
    }

    public Class resolve( String className ) throws ClassNotFoundException {
      try {
        return Class.forName(className, false, urlClassLoader);
      } catch (NoClassDefFoundError ncdfe) {
        String message = ncdfe.getMessage();
        if (message == null || message.equals(className)) {
          message = className;
        } else {
          message = message + "[" + className + "]";
        }
        throw new ClassNotFoundException(message, ncdfe);
      } catch (LinkageError le) {
        throw new ClassNotFoundException(className, le);
      }
    }

    @Override
    public void close() throws Exception {
      urlClassLoader.close();
    }
  }

  /**
   * Setter for property replaceClassNameStrings.
   *
   * @param replaceClassNameStrings New value of property replaceClassNameStrings.
   */
  public void setReplaceClassNameStrings( boolean replaceClassNameStrings ) {
    this.replaceClassNameStrings = replaceClassNameStrings;
  }

  /**
   * Sets scramble.
   *
   * @param scramble the scramble
   */
  public void setScramble( boolean scramble ) {
    if (scramble) {
      YGuardNameFactory.scramble();
      com.yworks.yguard.obf.KeywordNameMaker.scramble();
    }
  }

  /**
   * The type My line number table mapper.
   */
  public static final class MyLineNumberTableMapper implements com.yworks.yguard.obf.LineNumberTableMapper {
    private long salt;
    private LineNumberScrambler last;
    private long lastSeed;
    private Set classNames = new HashSet();

    /**
     * Instantiates a new My line number table mapper.
     *
     * @param salt the salt
     */
    public MyLineNumberTableMapper( long salt ) {
      this.salt = salt;
      this.last = new LineNumberScrambler(3584, lastSeed);
    }

    public boolean mapLineNumberTable( String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable ) {
      final String javaClassName = className.replace('/', '.').replace('$', '.');
      classNames.add(className.replace('/', '.'));
      long seed = salt ^ javaClassName.hashCode();
      LineNumberScrambler scrambler;
      if (seed == lastSeed) {
        scrambler = last;
      } else {
        scrambler = last = new LineNumberScrambler(3584, seed);
        lastSeed = seed;
      }
      for (int i = 0; i < lineNumberTable.getLineNumberTable().length; i++) {
        LineNumberInfo lineNumberInfo = lineNumberTable.getLineNumberTable()[i];
        lineNumberInfo.setLineNumber(scrambler.scramble(lineNumberInfo.getLineNumber()));
      }
      return true;
    }

    public void logProperties( PrintWriter pw ) {
      if (!classNames.isEmpty()) {
        for (Iterator it = classNames.iterator(); it.hasNext(); ) {
          pw.println("<property owner=\"" + ClassTree.toUtf8XmlString(it.next().toString()) + "\" name=\"scrambling-salt\" value=\"" + Long.toString(salt) + "\"/>");
        }
        classNames.clear();
      }
    }
  }

  /**
   * The type Line number squeezer.
   */
  public static final class LineNumberSqueezer implements LineNumberTableMapper {
    private List squeezedNumbers = new ArrayList();

    public boolean mapLineNumberTable( String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable ) {
      final LineNumberInfo[] table = lineNumberTable.getLineNumberTable();
      if (table.length > 0) {
        final LineNumberInfo lineNumberInfo = new LineNumberInfo(table[0].getStartPC(), table[0].getLineNumber());
        lineNumberTable.setLineNumberTable(new LineNumberInfo[]{lineNumberInfo});
        squeezedNumbers.add(new Object[]{className, methodName, methodSignature, lineNumberInfo});
        return true;
      }
      return false;
    }

    public void logProperties( PrintWriter pw ) {
      if (!squeezedNumbers.isEmpty()) {
        for (Iterator it = squeezedNumbers.iterator(); it.hasNext(); ) {
          Object[] ar = (Object[]) it.next();
          String className = ar[0].toString();
          String methodName = ar[1].toString();
          String methodSignature = ar[2].toString();
          int line = ((LineNumberInfo) ar[3]).getLineNumber();
          pw.println("<property owner=\"" + ClassTree.toUtf8XmlString(Conversion.toJavaClass(className)) + "#" + ClassTree.toUtf8XmlString(Conversion.toJavaMethod(methodName, methodSignature)) + "\" name=\"squeezed-linenumber\" value=\"" + line + "\"/>");
        }
        squeezedNumbers.clear();
      }
    }
  }

  /**
   * The type Line number scrambler.
   */
  public static final class LineNumberScrambler {
    private int[] scrambled;
    private int[] unscrambled;

    /**
     * Instantiates a new Line number scrambler.
     *
     * @param size the size
     * @param seed the seed
     */
    public LineNumberScrambler( int size, long seed ) {
      this.scrambled = new int[size];
      this.unscrambled = new int[size];
      for (int i = 0; i < size; i++) {
        this.scrambled[i] = i;
        this.unscrambled[i] = i;
      }
      Random r = new Random(seed);
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < size; j++) {
          int otherIndex = r.nextInt(size);
          if (otherIndex != j) {
            int pos1 = this.scrambled[j];
            int pos2 = this.scrambled[otherIndex];

            int p1 = this.unscrambled[pos1];
            int p2 = this.unscrambled[pos2];
            this.unscrambled[pos1] = p2;
            this.unscrambled[pos2] = p1;

            this.scrambled[j] = pos2;
            this.scrambled[otherIndex] = pos1;
          }
        }
      }
    }

    /**
     * Scramble int.
     *
     * @param i the
     * @return the int
     */
    public int scramble( int i ) {
      if (i >= scrambled.length) {
        return scrambled[i % scrambled.length] + (i / scrambled.length) * scrambled.length;
      } else {
        return scrambled[i];
      }
    }

    /**
     * Unscramble int.
     *
     * @param i the
     * @return the int
     */
    public int unscramble( int i ) {
      if (i >= scrambled.length) {
        return unscrambled[i % scrambled.length] + (i / scrambled.length) * scrambled.length;
      } else {
        return unscrambled[i];
      }
    }
  }

  /**
   * Main.
   *
   * @param args the args
   */
  public static void main( String[] args ) {
    new LineNumberScrambler(2000, 234432);
  }

  private String annotationClass;

  /**
   * Gets annotation class.
   *
   * @return the annotation class
   */
  public String getAnnotationClass() {
    return annotationClass;
  }

  /**
   * Sets annotation class.
   *
   * @param annotationClass the annotation class
   */
  public void setAnnotationClass( String annotationClass ) {
    this.annotationClass = annotationClass;
  }

  /*
   * IMPORTANT: Do not change the casing of the enum values.
   * These names are used as XML attribute names and should be all lowercase.
   */
  public enum ReplaceContentPolicy {
    /**
     * No content adjustment at all.
     */
    none,
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then text in resource files will be adjusted as follows
     * com.yworks.SampleStuff -&gt; A.A.SampleStuff
     * com.other.OtherStuff -&gt; A.other.OtherStuff
     */
    lenient,
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then text in resource files will be adjusted as follows
     * com.yworks.SampleClass -&gt; A.A.A
     */
    strict;
  }

  /*
   * IMPORTANT: Do not change the casing of the enum values.
   * These names are used as XML attribute names and should be all lowercase.
   */
  public enum ReplacePathPolicy {
    /**
     * No path adjustment at all.
     */
    none, // replaceName = false, replacePath = false
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then resource files will be renamed as follows
     * com/yworks/SampleStuff.properties -&gt; A/A/SampleStuff.properties
     * com/other/OtherStuff.properties -&gt; A/other/OtherStuff.properties
     */
    path, // replaceName = false, replacePath = true, default
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then resource files will be renamed as follows
     * com/yworks/SampleStuff.properties -&gt; com/yworks/A.properties
     * com/other/OtherStuff.properties -&gt; com/other/OtherStuff.properties
     */
    name, // replaceName = true, replacePath = false
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then resource files will be renamed as follows
     * com/yworks/SampleStuff.properties -&gt; A/A/A.properties
     * com/other/OtherStuff.properties -&gt; com/other/OtherStuff.properties
     */
    file, // replaceName = true, replacePath = true
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then resource files will be renamed as follows
     * com/yworks/SampleStuff.properties -&gt; A/A/A.properties
     * com/other/OtherStuff.properties -&gt; A/other/OtherStuff.properties
     */
    fileorpath,
    /**
     * If class obfuscation yields
     * com.yworks.SampleClass -&gt; A.A.A
     * then resource files will be renamed as follows
     * com/yworks/SampleStuff.properties -&gt; A/A/A.properties
     * com/other/OtherStuff.properties -&gt; A/other/OtherStuff.properties
     */
    lenient
  }
}
