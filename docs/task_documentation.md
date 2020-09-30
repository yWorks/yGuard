# Task documentation

## Preamble

Using the `yGuard` Ant task, name obfuscation and code shrinking can be seamlessly integrated into your deployment process.

The `yguard` task contains two nested elements that perform the name obfuscation and code shrinking separately:

- The [shrink](#the-shrink-element) element removes all code elements that are not reachable from the entrypoints given in the nested [keep](#the-keep-element) element.
- The [rename](#the-rename-element) element performs name-obfuscation, renaming all packages, classes, methods and fields according to a selectable name-mapping scheme. Elements can be excluded from the renaming process by annotating them with a certain annotation class in the source code or using a nested [keep](#the-keep-element) element.

## Table of contents

- [`yguard` element](#the-yguard-element)
    - [`inoutpair` element](#the-inoutpair-element)
    - [`externalclasses` element](#the-externalclasses-element)
    - [`attribute` element](#the-attribute-element)
    - [`rename` element](#the-rename-element)
        - [`property` element](#the-property-element)
        - [`patch` element](#generating-patch-jars)
        - [`adjust` element](#the-adjust-element)
        - [`map` element](#the-map-element)
            - [`package` element](#the-package-element)
            - [`class` element](#the-class-element)
            - [`method` element](#the-method-element)
            - [`field` element](#the-field-element)
    - [`shrink` element](#the-shrink-element)
        - [`entrypointjar` element](#the-entrypointjar-element)
    - [`keep` element](#the-keep-element)
- [Controlling obfuscation exclusion with annotations](#controlling-obfuscation-exclusion-with-annotations)
- [Generating patch JARs](#generating-patch-jars)
- [Deobfuscating stacktraces](#deobfuscating-stacktraces)
- [DTD used for Ant `<yguard>`](#dtd-used-for-ant-yguard)

## The yguard Element

The yguard task contains elements that define basic properties common to the nested `rename` and `shrink` tasks.
Please see the [troubleshooting section](troubleshooting) to learn about common pitfalls when using name obfuscation and shrinking software.

#### Attributes

The `yguard` element has no attributes.

#### Child Elements

- [inoutpair](#the-inoutpair-element)
- [externalclasses](#the-externalclasses-element)
- [attribute](#the-attribute-element)
- [rename](#the-rename-element)
- [shrink](#the-shrink-element)

### The `inoutpair` Element

At least one `inoutpair` element or one non-empty `inoutpairs` element has to be specified in order to run the yguard tasks. This element specifies the paths to the input and output jar files.

`inoutpair` also supports the usage of directories. This is detected by [File.isDirectory](https://docs.oracle.com/javase/7/docs/api/java/io/File.html#isDirectory()). However, in general you do not want to use directories for `inoutpairs` (advanced use cases).

#### Attributes

<table>
<thead>
    <tr>
        <th width="12%"><b>Attribute</b></th>
        <th width="78%"><b>Description</b></th>
        <th width="10%"><b>Required</b></th>
    </tr>
</thead>

<tr>
    <td><code>in</code></td>
    <td>
    Specifies an exisiting jar file, which contains the unshrinked and
    unobfuscated .class files.
    </td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>out</code></td>
    <td>
    Specifies a path to a jar file which will be created and used to
    put the results of the shrinking and obfuscation process.
    </td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>resources</code></td>
    <td>
    Will only be considered if the
    <a href="#the-yguard-element"><code>yguard</code></a> element
    contains a nested
    <a href="#"><code>shrink</code></a> element.
    <br>
    Determines how the shrinking engine handles all non-.class files.
    <br>
    Currently the following three resource policies are supported:
    <ul>
        <li><code>copy</code><br>
        the default, simply copies all resource files to the output jar.
        </li>
        <li><code>auto</code><br>
        copies only those resource files that reside in a directory that
        still contains one or more .class files after shrinking.
        </li>
        <li><code>none</code><br>
        discards all resource files.
        </li>
    </ul>
    </td>
    <td>
    No, defaults to <code>copy</code>.
    </td>
</tr>
</table>

#### Child Elements

The `inoutpair` element has no child elements.

If multiple jar files need to be obfuscated at once the `inoutpairs` element can be used alternatively.

## The `inoutpairs` Elements
Additionally or alternatively to `inoutpair` elements this element can be specified in order to specify the paths to the input and output jar files.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>resources</code></td>
    <td>
    Will only be considered if the
    <a href="#yguard"><code>yguard</code></a> element
    contains a nested
    <a href="#shrink"><code>shrink</code></a> element.
    <br>
    Determines how the shrinking engine handles all non-.class files.
    <br>
    Currently the following three resource policies are supported:
    <ul>
        <li><code>copy</code><br>
        the default, simply copies all resource files to the output jar.
        </li>
        <li><code>auto</code><br>
        copies only those resource files that reside in a directory that
        still contains one or more .class files after shrinking.
        </li>
        <li><code>none</code><br>
        discards all resource files.
        </li>
    </ul>
    </td>
    <td>
    No, defaults to <code>copy</code>.
    </td>
</tr>
</table>

#### Child Elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)
- optionally a [mapper](http://ant.apache.org/manual/Types/mapper.html) that determines the name mapping between the unobfuscated and obfuscated versions of the jar files. Note that `identitymapper` and `mergemapper` are not supported. All matched jar file names need to be mapped to exactly one jar file name that differs from the original jar file.

#### Examples

```xml
<!-- use all jars in the input-lib-dir directory and obfuscate them to *_obf.jar -->
<inoutpairs resources="auto">
  <fileset dir="${input-lib-dir}">
    <include name="myapp*.jar"/>
    <exclude name="*_obf.jar"/>
  </fileset>
  <mapper type="glob" from="*.jar" to="*_obf.jar"/>
</inoutpairs>

<!-- the above mapper is the default one so the following snippet does the same -->
<inoutpairs resources="auto">
  <fileset dir="${input-lib-dir}">
    <include name="myapp*.jar"/>
    <exclude name="*_obf.jar"/>
  </fileset>
</inoutpairs>
``` 

## The `externalclasses` Element
If the jar to be processed by `yGuard` depends on external classes or libraries, this element can be used to specify classpaths to these entities. These libraries will neither be shrinked nor obfuscated. Use the `inoutpair` element for this purpose! See the `external_library` example for an example of when to use this element.
In order to achieve a maximum shrinking effect by the `shrink` task, all external dependencies should be declared in the `externalclasses` element. Otherwise, all non-private methods of classes that inherit from unresolvable classes will not be shrinked.

The elements attributes and child elements can be seen on the [Ant documentation page about using path elements](http://ant.apache.org/manual/using.html#path).

## The attribute Element
Using the `attribute` element, you can specify which attributes present in the input classes should be kept in the obfuscated output classes.

See the `linked_example` for an example of when to use this element.

#### Attributes

 <table class="listing">
    <thead>
        <tr>
            <th width="12%"><b>Attribute</b></th>
            <th width="78%"><b>Description</b></th>
            <th width="10%"><b>Required</b></th>
        </tr>
    </thead>
<tr>
    <td><code>name</code></td>
    <td>A comma-separated list of attribute names that are to
    be retained in the shrinked and/or
    obfuscated class
    files.
    </td>
    <td>Yes</td>
</tr>
</table>

#### Child Elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Example

```xml
<attribute name="SourceFile, LineNumberTable, LocalVariableTable">
  <patternset>
    <include name="com.mycompany.mylibrary.**"/>
  </patternset>
</attribute>
```

This will retain the attributes named _"SourceFile"_, _"LineNumberTable"_, and _"LocalVariableTable"_ effectively enabling debugging information for all classes in the `com.mycompany.mylibrary` package and subpackages.

## The `shrink` Element
The `shrink` task removes all classes, fields and methods that are not reachable from a number of entrypoints given by a nested [keep]() element.
See the [examples]() explenation of some common use cases. If your code uses reflection, please read the [troubleshooting](troubleshooting) section for information on this topic.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>
<tr>
    <td><code>logfile</code></td>
    <td>Determines the name of the logfile that is generated
    during the shrinking process. The logfile contains information about
    the entrypoints the shrinking engine uses, the removed classes,
    methods and fields as well as any warnings.
    <br>
    If the name ends with a ".gz", yGuard will automatically create a
    gzipped version of the file which potentially saves a lot of disc
    space.
    </td>
    <td>
    No, defaults to<code>yshrinklog.xml</code>
    </td>
</tr>
<tr>
    <td>
    <a name="createstubs"></a><code>createStubs</code>
    </td>
    <td>
    Instead of removing methods completely, this attribute causes the
    <code>shrink</code> task to insert a method
    stub that throws a <code>java.lang.InternalError</code> if it is
    called. This attribute is very useful if the shrinking process
    causes your application to break and you are uncertain about which
    additional code entities you have to include in the
    <a href="#keep"><code>keep</code></a>element. <br/>
    Note that classes considered as completely obsolete by the shrinking
    engine are still removed completely - this attribute only
    affects obsolete methods of non-obsolete classes.
    </td>
    <td>
    No, defaults to <code>false</code>
    </td>
</tr>
</table>

#### Child Elements

- [keep](#the-keep-element)
- [entrypointjar](#the-entrypointjar-element)

## The `entrypointjar` Element

The `entrypointjar` element can be used for convenience if your application uses libraries that are to be shrinked, but the jarfile using these libraries should be left untouched by the shrinking engine. Such a jarfile could be specified as an `entrypointjar`.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>name</code></td>
    <td>Path to to the jar file to use as entrypointjar.</td>
    <td>Yes</td>
</tr>
</table>

#### Child Elements

The `entrypointjar` element has no child elements.

#### Example

```xml
<yguard>
  <inoutpair in="lib-in.jar" out="lib-out.jar" />
  <shrink>
    <entrypointjar name="myApp.jar"/>
  </shrink>
</yguard>
```

## The `rename` Element
The basic idea is, that all elements will be renamed by this task. There are different use cases, where you sometimes want to exclude or simply just have to exclude some elements from name obfuscation, i.e. **not** rename them but keep in the API as is. See the [examples]() for explanation of some common use cases. If your code uses reflection, please read the [troubleshooting](troubleshooting) section for information on this topic. Excluding elements can be achieved by using the [keep](#the-keep-element) element, the `mainclass` attribute of the `rename` element and by annotating elements in the source code with the annotation that is specified in the `annotationClass` attribute of the `rename` element. Using the nested `keep` element, you have to specify all classes, methods, fields, and attributes that should be excluded from name obfuscation. Another way is to [annotate the elements directly in the source code](#annotate) that should be obfuscated or excluded. You can use the yFiles obfuscation annotation `com.yworks.util.annotation.Obfuscation` for that or specify your own annotation in the `annotationClass` attribute of this element.

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>mainclass</code></td>
    <td>Can be used as a shortcut to specify the mainclass
    of your application. Both the class name and the main method will be
    excluded from name obfuscation.
    Alternatively you may want to consider to exclude the main method
    only. If your jar contains a <code>Main-Class</code> attribute, the
    <code>rename</code> task will automatically adjust
    the value to the obfuscated
    name.
    </td>
    <td>No</td>
</tr>
<tr>
    <td><code>logfile</code></td>
    <td>Determines the name of the logfile that is generated
    during the renaming process. The logfile contains information about
    the mappings the name obfuscator generates as well as any warnings.
    <br>
    If the name ends with a ".gz", yGuard will automatically create a
    gzipped version of the file which potentially saves a lot of disc
    space.
    </td>
    <td>
    No, defaults to<code>yguardlog.xml</code></td>
</tr>
<tr>
    <td><code>conservemanifest</code></td>
    <td>
    A boolean attribute (valid values:
    <code>true</code>/<code>false</code>) that determines whether the
    manifest file of the jars should be left untouched by the renaming
    engine. If set to <code>false</code>, the manifest will be modified
    to reflect the new message digests.
    </td>
    <td>
    No, defaults to <code>false</code>.
    </td>
</tr>
<tr>
    <td>
    <a name="replaceclassnamestrings"></a><code>replaceClassNameStrings</code>
    </td>
    <td>
    A boolean attribute (valid values:
    <code>true</code>/<code>false</code>) that determines whether the
    renaming engine should try to replace hardcoded Strings, which
    are used in conjunction with the <code>MyClass.class</code>
    construct.
    If set to <code>false</code>, those Strings will be left untouched
    and code of the form <code>MyClass.class</code> will break if
    MyClass gets obfuscated by name. If set to <code>true</code> (the
    default), yGuard will try to workaround this problem by replacing
    the hardcoded String with the appropriate obfuscated name. However
    this will only work if the unobfuscated class file has been
    generated with the usual compilers ('javac', 'jikes' and 'bjc') or
    compilers, that produce similar bytecode. This can also have the
    side-effect of modifying too many Strings, e.g if you have code that
    looks like<code>System.out.println("com.mycompany.MyClass");</code>,
    it might get replaced, if <code>MyClass.class</code> resides in the
    very same class with something like
    <code>System.out.println("com.A.OoO");</code>. It will most likely
    fail if the class has been previously obfuscated by another
    obfuscation tool or a different compiler has been used for
    compilation. Anyway it is always worth it to give it a try, if you
    want to have 'full obfuscation'.
    </td>
    <td>
    No, defaults to <code>true</code>
    </td>
</tr>
       <tr>
          <td>
            <a name="scramble"></a><code>scramble</code>
          </td>
          <td>
            A boolean attribute (valid values: <code>true</code>/<code>false</code>)
            that determines whether the renaming engine should generate pseudorandom
            name mappings for each invocation. If set to <code>false</code> (the
            default, for backward compatibility), each obfuscation will used a
            fixed map that generates names based on the order of the obfuscated
            elements. If nothing is changed, each obfuscation will generate the
            same obfuscated names for all elements. If set to <code>true</code>,
            yGuard generates pseudorandom mappings (using <code>java.util.Random</code>)
            that produce different obfuscated names in each build even if the
            unobfuscated source is unchanged.
          </td>
          <td>
            No, defaults to <code>false</code>
          </td>
</tr>
<tr>
    <td><a name="annotationClass"></a><code>annotationClass</code></td>
    <td>
    Specifies the name of the annotation class that can be used to
    exclude elements by annotating them in the source code. The
    specified annotation can be any annotation that fits the convention.
    </td>
    <td>
    No, defaults to <code>com.yworks.util.annotation.Obfuscation</code>
    </td>
</tr>
</table>

#### Child Elements

- [keep](#the-keep-element)
- [property](#the-property-element)
- [patch](#the-patch-property)
- [adjust](#the-adjust-property)
- [map](#the-map-property)

## The property Element

`property` elements can be used to give hints to the name obfuscation engine. Depending on the exact version of yGuard, the task may use these hints to control the process of obfuscation.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>
<tbody>

<tr>
    <td><code>name</code></td>
    <td>
    Specifies a key which may be interpreted by the obfuscation task.
    </td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>value</code></td>
    <td>
    Specifies the corresponding value of the property.
    </td>
    <td>Yes</td>
</tr>
</tbody>
</table>

#### Supported properties

 <table class="listing">
<thead>
<tr>
    <th width="10%"><b>Name</b></th>
    <th width="80%"><b>Description</b></th>
    <th width="10%"><b>Default</b></th>
</tr>
</thead>

<tbody>
<tr>
    <td><code class="property">error-checking</code></td>
    <td>
    Can be used to tell yGuard to bail out if it detects any problems.
    Currently this property can be set to the following value:
    <ul>
        <li>
        <code class="prop-value">pedantic</code><br>
        Will make the obfuscation run fail, i.e. the target which uses
        the <code>rename</code>element will fail, if
        yGuard detects any problems.
        </li>
    </ul>
    </td>
    <td><code>false</code></td>
</tr>

<tr>
    <td><code class="property">naming-scheme</code></td>
    <td>
    Can be used to tell the renaming engine to use a different naming
    scheme during the obfuscation.
    Currently this property can be set to one of the following values:
    <ul>
        <li>
        <code class="prop-value">small</code><br>
        Will produce very short names, i.e. the resulting jar
        file will be as small as possible.
        </li>
        <li>
        <code class="prop-value">best</code><br>
        Will produce names, that are very likely to be misunderstood by
        decompilers and disassemblers. Using this naming-scheme it is
        even impossible on most filesystems to successfully unjar or
        unzip the resulting jar file (Windows, Standard Unix, Standard
        Linux, MacOS).
        However this scheme takes up a lot of space and the resulting
        jar is likely to become large (typically roughly double the
        size).
        </li>
        <li>
        <code class="prop-value">mix</code><br>
        Is a mixture of both the other two values, which leads to
        reasonable small but still hard to decompile jar files.
        </li>
    </ul>
    </td>
    <td><code>small</code></td>
</tr>

<tr>
    <td><code class="property">language-conformity</code></td>
    <td>
    Can be used to advise the renaming engine to produce names, that
    should be decompilable by most decompilers. On the other hand,
    yGuard can produce class files that should be executable and
    verifiable by all of todays virtual machines, but produces
    absolutely nonsense names when decompiled (Ever tried to compile
    '<code>int class = false.this super(String$super.init if);</code>'
    ?!)
    Currently this property can be set to one of the following values:
    <ul>
        <li>
        <code class="prop-value">compatible</code><br>
        Will produce names, that are ok for (most) decompilers, java,
        jar and manifest files and can be unzipped to most filesystems.
        </li>
        <li>
        <code class="prop-value">legal</code><br>
        Will produce names, that are ok for (some) decompilers, java,
        jar and manifest files.
        </li>
        <li>
        <code class="prop-value">illegal</code><br>
        Will produce names, that will crash some tools but usually
        <b>not</b> the jvm, but JBuilder7 in many occasions for example.
        </li>
    </ul>
    </td>
    <td><code>legal</code></td>
</tr>

<tr>
    <td><code class="property">overload-enabled</code></td>
    <td>
    Determines whether the renaming engine tries to use the same names
    for methods with different signatures or whether it always generates
    unique method names.
    Setting this property to <code>false</code> eases the analysis of
    stacktraces but reduces the obfuscation effect.
    </td>
    <td><code>true</code></td>
</tr>

<tr>
    <td><code class="property">obfuscation-prefix</code></td>
    <td>
    Can be used to instruct the renaming engine to prefix packages, that
    are fully obfuscated with a given package prefix, e.g.
    <code>com.mycompany.obf</code>.
    </td>
    <td>-</td>
</tr>

<tr>
    <td><code class="property">digests</code></td>
    <td>
    Can be used to tell yGuard which digest algorithms should be used
    for the digest generation in the manifest file. Valid values are
    either <code class="prop-value">none</code>, or a comma-separated
    list of digest-algorithm identifiers, e.g.
    <code class="prop-value">SHA-1, MD5</code> (which is the default).
    </td>
    <td><code>SHA-1, MD5</code></td>
</tr>

<tr>
    <td><code class="property">expose-attributes</code></td>
    <td>
    Can be used to give yGuard a list of attributes yGuard should expose
    in addition to the standard attributes.
    By default yGuard removes unneeded attributes like "Deprecated" from
    methods. The value can be a comma separated list of attributes as
    defined in
    <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7">Section 4.7 of the VM Specification of the .class File Format</a>.
    E.g. in order to keep the "Deprecated" attribute one can add the
    following property:
    <br>
    <code>&lt;property name="expose-attributes" value="Deprecated"/></code>
    <br>
    Note that this affects all classes which will be obfuscated. For a
    better control of which attributes should be exposed in what classes
    use the <a href="#attribute">Attribute Element</a>.
    </td>
    <td>-</td>
</tr>
</tbody>
</table>

#### Child Elements
The `property` element has no child elements.

## The `keep` Element

This element is a child of the [rename](#the-rename-element) or [shrink](#the-shrink-element) element. It can be used to specify elements that are excluded from the parent `rename` or `shrink` task. The excluded classes, methods and fields are defined using nested [package](#the-package-element), [class](#the-class-element), [method](#the-method-element) and [field](#the-field-element) elements.

#### Attributes

The `keep` element provides a number of boolean attributes that determine whether debug information and annotations present in the input class files are to be retained in the output files. The default behavior of the `rename` and `shrink` elements for the respective attributes is explained in the table below.
Note that a more fine-grained control over which attributes to keep for which class files is possible using the [attribute](#the-attribute-element) element. Also, the `attribute` element allows to define attributes to keep for both the rename and the shrinkelement in a common place.

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="58%"><b>Description</b></th>
    <th width="15%"><b>Default (<code>rename</code>)</b></th>
    <th width="15%"><b>Default (<code>shrink</code>)</b></th>
</tr>
</thead>

<tbody>
<tr>
    <td><code>sourcefile</code></td>
    <td>
    Determines whether the name of the original source code file should
    be included in the output class files.
    </td>
    <td><code>remove</code></td>
    <td><code>remove</code></td>
</tr>
<tr>
    <td><code>linenumbertable</code></td>
    <td>
    Determines whether the line number table, that contains a mapping
    from each opcode in the class file to the line number in the
    original source code file should be included in the output class
    files.
    </td>
    <td><code>remove</code> </td>
    <td><code>remove</code> </td>
</tr>
<tr>
    <td><code>localvariabletable</code></td>
    <td>
    Determines whether the local variable table, that contains a mapping
    from each local variable in the class file to the name that has been
    used in the original source code file should be included in the
    output class files.
    </td>
    <td><code>remove</code> </td>
    <td><code>remove</code> </td>
</tr>
<tr>
    <td><code>localvariabletypetable</code></td>
    <td>
    Determines whether the local variable type table, that contains a
    mapping from each local variable in the class file to the name and
    its generic type signature that has been used in the original source
    code file should be included in the output class files.
    </td>
    <td><code>remove</code> </td>
    <td><code>remove</code> </td>
</tr>
<tr>
    <td><code>runtimevisibleannotations</code></td>
    <td>
    Determines whether annotations with the retention policy
    <code>RetentionPolicy.RUNTIME</code>should be included in the output
    class files.
    </td>
    <td><code>keep</code> </td>
    <td><code>keep</code> </td>
</tr>
<tr>
    <td><code>runtimevisibleparameterannotations</code></td>
    <td>
    Determines whether method paramater annotations with the retention
    policy <code>RetentionPolicy.RUNTIME</code> should be included in
    the output class files.
    </td>
    <td><code>keep</code> </td>
    <td><code>keep</code> </td>
</tr>
<tr>
    <td><code>runtimeinvisibleannotations</code></td>
    <td>
    Determines whether annotations with the retention policy
    <code>RetentionPolicy.CLASS</code>should be included in the output
    class files.
    </td>
    <td><code>keep</code> </td>
    <td><code>remove</code> </td>
</tr>
<tr>
    <td><code>runtimeinvisibleparameterannotations</code>
    </td>
    <td>
    Determines whether method paramater annotations with the retention
    policy <code>RetentionPolicy.CLASS</code> should be included in the
    output class files.
    </td>
    <td><code>keep</code> </td>
    <td><code>remove</code> </td>
</tr>
</tbody>
</table>

## The `class` Element

The `class` element can be used for excluding certain classes and/or their fields and methods from the renaming or shrinking process.
If no `name`, `extends` or `implements` attribute is given and the `class` element contains no nested `patternset`, a `class` element matches all class names.

The `classes`, `methods` and `fields` attributes tell the shrinking and renaming engines which classes, methods and fields to keep based on their visibility. The following table lists the possible values for all of these attributes and shows which elements will be excluded. A '*' denotes, that elements that have the given visibility will be excluded for the specified attribute value. A '-' denotes that the these elements will not be excluded from the process.

<table class="matrix">
<tr>
    <td><b>Value/Visibility</b></td>
    <td><code class="keyword">public</code></td>
    <td><code class="keyword">protected</code></td>
    <td><code class="keyword">friendly</code></td>
    <td><code class="keyword">private</code></td>
</tr>
<tr>
    <td><code class="keyword">none</code></td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td><code class="keyword">public</code></td>
    <td>*</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td><code class="keyword">protected</code></td>
    <td>*</td>
    <td>*</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td><code class="keyword">friendly</code></td>
    <td>*</td>
    <td>*</td>
    <td>*</td>
    <td>-</td>
</tr>
<tr>
    <td><code class="keyword">private</code></td>
    <td>*</td>
    <td>*</td>
    <td>*</td>
    <td>*</td>
</tr>
</table>

#### Attributes

 <table class="listing">
<thead>
<tr>
    <th width="12%">Attribute</th>
    <th width="78%">Description</th>
    <th width="10%">Required</th>
</tr>
</thead>

<tbody>
<tr>
    <td><code>name</code></td>
    <td>The name of the class to be kept.</td>
    <td>No</td>
</tr>
<tr>
    <td><code>classes</code></td>
    <td>The visibility of the classes to be kept.</td>
    <td>
    No, defaults to <code>none</code></td>
</tr>
<tr>
    <td><code>methods</code></td>
    <td>The visibility of the methods to be kept.</td>
    <td>
    No, defaults to <code>none</code></td>
</tr>
<tr>
    <td><code>fields</code></td>
    <td>The visibility of the fields to be kept.</td>
    <td>
    No, defaults to <code>none</code></td>
</tr>
<tr>
    <td><a name="extends"></a><code>extends</code></td>
    <td>
    If no <code>name</code> attribute is given, keeps
    all classes that equal or extend the class defined by the given
    fully qualified classname.
    <br>
    See <a href="#ex:extends">serializable_example</a> for an example usage of this
    attribute.
    </td>
    <td>No</td>
</tr>
<tr>
    <td><a name="implements"></a><code>implements</code></td>
    <td>
    If no <code>name</code> attribute is given, keeps
    all classes that equal or implement the class defined by the given
    fully qualified classname.
    <br>
    See <a href="#ex:extends">serializable_example</a> for an example usage of this
    attribute.
    </td>
    <td>No</td>
</tr>
</tbody>
</table>

#### Child elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Explanation

There are three possible ways of specifying which classes will be excluded from the shrinking and obfuscation process:

_1)_ One can specify a single java class using the fully qualified name in java syntax with the name attribute. For example:
```xml
<class name="mypackage.MyClass"/>
```
_2)_ One can specify multiple java classes using a modified version of a patternset. The patternset's includes and excludes element should use java syntax, but the usual wildcards are allowed. Some examples:
```xml
<class>
  <patternset>
    <include name="com.mycompany.**.*Bean"/>
    <exclude name="com.mycompany.secretpackage.*"/>
    <exclude name="com.mycompany.myapp.SecretBean"/>
  </patternset>
</class>
```
_3)_ This will expose all classes which reside in the package subtree of `com.mycompany` and whose name ends with `Bean` except for those, that reside in the `com.mycompany.secretpackage` package and the single `SecretBean` in `com.mycompany.myapp`.

```xml
<class>
  <patternset>
    <include name="com.mycompany.myapp.MainClass"/>
    <include name="org.w3c.sax?."/>
    <exclude name="org.w3c.sax?.**.*$$*"/>
  </patternset>
</class>
```

This will expose the `MainClass` class and all classes, which reside in packages like `org.w3c.sax1`, `org.w3c.sax2`, `org.w3c.saxb` except for inner classes. `'$'` is used as a separator between outer class names and inner class names. Since Ant uses `'$'` as an escape character, you have to use two consecutive `'$'s` (`'$$'`) if you want to pass one as an argument to the task.

_4)_ Finally one can specify classes depending on their visibility, i.e. depending whether they have been declared `public`, `protected`, `package-private` or `private` (inner classes). This can be achieved by additionally specifying the classes attribute in the class element.
 
 ```xml
 <class classes="protected">
  <patternset>
    <include name="com.mycompany.myapi."/>
  </patternset>
</class>
```

This will keep all class names, that are either `public` or `protected` and which reside in one of the subpackages of `com.mycompany.myapi` (note the abbreviation: the trailing dot behaves like the trailing `'/'` in the usual patternset, i.e. it could be rewritten as `com.mycompany.myapi.**.*`)

```xml
<class classes="protected"
  methods="protected"
  fields="protected">
  <patternset>
    <include name="**.*"/>
  </patternset>
</class>
```

This example shows the very common use case of excluding a complete public API from the shrinking and obfuscation process. There is an abbreviation for this use case: you can omit the `patternset` element, since in the case where the `classes` attribute is specified and there is no `patternset` child element used, the task will automatically apply this rule. In this example all classes will be exposed, that are either `public` or `protected`. Their methods and fields will be exposed as long as they are declared `public` or `protected`. If a class is `package-private` or `private` (inner classes), neither itself nor its methods or fields will be exposed.

The last example shows how to keep the `public` methods of certain classes only, but neither field names nor the class names themselves.
```xml
<class classes="none" methods="public" fields="none">
  <patternset>
    <include name="com.mycompany.myapi."/>
  </patternset>
</class>
```

## The `method` Element
Using the `method` element you can specify methods by signature which should be excluded from shrinking or name obfuscation.

#### Attributes
<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>name</code></td>
    <td>
    Specifies the method to keep. Use the complete signature using
    fully qualified class names and the return type!
    </td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>class</code></td>
    <td>
    Specifies the class which contains the method. Use the normal java
    syntax, i.e. the fully qualified name.
    This attribute can be omitted, if the patternset element is used as
    a child element, in which case all classes matching the patternset
    will be searched and their corresponding methods will be kept.
    </td>
    <td>No</td>
</tr>
</table>

#### Child Elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Examples

```xml
<method class="com.mycompany.myapp.MyClass"
  name="void main(java.lang.String[])"/>
<method class="com.mycompany.myapp.MyClass"
  name="int foo(double[][], java.lang.Object)"/>
<method name="void writeObject(java.io.ObjectOutputStream)">
  <patternset>
    <include name="com.mycompany.myapp.data.*"/>
  </patternset>
</method>
<method name="void readObject(java.io.ObjectInputStream)">
  <patternset>
    <include name="com.mycompany.myapp.data.*"/>
  </patternset>
</method>
```

This will keep the main method of the `MyClass` class and the `foo` method. Additionally all `readObject` and `writeObject` methods (used for serialization) will be kept in all classes of the `com.mycompany.myapp.data` package. Note that you have to specify the return argument's type, even if it is void and that you have to use the fully qualified name for all classes, even those, that are in the `java.lang package`.

## The `field` Element

Using the `field` element you can specify fields by name which should be excluded from shrinking or name obfuscation.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>name</code></td>
    <td>
    Specifies the field to keep.
    Use the name of the field only, do not include its type!
    </td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>class</code></td>
    <td>
    Specifies the class which contains the field.
    Use the normal java syntax, i.e. the fully qualified name.
    This attribute can be omitted, if the
    <code>patternset</code> element is used as
    a child element, in which case the all classes matching the
    patternset will be searched and their corresponding fields will be
    kept.
    </td>
    <td>No</td>
</tr>
</table>

#### Child Elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Examples

```xml
<field class="com.mycompany.myapp.MyClass" name="field"/>
<field name="serialVersionUID">
  <patternset>
    <include name="com.mycompany.myapp.data.*"/>
  </patternset>
</field>
```

This will keep the field named `field` of the `MyClass` class. Additionally all the `serialVersionUID` fields (used for serialization) will be kept in all classes of the `com.mycompany.myapp.data` package.

## The `package` Element
The `package` element can be used for excluding certain package's names from the renaming process. It cannot be used for the shrinking process.

All packages that are matched be the nested patternset element will not be obfuscated. This has no influence on the class, method, or field names but will only result in the package's name not being obfuscated. Normally, it is not necessary to use this element, instead the [class element](#the-class-element) is used to keep class names (and thus their package names) from being obfuscated.

#### Child Elements

- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Example

```xml
<package>
  <patternset>
    <include name="com.mycompany.myapp.*"/>
  </patternset>
</package>
```

This will keep the names of all packages that are direct descendants of `com.mycompany.myapp`. This will not influence the names of the classes contained in these packages.

## The `sourcefile` Element

The `sourcefile` element allows for a special treatment of the sourceFile attribute by the rename element.
Using nested property elements, the mapping of sourceFile attributes in obfuscated class files can be adjusted.

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Name</b></th>
    <th width="88%"><b>Description</b></th>
</tr>
</thead>
<tbody>
<tr>
    <td><code class="property">mapping</code></td>
    <td>
    The value of this property determines the name all
    <code>sourceFile</code> attributes matched by the
    <code>sourcefile</code> element are mapped to.
    </td>
</tr>
</tbody>
</table>

#### Child Elements

- [property](#the-property-element)
- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Example

```xml
<sourcefile>
  <property name="mapping" value="y"/>
  <patternset>
    <include name="com.mycompany.myapp.**"/>
  </patternset>
</sourcefile>
```

This will map all of the `sourceFile` attributes in the packages below `com.mycompany.myapp` to _"y"_, which is small and generally a nice letter.

## The `linenumbertable` Element

The `linenumbertable` element allows for a special treatment of the linenumbertable attribute by the [rename](#the-rename-element) element.

Using nested `property` elements, the mapping of `linenumbertable` attributes in obfuscated class files can be adjusted.

#### Attributes

 <table class="listing">
<thead>
<tr>
    <th width="12%"><b>Name</b></th>
    <th width="88%"><b>Description</b></th>
</tr>
</thead>

<tbody>
<tr>
    <td><code class="property">mapping-scheme</code></td>
    <td>
    Can be used with the following two values:
    <ul>
        <li>
        <code class="prop-value">scramble</code><br>
        This will use a non-trivial algorithm to scramble the line
        numbers in the existing file.
        The algorithm implemented uses a different scrambling scheme for
        each class. The optional
        <code class="property">scrambling-salt</code> property can be
        used to provide an integer value that will be used to "salt"
        the algorithm's random seed for the scrambling.
        The size of the (uncompressed) .class file will not change using
        this mapping scheme.
        </li>
        <li>
        <code class="prop-value">squeeze</code><br>
        This will use a simple algorithm that virtually puts all of a
        method's code into the first line of code of the method. It will
        appear as if each method had been written in a single line of
        code.
        The advantage of this scheme is drastically reduced size
        requirements and thus smaller .class files, while at the same
        time it will be possible to unambiguously determine the exact
        method from a stacktrace.
        </li>
    </ul>
    </td>
</tr>
<tr>
    <td><code class="property">scrambling-salt</code></td>
    <td>
    Can be used in conjunction with
    <code class="property">mapping-scheme</code> to provide an integer
    value that will be used to "salt" the algorithm's random seed for
    the scrambling.
    </td>
</tr>
</tbody>
</table>

#### Child Elements

- [property](#the-property-element)
- [patternset](http://ant.apache.org/manual/Types/patternset.html)

#### Examples

```xml
<linenumbertable>
  <patternset>
    <include name="com.mycompany.myapp.**"/>
  </patternset>
</linenumbertable>
```

This will keep the line numbers of all the classes in the `com.mycompany.myapp` packages and subpackages. Note that in order to see the line numbers in stacktraces, the sourcefile attribute has to be retained for those files, too, since otherwise the JDK will display _"Unknown source"_ for the stack elements.

```xml
<linenumbertable>
  <property name="mapping-scheme" value="scramble"/>
  <property name="scrambling-salt" value="1234"/>
  <patternset id="CompanyPatternSet">
    <include name="com.mycompany.myapp.**"/>
  </patternset>
</linenumbertable>
<sourcefile>
  <property name="mapping" value="y"/>
  <patternset refid="CompanyPatternSet"/>
</sourcefile>
```

This will keep scrambled line numbers for all classes found in and below the `com.mycompany.myapp` packages. The scrambling algorithm will use the given _"salt"_ value to use a predefined scrambling scheme. In order to see the scrambled line numbers, a [sourcefile](#the-sourcefile-element) element is used on the same patternset, which is referenced by its previously declared reference id, to rename the source files to _"y"_.

## The `adjust` Element
Using the `adjust` element one can specify resource files whose names and/or contents should be adjusted by the rename engine to reflect the obfuscated class names.

**Note**: This will only adjust files that are part of the *inoutpair* jars! _I.e._ the fileset's root directory is the combined root of all jars that are passed to yGuard via the `inoutpair` elements. yGuard will not modify any of the files on disk, except for the out-jar!

#### Attributes

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Required</b></th>
</tr>
</thead>

<tr>
    <td><code>replaceName</code></td>
    <td>
    Specifies whether or not the names of the specified resources should
    be adjusted.
    </td>
    <td>No, defaults to <code>false</code>
    </td>
</tr>
<tr>
    <td><code>replaceContent</code></td>
    <td>
    Specifies whether or not the contents of resource files should be
    adjusted.
    </td>
    <td>No, defaults to <code>false</code>
    </td>
</tr>
<tr>
    <td><code>replaceContentSeparator</code></td>
    <td>
    Specifies which separator is used to replace strings in content.
    </td>
    <td>No, defaults to <code>/</code>
    </td>
</tr>
<tr>
    <td><code>replacePath</code></td>
    <td>
    Specifies whether or not the paths to the resource files should be
    adjusted.
    </td>
    <td>No, defaults to <code>true</code></td>
</tr>
</table>

#### Child Elements

The adjust element can be used just like the standard Ant [`ZipFileSet`](http://ant.apache.org/manual/Types/zipfileset.html) element.

#### Examples

```xml
<!-- adjust the names of all java property files in the jars -->
<adjust replaceName="true">
  <include name="**/*.properties"/>
</adjust>

<!-- adjust the classnames specified within a single XML file in the jar -->
<adjust file="plugins.xml" replaceContent="true" />

<!-- suppress the adjustment of the resource path
com/mycompany/myapp/resource in the jar. -->
<!-- the package com.mycompany.myapp still gets obfuscated. -->
<adjust replacePath="false">
  <include name="com/mycompany/myapp/resource/*"/>
</adjust>
```

## The `map` Element

The `map` element is an immediate optional child of the [rename element](#the-rename-element). It can be used to specify the mapping for the renaming process directly. This is an advanced topic.

#### Child Elements

- [package](#the-package-element)
- [class](#the-class-element)
- [method](#the-method-element)
- [field](#the-field-element)

All of these elements use the `name` attribute to specify the specific element. The `method` and `field` elements need the `class` attribute in order to function properly. Neither wildcards nor nested `patternset` elements are allowed. Use the `map` attribute to specify the new name (subpackage, classname, methodname and fieldname respectively).

#### Examples

```xml
<map>
  <package name="com" map="etc"/>
  <package name="com.mycompany" map="nocompany"/>
  <package name="com.mycompany.myapp" map="asdf"/>
  <class name="com.mycompany.myapp.MainApp" map="foo"/>
  <method class="com.mycompany.myapp.MainApp"
    name="void main(java.lang.String[])" map="bar"/>
  <field class="com.mycompany.myapp.MainApp" name="field" map="a"/>
</map>
```

In this example the package structure `com.mycompany.myapp` will be obfuscated to `etc.nocompany.asdf`. The `MainApp` class will be called `foo` and its `main` method will be remapped to `bar` (and can therefor not be executed from commandline anymore). The field called `field` will be renamed to `a`.

## Controlling obfuscation exclusion with annotations

In order to exclude certain elements from obfuscation, it is possible to use annotations in the source code instead of listing those elements in the [keep element](#the-keep-element).

Any annotation class can be used for this, but it must be specified in the `annotationClass` attribute of the [rename element](#the-rename-element) and follow the convention as explained below to work. yGuard contains such an annotation in the distribution package ready for use. The annotation class is `com.yworks.util.annotation.Obfuscation` and can be found in the yGuard distribution in `ObfuscationAnnotation.jar`. Feel free add this attribute definition to your own codebase and possibly adjust the package name to your needs. Here is the source code for it:

### `Obfuscation.java`

```java
package com.yworks.util.annotation;

public @interface Obfuscation {

  boolean exclude() default true;

  boolean applyToMembers() default true;
}
```

This class is also the default annotation yGuard is looking for when obfuscating. By default the `Obfuscation` annotation is inherited using the `@Inherited` trait. If this behaviour is undesirable, consider creating a custom obfuscation annotation.

The convention for annotation classes that yGuard understands as obfuscation controlling annotations requires two attributes:

<table class="listing">
<thead>
<tr>
    <th width="12%"><b>Attribute</b></th>
    <th width="78%"><b>Description</b></th>
    <th width="10%"><b>Default value</b></th>
</tr>
</thead>

<tr>
    <td><code>exclude</code></td>
    <td>
    Specifies whether the annotated element should be excluded from the
    obfuscation. Note, that when retaining a class, the hierarchy of
    that class, i.e. the chain of outer class names and the package
    name, is also retained.
    </td>
    <td><code>true</code></td>
</tr>
<tr>
    <td><code>applyToMembers</code></td>
    <td>
    Specifies whether the child elements of the annotated element, if
    not otherwise specified, should be excluded from the obfuscation.
    For example, when annotating a class with
    <code>exclude = true</code> and this attribute set to
    <code>true</code>, inner classes, fields and methods of this
    class will be excluded from obfuscation. Annotating a child element
    of an element that has this attribute set to <code>true</code> will
    override the parents annotation configuration.
    </td>
    <td><code>true</code></td>
</tr>
</table>

## Generating Patch JARs

The true power of the `map` element lies in its use together with the `patch` element, which itself is a child element of the `rename` top level element.

#### Attributes

The `patch` element has no attributes.

#### Child Elements

- [class](#the-class-element)

#### Examples

Using the `patch` element one can generate jars, that can be used to serve as patches for versions of an application that have already been deployed in obfuscated form. During the main obfuscation run, yGuard produces an xml-logfile, in which the mapping between the unobfuscated and obfuscated names is contained. The patch element is used to declare a set of classes, that need to be patched. During the obfuscation, yGuard will include those files in the obfuscated jars only, that are declared inside this element.

```xml
<patch>
  <class name="com.mycompany.myapp.MainClass"/>
  <class>
    <patternset>
      <include name="com.mycompany.myapp.bugs.*"/>
    </patternset>
  </class>
</patch>
<map logfile="yguardlog.xml"/>
```
This will only include the MainClass class and all classes that belong to the bugs package in a patch jar. In order to work with the previously delivered obfuscated version, it is important to use the map element to specify the mapping of the elements from the previous run. This can most conveniently be achieved by specifying the log file from the corresponding run in the map element's logfile attribute.

## Deobfuscating stacktraces

yGuard provides a simple tool that makes it easy for the obfuscating party to deobfuscate stacktraces which have been obfuscated using yGuard. During the obfuscation yGuard produces an xml logfile which can automatically be gzipped for convenient storage. You should always keep those logfiles in order to be able to deobfuscate fully qualified classnames or methods or fields for debugging purposes e.g.
In order to run the yGuard deobfuscation tool do the following:

```
Console> java -jar yguard.jar mylogfile.xml
```

A tiny GUI will popup that will enable you to easily deobfuscate stacktraces and fully qualified classnames as well as provide a convenient way to browse the mapping generated by yGuard.
In the main window a tree view displays the package, class, and classmember hierarchy using the unobfuscated names. For each entry that has been obfuscated (classes, packages, methods, and fields that have not been obfuscated at all may not always be shown in the tree) the corresponding mapped/obfuscated name is displayed.
The two buttons at the top of the window allow to change the sorting of the items in the tree structure, so that they are either sorted according to their names before or after the obfuscation. Items will always be sorted by type first: packages, classes, innerclasses, methods, and fields. Small icons provide a convenient way to quickly find the corresponding items.

The lower part of the window contains an editable text area that can be used to enter text or paste stacktraces in. Pressing the button at the bottom of the window labelled "Deobfuscate" will trigger the deobfuscation of the contents in the text area. The tool will try to identify fully qualified class names (separated by dots) and use the mapping information to reconstruct the original names. If the tool identifies a stack trace element, it will try to deobfuscate scrambled line numbers, too, if they have been scrambled during the obfuscation process.

# DTD used for Ant `<yguard>`

The obfuscation and shrinking process can be completely configured inside your Ant script. The yguard task and nested elements should be used according to the following DTD. Note that this is for information purposes only, i.e. you do not have to include the following lines anywhere. This DTD should just provide a quick overview of the yGuard syntax. Due to restrictions of the DTD specification, the given DTD does not describe all available yGuard options. Please browse through the documentation above for complete documentation of the yGuard Ant task elements.

```xml
<!ELEMENT yguard (inoutpair+,externalclasses?,attribute*,(shrink|rename)+)>

<!ELEMENT inoutpair EMPTY>
<!ATTLIST inoutpair
in CDATA #REQUIRED
out CDATA #REQUIRED
resources CDATA #IMPLIED>
<!--
NOTE: the resources attribute only has an effect if a shrink element is present inside the yguard element.
-->

<!ELEMENT externalclasses ANY>
<!-- the externalclasses element is used just like Ant's classpath
element. See the Ant documentation for further details-->

<!ELEMENT attribute (patternset)*>
name CDATA #REQUIRED>

<!ELEMENT shrink (entrypointjar*,keep?)>
<!ATTLIST shrink
logfile CDATA #IMPLIED
createStubs CDATA #IMPLIED>

<!ELEMENT entrypointjar>
<!ATTLIST entrypointjar
name CDATA #REQUIRED>

<!ELEMENT rename (property*,patch?,adjust*,map?,keep?)>
<!ATTLIST rename
mainclass CDATA #IMPLIED
logfile CDATA #IMPLIED
conservemanifest CDATA #IMPLIED
replaceClassNameStrings CDATA #IMPLIED>

<!ELEMENT property EMPTY>
<!ATTLIST property
name CDATA #REQUIRED
value CDATA #REQUIRED>

<!ELEMENT patch (class)*>

<!ELEMENT adjust (#PCDATA)>
<!ATTLIST adjust
replaceName CDATA #REQUIRED
replaceContent CDATA #REQUIRED
replacePath CDATA #REQUIRED>

<!ELEMENT map (class|method|field|package)*>

<!ELEMENT package (patternset)*>
<!ATTLIST package
name CDATA #REQUIRED
map CDATA #REQUIRED>
<!--
NOTE: the map attribute is only supported
if the <package> element is nested inside a <map> element, whereas the patternset is
only supported inside the <keep>/<expose> sections.
-->

<!ELEMENT keep (package|class|method|field|sourcefile|linenumbertable)*>
<!--
NOTE: the nested <package>,<sourcefile>,<linenumbertable> and <attribute> sections are only
supported in the <rename> element.
-->
<!ATTLIST keep
linenumbertable CDATA #IMPLIED
localvariabletable CDATA #IMPLIED
localvariabletypetable CDATA #IMPLIED
runtimeinvisibleannotations CDATA #IMPLIED
runtimeinvisibletypeannotations CDATA #IMPLIED
runtimevisibleannotations CDATA #IMPLIED
runtimevisibletypeannotations CDATA #IMPLIED
sourcefile CDATA #IMPLIED>

<!ELEMENT class (patternset)*>
<!ATTLIST class
classes CDATA #IMPLIED
fields CDATA #IMPLIED
map CDATA #IMPLIED
methods CDATA #IMPLIED
name CDATA #IMPLIED>
<!--
NOTE: the map attribute is only supported
if the <class> element is nested inside an <rename> element.
-->

<!ELEMENT method (patternset)*>
<!ATTLIST method
class CDATA #IMPLIED
map CDATA #IMPLIED
name CDATA #IMPLIED>
<!--
NOTE: the map attribute is only supported
if the <method> element is nested inside an <rename> element.
-->

<!ELEMENT field (patternset)*>
<!ATTLIST field
class CDATA #IMPLIED
map CDATA #IMPLIED
name CDATA #IMPLIED>
<!--
NOTE: the field attribute is only supported
if the <method> element is nested inside an <rename> element.
-->
```

**Attention** users of IDEs that "support" the creation of Ant files (e.g. IDEA's IntelliJ): Your IDE may indicate some errors inside your ANT file when you use yGuard specific elements. This is because the IDE does not know about the DTD used by yGuard. However this is not a real problem, since the Ant file should nevertheless work as expected.
