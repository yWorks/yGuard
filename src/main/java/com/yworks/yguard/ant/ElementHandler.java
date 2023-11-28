package com.yworks.yguard.ant;

import org.xml.sax.SAXException;

import java.util.jar.Attributes;

public abstract class ElementHandler {
  protected MapParser mapParser;

  public ElementHandler(MapParser mapParser) {
    this.mapParser = mapParser;
  }

  public abstract void handleElement(String str2, Attributes attributes) throws SAXException;

}

