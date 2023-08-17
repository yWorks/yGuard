package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation(exclude = true, applyToMembers = false)
public class Person {

  public String name;

  public String occupation;

  @Obfuscation(exclude = true)
  public int age;
}