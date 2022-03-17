/*
 * Warning.java
 *
 * Created on 9 maggio 2004, 15.26
 * DESCRIZIONE: gestisce gli errori di tipo Warning;
 *  consente di recuperare o viceversa
 *  di scrivere sul log il messaggio di errore
 */

package it.unibo.homemanager.dbmanagement.errorservice;

public interface Warning {

  public String getLogMessage();
  
  public void log();
  
}
