Linked Library
--------------

```xml
<yguard>

  <inoutpair in="myapp.jar" out="myapp_obf.jar"/>
  <inoutpair in="lib/dep_to_obfuscate.jar" out="lib/dep_to_obfuscate_obf.jar"/>

  <externalclasses>
    <pathelement location="lib/dep_to_keep_as_is.jar"/>
  </externalclasses>

  <patternset id="myopenapp">
    <include name="org.myorg.myapp.**"/>
    <exclude name="org.myorg.myapp.mylibconnector.**"/>
  </patternset>

  <!-- Keep the attributes that provide debugging information. -->
  <attribute name="Deprecated, SourceFile, LineNumberTable, LocalVariableTable">
    <patternset refid="myopenapp"/>
  </attribute>

  <rename mainclass="org.myorg.myapp.Main" logfile="renamelog.xml">
    <keep>
      <!--
        Configure the obfuscator to exclude (most) classes in myapp from
        renaming, but to adjust them as needed to work with the renamed
        dep_to_obfuscate third-party library.
        The libconnector package in myapp will be obfuscated as much as
        possible, though.
        -->
      <class classes="private" methods="private" fields="private">
        <patternset refid="myopenapp"/>
      </class>
    </keep>
  </rename>

</yguard>
```

This example demonstrates how to configure obfuscation for an application that
depends on external libraries, some of which need to be obfuscated and some not.

The classes in dependency `dep_to_obfuscate` will be completely obfuscated.  
The classes in dependency `dep_to_keep_as_is` will not be changed at all.  
The classes in the application will be adjusted for the renamings applied to
`dep_to_obfuscate`, but the names of the application classes and their members
are not obfuscated (except for the classes in the `mylibconnector` package
and sub-packages thereof) due to the exclusion rules in the `keep` element.

The `attribute` element ensures that debugging information is retained for
exactly those classes in the application that are excluded from obfuscation as
well.
