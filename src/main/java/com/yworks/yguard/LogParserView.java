package com.yworks.yguard;

import com.yworks.yguard.YGuardLogParser.ClassStruct;
import com.yworks.yguard.YGuardLogParser.Mapped;
import com.yworks.yguard.YGuardLogParser.MethodStruct;
import com.yworks.yguard.YGuardLogParser.PackageStruct;
import com.yworks.util.Version;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

/**
 * Displays a browsable yGuard mapping file and provides controls for
 * de-obfuscating obfuscated stacktraces.
 *
 * @author Thomas Behr
 */
class LogParserView {
  /**
   * Instantiates a new Log parser view.
   */
  LogParserView() {
  }

  /**
   * Show.
   *
   * @param initialPath the initial path
   */
  void show( final File initialPath ) {
    final JTree tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
    tree.setCellRenderer(new TreeCellRenderer() {
      DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JComponent c = (JComponent) dtcr.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode dmtr = (DefaultMutableTreeNode) value;
        if (dmtr.getUserObject() != null) {
          dtcr.setIcon(((Mapped)dmtr.getUserObject()).getIcon());
        }
        return c;
      }
    });
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);


    final JPanel textPanel = new JPanel(new BorderLayout());
    final JTextArea textArea = new JTextArea();
    textArea.setMinimumSize(new Dimension(600, 200));
    final JScrollPane textScrollPane = new JScrollPane(textArea);
    textScrollPane.getViewport().setPreferredSize(new Dimension(400, 200));
    textPanel.add(textScrollPane, BorderLayout.CENTER);
    final JButton button = new JButton("Deobfuscate!");
    button.setMnemonic('D');
    textPanel.add(button, BorderLayout.SOUTH);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deobfuscate(getParser(tree), textArea);
      }
    });

    final JPanel top = new JPanel(new BorderLayout());
    top.add(new JScrollPane(tree), BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0,0));
    buttonPanel.add(new JButton(new AbstractAction("Sort by Mapping") {
      public void actionPerformed(ActionEvent e) {
        final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        sort(model, new MappedNameComparator());
      }
    }));
    buttonPanel.add(new JButton(new AbstractAction("Sort by Names") {
      public void actionPerformed(ActionEvent e) {
        final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        sort(model, new NameComparator());
      }
    }));
    top.add(buttonPanel, BorderLayout.NORTH);


    final JFrame frame = new JFrame(newTitle(initialPath.getAbsolutePath()));

    final JMenu recent = new JMenu("Open Recent");

    final JFileChooser jfc = new JFileChooser();
    jfc.addChoosableFileFilter(new FileFilterImpl(".gz", "Compressed XML (*.gz)"));
    jfc.addChoosableFileFilter(new FileFilterImpl(".xml", "XML (*.xml)"));
    jfc.setAcceptAllFileFilterUsed(true);
    jfc.setFileFilter(jfc.getAcceptAllFileFilter());

    final File parent = initialPath.getParentFile();
    if (parent != null) {
      jfc.setCurrentDirectory(parent);
    }

    final UiContext ctx = new UiContext(frame, tree, textArea, recent, jfc);

    try {
      setParser(tree, newParser(initialPath));
      addRecent(ctx, initialPath);
    } catch (Exception ex) {
      setParser(tree, new YGuardLogParser());

      frame.setTitle("Element Mapping - yGuard " + Version.getVersion());

      final String msg = toErrorMessage(initialPath, ex);
      frame.addComponentListener(new ComponentAdapter() {
        public void componentShown( final ComponentEvent e ) {
          frame.removeComponentListener(this);
          EventQueue.invokeLater(new Runnable() {
            public void run() {
              showErrorMessage(msg, tree);
            }
          });
        }
      });
    }

    final JMenu file = new JMenu("File");
    file.add(new AbstractOpenAction(ctx, "Open") {
      public void actionPerformed( final ActionEvent e ) {
        final JFileChooser jfc = context.fileChooser;
        if (jfc.showOpenDialog(top) == JFileChooser.APPROVE_OPTION) {
          open(jfc.getSelectedFile());
        }
      }

      @Override
      void onOpened( final UiContext context, final File path ) {
        LogParserView.addRecent(context, path);
        super.onOpened(context, path);
      }
    });
    file.add(recent);
    file.addSeparator();
    file.add(new AbstractAction("Quit") {
      public void actionPerformed( final ActionEvent e ) {
        System.exit(0);
      }
    });

    final JMenu help = new JMenu("?");
    help.add(new AbstractAction("About") {
      public void actionPerformed( final ActionEvent e ) {
        final JLabel jl = new JLabel("Element Mapping - yGuard " + Version.getVersion());
        JOptionPane.showMessageDialog(top, jl, "About", JOptionPane.PLAIN_MESSAGE);
      }
    });
    final JMenuBar jmb = new JMenuBar();
    jmb.add(file);
    jmb.add(help);


    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(jmb);
    frame.setContentPane(new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, textPanel));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * Add recent.
   *
   * @param context the context
   * @param path    the path
   */
  static void addRecent( final UiContext context, final File path ) {
    final JMenu menu = context.recentMenu;
    final int n = menu.getItemCount();
    if (n > 9) {
      menu.remove(n - 1);
    }

    final RecentAction ra = new RecentAction(context, path);
    final JMenuItem jc = menu.add(ra);
    ra.setItem(jc);
    if (n > 0) {
      menu.remove(menu.getItemCount() - 1);
      menu.add(jc, 0);
    }
  }

  /**
   * Show error message.
   *
   * @param text   the text
   * @param parent the parent
   */
  static void showErrorMessage( final String text, final JComponent parent ) {
    final JTextArea jta = new JTextArea(text);
    jta.setEditable(false);
    final JScrollPane jsp = new JScrollPane(jta);
    jsp.setPreferredSize(new Dimension(400, 600));
    JOptionPane.showMessageDialog(parent, jsp, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * To error message string.
   *
   * @param file the file
   * @param ex   the ex
   * @return the string
   */
  static String toErrorMessage( final File file, final Exception ex ) {
    final StringWriter sw = new StringWriter();
    sw.write("Could not read ");
    sw.write(file.getAbsolutePath());
    sw.write(":\n");
    ex.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  /**
   * Gets parser.
   *
   * @param tree the tree
   * @return the parser
   */
  static YGuardLogParser getParser( final JTree tree ) {
    return (YGuardLogParser) tree.getClientProperty("PARSER");
  }

  /**
   * Sets parser.
   *
   * @param tree   the tree
   * @param parser the parser
   */
  static void setParser( final JTree tree, final YGuardLogParser parser ) {
    tree.setModel(parser.getTreeModel());
    tree.putClientProperty("PARSER", parser);
  }

  /**
   * New parser y guard log parser.
   *
   * @param file the file
   * @return the y guard log parser
   * @throws Exception the exception
   */
  static YGuardLogParser newParser( final File file ) throws Exception {
    final YGuardLogParser parser = new YGuardLogParser();
    parser.parse(file);
    return parser;
  }

  /**
   * New title string.
   *
   * @param path the path
   * @return the string
   */
  static String newTitle( final String path ) {
    final String t = path == null ? "" : path.trim();
    if (t.length() > 31) {
      return newTitleSuffix(t);
    } else {
      return t;
    }
  }

  /**
   * New title suffix string.
   *
   * @param path the path
   * @return the string
   */
  static String newTitleSuffix( final String path ) {
    int idx = path.lastIndexOf(File.separatorChar);
    if (idx > -1) {
      idx = path.lastIndexOf(File.separatorChar, idx - 1);
    }
    if (idx > -1) {
      return "..." + path.substring(idx);
    } else {
      return path;
    }
  }

  /**
   * Deobfuscate.
   *
   * @param parser   the parser
   * @param textArea the text area
   */
  static void deobfuscate(
          final YGuardLogParser parser, final JTextArea textArea
  ) {
    String[] lines = textArea.getText().split("\n");
    lines = parser.translate(lines);
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < lines.length; ++i) {
      sb.append(lines[i]).append("\n");
    }
    textArea.setText(sb.toString());
    textArea.setCaretPosition(0);
  }

  /**
   * Sort.
   *
   * @param model the model
   * @param c     the c
   */
  static void sort( final DefaultTreeModel model, final Comparator c ) {
    sortRecursively((DefaultMutableTreeNode) model.getRoot(), c);
    model.nodeStructureChanged((DefaultMutableTreeNode) model.getRoot());
  }

  private static void sortRecursively(DefaultMutableTreeNode parent, Comparator c) {
    if (parent.getChildCount() > 0) {
      if (parent.getChildCount() > 1) {
        sort(parent, c);
      }
      for (Enumeration enu = parent.children(); enu.hasMoreElements();) {
        DefaultMutableTreeNode tn =  (DefaultMutableTreeNode) enu.nextElement();
        sortRecursively(tn, c);
      }
    }
  }

  private static void sort(DefaultMutableTreeNode parent, Comparator c) {
    DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[parent.getChildCount()];
    for (int i = 0; i < children.length; ++i) {
      children[i] = ((DefaultMutableTreeNode) parent.getChildAt(i));
    }
    parent.removeAllChildren();
    Arrays.sort(children, c);
    for (int i = 0; i < children.length; ++i) {
      parent.add(children[i]);
    }
  }


  /**
   * The type Mapped name comparator.
   */
  static final class MappedNameComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Mapped m1 = (Mapped) ((DefaultMutableTreeNode)o1).getUserObject();
      Mapped m2 = (Mapped) ((DefaultMutableTreeNode)o2).getUserObject();
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
      return m1.getMappedName().compareTo(m2.getMappedName());
    }
  }


  /**
   * The type Name comparator.
   */
  static final class NameComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Mapped m1 = (Mapped) ((DefaultMutableTreeNode)o1).getUserObject();
      Mapped m2 = (Mapped) ((DefaultMutableTreeNode)o2).getUserObject();
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
  }

  private static class FileFilterImpl extends FileFilter {
    private final String suffix;
    private final String description;

    /**
     * Instantiates a new File filter.
     *
     * @param suffix      the suffix
     * @param description the description
     */
    FileFilterImpl( final String suffix, final String description ) {
      this.suffix = suffix == null ? "" : suffix.toLowerCase();
      this.description = description;
    }

    public boolean accept( final File f ) {
      return f.isDirectory() || f.getName().toLowerCase().endsWith(suffix);
    }

    public String getDescription() {
      return description;
    }
  }


  private static final class UiContext {
    /**
     * The Frame.
     */
    final JFrame frame;
    /**
     * The Mapping tree.
     */
    final JTree mappingTree;
    /**
     * The Text area.
     */
    final JTextArea textArea;
    /**
     * The Recent menu.
     */
    final JMenu recentMenu;
    /**
     * The File chooser.
     */
    final JFileChooser fileChooser;

    /**
     * Instantiates a new Ui context.
     *
     * @param frame       the frame
     * @param mappingTree the mapping tree
     * @param textArea    the text area
     * @param recentMenu  the recent menu
     * @param fileChooser the file chooser
     */
    UiContext(
            final JFrame frame,
            final JTree mappingTree,
            final JTextArea textArea,
            final JMenu recentMenu,
            final JFileChooser fileChooser
    ) {
      this.frame = frame;
      this.mappingTree = mappingTree;
      this.textArea = textArea;
      this.recentMenu = recentMenu;
      this.fileChooser = fileChooser;
    }
  }

  private abstract static class AbstractOpenAction extends AbstractAction {
    /**
     * The Context.
     */
    final UiContext context;

    /**
     * Instantiates a new Abstract open action.
     *
     * @param context the context
     * @param name    the name
     */
    AbstractOpenAction( final UiContext context, final String name ) {
      super(name);
      this.context = context;
    }

    /**
     * Open.
     *
     * @param path the path
     */
    void open( final File path ) {
      final JTree tree = context.mappingTree;
      try {
        setParser(tree, newParser(path));
        onOpened(context, path);
      } catch (Exception ex) {
        showErrorMessage(toErrorMessage(path, ex), tree);
      }
    }

    /**
     * On opened.
     *
     * @param context the context
     * @param path    the path
     */
    void onOpened( final UiContext context, final File path ) {
      context.textArea.setText("");
      context.frame.setTitle(newTitle(path.getAbsolutePath()));
    }
  }

  private static final class RecentAction extends AbstractOpenAction {
    /**
     * The Path.
     */
    final File path;
    /**
     * The Item.
     */
    JMenuItem item;

    /**
     * Instantiates a new Recent action.
     *
     * @param context the context
     * @param path    the path
     */
    RecentAction( final UiContext context, final File path ) {
      super(context, LogParserView.newTitleSuffix(path.getAbsolutePath()));
      this.path = path;
    }

    @Override
    public void actionPerformed( final ActionEvent e ) {
      open(path);
    }

    @Override
    void onOpened( final UiContext context, final File path ) {
      updateRecent(context, path);
      updateFileChooser(context, path);
      super.onOpened(context, path);
    }

    private void updateRecent( final UiContext context, final File path ) {
      final JMenu menu = context.recentMenu;
      final JMenuItem ami = getItem();
      if (ami != null) {
        for (int i = 0, n = menu.getItemCount(); i < n; ++i) {
          final JMenuItem mi = menu.getItem(i);
          if (mi == ami) {
            menu.remove(i);
            menu.add(ami, 0);
            break;
          }
        }
      }
    }

    private void updateFileChooser( final UiContext context, final File path ) {
      final File parent = path.getParentFile();
      if (parent != null) {
        context.fileChooser.setCurrentDirectory(parent);
      }
    }

    /**
     * Gets item.
     *
     * @return the item
     */
    JMenuItem getItem() {
      return item;
    }

    /**
     * Sets item.
     *
     * @param jc the jc
     */
    void setItem( final JMenuItem jc ) {
      this.item = jc;
    }
  }
}
