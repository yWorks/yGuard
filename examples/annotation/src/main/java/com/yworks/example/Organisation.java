package com.yworks.example;

import com.yworks.util.annotation.Obfuscation;

@Obfuscation ( exclude = true, applyToMembers = true )
class Organisation {

  public String name;

  public String category;

  @Obfuscation( exclude = false, applyToMembers = true )
  class Address {

    String countryCode;

    String street;

    String houseNumber;
  }
}
