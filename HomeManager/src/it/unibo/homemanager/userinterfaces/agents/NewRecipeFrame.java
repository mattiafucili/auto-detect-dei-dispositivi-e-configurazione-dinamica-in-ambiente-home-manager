package it.unibo.homemanager.userinterfaces.agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.unibo.homemanager.agents.Ingredient;
import it.unibo.homemanager.agents.Recipe;

public class NewRecipeFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTextField textRecipeName;
	private JTextField textCookingTime;
	private JTextField textTemperature;
	private JTextField textIngredient;
	private JTextField textQuantity;
	private JButton buttonAddIngredient;
	private JButton buttonRemoveIngredient;
	private JList<Ingredient> listIngredients;
	private DefaultListModel<Ingredient> ingredientsListModel;
	private JButton buttonSaveRecipe;
	private JButton buttonCookWithoutSaving;
	
	private Recipe recipe;
	private int action; // 0: save, 1: cook without saving
	
	public NewRecipeFrame() {
		initComponents();
		init();
		
		recipe = new Recipe();
		action = -1;
	}

	public JTextField getTextRecipeName() {
		return (textRecipeName);
	}

	public JTextField getTextCookingTime() {
		return (textCookingTime);
	}

	public JTextField getTextTemperature() {
		return (textTemperature);
	}

	public JTextField getTextIngredient() {
		return (textIngredient);
	}

	public JTextField getTextQuantity() {
		return (textQuantity);
	}

	public JButton getButtonAddIngredient() {
		return (buttonAddIngredient);
	}

	public JButton getButtonRemoveIngredient() {
		return (buttonRemoveIngredient);
	}

	public JList<Ingredient> getListIngredients() {
		return (listIngredients);
	}

	public DefaultListModel<Ingredient> getIngredientsListModel() {
		return (ingredientsListModel);
	}

	public JButton getButtonSaveRecipe() {
		return (buttonSaveRecipe);
	}

	public JButton getButtonCookWithoutSaving() {
		return (buttonCookWithoutSaving);
	}

	public Recipe getRecipe() {
		return (recipe);
	}

	public int getAction() {
		return (action);
	}

	private void initComponents() {
		textRecipeName = new JTextField(10);
		textCookingTime = new JTextField(10);
		textTemperature = new JTextField(10);
		textIngredient = new JTextField(15);
		textQuantity = new JTextField(15);
		buttonAddIngredient = new JButton("Add ingredient");
		buttonRemoveIngredient = new JButton("Remove ingredient");
		listIngredients = new JList<Ingredient>();
		ingredientsListModel = new DefaultListModel<Ingredient>();
		buttonSaveRecipe = new JButton("Save");
		buttonCookWithoutSaving = new JButton("Cook without saving");
	}
	
	private void init() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topPanel.setLayout(new GridLayout(3, 2, 0, 10));
		
		JLabel labelRecipeName = new JLabel("Name: ");
		getTextRecipeName().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		
		JLabel labelCookingTime = new JLabel("Cooking time: ");
		getTextCookingTime().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

		JLabel labelTemperature = new JLabel("Temperature: ");
		getTextTemperature().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		
		topPanel.add(labelRecipeName);
		topPanel.add(getTextRecipeName());
		topPanel.add(labelCookingTime);
		topPanel.add(getTextCookingTime());
		topPanel.add(labelTemperature);
		topPanel.add(getTextTemperature());
		
		mainPanel.add(topPanel);
		
		getListIngredients().setModel(getIngredientsListModel());
		JScrollPane listIngredientsScrollPane = new JScrollPane(getListIngredients());
		listIngredientsScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(3, 3, 3, 3)));
		listIngredientsScrollPane.setSize(new Dimension(300, 200));
		mainPanel.add(listIngredientsScrollPane);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		middlePanel.setLayout(new GridLayout(2, 2, 0, 10));
		
		JLabel labelIngredientName = new JLabel("Ingredient name: ");
		getTextIngredient().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		
		JLabel labelIngredientQuantity = new JLabel("Ingredient quantity: ");
		getTextQuantity().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		
		middlePanel.add(labelIngredientName);
		middlePanel.add(getTextIngredient());
		middlePanel.add(labelIngredientQuantity);
		middlePanel.add(getTextQuantity());
		
		mainPanel.add(middlePanel);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getButtonAddIngredient().setFocusPainted(false);
		getButtonAddIngredient().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonAddIngredient(event);
			}
		});
		bottomPanel.add(getButtonAddIngredient());
		getButtonRemoveIngredient().setFocusPainted(false);
		getButtonRemoveIngredient().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonRemoveIngredient(event);
			}
		});
		bottomPanel.add(getButtonRemoveIngredient());
		
		mainPanel.add(bottomPanel);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		getButtonSaveRecipe().setFont(new Font(getButtonSaveRecipe().getFont().getName(), getButtonSaveRecipe().getFont().getStyle(), 16));
		getButtonSaveRecipe().setFocusPainted(false);
		getButtonSaveRecipe().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonSaveRecipe(event);
			}
		});
		getButtonCookWithoutSaving().setFont(new Font(getButtonSaveRecipe().getFont().getName(), getButtonCookWithoutSaving().getFont().getStyle(), 16));
		getButtonCookWithoutSaving().setFocusPainted(false);
		getButtonCookWithoutSaving().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonCookWithoutSaving(event);
			}
		});
		
		controlPanel.add(getButtonSaveRecipe());
		controlPanel.add(getButtonCookWithoutSaving());
		mainPanel.add(controlPanel);
		
		getContentPane().add(mainPanel);
		
		setTitle("New recipe...");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		pack();
		setResizable(false);
		
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		int screenWidth = (int) defaultToolkit.getScreenSize().getWidth();
		int screenHeight = (int) defaultToolkit.getScreenSize().getHeight();
		
		int windowWidth = (int) getSize().getWidth();
		int windowHeight = (int) getSize().getHeight();
		
		int x = (screenWidth - windowWidth) / 2;
		int y = (screenHeight - windowHeight) / 2;
		setLocation(x, y);
	}

	private void onClickButtonAddIngredient(ActionEvent event) {
		try {
			String name = getTextIngredient().getText();
			if ( name.isEmpty() )
				JOptionPane.showMessageDialog(this, "Invalid ingredient name!");
			else { // if (!name.isEmpty())
				int quantity = Integer.parseInt(getTextQuantity().getText());
				Ingredient toAdd = new Ingredient(name, quantity);
				getRecipe().addIngredient(toAdd);
				getIngredientsListModel().clear();
				for (Ingredient ingredient : recipe.getIngredients())
					getIngredientsListModel().addElement(ingredient);
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Invalid ingredient quantity!");
		}
		resetIngredient();
	}
	
	private void resetIngredient() {
		getTextIngredient().setText("");
		getTextQuantity().setText("");
		getTextIngredient().requestFocus();
	}

	private void onClickButtonRemoveIngredient(ActionEvent event) {
		Ingredient selected = getListIngredients().getSelectedValue();
		if ( selected != null )
			getIngredientsListModel().removeElement(selected);
	}
	
	private void onClickButtonSaveRecipe(ActionEvent event) {
		setRecipe(0);
	}
	
	private void onClickButtonCookWithoutSaving(ActionEvent event) {
		setRecipe(1);
	}
	
	private void setRecipe(int action) {
		String name = getTextRecipeName().getText();
		if ( name.isEmpty() ) {
			JOptionPane.showMessageDialog(this, "Invalid recipe name!");
			getTextRecipeName().requestFocus();
		}
		else {
			try {
				int cookingTime = Integer.parseInt(getTextCookingTime().getText());
				try {
					int temperature = Integer.parseInt(getTextTemperature().getText());
					
					List<Ingredient> ingredients = new ArrayList<Ingredient>();
					for ( int i = 0; i < getIngredientsListModel().getSize(); i++ )
						ingredients.add(getIngredientsListModel().getElementAt(i));
					
					if (ingredients.size() == 0)
						JOptionPane.showMessageDialog(this, "Recipe without ingredients!");
					else { // if (ingredients.size() > 0)
						getRecipe().setName(name);
						getRecipe().setCookingTime(cookingTime);
						getRecipe().setTemperature(temperature);
						this.action = action;
						
						dispose();
					}
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Invalid temperature!");
					getTextTemperature().setText("");
					getTextTemperature().requestFocus();
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid cooking time!");
				getTextCookingTime().setText("");
				getTextCookingTime().requestFocus();
			}
		}
	}
}
