package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation (exclude = true, applyToMembers = true)
public class Organisation {

  public String name;

  public String category;

  @Obfuscation(exclude = false, applyToMembers = true)
  public static class Address {

    String countryCode;

    String street;

    String houseNumber;
  }
}
