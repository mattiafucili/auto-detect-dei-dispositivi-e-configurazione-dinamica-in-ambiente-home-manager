/*
 * GeneralException.java
 *
 * Created on 9 maggio 2004, 15.24
 * DESCRIZIONE: gestisce la GeneralException;
 *  consente di recuperare o viceversa
 *  di scrivere sul log il messaggio di errore
 */

package it.unibo.homemanager.dbmanagement.errorservice;

public interface GeneralException {   
  
  public String getLogMessage();           
  
  public void log();
  
}
