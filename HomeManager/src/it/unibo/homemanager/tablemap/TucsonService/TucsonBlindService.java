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
import it.unibo.homemanager.tablemap.Blind;
import it.unibo.homemanager.tablemap.ServicesInterfaces.BlindServiceInterface;

public class TucsonBlindService implements BlindServiceInterface {
	
	

	private String nome_template;

	public TucsonBlindService() {
		
		nome_template="blind";
	}

	@Override
	public Blind getBlind(Database database, String name, int sId)
			throws Exception {
		
		Blind l = null;

        TucsonDatabase db= (TucsonDatabase)database;
		/*creazione del template corretto da cercare*/
		Value nome= new Value(name);
		Value ri= new Value(sId);
		LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,ri);
		LogicTuple lt=db.read(template);

		l=this.createBlindByLogicTuple(lt);
		return l;
	}

	@Override
	public Blind insertBlind(Database database, String n, int si)
			throws Exception {
		
		Blind l;
        l= new Blind(0,n,si);
		boolean exist = false;
		TucsonDatabase db= (TucsonDatabase)database;
       
		/* controllo che la Blind non sia gi� presente (nome e roomId) */
		Value nu=new Value(n);
		Value ri= new Value(si);
		LogicTuple template;
		try {
			template = new LogicTuple(nome_template,new Var("X"),nu,ri);
			if(db.read(template)!=null) exist=true;

			if (exist) {
				throw new DuplicatedRecordDbException("Blind: insert(): Tentativo di inserimento "+
						"di una Blind gi� presente.");
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

	
	

	public void updateBlind(Database database,Blind b) 
			throws Exception {

		try{
			TucsonDatabase db= (TucsonDatabase)database;
			Value oi= new Value(b.idL); 
			LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"));

			if (db.read(template)==null)
			{
				throw new ResultSetDbException("Blind: update(): Tentativo di aggiornare "+
						"un blind non esistente.");
			}

			db.delete(template);

			Value n=new Value(b.name);
			Value ri= new Value(b.roomId);
			LogicTuple lt= new LogicTuple(nome_template,oi,n,ri); 
			db.insert(lt);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	
	//  
	public void deleteBlind(Database database,Blind b) 
			throws Exception{


		try {
			TucsonDatabase db= (TucsonDatabase)database;
			if(b.name==null){
				throw new ResultSetDbException("Blind: delete(): Tentativo di eliminazione "+
						"di una Blind non esistente.");
			}

			Value newId=new Value(b.idL);

			Value n=new Value(b.name);
			Value ri= new Value(b.roomId);
			LogicTuple template= new LogicTuple(nome_template,newId,n,ri); 
			db.delete(template);
		}catch (Exception e1) {

			e1.printStackTrace();
		}		

	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector getBlinds(Database database) throws Exception {
		TucsonDatabase db= (TucsonDatabase)database;
		Blind l;
		Vector Blinds = new Vector();    
		Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"));
		List lista=db.readCentre(template);

		if(lista.isEmpty())
		    return Blinds;

		for(int i=0;i<lista.size();i++)
		{
			l=this.createBlindByString(lista.get(i).toString());
			Blinds.add(l);
		}

		return Blinds;
	}
	
	
    private Blind createBlindByString (String s)

	{   
    	Blind b=null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==3)
		{
			int idL= Integer.parseInt(t.nextToken());
			String name= (t.nextToken());
			int roomId=Integer.parseInt(t.nextToken());
			b= new Blind(idL,name,roomId);
		}
		return b;

	}

	private Blind createBlindByLogicTuple(LogicTuple r) {
        
		Blind b= null;
		try { int idL = r.getArg(0).intValue();
		     String name = (r.getArg(1).toString());
		     int roomId = r.getArg(2).intValue();
		     b= new Blind(idL,name,roomId);
		     } 
		catch (Exception sqle) {}
		
		return b;
	}
	
	
	@SuppressWarnings("rawtypes")
	private  int calcId(TucsonDatabase db)

	{   
		try { int id=0;
		Database database=db.getDatabase();
		Vector v= this.getBlinds(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Blind)v.elementAt(i)).idL>id) id=((Blind)v.elementAt(i)).idL;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

}
