package it.unibo.homemanager.userinterfaces.agents;

import java.util.List;

import javax.swing.JTable;

import it.unibo.homemanager.agents.Recipe;

public class RecipesTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	public RecipesTable() {
		setModel(new RecipesTableModel());
	}

	public void setData(List<Recipe> data) {
		RecipesTableModel tableModel = (RecipesTableModel) getModel();
		tableModel.setRows(data);
	}
	
	public Recipe getSelectedItem() {
		RecipesTableModel tableModel = (RecipesTableModel) getModel();
		int index = getSelectedRow();
		if ( index == -1 )
			return (null);
		return (tableModel.getRow(index));
	}
	
}
