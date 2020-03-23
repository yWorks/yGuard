serializable_exclusion
----------------------

This example demonstrates the usage of the *implements* and *extends* attributes of the *class* element. All Serializable classes are excluded from shrinking by using the implements attribute of the class element. 

Additionally, all classes that extend the base class for menu items, `org.myorg.myapp.MyMenuItem`, are defined as entrypoints for the shrinking engine using the extends attribute of the class element. The `readObject` and `writeObject` methods and the field `serialVersionUID` needed for serialization are excluded from name obfuscation as well.
