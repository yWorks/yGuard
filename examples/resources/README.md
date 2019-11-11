resources
---------

This example, demonstrates full method and field obfuscation for a program, that uses `.properties` files and other resources files. 

Some configuration files are used that contain fully qualified classnames for plugins that are going to be obfuscated. Therefore yGuard is instructed to automatically replace the plain-text entries in those files with the obfuscated name versions.

Additionally some resources are hardcoded into the classes (image locations and html files, e.g.). yGuard gets instructed not to move these resource files even if they reside in a package structure that is obfuscated.

Since the property files have been created with the same name as the classes that make use of them and they are being loaded using `this.getClass().getName()`, yGuard is configured to rename the `.properties` files according to the obfuscated names of the corresponding `.class` files.
