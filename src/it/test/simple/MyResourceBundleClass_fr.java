package test.simple;

import java.util.ListResourceBundle;

/**
 * The type My resource bundle class fr.
 */
public class MyResourceBundleClass_fr extends ListResourceBundle  {

    /**
     * The Contents.
     */
    static final Object[][] contents = {
              {"MESSAGE", "BONJOUR"},
      };

    /**
     * Get contents object [ ] [ ].
     *
     * @return the object [ ] [ ]
     */
    protected Object[][] getContents() {
    return contents;
  }

}
