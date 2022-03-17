/*
 * ActPreference.java
 *
 * Created on 24 ottobre 2006, 16.40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import java.util.StringTokenizer;

/**
 *
 * @author admin
 */
public class ActPreference {
    
    private int room;
    private int dev;
    
    /** Creates a new instance of ActPreference */
    public ActPreference(String act) {
        act = act.replace("'", "");
        StringTokenizer st = new StringTokenizer(act,"-");
        System.out.println(act);
        room = Integer.parseInt(st.nextToken());
        String asd = st.nextToken();
        if(asd.equals(" ")){
            dev = 0;
        }
        else{
        dev = Integer.parseInt(asd);}
    }
    
    public int getRoom() {
        return room;
    }
    
    public int getDev() {
        return dev;
    } 
    
    public String toString() {
        return room+"-"+dev;
    }
}
