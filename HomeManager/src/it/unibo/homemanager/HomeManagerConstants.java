/*
 * HomeManagerConstants.java
 *
 * Created on 10 ottobre 2006, 21.50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager;

/**
 *
 * @author admin
 */
public class HomeManagerConstants {
    
    private String path = "../../logfile/";
    
    /** Creates a new instance of HomeManagerConstants */
    public HomeManagerConstants() {}
    
    public String fatalLogFile = path+"fatalLog.txt";
    public String generalLogFile = path+"generalLog.txt";
    public String genExceptionLogFile = path+"genExceptionLog.txt";
    public String warningLogFile = path+"warningLog.txt";
    public String dbServiceLogFile = path+"dbServiceLog.txt";
    public String ftErrorLogFile = path+"ftErrorLog.txt";
}
