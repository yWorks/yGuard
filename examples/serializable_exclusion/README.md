serializable_exclusion
----------------------

```
<yguard>

  <inoutpair in="myapp.jar" out="myapp_obf.jar"/>

  <shrink>
    <keep>
      <!-- main method -->
      <method name="void main(java.lang.String[])" class="org.myorg.myapp.Main" />
      <!-- serializable classes -->
      <class implements="java.io.Serializable" classes="private" methods="private" fields="private" />
      <!-- menu items loaded per reflection -->
      <class extends="org.myorg.myapp.MyMenuItem" classes="friendly" methods="public" fields="public" />
    </keep>
  </shrink>

  <rename mainclass="org.myorg.myapp.Main" logfile="renamelog.xml">

    <keep>
      <method name="void readObject(java.io.ObjectInputStream)" />
      <method name="void writeObject(java.io.ObjectOutputStream)" />
      <field name="serialVersionUID" />
      <class extends="org.myorg.myapp.MyMenuItem" classes="friendly" methods="public" fields="public" />
    </keep>
  </rename>

</yguard>
```

This example demonstrates the usage of the *implements* and *extends* attributes of the *class* element. All Serializable classes are excluded from shrinking by using the implements attribute of the class element. 

Additionally, all classes that extend the base class for menu items, `org.myorg.myapp.MyMenuItem`, are defined as entrypoints for the shrinking engine using the extends attribute of the class element. The `readObject` and `writeObject` methods and the field `serialVersionUID` needed for serialization are excluded from name obfuscation as well.
