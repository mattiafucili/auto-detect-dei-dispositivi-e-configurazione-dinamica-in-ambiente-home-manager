/*
 * ManageUsersPanel.java
 *
 * Created on 7 novembre 2006, 21.08
 */

package it.unibo.homemanager.userinterfaces;

/**
 *
 * @author  admin
 */

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;

import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.util.Utilities;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ManageUsersPanel extends javax.swing.JPanel {
    
    private MainPanel mp;
    private User u;
    private Vector tid = null;
    private ServiceFactory sf;
    private UserServiceInterface userService;
    private Database database;
    
    /** Creates new form ManageUsersPanel */
    public ManageUsersPanel(MainPanel mpanel, Vector tid, ServiceFactory sf) {
        mp = mpanel;
        this.tid = tid;
	this.sf = sf;
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(ManageUsersPanel.class.getName()).log(Level.SEVERE, null, ex);
	}
        initComponents();
    }
    
    protected void init(User usr) {
        u = usr;
        this.userService = sf.getUserServiceInterface();
        lbRes.setText("");
        fillList();
        if(!isVisible())
            setVisible(true);
    }
    
    private void fillList() {
        try {
            listUsers.removeAll();
            Vector users = userService.getUsers(database.getDatabase());
            Vector items = new Vector();
            for(int i=0;i<users.size();i++) {
                User usr = (User)users.elementAt(i);
                if(!u.username.equals(usr.username))
                    items.add(usr.username+", "+Utilities.writeRole(usr.getRoleChar()));//usr.role.charAt(0)));
            }
            listUsers.setListData(items);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected void updatePanel() {
        fillList();
    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();
        btMenu = new javax.swing.JButton();
        btLogOut = new javax.swing.JButton();
        btModify = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        lbRes = new javax.swing.JLabel();
        btInsert = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 255));
        setMaximumSize(new java.awt.Dimension(538, 343));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));
        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("Change users'role or delete them using the corresponding buttons.");

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 255));
        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 12));
        jScrollPane1.setViewportView(listUsers);

        btMenu.setFont(new java.awt.Font("Courier New", 1, 14));
        btMenu.setForeground(new java.awt.Color(102, 0, 102));
        btMenu.setText("MENU");
        btMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenubackToMenu(evt);
            }
        });

        btLogOut.setFont(new java.awt.Font("Courier New", 1, 14));
        btLogOut.setForeground(new java.awt.Color(102, 0, 102));
        btLogOut.setText("LOG OUT");
        btLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogOutActionPerformed(evt);
            }
        });

        btModify.setFont(new java.awt.Font("Courier New", 1, 14));
        btModify.setForeground(new java.awt.Color(0, 0, 153));
        btModify.setText("MODIFY");
        btModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModifyActionPerformed(evt);
            }
        });

        btDelete.setFont(new java.awt.Font("Courier New", 1, 14));
        btDelete.setForeground(new java.awt.Color(0, 0, 153));
        btDelete.setText("DELETE");
        btDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeleteActionPerformed(evt);
            }
        });

        lbRes.setFont(new java.awt.Font("Courier New", 1, 13));
        lbRes.setForeground(new java.awt.Color(255, 0, 0));

        btInsert.setFont(new java.awt.Font("Courier New", 1, 14));
        btInsert.setForeground(new java.awt.Color(0, 0, 153));
        btInsert.setText("INSERT NEW USER");
        btInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btInsertActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(btMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(16, 16, 16)
                                .add(btLogOut, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(46, 46, 46))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, btInsert, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, lbRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, btDelete, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, btModify, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))))
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btLogOut)
                            .add(btMenu))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lbRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                        .add(14, 14, 14)
                        .add(btModify)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btDelete)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btInsert)
                        .add(44, 44, 44))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btInsertActionPerformed
        NewUserFrame nu = new NewUserFrame("Insert a New User",this, sf);
        nu.setVisible(true);
    }//GEN-LAST:event_btInsertActionPerformed

    private void btDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeleteActionPerformed
        if(listUsers.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null,
                    "Please select a User.",
                    "MISSING SELECTION",JOptionPane.ERROR_MESSAGE);
            return;
        }
        User usr = getSelectedUser(listUsers.getSelectedValue().toString());
        // check if admin wants to delete max priority user
        try {
        //    LogicTuple lt = ctx.rdp(tid,new LogicTuple("max_prio",new Value(usr.idUser)));
        //    if(lt != null) {
//                JOptionPane.showMessageDialog(null,"Cannot delete "+
//                    usr.username+": MAX PRIORITY USER! Please modify policy before doing this" +
//                        " operation.","ERROR",JOptionPane.ERROR_MESSAGE);
//                return;
       //     }
            int choice = JOptionPane.showConfirmDialog(null,"Do you really want to delete "+
                    usr.username+"?",
                    "CHECK",JOptionPane.OK_CANCEL_OPTION);
            if(choice == JOptionPane.OK_OPTION) {
		userService.deleteUser(database.getDatabase(), usr);
                //usr.delete(database.getDatabase());
                fillList();
                lbRes.setText(usr.username+" DELETED!");
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btDeleteActionPerformed

    private void btModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btModifyActionPerformed
        if(listUsers.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null,
                    "Please select a User.",
                    "MISSING SELECTION",JOptionPane.ERROR_MESSAGE);
            return;
        }
        User usr = getSelectedUser(listUsers.getSelectedValue().toString());
        int choice = JOptionPane.showConfirmDialog(null,"Do you really want "+
                usr.username+" to become an "+Utilities.getOtherRole(usr.getRoleChar())+"?",//.role.charAt(0))+"?",
                "CHECK",JOptionPane.OK_CANCEL_OPTION);
        if(choice == JOptionPane.OK_OPTION) {
            try {
                usr.role = Utilities.getOtherRoleChar(usr.getRoleChar());//role.charAt(0));
                System.out.println("Voglio modificare "+ usr.username+ "che diventi "+ usr.role);
                //usr.update(database.getDatabase());
		userService.updateUser(database.getDatabase(), usr);
                fillList();
                lbRes.setText(usr.username+" UPDATED!");
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btModifyActionPerformed

    private User getSelectedUser(String item) {
        User usr = null;
        
        String[] tokens = item.split(",");
        try {
            usr = userService.getUser(database.getDatabase(),tokens[0].trim());
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return usr;
    }
    
    private void btLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOutActionPerformed
        mp.logOut(evt);
    }//GEN-LAST:event_btLogOutActionPerformed

    private void btMenubackToMenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenubackToMenu
        mp.loadInitRolePanel(u);
    }//GEN-LAST:event_btMenubackToMenu
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btInsert;
    private javax.swing.JButton btLogOut;
    private javax.swing.JButton btMenu;
    private javax.swing.JButton btModify;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbRes;
    private javax.swing.JList listUsers;
    // End of variables declaration//GEN-END:variables
    
}
