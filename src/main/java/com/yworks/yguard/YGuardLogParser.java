package com.yworks.yguard;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * The type Y guard log parser.
 */
public class YGuardLogParser {
  private final DefaultTreeModel tree;

  private final MyContentHandler contentHandler = new MyContentHandler();

  /**
   * The interface Mapped.
   */
  interface Mapped {
    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets mapped name.
     *
     * @return the mapped name
     */
    String getMappedName();

    /**
     * Gets icon.
     *
     * @return the icon
     */
    Icon getIcon();
  }

  private static class AbstractMappedStruct implements Mapped {
    private String name;
    private String mappedName;
    private final Icon icon;

    /**
     * Instantiates a new Abstract mapped struct.
     *
     * @param namePart   the name part
     * @param mappedName the mapped name
     * @param icon       the icon
     */
    public AbstractMappedStruct( String namePart, String mappedName, Icon icon ) {
      this.name = namePart;
      this.mappedName = mappedName;
      this.icon = icon;
    }

    public String getMappedName() {
      return mappedName;
    }

    public Icon getIcon() {
      return icon;
    }

    public String getName() {
      return name;
    }

    /**
     * Sets mapped name.
     *
     * @param n the n
     */
    public void setMappedName( String n ) {
      this.mappedName = n;
    }

    /**
     * Sets name.
     *
     * @param n the n
     */
    public void setName( String n ) {
      this.name = n;
    }

    public String toString() {
      return getName() + " -> " + getMappedName();
    }
  }

  /**
   * The type Package struct.
   */
  static final class PackageStruct extends AbstractMappedStruct {
    /**
     * Instantiates a new Package struct.
     *
     * @param name the name
     * @param map  the map
     */
    PackageStruct( String name, String map ) {
      super(name, map, Icons.PACKAGE_ICON);
    }

    public String toString() {
      return getName() + " -> " + getMappedName();
    }
  }

  /**
   * The type Class struct.
   */
  static final class ClassStruct extends AbstractMappedStruct {
    /**
     * Instantiates a new Class struct.
     *
     * @param name the name
     * @param map  the map
     */
    ClassStruct( String name, String map ) {
      super(name, map, Icons.CLASS_ICON);
    }

    public String toString() {
      return getName() + " -> " + getMappedName();
    }
  }

  /**
   * The type Method struct.
   */
  static final class MethodStruct extends AbstractMappedStruct {
    /**
     * Instantiates a new Method struct.
     *
     * @param name the name
     * @param map  the map
     */
    MethodStruct( String name, String map ) {
      super(name, map, Icons.METHOD_ICON);
    }

    public String toString() {
      return getName() + " -> " + getMappedName();
    }
  }

  private static final class FieldStruct extends AbstractMappedStruct {
    /**
     * Instantiates a new Field struct.
     *
     * @param name the name
     * @param map  the map
     */
    FieldStruct( String name, String map ) {
      super(name, map, Icons.FIELD_ICON);
    }

    public String toString() {
      return getName() + " -> " + getMappedName();
    }
  }

  /**
   * Instantiates a new Y guard log parser.
   */
  public YGuardLogParser() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(null, true);
    this.tree = new DefaultTreeModel(root, true);
  }

  /**
   * Find child default mutable tree node.
   *
   * @param node   the node
   * @param name   the name
   * @param ofType the of type
   * @return the default mutable tree node
   */
  protected DefaultMutableTreeNode findChild( TreeNode node, String name, Class ofType ) {
    return findChild(node, name, ofType, false);
  }

  /**
   * Find child default mutable tree node.
   *
   * @param node   the node
   * @param name   the name
   * @param ofType the of type
   * @param useMap the use map
   * @return the default mutable tree node
   */
  protected DefaultMutableTreeNode findChild( TreeNode node, String name, Class ofType, boolean useMap ) {
    for (Enumeration enumeration = node.children(); enumeration.hasMoreElements(); ) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) enumeration.nextElement();
      Mapped m = (Mapped) child.getUserObject();
      if (ofType == null || ofType.isAssignableFrom(m.getClass())) {
        if (useMap) {
          if (m.getMappedName().equals(name)) {
            return child;
          }
        } else {
          if (m.getName().equals(name)) {
            return child;
          }
        }
      }
    }
    return null;
  }

