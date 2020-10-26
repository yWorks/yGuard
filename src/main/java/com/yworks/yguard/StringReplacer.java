/*
 * QuotedStringsReplacement.java
 *
 * Created on June 25, 2003, 4:13 PM
 */

package com.yworks.yguard;

import com.yworks.yguard.obf.GuardDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type String replacer.
 *
 * @author wiese
 */
public class StringReplacer
{
  /**
   * The Pattern.
   */
  Pattern pattern;

  /**
   * Creates a new instance of StringReplacer  @param patternString the pattern string
   */
  public StringReplacer(String patternString)
  {
    setPattern(patternString);
  }

  /**
   * Sets pattern.
   *
   * @param patternString the pattern string
   */
  public void setPattern(String patternString)
  {
    pattern = Pattern.compile(patternString);
  }

  /**
   * Replace.
   *
   * @param in     the in
   * @param result the result
   * @param map    the map
   */
  public void replace(String in, StringBuffer result, Map map)
  {
    String line = in;
    
    result.setLength(0);
    Matcher matcher = pattern.matcher(line);
    String match = null;
    String replacement = null;
       
    boolean found = matcher.find();
    while (found)
    {
       match = line.substring(matcher.start(), matcher.end());
       //System.out.println("\n match: " + match); 
       replacement = (String)map.get(match);
       if(replacement == null) replacement = match;
       if (replacement.indexOf('\\') >= 0){
         replacement = replacement.replaceAll("\\\\","\\\\\\\\");
       }
       if (replacement.indexOf('$') >= 0){
         replacement = replacement.replaceAll("\\$","\\\\\\$");
       }
       matcher.appendReplacement(result, replacement); 
       found = matcher.find();
     }
     matcher.appendTail(result);
  }

  /**
   * Replace.
   *
   * @param in        the in
   * @param out       the out
   * @param db        the db
   * @param separator the separator
   * @throws IOException the io exception
   */
  public void replace( Reader in, Writer out, GuardDB db, String separator ) throws IOException
  {
    BufferedReader bin = new BufferedReader(in);
    String line;
    StringBuffer result = new StringBuffer(80);
    
    while((line = bin.readLine())!= null)
    {
       result.setLength(0);
       Matcher matcher = pattern.matcher(line);
       String match;

       boolean found = matcher.find();
       while (found)
       {
         String replacement = "";
         match = line.substring(matcher.start(), matcher.end());
         String[] parts = match.split(Pattern.quote(separator));
         List<String> mapped = db.translateItem(parts);
         while (mapped.size() < parts.length) {
           mapped.add(parts[mapped.size()]);
         }
         for(int i = 0; i < mapped.size(); i++) {
           if (i > 0) replacement += separator;
           replacement += mapped.get(i);
         }
         if (replacement.indexOf('\\') >= 0){
           replacement = replacement.replaceAll("\\\\","\\\\\\\\");
         }
         if (replacement.indexOf('$') >= 0){
           replacement = replacement.replaceAll("\\$","\\\\\\$");
         }

         matcher.appendReplacement(result, replacement); 
         found = matcher.find();
       }
       matcher.appendTail(result);
       out.write(result.toString());
       out.write('\n');
      
    }
  }
  
//  public static void main(String[] args) throws IOException
//  {
//    String pattern = "(?:\\d|\\w|[$])+(\\.(?:\\d|\\w|[$])+)+";
//    
//    Reader in = new InputStreamReader(System.in);
//    Writer out = new OutputStreamWriter(System.out);
//   
//    Map map = new HashMap();
//    map.put("y.view.ReplaceMe",  "[A]");
//    map.put("y.view.ExposedNotMe$Inner",    "[\\B]");
//    map.put("y.view.ExposedNotMe$_0",    "[\\B2]");
//    map.put("y.view.AlsoReplaceMe",  "y.view.AlsoMe_$not\\$_this[C]");
//    
//    StringReplacer sp = new StringReplacer(pattern);
//    sp.replace(in,out, map);
//    
//    out.write("Done\n");
//    out.close();
//  }
}
