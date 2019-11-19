external_library
----------------

This example demonstrates full method and field obfuscation for a program, that has external dependencies. 

The dependencies are specified in the externalclasses element using standard Ant path specification mechanisms. Classes residing in `lib/gson.jar` will be used to resolve external dependencies during the obfuscation run. 

This is necessary if external classes want to access obfuscated classes directly using an externally defined interface or superclass. 

yGuard automatically detects externally declared methods and prevents renaming and shrinking of these items. 

As a result, the shrinked and obfuscated jar file can be used together with unmodified versions of external libraries without causing any problems.

This example also demonstrates the use of the `error-checking` property. In this case the Ant target fails if any problem is detected during the obfuscation run.

### Executing the archive with `mvn`

For `mvn`, in order to make the resulting archives work you need to additionally:

- enable the `addClasspath` in the `maven-jar-plugin`
- enable the `maven-dependency-plugin`
- execute the library with additional classpath like so: `java -cp "HelloWorld-1.0-SNAPSHOT_obfuscated.jar:alternateLocation/*" com.yworks.example.HelloWorld`

This will add the `gson.jar` to the execution path. If you handle this differently in your setup (e.g compiling a _fat_ JAR), you can safely ignore this code.
