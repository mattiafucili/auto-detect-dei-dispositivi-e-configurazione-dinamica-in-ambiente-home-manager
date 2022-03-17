package it.unibo.homemanager.tablemap.ServicesInterfaces;


import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.WasherProgram;

public interface WasherProgramServiceInterface {
	
	
	 public  WasherProgram getWasherProgram(Database database,String prog,int idDev)
			    throws Exception;
			  
			  public  WasherProgram insertWasherProgram(Database database,String p,
			          int du,int idDev)
			    throws Exception;
			  
			
			  @SuppressWarnings({ "rawtypes"})
			public  Vector getWasherProgramsForDevice(Database database,int idDev) 
			    throws Exception;
			  
			  
			  
			  @SuppressWarnings({ "rawtypes" })
			public  Vector getWasherPrograms(Database database) throws Exception;
			



				public void updateWasher(Database database,WasherProgram wp) 
						throws Exception;

				public void deleteWasher(Database database,WasherProgram wp) 
						throws Exception;





	


}
