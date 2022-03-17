package it.unibo.homemanager.userinterfaces.agents;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import it.unibo.homemanager.agents.Recipe;

public class RecipesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private String[] columns;
	private List<Recipe> rows;
	
	public RecipesTableModel() {
		columns = new String[] {"Name", "Cooking time", "Temperature"};
		rows = new ArrayList<Recipe>();
	}

	public String[] getColumns() {
		return (columns);
	}
	
	@Override
	public String getColumnName(int index) {
		return (getColumns()[index]);
	}
	
	public int getColumnCount() {
		return (getColumns().length);
	}

	public List<Recipe> getRows() {
		return (rows);
	}
	
	public Recipe getRow(int index) {
		return (getRows().get(index));
	}
	
	public void setRows(List<Recipe> rows) {
		this.rows = rows;
		fireTableDataChanged();
	}
	
	public int getRowCount() {
		return (rows.size());
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Recipe recipe = getRows().get(rowIndex);
		switch (columnIndex) {
		case 0:
			return (recipe.getName());
		case 1: 
			return (recipe.getCookingTime());
		case 2:
			return (recipe.getTemperature());
		default:
			return (null);
		}
	}

}