//  protected DefaultMutableTreeNode find(String name, StringBuffer buffer, boolean useMap) {
//    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getRoot();
//    for (StringTokenizer st = new StringTokenizer(name,".$",false); st.hasMoreElements();) {
//      String token = st.nextToken();
//      DefaultMutableTreeNode child = findChild(node, token, null, useMap);
//      if (child == null) {
//        return null;
//      }
//      node = child;
//    }
//    return node;
//  }

  /**
   * Gets package node.
   *
   * @param packageName the package name
   * @return the package node
   */
  protected DefaultMutableTreeNode getPackageNode( String packageName ) {
    return getPackageNode(packageName, false);
  }

  /**
   * Gets package node.
   *
   * @param packageName the package name
   * @param useMap      the use map
   * @return the package node
   */
  protected DefaultMutableTreeNode getPackageNode( String packageName, boolean useMap ) {
    DefaultMutableTreeNode node = getRoot();
    if (packageName != null) {
      StringTokenizer st = new StringTokenizer(packageName, ".", false);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        DefaultMutableTreeNode child = findChild(node, token, PackageStruct.class, useMap);
        if (child == null) {
          PackageStruct ps = new PackageStruct(token, token);
          child = new DefaultMutableTreeNode(ps, true);
          node.insert(child, calcChildIndex(node, child));
        }
        node = child;
      }
    }
    return node;
  }

  /**
   * Gets class.
   *
   * @param fqn the fqn
   * @return the class
   */
  protected ClassStruct getClass( String fqn ) {
    return (ClassStruct) getClassNode(fqn).getUserObject();
  }

  /**
   * Gets package.
   *
   * @param fqn the fqn
   * @return the package
   */
  protected PackageStruct getPackage( String fqn ) {
    return (PackageStruct) getPackageNode(fqn).getUserObject();
  }

  /**
   * Gets method.
   *
   * @param fqn       the fqn
   * @param signature the signature
   * @return the method
   */
  protected MethodStruct getMethod( String fqn, String signature ) {
    return (MethodStruct) getMethodNode(fqn, signature).getUserObject();
  }

  /**
   * Gets field.
   *
   * @param fqn       the fqn
   * @param signature the signature
   * @return the field
   */
  protected FieldStruct getField( String fqn, String signature ) {
    return (FieldStruct) getFieldNode(fqn, signature).getUserObject();
  }

  /**
   * Gets class node.
   *
   * @param fqn the fqn
   * @return the class node
   */
  protected DefaultMutableTreeNode getClassNode( String fqn ) {
    return getClassNode(fqn, false);
  }

  /**
   * Gets class node.
   *
   * @param fqn    the fqn
   * @param useMap the use map
   * @return the class node
   */
  protected DefaultMutableTreeNode getClassNode( String fqn, boolean useMap ) {
    String packageName;
    String className;
    if (fqn.indexOf('.') < 0) {
      packageName = null;
      className = fqn;
    } else {
      packageName = fqn.substring(0, fqn.lastIndexOf('.'));
      className = fqn.substring(fqn.lastIndexOf('.') + 1);
    }
    DefaultMutableTreeNode pn = getPackageNode(packageName);
    if (className.indexOf('$') > 0) {
      for (StringTokenizer st = new StringTokenizer(className, "$", false); st.hasMoreTokens(); ) {
        String token = st.nextToken();
        DefaultMutableTreeNode child = findChild(pn, token, ClassStruct.class, useMap);
        if (child == null) {
          child = new DefaultMutableTreeNode(new ClassStruct(token, token), true);
          pn.insert(child, calcChildIndex(pn, child));
        }
        pn = child;
      }
      return pn;
    } else {
      DefaultMutableTreeNode child = findChild(pn, className, ClassStruct.class, useMap);
      if (child == null) {
        child = new DefaultMutableTreeNode(new ClassStruct(className, className), true);
        pn.insert(child, calcChildIndex(pn, child));
      }
      return child;
    }
  }

  /**
   * Gets method node.
   *
   * @param cname the cname
   * @param fqn   the fqn
   * @return the method node
   */
  protected DefaultMutableTreeNode getMethodNode( String cname, String fqn ) {
    return getMethodNode(cname, fqn, false);
  }


  /**
   * Gets method node.
   *
   * @param cname  the cname
   * @param fqn    the fqn
   * @param useMap the use map
   * @return the method node
   */
  protected DefaultMutableTreeNode getMethodNode( String cname, String fqn, boolean useMap ) {
    DefaultMutableTreeNode cn = getClassNode(cname);
    DefaultMutableTreeNode child = findChild(cn, fqn, MethodStruct.class, useMap);
    if (child == null) {
      MethodStruct ms = new MethodStruct(fqn, fqn);
      child = new DefaultMutableTreeNode(ms, false);
      cn.insert(child, calcChildIndex(cn, child));
    }
    return child;
  }

  private int calcChildIndex( DefaultMutableTreeNode cn, DefaultMutableTreeNode child ) {
    int left = 0;
    int right = cn.getChildCount() - 1;
    Object userObject = child.getUserObject();
    while (right >= left) {
      int test = (left + right) / 2;
      Object testObject = ((DefaultMutableTreeNode) cn.getChildAt(test)).getUserObject();
      int cmp = compare(userObject, testObject);
      if (cmp == 0) {
        return test;
      } else {
        if (cmp < 0) {
          right = test - 1;
        } else {
          left = test + 1;
        }
      }
    }
    return left;
  }

  private int compare( Object o1, Object o2 ) {
    Mapped m1 = (Mapped) o1;
    Mapped m2 = (Mapped) o2;
    if (m1.getClass() != m2.getClass()) {
      if (m1.getClass() == PackageStruct.class) {
        return -1;
      } else if (m2.getClass() == PackageStruct.class) {
        return 1;
      }
      if (m1.getClass() == ClassStruct.class) {
        return -1;
      } else if (m2.getClass() == ClassStruct.class) {
        return 1;
      }
      if (m1.getClass() == MethodStruct.class) {
        return -1;
      } else if (m2.getClass() == MethodStruct.class) {
        return 1;
      }
    }
    return m1.getName().compareTo(m2.getName());
  }

  /**
   * Gets field node.
   *
   * @param cname the cname
   * @param fqn   the fqn
   * @return the field node
   */
  protected DefaultMutableTreeNode getFieldNode( String cname, String fqn ) {
    return getFieldNode(cname, fqn, false);
  }

  /**
   * Gets field node.
   *
   * @param cname  the cname
   * @param fqn    the fqn
   * @param useMap the use map
   * @return the field node
   */
  protected DefaultMutableTreeNode getFieldNode( String cname, String fqn, boolean useMap ) {
    DefaultMutableTreeNode cn = getClassNode(cname);
    DefaultMutableTreeNode child = findChild(cn, fqn, FieldStruct.class, useMap);
    if (child == null) {
      FieldStruct ms = new FieldStruct(fqn, fqn);
      child = new DefaultMutableTreeNode(ms, false);
      cn.insert(child, calcChildIndex(cn, child));
    }
    return child;
  }

  /**
   * Parse.
   *
   * @param file the file
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException                 the sax exception
   * @throws IOException                  the io exception
   */
  void parse( final File file ) throws ParserConfigurationException, SAXException, IOException {
    if (file.getName().toLowerCase().endsWith(".gz")) {
      parse(new InputSource(new GZIPInputStream(new FileInputStream(file))));
    } else {
      URL url = file.toURI().toURL();
      if (url != null) {
        parse(url);
      }
    }
  }

  /**
   * Parse.
   *
   * @param url the url
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException                 the sax exception
   * @throws IOException                  the io exception
   */
  public void parse( URL url ) throws ParserConfigurationException, SAXException, IOException {
    parse(new InputSource(url.openStream()));
  }

  /**
   * Parse.
   *
   * @param is the is
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException                 the sax exception
   * @throws IOException                  the io exception
   */
  public void parse( InputSource is ) throws ParserConfigurationException, SAXException, IOException {
    SAXParserFactory f = SAXParserFactory.newInstance();
    f.setValidating(false);
    SAXParser parser = f.newSAXParser();
    XMLReader r = parser.getXMLReader();
    r.setContentHandler(contentHandler);
    r.parse(is);
  }

  /**
   * Translate string.
   *
   * @param fqn the fqn
   * @return the string
   */
  public String translate( String fqn ) {
    DefaultMutableTreeNode node = getRoot();

    final StringBuffer ocnSb = new StringBuffer();

    final StringBuffer sb = new StringBuffer();
    boolean buildPrefix = true;
    for (StringTokenizer st = new StringTokenizer(fqn, "$.", true); st.hasMoreTokens(); ) {
      String token = st.nextToken();
      sb.append(token);

      if ("$".equals(token) || ".".equals(token)) {
        continue;
      }

      final boolean hasNext = st.hasMoreTokens();
      final Class type = hasNext ? null : ClassStruct.class;
      DefaultMutableTreeNode child = findChild(node, sb.toString(), type, true);
      if (child == null) {
        if (buildPrefix && hasNext) {
          // next token is a dot ...
          st.nextToken();
          // ... however obfuscation prefixes are prepended with a slash delimiter
          sb.append('/');
          continue;
        } else {
          if (hasNext) {
            ocnSb.append(sb.toString().replace('/', '.'));
            append(ocnSb, st);
          } else if (buildPrefix) {
            ocnSb.append(fqn);
          } else if (node.getUserObject().getClass() == ClassStruct.class) {
            ocnSb.append(translateMethodName(node, sb.toString()));
          } else {
            ocnSb.append(sb.toString().replace('/', '.'));
          }
          node = null;
          break;
        }
      }

      buildPrefix = false;
      sb.setLength(0);
      node = child;

      ocnSb.append(getOriginalName(child));
      if (st.hasMoreTokens()) {
        ocnSb.append(st.nextToken());
      }
    }

    return ocnSb.toString();
  }

  /**
   * Translate my stack trace element.
   *
   * @param ste the ste
   * @return the my stack trace element
   */
  public MyStackTraceElement translate( MyStackTraceElement ste ) {
    try {
      DefaultMutableTreeNode classNode = getRoot();
      int dollarPos = ste.getClassName().indexOf('$');
      if (dollarPos < 0) {
        dollarPos = ste.getClassName().length();
      }
      int lastDot = ste.getClassName().substring(0, dollarPos).lastIndexOf('.');

      String packageName = ste.getClassName().substring(0, lastDot + 1);
      String classAndInnerClassName = ste.getClassName().substring(lastDot + 1);

      final StringBuffer ocnSb = new StringBuffer();

      final StringBuffer sb = new StringBuffer();
      boolean buildPrefix = true;
      for (StringTokenizer st = new StringTokenizer(packageName, ".", true); st.hasMoreTokens(); ) {
        String token = st.nextToken();
        sb.append(token);

        DefaultMutableTreeNode child = findChild(classNode, sb.toString(), PackageStruct.class, true);
        if (child == null) {
          if (buildPrefix && st.hasMoreTokens()) {
            // next token is a dot ...
            st.nextToken();
            // ... however obfuscation prefixes are prepended with a slash delimiter
            sb.append('/');
            continue;
          } else {
            classNode = null;
            break;
          }
        }

        buildPrefix = false;
        sb.setLength(0);
        classNode = child;

        ocnSb.append(getOriginalName(classNode));
        if (st.hasMoreTokens()) {
          ocnSb.append(st.nextToken());
        }
      }
      if (buildPrefix) {
        classNode = null;
      }

      sb.setLength(0);
      for (StringTokenizer st = new StringTokenizer(classAndInnerClassName, "$.", true); st.hasMoreTokens(); ) {
        String token = st.nextToken();
        sb.append(token);
        if (!"$".equals(token) && !".".equals(token)) {
          token = sb.toString();
          sb.setLength(0);

          DefaultMutableTreeNode child = findChild(classNode, token, ClassStruct.class, true);
          if (child == null) {
            ocnSb.append(token);
            append(ocnSb, st);
            classNode = null;
            break;
          }
          classNode = child;
          ocnSb.append(getOriginalName(classNode));
          if (st.hasMoreTokens()) {
            ocnSb.append(st.nextToken());
          }
        }
      }

      final String newMethodName = translateMethodName(classNode, ste.getMethodName());

      int lineNumber = 0;
      final String originalClassName = ocnSb.toString();

      try {
        lineNumber = ste.getLineNumber();
        if (lineNumber > 0) {
          Map property = (Map) this.contentHandler.ownerProperties.get(originalClassName);
          long salt = -1;
          if (property != null) {
            String saltString = (String) property.get("scrambling-salt");
            if (saltString != null) {
              try {
                salt = Long.parseLong(saltString);
                final long seed = salt ^ originalClassName.replace('$', '.').hashCode();

                final ObfuscatorTask.LineNumberScrambler scrambler = new ObfuscatorTask.LineNumberScrambler(3584, seed);
                lineNumber = scrambler.unscramble(lineNumber);
              } catch (NumberFormatException nfe) {
                // too bad
              }
            }
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      String fileName = classNode == null ? "" : buildFilename(originalClassName);
      return new MyStackTraceElement(originalClassName, newMethodName, fileName, lineNumber);
    } catch (Exception e) {
      return ste;
    }
  }

  private static String translateMethodName( DefaultMutableTreeNode node, String mappedName ) {
    final StringBuffer originalName = new StringBuffer();
    if (node != null) {
      String del = "";
      for (Enumeration en = node.children(); en.hasMoreElements(); ) {
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
        Mapped mapped = (Mapped) child.getUserObject();
        if (mapped.getClass() == MethodStruct.class) {
          if (mapped.getMappedName().equals(mappedName)) {
            String name = mapped.getName();
            // strip empty signature
            int braceIndex = name.indexOf('(');
            if (0 < braceIndex && braceIndex + 1 == name.indexOf(')')) {
              name = name.substring(0, braceIndex);
            }
            // strip return value
            int spaceIndex = name.lastIndexOf(' ', braceIndex < 0 ? name.length() : braceIndex);
            if (0 < spaceIndex) {
              name = name.substring(spaceIndex + 1);
            }
            originalName.append(del).append(name);
            del = "|";
          }
        }
      }
    }
    return originalName.length() < 1 ? mappedName : originalName.toString();
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main( String[] args ) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage java -jar yguard.jar logfile.xml[.gz] [-pipe] [name]");
      System.out.println(" where 'logfile.xml' is the logfile that has been generated ");
      System.out.println(" during the obfuscation process");
      System.out.println(" and which may be gzipped (with .gz extension)");
      System.out.println(" and where 'name' is an optional string, which will be translated");
      System.out.println(" according to the logfile automatically.");
      System.out.println(" If no 'name' is given, a tiny GUI will popup that will help in translating");
      System.out.println(" stacktraces, fully qualified classnames etc.");
      System.out.println(" If '-pipe' is specified as the last argument after the logfile the tool");
      System.out.println(" will translate the input from standard in and output the translation to");
      System.out.println(" standard out until the input is closed.");
      System.exit(-1);
    }
    final File file = new File(args[0]);
    if (!file.isFile() || !file.canRead()) {
      System.err.println("Could not open file " + args[0]);
      System.exit(-1);
    }

    if (args.length < 2) {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          (new LogParserView()).show(file);
        }
      });
    } else {
      final YGuardLogParser parser = new YGuardLogParser();
      parser.parse(file);

      if (args[1].equals("-pipe")) {
        InputStreamReader er = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(er);
        String s;
        while ((s = br.readLine()) != null) {
          System.out.println(parser.translate(new String[]{s})[0]);
        }
      } else {
        String[] strings = new String[args.length - 1];
        System.arraycopy(args, 1, strings, 0, args.length - 1);
        String[] s = parser.translate(strings);
        for (int i = 0; i < s.length; i++) {
          System.out.println(s[i]);
        }
      }
    }
  }
