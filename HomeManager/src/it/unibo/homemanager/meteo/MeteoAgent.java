package it.unibo.homemanager.meteo;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.agents.TucsonAgentInterface;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.TucsonDatabase;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;
import it.unibo.homemanager.userinterfaces.ViewMeteoAgentPanel;

public class MeteoAgent extends AbstractTucsonAgent implements TucsonAgentInterface {

	 	private TucsonTupleCentreId myTid;
	    private TracePanel tp;
	    private EnhancedSynchACC acc;
	    private ViewMeteoAgentPanel panel;
	    private Database db;
	    private List<String> siti=new ArrayList<String>();
	    private List<String> url=new ArrayList<String>();
	    private List<String> path=new ArrayList<String>();
	    
	    /**
	     * Creates a new instance of MeteoAgent
	     * @param database 
	     */
	    public MeteoAgent(String id,TracePanel tp, TucsonTupleCentreId tuple, Database database) throws TucsonInvalidAgentIdException {
	        super(id);
	        this.tp=tp;
	        this.myTid=tuple;
	        this.db=database;
	    }
	    
	    public void setPanel(ViewManageAgentPanel maPanel)
	    {
	    	panel=new ViewMeteoAgentPanel(maPanel, this, tp);
	    }
	    
	    @Override
	    public void operationCompleted(ITucsonOperation ito) {
	        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }
	    
