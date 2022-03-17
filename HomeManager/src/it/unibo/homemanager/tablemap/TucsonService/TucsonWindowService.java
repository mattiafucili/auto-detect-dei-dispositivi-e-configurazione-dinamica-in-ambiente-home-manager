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
import it.unibo.homemanager.tablemap.Window;
import it.unibo.homemanager.tablemap.ServicesInterfaces.WindowServiceInterface;

public class TucsonWindowService implements WindowServiceInterface {

	
	  private String nome_template;
    public TucsonWindowService()
       {
    	  nome_template="window";
       }

    public  Window getWindow(Database database, String name, int sId)
    throws Exception {

    	Window w = null;
    	TucsonDatabase db= (TucsonDatabase)database;

		/*creazione del template corretto da cercare*/
		Value nome= new Value(name);
		Value ri= new Value(sId);
		LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,ri);
		LogicTuple lt=db.read(template);

		w=this.createWindowByLogicTuple(lt);
		return w;

  }

  public  Window insertWindow(Database database,String no,int si)
    throws Exception{

   Window l;

   l = new Window(0,no,si);
   TucsonDatabase db= (TucsonDatabase)database;
   
   boolean exist = false;
	/* controllo che la finestra non sia gi� presente (nome e stanza) */
	Value n=new Value(no);
	Value ri= new Value(si);
	LogicTuple template;
	try {
		template = new LogicTuple(nome_template,new Var("X"),n,ri);
		if(db.read(template)!=null) exist=true;

		if (exist) {
			throw new DuplicatedRecordDbException("Window: insert(): Tentativo di inserimento "+
				"di una finestra gi� presente.");
			}
		
		/*inserimento del dispositivo*/
		Value newId=new Value(this.calcId(db)+1);
		if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}
		

		LogicTuple lt= new LogicTuple(nome_template,newId,n,ri);
		db.insert(lt);
		
	} catch (Exception e1) {

		e1.printStackTrace();
	}		
   return l;
  }

  /* consente di visualizzare tutte le lampade presenti */
  @SuppressWarnings({ "rawtypes", "unchecked" })
public  Vector getWindows(Database database)
    throws Exception {

    Window l;
    Vector windows = new Vector();
    TucsonDatabase db= (TucsonDatabase)database;
    Value template= new Value("window",new Var("X"),new Var("Y"),new Var("Z"));
	List lista=db.readCentre(template);

	if(lista.isEmpty()) return windows;

	for(int i=0;i<lista.size();i++)
	{
		l=this.createWindowByString(lista.get(i).toString());
		windows.add(l);
	}

	return windows;
  }
  
  
  private  Window createWindowByString(String s)

	{   Window w=null;
		String s1=s.substring(s.indexOf("(")+1,s.length()-1);
		StringTokenizer t= new StringTokenizer(s1,",");
		if(t.countTokens()==3)
		{
			int idL= Integer.parseInt(t.nextToken());
			String name=(t.nextToken());
			int roomId=Integer.parseInt(t.nextToken());
			 w= new Window(idL,name,roomId);
		}
		return w;

	}

	private Window createWindowByLogicTuple (LogicTuple r) {

		 Window w= null;
		try {int idL = r.getArg(0).intValue();
		     String name = (r.getArg(1).toString());
		     int roomId = r.getArg(2).intValue();
		     w= new Window(idL,name,roomId);
		     
		} catch (Exception sqle) {}
		return w;
	}

	 


	public void updateWindow(Database database,Window w) 
			throws Exception {

   try{
		Value oi= new Value(w.idL); 
		LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"));
		TucsonDatabase db= (TucsonDatabase)database;
		
		if (db.read(template)==null)
		{
			throw new ResultSetDbException("Window: update(): Tentativo di aggiornare "+
					"una finestra non esistente.");
		}
		
		db.delete(template);
		Value n=new Value(w.name);
		Value ri= new Value(w.roomId);
		LogicTuple lt= new LogicTuple(nome_template,oi,n,ri); 
		db.insert(lt);
   }catch(Exception e){e.printStackTrace();}
	}
	//  
	public void deleteWindow(Database database,Window w) 
			throws Exception{

		
		try {
			TucsonDatabase db= (TucsonDatabase)database;
			if(w.name==null){
				throw new ResultSetDbException("Window: delete(): Tentativo di eliminazione "+
						"di una finestra non esistente.");
					}
			
			Value newId=new Value(w.idL);
			Value n=new Value(w.name);
			Value ri= new Value(w.roomId);
			LogicTuple template= new LogicTuple(nome_template,newId,n,ri); 
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
		Vector v= this.getWindows(database);
		for(int i=0;i<v.size();i++)
		{
			if(((Window)v.elementAt(i)).idL>id) id=((Window)v.elementAt(i)).idL;
		}
		return id;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}
}
