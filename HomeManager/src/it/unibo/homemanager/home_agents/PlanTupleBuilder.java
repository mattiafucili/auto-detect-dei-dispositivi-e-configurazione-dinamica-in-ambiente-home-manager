/*
 * PlanTupleBuilder.java
 *
 * Created on 23 novembre 2006, 22.50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents;

import it.unibo.homemanager.tablemap.User;

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.util.*;

/**
 *
 * @author admin
 */
public class PlanTupleBuilder extends LogicTuple {
    
    /**
     * Creates a new instance of PlanTupleBuilder
     */
    public PlanTupleBuilder() {}
    
    public static LogicTuple getCmdTuple(LogicTuple t) {
        LogicTuple lt = null;
        try {
            lt = new LogicTuple("usr_cmd",t.getArg(0),t.getArg(1),
                                t.getArg(2),t.getArg(3));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lt;
    }
    
    public static LogicTuple getPlanCmdTuple(Command c) {
        return new LogicTuple("plan_cmd",new Value(c.getIdDev()),
                        new Value(c.getState()));
    }
    
    public static LogicTuple getPlanCmdsTuple(Command c,TupleArgument room) {
        return new LogicTuple("plan_cmds",room,new Value(c.toString()));
    }
    
    public static LogicTuple getPrefTuple(User u) {
        return new LogicTuple("user_pref",new Value(u.idUser),
                        new Value(u.temp_heat),new Value(u.temp_ac),
                        new Value(u.activate));
    }
    
    public static int getTempArg(String tMode,LogicTuple pref) {
        if(tMode.equals("ac")) return getAcTemperature(pref);
        else return getHeatTemperature(pref);
    }
    
    private static int getHeatTemperature(LogicTuple pref) {
        int h = 0;
        try {
            h = pref.getArg(1).intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return h;
    }
    
    private static int getAcTemperature(LogicTuple pref) {
        int ac = 0;
        try {
            ac = pref.getArg(2).intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ac;
    }
    
    public static String getActivateString(LogicTuple pref) {
        String s = "";
        try {
            s = pref.getArg(3).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    public static int getIdUser(LogicTuple pref) {
        int id = 0;
        try {
            id = pref.getArg(0).intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }
}
