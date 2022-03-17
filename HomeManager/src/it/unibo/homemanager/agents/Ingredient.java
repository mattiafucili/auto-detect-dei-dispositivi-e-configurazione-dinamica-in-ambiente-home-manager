package it.unibo.homemanager.agents;

public class Ingredient {
	private String name;
	private int quantity;

	public Ingredient() {
		this("xxx", -1);
	}
	
	public Ingredient(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}

	public String getName() {
		return (name);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return (quantity);
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Ingredient){
			Ingredient that = (Ingredient) obj;
			return ( getName().equalsIgnoreCase(that.getName()) );
		}
		return (false);
	}

	public String toString() {
		return (getName() + ", " + getQuantity() + "\n");
	}
}