package com.yworks.yguard.test.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Tests the annotation config inheritance
 */
public class AnnoTest7KEEP {

  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP1 {

    @Obfuscation(exclude = false, applyToMembers = true)
    public class InnerOBFUSCATE {

      boolean innerFieldOBFUSCATE;
    }
  }

  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP2 {

    @Obfuscation(exclude = false, applyToMembers = false)
    public class InnerOBFUSCATE {

      boolean innerFieldKEEP;
    }
  }

  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP3 {

    @Obfuscation(exclude = false, applyToMembers = true)
    public class InnerOBFUSCATE {

      boolean innerFieldOBFUSCATE;

      class InnerInnerOBFUSCATE {

        // inherits from InnerObfuscate
        boolean innerFieldOBFUSCATE;
      }
    }
  }

  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP4 {

    @Obfuscation(exclude = false, applyToMembers = false)
    public class InnerOBFUSCATE {

      // inherits from KEEP4
      boolean innerFieldKEEP;

      @Obfuscation(exclude = false, applyToMembers = false)
      class InnerInnerOBFUSCATE {

        // inherits from KEEP4
        boolean innerFieldKEEP;
      }
    }
  }
}
