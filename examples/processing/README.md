# processing

This example demonstrates how to use pre-processing and post-processing in order to obfuscate JAR's that use non-conformant folders (such as `BOOT-INF`).

This example will use a Spring Boot starter application.

Basically the process runs through the following steps:

1. compile and package the Spring Boot Application
2. extract `classes` from `BOOT_INF`
3. use a standard obfuscation scheme which will leave libraries unobfuscated
4. package the `com` from the obfuscated JAR back into the JAR packaged in step 1

The steps are illustrated with command line instructions below.

```shell
# package the spring boot application
mvn compile package

# extract BOOT_INF (classes)
jar -xvf target/HelloWorld-0.0.1-SNAPSHOT.jar BOOT-INF/classes 

# switch to BOOT-INF to obfuscate the classes
cd BOOT-INF/
mvn package clean

# update the JAR with obfuscated classes
cd ../
jar uvf target/HelloWorld-0.0.1-SNAPSHOT.jar BOOT-INF
```
