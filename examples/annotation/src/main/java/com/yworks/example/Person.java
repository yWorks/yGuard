package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation( exclude = true, applyToMembers = false)
public class Person {

  public String name;

  public String occupation;

  @com.yworks.util.annotation.Obfuscation( exclude = true )
  public int age;
}