	    @Override
	    public void operationCompleted(AbstractTupleCentreOperation atco) {
	        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    public List<String> getSiti()  //DA MODIFICARE
	    {
	    	/*List<String> siti=new ArrayList<String>();
	    	siti.add("Google");
	        siti.add("Yahoo");
	        siti.add("OpenWeatherMap");*/
	        return siti;
	    }
	    
	    public List<String> getIcon()
	    {
	    	return path;
	    }
	    
	    private void inizializzaCampi() {
			try{
				LogicTuple t=new LogicTuple("meteo_site",new Var(),new Var(),new Var());
				ITucsonOperation op_rdAll=this.acc.rdAll(((TucsonDatabase)db).getTucsonTupleCentreId(),t,Long.MAX_VALUE);
				if(op_rdAll.isResultSuccess())
				{
					List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
					for (LogicTuple logicTuple : l) {
						siti.add(logicTuple.getArg(0).toString().substring(1, logicTuple.getArg(0).toString().length()-1));
						url.add(logicTuple.getArg(1).toString().substring(1, logicTuple.getArg(1).toString().length()-1));
						path.add(logicTuple.getArg(2).toString().substring(1, logicTuple.getArg(2).toString().length()-1));
					}
				}
			}
	    catch(TucsonOperationNotPossibleException ex){
            ex.printStackTrace();
            tp.appendText("MeteoAgent : error in activity.");
        }
        catch(UnreachableNodeException ex){
            ex.printStackTrace();
            tp.appendText("MeteoAgent : error in activity.");
        }
        catch(OperationTimeOutException ex){
            ex.printStackTrace();
            tp.appendText("MeteoAgent : error in activity.");
        }
        catch(Exception ex)
        {
      	  ex.printStackTrace();
            tp.appendText("MeteoAgent : error in activity.");
        }
		}
	    
		@Override
		protected void main() {
			tp.appendText("-- METEO AGENT STARTED.");
	        this.acc = this.getContext();
	        LogicTuple t1a,t1b,t1c,t2a,t2b,t2c,t3a,t3b,t3c,tdate;
	        Meteo m1,m2,m3;
	        inizializzaCampi();
	        try{
	            while(true) {
	            	
	            	ParserMeteo.init();		//inizializza la posizione per i parser
	            	//Thread.sleep(5000);
	            	m1=ParserMeteo.previsioniM(url.get(0));	//weathermap
	            	m2=ParserMeteo.previsioniY(url.get(2));	//yahoo
	            	m3=ParserMeteo.previsioniG(url.get(1));	//google
	            	
	            	
	            	//INSERISCO LE TUPLE DI ESEMPIO PER LA REAZIONE------>una volta impostata la reazione sono da eliminare queste righe
	            	LogicTuple test1=new LogicTuple("shutters_action",new Value("up"),new Value(6),new Value(44));
	            	LogicTuple test2=new LogicTuple("shutters_action",new Value("down"),new Value(7),new Value(8));
	            	this.acc.out(this.myTid, test1, null);
	            	this.acc.out(this.myTid, test2, null);
	            	//
	            	
	            	
	            	
	            	//creo tutte le tuple---> 3 tuple per ogni sito meteo
	            	t1a=new LogicTuple("meteo",new Value(m1.getNomeSito()),new Value(m1.getCitta()),new Value(m1.getOraAlba()),new Value(m1.getMinutoAlba()),new Value(m1.getOraTramonto()),new Value(m1.getMinutoTramonto()),new Value(m1.getTemperatura()));
	            	t1b=new LogicTuple("meteo",new Value(m1.getNomeSito()) ,new Value(m1.getTemperaturaMax()),new Value(m1.getTemperaturaMin()),new Value(m1.getUmidita()),new Value(m1.getPressione()),new Value(m1.getVento()));
	            	t1c=new LogicTuple("meteo",new Value(m1.getNomeSito()) ,new Value(m1.getPrecipitazioni()), new Value(m1.getMeteoCode()),new Value(m1.getMeteo()));
	            	
	            	
	            	t2a=new LogicTuple("meteo",new Value(m2.getNomeSito()),new Value(m2.getCitta()),new Value(m2.getOraAlba()),new Value(m2.getMinutoAlba()),new Value(m2.getOraTramonto()),new Value(m2.getMinutoTramonto()),new Value(m2.getTemperatura()));
	            	t2b=new LogicTuple("meteo",new Value(m2.getNomeSito()) ,new Value(m2.getTemperaturaMax()),new Value(m2.getTemperaturaMin()),new Value(m2.getUmidita()),new Value(m2.getPressione()),new Value(m2.getVento()));
	            	t2c=new LogicTuple("meteo",new Value(m2.getNomeSito()),new Value(m2.getPrecipitazioni()), new Value(m2.getMeteoCode()),new Value(m2.getMeteo()));
	            	
	            	
	            	t3a=new LogicTuple("meteo",new Value(m3.getNomeSito()),new Value(m3.getCitta()),new Value(m3.getOraAlba()),new Value(m3.getMinutoAlba()),new Value(m3.getOraTramonto()),new Value(m3.getMinutoTramonto()),new Value(m3.getTemperatura()));
	            	t3b=new LogicTuple("meteo",new Value(m3.getNomeSito()) ,new Value(m3.getTemperaturaMax()),new Value(m3.getTemperaturaMin()),new Value(m3.getUmidita()),new Value(m3.getPressione()),new Value(m3.getVento()));
	            	t3c=new LogicTuple("meteo",new Value(m3.getNomeSito()),new Value(m3.getPrecipitazioni()), new Value(m3.getMeteoCode()),new Value(m3.getMeteo()));
	            	
	            	Date data=new Date();
	            	Timestamp t=new Timestamp(data.getTime());
	            	tdate=new LogicTuple("meteo",new Value(t.toString()));
	            	
	            	//blocco vers1.0 di eliminazione tuple vecchie
	            	/*{
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE); 	//perche MAX_VALUE?
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE); 	//perche MAX_VALUE?
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE); 	//perche MAX_VALUE?
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	template=new LogicTuple("meteo", new Var(), new Var(), new Var());
	            	acc.inp(myTid, template, Long.MAX_VALUE);
	            	}*/
	            	
	            	
	            	
	            	//inserisco le tuple nuove
	            	acc.out(this.myTid,t1a, null); 
	            	acc.out(this.myTid,t1b, null);
	            	acc.out(this.myTid,t1c, null);
	            	
	            	acc.out(this.myTid,t2a, null); 
	            	acc.out(this.myTid,t2b, null);
	            	acc.out(this.myTid,t2c, null);
	            	
	            	acc.out(this.myTid,t3a, null); 
	            	acc.out(this.myTid,t3b, null);
	            	acc.out(this.myTid,t3c, null);
	            	
	            	acc.out(this.myTid, tdate, null);
	            	
	            	
	            	tp.appendText("-- METEO AGENT UPDATED.");
	            	Thread.sleep(900000);  //imposto che le previsioni si aggiornano ogni 15 minuti(=900000 millisec)          [ 8 ore(=28800000 millisec)]
	            	
	            	//elimino le tuple precedenti nel centro di tuple per poi salvare quelle piu aggiornate
	            	//vers2.0 per� devo metterla dopo l aggiunta perch� senno la inAll � bloccante fino a quando non trova tuple
	            	
	            	reset();
	            	
	            }
	            //acc.exit();
	          }catch(TucsonOperationNotPossibleException ex){
	              ex.printStackTrace();
	              tp.appendText("MeteoAgent : error in activity.");
	          }
	          catch(UnreachableNodeException ex){
	              ex.printStackTrace();
	              tp.appendText("MeteoAgent : error in activity.");
	          }
	          catch(OperationTimeOutException ex){
	              ex.printStackTrace();
	              tp.appendText("MeteoAgent : error in activity.");
	          }
	          catch(Exception ex)
	          {
	        	  ex.printStackTrace();
	              tp.appendText("MeteoAgent : error in activity.");
	          }
		}
		
		

		private void reset() throws OperationTimeOutException,UnreachableNodeException,TucsonOperationNotPossibleException
		{
			LogicTuple template=new LogicTuple("meteo", new Var(), new Var(),
					new Var(), new Var(), new Var(), new Var(), new Var());
        	acc.inAll(myTid, template, Long.MAX_VALUE);
        	template=new LogicTuple("meteo", new Var(), new Var(), new Var(),
        			new Var(), new Var(), new Var());
        	acc.inAll(myTid, template, Long.MAX_VALUE);
        	template=new LogicTuple("meteo",new Var(), new Var(), new Var(), new Var());
        	acc.inAll(myTid, template, Long.MAX_VALUE);
        	template=new LogicTuple("meteo",new Var());
        	acc.inAll(myTid, template, Long.MAX_VALUE);
		}

		@Override
		public void show() {
			panel.init();
		}

		@Override
		public void hide() {
			panel.hidePanel();
		}

		@Override
		public JPanel getInterface() {
			return panel;
		}

		@Override
		public String getName() {
			return "Weather Agent";
		}

public String[] getColumnNames(String site) {
			
			switch (site) {
			case "OpenWeatherMap":
				String[] uscita1={"Site",
                        "City",
                        "Hour sunrise",
                        
                        "Hour sunset",
                       
                        "T avg \u00b0C",
                        "T max \u00b0C",
                        "T min \u00b0C",
                        "Humidity %",
                        "Pressure mb",
                        "Wind km/h",
                        "Rainfall %",
                        
                        "Forecast",
                        };
				return uscita1;
				
			case "Yahoo":
				String[] uscita2={"Site",
                        "City",
                        "Hour sunrise",
                        
                        "Hour sunset",
                       
                        "T avg \u00b0C",
                        "T max \u00b0C",
                        "T min \u00b0C",
                        "Humidity %",
                        "Pressure mb",
                        "Wind km/h",
                        //"Rainfall %",
                        
                        "Forecast",
                        };
				return uscita2;
				
			case "Google":
				String[] uscita3={"Site",
                        "City",
                        //"Ora alba",
                        //"Minut alba",
                        //"Ora tramonto",
                       //"Minut tramonto",
                        "T avg \u00b0C",
                        //"Temp max",
                        //"Temp min",
                        "Humidity %",
                        "Pressure mb",
                        "Wind km/h",
                        "Rainfall %",
                        //"Code meteo",
                        "Forecast",
                        };
				return uscita3;
				
			default:
				return null;
			}
			
		}
		//GETDATA TOTALE PER AVERE TUTTO IN UNA SOLA MATRICE. not used
		public Object[][] getData(String site)  {
			Object[][] data=null;
			switch (site) {
			case "OpenWeatherMap":
				try{
				LogicTuple t1=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
				LogicTuple t2=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var());
				LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
				ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
				ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
				ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
				
				if(op1.isResultSuccess() && op2.isResultSuccess()&& op3.isResultSuccess())
				{
					data= new Object[1][15];
					data[0][0]=t1.getArg(0); //sito
					data[0][1]=t1.getArg(1); //citta
					data[0][2]=t1.getArg(2); //ora alba
					data[0][3]=t1.getArg(3); //min alba
					data[0][4]=t1.getArg(4); //ora tram
					data[0][5]=t1.getArg(5); //min tram
					data[0][6]=t1.getArg(6); //temp
					
					data[0][7]=t2.getArg(1); //temp max
					data[0][8]=t2.getArg(2); //temp min
					data[0][9]=t2.getArg(3); //umidita
					data[0][10]=t2.getArg(4); //pressione
					data[0][11]=t2.getArg(5); //vento
					
					data[0][12]=t3.getArg(1); //precip
					data[0][13]=t3.getArg(2); //code meteo
					data[0][14]=t3.getArg(3); //meteo
				}
				else
				{
					tp.appendText("MeteoAgent : error in activity.");
				}
				}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
			
				return data;
				
			case "Yahoo":
				
				try{
					LogicTuple t1=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t2=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t3=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var());
					ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
					ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
					ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
					
					if(op1.isResultSuccess() && op2.isResultSuccess()&& op3.isResultSuccess())
					{
						data= new Object[1][14];
						data[0][0]=t1.getArg(0); //sito
						data[0][1]=t1.getArg(1); //citta
						data[0][2]=t1.getArg(2); //ora alba
						data[0][3]=t1.getArg(3); //min alba
						data[0][4]=t1.getArg(4); //ora tram
						data[0][5]=t1.getArg(5); //min tram
						data[0][6]=t1.getArg(6); //temp
						
						data[0][7]=t2.getArg(1); //temp max
						data[0][8]=t2.getArg(2); //temp min
						data[0][9]=t2.getArg(3); //umidita
						data[0][10]=t2.getArg(4); //pressione
						data[0][11]=t2.getArg(5); //vento
						
						//data[0][12]=t3.getArg(1); //precip
						data[0][12]=t3.getArg(2); //code meteo
						data[0][13]=t3.getArg(3); //meteo
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			case "Google":
				
				try{
					LogicTuple t1=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t2=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t3=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var());
					ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
					ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
					ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
					
					if(op1.isResultSuccess() && op2.isResultSuccess()&& op3.isResultSuccess())
					{
						data= new Object[1][8];
						data[0][0]=t1.getArg(0); //sito
						data[0][1]=t1.getArg(1); //citta
						//data[0][2]=t1.getArg(2); //ora alba
						//data[0][3]=t1.getArg(3); //min alba
						//data[0][4]=t1.getArg(4); //ora tram
						//data[0][5]=t1.getArg(5); //min tram
						data[0][2]=t1.getArg(6); //temp
						
						//data[0][7]=t2.getArg(1); //temp max
						//data[0][8]=t2.getArg(2); //temp min
						data[0][3]=t2.getArg(3); //umidita
						data[0][4]=t2.getArg(4); //pressione
						data[0][5]=t2.getArg(5); //vento
						
						data[0][6]=t3.getArg(1); //precip
						//data[0][13]=t3.getArg(2); //code meteo
						data[0][7]=t3.getArg(3); //meteo
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			default:
				return null;
			}
		}

		public Object[][] getData1(String site) { // primi 6
			Object[][] data=null;
			switch (site) {
			case "OpenWeatherMap":
				try{
				LogicTuple t1=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
				//LogicTuple t2=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var());
				//LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
				ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
				
				
				if(op1.isResultSuccess())
				{
					data= new Object[1][4];
					data[0][0]=t1.getArg(0); //sito
					data[0][1]=t1.getArg(1); //citta
					if((""+t1.getArg(3)).length()==1)
						data[0][2]=""+t1.getArg(2)+":0"+t1.getArg(3); //ora alba
					else
						data[0][2]=""+t1.getArg(2)+":"+t1.getArg(3);
					if((""+t1.getArg(5)).length()==1)
						data[0][3]=""+t1.getArg(4)+":0"+t1.getArg(5); //ora tram
					else
						data[0][3]=""+t1.getArg(4)+":"+t1.getArg(5);
				}
				else
				{
					tp.appendText("MeteoAgent : error in activity.");
				}
				}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
			
				return data;
				
			case "Yahoo":
				
				try{
					LogicTuple t1=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t2=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
					ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
					
					
					if(op1.isResultSuccess())
					{
						data= new Object[1][4];
						data[0][0]=t1.getArg(0); //sito
						data[0][1]=t1.getArg(1); //citta
						if((""+t1.getArg(3)).length()==1)
							data[0][2]=""+t1.getArg(2)+":0"+t1.getArg(3); //ora alba
						else
							data[0][2]=""+t1.getArg(2)+":"+t1.getArg(3);
						if((""+t1.getArg(5)).length()==1)
							data[0][3]=""+t1.getArg(4)+":0"+t1.getArg(5); //ora tram
						else
							data[0][3]=""+t1.getArg(4)+":"+t1.getArg(5);
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			case "Google":
				
				try{
					LogicTuple t1=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t2=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t3=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var());
					ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
					ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
					
					
					if(op1.isResultSuccess() && op2.isResultSuccess())
					{
						data= new Object[1][6];
						data[0][0]=t1.getArg(0); //sito
						data[0][1]=t1.getArg(1); //citta
						//data[0][2]=t1.getArg(2); //ora alba
						//data[0][3]=t1.getArg(3); //min alba
						//data[0][4]=t1.getArg(4); //ora tram
						//data[0][5]=t1.getArg(5); //min tram
						data[0][2]=t1.getArg(6); //temp
						
						//data[0][7]=t2.getArg(1); //temp max
						//data[0][8]=t2.getArg(2); //temp min
						data[0][3]=t2.getArg(3); //umidita
						data[0][4]=t2.getArg(4); //pressione
						data[0][5]=t2.getArg(5); //vento
						
						
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			default:
				return null;
			}
		}

		public Object[][] getData2(String site) { //secondi 6
			Object[][] data=null;
			switch (site) {
			case "OpenWeatherMap":
				try{
				LogicTuple t1=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
				LogicTuple t2=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var());
				//LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
				ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
				ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
				
				
				if(op1.isResultSuccess() && op2.isResultSuccess())
				{
					data= new Object[1][6];
					
					double d=Double.parseDouble(""+t1.getArg(6));
					DecimalFormat decForm = new DecimalFormat("#.##", new DecimalFormatSymbols());
				    decForm.setRoundingMode(RoundingMode.CEILING); // solo da JAVA 6 in poi
				     
				    
					data[0][0]=decForm.format(d); //temp
					
					d=Double.parseDouble(""+t2.getArg(1));
					data[0][1]=decForm.format(d); //temp max
					d=Double.parseDouble(""+t2.getArg(2));
					data[0][2]=decForm.format(d); //temp min
					data[0][3]=t2.getArg(3); //umidita
					data[0][4]=t2.getArg(4); //pressione
					d=Double.parseDouble(""+t2.getArg(5));
					data[0][5]=decForm.format(d); //vento
					
					
				}
				else
				{
					tp.appendText("MeteoAgent : error in activity.");
				}
				}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
			
				return data;
				
			case "Yahoo":
				
				try{
					LogicTuple t1=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t2=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
					ITucsonOperation op1=acc.rdp(myTid, t1, Long.MAX_VALUE); 	
					ITucsonOperation op2=acc.rdp(myTid, t2, Long.MAX_VALUE);
					
					
					if(op1.isResultSuccess() && op2.isResultSuccess())
					{
						data= new Object[1][6];
						
						data[0][0]=t1.getArg(6); //temp
						
						data[0][1]=t2.getArg(1); //temp max
						data[0][2]=t2.getArg(2); //temp min
						data[0][3]=t2.getArg(3); //umidita
						data[0][4]=t2.getArg(4); //pressione
						data[0][5]=t2.getArg(5); //vento
						
						
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			case "Google":
				
				try{
					//LogicTuple t1=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t2=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t3=new LogicTuple("meteo", new Value("google"), new Var(), new Var(), new Var());
					
					ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
					
					if(op3.isResultSuccess())
					{
						data= new Object[1][2];
						
						
						data[0][0]=t3.getArg(1); //precip
						//data[0][13]=t3.getArg(2); //code meteo
						data[0][1]=t3.getArg(3); //meteo
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			default:
				return null;
			}
		}

		public Object[][] getData3(String site) { //terzi 6
			Object[][] data=null;
			switch (site) {
			case "OpenWeatherMap":
				try{
				//LogicTuple t1=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
				//LogicTuple t2=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var(), new Var(), new Var());
				LogicTuple t3=new LogicTuple("meteo", new Value("openweathermap"), new Var(), new Var(), new Var());
			
				ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
				
				if(op3.isResultSuccess())
				{
					data= new Object[1][2];
					
					
					data[0][0]=t3.getArg(1); //precip
					//data[0][1]=t3.getArg(2); //code meteo
					data[0][1]=t3.getArg(3); //meteo
				}
				else
				{
					tp.appendText("MeteoAgent : error in activity.");
				}
				}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
			
				return data;
				
			case "Yahoo":
				
				try{
					//LogicTuple t1=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var(), new Var());
					//LogicTuple t2=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var(), new Var(), new Var());
					LogicTuple t3=new LogicTuple("meteo", new Value("yahoo"), new Var(), new Var(), new Var());
					
					ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
					
					if(op3.isResultSuccess())
					{
						data= new Object[1][1];
						
						
						//data[0][12]=t3.getArg(1); //precip
						//data[0][0]=t3.getArg(2); //code meteo
						data[0][0]=t3.getArg(3); //meteo
					}
					else
					{
						tp.appendText("MeteoAgent : error in activity.");
					}
					}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
				
					return data;
				
			case "Google":
			
					return null;
				
			default:
				return null;
			}
		}

		

		//DA TESTARE
		
		public String[] getShuttersActions() {
			 LogicTuple t3=new LogicTuple("shutters_action", new Value("up"), new Var(), new Var());
			 LogicTuple t4=new LogicTuple("shutters_action", new Value("down"), new Var(), new Var());
			 try{
				ITucsonOperation op3=acc.rdp(myTid, t3, Long.MAX_VALUE);
				ITucsonOperation op4=acc.rdp(myTid, t4, Long.MAX_VALUE);
				if(op3.isResultSuccess() && op4.isResultSuccess())
				{
					String one;
					String two;
					if((""+t3.getArg(2)).length()==1)
						one=""+t3.getArg(0)+"         "+t3.getArg(1)+":0"+t3.getArg(2);
					else
						one=""+t3.getArg(0)+"         "+t3.getArg(1)+":"+t3.getArg(2);
					if((""+t4.getArg(2)).length()==1)
						two=""+t4.getArg(0)+"    "+t4.getArg(1)+":0"+t4.getArg(2);
					else
						two=""+t4.getArg(0)+"    "+t4.getArg(1)+":"+t4.getArg(2);
					String[] ar = {one, two};
					return ar;
				}
				else
				{
					tp.appendText("MeteoAgent : error in activity.");
					return null;
				}
				}
				catch(TucsonOperationNotPossibleException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(UnreachableNodeException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(OperationTimeOutException ex){
		              ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
		          catch(Exception ex)
		          {
		        	  ex.printStackTrace();
		              tp.appendText("MeteoAgent : error in activity.");
		          }
			 return null;
		}
}