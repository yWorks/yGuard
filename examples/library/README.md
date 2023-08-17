library
-------

This example demonstrates obfuscation for a Java library that exposes a public
API. 

All public and protected classes, methods, and fields are excluded from
obfuscaton. Package-private and private classes, methods and fields are renamed
whenever possible.

This example also shows how to use of the `attribute` element. In this case, it
prevents the `yguard` task from removing the `Deprecated` flag from `.class`
files.
