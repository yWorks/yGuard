package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.PatternMatchedSection;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.SelectorUtils;

/**
 * The type Pattern matched filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class PatternMatchedFilter extends AbstractEntryPointFilter {

  private Project project;

    /**
     * Instantiates a new Pattern matched filter.
     *
     * @param p the p
     */
    public PatternMatchedFilter( final Project p ) {
    project = p;
  }

    /**
     * Match boolean.
     *
     * @param type    the type
     * @param str     the str
     * @param section the section
     * @return the boolean
     */
    protected boolean match( TypePatternSet.Type type, String str, PatternMatchedSection section ) {

    PatternSet patternSet = section.getPatternSet( type );

    if ( patternSet != null ) {

      String[] excludePatterns = patternSet.getExcludePatterns( project );
      if ( null != excludePatterns ) {
        for ( String excludePattern : excludePatterns ) {
          if ( SelectorUtils.match( excludePattern, str ) ) {
            return false;
          }
        }
      }

      String[] includePatterns = patternSet.getIncludePatterns( project );
      if ( null != includePatterns ) {
        for ( String includePattern : includePatterns ) {
          if ( SelectorUtils.match( includePattern, str ) ) {
            return true;
          }
        }
      } else {
        return true; // no include given: include all
      }
    } else {
      return true; // no patternset for type given: include all
    }
    return false; // str wasnt contained in includes / excludes
  }


}
