/*
 * Visitor.java
 *
 * Created on 4 novembre 2006, 16.47
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


public class Visitor {
    
    public int idV;
    public String fn,sn;
    
    /** Creates a new instance of Visitor */
    public Visitor(int id,String f,String s) {                  
        idV = id;
        fn = f;
        sn = s;
    }
    
    @Override
    public String toString() {
	return fn+" "+sn;
    }
    
}
