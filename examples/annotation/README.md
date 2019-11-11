annotation
----------

This section gives examples on how to use annotations to control which elements should be excluded from the obfuscation process (i.e., keep their names). The following assumes that there is an annotation class named `com.yworks.util.annotation.Obfuscation` in the classpath that follows the convention as described above.

## Obfuscation Exclusion per Item

```java
@com.yworks.util.annotation.Obfuscation( exclude = true, applyToMembers = false)
public class Person {

  public String name;

  public String occupation;

  @com.yworks.util.annotation.Obfuscation( exclude = true )
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

The table above shows the result of the obfuscation of this example. The _Person_ class is annotated to be excluded from the obfuscation, but the _applyToMembers_ attribute is set to false, which means that the child elements of the class (the String and int fields) do not inherit this setting from its parent. The _name_ and _occupation_ fields are not annotated and do not inherit the annotation configuration from their parent, so they are obfuscated. The _age_ field however is also annotated to be excluded from obfuscation and thus keeps its name.

## Obfuscation Exclusion for Members Using applyToMembers

```java
@com.yworks.util.annotation.Obfuscation( exclude = true, applyToMembers = true)
public class Person {

  @com.yworks.util.annotation.Obfuscation( exclude = false )
  public String name;

  public String occupation;

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
    <td>No</td>
</tr>
<tr>
    <td><code>field age</code></td>
    <td>No</td>
</tr>
</table>

Again, the class _Person_ keeps its name, but the configuration is inherited by its members, too. The fields _occupation_ and _age_ have no annotation set and inherit the configuration of its parent, which is exclude = true, so they keep their names. The field _name_ however specifies its own annotation and thus overrides the configuration of its parent and sets its own exclusion to false, so it is obfuscated.

## Obfuscation Exclusion for Members using applyToMembers in the Case of Nested Classes

```java
@Obfuscation ( exclude = true, applyToMembers = true )
public class Person {

  public String name;

  public String occupation;

  public int age;

  @Obfuscation ( exclude = false, applyToMembers = true )
  class BirthInfo {

    String birthPlace;

    class Date {

      int year;

      int month;

      int day;
    }
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
    <td><code>class Person</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field name</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field occupation</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field age</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>class BirthInfo</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field birthPlace</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>class Date</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field year</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field month</code></td>
    <td>Yes</td>
</tr>
<tr>
    <td><code>field day</code></td>
    <td>Yes</td>
</tr>
</table>

The `applyToMembers` configuration is also applicable to inner classes: In the above example, the top level class `Person` is annotated so that itself and all its members should be excluded from obfuscation. But, the inner class `BirthInfo` overrides this configuration by setting exclude to false for itself and all of its members. When yGuard has to decide whether to keep or obfuscate a non-annotated element, then it will look for annotations by going up the nesting-hierarchy until the top level class is reached or a parent has the `applyToMembers` configuration set to true. In this case, when deciding whether to keep the inner class `Date`, yGuard goes up the hierarchy and finds the annotation at `BirthInfo`, which is annotated `exclude = false`. This results in `Date` being obfuscated. The same applies to the members of `Date`.

## Obfuscation Exclusion Using applyToMembers in the Case of Nested Classes II

```java
@Obfuscation ( exclude = true, applyToMembers = true )
public class Person {

  public String name;

  public String occupation;

  public int age;

  @Obfuscation ( exclude = false, applyToMembers = false )
  class BirthInfo {

    String birthPlace;

    class Date {

      int year;

      int month;

      int day;
    }
  }
}
```

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
    <td>No</td>
</tr>
<tr>
    <td><code>field occupation</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field age</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>class BirthInfo</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field birthPlace</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>class Date</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field year</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field month</code></td>
    <td>No</td>
</tr>
<tr>
    <td><code>field day</code></td>
    <td>No</td>
</tr>
</table>

The above example shows almost the same code as in Example 8.c, but this time the class <code>BirthInfo</code> has the <span class="attribute">applyToMembers</span> annotation set to <code>false</code>. One might expect that, as a result, all members of <code>BirthInfo</code> are kept and only <code>BirthInfo</code> is obfuscated, but instead, all elements in this example are kept. The explanation: when reaching <code>Date</code>, yGuard is looking up its parent's configuration, <code>BirthInfo</code>, which has <code>applyToMembers = false</code>. yGuard then proceeds to the next parent class in the nesting-hierarchy, <code>Person</code>, which has <code>applyToMembers = true</code> and <code>exclude = true</code> set. This causes <code>Date</code> to be kept. But keeping a class also means that its fully qualified name and thus its nesting-hierarchy needs to be kept. Although the annotation of <code>BirthInfo</code> does not explicitly retain the the name of the class, the retention of <code>Date</code> and its nesting-hierarchy causes <code>BirthInfo</code> to be kept.

## Default Behavior

```java
public class Person {

  public String name;

  public String occupation;

  @com.yworks.util.annotation.Obfuscation
  public int age;

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
    <td><code>class Person</code></td>
    <td>Yes</td>
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

This example shows the default behavior of yGuard without specifying an annotation and when not assigning the attributes of the annotation. <code>Person</code>, <code>name</code> and <code>occupation</code> are obfuscated, while <code>age</code> is annotated. The default value for <span class="attribute">exclude</span> is <code>true</code>, so the field <code>age</code> is not obfuscated.
