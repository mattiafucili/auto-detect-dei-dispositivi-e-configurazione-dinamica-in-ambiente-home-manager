package it.unibo.homemanager.social;

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
import it.unibo.homemanager.userinterfaces.ViewTwitterAgentPanel;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterAgent extends AbstractTucsonAgent implements TucsonAgentInterface
{
	private TucsonTupleCentreId myTid;
	private Twitter twitter=null;
	private EnhancedSynchACC acc;
	private TracePanel tp;
	private Database db;
	private Date lastTimeStamp=null;
	private ViewTwitterAgentPanel panel;
	private ArrayList<String> consumerKeyArray=null;
	private ArrayList<String> accessTokensArray=null;
	
	public TwitterAgent(String id, TracePanel tracePanel, TucsonTupleCentreId tid, Database database) throws TucsonInvalidAgentIdException 
	{
        super(id);
        this.myTid = tid;
        this.tp = tracePanel;
        //this.twitter = LoginTwitter.login();
        this.db=database;
        this.consumerKeyArray = new ArrayList<String>();
        this.accessTokensArray = new ArrayList<String>();
        this.consumerKeyArray.add("cNYRXz1ruxKdSotsm25b3MowQ");
        this.accessTokensArray.add("3104361574-Y56UJkSiGT6UVLrmFoWQ8qLCQfp0o9ovx0cOQro");
        
    }
	
	 public void setPanel(ViewManageAgentPanel maPanel)
	 {
		 panel=new ViewTwitterAgentPanel(maPanel, this, tp);
	 }

	@Override
	public void operationCompleted(AbstractTupleCentreOperation arg0) 
	{
		 throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void operationCompleted(ITucsonOperation arg0) 
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	protected void main()
	{
		tp.appendText("-- TWITTER AGENT STARTED.");
	}
	
	public void saveTweets() throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException
	{
		List<Status> statuses = getTwitterStatusesFromHome();
		
		if(statuses!=null) 
		{
			this.tp.appendText("TwitterAgent: saving tweets");
			for(int i=(statuses.size()-1); i>0; i--)  //leggo in ordine cronologico
			{
				Status s = (Status) statuses.get(i);
				
				if(this.lastTimeStamp==null || s.getCreatedAt().after(this.lastTimeStamp))
				{
					double latitude = 0;
					double longitude = 0;
					
					if(s.getGeoLocation()!=null)
					{
						latitude = s.getGeoLocation().getLatitude();
						longitude = s.getGeoLocation().getLongitude();
					}
					
					//Debugging
					//String user = s.getUser().getName();
					//String testo=s.getText();
					//String data = s.getCreatedAt().toString();
					
					LogicTuple template = new LogicTuple("tweet", new Value(s.getUser().getName()),
							new Value(s.getText()), new Value(s.getCreatedAt().toString()),
							new Value(latitude), new Value(longitude));
					
					ITucsonOperation op = this.acc.out(this.myTid, template, null);
					if(op.isResultSuccess())
					{
						this.lastTimeStamp = s.getCreatedAt();
					}
					
				}
			}
		}
	}
	
	private List<Status> getTwitterStatusesFromHome()
	{
		List<Status> statuses=null;
		
		try 
		{
			if(this.twitter==null)
			{
				String[] credenziali = this.ottieniCredenziali();
				int indice = Integer.parseInt(credenziali[0]);
				this.twitter = LoginTwitter.login(this.consumerKeyArray.get(indice-1), credenziali[1], this.accessTokensArray.get(indice-1), credenziali[2]);
			}
			statuses = this.twitter.getHomeTimeline();
        } 
		catch (TwitterException e) 
		{
            e.printStackTrace();
        }
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return statuses;
	}
	
	@Override
	public void show() 
	{
		panel.init();
	}

	@Override
	public void hide() 
	{
		panel.hidePanel();
	}

	@Override
	public JPanel getInterface() 
	{
		return panel;
	}

	@Override
	public String getName() 
	{
		return "Twitter Agent";
	}
	
	public ArrayList<String> readTweets()
	{
		ArrayList<String> result = new ArrayList<String>();
		
		LogicTuple t = new LogicTuple("tweet", new Var(), new Var(), new Var(), new Var(), new Var());
		ITucsonOperation op;
		try 
		{
			op = this.acc.rdAll(this.myTid, t, null);
			if(op.isResultSuccess())
			{
				List<LogicTuple> l = op.getLogicTupleListResult();
				for (LogicTuple logicTuple : l) 
				{
					String tmp = logicTuple.getArg(0).toString() + ":	" + logicTuple.getArg(1).toString() + " " + logicTuple.getArg(2).toString();
					result.add(tmp);
				}
			}
		} 
		catch (TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e)
		{

			e.printStackTrace();
		}
		
		
		return result;
	}
	
	private String[] ottieniCredenziali()
	{
		String[] result = new String[3];
		this.acc=this.getContext();
		
		try 
		{
			LogicTuple t = new LogicTuple("credenziali_twitter", new Var(), new Var(), new Var());
			ITucsonOperation op_rd=this.acc.rd(((TucsonDatabase)db).getTucsonTupleCentreId(), t, Long.MAX_VALUE);
			if(op_rd.isResultSuccess())
			{
				LogicTuple lt = op_rd.getLogicTupleResult();
				result[0] = lt.getArg(0).toString();
				result[1] = lt.getArg(1).toString();
				result[2] = lt.getArg(2).toString();
			}
			
		} 
		catch (TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

}