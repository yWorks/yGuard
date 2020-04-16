examples
--------

This folder contains various examples for setting up `yGuard` in different scenarios.

- [application](application/) (a very basic setup. Simple program with one main method and no other exposed functionality.)
- [library](library/) (library example, illustrating how to expose specific parts of a library)
- [external_library](external_library/) (shows how to embed an external program into obfuscation)
- [resources](resources/)  (shows how to embed resources, such a `.properties` files)
- [linked_library](recipes/LINKED_LIBRARY.md) (shows how to obfuscate a third party library)
- [serializable_exclusion](serializable_exclusion/) (shows how to exclude serializable elements using `implements` and `extends`)
- [annotation](annotation/) (shows how to use annotations to exlude items from obfuscation) 
- [processing](processing/) (shows how to use yGuard as a intermediate processing step. Demonstrated with Spring Boot.)

## Building a example

All examples can be built using all major build systems. Build instructions cane be found below, depending on your build system of choice.

### Compiling with `Gradle`

```
cd examples/example
gradle build obfuscate
cd build/libs/
java -jar example.jar
java -jar example_obf.jar # behaves identically
```

### Compiling with `Maven`

```
cd examples/example
mvn compile package
cd target/
java -jar HelloWorld-1.0-SNAPSHOT.jar
java -jar HelloWorld-1.0-SNAPSHOT_obfuscated.jar # behaves identically
```

### Compiling with `Ant`


```
cd examples/example
# copy the lib folder to the example including ALL of its dependencies
# adjust the version in the build.xml
ant -p  # for a list of available commands
ant run # will directly run the obfuscated version
```
