/*
 * Conversion.java
 *
 * Created on 9 maggio 2004, 18.57
 */

package it.unibo.homemanager.tablemap;

import java.util.*;

public class Conversion {
  
  /** From ' to '' for Oracle queries
   * @param inputString The String to convert
   * @return The converted String
 */
  public static String getDatabaseString(String inputString) {

    if (inputString == null)
      return "-";

    /* inputString = inputString.replace('"', '\''); */

    StringBuffer temp = new StringBuffer();
    int indexFrom = 0;
    int indexTo = 0;
    indexTo = inputString.indexOf("'", indexFrom);
    while (indexTo >= 0) {
      temp.append(inputString.substring(indexFrom, indexTo));
      temp.append("'");
      indexFrom = indexTo;
      indexTo = inputString.indexOf("'", ++indexFrom);
    }
    temp = temp.append(inputString.substring(indexFrom, inputString.length()));

    for (int i=temp.length()-1; i>=0; i--) {
      if (temp.charAt(i) == '"') temp.deleteCharAt(i);
    }

    return temp.toString();

  }



/** Constructs a Vector of String objects from a string tokenized 
 *  by the separator
 * @param toTokenize The String to tokenize
 * @param separator The separator String
 * @return A Vector of String objects
 */
  public static Vector tokenizeString(String toTokenize,String separator) {

    StringTokenizer tokenizer;
    Vector result=new Vector();

    if (toTokenize!=null) {
      tokenizer=new StringTokenizer(toTokenize,separator);
      while (tokenizer.hasMoreTokens())
        result.add((String)tokenizer.nextToken());
    }

    return result;

  }


  /** compatibilit� XML.
   * converte i caratteri speciali html nei corrispondenti xml.
   * @param html
   * @return 
 */

  public static String html2xml (String html) {
     String[] tag = {"amp","euml","ndash","rdquo","ldquo","dollar","quot","rsquo","percnt","nbsp","raquo","quot","Ugrave","Uacute","ugrave","uacute","Ouml","Ograve","Oacute","ograve","oacute","Ntilde","Iuml","Igrave","Iacute","iuml","igrave","iacute","Euml","Egrave","Eacute","euml","egrave","eacute","aacute","agrave","auml","Aacute","Agrave","Auml"};
     String[] xml = {"38","235","173","39", "39", "36", "34", "39", "37","160","187","34","217","218","249","250","214","210","211","242","243","209","207","204","205","239","236","237","203","200","201","235","232","233","225","224","228","193","192","196"};
     for (int i=0; i<tag.length; i++)
        html=replaceAll(html,"&"+tag[i],"&#"+xml[i]);

    return html;
  }

  /** compatibilit� XML.
   * converte i caratteri speciali html nei corrispondenti xml.
   * @param html
   * @return 
 */

  public static String chr2xml (String html) {
      String[] tag = {"�","�","$","%","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�","�"};
      String[] xml = {"235","173", "36", "37","187","217","218","249","250","214","210","211","242","243","209","207","204","205","239","236","237","203","200","201","235","232","233","225","224","228","193","192","196"};
      
      for (int i=0; i<tag.length; i++)
        html = replaceAll(html,tag[i],"&#"+xml[i]+";");
      
    return html;
  }


  /** compatibilit� XML.<br/>
   * & --> &amp;<br/>
   * @param html
   * @return 
 */

  public static String extra2xml (String html) {

    /*  &--> &amp; */
    html = replaceAll (html, "&#", "@@");
    html = replaceAll (html, "&", "&#38;");
    html = replaceAll (html, "@@", "&#" );
    return html;
  }

/** Replace all the substrings in a String with other substrings
 * @param sTxt The String to scan
 * @param sOldTag The substring to substitute
 * @param sNewTag The new String
 * @return The modified String
 */   
  public static String replaceAll (String sTxt, String sOldTag, String sNewTag) {

    String newText = "";
    int pos=0;
    int lastpos=0;
    while ( (pos=sTxt.indexOf(sOldTag,lastpos))!=-1) {
      newText += sTxt.substring(lastpos,pos)+sNewTag;
      lastpos = pos+sOldTag.length();
    }
    newText += sTxt.substring(lastpos,sTxt.length());
    return newText.toString();
  }
  
  //Controlla la presenza di lettere speciali: ovvero lettere che non sono minuscole
  public static boolean islow(String input)
  {
    for(int i=0;i<input.length();i++)
    {
       if(!((input.charAt(i)>'a')&&(input.charAt(i)<'z')))
	 return false;
    }
    return true; 
  }

}
