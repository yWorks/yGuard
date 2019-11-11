---
layout: default
title: Installation
permalink: /install/
---

Depending on your build system, you will use `AntRun` or `Ant` directly to install `yGuard`.

### Installation using `Ant`
Download the bundle via the [latest release from GitHub](https://github.com/yWorks/yguard/releases/latest). After downloading and extracting the `jar` files, place it in a path near to your build script. You may use absolute paths, but our example following example expect the jar file to lie in the same directory as your build file. Once extracted, you can use the `yguard` element like so:

```xml
<property name="version" value="2.9.x"/>

<target name="yguard">
    <taskdef name="yguard" classname="com.yworks.yguard.YGuardTask" classpath="${projectDir}/yguard-${version}.jar"/>
    <yguard>
        <!-- see the yGuard task documentation for information about the <yguard> element-->
    </yguard>
</target>
``` 

### Installation using `Maven`
You can use `yGuard` directly from `Maven` central. Add the `yGuard` dependency to your `POM`:

```xml
<dependency>
    <groupId>com.yworks</groupId>
    <artifactId>yguard</artifactId>
    <version>2.9.x</version>
    <scope>compile</scope>
</dependency>
```

Once declared, you can use the `antrun` plugin to define the `yGuard` task like so:
```xml
<plugin>
    <artifactId>maven-antrun-plugin</artifactId>
    <version>1.8</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <id>obfuscate</id>
            <configuration>
                <tasks>
                    <property name="runtime_classpath" refid="maven.runtime.classpath"/>
                    <taskdef name="yguard" classname="com.yworks.yguard.YGuardTask" classpath="${runtime_classpath}"/>
                    <yguard>
                       <!-- see the yGuard task documentation for information about the <yguard> element-->
                    </yguard>
                </tasks>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Installation using `Gradle`

You can use `yGuard` directly from `Maven` central. You can define and use `yGuard` in your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'com.yworks:yguard:2.9.x'
}

task yguard {
  group 'yGuard'
  description 'Obfuscates and shrinks the java archive.'

  doLast {
    ant.taskdef(
        name: 'yguard',
        classname: 'com.yworks.yguard.YGuardTask',
        classpath: sourceSets.main.runtimeClasspath.asPath
    )

    ant.yguard {
        // see the yGuard task documentation for information about the yGuard element
    }
  }
}
```
