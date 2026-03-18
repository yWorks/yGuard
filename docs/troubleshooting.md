# Troubleshooting

There are a couple of things you should be aware of when obfuscating software.
The weakest part of an application considering name obfuscation is code that uses reflection to dynamically load classes, invoke methods etc. Therefore, you have to be especially careful when using the yguard task on applications that rely on reflection.
The most important facts to keep in mind when using yGuard are described here briefly:

- If you use the `rename` task, code in the form of `MyApplication.class` will break if `MyApplication` will be obfuscated by name and the obfuscation switch [replaceClassNameStrings](task_documentation.md#the-rename-element) is set to `false`.
- Automatic introspection and reflection will break in most cases, when you decide to obfuscate the corresponding methods and fields.
- `Class.forName(className)` will not work when using the `rename` task unless you use the obfuscated name string in your variable or the String is a local constant and [replaceClassNameStrings](task_documentation.md#the-keep-element) is not set or set to `true`.
- The customized serialization mechanism will not work if you obfuscated the writeObject and readObject methods as well as the serializationUID field.
- Simple bean introspection will not work, if you decide to obfuscate your public accessor methods, since it makes use of reflection.
- If you do not set the `-Xmx` property for the Java virtual machine, the `yguard` Ant task might fail due to a `java.lang.OutOfMemoryError`.
To solve this problem, set the `-Xmx` option in the `ANT_OPTS` variable, e.g.:
```
bash> export ANT_OPTS="-Xmx512M"
or
cshell> setenv ANT_OPTS "-Xmx512M"
```
