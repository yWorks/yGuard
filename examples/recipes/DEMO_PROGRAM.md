# Demo program

This example demonstrates the common use case of a demo program. The keep sections of both the shrink and rename elements contain code entities that will often have to be excluded from the shrinking and renaming process. These are the main code entrypoints (the main method), classes that are loaded per reflection and fields and methods needed for serialization. Note how the same patternsets can be reused using the id and refid attributes.

```xml
<yguard>

  <inoutpair in="demo.jar" out="demo_obf.jar"/>

  <shrink logfile="shrinklog.xml">

    <keep>

      <!-- main method -->
      <method name="void main(java.lang.String[])" class="com.mycompany.myapp.Main" />

      <!-- needed for reflection -->
      <class name="com.mycompany.myapp.data.DataObject"
      methods="public" fields="none"/>
      <!-- needed for reflection (name only) -->
      <class name="com.mycompany.myapp.data.InnerDataObject"/>
      <!-- needed for serialization -->
      <method name="void writeObject(java.io.ObjectOutputStream)">
        <patternset id="datapatternset">
          <include name="com.mycompany.myapp.data.*"/>
        </patternset>
      </method>
      <method name="void readObject(java.io.ObjectInputStream)">
        <patternset refid="datapatternset"/>
      </method>
      <field name="serialVersionUID">
        <patternset refid="datapatternset"/>
      </field>
    </keep>
  </shrink>

  <rename mainclass="com.mycompany.myapp.Main" logfile="renamelog.xml">

    <property name="language-conformity" value="illegal"/>
    <property name="naming-scheme" value="mix"/>
    <keep>
      <class name="com.mycompany.myapp.data.DataObject"
      methods="public" fields="none"/>
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
