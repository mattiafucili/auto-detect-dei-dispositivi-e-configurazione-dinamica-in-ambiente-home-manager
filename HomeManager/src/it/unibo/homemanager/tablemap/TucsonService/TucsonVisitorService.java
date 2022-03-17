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
import it.unibo.homemanager.tablemap.Visitor;
import it.unibo.homemanager.tablemap.ServicesInterfaces.VisitorServiceInterface;

public class TucsonVisitorService implements VisitorServiceInterface {
	
	private String nome_template;
	
	 
    public TucsonVisitorService()
       {
    	 nome_template="visitor";
       }
    
    public  Visitor getVisitor(Database database,String fn,String sn)
    throws Exception{

    	Visitor v = null;


		/*creazione del template corretto da cercare*/
		Value nome= new Value(fn);
		TucsonDatabase db= (TucsonDatabase)database;
		Value ri= new Value(sn);
		LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,ri);
		LogicTuple lt=db.read(template);

		v=this.createVisitorByLogicTuple(lt);
		return v;
  }
  
  public  Visitor insertVisitor(Database database,String fn,String sn)
    throws Exception {

   Visitor v;

   v = new Visitor(0,fn,sn);
   TucsonDatabase db= (TucsonDatabase)database;
   boolean exist = false;

	/* controllo che la lampada non sia gi� presente (nome e cognome) */
	Value n=new Value(fn);
	Value ri= new Value(sn);
	LogicTuple template;
	try {
		template = new LogicTuple(nome_template,new Var("X"),n,ri);
		if(db.read(template)!=null) exist=true;

		if (exist) {
			throw new DuplicatedRecordDbException("Visitor: insert(): Tentativo di inserimento "+
					"di un visitatore gi� presente.");
		}

		/*inserimento del dispositivo*/
		Value newId=new Value(this.calcId(db)+1);
		if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}



		LogicTuple lt= new LogicTuple(nome_template,newId,n,ri);
		db.insert(lt);

	} catch (Exception e1) {

		e1.printStackTrace();
	}		

   
   return v;
  }
  
  /* consente di visualizzare tutti i Visitatori presenti */
  @SuppressWarnings({ "rawtypes", "unchecked" })
public  Vector getVisitors(Database database) 
    throws Exception {

	  Visitor v;
	  TucsonDatabase db= (TucsonDatabase)database;
		Vector vv= new Vector();    
		Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"));
		List lista=db.readCentre(template);

		if(lista.isEmpty()) return vv;

		for(int i=0;i<lista.size();i++)
		{
			v=this.createVisitorByString(lista.get(i).toString());
			vv.add(v);
		}

		return vv;
  }
  
  
  private  Visitor createVisitorByString (String s)

	{   Visitor v=null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==3)
		{
			int idV= Integer.parseInt(t.nextToken());
			String fn=Conversion.getDatabaseString((t.nextToken()));
			String sn=Conversion.getDatabaseString((t.nextToken()));
			v= new Visitor(idV,fn,sn);
		}
		return v;

	}

	private  Visitor createVisitorByLogicTuple (LogicTuple r) {
         Visitor v=null;
		try {int idV = r.getArg(0).intValue();
		     String fn =Conversion.getDatabaseString(r.getArg(1).toString());
		     String sn=Conversion.getDatabaseString(r.getArg(2).toString());
		     v= new Visitor(idV,fn,sn);
		} catch (Exception sqle) {}
		
		return v;
	}



	@SuppressWarnings("static-access")
	public void updateVisitor(Database database,Visitor v) 
			throws Exception {

		try{
			TucsonDatabase db= (TucsonDatabase)database;
			LogicTuple template= new LogicTuple().parse("visitor"+"("+v.idV+","+ new Var("Y")+","+new Var("Z")+")");

			if (db.read(template)==null)
			{
				throw new ResultSetDbException("Visitor: update(): Tentativo di aggiornare "+
						"un visitatore non esistente.");
			}

			db.delete(template);

			
			System.out.println("Da modificare con: "+ v.idV+ v.fn+ v.sn);
			
			LogicTuple lt= new LogicTuple().parse("visitor"+"("+v.idV+",'"+ v.fn+"','"+v.sn+"')");
			System.out.println("Vuoi inserire: "+lt);
			db.insert(lt);
		}catch(Exception e){e.printStackTrace();}
	}
	//  
	@SuppressWarnings("static-access")
	public void deleteVisitor(Database database,Visitor v) 
			throws Exception{


		try {
			TucsonDatabase db= (TucsonDatabase)database;
			if(v.fn==null){
				throw new ResultSetDbException("Visitor: delete(): Tentativo di eliminazione "+
						"di una visitatore non esistente.");
			}

			LogicTuple template= new LogicTuple().parse("visitor"+"("+v.idV+","+ new Var("Y")+","+new Var("Z")+")");
			db.delete(template);
		}catch (Exception e1) {

			e1.printStackTrace();
		}		

	}




	@SuppressWarnings("rawtypes")
	private int calcId(TucsonDatabase db)

	{   
		try { int id=0;
		Database database=db.getDatabase();
		Vector v= this.getVisitors(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Visitor)v.elementAt(i)).idV>id) id=((Visitor)v.elementAt(i)).idV;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

}
