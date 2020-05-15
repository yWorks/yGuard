package com.yworks.yshrink.util;

import org.objectweb.asm.Type;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Util {

  public static final String toJavaClass( String className ) {
    if ( className.endsWith( ".class" ) ) {
      className = className.substring( 0, className.length() - 6 );
    }
    return className.replace( '/', '.' );
  }

  public static final String toInternalClass( String className ) {
    if ( className.endsWith( ".class" ) ) {
      className = className.substring( 0, className.length() - 6 );
    }
    return className.replace( '.', '/' );
  }

  private static final String toNativeType( String type, int arraydim ) {
    StringBuffer nat = new StringBuffer( 30 );
    for ( int i = 0; i < arraydim; i++ ) {
      nat.append( '[' );
    }
    if ( "byte".equals( type ) ) {
      nat.append( 'B' );
    } else if ( "char".equals( type ) ) {
      nat.append( 'C' );
    } else if ( "double".equals( type ) ) {
      nat.append( 'D' );
    } else if ( "float".equals( type ) ) {
      nat.append( 'F' );
    } else if ( "int".equals( type ) ) {
      nat.append( 'I' );
    } else if ( "long".equals( type ) ) {
      nat.append( 'J' );
    } else if ( "short".equals( type ) ) {
      nat.append( 'S' );
    } else if ( "boolean".equals( type ) ) {
      nat.append( 'Z' );
    } else if ( "void".equals( type ) ) {
      nat.append( 'V' );
    } else { //Lclassname;
      nat.append( 'L' );
      nat.append( type.replace( '.', '/' ) );
      nat.append( ';' );
    }
    return nat.toString();
  }

  public static final String verboseToNativeType( String type ) {

    if ( type == "" ) return null;

    Pattern p = Pattern.compile( "\\s*\\[\\s*\\]\\s*" );
    Matcher m = p.matcher( type );

    int arrayDim = 0;
    while ( m.find() ) {
      arrayDim++;
    }

    return toNativeType( type.substring( 0, type.length()-(arrayDim*2)), arrayDim );
  }

  /**
   * extracts the class name or primitve identifier from any type descriptor.
   * <p/>
   * e.g. [[Ltest/ugly/JJ -> test/ugly/JJ
   *
   * @param desc
   * @return the extracted class name or primitive identifier.
   */
  public static final String getTypeNameFromDescriptor( final String desc ) {

    String r = desc;

    final int i = desc.lastIndexOf( '[' );
    if ( i != -1 ) {
      final char type = desc.charAt( i + 1 );
      if ( type != 'L' ) {
        r = String.valueOf( type );
      } else {
        r = desc.substring( i + 2, desc.length() - 1 );
      }
    } else {
      if ( desc.endsWith(";") ) {
        r = desc.substring( 1, desc.length() - 1 );
      }
    }
    return r;
  }

  public static String toJavaType( String type ) {
    StringBuffer nat = new StringBuffer( 30 );
    int arraydim = 0;
    while ( type.charAt( arraydim ) == '[' ) arraydim++;
    type = type.substring( arraydim );
    switch ( type.charAt( 0 ) ) {
      default:
        throw new IllegalArgumentException( "unknown native type:" + type );
      case 'B':
        nat.append( "byte" );
        break;
      case 'C':
        nat.append( "char" );
        break;
      case 'D':
        nat.append( "double" );
        break;
      case 'F':
        nat.append( "float" );
        break;
      case 'I':
        nat.append( "int" );
        break;
      case 'J':
        nat.append( "long" );
        break;
      case 'S':
        nat.append( "short" );
        break;
      case 'Z':
        nat.append( "boolean" );
        break;
      case 'V':
        nat.append( "void" );
        break;
      case 'L':
        String className = type.substring( 1, type.length() - 1 );
        if ( className.indexOf( '<' ) >= 0 ) {
          String parameters = type.substring( className.indexOf( '<' ) + 2, className.lastIndexOf( '>' ) - 1 );
          className = className.substring( 0, className.indexOf( '<' ) );
          nat.append( className.replace( '/', '.' ) );
          nat.append( '<' );
          nat.append( toJavaParameters( parameters ) );
          nat.append( '>' );
        } else {
          nat.append( className.replace( '/', '.' ) );
        }
        break;
    }
    for ( int i = 0; i < arraydim; i++ ) {
      nat.append( "[]" );
    }
    return nat.toString();
  }

  public static String toJavaParameters( String parameters ) {
    StringBuffer nat = new StringBuffer( 30 );
    switch ( parameters.charAt( 0 ) ) {
      default:
        throw new IllegalArgumentException( "unknown native type:" + parameters.charAt( 0 ) );
      case '+':
        nat.append( "? extends " ).append( toJavaParameters( parameters.substring( 1 ) ) );
        break;
      case '-':
        nat.append( "? super " ).append( toJavaParameters( parameters.substring( 1 ) ) );
        break;
      case '*':
        nat.append( "*" );
        if ( parameters.length() > 1 ) {
          nat.append( ", " ).append( toJavaParameters( parameters.substring( 1 ) ) );
        }
        break;
      case 'B':
        nat.append( "byte" );
        break;
      case 'C':
        nat.append( "char" );
        break;
      case 'D':
        nat.append( "double" );
        break;
      case 'F':
        nat.append( "float" );
        break;
      case 'I':
        nat.append( "int" );
        break;
      case 'J':
        nat.append( "long" );
        break;
      case 'S':
        nat.append( "short" );
        break;
      case 'Z':
        nat.append( "boolean" );
        break;
      case 'V':
        nat.append( "void" );
        break;
      case 'L':
        int len = parameters.indexOf( '<' );
        if ( len >= 0 ) {
          len = Math.min( len, parameters.indexOf( ';' ) );
        }
        break;
      case 'T':
        int index = parameters.indexOf( ';' );
        nat.append( parameters.substring( 1, index ) );
        if ( parameters.length() > index ) {
          nat.append( ", " );
          nat.append( parameters.substring( index ) );
        }
        break;
    }
    return nat.toString();
  }

  public static final String getArgumentString( Type[] arguments ) {

    StringBuilder buf = new StringBuilder();
    for ( int i = 0; i < arguments.length - 1; i++ ) {

      buf.append( Util.toJavaType( arguments[ i ].getDescriptor() ) ).append( "," );
    }
    if ( arguments.length > 0 ) {
      buf.append( Util.toJavaType( arguments[ arguments.length - 1 ].getDescriptor() ) );
    }
    return buf.toString();
  }

  public static final String[] toNativeMethod( String javaMethod ) {
    StringTokenizer tokenizer = new StringTokenizer( javaMethod, "(,[]) ", true );
    String tmp = tokenizer.nextToken();
    ;
    while ( tmp.trim().length() == 0 ) {
      tmp = tokenizer.nextToken();
    }
    String returnType = tmp;
    tmp = tokenizer.nextToken();
    int retarraydim = 0;
    while ( tmp.equals( "[" ) ) {
      tmp = tokenizer.nextToken();
      if ( !tmp.equals( "]" ) ) throw new IllegalArgumentException( "']' expected but found " + tmp );
      retarraydim++;
      tmp = tokenizer.nextToken();
    }
    if ( tmp.trim().length() != 0 ) {
      throw new IllegalArgumentException( "space expected but found " + tmp );
    }
    tmp = tokenizer.nextToken();
    while ( tmp.trim().length() == 0 ) {
      tmp = tokenizer.nextToken();
    }
    String name = tmp;
    StringBuffer nativeMethod = new StringBuffer( 30 );
    nativeMethod.append( '(' );
    tmp = tokenizer.nextToken();
    while ( tmp.trim().length() == 0 ) {
      tmp = tokenizer.nextToken();
    }
    if ( !tmp.equals( "(" ) ) throw new IllegalArgumentException( "'(' expected but found " + tmp );
    tmp = tokenizer.nextToken();
    while ( !tmp.equals( ")" ) ) {
      while ( tmp.trim().length() == 0 ) {
        tmp = tokenizer.nextToken();
      }
      String type = tmp;
      tmp = tokenizer.nextToken();
      while ( tmp.trim().length() == 0 ) {
        tmp = tokenizer.nextToken();
      }
      int arraydim = 0;
      while ( tmp.equals( "[" ) ) {
        tmp = tokenizer.nextToken();
        if ( !tmp.equals( "]" ) ) throw new IllegalArgumentException( "']' expected but found " + tmp );
        arraydim++;
        tmp = tokenizer.nextToken();
      }
      while ( tmp.trim().length() == 0 ) {
        tmp = tokenizer.nextToken();
      }

      nativeMethod.append( toNativeType( type, arraydim ) );
      if ( tmp.equals( "," ) ) {
        tmp = tokenizer.nextToken();
        while ( tmp.trim().length() == 0 ) {
          tmp = tokenizer.nextToken();
        }
        continue;
      }
    }
    nativeMethod.append( ')' );
    nativeMethod.append( toNativeType( returnType, retarraydim ) );
    String[] result = new String[]{ name, nativeMethod.toString() };
    return result;
  }

  /**
   * Encode a byte[] as a Base64 (see RFC1521, Section 5.2) String.
   */
  private static final char[] base64 = {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static final char pad = '=';

  public static String toBase64( byte[] b ) {
    StringBuffer sb = new StringBuffer();
    for ( int ptr = 0; ptr < b.length; ptr += 3 ) {
      sb.append( base64[ ( b[ ptr ] >> 2 ) & 0x3F ] );
      if ( ptr + 1 < b.length ) {
        sb.append( base64[ ( ( b[ ptr ] << 4 ) & 0x30 ) | ( ( b[ ptr + 1 ] >> 4 ) & 0x0F ) ] );
        if ( ptr + 2 < b.length ) {
          sb.append( base64[ ( ( b[ ptr + 1 ] << 2 ) & 0x3C ) | ( ( b[ ptr + 2 ] >> 6 ) & 0x03 ) ] );
          sb.append( base64[ b[ ptr + 2 ] & 0x3F ] );
        } else {
          sb.append( base64[ ( b[ ptr + 1 ] << 2 ) & 0x3C ] );
          sb.append( pad );
        }
      } else {
        sb.append( base64[ ( ( b[ ptr ] << 4 ) & 0x30 ) ] );
        sb.append( pad );
        sb.append( pad );
      }
    }
    return sb.toString();
  }
}
