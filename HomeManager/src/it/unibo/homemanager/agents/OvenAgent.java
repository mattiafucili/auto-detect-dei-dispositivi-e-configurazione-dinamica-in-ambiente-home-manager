package it.unibo.homemanager.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.communication.AbstractCommunication;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;
import it.unibo.homemanager.userinterfaces.agents.OvenPanel;

public class OvenAgent extends AbstractTucsonAgent implements TucsonAgentInterface {
	private Device device;
	private String name;
	
	private boolean state;
	private Recipe currentRecipe;
	
	private List<StateChangeListener> listeners;
	
	private EnhancedSynchACC acc;
	
	public TucsonTupleCentreId ovenTc;
	private TucsonTupleCentreId usageManagerTc;
	
	private OvenPanel ovenPanel;
	private TracePanel tracePanel;
	
	public OvenAgent(Device device, TracePanel tracePanel, TucsonTupleCentreId ovenTc, TucsonTupleCentreId usegeManagerTc, Database database) throws TucsonInvalidAgentIdException {
		super(device.getDeviceName() + "_" + device.getDeviceId());
		this.device = device;
		name = device.getDeviceName() + "_" + device.getDeviceId();

		state = false;
		currentRecipe = null;
		listeners = new ArrayList<StateChangeListener>();

		this.ovenTc = ovenTc;
		this.usageManagerTc = usageManagerTc;

		this.tracePanel = tracePanel;
	}
	
	public JPanel getInterface() {
		return (ovenPanel);
	}
	
	public void setPanel(ViewManageAgentPanel butlerPanel) {
		ovenPanel = new OvenPanel(butlerPanel, this, getTracePanel());
	}
	
	public Device getDevice() {
		return (device);
	}
	
	public String getName() {
		return (name);
	}
	
	public boolean getState() {
		return (state);
	}
	
	public void setState(boolean state) {
		this.state = state;
		onStateChanged();
	}
	
	public Recipe getCurrentRecipe() {
		return (currentRecipe);
	}
	
	public void setCurrentRecipe(Recipe currentRecipe) {
		this.currentRecipe = currentRecipe;
		onStateChanged();
	}
	
	public List<StateChangeListener> getListeners() {
		return (listeners);
	}
	
	public void addListener(StateChangeListener stateChangeListener) {
		getListeners().add(stateChangeListener);
	}
	
	public void removeListener(StateChangeListener stateChangeListener) {
		getListeners().remove(stateChangeListener);
	}
	
	public EnhancedSynchACC getAcc() {
		return (acc);
	}
	
	public TucsonTupleCentreId getOvenTc() {
		return (ovenTc);
	}
	
	public TucsonTupleCentreId getUsageManagerTc() {
		return (usageManagerTc);
	}
	
	public OvenPanel getOvenPanel() {
		return (ovenPanel);
	}
	
	public TracePanel getTracePanel() {
		return (tracePanel);
	}
	
	protected void main() {
		acc = getContext();

		getTracePanel().appendText("--- " + getName() + " STARTED!");

		try {
			writePresenceInformation();

			while (true) {
				alignState();

				LogicTuple recipeRequest = isThereRecipeToCook();
				if ( recipeRequest != null )
					cookRecipe(recipeRequest);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { }
			}
		} catch (Exception e) { }
	}
	
	private boolean writePresenceInformation() throws Exception {
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(" + getDevice().getDeviceName() + "(" + getDevice().getDeviceId() + ")" + "," + AbstractCommunication.getInfoDevice() + 
						"(" + "off" + "," + getDevice().getDeviceEnergy() + ")" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		return (operation.isResultSuccess());
	}
	
	private void alignState() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + "order" + "," + getDevice().getDeviceName() + "("
				+ getDevice().getDeviceId() + ")" + "," + "A" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if (operation.isResultSuccess()) {
			LogicTuple result = operation.getLogicTupleResult();
			
			int first = 0;
			int second = 1;
			int third = 2;
			int fourth = 3;
			int fifth = 4;
			
			String deviceState = result.getArg(third).toString();
			if (deviceState.equals("on")) {
				nameTemplate = "current";
				tuple = nameTemplate + "(" + getDevice().getDeviceName() + "(" + getDevice().getDeviceId() + ")" + "," + "A" + ")";
				template = LogicTuple.parse(tuple);
				operation = getAcc().rd(getOvenTc(), template, Long.MAX_VALUE);
				
				if (operation.isResultSuccess()) {
					result = operation.getLogicTupleResult();
					TupleArgument recipeArgument = result.getArg(second);
					
					int recipeId = recipeArgument.getArg(first).intValue();
					String recipeName = recipeArgument.getArg(second).toString();
					int cookingTime = recipeArgument.getArg(third).intValue();
					int temperature = recipeArgument.getArg(fourth).intValue();
					TupleArgument ingredientsArgument = recipeArgument.getArg(fifth);
					
					List<Ingredient> ingredients = new ArrayList<Ingredient>();
					for (int i = 0; i < ingredientsArgument.getArity(); i++) {
						TupleArgument ingredientArgument = ingredientsArgument.getArg(i);
						String name = ingredientArgument.getArg(first).toString();
						int quantity = ingredientArgument.getArg(second).intValue();
						Ingredient ingredient = new Ingredient(name, quantity);
						ingredients.add(ingredient);
					}
					
					Recipe currentRecipe = new Recipe(recipeId, recipeName, cookingTime, temperature, ingredients);
					
					setState(true);
					setCurrentRecipe(currentRecipe);
				}
			}
			else { // if ( deviceState.equals("off") )
				setState(false);
				setCurrentRecipe(null);
			}
		}
	}
	
