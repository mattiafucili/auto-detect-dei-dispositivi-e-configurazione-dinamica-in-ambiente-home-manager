/*
 * To change this template, choose Tools | Templates
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
 * @author Administrator
 */
public class Window {

    public int idL, roomId;
    public String name;

    /** Creates a new instance of Lamp */
    public Window(int id,String nm,int si) {
        idL = id;
        name = nm;
        roomId = si;
    }

    @Override
    public String toString() {
	return "idL: " + idL + ", name: " + name + ", roomId: " + roomId;
    }
}
