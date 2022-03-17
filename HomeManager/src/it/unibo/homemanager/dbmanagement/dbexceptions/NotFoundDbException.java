/*
 * NotFoundDbException.java
 *
 * Created on 11 ottobre 2006, 11.42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.dbexceptions;

import it.unibo.homemanager.dbmanagement.errorservice.FatalError;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import it.unibo.homemanager.dbmanagement.Database;

/**
 *
 * @author admin
 */
public class NotFoundDbException extends DbException implements FatalError {
  
  public NotFoundDbException(String msg, Database database) {
    
    super("Fatal Error: "+msg);
    this.database = database;
       
    this.logMessage = "Fatal Error\n"+msg+"\n";
    
    ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
    this.printStackTrace(new PrintWriter(stackTrace,true));        
    
    this.logMessage = this.logMessage+stackTrace.toString();        
    
  }    
  
  public NotFoundDbException(String msg) {    
    this(msg,null);            
  }  
 
 /** Ritorna il messaggio di Errore corrispondente al Fatal Error **/   
  public String getLogMessage() {    
    return logMessage;    
  }
   
 /** Chiamata di RollBack (implementazione classe Astratta FatalError) **/   
  public void makeRollBack() {          
    //if (database!=null) this.database.rollBack();           
  }
}
