package it.unibo.homemanager.communication;

public abstract class AbstractCommunication {

	private static final String new_name = "new_name";
	private static final String info_device = "info_device";
	private static final String device = "device";
	private static final String state_change = "state_change";
	private static final String presence_information = "presence_information";
	private static final String order = "order";
	private static final String request = "request";
	private static final String response = "respone";
	private static final String max = "max";
	private static final String cooking = "cooking";
	private static final String recipe = "recipe";
	private static final String check_recipe = "check_recipe";
	private static final String remove_ingredients = "remove_ingredients";
	private static final String ingredients = "ingredients";
	private static final String ingredient = "ingredient";
	
	public static String getNewName() {
		return new_name;
	}
	
	public static String getInfoDevice() {
		return info_device;
	}
	
	public static String getDevice() {
		return device;
	}

	public static String getOrder() {
		return order;
	}

	public static String getRequest() {
		return request;
	}

	public static String getResponse() {
		return response;
	}

	public static String getStateChange() {
		return state_change;
	}

	public static String getPresenceInformation() {
		return presence_information;
	}

	public static String getMax() {
		return max;
	}

	public static String getCooking() {
		return cooking;
	}

	public static String getRecipe() {
		return recipe;
	}
	
	public static String getCheckRecipe() {
		return check_recipe;
	}

	public static String getRemoveIngredients() {
		return remove_ingredients;
	}

	public static String getIngredients() {
		return ingredients;
	}

	public static String getIngredient() {
		return ingredient;
	}
}
