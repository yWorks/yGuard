yGuard
------

![Continous Integration for yGuard](https://github.com/yWorks/yGuard/workflows/Continous%20Integration%20for%20yGuard/badge.svg)

`yGuard` is an open-source Java obfuscation tool. With `yGuard` it is easy as pie (🍰) to configure obfuscation through an extensive `ant` task.

yGuard is brought to you by [yWorks GmbH](https://www.yworks.com/), creator of the family of graph and diagram visualization frameworks [yFiles](https://www.yworks.com/yfiles) and other fine [products](https://www.yworks.com/products).

## Obtaining yGuard

Recent `yGuard` releases can be downloaded from GitHub, or used from `Maven` central directly.

Legacy code ported from `retroguard` under `LGPL` is compiled into a seperate archive called `retroguard-${VERSION}.jar`.

Previous releases _may_ still be downloaded from the [yWorks download center](https://www.yworks.com/downloads#yGuard).

## Usage

An online version of this documentation [is available](https://yworks.github.io/yGuard/).

Additionally, `yGuard` is distributed with usage instructions. In your distribution, the `docs` folder contains informtion about `yGuard`. Browse `docs/index.html` to view the documentation.

## Examples

`yGuard` comes with a exhaustive set of examples to cover many use cases. Please review the [list of examples in the examples directory](./examples).

## Development

### Building

To build `yGuard` you will need `Java >= 7`.

Download the repository and build `yGuard` using `./gradlew build`.

The bundle version (for Ant users) is built using the `assembleBundleDist` task (e.g `./gradlew assembleBundleDist`).
The `assembleBundleDist` task comes with a optional parameter to copy documentation (`-Pcopy-docs`).

In order to build the documentation you will need [mkdocs](https://www.mkdocs.org/):

```
pip3 install mkdocs # or use a virtual environment if you prefer
mkdocs build
``` 

### Using IntellIJ with yGuard

`yGuard` will be loaded without further ado from `IntellIJ` thanks to the Gradle integration.
Opening the `yGuard` folder will set up Gradle and mark sources, tests and resources accordingly.
