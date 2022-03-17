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
import it.unibo.homemanager.tablemap.Conversion;
import it.unibo.homemanager.tablemap.Sensor;
import it.unibo.homemanager.tablemap.ServicesInterfaces.SensorServiceInterface;

public class TucsonSensorService implements SensorServiceInterface {

	private String nome_template;
	
	public TucsonSensorService ()
	   {
		   nome_template="sensor";
	   }
	
	
	
	 public  Sensor getSensor(Database database, String name, int roomId)
			    throws Exception {

			    	Sensor s = null;
			    	TucsonDatabase db= (TucsonDatabase)database;

					/*creazione del template corretto da cercare*/
					Value nome= new Value(name);
					Value ri= new Value(roomId);
					LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,ri);
					LogicTuple lt=db.read(template);

					s=this.createSensorByLogicTuple(lt);
					return s;


			  }
			    
			    public  Sensor getSensorById(Database database,int id)
			    throws Exception {

			    	Sensor s = null;
			    	TucsonDatabase db= (TucsonDatabase)database;

					Value ri= new Value(id);
					LogicTuple template= new LogicTuple(nome_template,ri,new Var("X"),new Var("Y"));

					LogicTuple lt=db.read(template);

					s=this.createSensorByLogicTuple(lt);

					return s;
			  }
			  
			  public  Sensor insertSensor(Database database,String n,int ri)
			    throws Exception{
               
			   boolean exist = false;
			   Sensor s;

			   s = new Sensor(0,n,ri);
			   TucsonDatabase db= (TucsonDatabase)database;
			  
			   /* controllo che il sensore non sia gi� presente (nome) */
				Value nu=new Value(n);
				LogicTuple template;
				try {
					template = new LogicTuple(nome_template,new Var("X"),nu,new Var("Y"));
					if(db.read(template)!=null) exist=true;

					if (exist) {
						throw new DuplicatedRecordDbException("Sensor: insert(): Tentativo di inserimento "+
								"di un sensore gi� presente.");
					}

					/*inserimento del dispositivo*/
					Value newId=new Value(this.calcId(db)+1);
					if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}


					Value riu=new Value(ri);
					LogicTuple lt= new LogicTuple(nome_template,newId,nu,riu);
					db.insert(lt);

				} catch (Exception e1) {

					e1.printStackTrace();
				}		

			   
			   
			   
			   
			   return s;
			  }
			  
			  /* consente di visualizzare tutti i sensori presenti */
			  @SuppressWarnings({ "rawtypes", "unchecked" })
			public  Vector getSensors(Database database) 
			    throws Exception {

				  Sensor s;
				  TucsonDatabase db= (TucsonDatabase)database;
					Vector ss = new Vector();    
					Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"));
					List lista=db.readCentre(template);

					if(lista.isEmpty()) return ss;

					for(int i=0;i<lista.size();i++)
					{
						s=this.createSensorByString(lista.get(i).toString());
						ss.add(s);
					}

					return ss;
			   
			  }

	

	public void updateSensor(Database database,Sensor s) 
			throws Exception {

		try{
			Value oi= new Value(s.idSens); 
			TucsonDatabase db= (TucsonDatabase)database;
			LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"));

			if (db.read(template)==null)
			{
				throw new ResultSetDbException("Sensor: update(): Tentativo di aggiornare "+
						"un dispositivo non esistente.");
			}

			db.delete(template);

			Value n=new Value(s.name);
			Value ri= new Value(s.roomId);
			LogicTuple lt= new LogicTuple(nome_template,oi,n,ri); 
			db.insert(lt);
		}catch(Exception e){e.printStackTrace();}
	}

	public void deleteSensor(Database database,Sensor s) 
			throws Exception{


		try {
			TucsonDatabase db= (TucsonDatabase)database;

			if(s.name==null){
				throw new ResultSetDbException("Sensor: delete(): Tentativo di eliminazione "+
						"di un sensore non esistente.");
			}

			Value newId=new Value(s.idSens);

			Value n=new Value(s.name);
			Value ri= new Value(s.roomId);
			LogicTuple template= new LogicTuple(nome_template,newId,n,ri); 
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
		Vector v= this.getSensors(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Sensor)v.elementAt(i)).idSens>id) id=((Sensor)v.elementAt(i)).idSens;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}
	
	private Sensor createSensorByString (String s)

	{
		 Sensor sens=null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==3)
		{
			int idSens= Integer.parseInt(t.nextToken());
			String name= Conversion.getDatabaseString(t.nextToken());
			int roomId=Integer.parseInt(t.nextToken());
			sens=new Sensor(idSens,name,roomId);
		}
		
		return sens;

	}

	private Sensor createSensorByLogicTuple(LogicTuple r) {
        
		 Sensor sens=null;
		try { int idSens = r.getArg(0).intValue();
		     String name = Conversion.getDatabaseString(r.getArg(1).toString());
		     int roomId = r.getArg(2).intValue();
		     sens=new Sensor(idSens,name,roomId);
		     } catch (Exception sqle) {}
		
		return sens;
		     
	}
}
