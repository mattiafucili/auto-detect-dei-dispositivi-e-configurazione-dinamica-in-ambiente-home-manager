/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.homemanager.userinterfaces;

import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.detection.AgentManager;
import it.unibo.homemanager.meteo.MeteoAgent;
import it.unibo.homemanager.tablemap.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.ListModel;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.Color;

import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JList;

/**
 *
 * @author Alessandro
 */
public class ViewMeteoAgentPanel extends javax.swing.JPanel {

	private ViewManageAgentPanel butlerPanel;
	private MeteoAgent meteoAgent;
	private TracePanel tp;
	private static List<String> siti;
	

	public ViewMeteoAgentPanel(ViewManageAgentPanel maPanel, MeteoAgent meteoAgent, TracePanel tp) {
		setPreferredSize(new Dimension(538, 343));
		setMinimumSize(new Dimension(538, 343));
		setMaximumSize(new Dimension(538, 343));
		this.butlerPanel=maPanel;
        this.meteoAgent=meteoAgent;
        this.tp=tp;
        
        
        
        JLabel lblMeteoAgent = new JLabel("Weather Agent");
        lblMeteoAgent.setBounds(20, 12, 150, 21);
        lblMeteoAgent.setFont(new Font("Courier New", Font.BOLD, 18));
        
        JButton jButton4 = new JButton();
        jButton4.setBounds(240, 11, 109, 25);
        jButton4.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		backButtonActionPerformed(arg0);
        	}
        });
        jButton4.setText("Back");
        jButton4.setForeground(new Color(102, 0, 102));
        jButton4.setFont(new Font("Courier New", Font.BOLD, 14));
        
        JButton jButton3 = new JButton();
        jButton3.setBounds(367, 11, 110, 25);
        jButton3.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		menuButtonActionPerformed(e);
        	}
        });
        jButton3.setText("Menu");
        jButton3.setForeground(new Color(102, 0, 102));
        jButton3.setFont(new Font("Courier New", Font.BOLD, 14));
        
        JLabel lblSelezionaSitoMeteo = new JLabel("Weather site");
        lblSelezionaSitoMeteo.setBounds(136, 51, 94, 14);
        lblSelezionaSitoMeteo.setFont(new Font("Courier New", Font.PLAIN, 11));
        
        siticomboBox = new JComboBox();
        siticomboBox.setBounds(240, 47, 237, 20);
       
        
        
        
        lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(216, 70, 100, 132);
        
        
        lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setBounds(306, 63, 99, 127);
        
        
        label = new JLabel("");
        label.setBounds(392, 87, 100, 100);
        
        
        
        scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 192, 508, 43);
        
        
        scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(20, 246, 508, 43);
        
        scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(20, 300, 508, 43);
        
        table_2 = new JTable();
        scrollPane_2.setViewportView(table_2);
        
        table_1 = new JTable();
        scrollPane_1.setViewportView(table_1);
        setLayout(null);
        
        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateColumnsFromModel(false);
        scrollPane.setViewportView(table);
        add(scrollPane);
        add(scrollPane_1);
        add(scrollPane_2);
        add(lblMeteoAgent);
        add(jButton4);
        add(jButton3);
        add(lblSelezionaSitoMeteo);
        add(lblNewLabel);
        add(lblNewLabel_1);
        add(siticomboBox);
        add(label);
        
        JLabel lblShuttersActions = new JLabel("Shutters actions");
        lblShuttersActions.setFont(new Font("Courier New", Font.PLAIN, 11));
        lblShuttersActions.setBounds(23, 103, 121, 14);
        add(lblShuttersActions);
        
        listModel=new DefaultListModel();
        JList actions=new JList(listModel);
        actions.setBounds(20, 117, 173, 35);
        add(actions);
	}
	
	private JComboBox siticomboBox;
	private JTable table;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JTable table_1;
	private JTable table_2;
	private DefaultListModel listModel;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel label;

	private void sitiComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jlistAgentComboBoxActionPerformed
        if(siti.contains(siticomboBox.getSelectedItem())) 
        {
        	String site="www.google.it";
        	Socket sock=new Socket();
    		InetSocketAddress addr=new InetSocketAddress(site,80);
    		try {
					sock.connect(addr, 3000);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		if(sock.isConnected()){
        	//cerca nel centro di tuple e stampa fuori le tuple
        	String[] columnNames = meteoAgent.getColumnNames(""+siticomboBox.getSelectedItem());
        	if(columnNames!=null)
        	{
        		Object[][] data1 = meteoAgent.getData1(""+siticomboBox.getSelectedItem());
        		Object[][] data2 = meteoAgent.getData2(""+siticomboBox.getSelectedItem());
        		Object[][] data3 = meteoAgent.getData3(""+siticomboBox.getSelectedItem());
        		String scelto=""+siticomboBox.getSelectedItem();
        		
        		if(scelto.equals(siti.get(1)))//google
        		{
        			table = new JTable(data1, Arrays.copyOfRange(columnNames, 0, 6));
        			scrollPane.setViewportView(table);
        			
        			table_1 = new JTable(data2, Arrays.copyOfRange(columnNames, 6, 8));
        			scrollPane_1.setViewportView(table_1);
        			
        			scrollPane_2.setViewportView(null);
        		}
        		if(scelto.equals(siti.get(2)))//yahoo
        		{
        			table = new JTable(data1, Arrays.copyOfRange(columnNames, 0, 4));
        			scrollPane.setViewportView(table);
        			
        			table_1 = new JTable(data2, Arrays.copyOfRange(columnNames, 4 ,10 ));
        			scrollPane_1.setViewportView(table_1);
        			
        			table_2 = new JTable(data3, Arrays.copyOfRange(columnNames,10,11)); //da controllare!!!
        			scrollPane_2.setViewportView(table_2);
        		}
        		if(scelto.equals(siti.get(0)))//openweathermap
        		{
        			table = new JTable(data1, Arrays.copyOfRange(columnNames, 0, 4));
        			scrollPane.setViewportView(table);
        			
        			table_1 = new JTable(data2, Arrays.copyOfRange(columnNames, 4 ,10 ));
        			scrollPane_1.setViewportView(table_1);
        			
        			table_2 = new JTable(data3, Arrays.copyOfRange(columnNames,10,12));
        			scrollPane_2.setViewportView(table_2);
        		}
        		
        		
        		
        		/*if(data1!=null)
        		{
        			table = new JTable(data1, Arrays.copyOfRange(columnNames, 0, 6));
        			scrollPane.setViewportView(table);
        		}
        		if(data2!=null)
        		{
        			table_1 = new JTable(data2, Arrays.copyOfRange(columnNames, 6,meteoAgent.getIndex(""+siticomboBox.getSelectedItem()) ));
        			scrollPane_1.setViewportView(table_1);
        		}
        		if(data3!=null)
        		{
        			table_2 = new JTable(data3, Arrays.copyOfRange(columnNames,meteoAgent.getIndex(""+siticomboBox.getSelectedItem()), columnNames.length));
        			scrollPane_2.setViewportView(table_2);
        		}
        		else
        		{
        			scrollPane_2.setViewportView(null);
        		}*/
        		
        	}
    		
        	}
        	else
    		{
    			JOptionPane.showMessageDialog(this, "Error: can't connect to network");
    		}
        
        }
        
    }//GEN-LAST:event_jlistAgentComboBoxActionPerformed
	

	private void menuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuButtonActionPerformed
        this.hidePanel();
        butlerPanel.menu();
    }//GEN-LAST:event_menuButtonActionPerformed
	
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        this.hidePanel();
        butlerPanel.showPanel();
	}//GEN-LAST:event_backButtonActionPerformed
	
	public void hidePanel() {
	        if(isVisible())
	            setVisible(false);
	    }
	     
	public void init() {
	        if(!isVisible())
	        {
	        	setVisible(true);
	        	String[] ar=meteoAgent.getShuttersActions();   //PREPARARE REAZIONE
	        	if(ar!=null){
	        	listModel.addElement(ar[0]);
	        	listModel.addElement(ar[1]);
	        	}
	        	
	        	//inizializzo la lista dei siti
	            siti=meteoAgent.getSiti();
	            siticomboBox.removeAllItems();
	            for(String s : siti)
	            {
	            	siticomboBox.addItem(s);
	            }
	            siticomboBox.setSelectedIndex(-1);
	            siticomboBox.addActionListener(new java.awt.event.ActionListener() {
	                public void actionPerformed(java.awt.event.ActionEvent evt) {
	                    sitiComboBoxActionPerformed(evt);
	                }
	            });
	            
	            List<String> icons=meteoAgent.getIcon();
	            lblNewLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(icons.get(0))));
	            lblNewLabel_1.setIcon(new javax.swing.ImageIcon(getClass().getResource(icons.get(1))));
	            label.setIcon(new javax.swing.ImageIcon(getClass().getResource(icons.get(2))));
	        }
	        
	    }
}
