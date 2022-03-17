package it.unibo.homemanager.userinterfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import it.unibo.homemanager.agents.TucsonAgentInterface;
import it.unibo.homemanager.detection.AgentManager;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.detection.DeviceListener;
import it.unibo.homemanager.detection.DeviceManagerAgent;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.userinterfaces.MainPanel;

public class ViewManageAgentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private MainPanel mainPanel;
	private User user;
	
	private JLabel labelTitle;
	public JComboBox<String> comboBoxAgents;
	private JButton buttonMenu;
	private JButton buttonProperties;

	public ViewManageAgentPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;

		initComponents();
		
		try {
			DeviceManagerAgent deviceManagerAgent = DeviceManagerAgent.getInstance();
			deviceManagerAgent.addListener(new DeviceListener() {
				@Override
				public void onNewDevice(Device device) {
					onNewDeviceEvent(device);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		buttonMenu = new JButton();
		labelTitle = new JLabel();
		comboBoxAgents = new JComboBox<String>();
		buttonProperties = new JButton();

		setBackground(new Color(204, 204, 255));
		setMaximumSize(new Dimension(538, 343));
		setMinimumSize(new Dimension(538, 343));

		buttonMenu.setFont(new Font("Courier New", 1, 14));
		buttonMenu.setText("Menu");
		buttonMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				onClickMenuButton(event);
			}
		});

		labelTitle.setFont(new Font("Courier New", 1, 24));
		labelTitle.setForeground(new Color(0, 51, 255));
		labelTitle.setText("Manage Agents");

		comboBoxAgents.setModel(
				new DefaultComboBoxModel<String>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		comboBoxAgents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onSelectedItemChangedComboBoxAgents(event);
			}
		});

		buttonProperties.setFont(new Font("Courier New", 1, 14));
		buttonProperties.setText("Properties");
		buttonProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickPropertiesButton(event);
			}
		});

		javax.swing.GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(37, 37, 37)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(labelTitle, GroupLayout.PREFERRED_SIZE, 188,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(comboBoxAgents, GroupLayout.PREFERRED_SIZE, 162,
										GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(buttonMenu, GroupLayout.Alignment.TRAILING,
								GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
						.addComponent(buttonProperties, GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
				.addGap(67, 67, 67)));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(30, 30, 30)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(buttonMenu).addComponent(labelTitle, GroupLayout.PREFERRED_SIZE,
										51, GroupLayout.PREFERRED_SIZE))
				.addGap(91, 91, 91)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(comboBoxAgents, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(buttonProperties)).addContainerGap(146, Short.MAX_VALUE)));
	}

	protected void init(User user) {
		this.user = user;
		fillComboBoxAgents();
		setVisible(true);
	}

	public void showPanel() {
		mainPanel.loadButlerPanel();
	}
	
	protected void hidePanel() {
		setVisible(false);
	}

	private void onClickMenuButton(ActionEvent event) {
		menu();
	}
	
	public void menu() {
		mainPanel.loadInitRolePanel(user);
	}

	private void onSelectedItemChangedComboBoxAgents(ActionEvent event) {
		buttonProperties.setEnabled( comboBoxAgents.getSelectedItem() != null );
	}

	private void onClickPropertiesButton(ActionEvent event) {
		String selectedItem = comboBoxAgents.getSelectedItem().toString();
		if (selectedItem != null) {
			String google = "www.google.it";
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress(google, 80);
			try {
				socket.connect(address, 3000);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				AgentManager agentManager = AgentManager.getInstance();
				TucsonAgentInterface agent = agentManager.getAgentByName(selectedItem);
				mainPanel.loadAgentPanel(agent);
			} else
				JOptionPane.showMessageDialog(this, "Error: can't connect to network");
		}
	}

	private void fillComboBoxAgents() {
		comboBoxAgents.removeAllItems();
		
		AgentManager agentManager = AgentManager.getInstance();
		ArrayList<String> agents = agentManager.getStringAgents();
		
		for (String agent : agents)
			comboBoxAgents.addItem(agent);
		
		comboBoxAgents.setSelectedIndex(-1);
	}
	
	private void onNewDeviceEvent(Device device) {
		if ( device.getDeviceType().equals("s") )
			comboBoxAgents.addItem(device.getDeviceName() + "_" + device.getDeviceId());
	}
}
