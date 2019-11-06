examples
--------

This folder contains various examples for setting up `yGuard` in different scenarios.

- [application](application/) (a very basic setup. Simple program with one main method and no other exposed functionality.)
- [library](library/) (library example, illustrating how to expose specific parts of a library.) 

## Building a example

All examples can be built using all major build systems. Build instructions cane be found below, depending on your build system of choice.

### Compiling with `Gradle`

```
gradle build obfuscate
cd build/libs/
java -jar example.jar
java -jar example_obf.jar # behaves identically
```

### Compiling with `Maven`

```
mvn compile package
cd target/
java -jar HelloWorld-1.0-SNAPSHOT.jar
java -jar HelloWorld-1.0-SNAPSHOT_obfuscated.jar # behaves identically
```

### Compiling with `Ant`


```
cd ../../
gradle assembleBundleDist # this is a prerequisite for running the ant task and will produce a yguard-bundle-2.9.x.zip

cd examples/example
ant run # will directly run the obfuscated version
```
