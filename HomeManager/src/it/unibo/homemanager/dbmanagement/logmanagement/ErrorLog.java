/*
 * ErrorLog.java
 *
 * Created on 10 ottobre 2006, 21.34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.dbmanagement.logmanagement;

import it.unibo.homemanager.HomeManagerConstants;

/**
 *
 * @author admin
 */
public class ErrorLog {
    
    private HomeManagerConstants hmc = new HomeManagerConstants();
    private LService ls = new LService();
    
    /** Creates a new instance of ErrorLog */
    public ErrorLog() {}
    
  public void fatalErrorLog(String logMessage) {   
    ls.logPrintln(hmc.fatalLogFile,logMessage);    
  }
  
  public void generalErrorLog(String logMessage) {   
    ls.logPrintln(hmc.generalLogFile,logMessage);    
  }  
  
  public void generalExceptionLog(String logMessage) {   
    ls.logPrintln(hmc.genExceptionLogFile,logMessage);    
  }  
  
  public void warningLog(String logMessage) {   
    ls.logPrintln(hmc.warningLogFile,logMessage);    
  }
  
  public void databaseErrorLog(String logMessage) {   
    ls.logPrintln(hmc.dbServiceLogFile,logMessage);    
  }
  
  public void FrontendErrorLog(String logMessage) {   
    ls.logPrintln(hmc.ftErrorLogFile,logMessage);    
  }    
}
