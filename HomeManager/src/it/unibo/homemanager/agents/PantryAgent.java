package it.unibo.homemanager.agents;

import java.util.ArrayList;
import java.util.List;

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
import it.unibo.homemanager.userinterfaces.agents.PantryPanel;

public class PantryAgent extends AbstractTucsonAgent implements TucsonAgentInterface, ContainersInterface {
	private Device device;
	private String name;
	
	private boolean state;
	
	private List<Ingredient> currentIngredients;
	
	private List<StateChangeListener> stateListeners;
	private List<IngredientsChangeListener> ingredientsListeners;
	
	private EnhancedSynchACC acc;

	private TucsonTupleCentreId pantryTc;
	private TucsonTupleCentreId mixerContainerTc;
	private TucsonTupleCentreId usageManagerTc;

	private PantryPanel pantryPanel;
	private TracePanel tracePanel;

	public PantryAgent(Device device, TracePanel tracePanel, TucsonTupleCentreId pantryTc, TucsonTupleCentreId mixerContainerTc, TucsonTupleCentreId usageManagerTc, Database database) throws TucsonInvalidAgentIdException {
		super(device.getDeviceName() + "_" + device.getDeviceId());

		this.device = device;
		this.name = device.getDeviceName() + "_" + device.getDeviceId();
		
		state = false;
		currentIngredients = new ArrayList<Ingredient>();
		
		stateListeners = new ArrayList<StateChangeListener>();
		ingredientsListeners = new ArrayList<IngredientsChangeListener>();
		
		this.pantryTc = pantryTc;
		this.mixerContainerTc = mixerContainerTc;
		this.usageManagerTc = usageManagerTc;
		
		this.tracePanel = tracePanel;
	}

	public JPanel getInterface() {
		return pantryPanel;
	}

	public void setPanel(ViewManageAgentPanel manageAgentPanel) {
		pantryPanel = new PantryPanel(manageAgentPanel, this, getTracePanel());
	}

	public Device getDevice() {
		return device;
	}
	
	public String getName() {
		return name;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
		onStateChanged();
	}
	
	public List<Ingredient> getCurrentIngredients() {
		return currentIngredients;
	}

	public void setCurrentIngredients(List<Ingredient> currentIngredients) {
		this.currentIngredients = currentIngredients;
		onIngredientsChanged();
	}

	public List<StateChangeListener> getStateListeners() {
		return stateListeners;
	}
	
	public void addStateListener(StateChangeListener stateChangeListener) {
		getStateListeners().add(stateChangeListener);
	}
	
	public void removeStateListener(StateChangeListener stateChangeListener) {
		getStateListeners().remove(stateChangeListener);
	}
	
	public List<IngredientsChangeListener> getIngredientsListeners() {
		return ingredientsListeners;
	}
	
	public void addIngredientsListener(IngredientsChangeListener ingredientsChangeListener) {
		getIngredientsListeners().add(ingredientsChangeListener);
	}
	
	public void removeIngredientsListener(IngredientsChangeListener ingredientsChangeListener) {
		getIngredientsListeners().remove(ingredientsChangeListener);
	}

	public EnhancedSynchACC getAcc() {
		return acc;
	}

	public TucsonTupleCentreId getPantryTupleCentre() {
		return pantryTc;
	}

	public TucsonTupleCentreId getMixerContainerTc() {
		return mixerContainerTc;
	}
	
	public TucsonTupleCentreId getUsageManagerTc() {
		return usageManagerTc;
	}
	
	public PantryPanel getPantryPanel() {
		return pantryPanel;
	}

	public TracePanel getTracePanel() {
		return tracePanel;
	}

