package it.unibo.homemanager.userinterfaces.agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import it.unibo.homemanager.agents.Ingredient;
import it.unibo.homemanager.agents.MixerAgent;
import it.unibo.homemanager.agents.Recipe;
import it.unibo.homemanager.agents.RecipesChangeListener;
import it.unibo.homemanager.agents.Result;
import it.unibo.homemanager.agents.StateChangeListener;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;

public class MixerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private MixerAgent mixerAgent;
	private ViewManageAgentPanel butlerPanel;
	
	private JButton buttonBack;
	private JButton buttonMenu;
	private RecipesTable tableRecipes;
	private JTextPane textRecipe;
	private JButton buttonCookRecipe;
	private JButton buttonDeleteRecipe;
	private JButton buttonNewRecipe;
	
	public MixerPanel(MixerAgent mixerAgent, ViewManageAgentPanel butlerPanel, TracePanel tracePanel) {
		this.mixerAgent = mixerAgent;
		this.butlerPanel = butlerPanel;
		
		initComponents();
		initGUI();
		
		getMixerAgent().addStateListener(new StateChangeListener() {
			@Override
			public void onStateChanged() {
				onStateChangedEvent();
			}
		});
		onStateChangedEvent();
		
		getMixerAgent().addRecipesListener(new RecipesChangeListener() {
			@Override
			public void onRecipesChanged() {
				onRecipesChangedEvent();
			}
		});
		onRecipesChangedEvent();
	}

	public MixerAgent getMixerAgent() {
		return (mixerAgent);
	}
	
	public ViewManageAgentPanel getButlerPanel() {
		return (butlerPanel);
	}

	public JButton getButtonBack() {
		return (buttonBack);
	}
	
	public JButton getButtonMenu() {
		return (buttonMenu);
	}

	public RecipesTable getTableRecipes() {
		return (tableRecipes);
	}

	public JTextPane getTextRecipe() {
		return (textRecipe);
	}

	public JButton getButtonCookRecipe() {
		return (buttonCookRecipe);
	}

	public JButton getButtonDeleteRecipe() {
		return (buttonDeleteRecipe);
	}

	public JButton getButtonNewRecipe() {
		return (buttonNewRecipe);
	}
	
	private void initComponents() {
		buttonBack = new JButton("Back");
		buttonMenu = new JButton("Menu");
		tableRecipes = new RecipesTable();
		textRecipe = new JTextPane();
		buttonCookRecipe = new JButton("Cook");
		buttonDeleteRecipe = new JButton("Delete");
		buttonNewRecipe = new JButton("New");
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		JLabel labelTitle = new JLabel("Name: " + getMixerAgent().getName());
		getButtonBack().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickButtonBack();
			}
		});
		getButtonMenu().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickButtonMenu();
			}
		});
		
		titlePanel.add(labelTitle);
		titlePanel.add(getButtonBack());
		titlePanel.add(getButtonMenu());
		
		JPanel recipesPanel = new JPanel();
		recipesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		recipesPanel.setLayout(new BoxLayout(recipesPanel, BoxLayout.Y_AXIS));

		// tableRecipes
		getTableRecipes().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				onListSelectionChanged(event);
			}
		});
		// ---
		
		// textRecipe
		getTextRecipe().setEditable(false);
		getTextRecipe().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		StyledDocument document = getTextRecipe().getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		document.setParagraphAttributes(0, document.getLength(), center, false);
		// ---
		
		// tableRecipesScrollPane
		JScrollPane tableRecipesScrollPane = new JScrollPane(getTableRecipes());
		tableRecipesScrollPane.setPreferredSize(new Dimension(400, 125));
		// ---
		
		// textRecipeScrollPane
		JScrollPane textRecipeScrollPane = new JScrollPane(getTextRecipe());
		textRecipeScrollPane.setPreferredSize(new Dimension(300, 135));
		textRecipeScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));	
		// ---
		
		// buttonCookRecipe
		getButtonCookRecipe().setFocusPainted(false);
		getButtonCookRecipe().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonCookRecipe(event);
			}
		});
		// ---
		
		// buttonDeleteRecipe
		getButtonDeleteRecipe().setFocusPainted(false);
		getButtonDeleteRecipe().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonDeleteRecipe(event);
			}
		});
		// ---
		
		// buttonNewRecipe
		getButtonNewRecipe().setFocusPainted(false);
		getButtonNewRecipe().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonNewRecipe(event);
			}
		});
		// ---
		
		// recipesInformationPanel
		JPanel recipesInformationPanel = new JPanel();
		recipesInformationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		recipesInformationPanel.setLayout(new BoxLayout(recipesInformationPanel, BoxLayout.Y_AXIS));
		// ---
		
		// recipesHandlerPanel
		JPanel recipesHandlerPanel = new JPanel();
		recipesHandlerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		recipesHandlerPanel.add(getButtonCookRecipe());
		recipesHandlerPanel.add(getButtonDeleteRecipe());
		recipesHandlerPanel.add(getButtonNewRecipe());
		// ---
		
		recipesInformationPanel.add(textRecipeScrollPane);
		recipesInformationPanel.add(recipesHandlerPanel);
		recipesPanel.add(titlePanel);
		recipesPanel.add(tableRecipesScrollPane);
		recipesPanel.add(recipesInformationPanel);
		mainPanel.add(recipesPanel);
		
		add(mainPanel);
	}

	private void onClickButtonBack() {
		hidePanel();
		getButlerPanel().menu();
	}

	private void onClickButtonMenu() {
		hidePanel();
		getButlerPanel().showPanel();
	}

	private void onListSelectionChanged(ListSelectionEvent e) {
		Recipe recipe = getTableRecipes().getSelectedItem();
		if ( recipe == null )
			getTextRecipe().setText("");
		else // if ( recipe != null )
			getTextRecipe().setText(recipe.toString());
	}

	private void onClickButtonCookRecipe(ActionEvent event) {
		Recipe recipe = getTableRecipes().getSelectedItem();
		if ( recipe != null )
			doCook(recipe);
	}
	
	private void onClickButtonDeleteRecipe(ActionEvent event) {
		Recipe recipe = getTableRecipes().getSelectedItem();
		if (recipe != null) {
			try {
				getMixerAgent().deleteRecipe(recipe);
				getMixerAgent().readAllRecipes();
				JOptionPane.showMessageDialog(this, "Recipe has been deleted!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onClickButtonNewRecipe(ActionEvent event) {
		NewRecipeFrame newRecipeFrame = new NewRecipeFrame();
		newRecipeFrame.setVisible(true);
		
		if ( newRecipeFrame.getAction() == 0 ) { // save recipe.
			try {
				getMixerAgent().saveRecipe(newRecipeFrame.getRecipe());
				getMixerAgent().readAllRecipes();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ( newRecipeFrame.getAction() == 1 ) // cook temp recipe.
			doCook(newRecipeFrame.getRecipe());
	}
	
	private void doCook(Recipe recipe) {
		try {
			Result result = getMixerAgent().prepareRecipe(recipe);
			if (result.getId() == 0)
				JOptionPane.showMessageDialog(this, "Request sent successfully!");
			if (result.getId() == -1)
				JOptionPane.showMessageDialog(this, "Mixer already on!");
			else if (result.getId() == -2)
				JOptionPane.showMessageDialog(this, "Mixer can't turn on");
			else if (result.getId() == -3) {
				String message = "";
				message = message + "Fridge doesn't contains the following ingredients: " + System.lineSeparator();
				for ( Ingredient ingredient : result.getIngredients() )
					message = message + " - " + ingredient.getName() + " " + "(" + ingredient.getQuantity() + ")" + System.lineSeparator();
				message = message + "Would you like to send a purchase order?";
				int option = JOptionPane.showConfirmDialog(this, message, "", JOptionPane.YES_NO_OPTION);
	    		if (option==JOptionPane.YES_OPTION)
	    			getMixerAgent().insertOrder(result.getIngredients());
			}
			else if (result.getId() == -4)
				JOptionPane.showMessageDialog(this, "No ovens available!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onStateChangedEvent() {
		getButtonCookRecipe().setEnabled( !getMixerAgent().getState() );
	}
	
	private void onRecipesChangedEvent() {
		List<Recipe> recipes = getMixerAgent().getCurrentRecipes();
		getTableRecipes().setData(recipes);
	}
	
	public void init() {
		setVisible(true);
	}

	public void hidePanel() {
		setVisible(false);
	}
}
