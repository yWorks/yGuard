library
-------

This case is especially useful if you'd like to provide and expose a public API. 

All the classes, methods and fields, that can be seen in a javadoc generated API will be excluded from the shrinking and renaming tasks. Package friendly and private classes, methods and fields will be shrinked or obfuscated whenever possible.
This example also displays the use of the `attribute` element. In this case it prevents the yguard task from removing the `Deprecated` flag from the entities in the `.class` files.
