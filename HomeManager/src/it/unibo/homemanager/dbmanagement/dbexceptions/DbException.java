/*
 * DbException.java
 *
 * Created on 10 ottobre 2006, 20.29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.dbexceptions;

import it.unibo.homemanager.dbmanagement.logmanagement.ErrorLog;
import it.unibo.homemanager.dbmanagement.*;

/**
 *
 * @author admin
 */
public class DbException extends Exception {
    
    protected Database database = null;
    protected String logMessage;
    protected ErrorLog el = new ErrorLog();
    
    /** Creates a new instance of DbException */
    public DbException() {}
    
    public DbException(String msg) {
        super(msg);
    }
    
    public DbException(String msg,Database database) {
    
    super(msg);
    this.database = database;
    logMessage="Created Exception: "+ msg;
    log();
  }
  
  public void log() {    
    el.databaseErrorLog(logMessage);    
  }
}
