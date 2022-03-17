package it.unibo.homemanager.usages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.communication.AbstractCommunication;

public class UsageManagerAgent extends AbstractTucsonAgent {
	private static UsageManagerAgent usageManagerAgent;
	
	private TucsonTupleCentreId usageManagerTupleCentre;
	private EnhancedSynchACC acc;
	
	private Map<String, String> presences;
	
	private UsageManagerAgent() throws Exception {
		super("usageManagerAgent");
		
		usageManagerTupleCentre = new TucsonTupleCentreId("usage_manager_tc", "localhost", "20504");
		presences = new HashMap<String, String>();
	}
	
	private void init() throws Exception {
		File file = new File("usages.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = "";
		
		while ( (line = reader.readLine()) != null) {
			LogicTuple logicTuple = LogicTuple.parse(line);
			getAcc().out(getUsageManagerTc(), logicTuple, Long.MAX_VALUE);
		}
		reader.close();
	}
	
	public TucsonTupleCentreId getUsageManagerTc() {
		return (usageManagerTupleCentre);
	}
	
	public EnhancedSynchACC getAcc() {
		return (acc);
	}
	
	public Map<String, String> getPresences() {
		return (presences);
	}
	
	public static UsageManagerAgent getInstance() throws Exception {
		if ( usageManagerAgent == null )
			usageManagerAgent = new UsageManagerAgent();
		return (usageManagerAgent);
	}
	
	@Override
	protected void main() {
		acc = getContext();
		
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while ( true ) {
			try {
				List<LogicTuple> presenceInformationRequest = presenceInformationRequest();
				if ( presenceInformationRequest.size() != 0 )
					managePresenceInformationRequest(presenceInformationRequest);
			} catch (Exception e) { }
			
			try {
				LogicTuple stateChangeRequest = stateChangeRequest();
				if ( stateChangeRequest != null )
					manageStateChangeRequest(stateChangeRequest);
			} catch (Exception e) { }
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private List<LogicTuple> presenceInformationRequest() throws Exception {
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(" + "A,B,C" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().rdAll(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if (!operation.isResultSuccess())
			return (new ArrayList<LogicTuple>());
		
		List<LogicTuple> currentPresences = operation.getLogicTupleListResult();
		List<LogicTuple> result = new ArrayList<LogicTuple>();
		
		int first = 0;
		int third = 2;
		
		for ( LogicTuple currentPresence : currentPresences ) {
			String deviceName = currentPresence.getArg(first).toString();
			String deviceType = currentPresence.getArg(third).toString();
			
			if ( !getPresences().containsKey(deviceName + "-" + deviceType) )
				result.add(currentPresence);
		}
		
		return (result);
	}
	
	private void managePresenceInformationRequest(List<LogicTuple> newPresences) throws Exception {
		int first = 0;
		int third = 2;
		
		for (LogicTuple presenceInformation : newPresences) {
			String deviceName = presenceInformation.getArg(first).toString();
			String deviceType = presenceInformation.getArg(third).toString();
			if ( deviceType.equals("s") )
				getPresences().put(deviceName + "-" + deviceType, presenceInformation.toString());
		}
		
		for (LogicTuple presenceInformation : newPresences) {
			String deviceName = presenceInformation.getArg(first).toString();
			String deviceType = presenceInformation.getArg(third).toString();
			if (deviceType.equals("p")) {
				LogicTuple association = getAssociation(deviceName);
				
				if ( association == null )
					throw (new Exception("association null!"));
				
				alignState(presenceInformation, association);
				getPresences().put(deviceName + "-" + deviceType, presenceInformation.toString());
			}
		}
	}
	
	private LogicTuple getAssociation(String deviceName) throws Exception {
		int first = 0;
		int second = 1;
		
		for (String device : getPresences().keySet()) {
			String[] array = device.split("-");
			String currentDeviceName = array[first];
			String currentDeviceType = array[second];
			
			if ( currentDeviceName.equals(deviceName) && currentDeviceType.equals("s") ) {
				String tuple = getPresences().get(device);
				LogicTuple logicTuple = LogicTuple.parse(tuple);
				return (logicTuple);
			}
		}
		return (null);
	}
	
	private void alignState(LogicTuple presenceInformation, LogicTuple association) throws Exception {
		int first = 0;
		int second = 1;
		
		String deviceName = presenceInformation.getArg(first).toString();
		String deviceState = association.getArg(second).getArg(first).toString();
		int watt = presenceInformation.getArg(second).getArg(second).intValue();
		
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(" + deviceName + "," + "A" + "," + "p" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		getAcc().inp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		nameTemplate = AbstractCommunication.getPresenceInformation();
		tuple = nameTemplate + "(" + deviceName + "," + AbstractCommunication.getInfoDevice() + "(" + deviceState + "," + watt + ")" + "," + "p" + ")";
		template = LogicTuple.parse(tuple);
		getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		nameTemplate = AbstractCommunication.getStateChange();
		tuple = nameTemplate + "(" + AbstractCommunication.getOrder() + "," + deviceName + "," + deviceState + "," + "p" + ")";
		template = LogicTuple.parse(tuple);
		getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
	}
	
	private LogicTuple stateChangeRequest() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + "A,B,C" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (null);
		
		return (operation.getLogicTupleResult());
	}
	
	private void manageStateChangeRequest(LogicTuple stateChangeRequest) throws Exception {
		int second = 1;
		int third = 2;
		int fourth = 3;
		
		String deviceName = stateChangeRequest.getArg(second).toString();
		String requestType = stateChangeRequest.getArg(third).toString();
		String deviceType = stateChangeRequest.getArg(fourth).toString();
		
		if ( requestType.equals("on") )
			turnOn(deviceName, deviceType);
		else if ( requestType.equals("off") )
			turnOff(deviceName, deviceType);
	}
	
	private void turnOn(String deviceName, String deviceType) throws Exception {
		int second = 1;
		int third = 2;
		
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(" + deviceName + "," + "A,B" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().rdp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if (operation.isResultSuccess()) {
			LogicTuple result = operation.getLogicTupleArgument();
			int watt = result.getArg(second).getArg(second).intValue();
			
			if (canTurnOn(deviceName, watt)) {
				tuple = nameTemplate + "(" + deviceName + "," + "A,B" + ")";
				template = LogicTuple.parse(tuple);
				operation = getAcc().inAll(getUsageManagerTc(), template, Long.MAX_VALUE);
				
				if (operation.isResultSuccess()) {
					List<LogicTuple> devices = operation.getLogicTupleListResult();
					for ( LogicTuple device : devices ) {
						String currentDeviceType = device.getArg(third).toString();
						
						nameTemplate = AbstractCommunication.getPresenceInformation();
						tuple = nameTemplate + "(" + deviceName + "," + AbstractCommunication.getInfoDevice() + "(" + "on" + "," + watt + ")" + "," + currentDeviceType + ")";
						template = LogicTuple.parse(tuple);
						operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
						
						getPresences().put(deviceName + "-" + currentDeviceType, tuple);
						
						if (deviceType.equals(currentDeviceType)) {
							nameTemplate = AbstractCommunication.getStateChange();
							tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + deviceName + "," + "on" + "," + currentDeviceType + ")";
							template = LogicTuple.parse(tuple);
							operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
						} else { // if (!deviceType.equals(currentDeviceType))
							nameTemplate = AbstractCommunication.getStateChange();
							tuple = nameTemplate + "(" + AbstractCommunication.getOrder() + "," + deviceName + "," + "on" + "," + currentDeviceType + ")";
							template = LogicTuple.parse(tuple);
							operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
						}
					}
				}
			} else {
				nameTemplate = AbstractCommunication.getStateChange();
				tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + deviceName + "," + "off" + "," + deviceType + ")";
				template = LogicTuple.parse(tuple);
				operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
			}
		}
	}
	
	private boolean canTurnOn(String deviceName, int watt) throws Exception {
		int first = 0;
		int second = 1;
		
		String nameTemplate = AbstractCommunication.getMax();
		String tuple = nameTemplate + "(" + "A" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().rdp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		LogicTuple maxTuple = operation.getLogicTupleResult();
		int max = maxTuple.getArg(first).intValue();
		
		nameTemplate = AbstractCommunication.getPresenceInformation();
		tuple = nameTemplate + "(A,B,C)";
		template = LogicTuple.parse(tuple);
		operation = getAcc().rdAll(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		List<LogicTuple> allPresences = operation.getLogicTupleListResult();
		List<String> devices = new ArrayList<String>();
		
		int currentWatt = 0;
		for (LogicTuple presence : allPresences) {
			String currentDevice = presence.getArg(first).toString();
			if ( !devices.contains(currentDevice) && !currentDevice.equals(deviceName) ) {
				String deviceState = presence.getArg(second).getArg(first).toString();
				int currentDeviceWatt = presence.getArg(second).getArg(second).intValue();
				
				if ( deviceState.equals("on") )
					currentWatt = currentWatt + currentDeviceWatt;
				
				devices.add(currentDevice);
			}
		}
		return ( (currentWatt + watt) < max );
	}
	
	private void turnOff(String deviceName, String deviceType) throws Exception {
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(" + deviceName + "," + "A,B" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inAll(getUsageManagerTc(), template, Long.MAX_VALUE);
		if (operation.isResultSuccess()) {
			List<LogicTuple> devices = operation.getLogicTupleListResult();
			
			int second = 1;
			int third = 2;
			
			for ( LogicTuple device : devices ) {
				int watt = device.getArg(second).getArg(second).intValue();
				String currentDeviceType = device.getArg(third).toString();
				nameTemplate = AbstractCommunication.getPresenceInformation();
				tuple = nameTemplate + "(" + deviceName + "," + AbstractCommunication.getInfoDevice() + "(" + "off" + "," + watt + ")" + "," + currentDeviceType + ")";
				template = LogicTuple.parse(tuple);
				operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
				getPresences().put(deviceName + "-" + currentDeviceType, tuple);
				
				if ( !currentDeviceType.equals(deviceType) ) {
					nameTemplate = AbstractCommunication.getStateChange();
					tuple = nameTemplate + "(" + AbstractCommunication.getOrder() + "," + deviceName + "," + "off" + "," + currentDeviceType + ")";
					template = LogicTuple.parse(tuple);
					operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
				}
			}
		}
	}
	
	@Override
	public void operationCompleted(AbstractTupleCentreOperation arg) { }
	
	@Override
	public void operationCompleted(ITucsonOperation arg) { }
	
}