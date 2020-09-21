/*
 * ObfuscatorTask.java
 *
 * Created on October 10, 2002, 5:30 PM
 */
package com.yworks.yguard;

import com.yworks.util.CollectionFilter;
import com.yworks.util.ant.ZipScannerTool;
import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.ExposeSection;
import com.yworks.yguard.ant.FieldSection;
import com.yworks.yguard.ant.MapParser;
import com.yworks.yguard.ant.Mappable;
import com.yworks.yguard.ant.MethodSection;
import com.yworks.yguard.ant.PackageSection;
import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.AttributesSection;
import com.yworks.common.ant.EntryPointsSection;
import com.yworks.common.ant.Exclude;
import com.yworks.common.ant.InOutPair;
import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.YGuardBaseTask;
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
import com.yworks.yguard.obf.Version;
import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.obf.classfile.LineNumberInfo;
import com.yworks.yguard.obf.classfile.LineNumberTableAttrInfo;
import com.yworks.yguard.obf.classfile.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * The main obfuscation Ant Task
 * @author Sebastian Mueller, yWorks GmbH  (sebastian.mueller@yworks.com)
 */
public class ObfuscatorTask extends YGuardBaseTask
{

  //private List pairs = new ArrayList();
  private String mainClass;
  private boolean conserveManifest = false;
  private File logFile = new File("yguardlog.xml");
  private ExposeSection expose = null;
  private List adjustSections = new ArrayList();
  private MapSection map = null;
  private PatchSection patch = null;
  //private Path resourceClassPath;

  // shrinking attributes
  private boolean doShrink = false;
  private EntryPointsSection entryPoints = null;
  private File shrinkLog = null;
  private boolean useExposeAsEntryPoints = true;

  private static final String LOG_TITLE_PRE_VERSION = "  yGuard Bytecode Obfuscator, v";
  private static final String LOG_TITLE_POST_VERSION = ", a Product of yWorks GmbH - http://www.yworks.com";
  private static final String LOG_CREATED = "  Logfile created on ";
  private static final String LOG_INPUT_FILE =  "  Jar file to be obfuscated:           ";
  private static final String LOG_OUTPUT_FILE = "  Target Jar file for obfuscated code: ";

  private static final String NO_SHRINKING_SUPPORT = "No shrinking support found.";
  private static final String DEPRECATED  = "The obfuscate task is deprecated. Please use the new com.yworks.yguard.YGuardTask instead.";


  /** Holds value of property replaceClassNameStrings. */
  private boolean replaceClassNameStrings = true;
  private File[] tempJars;
  private boolean needYShrinkModel;
  private YShrinkModel yShrinkModel;

  public ObfuscatorTask() {
    super();
  }

  public ObfuscatorTask( boolean mode ) {
    super( mode );
  }

  private static String toNativePattern(String pattern){
    if (pattern.endsWith(".class")){
      return pattern;
    } else {
      if (pattern.endsWith("**")){
        return pattern.replace('.','/')+"/*.class";
      } else if (pattern.endsWith("*")){
        return pattern.replace('.','/')+".class";
      } else if ( pattern.endsWith( "." ) ) {
        return pattern.replace( '.', '/' ) + "**/*.class";
      } else {
        return pattern.replace( '.', '/' ) + ".class";
      }
    }
  }

  public static String[] toNativePattern(String[] patterns){
    if (patterns == null){
      return new String[0];
    } else {
      String[] res = new String[patterns.length];
      for (int i = 0; i < patterns.length; i++){
        res[i] = toNativePattern(patterns[i]);
      }
      return res;
    }
  }

  public static final String toNativeClass(String className){
      return className.replace('.','/');
  }

