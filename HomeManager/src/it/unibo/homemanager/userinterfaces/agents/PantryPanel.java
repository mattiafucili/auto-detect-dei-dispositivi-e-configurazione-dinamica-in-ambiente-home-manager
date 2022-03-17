package it.unibo.homemanager.userinterfaces.agents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import it.unibo.homemanager.agents.Ingredient;
import it.unibo.homemanager.agents.IngredientsChangeListener;
import it.unibo.homemanager.agents.PantryAgent;
import it.unibo.homemanager.agents.StateChangeListener;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;

public class PantryPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;

	private PantryAgent pantryAgent;
	private ViewManageAgentPanel butlerPanel;

	public PantryPanel(ViewManageAgentPanel butlerPanel, PantryAgent pantryAgent, TracePanel tracePanel) {
		this.pantryAgent = pantryAgent;
		this.butlerPanel = butlerPanel;
		
		initComponents();
		
		getPantryAgent().addStateListener(new StateChangeListener() {	
			@Override
			public void onStateChanged() {
				onStateChangedEvent();
			}
		});
		onStateChangedEvent();
		
		getPantryAgent().addIngredientsListener(new IngredientsChangeListener() {	
			@Override
			public void onIngredientsChanged() {
				onIngredientsChangedEvent();
			}
		});
		onIngredientsChangedEvent();
	}
	
	public PantryAgent getPantryAgent() {
		return (pantryAgent);
	}
	
	public ViewManageAgentPanel getButlerPanel() {
		return (butlerPanel);
	}

	public void init() {
		setVisible(true);
	}

	public void hidePanel() {
		setVisible(false);
	}

	private void initComponents() {
		jButton4 = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		productNameText = new javax.swing.JTextField();
		quantityProductText = new javax.swing.JTextField();
		addToListButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jButton2 = new javax.swing.JButton();
		jLabel6 = new javax.swing.JLabel();
		quantityToDeleteTextField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		this.listModel1 = new DefaultListModel<Ingredient>();
		listProduct = new javax.swing.JList<Ingredient>(this.listModel1);
		JScrollPane listScrollPane = new JScrollPane();
		listScrollPane.getViewport().setView(listProduct);
		jLabel3 = new javax.swing.JLabel();
		jButton3 = new javax.swing.JButton();

		setBackground(new java.awt.Color(255, 255, 255));
		setMaximumSize(new java.awt.Dimension(538, 343));
		setMinimumSize(new java.awt.Dimension(538, 343));
		setPreferredSize(new java.awt.Dimension(538, 343));

		jButton4.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jButton4.setForeground(new java.awt.Color(102, 0, 102));
		jButton4.setText("Back");
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onClickButtonBack(evt);
			}
		});

		jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Pantry.jpg"))); // NOI18N
		jLabel4.setText("jLabel4");

		jLabel5.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
		jLabel5.setText("Pantry");

		jPanel1.setBackground(new java.awt.Color(51, 51, 255));
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Insert Product",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Courier New", 1, 12), new java.awt.Color(255, 255, 255))); // NOI18N
		jPanel1.setToolTipText("");

		productNameText.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		productNameText.setName(""); // NOI18N
		productNameText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				productNameTextMousePressed(evt);
			}
		});

		quantityProductText.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		quantityProductText.setName("quantityProductText"); // NOI18N
		quantityProductText.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				quantityProductTextMousePressed(event);
			}
		});

		addToListButton.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		addToListButton.setForeground(new java.awt.Color(0, 0, 255));
		addToListButton.setText("Add to list");
		addToListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onClickButtonAdd(event);
			}
		});

		jLabel1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jLabel1.setForeground(new java.awt.Color(255, 255, 255));
		jLabel1.setText("Name Product");

		jLabel2.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(255, 255, 255));
		jLabel2.setText("Quantity");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(productNameText))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(quantityProductText))
						.addGap(19, 19, 19))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel1Layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(addToListButton).addGap(55, 55, 55)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(jLabel2))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(productNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(quantityProductText, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(addToListButton).addGap(20, 20, 20)));

		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Delete Product",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Courier New", 0, 12), new java.awt.Color(255, 0, 0))); // NOI18N
		jPanel2.setForeground(new java.awt.Color(255, 0, 0));

		jButton2.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jButton2.setForeground(new java.awt.Color(255, 0, 0));
		jButton2.setText("Delete Product");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onClickButtonDelete(evt);
			}
		});

		jLabel6.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jLabel6.setForeground(new java.awt.Color(255, 0, 0));
		jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/arrow.png"))); // NOI18N
		jLabel6.setText("Select to List");

		quantityToDeleteTextField.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
		quantityToDeleteTextField.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				quantityToDeleteTextFieldMousePressed(evt);
			}
		});

		jLabel7.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jLabel7.setForeground(new java.awt.Color(255, 0, 0));
		jLabel7.setText("Quantity");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel2Layout.createSequentialGroup()
										.addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 215,
												Short.MAX_VALUE)
										.addContainerGap())
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel2Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(jButton2)
												.addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel7)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(quantityToDeleteTextField)))
										.addGap(40, 40, 40)))));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
								.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(quantityToDeleteTextField, javax.swing.GroupLayout.DEFAULT_SIZE,
												22, Short.MAX_VALUE)
										.addComponent(jLabel7))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jButton2).addContainerGap()));

		// listProduct.setCursor(new
		// java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

		jLabel3.setText("Product List");

		jButton3.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
		jButton3.setForeground(new java.awt.Color(102, 0, 102));
		jButton3.setText("Menu");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onClickButtonMenu(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup().addGap(20, 20, 20).addComponent(
												jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 67,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel3)))
								.addGap(18, 18, 18).addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 96,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(listScrollPane,
								javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(18, 18, 18).addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()))));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButton4).addComponent(jButton3))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addGap(4, 4, 4).addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createSequentialGroup().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup().addGap(42, 42, 42)
								.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 38,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel3)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(listScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 197,
								javax.swing.GroupLayout.PREFERRED_SIZE)));

		getAccessibleContext().setAccessibleName("Insert Product");
	}

	private void onClickButtonBack(ActionEvent event) {
		hidePanel();
		butlerPanel.menu();
	}

	private void onClickButtonMenu(java.awt.event.ActionEvent evt) {
		hidePanel();
		butlerPanel.showPanel();
	}

	private void onClickButtonAdd(ActionEvent event) {
		String nameProduct = this.productNameText.getText().trim();
		int quantity = 0;
		try {
			quantity = Integer.parseInt(this.quantityProductText.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Value to quantity non Integer;\nPlease insert value to Integer in quantity area.",
					"INCORRECT VALUE ", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (quantity <= 0) {
			JOptionPane.showMessageDialog(null, "Incorrect value. Please insert value > 0.", "ACTION INVALID",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int choice = JOptionPane.showConfirmDialog(null, "Do you really want to insert " + nameProduct + "?",
				"INSERT CHECK", JOptionPane.OK_CANCEL_OPTION);
		if (choice == JOptionPane.OK_OPTION) {
			try {
				this.pantryAgent.addIngredient(new Ingredient(nameProduct, quantity));
				this.quantityProductText.setText("");
				this.productNameText.setText("");
				getPantryAgent().getContent();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Product not added,try Again.", "ACTION NOT SUCCEEDE",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void onClickButtonDelete(ActionEvent event) {
		Ingredient element_selected;
		int quantity = 0;
		try {
			element_selected = listProduct.getSelectedValue();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"No product selected.Please select a product\n from those listed on the left.", "ACTION INVALID",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int quantity_actual = element_selected.getQuantity();
		try {
			quantity = Integer.parseInt(this.quantityToDeleteTextField.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Value to quantity non Integer;\nPlease insert value to Integer in quantity area.",
					"INCORRECT VALUE ", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int quantity_total = quantity_actual - quantity;
		if (quantity <= 0 || quantity_total < 0) {
			JOptionPane.showMessageDialog(null,
					"Incorrect value. Enter value greater than 0 and less than the current value.", "ACTION INVALID",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		int choice = JOptionPane.showConfirmDialog(null,
				"Do you really want to delete " + element_selected.getName() + "?", "DELETE CHECK",
				JOptionPane.OK_CANCEL_OPTION);
		if (choice == JOptionPane.OK_OPTION) {
			try {
				this.pantryAgent.updateContent(new Ingredient(element_selected.getName(), quantity_total));
				this.quantityToDeleteTextField.setText("");
				getPantryAgent().getContent();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Product not deleted,try again.", "ACTION NOT SUCCEEDED",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	private void productNameTextMousePressed(MouseEvent event) {
		productNameText.setText("");
	}

	private void quantityProductTextMousePressed(MouseEvent event) {
		quantityProductText.setText("");
	}

	private void quantityToDeleteTextFieldMousePressed(MouseEvent event) {
		quantityToDeleteTextField.setText("");
	}

	private DefaultListModel<Ingredient> listModel1;
	private javax.swing.JButton addToListButton;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JButton jButton4;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	public javax.swing.JList<Ingredient> listProduct;
	public javax.swing.JTextField productNameText;
	public javax.swing.JTextField quantityProductText;
	public javax.swing.JTextField quantityToDeleteTextField;

	private void onIngredientsChangedEvent() {
		listModel1.clear();
		List<Ingredient> ingredients = getPantryAgent().getCurrentIngredients();
		for ( Ingredient ingredient : ingredients )
			listModel1.addElement(ingredient);
	}
	
	private void onStateChangedEvent() {
		addToListButton.setEnabled(getPantryAgent().getState());
		jButton2.setEnabled(getPantryAgent().getState());
	}
}
