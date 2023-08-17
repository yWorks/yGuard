package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation(exclude = true, applyToMembers = true)
public class Employee {

  @Obfuscation(exclude = false)
  public String name;

  public String position;

  public String businessUnit;
}