	protected void main() {
		acc = getContext();
		
		try {
			writePresenceInformation();
			
			boolean result = false;
			do {
				result = turnOn();
			} while (result == false);
			
			getTracePanel().appendText("--- " + getName() + " STARTED!");
			
			while (true) {
				try {
					getContent();
					
					LogicTuple checkRecipeRequest = checkRecipeRequest();
					if (checkRecipeRequest != null) {
						checkRecipeAnswer(checkRecipeRequest);
						checkRemoveIngredientsFromPantry();
					}
					
					Thread.sleep(1000);
				} catch (Exception e) { }
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

	private boolean turnOn() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "(" + getDevice().getDeviceId() + 
						")" + "," + "on" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);
		
		nameTemplate = AbstractCommunication.getStateChange();
		tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + getDevice().getDeviceName() + "(" + getDevice().getDeviceId() + ")" + "," + "A" + "," + 
				 getDevice().getDeviceType() + ")";
		
		template = LogicTuple.parse(tuple);
		operation = getAcc().in(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);
		
		LogicTuple result = operation.getLogicTupleResult();
		int third = 2;
		String deviceState = result.getArg(third).toString();
		
		if ( !deviceState.equals("on") )
			return (false);
		
		setState(true);
		return (true);
	}

	private LogicTuple checkRecipeRequest() throws Exception {
		String nameTemplate = AbstractCommunication.getCheckRecipe();
		String pantryName = getName();

		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + pantryName + "," + "A" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getMixerContainerTc(), template, Long.MAX_VALUE);

		if (!operation.isResultSuccess())
			return (null);
		return (operation.getLogicTupleResult());
	}

