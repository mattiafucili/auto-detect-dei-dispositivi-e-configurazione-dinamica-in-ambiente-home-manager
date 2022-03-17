package it.unibo.homemanager.agents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.communication.AbstractCommunication;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;
import it.unibo.homemanager.userinterfaces.agents.MixerPanel;

public class MixerAgent extends AbstractTucsonAgent implements TucsonAgentInterface {
	private Device device;
	private String name;
	
	private boolean state;
	private List<Recipe> currentRecipes;
	
	private List<StateChangeListener> stateListeners;
	private List<RecipesChangeListener> recipesListeners;
	
	private List<String> typeContainers;

	private EnhancedSynchACC acc;

	private TucsonTupleCentreId mixerTc;
	private TucsonTupleCentreId mixerContainerTc;
	private TucsonTupleCentreId ovenTc;
	private TucsonTupleCentreId usageManagerTc;

	private MixerPanel mixerPanel;
	private TracePanel tracePanel;

	public MixerAgent(Device device, TracePanel tracePanel, TucsonTupleCentreId mixerTc,
			TucsonTupleCentreId mixerContainerTc, TucsonTupleCentreId ovenTc, TucsonTupleCentreId usageManagerTc,
			Database database) throws Exception {
		super(device.getDeviceName() + "_" + device.getDeviceId());

		this.device = device;
		name = device.getDeviceName() + "_" + device.getDeviceId();
		state = false;
		currentRecipes = new ArrayList<Recipe>();
		
		recipesListeners = new ArrayList<RecipesChangeListener>();
		stateListeners = new ArrayList<StateChangeListener>();
		
		initContainers();
		
		TucsonAgentId agentId = new TucsonAgentId(getName());
		acc = TucsonMetaACC.getContext(agentId);

		this.mixerTc = mixerTc;
		this.mixerContainerTc = mixerContainerTc;
		this.ovenTc = ovenTc;
		this.usageManagerTc = usageManagerTc;
		
		this.tracePanel = tracePanel;
	}

