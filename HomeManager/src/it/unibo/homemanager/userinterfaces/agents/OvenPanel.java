package it.unibo.homemanager.userinterfaces.agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import it.unibo.homemanager.agents.OvenAgent;
import it.unibo.homemanager.agents.StateChangeListener;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;

public class OvenPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;

	private OvenAgent ovenAgent;
	private ViewManageAgentPanel butlerPanel;
	
	private JButton buttonBack;
	private JButton buttonMenu;
	private JTextPane textRecipe;

	public OvenPanel(ViewManageAgentPanel butlerPanel, OvenAgent ovenAgent, TracePanel tracePanel) {
		this.ovenAgent = ovenAgent;
		this.butlerPanel = butlerPanel;
		
		initComponents();
		initGUI();
		
		getOvenAgent().addListener(new StateChangeListener() {
			@Override
			public void onStateChanged() {
				onStateChangedEvent();
			}
		});
		onStateChangedEvent();
	}

	public OvenAgent getOvenAgent() {
		return (ovenAgent);
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

	public JTextPane getTextRecipe() {
		return (textRecipe);
	}
	private void initComponents() {
		buttonBack = new JButton("Back");
		buttonMenu = new JButton("Menu");
		textRecipe = new JTextPane();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JLabel labelTitle = new JLabel("Name: " + getOvenAgent().getName());
		getButtonBack().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				onClickButtonBack(event);
			}
		});
		getButtonMenu().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				onClickButtonMenu(event);
			}
		});
		
		titlePanel.add(labelTitle);
		titlePanel.add(getButtonBack());
		titlePanel.add(getButtonMenu());
		
		getTextRecipe().setEditable(false);
		getTextRecipe().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		StyledDocument document = getTextRecipe().getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		document.setParagraphAttributes(0, document.getLength(), center, false);
		
		JScrollPane textRecipeScrollPane = new JScrollPane(getTextRecipe());
		textRecipeScrollPane.setPreferredSize(new Dimension(300, 250));
		textRecipeScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JPanel manageOvenPanel = new JPanel();
		manageOvenPanel.setLayout(new BoxLayout(manageOvenPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(titlePanel);
		mainPanel.add(textRecipeScrollPane);
		
		add(mainPanel);
	}
	
	private void onClickButtonBack(ActionEvent event) {
		hidePanel();
		getButlerPanel().menu();
	}

	private void onClickButtonMenu(ActionEvent event) {
		hidePanel();
		getButlerPanel().showPanel();
	}
	
	private void onStateChangedEvent() {
		if ( getOvenAgent().getState() )
			getTextRecipe().setText("State: ON");
		else // if ( !getOvenAgent().getState() )
			getTextRecipe().setText("State: OFF");
		
		getTextRecipe().setText(getTextRecipe().getText() + System.lineSeparator() + System.lineSeparator());
		
		if ( getOvenAgent().getCurrentRecipe() != null )
			getTextRecipe().setText( getTextRecipe().getText() + getOvenAgent().getCurrentRecipe().toString() );
		else // if (getOvenAgent().getCurrentRecipe() == null)
			getTextRecipe().setText(getTextRecipe().getText() + "");
	}

	public void init() {
		setVisible(true);
	}

	public void hidePanel() {
		setVisible(false);
	}
}
