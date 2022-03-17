/*
 * WasherProgram.java
 *
 * Created on 31 ottobre 2006, 20.35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.tablemap;

/**
 *
 * @author admin
 */

import it.unibo.homemanager.dbmanagement.dbexceptions.DuplicatedRecordDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.ResultSetDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.NotFoundDbException;
import it.unibo.homemanager.dbmanagement.Database;
import java.sql.*;


public class WasherProgram {
    
    public int idW,idDev,duration;
    public String prog;
    
    /** Creates a new instance of WasherProgram */
    public WasherProgram(int id,int idD,String p,int d) {
        idW = id;
        idDev = idD;
        prog = p;
        duration = d;
    }
    
    @Override
    public String toString() {
	return "Name: " + prog + "\n"+
		"Duration: " + duration;
    }
}
