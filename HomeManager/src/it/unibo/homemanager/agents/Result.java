package it.unibo.homemanager.agents;

import java.util.List;

public class Result {
	private int id;
	private List<Ingredient> ingredients;
	
	public Result(int id, List<Ingredient> ingredients) {
		this.id = id;
		this.ingredients = ingredients;
	}

	public int getId() {
		return (id);
	}

	public List<Ingredient> getIngredients() {
		return (ingredients);
	}
}
