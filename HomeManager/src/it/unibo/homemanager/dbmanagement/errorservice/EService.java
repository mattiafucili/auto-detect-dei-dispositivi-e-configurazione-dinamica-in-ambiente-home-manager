/*
 * EService.java
 *
 * Created on 9 maggio 2004, 15.12
 */


package it.unibo.homemanager.dbmanagement.errorservice;

import it.unibo.homemanager.dbmanagement.logmanagement.ErrorLog;
import java.util.*;
import java.io.*;
import org.xml.sax.*;



public class EService {
  
  /**
   * Rappresenta un errore ingestibile.
   * 
   * Ogni volta che nel flusso del sito si verifica una eccezione
   * che rappresenta una anormale condizione del flusso tale
   * da non poter essere gestita, l'errore � classificato con
   * questa costante affinch� il flusso venga redirezionato
   * verso una pagina di errore.
   *
   */  
  
  public static final int UNRECOVERABLE_ERROR = -1; 
  
  /** 
   * Rappresenta un errore gestibile.
   * 
   * Ogni volta che nel flusso del sito si verifica una eccezione
   * che rappresenta una anormale condizione del flusso tale
   * da poter essere gestita, l'errore � classificato con
   * questa costante affinch� il flusso lo possa gestire.
   *
   */  
      
  public static final int RECOVERABLE_ERROR = -2;
  
  private ErrorLog el = new ErrorLog();
          
  public EService() {}
  
  /** 
   * Gestisce il recover di un Fatal Error. 
   * 
   * Gestisce il recover di un Fatal Error 
   * inviando una mail di notifica al responsabile e loggando l'errore
   * sia sul log dei fatal error che su quello della sezione di codice
   * relativa all'errore.
   *
   */      
  
  public void logAndRecover(FatalError fatalError) {
 
    fatalError.log();
    
    el.fatalErrorLog(fatalError.getLogMessage());    
        
  }
  
  /** 
   *
   * Gestisce il recover di un General Error  
   * loggando l'errore sia sul log dei General Error che su quello
   * della sezione di codice relativa all'errore.
   *
   */        
  
  public void logAndRecover(GeneralError generalError) {
 
    generalError.log();
    
    el.generalErrorLog(generalError.getLogMessage());      
    
  }  
  
  /** 
   *
   * Gestisce il recover di un General Exception  
   * loggando l'errore sia sul log delle General Exception che su quello
   * della sezione di codice relativa all'errore.
   *
   */          
  
  public void logAndRecover(GeneralException generalException) {
 
    generalException.log();
    
    el.generalExceptionLog(generalException.getLogMessage());    
        
  }  

  /**  
   * 
   * Gestisce il recover di un Warning  
   * loggando l'errore sia sul log dei Warning che su quello
   * della sezione di codice relativa all'errore.
   * 
   */            
  
  public void logAndRecover(Warning warning) {
 
    warning.log();
    
    el.warningLog(warning.getLogMessage());    
        
  }
  

  public void logAndRecover(FileNotFoundException ex) {
 
    el.generalExceptionLog(ex.getMessage());
        
  }


  public void logAndRecover(IOException ex) {

    el.generalExceptionLog(ex.getMessage());

  }


  public void logAndRecover(SAXException ex) {

    el.generalExceptionLog(ex.getMessage());

  }


  /**
   *
   * Gestisce il recover di un Warning  
   * loggando l'errore sia sul log dei Warning che su quello
   * della sezione di codice relativa all'errore.
   *
   */            
  
  public void logFrontendException(Throwable exception,Hashtable info,Vector parameters) {
    
    StringBuffer parametersView=new StringBuffer();
    int i;
    
    String message=exception.getMessage();
    
    ByteArrayOutputStream stackTrace=new ByteArrayOutputStream();
    exception.printStackTrace(new PrintWriter(stackTrace,true));              
    
    for (i=0;i<parameters.size();i++) {
      parametersView.append( parameters.elementAt(i)+"\n" );
    }    
    
    el.FrontendErrorLog(message+"\n\n"+stackTrace.toString()+"\n\n"+info.toString()+"\n\n"+parametersView.toString());  
       
    try {
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
  }  
  
  public void logFrontend(String sMesg,Hashtable info,Vector parameters) {
    
    StringBuffer parametersView = new StringBuffer();
    int i;
    
    String message = sMesg;
    
    for (i=0;i<parameters.size();i++) {
      parametersView.append( parameters.elementAt(i)+"\n" );
    }    
    
    el.FrontendErrorLog(message+"\n\n"+info.toString()+"\n\n"+parametersView.toString()); 
    
  }  
  
}
