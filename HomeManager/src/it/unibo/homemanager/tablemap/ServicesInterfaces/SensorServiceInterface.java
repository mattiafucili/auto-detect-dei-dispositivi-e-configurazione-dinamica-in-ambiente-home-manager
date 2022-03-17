package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Sensor;


public interface SensorServiceInterface {
	
	
	 public  Sensor getSensor(Database database, String name, int roomId)
			    throws Exception;
			    
			    public  Sensor getSensorById(Database database,int id)
			    throws Exception;
			  
			  public  Sensor insertSensor(Database database,String n,int ri)
			    throws Exception;
			  
			  /* consente di visualizzare tutti i sensori presenti */
			  @SuppressWarnings({ "rawtypes"})
			public  Vector getSensors(Database database) 
			    throws Exception;

	

	public void updateSensor(Database database,Sensor s) 
			throws Exception ;

	public void deleteSensor(Database database,Sensor s) 
			throws Exception;
	

}
