external_library
----------------

This example demonstrates how to obfuscate an application that depends on
external libraries with e.g. third-party code.

The dependencies are specified in the `externalclasses` element using the
standard `Ant` path specification mechanisms.
Classes residing in `lib/gson-2.8.9.jar` will be used to resolve external
dependencies during the obfuscation run.

yGuard automatically detects externally declared methods and prevents renaming
and shrinking of these items. 

As a result, the shrinked and obfuscated jar file can be used together with
unmodified versions of external libraries without causing any problems.
