package it.unibo.homemanager.tablemap.ServicesInterfaces;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Visitor;

import java.util.Vector;

public interface VisitorServiceInterface {
	
	   public  Visitor getVisitor(Database database,String fn,String sn)
			    throws Exception;
			  
			  public  Visitor insertVisitor(Database database,String fn,String sn)
			    throws Exception;
			  
			
			@SuppressWarnings("rawtypes")
			public  Vector getVisitors(Database database) 
			    throws Exception;
			  
			 



				
				public void updateVisitor(Database database,Visitor v) 
						throws Exception;
				  
			
				
				public void deleteVisitor(Database database,Visitor v) 
						throws Exception;




}
