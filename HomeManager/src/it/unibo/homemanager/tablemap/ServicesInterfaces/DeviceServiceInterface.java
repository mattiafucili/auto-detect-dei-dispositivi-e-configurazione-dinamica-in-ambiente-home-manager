package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.ArrayList;


import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;

public interface DeviceServiceInterface {
		
	public Device getDevice(Database database, String name, int roomId)throws Exception;

	public Device getDeviceById(Database database, int id) throws Exception;

	public Device insertDevice(Database database,String n,
			float e,String p,String t,int ri)
					throws Exception;

	public Device deleteDevice(Database database,int id)
			throws Exception;

	public Device updateDevice(Database database,Device newDevice)throws Exception;
	
	public ArrayList<Device> getDevices(Database database) 
			throws Exception;

	public ArrayList<Device> getDevicesInRoom(Database database,int room) 
			throws Exception;
	  
	public int getDeviceId(Database database, String cerca) throws Exception;

}
