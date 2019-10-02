yGuard
------

`yGuard` is an open-source Java obfuscation tool. With `yGuard` it is easy as pie :cake: to configure obfuscation through an extensive `ant` task.

yGuard is brought to you by [yWorks GmbH](https://www.yworks.com/), creator of the outstanding Java™ graph visualization framework [yFiles](https://www.yworks.com/products/yfiles) and other fine [products](https://www.yworks.com/products).

## Obtaining yGuard

With the `2.8` release `yGuard` is now MIT-licensed. Releases can be downloaded directly from GitHub.

Legacy code ported from `retroguard` under `LGPL` is statically linked at compile time via `yGuard-lgpl.jar`.

Previous releases _may_ still be downloaded from the [yWorks download center](https://www.yworks.com/downloads#yGuard).

## Usage

`yGuard` is distributed with usage instructions. In your distribution, `README.html` contains documentation about the bundled `ant` task.

In subsequent releases the documentation _may_ be uploaded to GitHub directly.

## Building

### Ant-based builds

All releases `<=2.8` are built using `ant`. The build system is subject to change in `>=2.9`.

To build an `ant`-based version, follow below instructions:

```
cd deploy
ant deployzib
```

Which will produce an `archive` under `dist`. 

Using `-DversionMajor` and `-DversionMinor` flags will instruct the build to produce a distribution with the respective version.
