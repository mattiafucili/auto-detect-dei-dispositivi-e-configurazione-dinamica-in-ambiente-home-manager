/*
 * Room.java
 *
 * Created on 13 ottobre 2006, 15.22
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

public class Room {

    public int idRoom, wd, heat, ac;
    public String name;
    public float mq;

    public Room(int id, String n, float m, int w, int h, int a) {
      idRoom = id;
      name = n;
      mq = m;
      wd = w;
      heat = h;
      ac = a;
    }
    
    @Override
    public String toString() {
	return "Name: "+name+"\n"+
		"Area: "+mq+" mq\n"+
		"Windows and Doors (nr): "+wd+"\n"+
		"Heaters (nr): "+heat+"\n"+
		"Air Conditioners: "+ac;
    }
}
