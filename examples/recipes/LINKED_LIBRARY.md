linked_library
--------------

```
<yguard>

  <inoutpair in="myapp.jar" out="myapp_obf.jar"/>
  <inoutpair in="lib/thirdpartylib.jar" out="lib/thirdpartylib_obf.jar"/>

  <externalclasses>
    <pathelement location="lib/external.jar"/>
  </externalclasses>

  <!-- Keep all of the attributes for debugging, e.g. -->
  <attribute name="Deprecated, SourceFile, LineNumberTable, LocalVariableTable>
    <patternset refid="myopenapp"/>
  </attribute>

  <rename mainclass="org.myorg.myapp.Main" logfile="renamelog.xml">

    <property name="error-checking" value="pedantic"/>

    <keep>
    <!-- Tell the obfuscator to only adjust my classes -->
    <!-- to work with the obfuscated 3rd party library -->
    <!-- but leave them virtually unmodified otherwise -->
    <!-- The libconnector package however will be -->
    <!-- obfuscated as much as possible -->
    <class classes="private" methods="private" fields="private">
      <patternset id="myopenapp">
        <include name="org.myorg.myapp.**"/>
        <exclude name="org.myorg.myapp.mylibconnector.**"/>
      </patternset>
    </class>

    </keep>
  </rename>

</yguard>
```

This example demonstrates almost no _method_, _class_, and _field_ obfuscation for a program, that has external dependencies and additionally depends on a third party library jar which has to be obfuscated before deployment.

Only those parts that actually interface with the third party _jar_ in the `mylibconnector` package are being obfuscated. Nothing in the third party library jar will be exposed in the final application, everything will be obfuscated and the code in the open application that makes use of the third party jar will be adjusted. 

Note that the public part of the application will still be debuggable since all of the crucial attributes will be exposed for the open application part.
The dependencies are specified in the `externalclasses` element using standard [Ant path](http://ant.apache.org/manual/using.html#path) specification mechanisms. Classes residing in `lib/external.jar` will be used to resolve external dependencies during the obfuscation run. This is not strictly necessary in this case since the public API will be fully exposed, i.e. no methods which have been declared by interfaces or super class in external classes will be renamed.
