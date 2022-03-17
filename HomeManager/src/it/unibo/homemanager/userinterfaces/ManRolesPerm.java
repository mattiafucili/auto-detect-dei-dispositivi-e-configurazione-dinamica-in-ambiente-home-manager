/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManRolesPerm.java
 *
 * Created on 19-set-2009, 10.27.43
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.tablemap.User;
import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class ManRolesPerm extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private User user;
    private String ass_to_change;
    
    private TucsonAgentId agent;
    
    /** Creates new form ManRolesPerm */
    public ManRolesPerm(MainPanel mainp, TucsonTupleCentreId tc, RBACManager rm) {
        this.mp = mainp;
        this.rbac = tc;
        this.rbac_man = rm;
        try {
            agent = new TucsonAgentId("manageRolesPermissions");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

    public void init(User u){
        this.user = u;
        fillListAss();
        fillListPerms();
        fillListRoles();
    }

    private void fillListAss(){
        List<LogicTuple> rolesAss;
        Vector ro = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple assRolesPermTemplate = new LogicTuple("ass_roles_perm", new Var("X"), new Var("Y"));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, assRolesPermTemplate, null);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("ass_roles_perm", new Var("X"), new Var("Y")), new Var("Z")));
            rolesAss = op_rdAll.getLogicTupleListResult();
            System.err.println("[fillListAss@ManRolesPerm] rolesAss: " + rolesAss);
            String ass = "";

                //LogicTuple rol;
            for(int i=0;i<rolesAss.size();i++)
            {
                //rol = LogicTuple.parse(rolesAss.get(i).toString());
                ass = rolesAss.get(i).getArg(0).getName();//rol.getArg(0).getName();
                ass += "-";
                ass += rolesAss.get(i).getArg(1).getName();//rol.getArg(1).getName();
                ro.add(ass);
                System.err.println("[fillListAss@ManRolesPerm] ass: " + ass);
                //ass = "";
            }
            this.jList_Associations.setListData(ro);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRoles(){
        List<LogicTuple> roles;
        Vector rolesVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("role", new Var("X")), null);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("role", new Var("X")), new Var("Y")));
                //System.err.println("roles: "+rl);
                //roles = rl.getArg(1).toList();
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("[fillListRoles@ManRolesPerm] roles: " +roles);

                //LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                    rolesVector.add(roles.get(i).getArg(0).getName());
                //{
                //    rol = LogicTuple.parse(roles.get(i).toString());
                //    ro.add(rol.getArg(0).getName());
                //}
                
            this.jList_Roles.setListData(rolesVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void fillListPerms(){
        List<LogicTuple> permissions;
        Vector permissionsVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("permission", new Var("X")), new Var("Y")));
                //System.err.println("permissions: "+rl);
                //permissions = rl.getArg(1).toList();
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("permission", new Var("X")), null);
            permissions = op_rdAll.getLogicTupleListResult();
            System.err.println("[fillListPerms@ManRolesPerm] permissions: " +permissions);
            //    LogicTuple rol;
            for(int i=0;i<permissions.size();i++)
                permissionsVector.add(permissions.get(i).getArg(0).getName());
            //    {
            //        rol = LogicTuple.parse(permissions.get(i).toString());
            //        permissionsVector.add(rol.getArg(0).getName());
            //    }
            this.jList_Perm.setListData(permissionsVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void save_new_ass(String association){
        association = association.trim();
        StringTokenizer st = new StringTokenizer(association, "-");
        String role = st.nextToken();
        String permission = st.nextToken();
        
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("ass_roles_perm", new Value(role), new Value(permission)));
            //if(usro == null){
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("ass_roles_perm", new Value(role), new Value(permission)), null);
            if(op_rdp.isResultFailure())
                acc.out(this.rbac, new LogicTuple("ass_roles_perm", new Value(role), new Value(permission)), null);
            else
                javax.swing.JOptionPane.showMessageDialog(null,"Association already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
            fillListAss();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void delete_association(String association){
        //String role = "'"+del_role+"'";
        association = association.trim();
        System.out.println(association);
        StringTokenizer st = new StringTokenizer(association, "-");
        String role = st.nextToken();
        String permission = st.nextToken();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_inp = acc.inp(rbac, new LogicTuple("ass_roles_perm", new Value(role), new Value(permission)), null);

            if(op_inp.isResultFailure())
                javax.swing.JOptionPane.showMessageDialog(null,"Association not exsists!",
                "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
            fillListAss();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
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
        jList_Associations = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Roles = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_Perm = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField_Association = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jList_Associations.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_Associations.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_AssociationsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList_Associations);

        jLabel1.setText("Associations ROLES - PERMISSIONS:");

        jList_Roles.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList_Roles);

        jLabel2.setText("ROLES:");

        jList_Perm.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_Perm);

        jLabel3.setText("ASS. WITH:");

        jLabel4.setText("PERMISSIONS:");

        jButton1.setText("SET ASS.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setText("ASSOCIATION:");

        jButton2.setText("SAVE NEW ASS.");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("DELETE ASS.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("RBAC MENU");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("MENU");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("LOGOUT");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jTextField_Association, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jButton3)))
                            .addContainerGap(43, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(8, 8, 8)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(jScrollPane3))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                            .addComponent(jButton1)
                            .addGap(61, 61, 61)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton4, jButton5, jButton6});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jButton3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane2, jScrollPane3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(jButton6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                                        .addGap(26, 26, 26))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(jLabel3)
                                .addGap(56, 56, 56)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(70, 70, 70)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Association, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(74, 74, 74))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jScrollPane2, jScrollPane3});

    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
         this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList_AssociationsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_AssociationsValueChanged
        // TODO add your handling code here:
        if(!this.jList_Associations.isSelectionEmpty())
        {
        this.ass_to_change = this.jList_Associations.getSelectedValue().toString();
        this.jTextField_Association.setText(ass_to_change);
        }
    }//GEN-LAST:event_jList_AssociationsValueChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.jTextField_Association.setText(this.jList_Roles.getSelectedValue().toString()+"-"+this.jList_Perm.getSelectedValue().toString());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        save_new_ass(this.jTextField_Association.getText());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
         delete_association(this.jTextField_Association.getText());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(this.user);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jList_Associations;
    private javax.swing.JList jList_Perm;
    private javax.swing.JList jList_Roles;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField_Association;
    // End of variables declaration//GEN-END:variables

    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
}
