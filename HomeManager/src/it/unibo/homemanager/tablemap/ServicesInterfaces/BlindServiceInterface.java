package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Blind;

public  interface BlindServiceInterface {
    
    public Blind getBlind(Database database, String name, int sId) throws Exception;
    public Blind insertBlind(Database database,String n,int si) throws Exception;
    public void updateBlind(Database database,Blind b) throws Exception;
    public void deleteBlind(Database database,Blind b) throws Exception;
    @SuppressWarnings({ "rawtypes" })
    public Vector getBlinds(Database database) throws Exception;
    
}