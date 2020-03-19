package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation( exclude = true, applyToMembers = true)
class Member {

  public String id;

  public String department;
}
