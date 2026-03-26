## Setup

Depending on your build system, you will use [`AntRun`](http://maven.apache.org/plugins/maven-antrun-plugin/) or `Ant` directly to run `yGuard`.

### Setup using `Ant`
Download the bundle from the [Github release page](https://github.com/yWorks/yguard/releases/latest). After downloading and extracting the `jar` files, place them in a path near to your build script. You may use absolute paths, but our examples expect the jar file to lie in the same directory as your build file. Once extracted, you can use the `yguard` element like so:

```xml
<property name="version" value="5.0.0"/>

<target name="yguard">
    <taskdef name="yguard" classname="com.yworks.yguard.YGuardTask" classpath="${projectDir}/yguard-${version}.jar"/>
    <yguard>
        <!-- see the yGuard task documentation for information about the <yguard> element-->
    </yguard>
</target>
``` 

### Setup using `Maven`
Use the [yGuard Maven Plugin](https://github.com/yWorks/yguard-maven-plugin) to run `yGuard` from `Maven`:

```xml
<build>
  [...]
  <plugins>
    [...]
    <plugin>
      <groupId>com.yworks.maven.plugins</groupId>
      <artifactId>yguard-maven-plugin</artifactId>
      <version>1.0.0</version>
      <executions>
        <execution>
          <goals>
            <goal>run</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <yguardVersion>5.0.0</yguardVersion>
        <yguard>
          <!-- see the yGuard task documentation for information about the <yguard> element -->
        </yguard>
      </configuration>
    </plugin>
    [...]
  </plugins>
  [...]
</build>
```

### Setup using `Gradle`

You can use `yGuard` directly from `Maven` central. You can define and use `yGuard` in your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compileOnly 'com.yworks:yguard:5.0.0'
}

task yguard {
  group 'yGuard'
  description 'Obfuscates the java archive.'

  doLast {
    ant.taskdef(
        name: 'yguard',
        classname: 'com.yworks.yguard.YGuardTask',
        classpath: sourceSets.main.compileClasspath.asPath
    )

    ant.yguard {
        // see the yGuard task documentation for information about the yGuard element
    }
  }
}
```
