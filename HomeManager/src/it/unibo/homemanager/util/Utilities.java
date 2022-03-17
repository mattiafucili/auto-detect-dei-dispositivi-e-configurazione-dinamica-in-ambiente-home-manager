/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sik
 */
public class Utilities {
    
    private static ServiceFactory sf;
    
    public static boolean insertTupleInto(TucsonTupleCentreId tid, LogicTuple tuple) {
        boolean result = false;
        try {
            TucsonAgentId agent = new TucsonAgentId("insertSpecificTuple");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            System.out.println("Inserting " + tuple + " into " + tid.toString());
            ITucsonOperation op = acc.out(tid, tuple, null);
            result = op.isResultSuccess();
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static String writeRole(char role) {
        if(role == 'A') return "Administrator";
        else return "Ordinary User";
    }

    public void setServiceFactory(ServiceFactory sf) {
	this.sf = sf;
    }

    public static String writeActivate(String activate) {
        String str = "";
        RoomServiceInterface rs = sf.getRoomServiceInterface();
        DeviceServiceInterface ds = sf.getDeviceServiceInterface();
        String[] singlePrefs = activate.split(",");
        try {
            Database db = sf.getDatabaseInterface().getDatabase();
            for(int i=0;i<singlePrefs.length;i++) {
                ActPreference ap = new ActPreference(singlePrefs[i]);
                if(ap.getDev() > 0) {
                    Room r = rs.getRoomById(db,ap.getRoom());
                    Device d = ds.getDeviceById(db,ap.getDev());
                    str += "\n\t"+r.name+": "+d.getDeviceName();
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

    public static String[] getSinglesActPreferences(String activate) {
        return activate.split(",");
    }

    public static Vector addArray(Vector old, String[] array) {
        for(int i=0;i<array.length;i++)
            old.add(array[i]);
        return old;
    }

    public static Vector getActPreferencesInRoom(String[] act, int room, Hashtable users) {
        Vector ap = new Vector();
        for(int i=0;i<act.length;i++) {
            ActPreference a = new ActPreference(act[i]);
            if(a.getRoom() == room && a.getDev() != 0)
                ap.add(a);
        }
        return ap;
    }

    public static Vector addVector(Vector old, Vector v) {
        for(int i=0;i<v.size();i++)
            old.add(v.elementAt(i));
        return old;
    }

    public static Vector getActPreferencesInRoom(String[] acts, int room) {
        Vector ap = new Vector();
        for(int i=0;i<acts.length;i++) {
            ActPreference a = new ActPreference(acts[i]);
            if(a.getRoom() == room && a.getDev() != 0)
                ap.add(a);
        }
        return ap;
    }

    public static Device findDeviceByName(Vector devs, String name) {
        int i = 0;
        Device d = null;
        while(i < devs.size() && d == null) {
            d = (Device)devs.elementAt(i);
            if(name.compareTo(d.getDeviceName()) != 0) {
                i++;
                d = null;
            }
        }
        return d;
    }
    public static Device findDeviceByName(ArrayList<Device> devs, String name) {
        int i = 0;
        Device d = null;
        while(i < devs.size() && d == null) {
            d = devs.get(i);
            if(name.compareTo(d.getDeviceName()) != 0) {
                i++;
                d = null;
            }
        }
        return d;
    }

    public static String[] getDevCmds(String param) {
        param = param.substring(1, param.length()-1);
    	return param.split(";");
    }

    public static Room findRoomByName(Vector rooms, String name) {
        int i = 0;
        Room r = null;
        while(i < rooms.size() && r == null) {
            r = (Room)rooms.elementAt(i);
            if(name.compareTo(r.name) != 0) {
                i++;
                r = null;
            }
        }
        return r;
    }

    public static boolean isEmpty(String fieldText) {
        boolean res = false;
        
        if(fieldText.equals(""))
            res = true;
        
        return res;
    }

    public static char getCharFromStringRole(String str) {
        char role;
        
        if(str.equals("Administrator"))
            role = 'A';
        else 
            role = 'O';
        
        return role;
    }

    public static String getOtherRole(char role) {
        String otherRole;
        
        if(role == 'O')
            otherRole = "Administrator";
        else
            otherRole ="Ordinary User";
        
        return otherRole;
    }

    public static String getOtherRoleChar(char role) {
        String otherRole;
        
        if(role == 'A')
            otherRole = "O";
        else
            otherRole = "A";
        
        return otherRole;
    }

    public static String getPrioFactor(int selectedIndex) {
        String res = "cons";
        
        if(selectedIndex == 0)
            res = "users";
        
        return "cons";
    }
    
}
