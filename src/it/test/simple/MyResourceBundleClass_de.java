package test.simple;

import java.util.ListResourceBundle;

/**
 * The type My resource bundle class de.
 */
public class MyResourceBundleClass_de extends ListResourceBundle  {

    /**
     * The Contents.
     */
    static final Object[][] contents = {
              {"MESSAGE", "MOIN MOIN"},
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
