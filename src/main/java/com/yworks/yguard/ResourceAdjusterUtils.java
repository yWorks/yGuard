package com.yworks.yguard;

import com.yworks.yguard.obf.GuardDB;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Provides functions for mapping class and package identifiers to the
 * corresponding renamed identifiers.
 * @author Thomas Behr
 */
class ResourceAdjusterUtils {
  private ResourceAdjusterUtils() {
  }


  /**
   * Returns a regular expression for finding class and package identifiers
   * whose fragments are separated by one of the given separator charactors.
   * @param separator the separator characters for tokenizing identifiers.
   * Has to be one of <code>"."</code>, <code>"/"</code>, or <code>"./"</code>.
   * @return a regular expression for finding class and package identifiers
   * whose fragments are separated by one of the given separator charactors.
   */
  static String newContentPattern( final String separator ) {
    if (".".equals(separator)) {
      return "(?:\\w|[$])+(\\.(?:\\w|[$])+)+";
    } else {
      return "(?:\\w|[$])+((?:\\.|\\/)(?:\\w|[$])+)+";
    }
  }


  public static Function<String, String> newTranslateJavaFileOrPath(
    final GuardDB db
  ) {
    return new TranslateJavaFileOrPath(db);
  }

  /**
   * Creates a function that maps the file name of a non-class file that matches
   * the qualified name of a class to the corresponding file name for the
   * renamed class.
   * @param db the database that stores the renamed identifiers.
   * @param strict if <code>true</code>, translate the whole file name only;
   * otherwise translate as many of the given file name fragments as possible.
   * @return a function that maps a file name of a non-class file that matches
   * the qualified name of a class to the corresponding file name for the
   * renamed class.
   */
  static Function<String, String> newTranslateJavaFile(
    final GuardDB db, final boolean strict
  ) {
    return new TranslateJavaFile(db, strict);
  }

  /**
   * Creates a function that maps the file name of a service file to the
   * corresponding renamed class name.
   * @param db the database that stores the renamed identifiers.
   * @param strict if <code>true</code>, translate the whole file name only;
   * otherwise translate as many of the given file name fragments as possible.
   * @return a function that maps the file name of a service file to the
   * corresponding renamed class name.
   */
  static Function<String, String> newTranslateServiceFile(
    final GuardDB db, final boolean strict
  ) {
    return new TranslateServiceFile(db, strict);
  }

  /**
   * Creates a function that maps qualified class names to the corresponding
   * renamed class names.
   * @param db the database that stores the renamed identifiers.
   * @return a function that maps qualified class names to the corresponding
   * renamed class names.
   */
  static Function<String, String> newTranslateJavaClass( final GuardDB db ) {
    return new TranslateJavaClass(db);
  }

  /**
   * Creates a function that maps class and package identifiers to the
   * corresponding renamed identifiers.
   * @param db the database that stores the renamed identifiers.
   * @param separator the separator characters for tokenizing identifiers.
   * Has to be one of <code>"."</code>, <code>"/"</code>, or <code>"./"</code>.
   * @param strict if <code>true</code>, translate the whole identifier only;
   * otherwise translate as much of the given identifier as possible.
   * @return a function that maps class and package identifiers to the
   * corresponding renamed identifiers.
   */
  static Function<String, String> newTranslateMapping(
    final GuardDB db, final String separator, final boolean strict
  ) {
    if (".".equals(separator)) {
      return new TranslateJavaItem(db, strict);
    } else if ("/".equals(separator)) {
      return new TranslatePathItem(db, strict);
    } else { // "./".equals(sep)
      return new TranslateItem(db, strict);
    }
  }


  private static final class TranslateItem implements Function<String, String> {
    private final Pattern dot;
    private final TranslateJavaItem java;
    private final TranslatePathItem path;

    TranslateItem( final GuardDB db, final boolean strict ) {
      dot = Pattern.compile(newContentPattern("."));
      java = new TranslateJavaItem(db, strict);
      path = new TranslatePathItem(db, strict);
    }

    @Override
    public String apply( final String identifier ) {
      if (dot.matcher(identifier).matches()) {
        return java.apply(identifier);
      } else {
        return path.apply(identifier);
      }
    }
  }

  private static final class TranslateJavaItem implements Function<String, String> {
    private final GuardDB db;
    private final boolean strict;

    TranslateJavaItem( final GuardDB db, final boolean strict ) {
      this.db = db;
      this.strict = strict;
    }

    @Override
    public String apply( final String identifier ) {
      return db.translateItem(identifier, '.', strict);
    }
  }

  private static final class TranslatePathItem implements Function<String, String> {
    private final GuardDB db;
    private final boolean strict;

    TranslatePathItem( final GuardDB db, final boolean strict ) {
      this.db = db;
      this.strict = strict;
    }

    @Override
    public String apply( final String identifier ) {
      final char sep = '/';
      final int idxSlash = identifier.lastIndexOf(sep);
      final int idxExt = indexOfExtension(identifier, idxSlash + 1);
      if (idxExt > -1) {
        // this is a properties filename, translate up to the filename extension
        final String path = identifier.substring(0, idxExt);
        final String ext = identifier.substring(idxExt);
        return db.translateItem(path, sep, strict) + ext;
      } else {
        return db.translateItem(identifier, sep, strict);
      }
    }

    private static int indexOfExtension(
      final String path, final int fromIndex
    ) {
      if (path.endsWith(".properties")) {
        final int idx = path.indexOf('_', fromIndex);
        return idx > -1 ? idx : path.length() - 11;
      }
      return -1;
    }
  }

  private static final class TranslateJavaClass implements Function<String, String> {
    private final GuardDB db;

    TranslateJavaClass( final GuardDB db ) {
      this.db = db;
    }

    @Override
    public String apply( final String identifier ) {
      return db.translateJavaClass(identifier);
    }
  }

  private static final class TranslateJavaFile implements Function<String, String> {
    private final GuardDB db;
    private final boolean strict;

    TranslateJavaFile( final GuardDB db, final boolean strict ) {
      this.db = db;
      this.strict = strict;
    }

    @Override
    public String apply( final String identifier ) {
      return strict
        ? db.translateJavaFile(identifier)
        : db.translateItem(identifier, '/', false);
    }
  }

  private static final class TranslateServiceFile implements Function<String, String> {
    private final GuardDB db;
    private final boolean strict;

    TranslateServiceFile( final GuardDB db, final boolean strict ) {
      this.db = db;
      this.strict = strict;
    }

    @Override
    public String apply( final String identifier ) {
      return strict
        ? db.translateJavaFile(identifier).replace('/', '.')
        : db.translateItem(identifier, '.', false);
    }
  }

  private static class TranslateJavaFileOrPath implements Function<String, String> {
    private final GuardDB db;

    TranslateJavaFileOrPath( final GuardDB db ) {
      this.db = db;
    }

    @Override
    public String apply( final String identifier ) {
      final String mapped = db.translateJavaFile(identifier);
      return identifier.equals(mapped) ? db.getOutName(identifier) : mapped;
    }
  }
}
