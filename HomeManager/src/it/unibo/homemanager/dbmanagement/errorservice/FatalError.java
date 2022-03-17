/*
 * FatalError.java
 *
 * Created on 9 maggio 2004, 15.17
 * DESCRIZIONE: consente di recuperare il messaggio
 *  di log, di inviare un messaggio di posta al
 *  responsabile dell'applicazione e di fare la
 *  Rollback()
 */

package it.unibo.homemanager.dbmanagement.errorservice;

  public interface FatalError {

  public String getLogMessage();     
  
  public void log();
  
  public void makeRollBack();
  
}

