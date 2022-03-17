/*
 * Sensor.java
 *
 * Created on 17 ottobre 2006, 15.11
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
public class Sensor {
    
    public int idSens, roomId;
    public String name;
    
    /** Creates a new instance of Sensor */
    public Sensor(int id,String nm,int ri) {
        idSens = id;
        name = nm;
        roomId = ri;
    }
    
    @Override
    public String toString() {
	return "Name: " + name + "\n";
    }
}
