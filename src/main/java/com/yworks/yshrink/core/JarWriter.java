package com.yworks.yshrink.core;

import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.StreamProvider;
import com.yworks.util.abstractjar.impl.DirectoryStreamProvider;
import com.yworks.util.abstractjar.impl.DirectoryWrapper;
import com.yworks.util.abstractjar.impl.JarFileWrapper;
import com.yworks.util.abstractjar.impl.JarStreamProvider;
import com.yworks.yguard.common.ResourcePolicy;
import com.yworks.yguard.common.ShrinkBag;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.Logger;
import com.yworks.yshrink.util.Util;
import com.yworks.yshrink.util.Version;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class JarWriter implements ArchiveWriter {

  private Set<String> directoriesWritten = new HashSet<String>();
  private FileOutputStream fos;
  private JarOutputStream jos;
  private Manifest manifest;

  public static final String MANIFEST_FILENAME = "META-INF/MANIFEST.MF";
  private static final String SIGNATURE_FILE_PREFIX = "META-INF/";
  private static final String SIGNATURE_FILE_SUFFIX = ".SF";

  private final boolean createStubs;
  private final MessageDigest[] digests;

  public JarWriter( boolean createStubs, String digestNamesStr ) {
    this.createStubs = createStubs;

    String[] digestNames = ( digestNamesStr.trim().equalsIgnoreCase(
        "none" ) ) ? new String[ 0 ] : digestNamesStr.split( "," );

    for ( int i = 0; i < digestNames.length; i++ ) {
      digestNames[ i ] = digestNames[ i ].trim();
    }

    digests = new MessageDigest[ digestNames.length ];

    for ( int i = digestNames.length - 1; i >= 0; i-- ) {
      try {
        digests[ i ] = MessageDigest.getInstance( digestNames[ i ] );
      } catch ( NoSuchAlgorithmException e ) {
        Logger.err( "Unknwon digest algorithm: " + digestNames[ i ] );
        digests[ i ] = null;
      }
    }
  }

  public MessageDigest[] getDigests() {
    return digests;
  }

  private void addDigests( String entryName ) {
    Attributes oldEntryAttributes = manifest.getAttributes(entryName);
    Attributes newEntryAttributes = new Attributes(digests.length + 1);

    if (null != oldEntryAttributes) {
      Set<Object> keys = oldEntryAttributes.keySet();
      for (Object key : keys) {
        if (((Attributes.Name) key).toString().indexOf("Digest") == -1) {
          newEntryAttributes.put(key, oldEntryAttributes.get(key));
        }
      }
    }

    StringBuffer digestsList = new StringBuffer();
    for (int i = 0; i < digests.length; i++) {
      MessageDigest digest = digests[i];

      if (null != digest) {

        String digestKey = digest.getAlgorithm() + "-Digest";
        digestsList.append(digest.getAlgorithm());
        if (i < digests.length - 1) {
          digestsList.append(", ");
        }

        String digestVal = Util.toBase64(digest.digest());

        newEntryAttributes.putValue(digestKey, digestVal);
      }
    }

    newEntryAttributes.putValue("Digest-Algorithms", digestsList.toString());

    this.manifest.getEntries().put(entryName, newEntryAttributes);
  }

  private void calcDigests( final byte[] data ) {
    for (int i = digests.length - 1; i >= 0; i--) {
      if (null != digests[i]) {
        digests[i].reset();
        digests[i].update(data);
      }
    }
  }

  private void addEntry( final String fileName, final byte[] data ) throws IOException {

    JarEntry outEntry = new JarEntry(fileName);
    addDirectory(fileName);
    jos.putNextEntry(outEntry);
    jos.write(data);
    jos.closeEntry();

    calcDigests(data);

    addDigests(fileName);
  }

  private void addDirectory( final String fileName ) throws IOException {
    int index = 0;
    while ((index = fileName.indexOf("/", index + 1)) >= 0) {
      String directory = fileName.substring(0, index + 1);
      if (!directoriesWritten.contains(directory)) {
        directoriesWritten.add(directory);
        JarEntry directoryEntry = new JarEntry(directory);
        jos.putNextEntry(directoryEntry);
        jos.closeEntry();
      }
    }
  }

  private void finishManifest() throws IOException {
    manifest.getMainAttributes().putValue("Created-by",
                                          "yGuard Bytecode Obfuscator: Shrinker " + Version.getVersion());

    addDirectory(JarWriter.MANIFEST_FILENAME);
    jos.putNextEntry(new JarEntry(JarWriter.MANIFEST_FILENAME));
    this.manifest.write(jos);
    jos.closeEntry();
  }

  private void close() throws IOException {
    finishManifest();
    if (jos != null) {
      jos.finish();
      jos.close();
    }
    if (fos != null) {
      fos.close();
    }
  }

  public void write( Model model, ShrinkBag bag ) throws IOException {

    File in = bag.getIn();
    File out = bag.getOut();

    Logger.log( "writing shrinked " + in + " to " + out + "." );

    Logger.shrinkLog( "<inOutPair in=\"" + in + "\" out=\"" + out + "\">" );

    long inLength = in.length();

    Archive inJar = (in.isDirectory()) ? new DirectoryWrapper(in) : new JarFileWrapper(in);

    StreamProvider jarStreamProvider = (in.isDirectory()) ? new DirectoryStreamProvider(in) : new JarStreamProvider(in);
    DataInputStream stream = jarStreamProvider.getNextClassEntryStream();

    if ( !out.exists() ) out.createNewFile();

    manifest = ( inJar.getManifest() != null) ? new Manifest( inJar.getManifest() ) : new Manifest();
    fos = new FileOutputStream(out);
    jos = new JarOutputStream(new BufferedOutputStream(fos));

    int numClasses = 0;
    int numObsoleteClasses = 0;
    int numObsoleteMethods = 0;
    int numObsoleteFields = 0;
    int numRemovedResources = 0;

    Set<String> nonEmptyDirs = new HashSet<String>( 5 );

    Logger.shrinkLog( "\t<removed-code>" );

    while ( stream != null ) {

      String entryName = jarStreamProvider.getCurrentEntryName();

      numClasses++;

      ClassDescriptor cd = model.getClassDescriptor(
          entryName.substring( 0, entryName.lastIndexOf( ".class" ) ) );
      boolean obsolete = model.isObsolete( cd.getNode() );

      if ( !obsolete ) {

        nonEmptyDirs.add( jarStreamProvider.getCurrentDir() );

        // asm 3.x
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        // asm 2.x
        // ClassWriter cw = new ClassWriter(true);

        OutputVisitor outputVisitor = new OutputVisitor( cw, model, createStubs );
        ClassReader cr = new ClassReader( stream );

        // asm 3.x
        cr.accept( outputVisitor,0);

        // asm 2.x
        // cr.accept( outputVisitor,false);

        numObsoleteMethods += outputVisitor.getNumObsoleteMethods();
        numObsoleteFields += outputVisitor.getNumObsoleteFields();

        byte[] modifiedClass = cw.toByteArray();
        addEntry( entryName, modifiedClass );
      } else {
        manifest.getEntries().remove( entryName );
        numObsoleteClasses++;
        Logger.shrinkLog( "\t\t<class name=\"" + Util.toJavaClass( entryName ) + "\" />" );
      }

      stream = jarStreamProvider.getNextClassEntryStream();
    }

    Logger.shrinkLog( "\t</removed-code>" );
    Logger.shrinkLog( "\t<removed-resources>" );

    ResourcePolicy resourcePolicy = bag.getResources();

    if ( ! resourcePolicy.equals( ResourcePolicy.NONE ) ) {

      jarStreamProvider.reset();
      stream = jarStreamProvider.getNextResourceEntryStream();

      while ( stream != null ) {
        String entryName = jarStreamProvider.getCurrentEntryName();

        if ( ! resourcePolicy.equals( ResourcePolicy.NONE )
            &&
            (
                resourcePolicy.equals( ResourcePolicy.COPY )
                    ||
                    ( resourcePolicy.equals( ResourcePolicy.AUTO ) &&
                        nonEmptyDirs.contains( jarStreamProvider.getCurrentDir() ) ) ) ) {

          copyResource( entryName, jarStreamProvider, stream );
        } else {
          numRemovedResources++;
          Logger.shrinkLog(
              "\t<resource dir=\"" + jarStreamProvider.getCurrentDir() + "\" name=\"" + jarStreamProvider.getCurrentFilename() + "\" />" );
        }

        stream = jarStreamProvider.getNextResourceEntryStream();
      }
    }

    Logger.shrinkLog( "\t</removed-resources>" );

    close();

    long outLength = out.length();

    NumberFormat nf = NumberFormat.getPercentInstance();
    nf.setMinimumFractionDigits( 2 );
    String percent = nf.format( 1 - ( (double) outLength / (double) inLength ) );

    Logger.log( "\tshrinked " + in + " BY " + percent + "." );
    Logger.log( "\tsize before: " + inLength / 1024 + " KB, size after: " + outLength / 1024 + " KB." );
    Logger.log(
        "\tremoved " + numObsoleteClasses + " classes, " + numObsoleteMethods + " methods, " + numObsoleteFields + " fields, " + numRemovedResources + " resources." );
    Logger.log( "\t" + ( numClasses - numObsoleteClasses ) + " classes remaining of " + numClasses + " total." );

    Logger.shrinkLog( "</inOutPair>" );
  }

  private void copyResource( String entryName, StreamProvider jarStreamProvider, DataInputStream stream ) throws IOException {

    // don't copy manifest/signature files.
    if ( ! entryName.equals( MANIFEST_FILENAME )
        && ! ( entryName.endsWith( SIGNATURE_FILE_SUFFIX ) && entryName.startsWith( SIGNATURE_FILE_PREFIX ) ) ) {

      int entrySize = (int) jarStreamProvider.getCurrentEntry().getSize();
      if ( -1 != entrySize ) {
        byte[] data = new byte[ entrySize ];
        stream.readFully( data );
        addEntry( entryName, data );
      }
    }
  }

}