	private boolean checkRecipeAnswer(LogicTuple request) throws Exception {
		List<Ingredient> ingredients = getCurrentIngredients();

		int first = 0;
		int second = 1;
		int third = 2;

		TupleArgument missingIngredientsArgument = request.getArg(third);
		List<Ingredient> missingIngredients = new ArrayList<Ingredient>();

		for (int i = 0; i < missingIngredientsArgument.getArity(); i++) {
			TupleArgument missingIngredientArgument = missingIngredientsArgument.getArg(i);
			String ingredientName = missingIngredientArgument.getArg(first).toString();
			int ingredientQuantity = missingIngredientArgument.getArg(second).intValue();
			Ingredient missingIngredient = new Ingredient(ingredientName, ingredientQuantity);
			missingIngredients.add(missingIngredient);
		}

		List<Ingredient> myIngredients = new ArrayList<Ingredient>();
		for (Ingredient ingredient : missingIngredients)
			if (ingredients.contains(ingredient)) {
				Ingredient pantryIngredient = ingredients.get(ingredients.indexOf(ingredient));
				if (pantryIngredient.getQuantity() > ingredient.getQuantity())
					myIngredients.add(ingredient);
				else
					myIngredients.add(pantryIngredient);
			}

		String nameTemplate = AbstractCommunication.getCheckRecipe();
		String pantryName = getName();

		String strIngredients = getStringListIngredients(myIngredients);

		String tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + pantryName + "," + strIngredients + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getMixerContainerTc(), template, Long.MAX_VALUE);
		return (operation.isResultSuccess());
	}

	private void checkRemoveIngredientsFromPantry() throws Exception {
		String nameTemplate = AbstractCommunication.getRemoveIngredients();
		String pantryName = getName();

		String tuple = nameTemplate + "(" + pantryName + "," + "A,B" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().in(getMixerContainerTc(), template, Long.MAX_VALUE);
		if (operation.isResultSuccess()) {
			int third = 2;
			LogicTuple removeRequest = operation.getLogicTupleArgument();
			String remove = removeRequest.getArg(third).toString();
			if (remove.equals("remove"))
				removeIngredientsFromFridge(removeRequest);
		}
	}

	private boolean removeIngredientsFromFridge(LogicTuple removeRequest) throws Exception {
		int first = 0;
		int second = 1;

		List<Ingredient> pantryContent = getCurrentIngredients();
		TupleArgument ingredients = removeRequest.getArg(second);

		for (int i = 0; i < ingredients.getArity(); i++) {
			TupleArgument ingredientArgument = ingredients.getArg(i);
			String name = ingredientArgument.getArg(first).toString();
			int quantity = ingredientArgument.getArg(second).intValue();
			Ingredient pantryIngredient = pantryContent.get(pantryContent.indexOf(new Ingredient(name, -1)));
			Ingredient ingredient = new Ingredient(name, pantryIngredient.getQuantity() - quantity);
			if (!updateContent(ingredient))
				return (false);
		}
		return (true);
	}

	private String getStringListIngredients(List<Ingredient> ingredients) {
		if (ingredients.size() == 0)
			return ("ingredients");

		String result = "";
		result = AbstractCommunication.getIngredients() + "(";
		for (Ingredient ingredient : ingredients)
			result = result + AbstractCommunication.getIngredient() + "(" + ingredient.getName() + "," + ingredient.getQuantity() + ")" + ",";
		result = result.substring(0, result.length() - 1);
		result = result + ")";
		return (result);
	}

	public void operationCompleted(AbstractTupleCentreOperation arg) { }

	public void operationCompleted(ITucsonOperation arg) { }

	public synchronized void getContent() throws Exception {
		String nameTemplate = "content";
		String pantryName = getName();

		String tuple = nameTemplate + "(" + pantryName + "," + "A,B" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().rdAll(getPantryTupleCentre(), template, Long.MAX_VALUE);

		List<LogicTuple> logicTuples = operation.getLogicTupleListResult();
		List<Ingredient> ingredients = new ArrayList<Ingredient>();

		int second = 1;
		int third = 2;

		for (LogicTuple logicTuple : logicTuples) {
			String name = logicTuple.getArg(second).toString();
			int quantity = logicTuple.getArg(third).intValue();
			Ingredient ingredient = new Ingredient(name, quantity);
			ingredients.add(ingredient);
		}
		
		if ( checkDifferences(ingredients) )
			setCurrentIngredients(ingredients);
	}
	
	private boolean checkDifferences(List<Ingredient> ingredients) {
		if ( ingredients.size() != getCurrentIngredients().size() )
			return (true);
		
		for (Ingredient ingredient : ingredients) {
			if ( !getCurrentIngredients().contains(ingredient) )
				return (true);
			
			Ingredient fridgeIngredient = getCurrentIngredients().get(getCurrentIngredients().indexOf(ingredient));
			if ( ingredient.getQuantity() != fridgeIngredient.getQuantity() )
				return (true);
		}
		
		return (false);
	}

	public boolean addIngredient(Ingredient ingredient) throws Exception {
		String nameTemplate = "content";
		String pantryName = getName();

		String tuple = nameTemplate + "(" + pantryName + "," + ingredient.getName() + "," + "A" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getPantryTupleCentre(), template, Long.MAX_VALUE);

		int third = 2;

		if (operation.isResultSuccess()) {
			LogicTuple result = operation.getLogicTupleResult();
			int oldQuantity = result.getArg(third).intValue();
			int newQuantity = oldQuantity + ingredient.getQuantity();
			tuple = nameTemplate + "(" + pantryName + "," + ingredient.getName() + "," + newQuantity + ")";
			template = LogicTuple.parse(tuple);
			operation = getAcc().out(getPantryTupleCentre(), template, Long.MAX_VALUE);
			return (operation.isResultSuccess());
		} else { // ( if operation.isResultFailure() )
			tuple = nameTemplate + "(" + pantryName + "," + ingredient.getName() + "," + ingredient.getQuantity() + ")";
			template = LogicTuple.parse(tuple);
			operation = getAcc().out(getPantryTupleCentre(), template, Long.MAX_VALUE);
			return (operation.isResultSuccess());
		}
	}

	public boolean updateContent(Ingredient ingredient) throws Exception {
		String nameTemplate = "content";
		String pantryName = getName();

		String tuple = nameTemplate + "(" + pantryName + "," + ingredient.getName() + "," + "A" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getPantryTupleCentre(), template, Long.MAX_VALUE);

		if (operation.isResultSuccess()) {
			int newQuantity = ingredient.getQuantity();
			if (newQuantity > 0) {
				tuple = nameTemplate + "(" + pantryName + "," + ingredient.getName() + "," + newQuantity + ")";
				template = LogicTuple.parse(tuple);
				operation = getAcc().out(getPantryTupleCentre(), template, Long.MAX_VALUE);
				return (operation.isResultSuccess());
			}
			return (true);
		}
		return (false);
	}
	
	private void onStateChanged() {
		for ( StateChangeListener listener : getStateListeners() )
			listener.onStateChanged();
	}
	
	private void onIngredientsChanged() {
		for ( IngredientsChangeListener listener : getIngredientsListeners() )
			listener.onIngredientsChanged();
	}

	public void show() {
		getPantryPanel().init();
	}

	public void hide() {
		getPantryPanel().hidePanel();
	}
}
