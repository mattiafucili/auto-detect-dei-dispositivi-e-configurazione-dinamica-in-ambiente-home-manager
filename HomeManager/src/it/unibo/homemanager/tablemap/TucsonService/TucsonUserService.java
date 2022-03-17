package it.unibo.homemanager.tablemap.TucsonService;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.TucsonDatabase;
import it.unibo.homemanager.tablemap.Conversion;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;

public class TucsonUserService implements UserServiceInterface {
	
	

 public TucsonUserService(){}
 
	  @SuppressWarnings("static-access")
	public  User getUser(Database database, String username)
	    throws Exception{
	//
	    User user = null;
	    TucsonDatabase db= (TucsonDatabase)database;

		/*creazione del template corretto da cercare*/

	    LogicTuple arr= new LogicTuple().parse("user"+"(["+new Var("X")+","+ new Var("Y")+","+new Var("Z")+
				 ","+ username+","+new Var("A")+","+new Var("B")+","+new Var("C")+","+new Var("D")+","+new Var("E")+"])");

		LogicTuple lt=db.read(arr);
	    System.out.println("Letto="+lt);
		user=this.createUserByString(lt.toString());

	   return user;

	  }
	  
	  @SuppressWarnings("static-access")
	public User getUserById(Database database,int id)
	    throws Exception {

	    User user = null;
	    TucsonDatabase db= (TucsonDatabase)database;

	    LogicTuple template= new LogicTuple().parse("user"+"(["+id+","+ new Var("Y")+","+new Var("Z")+
				 ","+ new Var("X")+","+new Var("A")+","+new Var("B")+","+new Var("C")+","+new Var("D")+","+new Var("E")+"])");
		

		LogicTuple lt=db.read(template);

		user=this.createUserByString(lt.toString());

	    return user;

	  }
	  
	  @SuppressWarnings("static-access")
	public User insertUser(Database database,String fn,String sn,
	          String un,String p,String r,int th,int tac,String act)
	    throws Exception {

	  User u=null;

	   u = new User(0,fn,sn,un,p,r,th,tac,act);
	   TucsonDatabase db= (TucsonDatabase)database;
	   boolean exist = false;
		/* controllo che l'utente non sia giï¿½ presente (username) */
		try {
		
			LogicTuple arr= new LogicTuple().parse("user"+"(["+new Var("X")+","+ new Var("Y")+","+new Var("Z")+
					 ","+ un+","+new Var("A")+","+new Var("B")+","+new Var("C")+","+new Var("D")+","+new Var("E")+"])");
			if(db.read(arr)!=null) exist=true;

			if (exist) {
				throw new Exception("User: insert(): Tentativo di inserimento "+
					"di un utente già presente.");
				}
			
			/*inserimento del dispositivo*/
			int newId=(this.calcId(db)+1);
			String Sfirstname=fn;
			String Ssurname=sn;
			String Srole=r;
			if(!Conversion.islow(fn)) Sfirstname="'"+fn+"'";
			if(!Conversion.islow(sn)) Ssurname="'"+sn+"'";
			Srole="'"+r+"'";
			if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}
			 arr= new LogicTuple().parse("user"+"(["+newId+","+ Sfirstname+","+Ssurname+
					 ","+ un+","+p+","+Srole+","+th+","+tac+",'"+act+"'])");
			 System.out.println("Si vuole inserire "+ arr.toString());
			db.insert(arr);
			
		} catch (Exception e1) {

			e1.printStackTrace();
		}		

	   
	   
