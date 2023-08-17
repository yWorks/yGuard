serializable_exclusion
----------------------

This example demonstrates how to exclude classes based on their type hierarchy.
More specifically, in this example the `implements` attribute of yGuard's
`class` element is used to exclude all classed that implement the
`java.io.Serializable` interface. (In the same way, the `extends` attribute of
the same element could be used to exclude classes that extend a certain base
class).  

Additionally, all classes that extend the base class for menu items,
`com.yworks.example.MyMenuItem`, are defined as entrypoints for the shrinking
engine using the extends attribute of the class element.
The `readObject` and `writeObject` methods and the field `serialVersionUID`
needed for serialization are excluded from name obfuscation as well.
