package com.yworks.yguard.ant;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.MethodSection;
import com.yworks.yguard.ObfuscatorTask;

/**
 * The type Map parser.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public final class MapParser implements ContentHandler {

  private int state;
  private ArrayList entries = new ArrayList(50);
  private Map ownerProperties = new HashMap();
  private final ObfuscatorTask obfuscatorTask;

    /**
     * Instantiates a new Map parser.
     *
     * @param obfuscatorTask the obfuscator task
     */
    public MapParser( ObfuscatorTask obfuscatorTask ) {
    this.obfuscatorTask = obfuscatorTask;
  }

    /**
     * Get entries collection.
     *
     * @return the collection
     */
    public Collection getEntries(){
    return entries;
  }

    /**
     * Get owner properties map.
     *
     * @return the map
     */
    Map getOwnerProperties(){
    return ownerProperties;
  }

  public void characters(char[] values, int param, int param2) {}

  public void endDocument() {
    state = 0;
  }

  public void endElement(String str, String str1, String str2) {
    if (state == 3 && "map".equals(str2)){
      state = 2;
    }
    if (state == 2 && "yguard".equals(str2)){
      state = 1;
    }
  }

  public void endPrefixMapping(String str) {}

  public void ignorableWhitespace(char[] values, int param, int param2) {}

  public void processingInstruction(String str, String str1) {}

  public void setDocumentLocator( Locator locator)
  {}

  public void skippedEntity(String str) {}

  public void startDocument() {
    state = 1;
  }

  public void startElement(String str, String str1, String str2, Attributes attributes) throws SAXException
  {
    switch (state){
    case 2:
      if ("map".equals(str2)){
        state = 3;
      }
      else if ("property".equals(str2)){
        String key = attributes.getValue("key");
        String value = attributes.getValue("key");
        String owner = attributes.getValue("owner");
        Map map = (Map) ownerProperties.get(owner);
        if (map == null){
          map = new HashMap();
          ownerProperties.put(owner, map);
        }
        map.put(key, value);
//          state = 4;
      }
//        else if ("property".equals(str2)){
////          state = 3;
//        }
      break;
    case 1:
      if ("yguard".equals(str2)){
        String version = attributes.getValue("version");
        if ("1.0".equals(version) || "1.1".equals(version) || "1.5".equals(version)){
          state = 2;
        } else {
          throw new SAXNotRecognizedException("Version '"
            + version
            +"' of yguard logfile not supported!");
        }
      }
      break;
    case 3:
      if (str2.equals("package")){
        PackageSection ps = new PackageSection();
        ps.setName(attributes.getValue("name"));
        ps.setMap(attributes.getValue("map"));
        ps.addMapEntries(entries);
      } else
      if (str2.equals("class")){
        ClassSection cs = new ClassSection();
        cs.setName(attributes.getValue("name"));
        cs.setMap(attributes.getValue("map"));
        cs.addMapEntries(entries);
      } else
      if (str2.equals("method")) {
        MethodSection ms = new MethodSection();
        ms.setClass( attributes.getValue( "class" ) );
        ms.setName( attributes.getValue( "name" ) );
        ms.setMap( attributes.getValue( "map" ) );
        ms.addMapEntries( entries );
      } else
      if (str2.equals("field")){
        FieldSection fs = new FieldSection();
        fs.setClass(attributes.getValue("class"));
        fs.setName(attributes.getValue("name"));
        fs.setMap(attributes.getValue("map"));
        fs.addMapEntries(entries);
      } else {
        throw new SAXNotRecognizedException("Unknown child element "+str2+" in map element!");
      }
      break;
    }
  }

  public void startPrefixMapping(String str, String str1) {}
}
