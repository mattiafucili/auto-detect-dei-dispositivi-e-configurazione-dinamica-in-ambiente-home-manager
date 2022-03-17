/*
 * ResultSetDbException.java
 *
 * Created on 10 ottobre 2006, 20.30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.dbexceptions;

import it.unibo.homemanager.dbmanagement.errorservice.GeneralError;
import it.unibo.homemanager.dbmanagement.*;

import java.io.*;

/**
 *
 * @author admin
 */

public class ResultSetDbException extends DbException implements GeneralError {
  
  
  public ResultSetDbException(String msg,Database database) {    
    super("General Error: "+msg);
    this.database = database;    
    this.logMessage="General\n"+msg+"\n";    
  }
  

  public ResultSetDbException(String msg) {    
    this(msg,null);    
  }
  
  public String getLogMessage() {    
    return logMessage;    
  }
    
  /*public void makeRollBack() {    
    if (database!=null) this.database.rollBack();    
  }*/

    @Override
    public void makeRollBack() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}