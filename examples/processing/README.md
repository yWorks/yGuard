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

# create a temporary JAR containing only the classes of the application
cd BOOT-INF/classes/
jar cvf ../target/HelloWorld-1.0-SNAPSHOT.jar application.properties com
cd ../

# package and obfuscate the actual application
mvn package

# extract the classes from the obfuscated JAR
cd classes/
jar -xvf ../target/HelloWorld-1.0-SNAPSHOT_obfuscated.jar com

# clean up the `BOOT-INF` folder
cd ../
mvn clean

# update the JAR with obfuscated classes
cd ../
jar uvf target/HelloWorld-0.0.1-SNAPSHOT.jar BOOT-INF
```

Please note that we are considering to add support to obfuscate and shrink `.class` files from arbitrary directories, which would make creating a _"temporary"_ JAR obsolete. We will update this example as it happens.
