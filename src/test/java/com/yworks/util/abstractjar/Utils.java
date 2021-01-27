package com.yworks.util.abstractjar;

import java.io.File;
import java.io.IOException;

/**
 * todo TBE: add documentation
 *
 * @author Thomas Behr
 */
class Utils {
  private Utils() {
  }


  static File createTempDir( final String prefix ) throws IOException {
    final File file = File.createTempFile(prefix, "_tempdir");

    if (!file.delete()) {
      throw new IOException(
        "Could not delete temporary file " + file.getAbsolutePath() + '.');
    }

    if (!file.mkdir()) {
      throw new IOException(
        "Could not create temporary directory " + file.getAbsolutePath() + '.');
    }

    return file;
  }

  static void delete( final File file ) throws IOException {
    if (file.isDirectory()) {
      final File[] contents = file.listFiles();
      if (contents != null) {
        for (int i = 0; i < contents.length; i++) {
          delete(contents[i]);
        }
      }
      if (!file.delete()) {
        throw new IOException(
          "Could not delete directory " + file.getAbsolutePath() + '.');
      }
    } else {
      if (!file.delete()) {
        throw new IOException(
          "Could not delete file " + file.getAbsolutePath() + '.');
      }
    }
  }
}
