# processing

This example demonstrates how to use post-processing to obfuscate JAR's that use non-conformant folders (such as `BOOT-INF`).

This example will use a Spring Boot starter application.

Basically the process runs through the following steps:

1. compile and package the Spring Boot Application
2. extract `classes` from `BOOT_INF`
3. use a standard obfuscation scheme which will leave libraries unobfuscated
4. package the clasess from the obfuscated JAR back into the JAR packaged in step 1

The steps are illustrated with command line instructions below.

```shell
# package the spring boot application with obfuscated application classes
mvn clean compile package
```
