/*
 * Command.java
 *
 * Created on 26 ottobre 2006, 11.29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import java.util.StringTokenizer;
import alice.logictuple.*;

/**
 *
 * @author admin
 */
public class Command {
    
    private int idDev;
    private String state;
    
    /** Creates a new instance of Command */
    public Command(int id,String s) {
        idDev = id;
        state = s;
    }
    
    public Command(String str) {
        StringTokenizer st = new StringTokenizer(str,",'");
        idDev = Integer.parseInt(st.nextToken());
        state = st.nextToken().trim();
    }
    
    public int getIdDev() {
        return idDev;
    }
    
    public String getState() {
        return state;
    }
    
    public static Command getCommandFromCmdTuple(LogicTuple tt) {
        Command c = null;
        try {
            c = new Command(tt.getArg(2).intValue(),tt.getArg(3).toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return c;
    }
    
    public static Command getCommandForDistributor(LogicTuple tt) {
        Command c = null;
        try {
            c = new Command(tt.getArg(1).toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return c;
    }
    
    public String toString() {
        return idDev+", "+state;
    }
}
