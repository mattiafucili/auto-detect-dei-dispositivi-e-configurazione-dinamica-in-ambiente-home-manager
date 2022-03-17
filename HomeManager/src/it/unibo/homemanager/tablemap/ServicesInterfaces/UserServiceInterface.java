package it.unibo.homemanager.tablemap.ServicesInterfaces;

import java.util.Vector;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.User;


public interface UserServiceInterface {
	

	public  User getUser(Database database, String username)
	    throws Exception;
	  

	public User getUserById(Database database,int id)
	    throws Exception;
	  
	
	public User insertUser(Database database,String fn,String sn,
	          String un,String p,String r,int th,int tac,String act)
	    throws Exception;
	  

	  
	  @SuppressWarnings("rawtypes")
	public  Vector getUsers(Database database) 
			    throws Exception;
	  
	

		
		public void updateUser(Database database,User u) 
				throws Exception;
		
	
		public void deleteUser(Database database, User u) 
				throws Exception;
		
	  
	 
	

}