	private LogicTuple isThereRecipeToCook() throws Exception {
		String nameTemplate = AbstractCommunication.getCooking();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "(" + 
						getDevice().getDeviceId() + ")" + "," + "A" + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getOvenTc(), template, Long.MAX_VALUE);

		if (!operation.isResultSuccess())
			return (null);

		return (operation.getLogicTupleResult());
	}
	
	private void cookRecipe(LogicTuple resultTuple) throws Exception {
		int first = 0;
		int second = 1;
		int third = 2;
		int fourth = 3;
		int fifth = 4;

		String nameTemplate = AbstractCommunication.getCooking();
		String deviceName = resultTuple.getArg(second).toString();
		TupleArgument recipeInformation = resultTuple.getArg(third);
		
		int recipeId = recipeInformation.getArg(first).intValue();
		String recipeName = recipeInformation.getArg(second).toString();
		int cookingTime = recipeInformation.getArg(third).intValue();
		int temperature = recipeInformation.getArg(fourth).intValue();
		TupleArgument ingredientsArgument = recipeInformation.getArg(fifth);
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		for (int i = 0; i < ingredientsArgument.getArity(); i++) {
			TupleArgument ingredientArgument = ingredientsArgument.getArg(i);
			String name = ingredientArgument.getArg(first).toString();
			int quantity = ingredientArgument.getArg(second).intValue();
			Ingredient ingredient = new Ingredient(name, quantity);
			ingredients.add(ingredient);
		}
		
		Recipe recipe = new Recipe(recipeId, recipeName, cookingTime, temperature, ingredients);
		
		int result = turnOn(recipe);
		nameTemplate = AbstractCommunication.getCooking();
		String tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + deviceName + "," + result + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		getAcc().out(getOvenTc(), template, Long.MAX_VALUE);

		if (result == 0) {
			nameTemplate = "current";
			tuple = nameTemplate + "(" + deviceName + "," + recipeInformation + ")";
			template = LogicTuple.parse(tuple);
			getAcc().out(getOvenTc(), template, Long.MAX_VALUE);
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						String nameTemplate = "current";
						String tuple = nameTemplate + "(" + deviceName + "," + recipeInformation + ")";
						LogicTuple template = LogicTuple.parse(tuple);
						getAcc().inp(getOvenTc(), template, Long.MAX_VALUE);
						
						turnOff();
					} catch (Exception e) { }
				}
			}, cookingTime * 1000);
		}
	}
	
	public int turnOn(Recipe currentRecipe) throws Exception {
		int third = 2;

		// alignState();
		
		if ( getState() )
			return (2);

		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "("
				+ getDevice().getDeviceId() + ")" + "," + "on" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);

		nameTemplate = AbstractCommunication.getStateChange();
		tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + getDevice().getDeviceName() + "(" + getDevice().getDeviceId()
				+ ")" + "," + "A" + "," + getDevice().getDeviceType() + ")";
		
		template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().in(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		LogicTuple tupleResult = operation.getLogicTupleResult();
		String response = tupleResult.getArg(third).toString();
		
		if ( !response.equals("on") )
			return (1);
			
		setState(true);
		setCurrentRecipe(currentRecipe);
		return (0);
	}
	
	public boolean turnOff() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "("
				+ getDevice().getDeviceId() + ")" + "," + "off" + "," + getDevice().getDeviceType() + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);
		
		setState(false);
		setCurrentRecipe(null);
		return (true);
	}
	
	private void onStateChanged() {
		for ( StateChangeListener listener : getListeners() )
			listener.onStateChanged();
	}
	
	public void show() {
		getOvenPanel().init();
	}
	
	public void hide() {
		getOvenPanel().hidePanel();
	}
	
	public void operationCompleted(AbstractTupleCentreOperation arg) { }
	
	public void operationCompleted(ITucsonOperation arg) { }
	
}
