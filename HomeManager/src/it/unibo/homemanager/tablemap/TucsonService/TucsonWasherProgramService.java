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
import it.unibo.homemanager.tablemap.WasherProgram;
import it.unibo.homemanager.tablemap.ServicesInterfaces.WasherProgramServiceInterface;

public class TucsonWasherProgramService implements
		WasherProgramServiceInterface {
	
	private String nome_template;
	
	 public TucsonWasherProgramService() 
	   {
		  nome_template="washer_programs";
	   }
	    
	   
	    public  WasherProgram getWasherProgram(Database database,String prog,int idDev)
	    throws Exception {

	    	WasherProgram wp= null;
	    	TucsonDatabase db= (TucsonDatabase)database;

			/*creazione del template corretto da cercare*/
			Value p= new Value(prog);
			Value i= new Value(idDev);
			LogicTuple template= new LogicTuple(nome_template,new Var("X"),i,p,new Var("Y"));

			LogicTuple lt=db.read(template);

			wp=this.createWPByLogicTuple(lt);
			return wp;
	      

	  }
	  
	  public  WasherProgram insertWasherProgram(Database database,String p,
	          int du,int idDev)
	    throws Exception {

	   WasherProgram wp;

	   wp = new WasherProgram(0,idDev,p,du);
	   TucsonDatabase db= (TucsonDatabase)database;
	 
	   boolean exist = false;
		/* controllo che il programma non sia gi� presente (nome e dispositivo associato) */
		Value n=new Value(p);
		Value idr= new Value(idDev);
		LogicTuple template;
		try {
			template = new LogicTuple(nome_template,new Var("X"),idr,n,new Var("Z"));
			if(db.read(template)!=null) exist=true;

			if (exist) {
				throw new DuplicatedRecordDbException("Washer_program: insert(): Tentativo di inserimento "+
						"di un programma gi� presente.");
			}

			/*inserimento del dispositivo*/
			Value newId=new Value(this.calcId(db)+1);
			if((this.calcId(db)+1)==0){throw new Exception("Problema calcolo id");}
			Value w= new Value(idDev);
			Value h= new Value((p));
			Value a= new Value((du));

			LogicTuple lt= new LogicTuple(nome_template,newId,w,h,a);
			db.insert(lt);

		} catch (Exception e1) {

			e1.printStackTrace();
		}		
	   
	   return wp;
	  }
	  
	  /* consente di visualizzare tutti i programmi di un dispositivo */
	  @SuppressWarnings({ "rawtypes", "unchecked" })
	public  Vector getWasherProgramsForDevice(Database database,int idDev) 
	    throws Exception {

	    WasherProgram wp;
	    Vector programs = new Vector();    
	    Value idD= new Value(idDev);
	    TucsonDatabase db= (TucsonDatabase)database;
	    Value template= new Value("washer_programs",new Var("X"),idD,new Var("Z"),new Var("A"));
		List l=db.readCentre(template);

		if(l.isEmpty()) return programs;

		for(int i=0;i<l.size();i++)
		{
			wp=this.createWPByString(l.get(i).toString());
			programs.add(wp);
		}

		return programs;
	  }
	  
	  
	  
	  @SuppressWarnings({ "rawtypes", "unchecked" })
	public  Vector getWasherPrograms(Database database) throws Exception
	    {
		  WasherProgram wp;
		    Vector programs = new Vector();    
		    TucsonDatabase db= (TucsonDatabase)database;
		    Value template= new Value("washer_programs",new Var("X"),new Var("B"),new Var("Z"),new Var("A"));
			List l=db.readCentre(template);

			if(l.isEmpty()) return programs;

			for(int i=0;i<l.size();i++)
			{
				wp=this.createWPByString(l.get(i).toString());
				programs.add(wp);
			}

			return programs;
	    }
	  
	  private  WasherProgram createWPByString (String s)

		{ WasherProgram wp=null;
			String s1=s.substring(s.indexOf("(")+1,s.length()-1);
			StringTokenizer t= new StringTokenizer(s1,",");
			if(t.countTokens()==4)
			{
				int idW= Integer.parseInt(t.nextToken());
				int idDev= Integer.parseInt(t.nextToken());
				String prog=Conversion.getDatabaseString(t.nextToken());
				int duration=Integer.parseInt(t.nextToken());
               wp= new WasherProgram(idW, idDev, prog, duration);

			}
			return wp;


		}

		private   WasherProgram createWPByLogicTuple(LogicTuple r) {
           WasherProgram wp=null;
			try { int idW = r.getArg(0).intValue();
			     int idDev = r.getArg(1).intValue();
			     String prog =Conversion.getDatabaseString(r.getArg(2).toString());
			     int duration =  r.getArg(3).intValue();
			     wp= new WasherProgram(idW, idDev, prog, duration); 
			} catch (Exception sqle) {}
			return wp;
		}



		public void updateWasher(Database database,WasherProgram wp) 
				throws Exception {

			try{
				Value oi= new Value(wp.idW); 
				TucsonDatabase db= (TucsonDatabase)database;
				LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"),new Var("Z"));

				if (db.read(template)==null)
				{
					throw new ResultSetDbException("Washer_program: update(): Tentativo di aggiornare "+
							"un programma non esistente.");
				}

				db.delete(template);
				Value w= new Value(wp.idDev);
				Value h= new Value((wp.prog));
				Value a= new Value((wp.duration));
				
				LogicTuple lt= new LogicTuple(nome_template,oi,w,h,a); 
				db.insert(lt);
			}catch(Exception e){e.printStackTrace();}
		}
		//  

		public void deleteWasher(Database database,WasherProgram wp) 
				throws Exception{


			try {
				TucsonDatabase db= (TucsonDatabase)database;
				if(wp.prog==null){
					throw new ResultSetDbException("Washer_program: delete(): Tentativo di eliminazione "+
							"di un programma non esistente.");
				}

				Value newId=new Value(wp.idW);
				Value w= new Value(wp.idDev);
				Value h= new Value((wp.prog));
				Value a= new Value((wp.duration));
				LogicTuple template= new LogicTuple(nome_template,newId,w,h,a); 
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
			Vector v=this.getWasherPrograms(database);
			for(int i=0;i<v.size();i++)
			{
				if(((WasherProgram)v.elementAt(i)).idW>id) id=((WasherProgram)v.elementAt(i)).idW;
			}
			return id;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;

		}

	


}
