package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Test for inner classes
 */
@Obfuscation(applyToMembers = true, exclude = true)
public class AnnoTest5KEEP {

  public boolean publicBoolField1KEEP;

  @Obfuscation( exclude = false)
  public boolean publicBoolField1OBFUSCATE;

  @Obfuscation(applyToMembers = true, exclude = true)
  public static class Test5InnerKEEP {

    public boolean innerBoolField1KEEP;

    @Obfuscation( exclude = false)
    public boolean innerBoolField1OBFUSCATE;

    public static class Test5InnerInnerKEEP {

      public boolean innerinnerBoolField1OBFUSCATE;

      @Obfuscation( exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }

  @Obfuscation( exclude = false)
  public static class Test5InnerOBFUSCATE {

    @Obfuscation( exclude = true)
    public boolean innerBoolField1KEEP;

    public boolean innerBoolField1OBFUSCATE;

    public static class Test5InnerInnerOBFUSCATE {

      public boolean innerinnerBoolField1OBFUSCATE;
    }
  }

  public static class Test5Inner2KEEP {

    @Obfuscation( exclude = true)
    public boolean innerBoolField1KEEP;

    public boolean innerBoolField1OBFUSCATE;

    public static class Test5InnerInnerOBFUSCATE {

      public boolean innerinnerBoolField1OBFUSCATE;

      @Obfuscation( exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }
}
