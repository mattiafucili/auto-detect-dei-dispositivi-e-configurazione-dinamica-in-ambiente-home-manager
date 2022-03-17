package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Room;

public interface RoomServiceInterface {

	public  Room getRoom(Database database, String name, float mq, int wd)
				throws Exception;

		public  Room getRoomById(Database database,int id)
				throws Exception;
		
		public  Room getRoomByName(Database database, String name)
				throws Exception;

		public  Room insertRoom(Database database,int id,String n,float m,
				int w,int h,int a)
						throws Exception;
		
		
		
		@SuppressWarnings("rawtypes")
		public Vector getRooms(Database database) 
				throws Exception;
		
		



		public void updateRoom(Database database,Room r) 
				throws Exception;

		public void deleteRoom(Database database,Room r) 
				throws Exception;
	

}
