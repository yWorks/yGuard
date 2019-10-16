yGuard
------

[![Build Status](https://travis-ci.org/yWorks/yguard.svg?branch=master)](https://travis-ci.org/yWorks/yguard)

`yGuard` is an open-source Java obfuscation tool. With `yGuard` it is easy as pie :cake: to configure obfuscation through an extensive `ant` task.

yGuard is brought to you by [yWorks GmbH](https://www.yworks.com/), creator of the family of graph and diagram visualization frameworks [yFiles](https://www.yworks.com/yfiles) and other fine [products](https://www.yworks.com/products).

## Obtaining yGuard

Starting with the `2.8` release `yGuard` is MIT-licensed. Releases can be downloaded directly from GitHub.

Legacy code ported from `retroguard` under `LGPL` is statically linked at compile time via `yGuard-lgpl.jar`.

Previous releases _may_ still be downloaded from the [yWorks download center](https://www.yworks.com/downloads#yGuard).

## Usage

`yGuard` is distributed with usage instructions. In your distribution, `docs/index.html` contains documentation about the bundled `ant` task.

An online version of this documentation [is available as well](https://yworks.github.io/yguard/).

## Building

To build `yGuard` you will need `Java >= 7` and [Gradle](https://gradle.org/).

Once installed you can build `yGuard` like so:
```
gradle build
gradle obfuscate # if you would like to obfuscate the library as well
```

The built files can be found in `build/libs/`.

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
