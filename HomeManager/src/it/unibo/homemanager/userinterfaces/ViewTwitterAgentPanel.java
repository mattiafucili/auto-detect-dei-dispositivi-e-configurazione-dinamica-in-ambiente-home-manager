package it.unibo.homemanager.userinterfaces;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import alice.logictuple.LogicTuple;
import alice.logictuple.Var;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import it.unibo.homemanager.social.TwitterAgent;
import it.unibo.homemanager.userinterfaces.TracePanel;

public class ViewTwitterAgentPanel extends JPanel 
{
	
	private ViewManageAgentPanel butlerPanel;
	private TwitterAgent twitterAgent;
	private TracePanel tp;

	/**
	 * Create the panel.
	 */
	public ViewTwitterAgentPanel(ViewManageAgentPanel maPanel, TwitterAgent twitterAgent, TracePanel tp) 
	{
		setBackground(new java.awt.Color(204, 204, 255));
		setPreferredSize(new Dimension(538, 343));
		setMinimumSize(new Dimension(538, 343));
		setMaximumSize(new Dimension(538, 343));
		this.butlerPanel=maPanel;
        this.twitterAgent=twitterAgent;
        this.tp=tp;
		setLayout(null);
		
		JLabel lblTwitter = new JLabel("Twitter");
		lblTwitter.setFont(new Font("Courier New", Font.BOLD, 14));
		lblTwitter.setBounds(37, 25, 67, 28);
		add(lblTwitter);
		
		JButton btnMen = new JButton("Men\u00F9");
		btnMen.setFont(new Font("Courier New", Font.BOLD, 12));
		btnMen.setBounds(289, 29, 89, 23);
		btnMen.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		menuButtonActionPerformed(e);
        	}
        });
		add(btnMen);
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Courier New", Font.BOLD, 12));
		btnBack.setBounds(392, 29, 89, 23);
		btnBack.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		backButtonActionPerformed(arg0);
        	}
        });
		add(btnBack);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(37, 140, 444, 163);
		add(scrollPane);
		
		txtRisultati = new JTextArea();
		//txtrRisultati.setText("Risultati");
		scrollPane.setViewportView(txtRisultati);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Courier New", Font.BOLD, 12));
		btnSearch.setBounds(343, 106, 89, 23);
		btnSearch.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		searchButtonActionPerformed(arg0);
        	}
        });
		add(btnSearch);

	}
	
	private JTextArea txtRisultati;
	
	private void menuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
        this.hidePanel();
        butlerPanel.menu();
    }
	
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
        this.hidePanel();
        butlerPanel.showPanel();
	}
	
	private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) 
	{
		this.txtRisultati.setText("");
		try 
		{
			this.twitterAgent.saveTweets();
		} 
		catch (TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) 
		{
			e.printStackTrace();
		}
		
		ArrayList<String> tweets = this.twitterAgent.readTweets();
    	for(int i=0; i<tweets.size(); i++)
    	{
    		this.txtRisultati.append(tweets.get(i)+"\n");
    	}
	}
	
	public void hidePanel() 
	{
	        if(isVisible())
	            setVisible(false);
	    }
	     
	public void init() 
	{
	        if(!isVisible())
	        {
	        	setVisible(true);
	        }
	        
	}
	
	
	
}
