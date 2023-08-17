fxml
----

This example demonstrates how to obfuscate a JavaFX application.

JavaFX uses reflection to bind fields and methods in controller classes to FXML
markup. While yGuard is able to adjust the qualified names of controller classes
in `*.fxml` files, it is not able to recognize and adjust the names of
controller class members. Thus, the names of fields and methods that are used in
`*.fxml` files have to be excluded from obfuscation.

Note, yGuard is not able to adjust names of `*.fxml` files.
For this reason, FXML source files should be loaded with explicit name strings

```java
FXMLLoader.load(getClass().getResource("HelloWorld.fxml"));
```

to ensure `*.fxml` files can be resolved in classes that have been renamed.
