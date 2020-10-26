package com.yworks.yguard.obf;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Determines obfuscated class and method names from a yGuard mapping log file.
 *
 * @author Thomas Behr
 */
class Mapper {
  private final Map<String, String> mappings;

  private Mapper( final Map<String, String> mappings ) {
    this.mappings = mappings;
  }

    /**
     * Gets type name.
     *
     * @param tn the tn
     * @return the type name
     */
    String getTypeName( final String tn ) {
    return mappings.get("c:" + tn);
  }

    /**
     * Gets method name.
     *
     * @param tn the tn
     * @param mn the mn
     * @return the method name
     */
    String getMethodName( final String tn, final String mn ) {
    return mappings.get("m:" + tn + '#' + mn);
  }


    /**
     * New instance mapper.
     *
     * @param log the log
     * @return the mapper
     * @throws Exception the exception
     */
    static Mapper newInstance( final String log ) throws Exception {
    final HashMap<String, String> mappings = new HashMap<String, String>();
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    final SAXParser parser = factory.newSAXParser();
    parser.parse(
            new InputSource(new StringReader("<root>\n" + log + "</root>\n")),
            new MapHandler(mappings));
    return new Mapper(mappings);
  }



  private static class MapHandler extends DefaultHandler2 {
      /**
       * The Mappings.
       */
      final Map<String, String> mappings;
      /**
       * The Armed.
       */
      boolean armed;

      /**
       * Instantiates a new Map handler.
       *
       * @param mappings the mappings
       */
      MapHandler( final Map<String, String> mappings ) {
      this.mappings = mappings;
    }

    @Override
    public void startDocument() throws SAXException {
      armed = false;
    }

    @Override
    public void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes
    ) throws SAXException {
      if (armed) {
        if ("package".equals(qName)) {
          final String pn = attributes.getValue("name");
          final int idx = pn.lastIndexOf('.');
          if (idx < 0) {
            mappings.put("p:" + pn, attributes.getValue("map"));
          } else {
            final String prefix = mappings.get("p:" + pn.substring(0, idx));
            if (prefix == null) {
              mappings.put("p:" + pn, attributes.getValue("map"));
            } else {
              mappings.put("p:" + pn, prefix + "." + attributes.getValue("map"));
            }
          }
        } else if ("class".equals(qName)) {
          final String cn = attributes.getValue("name");
          final int idx = cn.lastIndexOf('.');
          if (idx < 0) {
            mappings.put("c:" + cn, attributes.getValue("map"));
          } else {
            final String prefix = mappings.get("p:" + cn.substring(0, idx));
            if (prefix == null) {
              mappings.put("c:" + cn, attributes.getValue("map"));
            } else {
              mappings.put("c:" + cn, prefix + "." + attributes.getValue("map"));
            }
          }
        } else if ("method".equals(qName)) {
          final String cn = attributes.getValue("class");
          final String mn = attributes.getValue("name");
          mappings.put("m:" + cn + '#' + mn, attributes.getValue("map"));
        }
      } else {
        armed |= "map".equals(qName);
      }
    }
  }
}
