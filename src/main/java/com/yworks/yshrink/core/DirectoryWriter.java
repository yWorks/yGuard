package com.yworks.yshrink.core;

import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.StreamProvider;
import com.yworks.util.abstractjar.impl.DirectoryStreamProvider;
import com.yworks.util.abstractjar.impl.DirectoryWrapper;
import com.yworks.util.abstractjar.impl.JarFileWrapper;
import com.yworks.util.abstractjar.impl.JarStreamProvider;
import com.yworks.common.ResourcePolicy;
import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.logging.Logger;
import com.yworks.yshrink.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Directory writer.
 */
public class DirectoryWriter implements ArchiveWriter {
  private static final String MANIFEST_FILENAME = "META-INF/MANIFEST.MF";
  private static final String SIGNATURE_FILE_PREFIX = "META-INF/";
  private static final String SIGNATURE_FILE_SUFFIX = ".SF";

  private File out;
  private Set<String> directoriesWritten = new HashSet<String>();
  private boolean createStubs;

  /**
   * Instantiates a new Directory writer.
   *
   * @param createStubs the create stubs
   */
  public DirectoryWriter( boolean createStubs ) {
    this.createStubs = createStubs;
  }

  private void addDirectory( final String fileName ) throws IOException {
    int index = 0;
    while ((index = fileName.indexOf("/", index + 1)) >= 0) {
      String directory = fileName.substring(0, index + 1);
      if (!directoriesWritten.contains(directory)) {
        directoriesWritten.add(directory);
        Files.createDirectory(out.toPath().resolve(directory));
      }
    }
  }

  private void copyResource( String entryName, StreamProvider jarStreamProvider, DataInputStream stream ) throws IOException {

    // don't copy manifest/signature files.
    if ( ! entryName.equals( MANIFEST_FILENAME )
         && ! ( entryName.endsWith( SIGNATURE_FILE_SUFFIX ) && entryName.startsWith( SIGNATURE_FILE_PREFIX ) ) ) {

      int entrySize = (int) jarStreamProvider.getCurrentEntry().getSize();
      if ( -1 != entrySize ) {
        byte[] data = new byte[ entrySize ];
        stream.readFully( data );
        addDirectory( entryName );
        Files.write( out.toPath().resolve(entryName), data );
      }
    }
  }

  @Override
  public void write( final Model model, final ShrinkBag bag ) throws IOException {
    File in = bag.getIn();
    out = bag.getOut();

    Logger.log("writing shrinked " + in + " to " + out + "." );

    Logger.shrinkLog( "<inOutPair in=\"" + in + "\" out=\"" + out + "\">" );

    long inLength = in.length();

    Archive inJar = (in.isDirectory()) ? new DirectoryWrapper(in) : new JarFileWrapper(in);

    StreamProvider jarStreamProvider = (in.isDirectory()) ? new DirectoryStreamProvider(in) : new JarStreamProvider(in);
    DataInputStream stream = jarStreamProvider.getNextClassEntryStream();

    int numClasses = 0;
    int numObsoleteClasses = 0;
    int numObsoleteMethods = 0;
    int numObsoleteFields = 0;
    int numRemovedResources = 0;

    Set<String> nonEmptyDirs = new HashSet<String>(5 );

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

        OutputVisitor outputVisitor = new OutputVisitor( cw, model, createStubs );
        ClassReader cr = new ClassReader(stream );

        // asm 3.x
        cr.accept( outputVisitor,0);


        numObsoleteMethods += outputVisitor.getNumObsoleteMethods();
        numObsoleteFields += outputVisitor.getNumObsoleteFields();

        byte[] modifiedClass = cw.toByteArray();
        addDirectory( entryName );
        Files.write( out.toPath().resolve(entryName), modifiedClass );
      } else {
        numObsoleteClasses++;
        Logger.shrinkLog("\t\t<class name=\"" + Util.toJavaClass(entryName ) + "\" />" );
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
}
