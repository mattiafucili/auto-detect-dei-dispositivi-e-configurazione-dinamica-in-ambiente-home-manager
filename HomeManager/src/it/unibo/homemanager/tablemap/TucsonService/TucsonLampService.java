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
import it.unibo.homemanager.tablemap.Lamp;
import it.unibo.homemanager.tablemap.ServicesInterfaces.LampServiceInterface;

public class TucsonLampService implements LampServiceInterface {
	
	
	private String nome_template;

	public TucsonLampService() {
		
		nome_template="lamp";
	}

	
	public  Lamp getLamp(Database database, String name, int sId)
			throws Exception {

		Lamp l = null;

		TucsonDatabase db= (TucsonDatabase)database;
		/*creazione del template corretto da cercare*/
		Value nome= new Value(name);
		Value ri= new Value(sId);
		LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,ri);
		LogicTuple lt=db.read(template);

		l=this.createLampByLogicTuple(lt);
		return l;


	}

	public  Lamp insertLamp(Database database,String n,int si)
			throws Exception {

		Lamp l;

		l = new Lamp(0,n,si);

		boolean exist = false;
		TucsonDatabase db= (TucsonDatabase)database;
		/* controllo che la lampada non sia gi� presente (nome e sensore) */
		Value nu=new Value(n);
		Value ri= new Value(si);
		LogicTuple template;
		try {
			template = new LogicTuple(nome_template,new Var("X"),nu,ri);
			if(db.read(template)!=null) exist=true;

			if (exist) {
				throw new DuplicatedRecordDbException("Lamp: insert(): Tentativo di inserimento "+
						"di una lampada gi� presente.");
			}

			/*inserimento del dispositivo*/
			Value newId=new Value(this.calcId(db)+1);
			if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}



			LogicTuple lt= new LogicTuple(nome_template,newId,nu,ri);
			db.insert(lt);

		} catch (Exception e1) {

			e1.printStackTrace();
		}		
   return l;
	}
	
	
	

	public void deleteLamp(Database database,Lamp l) 
			throws Exception{


		try {
			TucsonDatabase db= (TucsonDatabase)database;
			if(l.name==null){
				throw new ResultSetDbException("Lamp: delete(): Tentativo di eliminazione "+
						"di una lampada non esistente.");
			}

			Value newId=new Value(l.idL);

			Value n=new Value(l.name);
			Value ri= new Value(l.sensId);
			LogicTuple template= new LogicTuple(nome_template,newId,n,ri); 
			db.delete(template);
		}catch (Exception e1) {

			e1.printStackTrace();
		}		

	}


	/* consente di visualizzare tutte le lampade presenti */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  Vector getLamps(Database database) 
			throws Exception {

		Lamp l;
		TucsonDatabase db= (TucsonDatabase)database;
		Vector Lamps = new Vector();    
		Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"));
		List lista=db.readCentre(template);

		if(lista.isEmpty()) return Lamps;

		for(int i=0;i<lista.size();i++)
		{
			l=this.createLampByString(lista.get(i).toString());
			Lamps.add(l);
		}

		return Lamps;
	}

	@SuppressWarnings("rawtypes")
	private  int calcId(TucsonDatabase db)

	{   
		try { int id=0;
		Database database=db.getDatabase();
		Vector v= this.getLamps(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Lamp)v.elementAt(i)).idL>id) id=((Lamp)v.elementAt(i)).idL;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}
	
	
	/*costruttore che da una stringa completa letta dal centro di tuple 
	 * con read_all 
	 * ricavi i singoli parametri per creare una Lamp
	 */
	private Lamp  createLampByString(String s)

	{   Lamp l= null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==3)
		{
			int idL= Integer.parseInt(t.nextToken());
			String name= Conversion.getDatabaseString(t.nextToken());
			int sensId=Integer.parseInt(t.nextToken());
			l= new Lamp(idL,name,sensId);
		}
		return l;

	}

	public Lamp createLampByLogicTuple(LogicTuple r) {
		
		Lamp l=null;

		try {int idL = r.getArg(0).intValue();
		    String name = Conversion.getDatabaseString(r.getArg(1).toString());
		    int sensId = r.getArg(2).intValue();
		    l= new Lamp(idL,name,sensId);    
		} catch (Exception sqle) {}
		
		return l;
	}

}
