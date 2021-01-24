package com.yworks.yshrink.core;

import com.yworks.util.abstractjar.impl.DirectoryWriterImpl;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Placeholder for a directory writer with support for digests.
 * 
 * @author Thomas Behr
 */
class DirectoryWriter extends DirectoryWriterImpl {
  DirectoryWriter(
    final File outFile, final Manifest manifest
  ) throws IOException {
    super(outFile, manifest);
  }
}
