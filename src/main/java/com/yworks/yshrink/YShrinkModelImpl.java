package com.yworks.yshrink;

import com.yworks.yguard.YShrinkModel;
import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.ant.ResourceCpResolver;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.core.Analyzer;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.io.IOException;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class YShrinkModelImpl implements YShrinkModel {

  Model model;

  public YShrinkModelImpl() {
    model = new Model();
  }

  public void createSimpleModel( List<ShrinkBag> bags ) throws IOException {

    Analyzer analyzer = new Analyzer();
    analyzer.initModel( model, bags );
    analyzer.createInheritanceEdges( model );

  }

  public Set<String> getAllAncestorClasses( String className ) {
    Set<String> parents = new HashSet<String>( 3 );
    model.getAllAncestorClasses( className, parents );
    return parents;
  }

  public Set<String> getAllImplementedInterfaces( String className ) {
    Set<String> interfaces = new HashSet<String>( 3 );
    model.getAllImplementedInterfaces( className, interfaces );
    return interfaces;
  }

  public Collection<String> getAllClassNames() {
    return model.getAllClassNames();
  }

  public void setResourceClassPath(Path resourceClassPath, Task target) {
    ResourceCpResolver resourceCpResolver = new ResourceCpResolver(resourceClassPath, target);
    model.setClassResolver(resourceCpResolver);
  }

}