//  at A.A.A.A.E.main([Ljava.lang.String;)V(y:1866)
//  at A.A.A.A.E.main([Ljava.lang.String;)V(Unknown Source)
//  at java.lang.Thread.run()V(Unknown Source)
//  at java.lang.Thread.startThreadFromVM(Ljava.lang.Thread;)V(Unknown
//java.lang.Exception: Stack trace
//  at java.lang.Thread.dumpStack(Thread.java:1158)
//  at A.A.A.A.E.main(y:1866)
//  at A.A.A.A.E.main(Unknown Source)


  /**
   * Translate string [ ].
   *
   * @param args the args
   * @return the string [ ]
   */
  String[] translate( String[] args ) {
    String[] resultArr = new String[args.length];
    final Pattern jrockitPattern = Pattern.compile("(.*\\s+)?([^;()\\s]+)\\.([^;()\\s]+)\\(([^)]*)\\)(.+)\\(([^:)]+)(?::(\\d*))?\\)(.*)");
    final Pattern stePattern = Pattern.compile("(.*\\s+)?([^(\\s]+)\\.([^(\\s]+)\\(([^:)]*)(?::(\\d*))?\\)(.*)");
    final Pattern fqnPattern = Pattern.compile("([^:;()\\s]+\\.)+([^:;()\\s]+)");

    for (int i = 0; i < args.length; i++) {
      args[i] = CharConverter.convert(args[i]);
      Matcher m2 = jrockitPattern.matcher(args[i]);
      if (m2.matches()) {
        final String[] moduleAndType = split(m2.group(2));

        MyStackTraceElement ste;
        if (m2.group(7) == null) {
          ste = new MyStackTraceElement(moduleAndType[1], m2.group(3), "", 0);
        } else {
          ste = new MyStackTraceElement(moduleAndType[1], m2.group(3), m2.group(6), Integer.parseInt(m2.group(7)));
        }
        String params = m2.group(4);
        try {
          params = Conversion.toJavaArguments(params);
        } catch (RuntimeException rte) {
          // ignore
        }
        resultArr[i] =
                (m2.group(1) != null ? m2.group(1) : "") +
                moduleAndType[0] +
                format(ste, m2.group(7) == null ? m2.group(6) : null) +
                " [" + params + "]" + m2.group(8);
      } else {
        Matcher m = stePattern.matcher(args[i]);
        if (m.matches()) {
          final String[] moduleAndType = split(m.group(2));

          MyStackTraceElement ste;
          if (m.group(5) == null) {
            ste = new MyStackTraceElement(moduleAndType[1], m.group(3), "", 0);
          } else {
            ste = new MyStackTraceElement(moduleAndType[1], m.group(3), m.group(4), Integer.parseInt(m.group(5)));
          }
          resultArr[i] =
                  (m.group(1) != null ? m.group(1) : "") +
                  moduleAndType[0] +
                  format(translate(ste), m.group(5) == null ? m.group(4) : null) +
                  m.group(6);
        } else {
          StringBuffer replacement = new StringBuffer();
          final Matcher fqnMatcher = fqnPattern.matcher(args[i]);
          while (fqnMatcher.find()) {
            final String[] moduleAndType = split(fqnMatcher.group());
            String result;
            try {
              result = translate(moduleAndType[1]);
            } catch (Exception ex) {
              result = moduleAndType[1];
            }
            fqnMatcher.appendReplacement(replacement, moduleAndType[0] + escapeReplacement(result));
          }
          fqnMatcher.appendTail(replacement);
          resultArr[i] = replacement.toString();
        }
      }
    }
    return resultArr;
  }

  private static String[] split( final String moduleAndType ) {
    if (moduleAndType == null) {
      return new String[]{"", ""};
    } else {
      final int idx1 = moduleAndType.indexOf('$');
      final int idx2 = idx1 > -1 ? moduleAndType.lastIndexOf('/', idx1) : moduleAndType.lastIndexOf('/');
      if (idx2 > -1) {
        return new String[]{
                moduleAndType.substring(0, idx2 + 1),
                moduleAndType.substring(idx2 + 1)
        };
      } else {
        return new String[]{"", moduleAndType};
      }
    }
  }

  private static String format( final MyStackTraceElement ste, final String s ) {
    final String fn = ste.getFileName();
    if ((fn == null || fn.length() == 0) && s != null) {
      return ste.getClassName() + '.' +
             ste.getMethodName() + '(' + s + ')';
    } else {
      return ste.toString();
    }
  }

  /**
   * Gets tree model.
   *
   * @return the tree model
   */
  DefaultTreeModel getTreeModel() {
    return tree;
  }

  private DefaultMutableTreeNode getRoot() {
    return (DefaultMutableTreeNode) tree.getRoot();
  }

  private static String getOriginalName( DefaultMutableTreeNode node ) {
    return ((Mapped) node.getUserObject()).getName();
  }

  private static String escapeReplacement( String replacementString ) {
    if ((replacementString.indexOf('\\') == -1) && (replacementString.indexOf('$') == -1)) {
      return replacementString;
    }
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < replacementString.length(); i++) {
      char c = replacementString.charAt(i);
      if (c == '\\') {
        result.append('\\').append('\\');
      } else if (c == '$') {
        result.append('\\').append('$');
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }

  private static StringBuffer append( StringBuffer sb, StringTokenizer st ) {
    while (st.hasMoreTokens()) {
      sb.append(st.nextToken());
    }
    return sb;
  }

  private static String buildFilename( String qualifiedName ) {
    String fileName = "";
    int idxDot = qualifiedName.lastIndexOf('.');
    if (idxDot > 0) {
      fileName = qualifiedName.substring(idxDot + 1);
    } else {
      fileName = qualifiedName;
    }
    int idxDollar = fileName.indexOf('$');
    if (idxDollar > 0) {
      fileName = fileName.substring(0, idxDollar);
    }
    return fileName + ".java";
  }

  private class MyContentHandler implements ContentHandler {
    private boolean inMapSection;
    private boolean inLogSection;
    private boolean inExposeSection;
    /**
     * The Owner properties.
     */
    final Map ownerProperties = new HashMap();

    public void characters( char[] ch, int start, int length ) throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void endElement( String uri, String localName, String qName ) throws SAXException {
      if ("expose".equals(qName)) {
        inExposeSection = false;
      }
      if ("map".equals(qName)) {
        inMapSection = false;
      }
      if ("yguard".equals(qName)) {
        inLogSection = false;
      }
    }

    public void endPrefixMapping( String prefix ) throws SAXException {
    }

    public void ignorableWhitespace( char[] ch, int start, int length ) throws SAXException {
    }

    public void processingInstruction( String target, String data ) throws SAXException {
    }

    public void setDocumentLocator( Locator locator ) {
    }

    public void skippedEntity( String name ) throws SAXException {
    }

    public void startDocument() throws SAXException {
      ownerProperties.clear();
    }

    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {
      if ("expose".equals(qName)) {
        inExposeSection = true;
      }
      if ("map".equals(qName)) {
        inMapSection = true;
      }
      if ("yguard".equals(qName)) {
        inLogSection = true;
        String version = attributes.getValue("version");
        if ("1.5".compareTo(version) < 0) {
          throw new IllegalStateException("Version should not be greater than 1.5 but was " + version);
        }
      }
      if (inLogSection && !inMapSection) {
        if ("property".equals(qName)) {
          String key = attributes.getValue("name");
          String value = attributes.getValue("value");
          String owner = attributes.getValue("owner");
          Map map = (Map) ownerProperties.get(owner);
          if (map == null) {
            map = new HashMap();
            ownerProperties.put(owner, map);
          }
          map.put(key, value);
        }
      }
      if (inExposeSection) {
        if ("method".equals(qName)) {
          String className = attributes.getValue("class");
          String name = attributes.getValue("name");
          MethodStruct fs = getMethod(className, name);
        }
        if ("field".equals(qName)) {
          String className = attributes.getValue("class");
          String name = attributes.getValue("name");
          FieldStruct fs = getField(className, name);
        }
        if ("package".equals(qName)) {
          String name = attributes.getValue("name");
          PackageStruct ps = getPackage(name);
        }
        if ("class".equals(qName)) {
          String name = attributes.getValue("name");
          ClassStruct cs = YGuardLogParser.this.getClass(name);
        }
      }
      if (inMapSection) {
        if ("method".equals(qName)) {
          String className = attributes.getValue("class");
          String name = attributes.getValue("name");
          String map = attributes.getValue("map");
          MethodStruct fs = getMethod(className, name);
          fs.setMappedName(map);
        }
        if ("field".equals(qName)) {
          String className = attributes.getValue("class");
          String name = attributes.getValue("name");
          String map = attributes.getValue("map");
          FieldStruct fs = getField(className, name);
          fs.setMappedName(map);
        }
        if ("package".equals(qName)) {
          String name = attributes.getValue("name");
          String map = attributes.getValue("map");
          PackageStruct ps = getPackage(name);
          ps.setMappedName(map);
        }
        if ("class".equals(qName)) {
          String name = attributes.getValue("name");
          String map = attributes.getValue("map");
          ClassStruct cs = YGuardLogParser.this.getClass(name);
          cs.setMappedName(map);
        }
      }
    }

    public void startPrefixMapping( String prefix, String uri ) throws SAXException {
    }
  }

  /**
   * The type Char converter.
   */
  public static final class CharConverter {
    private static final Pattern unicodeEscape = Pattern.compile("&#(\\d{1,5});");

    /**
     * Convert string.
     *
     * @param s the s
     * @return the string
     */
    public static String convert( String s ) {
      StringBuilder r = new StringBuilder();

      Matcher matcher = unicodeEscape.matcher(s);

      int lastMatchEnd = 0;

      while (matcher.find()) {
        String match = matcher.group(1);
        r.append(s, lastMatchEnd, matcher.start());
        r.append((char) (Integer.parseInt(match)));
        lastMatchEnd = matcher.end();
      }
      r.append(s.substring(lastMatchEnd));

      return r.toString();
    }
  }

  /**
   * The type My stack trace element.
   */
  public static final class MyStackTraceElement {
    private final String className;
    private String methodName;
    private String fileName;
    private int lineNumber;

    /**
     * Instantiates a new My stack trace element.
     *
     * @param className  the class name
     * @param methodName the method name
     * @param fileName   the file name
     * @param lineNumber the line number
     */
    public MyStackTraceElement( String className, String methodName, String fileName, int lineNumber ) {
      this.className = className;
      this.methodName = methodName;
      this.fileName = fileName;
      this.lineNumber = lineNumber;
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
      return className;
    }

    /**
     * Sets method name.
     *
     * @param methodName the method name
     */
    public void setMethodName( String methodName ) {
      this.methodName = methodName;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
      return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName the file name
     */
    public void setFileName( String fileName ) {
      this.fileName = fileName;
    }

    /**
     * Gets line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
      return lineNumber;
    }

    /**
     * Sets line number.
     *
     * @param lineNumber the line number
     */
    public void setLineNumber( int lineNumber ) {
      this.lineNumber = lineNumber;
    }

    /**
     * Gets method name.
     *
     * @return the method name
     */
    public String getMethodName() {
      return methodName;
    }

    public String toString() {
      return getClassName() + "." + getMethodName() + "(" + (fileName != null && lineNumber >= 0 ? fileName + ":" + lineNumber : "unknown source") + ")";
    }
  }

  /**
   * The type Icons.
   */
  public static final class Icons implements Icon {
    /**
     * The constant CLASS_ICON.
     */
    public static final Icon CLASS_ICON = new Icons(Color.blue, "C");
    /**
     * The constant METHOD_ICON.
     */
    public static final Icon METHOD_ICON = new Icons(Color.red, "M");
    /**
     * The constant PACKAGE_ICON.
     */
    public static final Icon PACKAGE_ICON = new Icons(Color.yellow, "P");
    /**
     * The constant FIELD_ICON.
     */
    public static final Icon FIELD_ICON = new Icons(Color.green, "F");

    private static final Ellipse2D circle = new Ellipse2D.Double(1, 1, 14, 14);

    /**
     * The Color.
     */
    protected Color color;
    /**
     * The Label.
     */
    protected String label;

    /**
     * Instantiates a new Icons.
     *
     * @param color the color
     * @param label the label
     */
    public Icons( Color color, String label ) {
      this.color = color;
      this.label = label;
    }

    public void paintIcon( Component c, Graphics g, int x, int y ) {
      g.translate(x, y);
      g.setColor(color);
      Graphics2D g2d = (Graphics2D) g;
      Object a = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.fill(circle);
      g2d.setColor(color.darker());
      g2d.draw(circle);
      float width = (float) g2d.getFontMetrics().getStringBounds(label, g2d).getWidth();
      g2d.setColor(Color.black);
      g2d.drawString(label, 9 - width * 0.5f, 14);
      g2d.setColor(Color.white);
      g2d.drawString(label, 8 - width * 0.5f, 13);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, a);
      g.translate(-x, -y);
    }

    public int getIconWidth() {
      return 16;
    }

    public int getIconHeight() {
      return 16;
    }
  }
}
