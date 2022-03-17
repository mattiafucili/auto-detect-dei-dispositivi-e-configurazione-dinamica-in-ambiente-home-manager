/*
 * GeneralError.java
 *
 * Created on 9 maggio 2004, 15.20
 * DESCRIZIONE: gestisce l'errore General Error; 
 *  permette di recuperare il messaggio di log e di
 *  effettuare la Rollback()
 */

package it.unibo.homemanager.dbmanagement.errorservice;


public interface GeneralError {

  public String getLogMessage();

  public void log();
  
  public void makeRollBack();
  
}

