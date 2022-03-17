/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ViewRBACPolicy.java
 *
 * Created on 19-set-2009, 10.33.44
 */

package it.unibo.homemanager.userinterfaces;

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.tablemap.User;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
/**
 *
 * @author Administrator
 */
public class ViewRBACPolicy extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private Device dev;
    private User user;
    private String ass_to_change;
    private Vector rooms;
    private UserServiceInterface us;
    private Vector users;
    private Hashtable<String, String> ht;
    
    private ServiceFactory sf;
    private TucsonAgentId agent;
    
    /** Creates new form ViewRBACPolicy */
    public ViewRBACPolicy(MainPanel mainp, TucsonTupleCentreId tc, RBACManager rm, ServiceFactory sf) {
        this.mp = mainp;
        this.rbac = tc;
        this.rbac_man = rm;
	this.sf = sf;
	this.us = sf.getUserServiceInterface();
        try {
            agent = new TucsonAgentId("viewRBACPolicyAgent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }
    
    public void init(User u){
        this.user = u;
        fillUsersList();
//        fillRoomConstr();
//        fillListRoles();
//        fillListRooms();
//        fillListPerms();
//        fillListRoles();
    }

    private void fillUsersList() {
        this.ht = new Hashtable();
        String ut = "";
        try {
            this.jList_Users.removeAll();
            users = us.getUsers(sf.getDatabaseInterface().getDatabase());
            Vector items = new Vector();
            for(int i=0;i<users.size();i++) {
                User usr = (User)users.elementAt(i);
                ut = String.valueOf(usr.firstname);
                ut += " ";
                ut += usr.surname;
                items.add(ut);
                ht.put(ut, String.valueOf(usr.idUser));
                //ut="";
            }
            this.jList_Users.setListData(items);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
  
    private void fillListAss(){
        List<LogicTuple> associations;
        String user = this.jList_Users.getSelectedValue().toString();
        Vector rolesVector = new Vector();
        int j = Integer.parseInt(ht.get(user));
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //ITucsonOperation op_rdAll = acc.rdAll(this.rbac,new LogicTuple("ass_user_roles", new Var("X"), new Var("Y")), null);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,new LogicTuple("ass_user_roles", new Value(j), new Var("Y")), null);
            associations = op_rdAll.getLogicTupleListResult();
            System.err.println("ass. users-roles: "+associations);
            //String ass = "";

            //LogicTuple rol;
            for(int i=0;i<associations.size();i++)
            //{
                rolesVector.add(associations.get(i).getArg(1).getName());
                //rol = LogicTuple.parse(associations.get(i).toString());
                //if(rol.getArg(0).intValue() == j)
                //{
                    //ass = rol.getArg(1).getName();
                    //ro.add(ass);
                    //ass = "";
                //}
            //}
            this.jList_Roles.setListData(rolesVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
    private void fillListAssRP(){
        List<LogicTuple> associations;
        String role = this.jList_Roles.getSelectedValue().toString();
        Vector rolesPermsVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac, new LogicTuple("ass_roles_perm", new Value(role), new Var("Y")), null);
            associations = op_rdAll.getLogicTupleListResult();
            System.err.println("ass. roles-perm: "+associations);
            acc.exit();
                //String ass = "";

                //LogicTuple rol;
            for(int i=0;i<associations.size();i++)
                rolesPermsVector.add(associations.get(i).getArg(1).getName());
            //{
                //rol = LogicTuple.parse(associations.get(i).toString());
                //if(rol.getArg(0).getName().equals(role))
                //{
                    //ass = rol.getArg(1).getName();
                    //rolesPermsVector.add(ass);
                //}
            //}
            this.jList_Associations.setListData(rolesPermsVector);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void fillRoomConstr() {
        List<LogicTuple> roomRules;
        Vector roomRuleVector = new Vector();
        //String association = this.jList_Associations.getSelectedValue().toString();
        String role = this.jList_Roles.getSelectedValue().toString();
        try {
//            TupleArgument[] ta = new TupleArgument[9];
//            for(int i=0;i<ta.length;i++){
//                ta[i] = new Var();
//            }
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,
                    new LogicTuple("room_rule",
                            new Var("A"),
                            new Var("B"),
                            new Value(role),//new Var("C"),
                            new Var("D"),
                            new Var("E"),
                            new Var("F"),
                            new Var("G")),
                        Long.MAX_VALUE);
            roomRules = op_rdAll.getLogicTupleListResult();
            System.err.println("room_rule: "+roomRules);
            //String ass = "";

            //LogicTuple rol;
            for(int i=0;i<roomRules.size();i++)
                roomRuleVector.add(roomRules.get(i));
            //{
                //rol = LogicTuple.parse(roomRules.get(i).toString());
                //if(rol.getArg(1).getName().equals(association) && rol.getArg(2).getName().equals(association))
                //    roomRuleVector.add(rol);
//
//                    ass = String.valueOf(rol.getArg(0).intValue());
//                    ass += "-";
//                    ass += rol.getArg(1).getName();
//                    ro.add(ass);
//                    ass = "";
            //}
            this.jList_Rules1.setListData(roomRuleVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     
    private void fillDevConstr() {
        List<LogicTuple> roles;
        Vector ro = new Vector();

        String association = this.jList_Associations.getSelectedValue().toString();
        String role = this.jList_Roles.getSelectedValue().toString();
        try {
//            TupleArgument[] ta = new TupleArgument[9];
//            for(int i=0;i<ta.length;i++){
//                ta[i] = new Var();
//            }
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,
                    new LogicTuple("dev_rule",
                            new Var("A"),
                            new Value(association),//new Var("B"),
                            new Value(role),//new Var("C"),
                            new Var("D"),
                            new Var("E"),
                            new Var("F"),
                            new Var("G")),
                        null);
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("dev_rule: "+roles);
            //String ass = "";

                //LogicTuple rol;
            for(int i=0;i<roles.size();i++)
                ro.add(roles.get(i));
                //{
                    //rol = LogicTuple.parse(roles.get(i).toString());
                    //if(rol.getArg(1).getName().equalsIgnoreCase(association) && rol.getArg(2).getName().equalsIgnoreCase(role))
                        //ro.add(rol);
//
//                    ass = String.valueOf(rol.getArg(0).intValue());
//                    ass += "-";
//                    ass += rol.getArg(1).getName();
//                    ro.add(ass);
//                    ass = "";
                //}
            this.jList_Rules.setListData(ro);
                /*if(!ro.isEmpty()){
                    this.jList_Rules.setListData(ro);
                }
                else{
                  rl = ctx.inp(this.rbac, new LogicTuple("rd_all",
                        new Value("room_rule", new Var("A"), new Var("B"), new Var("C"),
                        new Var("D"), new Var("E"), new Var("F"), new Var("G")),
                        new Var("Z")));
                System.err.println("ass. roles-perm: "+rl);
                roles = rl.getArg(1).toList();
                ass = "";


                for(int i=0;i<roles.size();i++)
                {

                    rol = LogicTuple.parse(roles.get(i).toString());
                    if(rol.getArg(1).getName().equalsIgnoreCase(association) && rol.getArg(2).getName().equalsIgnoreCase(role))
                        ro.add(rol);
//
//                    ass = String.valueOf(rol.getArg(0).intValue());
//                    ass += "-";
//                    ass += rol.getArg(1).getName();
//                    ro.add(ass);
//                    ass = "";
                }
                if(!ro.isEmpty()){
                    this.jList_Rules1.setListData(ro);
                }
            }*/
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void clearFields() {
        this.jList_Roles.setListData(new Object[0]);
        this.jList_Associations.setListData(new Object[0]);
        this.jList_Rules.setListData(new Object[0]);
        this.jList_Rules1.setListData(new Object[0]);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList_Roles = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList_Rules = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList_Associations = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList_Users = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList_Rules1 = new javax.swing.JList();

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jList_Roles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_RolesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList_Roles);

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList3);

        jScrollPane4.setViewportView(jList_Rules);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        jButton3.setText("RBAC MENU");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("MENU");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("LOGOUT");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList_Associations.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_AssociationsValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jList_Associations);

        jLabel1.setText("ROLES:");

        jLabel2.setText("ASSOCIATIONS:");

        jList_Users.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_UsersValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(jList_Users);

        jLabel3.setText("USERS:");

        jLabel4.setText("DEV. RULES:");

        jLabel5.setText("ROOMS RULES:");

        jScrollPane7.setViewportView(jList_Rules1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(211, 211, 211)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jButton3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton5))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(87, 87, 87))
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)))
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                    .addComponent(jLabel5))
                .addGap(236, 236, 236))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, 0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(this.user);
}//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
}//GEN-LAST:event_jButton5ActionPerformed

    private void jList_UsersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_UsersValueChanged
        // TODO add your handling code here:
        if(!this.jList_Users.isSelectionEmpty() && !this.jList_Users.getValueIsAdjusting()) {
            clearFields();
            fillListAss();
        }
    }//GEN-LAST:event_jList_UsersValueChanged

    private void jList_RolesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_RolesValueChanged
        // TODO add your handling code here:
        if(!this.jList_Roles.isSelectionEmpty() && !this.jList_Roles.getValueIsAdjusting()) {
            fillListAssRP();
            fillRoomConstr();
        }
    }//GEN-LAST:event_jList_RolesValueChanged

    private void jList_AssociationsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_AssociationsValueChanged
        // TODO add your handling code here:
        if(!this.jList_Associations.isSelectionEmpty() && !this.jList_Associations.getValueIsAdjusting()){
            fillDevConstr();
        }
    }//GEN-LAST:event_jList_AssociationsValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList_Associations;
    private javax.swing.JList jList_Roles;
    private javax.swing.JList jList_Rules;
    private javax.swing.JList jList_Rules1;
    private javax.swing.JList jList_Users;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    // End of variables declaration//GEN-END:variables

}
