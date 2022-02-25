# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Added support for Java 16 records.
- Added support for Java 16 class files.
- Support for obfuscating multi-release jars.
  (Shrinking multi-release jars is not supported.)

### Improved
- Improved support for type annotations.

### Fixed
- Fixed digests for resource files.
- Ensured all opened streams are properly closed.

## [3.0.0]

### Added
- Added testimonials
- Added class constant for Kotlin (Kotlin support)
- Added directory support for reading archives from directories
- Added subclassing capabilities to further extend yGuard

### Removed
- Remove ability to read archives from URL

### Fixed
- Parse `exposed` section of log files as well
- Added compatability fixes to increment to Java 14
- Fixed major bug with interface inheritance
- Fixed Windows being unable to run yGuard because of file system issues

### Changed
- Replaced internal dependency graph from a Guava-dependant implementation to a standalone implementation
- Removed artificial split of retroguard and yGuard and unify the project under a single MIT license

## [2.10.0] 2020-05-22

### Added
- Added support for `invokedynamic` instructions to YShrink
- Added support for `default` methods in interfaces to YShrink
- Added more documentation to YShrink for future maintenance

### Changed
- Use mkdocs instead of Jekyll. Bundle docs in the upcoming releases.
- Support `ASM7` and Java 13 in both YShrink and YGuard

### Fixed

- Use a empty manifest in `JarWriter` where the shrinker would previously crash when no manifest was present
- Use Gradle wrapper with Gradle version 5.6.4 for reproducible builds

## [2.9.2] - 2019-12-09
## Added
- Added scramble to the <rename> element, introducing randomized mapping
- Add documentation about <property> default values
- Added @Inherited annotation to `com.yworks.util.annotation.Obfuscation`

## Fixed
- Do not use `setComment` when comment is actually `null`. Achieves Android compatibility.

## [2.9.1] - 2019-11-18
## Changed
- Use a Jekyll-based documentation instead of our own HTML

## Added
- Added example projects for Gradle, Maven and Ant
- Added installation instructions for Gradle, Maven and Ant
- Published packages on Maven Central

## [2.9.0] - 2019-10-17
### Changed
- Replaced legacy build system with Gradle. 
- Added `retroguard` dependency (LGPL-licensed)

## [2.8.0] - 2019-10-02
### Changed
- Replaced `y.base.Edge`, `y.base.Node` and `y.base.Graph` with a custom wrapper around Guava
- Removed parts licensed under `LGPL` into a dedicated library, which is linked statically.
- Relicensed the project under `MIT`

## [2.7.2] - 2019-09-26
### Fixed
- Fixed `UnsupportedOperationException` that occurred for Java 11 class files when excluding classes from obfuscation using the `extends` or `implements` attributes of the  `rename.keep.class` element in the yGuard ANT task.
- Fixed yGuard's `language-conformity` mode `illegal` to no longer produce unqualified names that contain dots or spaces.

## [2.7.1] - 2019-02-26
### Added
- Added support for the `MethodParameters` attribute, which was introduced with the Java 8 `.class` file format.

### Fixed
- Fixed `ClassCastException` that occurred when obfuscating Java 11 class files with `String` concatentation.
- Fixed `IllegalArgumentException` that occurred when excluding classes from obfuscation using the `extends` or `implements` attributes of the `rename.keep.class` element in the yGuard ANT task.

## [2.7] - 2019-02-05
### Added
- Added basic Java 11 class file support.
- Added Java 9 and Java 10 class file support.
- Added support for stacktraces with module prefixes.
- Added support for stacktraces with obfuscation prefixes.

### Changed
- Improved translation of overloaded methods.

