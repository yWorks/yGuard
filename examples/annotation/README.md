annotation
----------

This section gives examples on how to use annotations to control which elements
should be excluded from the obfuscation process (i.e., keep their names). The
following assumes that there is an annotation class named
`com.yworks.util.annotation.Obfuscation` in the classpath that follows the
convention as described above.

## Obfuscation Exclusion per Item

```java
@Obfuscation(exclude = true, applyToMembers = false)
public class Person {

  public String name;

  public String occupation;

  @Obfuscation(exclude = true)
  public int age;
}
```

#### Obfuscation

<table class="listing">
<thead>
<tr>
    <th width="50%"><b>Element</b></th>
    <th width="50%"><b>Obfuscated?</b></th>
</tr>
</thead>
<tr>
    <td><code>class Person</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field name</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field occupation</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field age</code></td>
    <td>No</td>
</tr>
</table>

The table above shows the obfuscation results for this example. The `Person`
class is annotated to be excluded from the obfuscation, but the `applyToMembers`
attribute is set to false, which means that the child elements of the class
(the String and int fields) do not inherit this setting from its parent. The
`name` and `occupation` fields are not annotated and do not inherit the
annotation configuration from their parent, so they are obfuscated. The `age`
field however is also annotated to be excluded from obfuscation and thus keeps
its name.

## Obfuscation Exclusion for Members Using applyToMembers

```java
@Obfuscation( exclude = true, applyToMembers = true)
public class Employee {

  @Obfuscation(exclude = false)
  public String name;

  public String position;

  public String businessUnit;
}
```

#### Obfuscation

<table class="listing">
<thead>
<tr>
    <th width="50%"><b>Element</b></th>
    <th width="50%"><b>Obfuscated?</b></th>
</tr>
</thead>
<tr>
    <td><code>class Employee</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field name</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field position</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field businessUnit</code></td>
    <td>No</td>
</tr>
</table>

Again, the class `Employee` keeps its name, but the configuration is inherited
by its members, too. The fields `position` and `businessUnit` have no annotation
set and inherit the configuration of its parent, which is `exclude = true`, so
they keep their names. The field `name` however specifies its own annotation and
thus overrides the configuration of its parent and sets its own exclusion to
`false`, so it is obfuscated.

## Obfuscation Exclusion for Members using applyToMembers in the Case of Nested Classes

```java
@Obfuscation (exclude = true, applyToMembers = true)
public class Organisation {

  public String name;

  public String category;

  @Obfuscation (exclude = false, applyToMembers = true)
  public static class Address {

    public String countryCode;

    public String street;

    public String houseNumber;
  }
}
```

#### Obfuscation

<table class="listing">
<thead>
<tr>
    <th width="50%"><b>Element</b></th>
    <th width="50%"><b>Obfuscated?</b></th>
</tr>
</thead>
<tr>
    <td><code>class Organisation</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field name</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field category</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>class Address</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field countryCode</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field street</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field houseNumber</code></td>
    <td>Yes</td>
</tr>
</table>

The `applyToMembers` configuration is also applicable to inner classes: In the
above example, the top level class `Organisation` is annotated so that the class
itself and all its members should be excluded from obfuscation. However, the
inner class `Address` overrides this configuration by setting `exclude` to
`false` for itself and all of its members.

## Default Behavior

```java
public class Company {

  public String name;

  @Obfuscation
  public String taxNumber;
}
```

#### Annotation

<table class="listing">
<thead>
<tr>
    <th width="50%"><b>Element</b></th>
    <th width="50%"><b>Obfuscated?</b></th>
</tr>
</thead>
<tr>
    <td><code>class Company</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field name</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field taxNumber</code></td>
    <td>No</td>
</tr>
</table>

This example shows the default behavior of yGuard without specifying an
annotation and when not assigning the attributes of the annotation.
The class `Company` and its field `name` are obfuscated, while the field
`taxNumber` is annotated. The default value for `exclude` is `true`, so the
field `taxNumber` is not obfuscated.