	private void initContainers() throws Exception {
		typeContainers = new ArrayList<String>();
		File file = new File("containers.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = "";

		while ((line = reader.readLine()) != null) {
			getTypeContainers().add(line);
		}
		reader.close();
	}
	
	private void initRecipe() throws Exception {
		File file = new File("recipes.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = "";
		
		while ( (line = reader.readLine()) != null) {
			LogicTuple logicTuple = LogicTuple.parse(line);
			getAcc().out(getMixerTc(), logicTuple, Long.MAX_VALUE);
			Recipe recipe = createRecipeByLogicTuple(logicTuple);
			getCurrentRecipes().add(recipe);
		}
		reader.close();
	}
	
	private Recipe createRecipeByLogicTuple(LogicTuple logicTuple) {
		int first = 0;
		int second = 1;
		int third = 2;
		int fourth = 3;
		int fifth = 4;
		
		int id = logicTuple.getArg(first).intValue();
		String name = logicTuple.getArg(second).toString();
		int cookingTime = logicTuple.getArg(third).intValue();
		int temperature = logicTuple.getArg(fourth).intValue();

		TupleArgument ingredientsArgument = logicTuple.getArg(fifth);
		List<Ingredient> ingredients = new ArrayList<Ingredient>();

		for (int i = 0; i < ingredientsArgument.getArity(); i++) {
			TupleArgument ingredientArgument = ingredientsArgument.getArg(i);
			String ingredientName = ingredientArgument.getArg(first).toString();
			int ingredientQuantity = ingredientArgument.getArg(second).intValue();
			Ingredient ingredient = new Ingredient(ingredientName, ingredientQuantity);
			ingredients.add(ingredient);
		}

		return new Recipe(id, name, cookingTime, temperature, ingredients);
	}

	private void persistRecipes() throws Exception {
		FileWriter writer = new FileWriter(new File("recipes.txt"));
		String nameTemplate = AbstractCommunication.getRecipe();
		
		for(Recipe recipe : getCurrentRecipes()) {
			// recipe(id,name,cookingTime,temperature,ingredients(ingredient(name1,qta1),ingredient(name2,qta2),...))
			String tuple = nameTemplate + "(" + recipe.getId() + "," + recipe.getName() + "," + recipe.getCookingTime()
					+ "," + recipe.getTemperature() + "," + getStringListIngredients(recipe.getIngredients()) + ")";
			writer.write(tuple);
		}
		writer.close();
	}

	@Override
	public JPanel getInterface() {
		return (mixerPanel);
	}

	public Device getDevice() {
		return (device);
	}

	@Override
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
	
	public List<Recipe> getCurrentRecipes() {
		return (currentRecipes);
	}

	public void setCurrentRecipes(List<Recipe> currentRecipes) {
		this.currentRecipes = currentRecipes;
		onRecipesChanged();
		try {
			persistRecipes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<StateChangeListener> getStateListeners() {
		return (stateListeners);
	}
	
	public void addStateListener(StateChangeListener stateChangeListener) {
		getStateListeners().add(stateChangeListener);
	}
	
	public void removeStateListener(StateChangeListener stateChangeListener) {
		getStateListeners().remove(stateChangeListener);
	}
	
	public List<RecipesChangeListener> getRecipesListeners() {
		return (recipesListeners);
	}
	
	public void addRecipesListener(RecipesChangeListener recipesChangeListener) {
		getRecipesListeners().add(recipesChangeListener);
	}
	
	public void removeRecipesListener(RecipesChangeListener recipesChangeListener) {
		getRecipesListeners().remove(recipesChangeListener);
	}

	public List<String> getTypeContainers() {
		return (typeContainers);
	}

	public EnhancedSynchACC getAcc() {
		return (acc);
	}

	public TucsonTupleCentreId getMixerTc() {
		return (mixerTc);
	}

	public TucsonTupleCentreId getMixerContainerTc() {
		return (mixerContainerTc);
	}

	public TucsonTupleCentreId getOvenTc() {
		return (ovenTc);
	}

	public TucsonTupleCentreId getUsageManagerTc() {
		return (usageManagerTc);
	}

	public MixerPanel getMixerPanel() {
		return (mixerPanel);
	}

	public TracePanel getTracePanel() {
		return (tracePanel);
	}

	@Override
	protected void main() {	
		getTracePanel().appendText("--- " + getName() + " STARTED!");
		
		try {
			initRecipe();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			writePresenceInformation();
			while (true) {
				alignState();
				
				readAllRecipes();
				
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
		String tuple = nameTemplate + "(" + AbstractCommunication.getOrder() + "," + getDevice().getDeviceName() + "("
				+ getDevice().getDeviceId() + ")" + "," + "A" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().inp(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if (operation.isResultSuccess()) {
			LogicTuple result = operation.getLogicTupleResult();
			int third = 2;
			String deviceState = result.getArg(third).toString();
			if ( deviceState.equals("on") )
				setState(true);
			else // if ( deviceState.equals("off") )
				setState(false);
		}
	}

	public void setPanel(ViewManageAgentPanel butlerPanel) {
		mixerPanel = new MixerPanel(this, butlerPanel, getTracePanel());
	}

	@Override
	public void operationCompleted(AbstractTupleCentreOperation arg) { }

	@Override
	public void operationCompleted(ITucsonOperation arg) { }

	public synchronized void readAllRecipes() throws Exception {
		String nameTemplate = AbstractCommunication.getRecipe();

		// recipe(*,*,*,*,*)
		String tuple = nameTemplate + "(" + "A,B,C,D,E" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		Long maxValue = new Long(Long.MAX_VALUE);
		ITucsonOperation operation = getAcc().rdAll(getMixerTc(), template, maxValue);

		List<LogicTuple> logicTuples = operation.getLogicTupleListResult();

		int first = 0;
		int second = 1;
		int third = 2;
		int fourth = 3;
		int fifth = 4;
		
		List<Recipe> allRecipes = new ArrayList<Recipe>();
		for (LogicTuple logicTuple : logicTuples) {
			int id = logicTuple.getArg(first).intValue();
			String name = logicTuple.getArg(second).toString();
			int cookingTime = logicTuple.getArg(third).intValue();
			int temperature = logicTuple.getArg(fourth).intValue();

			TupleArgument ingredientsArgument = logicTuple.getArg(fifth);
			List<Ingredient> ingredients = new ArrayList<Ingredient>();

			for (int i = 0; i < ingredientsArgument.getArity(); i++) {
				TupleArgument ingredientArgument = ingredientsArgument.getArg(i);
				String ingredientName = ingredientArgument.getArg(first).toString();
				int ingredientQuantity = ingredientArgument.getArg(second).intValue();
				Ingredient ingredient = new Ingredient(ingredientName, ingredientQuantity);
				ingredients.add(ingredient);
			}

			Recipe recipe = new Recipe(id, name, cookingTime, temperature, ingredients);
			allRecipes.add(recipe);
		}
		
		if ( checkDifferences(allRecipes) )
			setCurrentRecipes(allRecipes);
	}
	
	private boolean checkDifferences(List<Recipe> recipes) {
		if ( recipes.size() != getCurrentRecipes().size() )
			return (true);
		
		for ( Recipe recipe : recipes )
			if ( !getCurrentRecipes().contains(recipe) )
				return (true);
		
		return (false);
	}

	public Result prepareRecipe(Recipe recipe) throws Exception {
		// alignState();
		
		if ( getState() )
			return (new Result(-1, null));
		
		if ( !turnOn() )
			return (new Result(-2, null));
		
		setCurrentRecipe(recipe);
		
		Set<String> containers = getContainers();

		List<Ingredient> missingIngredients = new ArrayList<Ingredient>();
		for ( Ingredient ingredient : recipe.getIngredients() )
			missingIngredients.add(new Ingredient(ingredient.getName(), ingredient.getQuantity()));

		Map<String, List<Ingredient>> answers = new HashMap<String, List<Ingredient>>();

		for (String container : containers) {
			// check_recipe(request, name_device, missing_recipe_ingredients);
			String nameTemplate = AbstractCommunication.getCheckRecipe();
			String nameDevice = container;
			String strIngredients = getStringListIngredients(missingIngredients);
			String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + nameDevice + "," + strIngredients + ")";
			LogicTuple template = LogicTuple.parse(tuple);
			Long maxValue = new Long(Long.MAX_VALUE);
			ITucsonOperation operation = getAcc().out(getMixerContainerTc(), template, maxValue);
			if (operation.isResultSuccess()) {
				nameTemplate = AbstractCommunication.getCheckRecipe();
				// check_recipe(response, name_device, your_recipe_ingredients);
				tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + nameDevice + "," + "A" + ")";
				template = LogicTuple.parse(tuple);
				Long timeOut = new Long(3000);
				try {
					operation = getAcc().in(getMixerContainerTc(), template, timeOut);
					if (operation.isResultSuccess()) {
						LogicTuple answer = operation.getLogicTupleResult();

						int first = 0;
						int second = 1;
						int third = 2;

						TupleArgument ingredientsArgument = answer.getArg(third);
						List<Ingredient> ingredients = new ArrayList<Ingredient>();

						for (int i = 0; i < ingredientsArgument.getArity(); i++) {
							TupleArgument ingredientArgument = ingredientsArgument.getArg(i);
							String ingredientName = ingredientArgument.getArg(first).toString();
							int ingredientQuantity = ingredientArgument.getArg(second).intValue();
							Ingredient ingredient = new Ingredient(ingredientName, ingredientQuantity);
							ingredients.add(ingredient);
						}

						answers.put(nameDevice, ingredients);
						
						for (Ingredient ingredient : ingredients) {
							Ingredient missingIngredient = missingIngredients
									.get(missingIngredients.indexOf(ingredient));
							if ( ingredient.getQuantity() < missingIngredient.getQuantity() )
								missingIngredient.setQuantity(missingIngredient.getQuantity() - ingredient.getQuantity());
							else if ( ingredient.getQuantity() == missingIngredient.getQuantity() )
								missingIngredients.remove(missingIngredient);
						}

						if (missingIngredients.size() == 0) {
							sendAnswerToContainers(answers, "remove");
							
							mixIngredients();
							
							Set<String> ovens = getOvens();
							
							nameTemplate = AbstractCommunication.getRecipe();
							strIngredients = getStringListIngredients(recipe.getIngredients());
							// recipe(id,name,cookingTime,temperature,ingredients(ingredient(name1,qta1),ingredient(name2,qta2),...))
							String strRecipe = nameTemplate + "(" + recipe.getId() + "," + recipe.getName() + "," + recipe.getCookingTime() + 
												"," + recipe.getTemperature() + "," + strIngredients + ")";
							
							for ( String ovenName : ovens )
								if ( cookRecipe(ovenName, strRecipe) ) {
									resetCurrentRecipe();
									turnOff();
									return (new Result(0, null));
								}
							
							resetCurrentRecipe();
							turnOff();
							
							return (new Result(-4, null));
						}
					}
				} catch (OperationTimeOutException e) { }
			}
		}
		
		sendAnswerToContainers(answers, "none");
		resetCurrentRecipe();
		turnOff();
		return (new Result(-3, missingIngredients));
	}
	
	private void setCurrentRecipe(Recipe recipe) throws Exception {
		String nameTemplate = AbstractCommunication.getRecipe();
		String strIngredients = getStringListIngredients(recipe.getIngredients());
		// recipe(id,name,cookingTime,temperature,ingredients(ingredient(name1,qta1),ingredient(name2,qta2),...))
		String strRecipe = nameTemplate + "(" + recipe.getId() + "," + recipe.getName() + "," + recipe.getCookingTime() + 
							"," + recipe.getTemperature() + "," + strIngredients + ")";
		
		nameTemplate = "current";
		String tuple = nameTemplate + "(" + getDevice().getDeviceName() + "(" + 
				getDevice().getDeviceId() + ")" + "," + strRecipe + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		getAcc().out(getOvenTc(), template, Long.MAX_VALUE);
	}
	
	private void resetCurrentRecipe() throws Exception {
		String nameTemplate = "current";
		String tuple = nameTemplate + "(" + getDevice().getDeviceName() + "(" + 
				getDevice().getDeviceId() + ")" + "," + "A" + ")";
		LogicTuple template = LogicTuple.parse(tuple);
		getAcc().inp(getOvenTc(), template, Long.MAX_VALUE);
	}
	
	private void mixIngredients() throws Exception {
		// waiting...
	}
	
	private boolean turnOn() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "(" + 
						getDevice().getDeviceId() + ")" + "," + "on" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);

		nameTemplate = AbstractCommunication.getStateChange();
		tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + getDevice().getDeviceName() + "(" + getDevice().getDeviceId() + "),Z,"
				+ getDevice().getDeviceType() + ")";
		
		template = LogicTuple.parse(tuple);
		operation = getAcc().in(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);
		
		LogicTuple tupleResult = operation.getLogicTupleResult();
		int third = 2;
		String response = tupleResult.getArg(third).toString();
		
		if ( !response.equals("on") )
			return (false);
		
		setState(true);
		return (true);
	}
	
	public boolean turnOff() throws Exception {
		String nameTemplate = AbstractCommunication.getStateChange();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + getDevice().getDeviceName() + "(" +
						getDevice().getDeviceId() + ")" + "," + "off" + "," + getDevice().getDeviceType() + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getUsageManagerTc(), template, Long.MAX_VALUE);
		
		if ( !operation.isResultSuccess() )
			return (false);
		
		setState(false);
		return (true);
	}
	
	private Set<String> getContainers() throws Exception {
		Set<String> containers = new HashSet<String>();
		
		for (String typeContainer : getTypeContainers()) {
			String nameTemplate = AbstractCommunication.getPresenceInformation();
			String tuple = nameTemplate + "(" + typeContainer + "(" + "A" + ")" + "," + "B,C" + ")";
			
			LogicTuple template = LogicTuple.parse(tuple);
			ITucsonOperation operation = getAcc().rdAll(getUsageManagerTc(), template, Long.MAX_VALUE);
	
			if (operation.isResultSuccess()) {
				int first = 0;
				for (LogicTuple logicTuple : operation.getLogicTupleListResult()) {
					String type = logicTuple.getArg(first).getName();
					int index = logicTuple.getArg(first).getArg(first).intValue();
					containers.add(type + "_" + index);
				}
			}
		}

		return (containers);
	}

	private Set<String> getOvens() throws Exception {
		Set<String> ovens = new HashSet<String>();
		String nameTemplate = AbstractCommunication.getPresenceInformation();
		String tuple = nameTemplate + "(oven(A),B,C)";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().rdAll(getUsageManagerTc(), template, Long.MAX_VALUE);

		if (operation.isResultSuccess()) {
			int first = 0;
			for (LogicTuple logicTuple : operation.getLogicTupleListResult()) {
				String oven = logicTuple.getArg(first).toString();
				ovens.add(oven);
			}
		}
		return (ovens);
	}

	private boolean cookRecipe(String ovenName, String strRecipe ) throws Exception {
		String nameTemplate = AbstractCommunication.getCooking();
		String tuple = nameTemplate + "(" + AbstractCommunication.getRequest() + "," + ovenName + "," + strRecipe + ")";
		
		LogicTuple template = LogicTuple.parse(tuple);
		ITucsonOperation operation = getAcc().out(getOvenTc(), template, Long.MAX_VALUE);

		if ( !operation.isResultSuccess() )
			return (false);

		nameTemplate = AbstractCommunication.getCooking();
		tuple = nameTemplate + "(" + AbstractCommunication.getResponse() + "," + ovenName + "," + "A" + ")";
		template = LogicTuple.parse(tuple);
		operation = getAcc().in(getOvenTc(), template, Long.MAX_VALUE);

		if ( !operation.isResultSuccess() )
			return (false);

		int third = 2;
		int cookingResult = operation.getLogicTupleResult().getArg(third).intValue();
		
		if ( cookingResult != 0 )
			return (false);
		
		return (true);
	}

	private void sendAnswerToContainers(Map<String, List<Ingredient>> answers, String answer) throws Exception {
		String nameTemplate = AbstractCommunication.getRemoveIngredients();
		for (String container : answers.keySet()) {
			String yourIngredients = getStringListIngredients(answers.get(container));
			String tuple = nameTemplate + "(" + container + "," + yourIngredients + "," + answer + ")";
			LogicTuple removeIngredientsRequest = LogicTuple.parse(tuple);
			Long maxValue = new Long(Long.MAX_VALUE);
			getAcc().out(getMixerContainerTc(), removeIngredientsRequest, maxValue);
		}
	}

	public boolean deleteRecipe(Recipe recipe) throws Exception {
		String nameTemplate = AbstractCommunication.getRecipe();

		String ingredients = getStringListIngredients(recipe.getIngredients());

		// recipe(id,name,cookingTime,temperature,ingredients(ingredient1(name,quantity),ingredient2(name,quantity),...,ingredientN(name,quantity)))
		String tuple = nameTemplate + "(" + recipe.getId() + "," + recipe.getName() + "," + recipe.getCookingTime()
				+ "," + recipe.getTemperature() + "," + ingredients + ")";

		LogicTuple template = LogicTuple.parse(tuple);
		Long maxValue = new Long(Long.MAX_VALUE);
		ITucsonOperation operation = getAcc().inp(getMixerTc(), template, maxValue);
		return (operation.isResultSuccess());
	}

	public boolean saveRecipe(Recipe recipe) throws Exception {
		String nameTemplate = AbstractCommunication.getRecipe();
		
		recipe.setId(getNewRecipeId());
		String ingredients = getStringListIngredients(recipe.getIngredients());

		// recipe(id,name,cookingTime,temperature,ingredients(ingredient(name1,qta1),ingredient(name2,qta2),...))
		String tuple = nameTemplate + "(" + recipe.getId() + "," + recipe.getName() + "," + recipe.getCookingTime()
				+ "," + recipe.getTemperature() + "," + ingredients + ")";

		LogicTuple template = LogicTuple.parse(tuple);
		Long maxValue = new Long(Long.MAX_VALUE);
		ITucsonOperation operation = getAcc().out(getMixerTc(), template, maxValue);
		return (operation.isResultSuccess());
	}

	private int getNewRecipeId() throws Exception {
		String nameTemplate = AbstractCommunication.getRecipe();
		int id = -1;
		ITucsonOperation operation = null;

		do {
			id = id + 1;
			// recipe(id,*,*,*,*,*)
			String tuple = nameTemplate + "(" + id + "," + "A,B,C,D,E)";
			LogicTuple template = LogicTuple.parse(tuple);
			Long maxValue = new Long(Long.MAX_VALUE);
			operation = getAcc().rdp(getMixerTc(), template, maxValue);
		} while (operation.isResultSuccess());

		return (id);
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
	
	private void onStateChanged() {
		for ( StateChangeListener listener : getStateListeners() )
			listener.onStateChanged();
	}
	
	private void onRecipesChanged() {
		for ( RecipesChangeListener listener : getRecipesListeners() )
			listener.onRecipesChanged();
	}
	
	public boolean insertOrder(List<Ingredient> ingredients) {
//		String stringIngredients = "";
//		for (Ingredient ingredient : ingredients)
//			stringIngredients = stringIngredients + ingredient.getName() + "-" + ingredient.getQuantity() + ";";
//		stringIngredients = stringIngredients.substring(0, stringIngredients.length() - 1);
//		
//		String nameTemplate = "buy";
//		
//		int orderId = getNewOrderId();
//		
//		Date now = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd'_'hh.mm.ss");
//		
//		String tuple = nameTemplate + "(" + orderId + "," + stringIngredients + "," + formatter.format(now) + ")";
//		LogicTuple template = LogicTuple.parse(tuple);
//		ITucsonOperation operation = getAcc().out(getShopperTc(), template, Long.MAX_VALUE);
//		
//		return (operation.isResultSuccess());
		
		return (true);
	}
	
//	private int getNewOrderId() throws Exception {
//		String nameTemplate = "buy";
//		int id = -1;
//		ITucsonOperation operation = null;
//
//		do {
//			id = id + 1;
//			// buy(id,*,*)
//			String tuple = nameTemplate + "(" + id + "," + "A,B)";
//			LogicTuple template = LogicTuple.parse(tuple);
//			Long maxValue = new Long(Long.MAX_VALUE);
//			operation = getAcc().rdp(getShopperTc(), template, maxValue);
//		} while (operation.isResultSuccess());
//
//		return (id);
//	}

	public void show() {
		getMixerPanel().init();
	}

	public void hide() {
		getMixerPanel().hidePanel();
	}

}