  public static final String[] toNativeMethod(String javaMethod){
      StringTokenizer tokenizer = new StringTokenizer(javaMethod, "(,[]) ", true);
      String tmp = tokenizer.nextToken();;
      while (tmp.trim().length() == 0){
          tmp = tokenizer.nextToken();
      }
      String returnType = tmp;
      tmp = tokenizer.nextToken();
      int retarraydim = 0;
      while (tmp.equals("[")){
          tmp = tokenizer.nextToken();
          if (!tmp.equals("]")) throw new IllegalArgumentException("']' expected but found "+tmp);
          retarraydim++;
          tmp = tokenizer.nextToken();
      }
    if ( tmp.trim().length() != 0 ) {
      throw new IllegalArgumentException( "space expected but found " + tmp );
    }
      tmp = tokenizer.nextToken();
      while (tmp.trim().length() == 0){
          tmp = tokenizer.nextToken();
      }
      String name = tmp;
      StringBuffer nativeMethod = new StringBuffer(30);
      nativeMethod.append('(');
      tmp = tokenizer.nextToken();
      while (tmp.trim().length() == 0){
          tmp = tokenizer.nextToken();
      }
      if (!tmp.equals("(")) throw new IllegalArgumentException("'(' expected but found "+tmp);
      tmp = tokenizer.nextToken();
      while (!tmp.equals(")")){
          while (tmp.trim().length() == 0){
              tmp = tokenizer.nextToken();
          }
          String type = tmp;
          tmp = tokenizer.nextToken();
          while (tmp.trim().length() == 0){
              tmp = tokenizer.nextToken();
          }
          int arraydim = 0;
          while (tmp.equals("[")){
              tmp = tokenizer.nextToken();
              if (!tmp.equals("]")) throw new IllegalArgumentException("']' expected but found "+tmp);
              arraydim++;
              tmp = tokenizer.nextToken();
          }
          while (tmp.trim().length() == 0){
              tmp = tokenizer.nextToken();
          }

          nativeMethod.append(toNativeType(type, arraydim));
          if (tmp.equals(",")){
              tmp = tokenizer.nextToken();
              while (tmp.trim().length() == 0){
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

  private static final String toNativeType(String type, int arraydim){
      StringBuffer nat = new StringBuffer(30);
      for (int i = 0; i < arraydim; i++){
          nat.append('[');
      }
      if ("byte".equals(type)){
          nat.append('B');
      } else       if ("char".equals(type)){
          nat.append('C');
      } else       if ("double".equals(type)){
          nat.append('D');
      } else       if ("float".equals(type)){
          nat.append('F');
      } else       if ("int".equals(type)){
          nat.append('I');
      } else       if ("long".equals(type)){
          nat.append('J');
      } else       if ("short".equals(type)){
          nat.append('S');
      } else       if ("boolean".equals(type)){
          nat.append('Z');
      } else       if ("void".equals(type)){
          nat.append('V');
      } else { //Lclassname;
          nat.append('L');
          nat.append(type.replace('.','/'));
          nat.append(';');
      }
      return nat.toString();
  }

  public void setNeedYShrinkModel( boolean b ) {
    this.needYShrinkModel = b;
  }

  /** Used by ant to handle the <code>patch</code> element.
   */
  public final class PatchSection {
    private List patches = new ArrayList();
    public void addConfiguredClass(ClassSection cs){
      patches.add(cs);
    }

    Collection createEntries(Collection srcJars) throws IOException{
        Collection entries = new ArrayList(20);
        for (Iterator it = srcJars.iterator(); it.hasNext();)
        {
          File file = (File) it.next();
          ZipFileSet zipFile = new ZipFileSet();
          zipFile.setProject(getProject());
          zipFile.setSrc(file);
          for (Iterator it2 = patches.iterator(); it2.hasNext();){
              ClassSection cs = (ClassSection) it2.next();
              if (cs.getName() == null){
                cs.addEntries(entries, zipFile);
              } else {
                cs.addEntries(entries, cs.getName());
              }
          }
        }
        return entries;
    }
  }

  /** Used by ant to handle the <code>inoutpair</code> element.
   */
//  public static final class InOutPair{
//    private File inFile;
//    private File outFile;
//    public void setIn(File file){
//      this.inFile = file;
//    }
//    public void setOut(File file){
//      this.outFile = file;
//    }
//
//    public String toString(){
//      return "in: "+inFile+"; out: "+outFile;
//    }
//  }

  /** Used by ant to handle the <code>classes</code>,
   * <CODE>methods</CODE> and <CODE>fields</CODE> attributes.
   */
  public static final class Modifiers extends EnumeratedAttribute {
    public String[] getValues() {
        return new String[] {"public", "protected", "friendly", "private","none"};
    }

    private int myGetIndex(){
      String[] values = getValues();
      for (int i = 0; i < values.length; i++){
        if (getValue().equals(values[i])){
          return i;
        }
      }
      return -1;
    }

    public int getModifierValue(){
      switch (myGetIndex()){
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


  /** Used by ant to handle the <code>map</code> element.
   */
  public final class MapSection{
    private File logFile;
    private List mappables = new ArrayList();
    public void addConfiguredPackage( PackageSection ps){
      mappables.add(ps);
    }
//    public ClassSection createClass() {
//      ClassSection cs = new ClassSection(  );
//      mappables.add( cs );
//      return cs;
//    }
    public void addConfiguredClass(ClassSection ps){
      mappables.add(ps);
    }
    public void addConfiguredField( FieldSection ps){
      mappables.add(ps);
    }
    public void addConfiguredMethod(MethodSection ps){
      mappables.add(ps);
    }

    public void setLogFile(File logFile){
      this.logFile = logFile;
    }

    Collection createEntries(Project antproject, PrintWriter log) throws BuildException{
      Collection res;
      if (logFile != null){
        try{
          SAXParserFactory f = SAXParserFactory.newInstance();
          f.setValidating(false);
          SAXParser parser = f.newSAXParser();
          XMLReader r = parser.getXMLReader();
          MapParser mp = new MapParser( ObfuscatorTask.this );
          r.setContentHandler(mp);
          Reader reader;
          if (logFile.getName().endsWith(".gz")){
            reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(logFile)));
          } else {
            reader = new FileReader(logFile);
          }
          InputSource source = new InputSource(reader);
          antproject.log("Parsing logfile's "+logFile.getName()+" map elements...", Project.MSG_INFO);
          r.parse(source);
          reader.close();
          r = null;
          f = null;
          parser = null;
          res = mp.getEntries();
        } catch (ParserConfigurationException pxe){
          throw new BuildException("Could configure xml parser!",pxe);
        } catch (SAXException pxe){
          throw new BuildException("Error parsing xml logfile!"+pxe,pxe);
        } catch (IOException ioe){
          throw new BuildException("Could not parse map from logfile!",ioe);
        }
      } else {
         res = new ArrayList(mappables.size());
      }
      for (Iterator it = mappables.iterator(); it.hasNext();){
        Mappable m = (Mappable)it.next();
        m.addMapEntries(res);
      }
      return res;
    }
  }


  /**
   * Used by ant to handle the <code>adjust</code> element.
   */
  public class AdjustSection extends ZipFileSet {
      private boolean replaceName = false;
      private boolean replaceContent = false;
      private boolean replacePath = true;

      private Set entries;

      public AdjustSection()
      {
        setProject(ObfuscatorTask.this.getProject());
      }

      public boolean contains(String name)
      {
        return entries.contains(name);
      }

      public void setReplaceContent(boolean rc){
        this.replaceContent = rc;
      }

      public boolean getReplaceContent()
      {
        return replaceContent;
      }

      public void setReplacePath(boolean rp){
        this.replacePath = rp;
      }

      public boolean getReplacePath()
      {
        return replacePath;
      }

      public boolean getReplaceName()
      {
        return replaceName;
      }

      public void setReplaceName(boolean rn){
        this.replaceName = rn;
      }

      public void createEntries(Collection srcJars) throws IOException
      {
        entries = new HashSet();
        for(Iterator iter = srcJars.iterator(); iter.hasNext();)
        {
          File file = (File) iter.next();
          setSrc(file);

          DirectoryScanner scanner = getDirectoryScanner(getProject());
          String[] includedFiles  = ZipScannerTool.getMatches(this, scanner);

          for(int i = 0; i < includedFiles.length; i++)
          {
            entries.add(includedFiles[i]);
          }
        }
      }
  }

//  public Path createExternalClasses(){
//    if (this.resourceClassPath != null){
//        throw new IllegalArgumentException("Only one externalclasses element allowed!");
//    }
//    this.resourceClassPath = new Path(getProject());
//    return this.resourceClassPath;
//  }

  /** Used by ant to handle the nested <code>expose</code> element.
   * @return an ExposeSection instance
   */
  public ExposeSection createExpose(){
    if (this.expose != null){
          throw new IllegalArgumentException("Only one expose element allowed!");
      }
      this.expose = new ExposeSection( this );
    return expose;
  }

  public void addExcludes( EntryPointsSection entryPoints ) {
    if ( null == this.expose ) {
      createExpose();
    }
  }

  public Exclude createKeep() {
    return createExpose();
  }

  public void addAttributesSections( List<AttributesSection> attributesSections ) {
    if ( null != expose ) {
      List attributes = expose.getAttributes();
      for ( AttributesSection attributesSection : attributesSections ) {
        com.yworks.yguard.ant.AttributesSection asYGuard = new com.yworks.yguard.ant.AttributesSection( this );
        PatternSet patternSet = attributesSection.getPatternSet(TypePatternSet.Type.NAME);
        if (patternSet != null) {
          asYGuard.addConfiguredPatternSet( patternSet );
        }
        asYGuard.setName( attributesSection.getAttributesStr() );
        attributes.add( asYGuard );
      }
    }
  }

  /** Used by ant to handle the nested <code>adjust</code> element.
   * @return an AdjustSection instance
   */
  public AdjustSection createAdjust(){
    AdjustSection adjust = new AdjustSection();
    adjust.setProject(this.getProject());
    adjustSections.add(adjust);
    return adjust;
  }

  /** Used by ant to handle the nested <code>expose</code> element.
   */
  public void addConfiguredExpose(ExposeSection ex){
      if (this.expose != null){
          throw new IllegalArgumentException("Only one expose element allowed!");
      }
      this.expose = ex;
  }

  public EntryPointsSection createEntryPoints() {
    return new EntryPointsSection( this );
  }

  /**
   * Used by ant to handle the nested <code>entrypoints</code> element.
   */
  public void addConfiguredEntryPoints( EntryPointsSection eps ) {
    if ( this.entryPoints != null ) {
      throw new IllegalArgumentException( "Only one entrypoints element allowed!" );
    }
    this.entryPoints = eps;
  }

  /** Used by ant to handle the nested <code>map</code> element.
   * @return an instance of MapSection
   */
  public MapSection createMap(){
      if (this.map != null){
          throw new IllegalArgumentException("Only one map element allowed!");
      }
      this.map = new MapSection();
    return map;
  }

  /** Used by ant to handle the nested <code>map</code> element.
   */
  public void addConfiguredMap(MapSection map){
      if (this.map != null){
          throw new IllegalArgumentException("Only one map element allowed!");
      }
      this.map = map;
  }

  /** Used by ant to handle the nested <code>patch</code> element.
   * @return an instance of PatchSection
   */
  public PatchSection createPatch(){
      if (this.patch != null){
          throw new IllegalArgumentException("Only one patch element allowed!");
      }
      this.patch = new PatchSection();
    return patch;
  }

  /** Used by ant to handle the nested <code>patch</code> element.
   */
  public void addConfiguredPatch(PatchSection patch){
      if (this.patch != null){
          throw new IllegalArgumentException("Only one patch element allowed!");
      }
      this.patch = patch;
  }


//  /** Used by ant to handle the nested <code>adjust</code> element.
//   */
//  public void addConfiguredAdjust(AdjustSection adjust){
//    adjustSections.add(adjust);
//    System.out.println("addConfiguredAdjust");
//  }

  /** Used by ant to handle the <code>logfile</code> attribute.
   * @param file
   */
  public void setLogFile(File file){
    this.logFile = file;
  }

  /** Used by ant to handle the <code>conservemanifest</code> attribute.
   */
  public void setConserveManifest(boolean c){
    this.conserveManifest = c;
  }

  /** Used by ant to handle the <code>mainclass</code> attribute.
   */
  public void setMainClass(String mainClass){
    this.mainClass = mainClass;
  }


  /** Used by ant to handle the start the obfuscation process.
   */
  public void execute() throws BuildException
  {
    getProject().log(this,"yGuard Obfuscator v" + Version.getVersion() + " - http://www.yworks.com/products/yguard", Project.MSG_INFO);


    if ( mode == MODE_STANDALONE ) {
      getProject().log( this, DEPRECATED, Project.MSG_WARN );
    }

    TaskLogger taskLogger = new TaskLogger();

    if ( ! ( mode == MODE_STANDALONE ) ) {
      doShrink = false;
    }

    if( doShrink ) doShrink();

    ResourceCpResolver resolver = null;
    if (resourceClassPath != null){
      resolver = new ResourceCpResolver(resourceClassPath, this);
      Cl.setClassResolver(resolver);
    }

    YGuardNameFactory nameFactory = null;

    if (properties.containsKey("naming-scheme")
        || properties.containsKey("language-conformity")
        || properties.containsKey("overload-enabled")){
      String ns = (String) properties.get("naming-scheme");
      String lc = (String) properties.get("language-conformity");

      int ilc = YGuardNameFactory.LEGAL;
      int ins = YGuardNameFactory.SMALL;

      if ("compatible".equalsIgnoreCase(lc)){
        ilc = YGuardNameFactory.COMPATIBLE;
      } else if ("illegal".equalsIgnoreCase(lc)){
        ilc = YGuardNameFactory.ILLEGAL;
      }
      if ("mix".equalsIgnoreCase(ns)){
        ins = YGuardNameFactory.MIX;
      }
      if ("best".equalsIgnoreCase(ns)){
        ins = YGuardNameFactory.BEST;
      }
      nameFactory = new YGuardNameFactory(ilc|ins);

      nameFactory.setPackagePrefix((String) properties.get("obfuscation-prefix"));
    } else {
      nameFactory = new YGuardNameFactory(YGuardNameFactory.LEGAL|YGuardNameFactory.SMALL);
      nameFactory.setPackagePrefix((String) properties.get("obfuscation-prefix"));
    }

    if (properties.containsKey("overload-enabled")) {
      String overload = (String) properties.get("overload-enabled");
      boolean overloadEnabled = true;
      if( "false".equalsIgnoreCase(overload) || "no".equalsIgnoreCase("overload")) {
        overloadEnabled = false;
      }
      nameFactory.setOverloadEnabled(overloadEnabled);
    }

    boolean pedantic = false;
    if (properties.containsKey("error-checking")){
      String ed = (String) properties.get("error-checking");
      if ("pedantic".equalsIgnoreCase(ed)){
        pedantic = true;
      }
    }
    getProject().log(this,"Using NameMakerFactory: "+NameMakerFactory.getInstance(), Project.MSG_VERBOSE);

    if (pairs == null){
      throw new BuildException("No in out pairs specified!");
    }
    Collection inFilesList = new ArrayList(pairs.size());
    File[] inFiles = new File[pairs.size()];
    File[] outFiles = new File[pairs.size()];
    for (int i = 0; i < pairs.size();i++)
    {
      InOutPair pair = (InOutPair) pairs.get(i);
      if (pair.getIn() == null || !pair.getIn().canRead()){
        throw new BuildException("Cannot open inoutpair.in "+pair.getIn());
      }
      inFiles[i] = pair.getIn();
      inFilesList.add(pair.getIn());
      if (pair.getOut() == null){
        throw new BuildException("Must specify inoutpair.out!");
      }
      outFiles[i] = pair.getOut();
    }
    PrintWriter log = null;
    if(logFile != null)
    {
      try{
        if (logFile.getName().endsWith(".gz")){
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
        taskLogger.setWriter( log );
      } catch (IOException ioe){
        getProject().log(this, "Could not create logfile: "+ioe, Project.MSG_ERR);
          log = new PrintWriter(System.out);
      }
    } else {
        log = new PrintWriter(System.out);
    }
    writeLogHeader(log, inFiles, outFiles);
    try{
      Collection rules = null;
      if (expose != null){
        rules = expose.createEntries(inFilesList);
      } else {
        rules = new ArrayList(20);
      }

      if (mainClass!= null)
      {
          String cn = toNativeClass(mainClass);
          rules.add(new YGuardRule(YGuardRule.TYPE_CLASS, cn));
          rules.add(new YGuardRule(YGuardRule.TYPE_METHOD, cn+"/main","([Ljava/lang/String;)V"));
      }
      if (map != null){
        Collection mapEntries = map.createEntries(getProject(), log);
        rules.addAll(mapEntries);
      }

      for(Iterator iter = adjustSections.iterator(); iter.hasNext(); )
      {
        AdjustSection as = (AdjustSection)iter.next();
        as.createEntries(inFilesList);
      }

      if (properties.containsKey("expose-attributes")){
        StringTokenizer st = new StringTokenizer((String)properties.get("expose-attributes"),",",false);
        while (st.hasMoreTokens()){
          String attribute = st.nextToken().trim();
          rules.add(new YGuardRule(YGuardRule.TYPE_ATTR, attribute));
          getProject().log(this, "Exposing attribute '"+attribute+"'", Project.MSG_VERBOSE);
        }
      }

      try
      {
        ObfuscatorTask.LogListener listener = new ObfuscatorTask.LogListener(getProject());
        Filter filter = null;
        if (patch != null){
          getProject().log(this, "Patching...", Project.MSG_INFO);
          Collection patchfiles = patch.createEntries(inFilesList);
          //generate namelist of classes....
          Set names = new HashSet();
          for (Iterator it = patchfiles.iterator(); it.hasNext();){
              YGuardRule entry = (YGuardRule) it.next();
              if (entry.type == YGuardRule.TYPE_CLASS){
                names.add(entry.name+".class");
              }
          }
          filter = new ClassFileFilter(new CollectionFilter(names));
        }
        GuardDB db = new GuardDB(inFiles);

        if (properties.containsKey("digests")) {
          String digests = (String) properties.get("digests");
          if (digests.trim().equalsIgnoreCase("none")){
            db.setDigests(new String[0]);
          } else {
            db.setDigests(digests.split("\\s*,\\s*"));
          }
        }

        if (annotationClass != null) db.setAnnotationClass(toNativeClass(annotationClass));

        db.setResourceHandler(new ResourceAdjuster(db));
        db.setPedantic(pedantic);
        db.setReplaceClassNameStrings(replaceClassNameStrings);
        db.addListener(listener);
        db.retain(rules, log);
        db.remapTo(outFiles, filter, log, conserveManifest);

        for (Iterator it = rules.iterator(); it.hasNext();){
          ((YGuardRule)it.next()).logProperties(log);
        }

        db.close();
        Cl.setClassResolver(null);

        if( doShrink ) {
          for ( int i = 0; i < tempJars.length; i++ ) {
            if ( null != tempJars[ i ] ) {
              tempJars[ i ].delete();
            }
          }
        }

        if ( !Logger.getInstance().isAllResolved() ) {
          Logger.getInstance().warning( "Not all dependencies could be resolved. Please see the logfile for details." );
        }

      } catch (NoSuchMappingException nsm){
        throw new BuildException("yGuard was unable to determine the mapped name for "+nsm.getKey()+".\n Probably broken code. Try recompiling from source!",nsm);
      } catch (ClassNotFoundException cnfe){
        throw new BuildException("yGuard was unable to resolve a class ("+cnfe+").\n Probably a missing external dependency.",cnfe);
      } catch (IOException ioe){
        if (ioe.getMessage() != null){
          getProject().log(this, ioe.getMessage(), Project.MSG_ERR);
        }
        throw new BuildException("yGuard encountered an IO problem!", ioe);
      } catch (ParseException pe){
        throw new BuildException("yGuard encountered problems during parsing!", pe);
      } catch (RuntimeException rte){
        if (rte.getMessage() != null){
          getProject().log(this, rte.getMessage(), Project.MSG_ERR);
        }
        rte.printStackTrace();
        throw new BuildException("yGuard encountered an unknown problem!", rte);
      } finally{
          writeLogFooter(log);
          log.flush();
          log.close();
      }
    } catch (IOException ioe){
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

  private void doShrink() {



    YShrinkInvoker yShrinkInvoker = null;

    try {
       yShrinkInvoker = (YShrinkInvoker) Class.forName( "com.yworks.yguard.yshrink.YShrinkInvokerImpl" ).newInstance();
    } catch ( InstantiationException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    } catch ( IllegalAccessException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    } catch ( ClassNotFoundException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    }

    if ( null == yShrinkInvoker ) return;

    yShrinkInvoker.setContext( (Task)this );

    tempJars = new File[ pairs.size() ];
    File[] outJars  = new File[ pairs.size() ];

    for ( int i = 0; i < tempJars.length; i++ ) {
      try {
        tempJars[ i ] = File.createTempFile( "tempJar_", "_shrinked.jar", new File(((InOutPair) pairs.get( i )).getOut().getParent()));
      } catch ( IOException e ) {
        getProject().log( "Could not create tempfile for shrinking " + tempJars[ i ] + ".", Project.MSG_ERR );
        tempJars[ i ] = null;
      }

      if ( null != tempJars[ i ] ) {
        System.out.println( "temp-jar: " + tempJars[ i ] );
        ShrinkBag pair = ((ShrinkBag) pairs.get( i ));
        outJars[ i ] = pair.getOut();
        pair.setOut( tempJars[ i ] );
        yShrinkInvoker.addPair( pair );
      }
    }

    yShrinkInvoker.setResourceClassPath( resourceClassPath );

    if ( shrinkLog != null ) {
      yShrinkInvoker.setLogFile( shrinkLog );
    }

    if ( null != entryPoints ) {
      yShrinkInvoker.setEntyPoints( entryPoints );
    }

    if ( null != expose && useExposeAsEntryPoints ) {
      for ( ClassSection cs : (List<ClassSection>) expose.getClasses()) {
        yShrinkInvoker.addClassSection( cs );
      }
      for ( MethodSection ms : (List<MethodSection>) expose.getMethods()) {
        yShrinkInvoker.addMethodSection( ms );
      }
      for ( FieldSection fs : (List<FieldSection>) expose.getFields() ) {
        yShrinkInvoker.addFieldSection( fs );
      }
    }

    yShrinkInvoker.execute();

    for ( int i = 0; i < tempJars.length; i++ ) {
      if( null != tempJars[ i ] ) {
        InOutPair pair = ((InOutPair) pairs.get( i ));
        pair.setIn( tempJars[ i ] );
        pair.setOut( outJars[ i ] );
      }
    }
  }

  public void addInheritanceEntries( Collection entries ) throws IOException {

    if ( ! needYShrinkModel || expose == null ) return;

    yShrinkModel = null;

    try {
      yShrinkModel = (YShrinkModel) Class.forName( "com.yworks.yguard.yshrink.YShrinkModelImpl" ).newInstance();
    } catch ( InstantiationException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    } catch ( IllegalAccessException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    } catch ( ClassNotFoundException e ) {
      throw new BuildException( NO_SHRINKING_SUPPORT, e );
    }

    if ( null == yShrinkModel ) return;

    if (this.resourceClassPath != null) {
      yShrinkModel.setResourceClassPath(this.resourceClassPath,this);
    }

    yShrinkModel.createSimpleModel( (List<ShrinkBag>) pairs );

    for ( String className : yShrinkModel.getAllClassNames() ) {

      Set<String> allAncestorClasses = yShrinkModel.getAllAncestorClasses( className );
      Set<String> allInterfaces = yShrinkModel.getAllImplementedInterfaces( className );

      for ( ClassSection cs : (List<ClassSection>) expose.getClasses() ) {

        if ( null != cs.getExtends() ) {
          String extendsName = cs.getExtends();
          if ( extendsName.equals( className ) ) {
            //System.out.println( extendsName + " equals "+className );
            cs.addEntries( entries, className );
          } else {
            if ( allAncestorClasses.contains( extendsName ) ) {
              cs.addEntries( entries, className );
              //System.out.println( extendsName + " extends "+className );
            }
          }
        }

        if ( null != cs.getImplements() ) {
          String interfaceName = cs.getImplements();
          if ( interfaceName.equals( className ) ) {
            //System.out.println( interfaceName + " equals "+className );
            cs.addEntries( entries, className );
          } else {
            if ( allInterfaces.contains( interfaceName ) ) {
              cs.addEntries( entries, className );
              //System.out.println( interfaceName + " implements "+className );
            }
          }
        }
      }
    }
  }

  public void setShrink( boolean doShrink ) {

    if ( mode == MODE_STANDALONE ) {
      this.doShrink = doShrink;
    } else {
      throw new BuildException(
          "The shrink attribute is not supported when the obfuscate task is nested inside a yguard task.\n Use a separate nested shrink task instead." );
    }

  }

   public void setShrinkLog( File shrinkLog ) {
    this.shrinkLog = shrinkLog;
  }

  public void setUseExposeAsEntryPoints( boolean useExposeAsEntryPoints ) {
    this.useExposeAsEntryPoints = useExposeAsEntryPoints;
  }

  class ResourceAdjuster implements ResourceHandler
  {
     GuardDB db;
     Map map;
     StringReplacer contentReplacer = null;

     ResourceAdjuster(final GuardDB db)
     {
       this.db = db;
       map = new HashMap() {
         public Object get(Object key)
         {
           return db.translateJavaClass(key.toString());
         }
       };
     }

     public boolean filterName(String inName, StringBuffer outName)
     {
       boolean rp = true;
       boolean rn = false;

       for(Iterator iter = adjustSections.iterator(); iter.hasNext();)
       {
         AdjustSection as = (AdjustSection)iter.next();
         if(as.contains(inName))
         {
           if(as.getReplaceName()) rn = true;
           if(!as.getReplacePath()) rp = false;
         }
       }

       if(rn)
       {
         outName.setLength(0);
         final String servicesPrefix = "META-INF/services/";
         if (inName.startsWith(servicesPrefix)) {
           // the file name of a service is a fully qualified class name
           final String cn = inName.substring(servicesPrefix.length());
           outName.append(servicesPrefix);
           // translateJavaFile returns a path name, replacing file separators
           // with dots converts that path name back into a qualified class
           // name (which is the required file name for a service)
           outName.append(db.translateJavaFile(cn).replace('/', '.'));
         } else {
           int index = 0;
           if (inName.endsWith(".properties")) {
             index = inName.indexOf('_');
           }
           if (index <= 0) {
             index = inName.indexOf('.');
           }
           String prefix = inName.substring(0, index);
           prefix = db.translateJavaFile(prefix);
           outName.append(prefix);
           outName.append(inName.substring(index));
         }
       }
       else
       {
         outName.append(inName);
       }

       if(!rp)
       {
         String outPath = inName.substring(0,inName.lastIndexOf('/')+1);
         String outFile = outName.toString();
         outFile = outFile.substring(outFile.lastIndexOf('/')+1);
         outName.setLength(0);
         outName.append(outPath);
         outName.append(outFile);
       }

       return rn || !rp;
     }


     public boolean filterContent(InputStream in, OutputStream out, String resourceName) throws IOException
     {
       for(Iterator iter = adjustSections.iterator(); iter.hasNext();)
       {
         AdjustSection as = (AdjustSection)iter.next();
         if(as.contains(resourceName) && as.getReplaceContent())
         {
           Writer writer = new OutputStreamWriter(out);
           getContentReplacer().replace(new InputStreamReader(in), writer, map);
           writer.flush();
           return true;
         }
       }
       return false;
     }

    public String filterString(String in, String resourceName) throws IOException {
      StringBuffer result =new StringBuffer(in.length());
      getContentReplacer().replace(in, result, map);
      return result.toString();
    }

    StringReplacer getContentReplacer()
     {
       if(contentReplacer == null)
       {
         contentReplacer = new StringReplacer("(?:\\w|[$])+(\\.(?:\\w|[$])+)+");
       }
       return contentReplacer;
     }

  };


  //accepts classes and their inner classes
  private static final class ClassFileFilter implements Filter{
      private com.yworks.util.Filter parent;
      ClassFileFilter(com.yworks.util.Filter parent){
      this.parent = parent;
    }
    public boolean accepts(Object o)
    {
      String s= (String) o;
      if (s.endsWith(".class") && s.indexOf('$') != -1)
      {
        s = s.substring(0, s.indexOf('$')) + ".class";
      }
      return parent.accepts(s);
    }
  }

  private final class TaskLogger extends Logger {

    private PrintWriter writer;

    TaskLogger(){
      super();
    }

    void setWriter( PrintWriter writer ) {
      this.writer = writer;
    }

    public void warning(String message)
    {
      getProject().log(ObfuscatorTask.this, "WARNING: "+message, Project.MSG_WARN);

    }

    public void warningToLogfile( String message ) {
      if ( null != writer ) {
        writer.println("<!-- " + "WARNING: "+message + " -->");
      }
    }

    public void log(String message)
    {
      getProject().log(ObfuscatorTask.this, message, Project.MSG_INFO);
    }

    public void error(String message)
    {
      getProject().log(ObfuscatorTask.this, "ERROR: "+message, Project.MSG_ERR);
    }
  }

  // Write a header out to the log file
  private void writeLogHeader(PrintWriter log, File[] inFile, File[] outFile)
  {
    log.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    log.println("<yguard version=\""+"1.5"+"\">");
      log.println("<!--");
      log.println(LOG_TITLE_PRE_VERSION + Version.getVersion() + LOG_TITLE_POST_VERSION);
      log.println();
      log.println(LOG_CREATED + new Date().toString());
      log.println();
      for(int i = 0; i < inFile.length; i++){
        log.println(LOG_INPUT_FILE + inFile[i].getName());
        log.println(LOG_OUTPUT_FILE + outFile[i].getName());
        log.println();
      }
      log.println("-->");
  }

  private static final class LogListener implements ObfuscationListener {

    private Project p;

    LogListener(Project p){
      this.p = p;
    }

    public void obfuscatingClass(String className)
    {
      p.log("Obfuscating class "+className, Project.MSG_VERBOSE);
    }

    public void obfuscatingJar(String inJar, String outJar)
    {
      p.log("Obfuscating Jar "+inJar+" to "+outJar);
    }

    public void parsingClass(String className)
    {
      p.log("Parsing class "+className, Project.MSG_VERBOSE);
    }

    public void parsingJar(String jar)
    {
      p.log("Parsing jar "+jar);
    }

  }

  private void writeLogFooter(PrintWriter log){
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

    static{
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
      } catch (IOException ioe){
        throw new InternalError("Could not load valid character bitset!" + ioe.getMessage());
      } catch (ClassNotFoundException cnfe){
        throw new InternalError("Could not load valid character bitset!" + cnfe.getMessage());
      }

      for (char i = 0; i < Character.MAX_VALUE; i++){
        if (!bs[0].get((int)i)) {
          illegalC.append(i);
        } else {
          legalC.append(i);
          if (i>255){
            crazyLegalC.append(i);
          } else if (i < 128){
            asciiC.append(i);
            if (Character.isLowerCase(i)){
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
      for (char i = 0; i < Character.MAX_VALUE; i++){

        if (!bs[1].get((int)i)){
          illegalC.append(i);
        } else {
          legalC.append(i);
          if (i>255){
            crazyLegalC.append(i);
          } else if (i < 128){
            asciiC.append(i);
          }
        }
      }
      legalChars = legalC.toString();
      crazylegalChars = crazyLegalC.toString();
      asciiChars = asciiC.toString();
    }

    int mode;
    static final int LEGAL = 0;
    static final int COMPATIBLE = 1;
    static final int ILLEGAL = 2;
    static final int SMALL = 4;
    static final int MIX = 12;
    static final int BEST = 8;

    boolean overloadEnabled = true;

    YGuardNameFactory(){
      this(LEGAL|SMALL);
    }

    YGuardNameFactory(int mode){
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

    private static String scrambleChars(String string) {
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

    public boolean isOverloadEnabled() {
      return overloadEnabled;
    }

    public void setOverloadEnabled(boolean overloadEnabled) {
      this.overloadEnabled = overloadEnabled;
    }

    void setPackagePrefix(String prefix){
      this.packagePrefix = prefix;
      if (packagePrefix != null){
        packagePrefix = packagePrefix.replace('.', '/')+'/';
      }
    }

    protected NameMaker createFieldNameMaker(String[] reservedNames, String fqClassName)
    {
      switch (mode){
        default:
        case COMPATIBLE+SMALL:
          LongNameMaker longNameMaker1 = new LongNameMaker(reservedNames,
              asciiLowerChars, asciiLowerChars, 1);
          longNameMaker1.setOverloadEnabled(overloadEnabled);
          return longNameMaker1;
        case LEGAL+SMALL:
          LongNameMaker longNameMaker2 = new LongNameMaker(reservedNames,
              legalFirstChars, legalChars, 1);
          longNameMaker2.setOverloadEnabled(overloadEnabled);
          return longNameMaker2;
        case COMPATIBLE+MIX:
        case LEGAL+MIX:
            AbstractNameMaker nm1 = new LongNameMaker(reservedNames, false, 6);
            AbstractNameMaker nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames);
          MixNameMaker mixNameMaker1 = new MixNameMaker(null, reservedNames, nm1,
              3);
          MixNameMaker mnm = mixNameMaker1;
            mnm.add(nm2, 1);
          mnm.setOverloadEnabled(overloadEnabled);
          return mnm;
        case COMPATIBLE+BEST:
        case LEGAL+BEST:
          LongNameMaker longNameMaker4 = new LongNameMaker(reservedNames, false,
              256);
          KeywordNameMaker keywordNameMaker1 = new KeywordNameMaker(reservedNames);
          CompoundNameMaker compoundNameMaker1 = new CompoundNameMaker(
            longNameMaker4,
            keywordNameMaker1);
          longNameMaker4.setOverloadEnabled(overloadEnabled);
          keywordNameMaker1.setOverloadEnabled(overloadEnabled);
          return compoundNameMaker1;
        case ILLEGAL+SMALL:
          LongNameMaker longNameMaker3 = new LongNameMaker(reservedNames,
              crazylegalFirstChars, crazylegalChars, 1);
          longNameMaker3.setOverloadEnabled(overloadEnabled);
          return longNameMaker3;
        case ILLEGAL+MIX:
          nm1 = new LongNameMaker(reservedNames, false, 6);
          nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames,
            KeywordNameMaker.KEYWORDS,
            KeywordNameMaker.SPACER);
          MixNameMaker mixNameMaker2 = new MixNameMaker(null, reservedNames, nm1,
              2);
          mixNameMaker2.add(nm2, 1);
          mixNameMaker2.setOverloadEnabled(overloadEnabled);
          return mixNameMaker2;
        case ILLEGAL+BEST:
          nm1 = new ObfuscatorTask.KeywordNameMaker(reservedNames,
            KeywordNameMaker.KEYWORDS,
            KeywordNameMaker.SPACER);
          nm2 = new LongNameMaker(reservedNames,false, 256);
          mnm = new MixNameMaker(null, reservedNames, nm1,
              1);
          mnm.add(nm2,1);
          mnm.setOverloadEnabled(overloadEnabled);
          return mnm;
      }
    }

    protected NameMaker createMethodNameMaker(String[] reservedNames, String fqClassName)
    {
      return createFieldNameMaker(reservedNames, fqClassName);
    }

    protected NameMaker createPackageNameMaker(String[] reservedNames, String packageName)
    {
      boolean topLevel = packageName.length() < 1;
      switch (mode){
        default:
        case COMPATIBLE+SMALL:
        case COMPATIBLE+MIX:
        case COMPATIBLE+BEST:
          if (topLevel && packagePrefix != null){
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, asciiLowerChars, asciiLowerChars, 1));
          } else {
            return new LongNameMaker(reservedNames,
              asciiLowerChars, asciiLowerChars, 1);
          }
        case LEGAL+SMALL:
        case ILLEGAL+SMALL:
          if (topLevel && packagePrefix != null){
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, asciiFirstChars, asciiChars, 1));
          } else {
            return new LongNameMaker(reservedNames,
              asciiFirstChars, asciiChars, 1);
          }
        case LEGAL+MIX:
        case ILLEGAL+MIX:
            AbstractNameMaker nm1 = new LongNameMaker(reservedNames,
            asciiFirstChars, asciiChars, 1);
            AbstractNameMaker nm3 = new LongNameMaker(reservedNames,true, 256);
            AbstractNameMaker nm4 = new LongNameMaker(reservedNames,true, 4);
            AbstractNameMaker nm2 = new ObfuscatorTask.KeywordNameMaker(reservedNames);
            MixNameMaker mnm = new MixNameMaker(topLevel ? packagePrefix : null, reservedNames, nm1, 8);
            mnm.add(nm4, 4);
            mnm.add(nm2, 4);
            mnm.add(nm3, 1);
            return mnm;
        case LEGAL+BEST:
        case ILLEGAL+BEST:
          if (topLevel && packagePrefix != null){
            return new PrefixNameMaker(packagePrefix, reservedNames, new LongNameMaker(null, true, 256));
          } else {
            return new LongNameMaker(reservedNames, true, 256);
          }

      }
    }

    protected NameMaker createClassNameMaker(String[] reservedNames, String fqClassName)
    {
      return createPackageNameMaker(reservedNames, fqClassName);
    }

    protected NameMaker createInnerClassNameMaker(String[] reservedNames, String fqInnerClassName)
    {
      switch (mode){
        default:
        case COMPATIBLE+SMALL:
        case COMPATIBLE+MIX:
        case COMPATIBLE+BEST:
          return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, asciiLowerChars, asciiLowerChars, 1));
        case LEGAL+SMALL:
        return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null, asciiFirstChars, asciiChars, 1));
        case LEGAL+MIX:
        return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null,true, 1));
        case LEGAL+BEST:
        return new PrefixNameMaker("_", reservedNames, new LongNameMaker(null,true, 4));
        case ILLEGAL+SMALL:
        return new LongNameMaker(reservedNames, asciiFirstChars, asciiChars, 1);
        case ILLEGAL+MIX:
        return new LongNameMaker(reservedNames,true, 1);
        case ILLEGAL+BEST:
        return new LongNameMaker(reservedNames,true, 10);
      }
    }

    public String toString(){
      switch (mode){
        default:
          return "yGuardNameFactory [naming-scheme: default; language-conformity: default]";
        case COMPATIBLE+SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: compatible]";
        case COMPATIBLE+MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: compatible]";
        case COMPATIBLE+BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: compatible]";
        case LEGAL+SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: legal]";
        case LEGAL+MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: legal]";
        case LEGAL+BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: legal]";
        case ILLEGAL+SMALL:
          return "yGuardNameFactory [naming-scheme: small; language-conformity: illegal]";
        case ILLEGAL+MIX:
          return "yGuardNameFactory [naming-scheme: mix; language-conformity: illegal]";
        case ILLEGAL+BEST:
          return "yGuardNameFactory [naming-scheme: best; language-conformity: illegal]";
      }
    }
  }

  static class CompoundNameMaker implements NameMaker{
    private NameMaker nm1,nm2;
    CompoundNameMaker(NameMaker nm1, NameMaker nm2){
      this.nm1 = nm1;
      this.nm2 = nm2;
    }
    public String nextName(String descriptor)
    {
      return nm1.nextName(descriptor)+nm2.nextName(descriptor);
    }
  }

  static class MixNameMaker extends AbstractNameMaker {

    List nameMakers = new ArrayList();
    final String prefix;

    MixNameMaker(String prefix, String[] reservedNames, AbstractNameMaker delegate, int count){
      super(reservedNames, "O0", 1);
      add(delegate, count);
      this.prefix = prefix;
    }

    void add(AbstractNameMaker delegate, int count){
      count = count < 1 ? 1 : count;
      for (int i = 0; i < count; i++){
        nameMakers.add(delegate);
      }
      Collections.shuffle(nameMakers);
    }

    String generateName(int i)
    {
      if (prefix != null){
        return prefix + ((AbstractNameMaker)nameMakers.get(i % nameMakers.size())).generateName(i);
      } else {
        return ((AbstractNameMaker)nameMakers.get(i % nameMakers.size())).generateName(i);
      }
    }
  }

  static final class LongNameMaker extends AbstractNameMaker{
    String chars;
    String firstChars;

    LongNameMaker(String[] reservedNames){
      this(reservedNames, false, 256);
    }

    LongNameMaker(String[] reservedNames, boolean ascii, int length){
      this(reservedNames, ascii?"Oo":"Oo\u00D2\u00D3\u00D4\u00D5\u00D6\u00D8\u00F4\u00F5\u00F6\u00F8",
      ascii?"Oo0":"0Oo\u00D2\u00D3\u00D4\u00D5\u00D6\u00D8\u00F4\u00F5\u00F6\u00F8",length);
    }

    LongNameMaker(String[] reservedNames, String firstChars, String chars, int minLength){
      super(reservedNames, null, minLength);
      this.chars = chars;
      if ( chars == null || chars.length() < 1 ) {
        throw new IllegalArgumentException( "must specify at least one character!" );
      }
      this.firstChars = firstChars;
      if (firstChars != null && firstChars.length()<1) this.firstChars = null;
    }

    String generateName(int i)
    {
      StringBuffer sb = new StringBuffer(20);
      int tmp = i;
      if (firstChars != null){
        sb.append(firstChars.charAt(tmp % firstChars.length()));
        if (firstChars.length() > 1){
          tmp = tmp / firstChars.length();
        } else {
          tmp--;
        }
      }
      while (tmp > 0){
        sb.append(chars.charAt(tmp % chars.length()));
        if (chars.length()>1){
          tmp = tmp / chars.length();
        } else {
          tmp--;
        }
      }
      if (chars.length()>1){
        while (sb.length()< minLength){
          sb.append(chars.charAt(0));
        }
      }
      return sb.toString();
    }
  }

  static final class KeywordNameMaker extends AbstractNameMaker{
    static final String[] KEYWORDS = new String[]{
      "this","super","new","Object","String","class","return","void","null","int",
      "if","float","for","do","while","public","private","interface",};
    static final String[] SPACER = new String[]{
      ".","$"," ","_",
    };

    static final String[] NOSPACER = new String[]{""};

    String chars;
    String[] keyWords;
    String spacer[];

    KeywordNameMaker(String[] reservedNames){
      this(reservedNames, KEYWORDS, NOSPACER);
    }

    KeywordNameMaker(String[] reservedNames, String[] keyWords, String[] spacer){
      super(reservedNames, "Oo0",0);
      this.keyWords = keyWords;
      this.spacer = spacer;
    }

    String generateName(int i)
    {
      StringBuffer sb = new StringBuffer(30);
      int tmp = i;
      int sc = 0;
      while (tmp > 0){
        sb.append(keyWords[tmp % keyWords.length]);
        tmp = tmp / keyWords.length;
        if (tmp>0){
          sb.append(spacer[sc % spacer.length]);
          sc++;
        }
      }
      return sb.toString();
    }
  }

  static final class PrefixNameMaker extends AbstractNameMaker {
    private String prefix;
    private AbstractNameMaker delegate;

    PrefixNameMaker(String prefix, String[] reservedNames, AbstractNameMaker delegate){
      super(reservedNames, "O0", 1);
      this.prefix = prefix;
      this.delegate = delegate;
    }

    String generateName(int i)
    {
      return prefix + delegate.generateName(i);
    }

  }

  static abstract class AbstractNameMaker implements NameMaker{
    Set reservedNames;
    Map countMap = new HashMap();
    String fillChars;
    int minLength;
    private static final String DUMMY = "(com.dummy.Dummy)";

    protected boolean overloadEnabled = true;
    private int counter = 1;

    public boolean isOverloadEnabled() {
      return overloadEnabled;
    }

    public void setOverloadEnabled(boolean overloadEnabled) {
      this.overloadEnabled = overloadEnabled;
    }


    AbstractNameMaker(String[] reservedNames){
      this(reservedNames, "0o", 256);
    }

    AbstractNameMaker(String[] reservedNames, String fillChars, int minLength){
      if (reservedNames!= null && reservedNames.length>0){
        this.reservedNames = new HashSet(Arrays.asList(reservedNames));
      } else {
        this.reservedNames = Collections.EMPTY_SET;
      }
      this.minLength = minLength;
      this.fillChars = fillChars != null ? fillChars: "0O";
    }

    /** Return the next unique name for this namespace, differing only for identical arg-lists.  */
    public String nextName(String descriptor)
    {
      if (descriptor == null){
        descriptor = DUMMY;
      }
      int j;

      if (overloadEnabled){
        descriptor = descriptor.substring(0, descriptor.lastIndexOf(')'));
        Integer i = (Integer) countMap.get(descriptor);
        if (i == null){
          i = new Integer(1);
        }
        j = i.intValue();
      } else {
        j = counter;
      }
      String result = null;
      StringBuffer sb = new StringBuffer(minLength>10?minLength+20:20);
      do {
        sb.setLength(0);
        String name = generateName(j);
        sb.append(name);
        if (sb.length() < minLength){
          while (sb.length()<minLength){
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
//      checkIdentifier(result);
      return result;
    }

//    private static final void checkIdentifier(String s){
//      if (s.length() < 1)// throw new RuntimeException("Identifer must be longer than 0");
//        System.err.println("Identifer must be longer than 0");
//      if (!Character.isJavaIdentifierStart(s.charAt(0))){
//        //throw new RuntimeException("Identifer must start legally! "+s );
//        System.err.println("Identifer must start legally! "+s);
//      }
//      for (int i = 1; i< s.length(); i++){
//        if (!Character.isJavaIdentifierPart(s.charAt(i))){
//          //throw new RuntimeException("Identifer must continue legally at! "+i+" "+s );
//          System.err.println("Identifer must continue legally at! "+i+" "+s );
//        }
//      }
//    }

    abstract String generateName(int i);
  }

  static final class ResourceCpResolver implements ClassResolver {
    Path resource;
    URLClassLoader urlClassLoader;

    ResourceCpResolver(Path resources, Task target){
      this.resource = resources;
      String[] list = resources.list();
      List listUrls = new ArrayList();
      for (int i = 0; i <list.length; i++){
        try{
          URL url = new File(list[i]).toURL();
          listUrls.add(url);
        } catch (MalformedURLException mfue){
          target.getProject().log(target, "Could not resolve resource: "+mfue, Project.MSG_WARN);
        }
      }
      URL[] urls = new URL[listUrls.size()];
      listUrls.toArray(urls);
      urlClassLoader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());
    }
    public Class resolve(String className) throws ClassNotFoundException
    {
      try {
        return Class.forName(className, false, urlClassLoader);
      } catch (NoClassDefFoundError ncdfe){
        String message = ncdfe.getMessage();
        if (message == null || message.equals(className)){
          message = className;
        } else {
          message = message + "[" + className + "]";
        }
        throw new ClassNotFoundException(message, ncdfe);
      } catch (LinkageError le){
        throw new ClassNotFoundException(className, le);
      }
    }

    @Override
    public void close() throws Exception {
      urlClassLoader.close();
    }
  }

  /** Setter for property replaceClassNameStrings.
   * @param replaceClassNameStrings New value of property replaceClassNameStrings.
   *
   */
  public void setReplaceClassNameStrings(boolean replaceClassNameStrings)
  {
    this.replaceClassNameStrings = replaceClassNameStrings;
  }

  public void setScramble(boolean scramble) {
    if (scramble) {
      YGuardNameFactory.scramble();
      com.yworks.yguard.obf.KeywordNameMaker.scramble();
    }
  }
  public static final class MyLineNumberTableMapper implements com.yworks.yguard.obf.LineNumberTableMapper {
    private long salt;
    private LineNumberScrambler last;
    private long lastSeed;
    private Set classNames = new HashSet();
    public MyLineNumberTableMapper(long salt){
      this.salt = salt;
      this.last = new LineNumberScrambler(3584, lastSeed);
    }

    public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable) {
      final String javaClassName = className.replace('/','.').replace('$','.');
      classNames.add(className.replace('/', '.'));
      long seed = salt ^ javaClassName.hashCode();
      LineNumberScrambler scrambler;
      if (seed == lastSeed){
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

    public void logProperties(PrintWriter pw) {
      if (!classNames.isEmpty()){
        for (Iterator it = classNames.iterator(); it.hasNext(); ){
          pw.println("<property owner=\"" + ClassTree.toUtf8XmlString(it.next().toString()) + "\" name=\"scrambling-salt\" value=\"" + Long.toString(salt) + "\"/>");
        }
        classNames.clear();
      }
    }
  }

  public static final class LineNumberSqueezer implements LineNumberTableMapper {
    private List squeezedNumbers = new ArrayList();
    public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable) {
      final LineNumberInfo[] table = lineNumberTable.getLineNumberTable();
      if (table.length > 0){
        final LineNumberInfo lineNumberInfo = new LineNumberInfo(table[0].getStartPC(), table[0].getLineNumber());
        lineNumberTable.setLineNumberTable(new LineNumberInfo[]{lineNumberInfo});
        squeezedNumbers.add(new Object[]{className, methodName, methodSignature, lineNumberInfo});
        return true;
      }
      return false;
    }

    public void logProperties(PrintWriter pw) {
      if (!squeezedNumbers.isEmpty()){
        for (Iterator it = squeezedNumbers.iterator(); it.hasNext();){
          Object[] ar = (Object[]) it.next();
          String className = ar[0].toString();
          String methodName = ar[1].toString();
          String methodSignature = ar[2].toString();
          int line = ((LineNumberInfo)ar[3]).getLineNumber();
          pw.println("<property owner=\"" + ClassTree.toUtf8XmlString(Conversion.toJavaClass(className)) + "#" + ClassTree.toUtf8XmlString(Conversion.toJavaMethod(methodName, methodSignature)) + "\" name=\"squeezed-linenumber\" value=\"" + line + "\"/>");
        }
        squeezedNumbers.clear();
      }
    }
  }

  public static final class LineNumberScrambler {
    private int[] scrambled;
    private int[] unscrambled;
    public LineNumberScrambler(int size, long seed){
      this.scrambled = new int[size];
      this.unscrambled = new int[size];
      for (int i = 0; i < size; i++){
        this.scrambled[i] = i;
        this.unscrambled[i] = i;
      }
      Random r = new Random(seed);
      for (int i = 0; i < 10; i++){
        for (int j = 0; j < size; j++){
          int otherIndex = r.nextInt(size);
          if (otherIndex != j){
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
//      for (int i = 0; i < 10000; i++) {
//        System.out.println("scramble " + i + " " + scramble(i));
//        if (unscramble(scramble(i)) != i){
//          throw new RuntimeException();
//        }
//      }
//      System.out.println("all is well");
    }

    public int scramble(int i){
      if (i >= scrambled.length){
        return scrambled[i % scrambled.length] + (i / scrambled.length) * scrambled.length;
      } else {
        return scrambled[i];
      }
    }

    public int unscramble(int i){
      if (i >= scrambled.length){
        return unscrambled[i % scrambled.length] + (i / scrambled.length) * scrambled.length;
      } else {
        return unscrambled[i];
      }
    }
  }

  public static void main(String[] args){
    new LineNumberScrambler(2000, 234432);
  }

//  public static void main(String[] args) throws Exception{
//    Project project = new Project();
//    File base =new File("/home/muellese/job/localcvs/yguard/deploy");
//    project.setBaseDir(base);
//    project.init();
//    ObfuscatorTask os = new ObfuscatorTask();
//    os.setProject(project);
//    ObfuscatorTask.InOutPair iop = new ObfuscatorTask.InOutPair();
//    iop.setIn(new File(base, "test.jar"));
//    iop.setOut(new File(base, "testobf.jar"));
//    os.setLogFile(new File(base,"testobflog.xml.gz"));
//    ObfuscatorTask.Property pop = new ObfuscatorTask.Property();
//    pop.setName("expose-attributes");
//    pop.setValue("Deprecated");
//    os.addConfiguredProperty(pop);
//    ObfuscatorTask.ExposeSection eps = os.createExpose();
//    ObfuscatorTask.ClassSection sec = new ObfuscatorTask.ClassSection();
//    PatternSet patternSet = new PatternSet();
//    patternSet.setProject(project);
//    patternSet.setIncludes("com.yworks.yguard.ObfuscatorTask*");
//    sec.addConfiguredPatternSet(patternSet);
//    ObfuscatorTask.Modifiers mm = new ObfuscatorTask.Modifiers();
//    mm.setValue("protected");
//    sec.setClasses(mm);
//    eps.addConfiguredClass(sec);
//    os.addConfiguredInOutPair(iop);
//    os.execute();
//  }

  private String annotationClass;

  public String getAnnotationClass() {
    return annotationClass;
  }

  public void setAnnotationClass(String annotationClass) {
    this.annotationClass = annotationClass;
  }
}
