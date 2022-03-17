package it.unibo.homemanager.tablemap.TucsonService;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.TucsonDatabase;
import it.unibo.homemanager.dbmanagement.dbexceptions.DuplicatedRecordDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.ResultSetDbException;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;

public class TucsonRoomService implements RoomServiceInterface {
	
	private String nome_template;
	public TucsonRoomService() 
	
	   {
		  nome_template="room";
	   }

	public  Room getRoom(Database database, String name, float mq, int wd)
			throws Exception {

		Room ro= null;
		TucsonDatabase db= (TucsonDatabase)database;

		/*creazione del template corretto da cercare*/
		Value nome= new Value(name);
		Value m= new Value(mq);
		Value w= new Value(wd);
		LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,m,w,new Var("A"),new Var("Y"));

		LogicTuple lt=db.read(template);

		ro=this.createRoomByLogicTuple(lt);
		return ro;
	}

	public  Room getRoomById(Database database,int id)
			throws Exception {

		Room ro = null;
		TucsonDatabase db= (TucsonDatabase)database;

		Value ri= new Value(id);
		LogicTuple template= new LogicTuple(nome_template,ri,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));

		LogicTuple lt=db.read(template);

		ro=this.createRoomByLogicTuple(lt);

		return ro;

	}

	public  Room getRoomByName(Database database, String name)
			throws Exception{

		Room r = null;
		TucsonDatabase db= (TucsonDatabase)database;
		Value ri= new Value(name);
		LogicTuple template= new LogicTuple("room",new Var("X"),ri,new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));
		LogicTuple lt=db.read(template);
		r=this.createRoomByLogicTuple(lt);

		return r;

	}

	public  Room insertRoom(Database database,int id,String n,float m,
			int w,int h,int a)
					throws Exception {

		Room r;

		r = new Room(0,n,m,w,h,a);
		TucsonDatabase db= (TucsonDatabase)database;
		
		boolean exist = false;
		/* controllo che la stanza non sia gi� presente (nome mq) */
		Value nu=new Value(n);
		Value mu= new Value(m);
		LogicTuple template;
		try {
			template = new LogicTuple(nome_template,new Var("X"),nu,mu,new Var("Y"),new Var("Z"),new Var("A"));
			if(db.read(template)!=null) exist=true;

			if (exist) {
				throw new DuplicatedRecordDbException("Room: insert(): Tentativo di inserimento "+
						"di una stanza gi� presente.");
			}

			/*inserimento del dispositivo*/
			Value newId=new Value(this.calcId(db)+1);
			if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}
			Value wu= new Value(w);
			Value hu= new Value((h));
			Value au= new Value((a));

			LogicTuple lt= new LogicTuple(nome_template,newId,nu,mu,wu,hu,au);
			db.insert(lt);

		} catch (Exception e1) {

			e1.printStackTrace();
		}		
		
		
		return r;

	}

	/* consente di visualizzare tutte le stanze presenti */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getRooms(Database database) 
			throws Exception {

		Room r;
		TucsonDatabase db= (TucsonDatabase)database;
		Vector Rooms = new Vector();    
		Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"),new Var("C"));
		List l = db.readCentre(template);

		if(l.isEmpty())
		    return Rooms;

		for(int i=0;i<l.size();i++)
		{
			r=this.createRoomByString(l.get(i).toString());
			Rooms.add(r);
		}

		return Rooms;
	}
	
	

	private Room createRoomByString(String s)

	{
		Room r= null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==6)
		{
			int idRoom= Integer.parseInt(t.nextToken());
			String name= t.nextToken();
			float mq= Float.parseFloat(t.nextToken());
			int wd= Integer.parseInt(t.nextToken());
			int heat=Integer.parseInt(t.nextToken());
			int ac=Integer.parseInt(t.nextToken());
            r=new Room(idRoom,name,mq,wd,heat,ac);

		}

    return r;
	}

	private Room createRoomByLogicTuple (LogicTuple r) {
        
		Room ro= null;
		try {int idRoom = r.getArg(0).intValue();
		    String name = r.getArg(1).toString();
		   float  mq = r.getArg(2).floatValue();
		   int wd =  r.getArg(3).intValue();
		    int heat = r.getArg(4).intValue();
		    int ac = r.getArg(5).intValue();
		    ro=new Room(idRoom,name,mq,wd,heat,ac);
		    } catch (Exception sqle) {}
    return ro;
	}



	public void updateRoom(Database database,Room r) 
			throws Exception {

		try{
			TucsonDatabase db= (TucsonDatabase)database;
			Value oi= new Value(r.idRoom); 
			LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));

			if (db.read(template)==null)
			{
				throw new ResultSetDbException("Room: update(): Tentativo di aggiornare "+
						"una stanza non esistente.");
			}

			db.delete(template);
			Value w= new Value(r.wd);
			Value h= new Value((r.heat));
			Value a= new Value((r.ac));
			Value n=new Value(r.name);
			Value m= new Value(r.mq);
			LogicTuple lt= new LogicTuple(nome_template,oi,n,m,w,h,a); 
			db.insert(lt);
		}catch(Exception e){e.printStackTrace();}
	}
	//  

	public void deleteRoom(Database database,Room r) 
			throws Exception{


		try {
			TucsonDatabase db= (TucsonDatabase)database;
			if(r.name==null){
				throw new ResultSetDbException("Room: delete(): Tentativo di eliminazione "+
						"di una stanza non esistente.");
			}

			Value newId=new Value(r.idRoom);
			Value w= new Value(r.wd);
			Value h= new Value((r.heat));
			Value a= new Value((r.ac));
			Value n=new Value(r.name);
			Value m= new Value(r.mq);
			LogicTuple template= new LogicTuple(nome_template,newId,n,m,w,h,a); 
			db.delete(template);
		}catch (Exception e1) {

			e1.printStackTrace();
		}		

	}


	@SuppressWarnings("rawtypes")
	private  int calcId(TucsonDatabase db)

	{   
		try { int id=0;
		Database database=db.getDatabase();
		Vector v=this.getRooms(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Room)v.elementAt(i)).idRoom>id) id=((Room)v.elementAt(i)).idRoom;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}


}
