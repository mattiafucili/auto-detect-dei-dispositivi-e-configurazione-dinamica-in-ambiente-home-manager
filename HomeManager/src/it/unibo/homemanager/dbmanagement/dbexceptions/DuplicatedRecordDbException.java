/*
 * DuplicatedRecordDbException.java
 *
 * Created on 11 ottobre 2006, 11.49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.dbexceptions;

import it.unibo.homemanager.dbmanagement.errorservice.Warning;
import java.io.*;

import it.unibo.homemanager.dbmanagement.*;

/**
 *
 * @author admin
 */

public class DuplicatedRecordDbException extends DbException implements Warning {
  
  
  /** Creates new DuplicatedRecordDBException */
  public DuplicatedRecordDbException(String msg) {
    super("Warning: "+msg);
    this.logMessage="Warning\n"+msg+"\n";
  }
  
  /** Ritorna il messaggio di Errore corrispondente al Warning **/
  public String getLogMessage() {
    return logMessage;
  }
  
}