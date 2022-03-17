/*
 * Lamp.java
 *
 * Created on 17 ottobre 2006, 17.54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.tablemap;

import it.unibo.homemanager.dbmanagement.dbexceptions.DuplicatedRecordDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.ResultSetDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.NotFoundDbException;
import it.unibo.homemanager.dbmanagement.Database;
import java.sql.*;


/**
 *
 * @author admin
 */
public class Lamp {
    
    public int idL, sensId;
    public String name;
    
    /** Creates a new instance of Lamp */
    public Lamp(int id,String nm,int si) {
        idL = id;
        name = nm;
        sensId = si;
    }
    
    @Override
    public String toString() {
	return " Name: "+name+"\n";
    }
}
