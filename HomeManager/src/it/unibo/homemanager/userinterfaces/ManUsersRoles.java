/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManUsersRoles.java
 *
 * Created on 19-set-2009, 10.26.58
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.tablemap.User;
import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
/**
 *
 * @author Administrator
 */
public class ManUsersRoles extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private User user;
    private String ass_to_change;
    private Vector users;
    private String role_set;
    private int cardinality;
    
    private TucsonAgentId agent;
    
    private UserServiceInterface userService;
    private Database database;
    
    /** Creates new form ManUsersRoles */
    public ManUsersRoles(MainPanel manp, TucsonTupleCentreId tc, RBACManager rm, ServiceFactory sf) {
        this.mp = manp;
        this.rbac = tc;
        this.rbac_man = rm;
        userService = sf.getUserServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("manageUserRoles");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

    public void init(User u){
        this.user = u;
        fillListAss();
        fillUsersList();
        fillListRoles();
    }


    private void fillListAss(){
        List<LogicTuple> associations;
        Vector associationsVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("ass_user_roles", new Var("X"), new Var("Y")), null);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("ass_user_roles", new Var("X"), new Var("Y")), new Var("Z")));
            associations = op_rdAll.getLogicTupleListResult();
                //System.err.println("ass. users-roles: "+associations);
                //associations = rl.getArg(1).toList();
            String ass = "";

            LogicTuple rol;
            for(int i=0;i<associations.size();i++)
            {
                rol = LogicTuple.parse(associations.get(i).toString());

                User ur = userService.getUserById(database.getDatabase(), rol.getArg(0).intValue());
                ass = ur.firstname;
                ass += " ";
                ass += ur.surname;
                ass += "-";
                ass += rol.getArg(1).getName();
                associationsVector.add(ass);
                //ass = "";
            }
            this.jList_Associations.setListData(associationsVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


     private void fillUsersList() {
        
        String ut = "";
         try {
            this.jList_Users.removeAll();
            users = userService.getUsers(database.getDatabase());
            Vector items = new Vector();
            for(int i=0;i<users.size();i++) {
                User usr = (User)users.elementAt(i);
                ut = String.valueOf(usr.firstname);
                ut += " ";
                ut += usr.surname;
                items.add(ut);
                ut="";
            }
            this.jList_Users.setListData(items);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }


      private void fillListRoles(){
        List<LogicTuple> roles;
        Vector ro = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("role", new Var("X")), new Var("Y")));
                //System.err.println("roles: "+rl);
                //roles = rl.getArg(1).toList();
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("role", new Var("X")), null);
            roles = op_rdAll.getLogicTupleListResult();
            
            LogicTuple rol;
            for(int i=0;i<roles.size();i++)
            {
                rol = LogicTuple.parse(roles.get(i).toString());
                ro.add(rol.getArg(0).getName());
            }
            this.jList_Roles.setListData(ro);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void delete_association(String association){
        //String role = "'"+del_role+"'";
        String asso = association.trim();
        System.out.println(asso);
        int id=0;
        String rol;
        StringTokenizer st = new StringTokenizer(asso, "-");
        String temp = st.nextToken();
        String[] names = temp.split(" ");
        for(int i=0;i<this.users.size();i++){
            User ui = (User)this.users.get(i);
            if(ui.firstname.equals(names[0]) && ui.surname.equals(names[1])){
                id = ui.idUser;
            }
        }
        rol = st.nextToken();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_inp = acc.inp(rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)), null);
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)));
            acc.exit();
            //if(usro == null)
            if(op_inp.isResultFailure())
                javax.swing.JOptionPane.showMessageDialog(null,"Association not exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            else {
                //ctx.in(this.rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)));
                fillListAss();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void save_new_ass(String association){
        association = association.trim();
        int id=0;
        String rol;
        StringTokenizer st = new StringTokenizer(association, "-");
        String temp = st.nextToken();
        String[] names = temp.split(" ");
        for(int i=0;i<this.users.size();i++){
            User ui = (User)this.users.get(i);
            if(ui.firstname.equals(names[0]) && ui.surname.equals(names[1])){
                id = ui.idUser;
            }
        }
        rol = st.nextToken();
        try{
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)));
            //if(usro == null){
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)), null);
            if (op_rdp.isResultFailure()) {
                acc.out(this.rbac, new LogicTuple("ass_user_roles", new Value(id), new Value(rol)), null);
                acc.exit();
                fillListAss();
            }
            else{
                javax.swing.JOptionPane.showMessageDialog(null,"Association already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
                acc.exit();
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void set_activate_default(String association) {
        association = association.trim();
        System.out.println(association);
        int id=0;
        String rol;
        StringTokenizer st = new StringTokenizer(association, "-");
        String temp = st.nextToken();
        String[] names = temp.split(" ");
        for(int i=0;i<this.users.size();i++){
            User ui = (User)this.users.get(i);
            if(ui.firstname.equals(names[0]) && ui.surname.equals(names[1])){
                id = ui.idUser;
            }
        }
        rol = st.nextToken();
        try {
            //LogicTuple lt = ctx.rdp(rbac, new LogicTuple("default_activate", new Value(id), new Var("Y")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("default_activate", new Value(id), new Var("Y")), null);
            if(op_rdp.isResultFailure()){
                acc.out(rbac, new LogicTuple("default_activate", new Value(id), new Value(rol)), null);
            }
            else{
                javax.swing.JOptionPane.showMessageDialog(null,"ERRORE: DEFAULT ROLE TO ACTIVATE ALREADY EXSISTS!",
                    "",javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean verify_ssd(String association) {
        //TO check...................
        boolean ret = false;
        int id=0;
        try{
            association = association.trim();
            System.err.println("association: " + association);
            String rol = "";
            StringTokenizer st = new StringTokenizer(association, "-");
             String temp = st.nextToken();
             String[] names = temp.split(" ");
              for(int i=0;i<this.users.size();i++){
                  User ui = (User)this.users.get(i);
                  if(ui.firstname.equals(names[0]) && ui.surname.equals(names[1])){
                      id = ui.idUser;
                  }
            }
              
            rol = st.nextToken();
            
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("ass_user_roles", new Value(id), new Var("X")), null);
            //LogicTuple lt2 = ctx.inp(rbac, new LogicTuple("rd_all", new Value("ass_user_roles", new Value(id), new Var("X")), new Var("Y")));
            List<LogicTuple> rolesAssociations = op_rdAll.getLogicTupleListResult(); //lt2.getArg(1).toList();
            int n = rolesAssociations.size();
            op_rdAll = acc.rdAll(rbac, new LogicTuple("role_set_ssd", new Var("X"), new Var("Z")), null);
            //LogicTuple lt = ctx.inp(rbac, new LogicTuple("rd_all", new Value("role_set_ssd", new Var("X"), new Var("Z")), new Var("Y")));
            for(int z=0;z<=n;z++){
                List<LogicTuple> rsSSD = op_rdAll.getLogicTupleListResult();
                for(int i=0;i<rsSSD.size();i++){
                    LogicTuple rs = rsSSD.get(i);
                    //tutti i ruoli di un role-set
                    List l1 = rs.getArg(1).toList();
                    boolean res = false;
                    int j=0;
                    int k=0;
                    while(j<l1.size()){
                        String ruolo = l1.get(j).toString();
                        System.err.println("RUOLOOOOOOO: " + ruolo);
                        if(l1.get(j).toString().equals("'"+rol+"'")){
                            k++;
                        }
                        j++;
                    }
                    
                    ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("ssd", rs.getArg(0), new Var("Y")), null);
                    LogicTuple lt = op_rdp.getLogicTupleResult();
                    if (lt.getArg(1).intValue() <= k) {
                        this.role_set = rs.getArg(0).toString();
                        this.cardinality = lt.getArg(1).intValue();
                        ret = true;
                    }
                }
            }
            acc.exit();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList_Associations = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Users = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_Roles = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jTextField_Association = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jButton1.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton1.setText("RBAC MENU");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton2.setText("MENU");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton3.setText("LOGOUT");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

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

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel1.setText("Association USERS - ROLES:");

        jList_Users.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList_Users);

        jLabel2.setBackground(new java.awt.Color(0, 51, 255));
        jLabel2.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel2.setText("USERS:");

        jLabel5.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel5.setText("ASS.WITH:");

        jList_Roles.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_Roles);

        jLabel4.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel4.setText("ROLES:");

        jLabel6.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel6.setText("ASSOCIATON:");

        jButton4.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton4.setText("Save New Ass.");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton5.setText("Delete Ass.");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Courier New", 1, 14));
        jButton6.setText("SET ASS.");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("SET ACTIVE DEFAULT");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(367, 367, 367)))
                        .addGap(385, 385, 385))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField_Association, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5))
                            .addComponent(jLabel6))
                        .addContainerGap(12, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton4, jButton5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton7))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(jLabel3)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jButton6)
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_Association, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(101, 101, 101))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton4, jButton5, jTextField_Association});

    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if(verify_ssd(this.jTextField_Association.getText()))
            save_new_ass(this.jTextField_Association.getText());
        else
            javax.swing.JOptionPane.showMessageDialog(null,"VINCOLO SSD VIOLATO: "+this.role_set+". CARDINALITA': "+this.cardinality,
                    "",javax.swing.JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList_AssociationsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_AssociationsValueChanged
        // TODO add your handling code here:
        if(!this.jList_Associations.isSelectionEmpty())
        {
            this.ass_to_change = this.jList_Associations.getSelectedValue().toString();
            this.jTextField_Association.setText(ass_to_change);
        }
    }//GEN-LAST:event_jList_AssociationsValueChanged

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        this.jTextField_Association.setText(this.jList_Users.getSelectedValue().toString()+ "-"+this.jList_Roles.getSelectedValue().toString());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        delete_association(this.jTextField_Association.getText());
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
          this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(this.user);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        if(!jList_Associations.isSelectionEmpty())
            set_activate_default(this.jList_Associations.getSelectedValue().toString());
    }//GEN-LAST:event_jButton7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList_Associations;
    private javax.swing.JList jList_Roles;
    private javax.swing.JList jList_Users;
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
