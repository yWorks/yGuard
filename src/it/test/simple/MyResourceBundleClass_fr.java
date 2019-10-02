package test.simple;

import java.util.ListResourceBundle;

public class MyResourceBundleClass_fr extends ListResourceBundle  {

  static final Object[][] contents = {
              {"MESSAGE", "BONJOUR"},
      };

  protected Object[][] getContents() {
    return contents;
  }

}
