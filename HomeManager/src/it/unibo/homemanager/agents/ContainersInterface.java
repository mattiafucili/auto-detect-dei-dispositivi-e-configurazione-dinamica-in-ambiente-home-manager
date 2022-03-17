package it.unibo.homemanager.agents;

public interface ContainersInterface {

	public void getContent() throws Exception;
	public boolean addIngredient(Ingredient ingredient) throws Exception;
	public boolean updateContent(Ingredient ingredient) throws Exception;
	
}
