package test.simple;

import java.util.ListResourceBundle;

public class MyResourceBundleClass_de extends ListResourceBundle  {

  static final Object[][] contents = {
              {"MESSAGE", "MOIN MOIN"},
      };

  protected Object[][] getContents() {
    return contents;
  }
}
