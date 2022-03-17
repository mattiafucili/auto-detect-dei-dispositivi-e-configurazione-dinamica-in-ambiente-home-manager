/*
 * LService.java
 *
 * Created on 10 ottobre 2006, 21.34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.logmanagement;

import java.util.*;
import java.sql.*;
import java.io.*;

import it.unibo.homemanager.dbmanagement.*;

/**
 *
 * @author admin
 */
public class LService {
    
    /** Creates a new instance of LService */
    public LService() {}
    
    public synchronized void logPrintln(String logFile,String logMessage) {

    try {
    
      PrintWriter log = new PrintWriter(new FileOutputStream(logFile,true),true);
      
      java.util.Date now = new java.util.Date();      
      log.println(now+"\t"+logMessage);
      log.flush();
      
    }
    catch (FileNotFoundException ex) {
      System.err.println("LService: logPrintln(): Impossibile trovare il File di Log degli Errori : "+logFile);
      ex.printStackTrace();
    }      
               
  }
    
}