	  return u;

	  }
	  
	  /* consente di visualizzare tutti gli Utenti registrati */
	  @SuppressWarnings({ "rawtypes","unchecked", "static-access" })
	  public  Vector getUsers(Database database) 
			    throws Exception{

			    User u;
			    Vector users = new Vector();    
			  
			    TucsonDatabase db= (TucsonDatabase)database;
			    LogicTuple arr= new LogicTuple().parse("user"+"(["+new Var("X")+","+ new Var("Y")+","+new Var("Z")+
						 ","+ new Var("I")+","+new Var("A")+","+new Var("B")+","+new Var("C")+","+new Var("D")+","+new Var("E")+"])");
				TupleArgument v= Value.parse("user("+arr.getArg(0)+")");
			
				List l=db.readCentreArray(v);

				if(l.isEmpty()) return users;

				for(int i=0;i<l.size();i++)
				{
					u=createUserByString(l.get(i).toString());
					users.add(u);
				}


			    return users;
			  }
	  
	  
	  private User createUserByString (String s)

		{    
		   User u=null;
			String s1=s.substring(s.indexOf("[")+1,s.length()-2);
			StringTokenizer t= new StringTokenizer(s1,",");
			if(t.countTokens()==9)
			{
				int idUser= Integer.parseInt(t.nextToken());
				String firstname=Conversion.getDatabaseString(t.nextToken());
				String surname=Conversion.getDatabaseString(t.nextToken());
				String username=(t.nextToken()).trim();
				String pwd=(t.nextToken()).trim();
				String role=Conversion.getDatabaseString(t.nextToken());
				int temp_heat=Integer.parseInt(t.nextToken());
				int temp_ac=Integer.parseInt(t.nextToken());
				String activate= Conversion.getDatabaseString(t.nextToken());
                u= new User(idUser,firstname,surname,username,pwd,role,temp_heat,temp_ac,activate);
			}
           return u;

		}

		/*private  User  createUserByLogicTuple (LogicTuple r) {
           User u=null;
			try {int idUser = r.getArg(0).intValue();
			     String firstname =Conversion.getDatabaseString(r.getArg(1).toString());
			     String surname =Conversion.getDatabaseString(r.getArg(2).toString());
			     String username =(r.getArg(3).toString());
			     String pwd =(r.getArg(4).toString());
			     String role =Conversion.getDatabaseString(r.getArg(5).toString());
			    int temp_heat = r.getArg(6).intValue();
			    int temp_ac = r.getArg(7).intValue();
			    String activate=Conversion.getDatabaseString(r.getArg(8).toString());
			    u= new User(idUser,firstname,surname,username,pwd,role,temp_heat,temp_ac,activate);
			   } catch (Exception sqle) {}
			
			return u;
		}*/



	

		@SuppressWarnings("static-access")
		public void updateUser(Database database,User u) 
				throws Exception {

	     try{
			/*controllo sull'id*/ 
	    	 TucsonDatabase db= (TucsonDatabase)database;
	    	 LogicTuple template= new LogicTuple().parse("user"+"(["+u.idUser+","+ new Var("Y")+","+new Var("Z")+
					 ","+ new Var("X")+","+new Var("A")+","+new Var("B")+","+new Var("C")+","+new Var("D")+","+new Var("E")+"])");
			
			if (db.read(template)==null)
			{
				throw new Exception("User: update(): Tentativo di aggiornare "+
						"un utente non esistente.");
			}
			
			db.delete(template);
			String Sfirstname=u.firstname;
			String Ssurname=u.surname;
			String Srole=u.role;
			
			Srole="'"+u.role+"'";
			
			LogicTuple arr= new LogicTuple().parse("user"+"(["+u.idUser+","+ Sfirstname+","+Ssurname+
					 ","+ u.username+","+u.pwd+","+Srole+","+u.temp_heat+","+u.temp_ac+","+ u.activate+"])");
		    db.insert(arr);
	     }catch(Exception e){e.printStackTrace();}
		}
		
		//  
		@SuppressWarnings("static-access")
		public void deleteUser(Database database, User u) 
				throws Exception{
	 
			
			try {
				TucsonDatabase db= (TucsonDatabase)database;
				
				if(u.username==null){
					throw new Exception("User: delete(): Tentativo di eliminazione "+
							"di un utente non esistente.");
						}
				
				LogicTuple arr= new LogicTuple().parse("user"+"(["+u.idUser+","+ u.firstname+","+u.surname+
						 ","+u.username+","+u.pwd+","+u.role+","+u.temp_heat+","+u.temp_ac+","+u.activate+"])");
				db.delete(arr);
			}catch (Exception e1) {

				e1.printStackTrace();
			}		

		}
		
	  
	 
	  
	  @SuppressWarnings("rawtypes")
		private int calcId(TucsonDatabase db)

		{   
			try { int id=0;
			Database database=db.getDatabase();
			Vector v= this.getUsers(database);
			for(int i=0;i<v.size();i++)
			{
				if(((User)v.elementAt(i)).idUser>id) id=((User)v.elementAt(i)).idUser;
			}
			return id;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;

		}

}
