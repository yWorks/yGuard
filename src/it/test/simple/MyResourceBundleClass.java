package test.simple;

import java.util.ListResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: schroede
 * Date: 07.09.2007
 * Time: 16:32:02
 * To change this template use File | Settings | File Templates.
 */
public class MyResourceBundleClass extends ListResourceBundle {

  static final Object[][] contents = {
              {"MESSAGE", "DEFAULT_MESSAGE"},
      };

  protected Object[][] getContents() {
    return contents;
  }
  


}
