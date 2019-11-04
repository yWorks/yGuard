yGuard
------

[![Build Status](https://travis-ci.org/yWorks/yguard.svg?branch=master)](https://travis-ci.org/yWorks/yguard)

`yGuard` is an open-source Java obfuscation tool. With `yGuard` it is easy as pie :cake: to configure obfuscation through an extensive `ant` task.

yGuard is brought to you by [yWorks GmbH](https://www.yworks.com/), creator of the family of graph and diagram visualization frameworks [yFiles](https://www.yworks.com/yfiles) and other fine [products](https://www.yworks.com/products).

## Obtaining yGuard

Recent `yGuard` releases can be downloaded from GitHub, or used from `Maven` central directly.

Legacy code ported from `retroguard` under `LGPL` is compiled into a seperate archive called `retroguard-${VERSION}.jar`.

Previous releases _may_ still be downloaded from the [yWorks download center](https://www.yworks.com/downloads#yGuard).

## Usage

An online version of this documentation [is available](https://yworks.github.io/yguard/).

Additionally, `yGuard` is distributed with usage instructions. In your distribution, the `docs` folder contains informtion about `yGuard`.
In order to properly view the offline documentation, a web server is needed. A quick way to accomplish this is using:

```
cd docs/
python3 -m http.server 4000
```


## Building

To build `yGuard` you will need `Java >= 7` and [Gradle](https://gradle.org/).

Once installed you can build `yGuard` using `gradle build`.

### Using IntellIJ with yGuard

`yGuard` will be loaded without further ado from `IntellIJ` thanks to the Gradle integration.
Opening the `yGuard` folder will set up Gradle and mark sources, tests and resources accordingly.

### Using this repository with a wrapper

This repository does not contain a copy of the Gradle wrapper. There has been [extensive discussions](https://stackoverflow.com/questions/20348451/why-should-the-gradle-wrapper-be-committed-to-vcs) about this topic, however committing a binary file to a `VCS` is simply considered bad practice.

If you prefer to use the wrapper, you can always generate it yourself with an installation of Gradle at hand:
```
gradle wrapper --gradle-version 5.2.1
```

If you do not have an installation of Gradle, you may use [a workaround](http://blog.vorona.ca/init-gradle-wrapper-without-gradle.html).