## [2.6] - 2017-06-12
### Added
- Added support for [Java 8 Type Annotations](https://blogs.oracle.com/java-platform-group/java-8s-new-type-annotations).
- The `implements` attribute now considers types in `<externalclasses>` as well when determining the interfaces implemented by a given class.

### Fixed
- Fixed incorrect obfuscation of `invokedynamic` instructions leading to AbstractMethodErrors of class files which had their constant pool entry order permutated for some reason (for example by the JarJar plugin for maven).

### Changed
- Changed the class file version of the yGuard library, so yGuard can be run with Java 7 again.

## [2.5.5] - 2017-01-27
### Fixed
- Fixed `IllegalArgumentException: "Invalid fully qualified name (b)"` that occurred when referencing or attempting to obfuscate classes that start with a dollar ('$') sign.

## [2.5.4] - 2016-02-19
### Fixed
- Fixed `IllegalArgumentException` caused by Java 8 compatibility problems that arose in certain situations, for example when using hierarchy based keep instructions.

## [2.5.3] - 2015-04-01
### Fixed
- Fixed incorrect obfuscation of `invokedynamic` instructions leading to `AbstractMethodError` when using default methods.

## [2.5.2] - 2014-03-17
### Added
- Added support for renaming `META-INF/services` entries if the entry corresponds to a type that is obfuscated.

### Fixed
- Fixed a bug that caused the stacktrace deobfuscation tool to ignore class name mappings that started with one or more $ characters.

## [2.5.1] - 2013-10-30	
### Fixed
- Fixed broken binary class file of the attached `com.yworks.util.annotation.Obfuscation` annotation in `ObfuscationAnnotation.jar`.

## [2.5] - 2013-10-23
### Added
- Added support for controlling obfuscation exclusion via annotations.
- Improved Java 7 support. yGuard does now support `invokedynamic` in the obfuscation process.

## [2.4.0.1] - 2012-09-28
### Fixed
- Fixed a regexp that could cause yGuard to parse manifest files for a very long time.

## [2.4] - 2011-09-20
### Added
- Added basic Java 7 class file support

### Fixed
- Fixed a bug that caused yGuard to introduce invalid signatures for typed classes with inner classes.
- Fixed a bug that caused yGuard to erroneously exclude all classes from obfuscation when an `implements` attribute was used for a `<class>` element without an additional `name` or `expose` attribute.
- Fixed a bug that caused the shrinker to ignore the `<attribute>` element if the default shrinking settings were used (i.e. if no `<keep>` element was defined).

## [2.3.0.1] - 2010-06-22
### Fixed
- Fixed a bug that caused the shrinker to remove the static initializer of a non-static inner class, when only fields of the inner class were referenced (e.g. compiler-generated switch map tables for enums).

## [2.3.0] - 2008-10-06
### Added
- yGuard won't try to initialize external classes needed for resolving anymore.
- The default behavior of the shrinker was changed to keep any referenced runtime visible annotation and parameter annotation attributes.

### Fixed
- Fixed a bug that caused the shrinker to remove the enclosing method of anonymous inner classes declared as entry points.
- The shrinker will now correctly adhere to the "lineNumberTable", "runtimeVisibleParameterAnnotations" etc. attributes of the `<keep>` element.
- Fixed a bug that caused the shrinker to remove referenced Annotation elements from Annotation interfaces.
- yGuard now won't rename or remove the package-info class.
- Fixed possible "Illegal group reference" Exception in yGuard's parse tool.
- The shrinker will now keep classes that are used as field types of referenced fields.
- The shrinker will now correctly keep method attributes of methods that are kept as stubs.

## [2.2.0] - 2007-04-26	
### Added
- yGuard is now fully JDK-1.6 compatible.
- The yGuard task now supports a way of specifying a set of jars that should be obfuscated at the same time using simple patternset syntax.
- yGuard now uses the same technique for all elements in the MANIFEST file to adjust fully qualified class name strings.
- It is now possible to tell yGuard not to obfuscate specific package names.
- yGuard can now be given a list of digest algorithms that will be used to create the digests in the manifest.
- yGuard now issues a warning if the package or class or method name of a native method is obfuscated.

## [2.1.0] - 2006-10-12
### Added
- Added `overloadEnabled` property

### Fixed
- Fixed a bug that caused yGuard to throw a `NullPointerException` if a `attribute` element contained no nested `patternset`.
- Fixed a bug that caused yGuard to use multiple logging instances if a yGuard task was executed multiple times.
- Fixed LineNumberTable and SourceFile elements

## [2.0.3] - 2006-08-09
- Fixed a bug that broke the nested `map` element of the `rename` element.

## [2.0.2] - 2006-07-31
### Fixed
- Fixed a bug that caused yGuard to fail during class file parsing if a method signature contained an inner class of a parameterized class.
- yGuard will not complain about the Java 1.5 attributes `Bridge`, `Enum` and `Varargs` as "unknown attributes" anymore.
- Fixed a bug that caused all classnames to be kept from renaming if the `extends` attribute was used in combination with the `classes` attribute.

## [2.0.1] - 2006-07-12	

### Fixed
- Fixed an inconsistency between the interpretation of the Ant syntax in the `rename` and `shrink` elements. Now, both elements apply `patternset` elements in nested `method` and `field` elements to class names, just as stated in the yGuard documentation.

### Changed
- If the `classes` attribute of the `class` element is not set or set to "none", the shrinking engine will now include the classes that match based on the given `name` attribute or nested `patternset`, effectively ignoring the classes attribute.

## [2.0] - 2006-06-22
### Added
- New elaborate code shrinking functionality.
- More powerful ant syntax: `extends/implements` attributes for the `class` element.
- yGuard now needs Java2 SDK 1.4.x or greater to function properly.

### Changed
- General Ant syntax changes due to the introduction of the new `yguard`, `shrink` and `rename` elements.

## [1.5.0_03]
### Fixed
- Fixed bad treatment of the new enclosing method feature in Tiger which sometimes led to AbstractMethodErrors at runtime. 

## [1.5.0_02] - 2005-05-31
### Fixed
- Fixed bad annotation handling for non-trivial annotations. Annotations had not been parsed and handled correctly, which could lead to either errors during obfuscation or Errors at runtime.

### Changed
- Improved obfuscation logic for enumerations. Now the two static methods valueOf(String) and values() don't have to be exposed manually anymore.

## [1.5.0_01] - 2005-05-24
### Fixed
- Fixed a bug that made yGuard ignore some of the attributes in the expose section ("sourcefile" and "linenumbertable").
- Fixed a rare but severe bug that accidentally removed method attributes and sometimes led to `ArrayIndexOutOfBoundsExceptions` during the obfuscation.

## [1.5] - 2005-05-19
### Added
- Added JDK 1.5 (a.k.a Java 5.0 code-named Tiger) compatibility. yGuard can now deal correctly with the new JDK and Java features: generics, var-args, enumerations, annotations, new `.class` bytecode construct, signatures, local variable type table, enclosing method information.
- Implemented the ability to obfuscate/scramble/shrink line number information. This makes it possible to more easily debug stacktraces without exposing line number information in the obfuscated version of the application.
- Implemented the ability to obfuscate the source file information. This is necessary in order to view line number information in stack traces. It is now possible to reassign the source file attribute inside class files so that the original source file name is not exposed in the obfuscated application.
- Added the ability to determine on a per-class basis what attributes to expose or obfuscate. This includes line number information (with optional scrambling/compression or removal), source file attributes, deprecation attributes, etc. This makes it easy to obfuscate parts of your application while keeping the public API untouched and debug information for third party jars intact.
- Improved the stacktrace deobfuscation tool. It can now unscramble line number information from stacktraces, features a more polished view of the mapping rules and deobfuscates stacktraces more reliably if the stacktraces are ambiguous.

## [1.3.2] - 2004-09-08
### Added
- yGuard now helps in the detection of duplicate class files in source jars. In pedantic mode yGuard will terminate if duplicate classes are detected in different source jars. yGuard's obfuscate task will always fail if mutliple class files containing a definition for the same jar are detected. The log file displays useful information for finding the duplicate entries.
- Improved xml log file output for Unicode characters.

### Fixed
- Fixed a name clash problem that occurred when already obfuscated code was being obfuscated again using different settings.
- Fixed a resolution problem concerning interfaces from external classpaths.

### Changed
- Improved name generation. yGuard will now generate legal identifiers (with respect to `Character.isJavaIdentifierStart()` and `Character.isJavaIdentifierPart(char)` and `Character.isIdentifierIgnorable(char))` and should therefor produce jars that should verify correctly even on older jdks if yGuard is run using newer jdks.
- Made the log file viewer output more consistent with inner class names that were being mapped during obfuscation versus those who remained fixed.
- Jar file entries are now being sorted prior to being written to the jar.
- Improved handling of external classes.

## [1.3.1_01] - 2004-04-27
### Fixed
- Fixed a problem concerning the adjust elements throwing a RuntimeException in the pattern matching code.
- Fixed a problem where the wrong set of files was affected in the adjust section.

### Changed
- yGuard now treats the _COMPATIBLE_ flag for the language conformity different. Field names and class names are now made up of lower ascii-only chars.

## [1.3.1] - 2004-03-02
### Added
- Improved the optional keeping of "Deprecated" tags. Somehow they still seem to get lost under certain conditions. Any feedback on this topic is welcomed.

### Fixed
- Due to a compile time dependency, yGuard could not be used with jdk 1.3.x anymore even if the automatic resource adjustment feature was not used. This has now been made possible again.
- Fixed a rare problem that broke obfuscated code using static method invocations and static field references.
- There was a bug in the log file viewer (`java -jar yguard.jar [logfile.xml[.gz]]`) that made it crash under very rare circumstances.
- Implemented a workaround for an Ant incompatibility problem, which resulted in yGuard behaving differently on different platforms and in conjunction with different Ant versions.

### Changed
- yGuard will now generate (empty) directory entries in the resulting jar files for each non-empty directory.

## [1.3] - 2004-01-05
### Added
- Added automatic text file and property file renaming mechanism. yGuard can now be configured to rename `.properties` files according to the obfuscation.
- It is now possible to process text files and replace occurances of class names with their obfuscated versions.
- One can now specify whether resource files should be kept in their original directory while at the same time the classes residing in the respective directory can be fully obfuscated to another package.
- yGuard can now automatically create gzipped (.gz) logfiles and work directly on compressed logfiles. This reduces the size of the logfiles drastically.
- It is now possible to simply specify a list of attributes, that should not be removed by yGuard (for example `Deprecated`) using the expose-attributes property.
- yGuard has a new name generation method (`language-conformity = compatible`), that creates jar file that can be successfully unzipped to the windows filesystem.
- In order to avoid namespace clashes, one can now easily specify a prefix for completeley obfuscated package hierarchies using the `obfuscation-prefix` property.
- Enhanced documentation (DTD, examples, and new features description).

### Fixed
- Innerclasses making use of the .class construct should now always be correctly obfuscated using the replaceClassNameStrings feature.
- The `patch` element had a bug concerning field name mappings, which is now resolved.
- yGuard now tests whether a newly obfuscated name already exists in external jars and automatically generates names, that should not clash.
- yGuard should now work together with Ant version 1.6 (there was an undocumented change in the API of Ant).

## [1.2]
### Added
- Added support for external libraries. This allows yGuard to obfuscate jars that have external dependencies more easily and using stronger obfuscation. It is now possible to specify dependencies using Ant classpath elements. yGuard then uses information found in these jars to resolve external dependencies.
- Improved error handling and task and logfile output. yGuard will now produce fewer unreasonable warnings. During the obfuscation run yGuard will give more detailed warnings and hints when unobfuscatable classes are detected.
- Added property error-checking which can be set to **pedantic**. In this case yGuard will not issue warnings but a build will fail instead of issueing simple warnings. This helps in finding problems.

### Fixed
- Fixed a minor issue. The documentation stated, that: `<class classes="protected"/>` behaved like `<class classes="protected">
  <patternset>
    <include name="**.*"/>
  </patternset>
</class>` but in the implementation `<include name="*"/>` had been applied. This has now been fixed to `<include name="**.*"/>`.
- Fixed some bugs in the documentation.

## [1.1] - 2002-12-17
### Added
- Added support for different naming schemes. These schemes result in smaller jar files, better obfuscation and lead to jar files, which cannot be unpacked to normal filesystems.
- Fixed two JBuilder incompatibilities. Innerclasses created by JBuilder do not prevent yGuard from working anymore and (correct) innerclasses created by yGuard do not crash JBuilder anymore (which btw. is a bug in JB).
- yGuard can now automatically obfuscate code of the form `Class.forName("com.mycompany.myapp.MyClass");` so that these classes can now be obfuscated by name, too.

### Fixed
- Implemented a fix for the problem, where the `ClassName.class` code construct prevented classes from being obfuscated entirely.
- Fixed two bugs concerning the handling of manifest files.
- Fixed a bug concerning the handling of the `Main-Class` attribute of manifest files.

### Changed
- Improved the serialization of the obfuscation map to the xml file, which can now be parsed back in by the included tool even for complicated naming schemes.
- Refactored the creation of the final jar files. The current implementation leads to more standard conform jar files.

## [1.0.0] - 2002-11-28
### Added
- The `Main-Class` attribute of the Manifest files will now be translated to the obfuscated name, if the main class is not exposed.

### Changed
- Implemented more robust handling of Manifest files. Implementation now makes use of `java.util.jar.Manifest`.
- The `conserveManifest` attribute of the obfuscate task now conserves the manifest in a better way.

[Unreleased]: https://github.com/yworks/yguard/compare/3.0.0...HEAD
[3.0.0]: https://github.com/yworks/yguard/compare/2.10.0...3.0.0
[2.10.0]: https://github.com/yworks/yguard/compare/2.9.2...2.10.0
[2.9.2]: https://github.com/yworks/yguard/compare/2.9.1...2.9.2
[2.9.1]: https://github.com/yworks/yguard/compare/2.9.0...2.9.1
[2.9.0]: https://github.com/yworks/yguard/compare/2.8.0...2.9.0
[2.8.0]: https://github.com/yWorks/yguard/tree/2.8.0
