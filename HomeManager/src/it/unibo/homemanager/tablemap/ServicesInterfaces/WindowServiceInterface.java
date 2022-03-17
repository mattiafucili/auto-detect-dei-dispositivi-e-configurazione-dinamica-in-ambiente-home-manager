package it.unibo.homemanager.tablemap.ServicesInterfaces;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Window;
import java.util.Vector;

public interface WindowServiceInterface {

	public  Window getWindow(Database database, String name, int sId)
			throws Exception;

	public  Window insertWindow(Database database,String no,int si)
			throws Exception;


	@SuppressWarnings({ "rawtypes"})
	public  Vector getWindows(Database database)
			throws Exception;



	public void updateWindow(Database database,Window w) 
			throws Exception;

	public void deleteWindow(Database database,Window w) 
			throws Exception;

}
