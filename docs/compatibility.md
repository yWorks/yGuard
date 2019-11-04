---
layout: default
title: Compatibility
permalink: /compatibility/
---

## Technical requirements

yGuard requires JDK 1.7.x or greater and Ant 1.5 or greater installed on your system. It may work with earlier versions of these pieces of software as well, however this has not been tested thoroughly. yGuard 1.3.x and upwards works together with Ant 1.6.

## Java 11 Compatibility

Beginning with version 2.7, yGuard supports obfuscation of Java class files that contain `nesthost` or `nestmembers` attributes which were introduced with the Java 11 `.class` file format.

yGuard does **not** support obfuscating `dynamic` instructions which were introduced with the Java 11 `.class` file format.

## Java 9 / Java 10 Compatibility

Beginning with version 2.7, yGuard supports obfuscation of Java class files that contain module information which was introduced with the Java 9 `.class` file format. yGuard does not change module names, though.

yGuard does **not** support obfuscating multi-release Java archives which were introduced with Java 9.

## Java 7 / Java 8 Compatibility

Beginning with version 2.5, yGuard supports obfuscation of Java class files that contain the `invokedynamic` instruction, which was introduced with the Java 7 `.class` file format. JDK 7 does not contain any means of issuing this instruction, with JDK 8 it is being issued when using lambda expressions or default methods.

While yGuard does fully support obfuscating `invokedynamic` instructions and therefore default methods and lambda expressions, shrinking of Java class files that contain this instruction is not supported yet.
