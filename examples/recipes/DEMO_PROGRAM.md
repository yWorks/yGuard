Demo Program
------------

```xml
<yguard>
  <inoutpair in="demo.jar" out="demo_obf.jar"/>

  <rename mainclass="com.mycompany.myapp.Main" logfile="renamelog.xml">
    <property name="language-conformity" value="illegal"/>
    <property name="naming-scheme" value="mix"/>

    <keep>
      <class name="com.mycompany.myapp.data.DataObject" methods="public" fields="none"/>
      <class name="com.mycompany.myapp.data.InnerDataObject"/>
      <method name="void writeObject(java.io.ObjectOutputStream)">
        <patternset refid="datapatternset" />
      </method>
      <method name="void readObject(java.io.ObjectInputStream)">
        <patternset refid="datapatternset"/>
      </method>
      <field name="serialVersionUID">
        <patternset refid="datapatternset"/>
      </field>
    </keep>
  </rename>
</yguard>
```

This example demonstrates the common use case of a demo program.

The `keep` section contains examples for excluding classes, methods, and fields
from the renaming process. Typically, these are classes that are loaded
per reflection and fields and methods needed for serialization.  
Note how patternsets can be reused using the `id` and `refid` attributes.
