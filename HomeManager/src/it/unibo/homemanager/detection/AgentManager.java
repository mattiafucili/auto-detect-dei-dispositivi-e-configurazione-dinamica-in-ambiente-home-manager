package it.unibo.homemanager.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.agents.TucsonAgentInterface;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;

public class AgentManager {
	private static AgentManager agentManager;
	
	private ArrayList<TucsonAgentInterface> agents;
	
	private Database database;
	
	private ViewManageAgentPanel butlerPanel;
	private TracePanel tracePanel;

	private AgentManager() { }

	public static AgentManager getInstance() {
		if ( agentManager == null )
			agentManager = new AgentManager();
		return (agentManager);
	}

	public void initComponents(ServiceFactory serviceFactory, ViewManageAgentPanel butlerPanel, TracePanel tracePanel, Map<String, TucsonTupleCentreId> tupleCentres) {
		agents = new ArrayList<TucsonAgentInterface>();
		
		try {
			database = serviceFactory.getDatabaseInterface();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.butlerPanel = butlerPanel;
		this.tracePanel = tracePanel;

		try {
			initListAgent(tupleCentres);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initListAgent(Map<String, TucsonTupleCentreId> tupleCentres) throws Exception {
		DeviceManagerAgent deviceManagerAgent = DeviceManagerAgent.getInstance();
		List<Device> devices = deviceManagerAgent.getDevices();
		for (Device device : devices) {
			if (device.getDeviceType().equals("s")) {
				TucsonAgentInterface agent = AgentsFactory.createAgent(device, tupleCentres, tracePanel, database, butlerPanel);
				if ( agent != null )
					getAgents().add(agent);
			}
		}
	}

	public ArrayList<TucsonAgentInterface> getAgents() {
		return (agents);
	}
	
	public Database getDatabase() {
		return (database);
	}
	
	public void addAgent(TucsonAgentInterface agent) {
		getAgents().add(agent);
	}
	
	public TucsonAgentInterface getAgentByName(String name) {
		for ( TucsonAgentInterface agent : getAgents() )
			if ( agent.getName().equalsIgnoreCase(name) )
				return (agent);
		return (null);
	}
	
	public ArrayList<String> getStringAgents() {
		ArrayList<String> result = new ArrayList<String>();
		for ( TucsonAgentInterface agent : getAgents() )
			result.add(agent.getName());
		return (result);
	}

	public void goAgents() {
		for ( TucsonAgentInterface agent : getAgents() ) {
			AbstractTucsonAgent abstractAgent = (AbstractTucsonAgent) agent;
			abstractAgent.go();
		}
	}
	
	public void goAgent(String name) {
		AbstractTucsonAgent abstractAgent = (AbstractTucsonAgent) getAgentByName(name);
		abstractAgent.go();
	}

}
