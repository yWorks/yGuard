package com.yworks.yguard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces class and package identifiers with the corresponding renamed
 * identifiers.
 * @author wiese
 */
public class StringReplacer
{
  /**
   * The pattern used to find class and package identifiers.
   */
  Pattern pattern;

  /**
   * Creates a new instance of StringReplacer
   * @param patternString the pattern string
   */
  public StringReplacer(String patternString)
  {
    setPattern(patternString);
  }

  /**
   * Sets the pattern used to find class and package identifiers.
   * @param regex the regular expression to be used as pattern.
   */
  public void setPattern(String regex)
  {
    pattern = Pattern.compile(regex);
  }

  /**
   * Replaces class and package identifiers in the given line of text.
   * @param in the line of text in which to replace identifers.
   * @param out the line of text with renamed identifiers.
   * @param map a function that maps class and package identifiers to renamed
   * identifiers.
   */
  public void replace(String in, StringBuffer out, Function<String, String> map)
  {
    replaceImpl(in, out, map);
  }

  /**
   * Replaces class and package identifiers in the given text.
   * @param in the text in which to replace identifers.
   * @param out the text with renamed identifiers.
   * @param map a function that maps class and package identifiers to renamed
   * identifiers.
   */
  public void replace(
    Reader in, Writer out, Function<String, String> map
  ) throws IOException
  {
    StringBuffer result = new StringBuffer(80);
    BufferedReader bin = new BufferedReader(in);
    for (String line = bin.readLine(); line != null; line = bin.readLine()) {
      replaceImpl(line, result, map);
      out.write(result.toString());
      out.write('\n');
    }
  }

  private void replaceImpl(
    final String in, final StringBuffer out, final Function<String, String> map
  ) {
    out.setLength(0);

    Matcher matcher = pattern.matcher(in);
    while (matcher.find()) {
      String match = in.substring(matcher.start(), matcher.end());
      String replacement = safeGet(map, match);
      if (replacement.indexOf('\\') >= 0){
        replacement = replacement.replaceAll("\\\\","\\\\\\\\");
      }
      if (replacement.indexOf('$') >= 0){
        replacement = replacement.replaceAll("\\$","\\\\\\$");
      }
      matcher.appendReplacement(out, replacement);
    }
    matcher.appendTail(out);
  }

  private static String safeGet(
    final Function<String, String> map, final String key
  ) {
    final String value = map.apply(key);
    return value == null ? key : value;
  }
}
