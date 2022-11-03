# Compatibility

## Technical requirements

yGuard requires JDK 1.7.x or greater and Ant 1.5 or greater installed on your system. It may work with earlier versions of these pieces of software as well, however this has not been tested thoroughly. yGuard 1.3.x and upwards works together with Ant 1.6.

## Java 14 - Java 17 Compatibility

Beginning with version 3.1.0, yGuard supports obfuscation of Java class files that contain `record` or `permittedsubclasses` attributes which were introduced with the Java 16 and Java 17 `.class` file formats.

## Java 11 - Java 13 Compatibility

Beginning with version 2.10, yGuard supports obfuscation of Java class files that contain `nesthost` or `hestmembers` attributes which were introduced with the Java 11 `.class` file format.

yGuard does **not** support obfuscating `dynamic` instructions which were introduced with the Java 11 `.class` file format.

Please read the notes regarding 3rd party JVM support if you intend to use yGuard with something other than Java.

## Java 9 / Java 10 Compatibility

Beginning with version 2.7, yGuard supports obfuscation of Java class files that contain module information which was introduced with the Java 9 `.class` file format. yGuard does not change module names, though.

yGuard does **not** support obfuscating multi-release Java archives which were introduced with Java 9.

## Java 7 / Java 8 Compatibility

Beginning with version 2.5, yGuard supports obfuscation of Java class files that contain the `invokedynamic` instruction, which was introduced with the Java 7 `.class` file format. JDK 7 does not contain any means of issuing this instruction, with JDK 8 it is being issued when using lambda expressions or default methods.

While yGuard does fully support obfuscating `invokedynamic` instructions and therefore default methods and lambda expressions, shrinking of Java class files that contain this instruction is not supported yet.

## Compatibility to 3rd party JVM

Obfuscating `dynamic` and `invokedynamic` instructions is a task that is theoretically infeasible. An obfuscation program cannot determine the type and parameters of such instructions in a generic way.
A trade-off solution for this is supporting known `MetaFactory` objects by their signature.
The `JRE` makes this task quite trivial. 
`yGuard` supports the built-in `LambdaMetafactory` and `StringConcatFactory`.

This trade-off however means `yGuard` offers only limited support for instruction sets based on `invokedynamic` or `dynamic`.
In particular, supporting new `JVM` targets, such as Scala, might require manual work.
As we currently do not have the expertise, nor do we have the resources for this project, this is a chore left for the community.

Below is a documentation on the design process involved in supporting the `LambdaMetafactory`. It should serve as a base for anyone deciding to add more support for e.g Scala or Groovy.

### How `LambdaMetafactory` is covered

To check that JVM compatibility is ensured in new releases, we verified that there are no differences in the class file format in JVM >= 11.
This can be checked in the documentation of the [class file format](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html).
The JRE ships two targets for the `invokedynamic` and `dynamic` instruction sets. These are:

- `LambdaMetafactory`
- `StringConcatFactory`

We can recognise these factories in the obfuscation and shrinking steps using their signature. 
Looking at the [documentation](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/invoke/LambdaMetafactory.html) tells us that we should cover two signatures for `LambdaMetaFactory`:

- `java/lang/invoke/LambdaMetafactory#metafactory`
- `java/lang/invoke/LambdaMetafactory#altMetafactory`

`yFiles` recognises these methods [during renaming](https://github.com/yWorks/yGuard/blob/master/retroguard/src/main/java/com/yworks/yguard/obf/classfile/ClassFile.java#L1189).
In order to obfuscate lambdas, these steps are performed:

- if an instance of `InvokeDynamicCpInfo` is found [while parsing the constant pool](https://github.com/yWorks/yGuard/blob/master/retroguard/src/main/java/com/yworks/yguard/obf/classfile/ClassFile.java#L1041), check its signature
- if it has a `LambdaMetafactory` signature [handle the special case](https://github.com/yWorks/yGuard/blob/master/retroguard/src/main/java/com/yworks/yguard/obf/classfile/ClassFile.java#L1046)

In the `LambdaMetafactoryTest` example, this will remap `com/yworks/yguard/obf/LambdaMetaFactoryTest$MyInterface;` to `La/a/a/a/a$_a;`. Most instances of lambda invocations will do a remapping on the JRE [functional interface](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/function/package-summary.html).

However, this process must be carefully tested with actual Java byte code. Even though `StringConcatFactory` uses similar code, its semantics is completely different.
Implementing such a factory requires in-depth knowledge of the underlying mechanism. Even in the case of the `JDK` it is not always perfectly clear which case will be mapped by the compiler to which construct.
