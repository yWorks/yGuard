---
layout: default
title: Troubleshooting
permalink: /troubleshooting/
---

There are a couple of things you should be aware of when obfuscating and shrinking software.
The weakest part of an application considering name obfuscation and code shrinking is code that uses reflection to dynamically load classes, invoke methods etc. Therefore, you have to be especially careful when using the yguard task on applications that rely on reflection.
The most important facts to keep in mind when using yGuard are described here briefly:

- If you use the `rename` task, code in the form of `MyApplication.class` will break if `MyApplication` will be obfuscated by name and the obfuscation switch [replaceClassNameStrings](task/#the-rename-element) is set to `false`. The `shrink` task will currently recognize code in the form of `MyApplication.class` only if the java files were compiled using an arbitrary version of the standard javac compiler (although the shrinking engine might recognize the `.class` construct also if the classes were compiled using a compiler that generates similar bytecode).
- Automatic introspection and reflection will break in most cases, when you decide to obfuscate the corresponding methods and fields. If you use the `shrink` task and your application uses reflection you should explicitly designate all entities loaded per reflection as code entrypoints using the [keep](task/#the-keep-element) element.
If your application is broken after using the `shrink` task, consider using the [createStubs](task/#the-shrink-element) attribute of the `shrink` task to find out which additional entities you need to include in the keep element.
- `Class.forName(className)` will not work when using the `rename` task unless you use the obfuscated name string in your variable or the String is a local constant and [replaceClassNameStrings](task/#the-keep-element) is not set or set to `true`. If you use the `shrink` task, `className` should be contained in the list of entrypoints using the `keep` element.
- The customized serialization mechanism will not work if you obfuscated or shrinked the writeObject and readObject methods as well as the serializationUID field.
- Simple bean introspection will not work, if you decide to obfuscate your public accessor methods, since it makes use of reflection.
- If you do not set the `-Xmx` property for the Java virtual machine, the `yguard` Ant task might fail due to a `java.lang.OutOfMemoryError`.
To solve this problem, set the `-Xmx` option in the `ANT_OPTS` variable, e.g.:
```
bash> export ANT_OPTS="-Xmx512M"
or
cshell> setenv ANT_OPTS "-Xmx512M"
```
