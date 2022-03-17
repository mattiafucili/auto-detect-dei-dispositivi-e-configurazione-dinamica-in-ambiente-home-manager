package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Lamp;

public interface LampServiceInterface {
	
	
	public  Lamp getLamp(Database database, String name, int sId)
			throws Exception;

	public  Lamp insertLamp(Database database,String n,int si)
			throws Exception;
	
	public  void deleteLamp(Database database,Lamp l)
			throws Exception;

	/* consente di visualizzare tutte le lampade presenti */
	@SuppressWarnings({ "rawtypes"})
	public  Vector getLamps(Database database) 
			throws Exception;


}
