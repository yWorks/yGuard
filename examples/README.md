examples
--------

This folder contains various examples for setting up `yGuard` in different scenarios.

| Example Name                                      | Description                                                                                                 |
|---------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| [application](application/)                       | Shows how to obfuscates everything but an application's `main` method.<br/>The most basic example there is. |
| [library](library/)                               | Shows how to obfuscate a Java library without changing its public API.                                      |
| [fxml](recipes/LINKED_LIBRARY.md)                 | Shows how to obfuscate a JavaFX application that uses FXML markup.                                          |
| [external_library](external_library/)             | Shows how to obfuscate an application that depends on external libraries.                                   |
| [annotation](annotation/)                         | Shows how to use annotations to exlude types and members from obfuscation.                                  |
| [serializable_exclusion](serializable_exclusion/) | Shows how to use `implements` (and `extends`) to exclude type hierarchies from obfuscation.                 |               
| [processing](processing/)                         | Shows how to obfuscate a Spring Boot application.                                                           |
| [resources](resources/)                           | Shows how to configure `rename` to adjust resource files when renaming classes.                             |

## Building an example

All examples can be built using all major build systems. Build instructions can be found below, depending on your build system of choice.

### Compiling with `Gradle`

```
cd examples/[example name]
gradle build
cd build/libs/
java -jar example_unobf.jar
java -jar example.jar # behaves identically
```

### Compiling with `Maven`

```
cd examples/[example name]
mvn package
cd target/
java -jar example-1.0-SNAPSHOT_unobf.jar
java -jar example-1.0-SNAPSHOT.jar # behaves identically
```

### Compiling with `Ant`


```
cd examples/[example name]
ant obfuscate
cd build/jar/
java -jar example_unobf.jar
java -jar example.jar # behaves identically
```
