package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Tests the annotation config inheritance
 */
public class AnnoTest7KEEP {

  /**
   * The type Keep 1.
   */
  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP1 {

    /**
     * The type Inner obfuscate.
     */
    @Obfuscation(exclude = false, applyToMembers = true)
    public class InnerOBFUSCATE {

      /**
       * The Inner field obfuscate.
       */
      boolean innerFieldOBFUSCATE;
    }
  }

  /**
   * The type Keep 2.
   */
  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP2 {

    /**
     * The type Inner obfuscate.
     */
    @Obfuscation(exclude = false, applyToMembers = false)
    public class InnerOBFUSCATE {

      /**
       * The Inner field keep.
       */
      boolean innerFieldKEEP;
    }
  }

  /**
   * The type Keep 3.
   */
  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP3 {

    /**
     * The type Inner obfuscate.
     */
    @Obfuscation(exclude = false, applyToMembers = true)
    public class InnerOBFUSCATE {

      /**
       * The Inner field obfuscate.
       */
      boolean innerFieldOBFUSCATE;

      /**
       * The type Inner inner obfuscate.
       */
      class InnerInnerOBFUSCATE {

        /**
         * The Inner field obfuscate.
         */
// inherits from InnerObfuscate
        boolean innerFieldOBFUSCATE;
      }
    }
  }

  /**
   * The type Keep 4.
   */
  @Obfuscation(exclude = true, applyToMembers = true)
  public class KEEP4 {

    /**
     * The type Inner obfuscate.
     */
    @Obfuscation(exclude = false, applyToMembers = false)
    public class InnerOBFUSCATE {

      /**
       * The Inner field keep.
       */
// inherits from KEEP4
      boolean innerFieldKEEP;

      /**
       * The type Inner inner obfuscate.
       */
      @Obfuscation(exclude = false, applyToMembers = false)
      class InnerInnerOBFUSCATE {

        /**
         * The Inner field keep.
         */
// inherits from KEEP4
        boolean innerFieldKEEP;
      }
    }
  }
}
