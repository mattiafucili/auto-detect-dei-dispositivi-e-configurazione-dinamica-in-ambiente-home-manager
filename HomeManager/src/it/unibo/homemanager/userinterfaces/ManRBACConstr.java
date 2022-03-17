/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManRBACConstr.java
 *
 * Created on 19-set-2009, 10.28.42
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.tablemap.User;
import alice.tucson.api.*;
import alice.logictuple.*;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class ManRBACConstr extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private User user;
    private String ass_to_change;
    private Vector rooms;
    private Vector devices;
    
    private TucsonAgentId agent;
    
    /** Creates new form ManRBACConstr */
    public ManRBACConstr(MainPanel manp, TucsonTupleCentreId tc, RBACManager rm) {
        this.mp = manp;
        this.rbac = tc;
        this.rbac_man = rm;
        try {
            agent = new TucsonAgentId("manageRBACConstraintAgent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

     public void init(User u){
        this.user = u;
        fillListRoles();
//        fillListRolesRSSD();
//        fillListRolesRDSD();
        fillListSSD();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(ManRBACConstr.class.getName()).log(Level.SEVERE, null, ex);
        }
        fillListDSD();
    }

    private void delete_dsd_rule(String toString) {
        StringTokenizer st = new StringTokenizer(toString, ",");
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            acc.in(rbac, new LogicTuple("dsd", new Value(st.nextToken()), new Var("Y")), null);
            //LogicTuple lt = ctx.in(rbac, new LogicTuple("dsd", new Value(st.nextToken()), new Var("Y")));
            acc.exit();
            this.fillListDSD();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void delete_ssd_rule(String selectedValue) {
        StringTokenizer st = new StringTokenizer(selectedValue, ",");
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            acc.in(rbac, new LogicTuple("ssd", new Value(st.nextToken()), new Var("Y")), null);
            //LogicTuple lt = ctx.in(rbac, new LogicTuple("ssd", new Value(st.nextToken()), new Var("Y")));
            acc.exit();
            this.fillListSSD();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

      private void fillListRoles(){
        List<LogicTuple> roles;
        Vector ro = new Vector();
        try {
            System.err.println("[fillListRoles@ManRBaCConstr] Method start!");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("role", new Var("X")), null);
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("roles: " + roles + " / size: " + roles.size());
            
            for(int i=0;i<roles.size();i++)
                ro.add(roles.get(i).getArg(0).getName());
               /* LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("role", new Var("X")), new Var("Y")));
                System.err.println("roles: "+rl);
                roles = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    rol = LogicTuple.parse(roles.get(i).toString());
                    ro.add(rol.getArg(0).getName());
                }*/
            this.jList_ROLES1.setListData(ro);
            this.jList_ROLES2.setListData(ro);
            acc.exit();
            System.err.println("[fillListRoles@ManRBaCConstr] Method end!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRolesRSSD() {
        List roleSet;
        Vector roleSetVector = new Vector();
        StringTokenizer st = new StringTokenizer(this.jList_SSD.getSelectedValue().toString(), ",");
        
        String temp;
        
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple roleSetTemplate = new LogicTuple("role_set_ssd", new Value(st.nextToken()), new Var("Y"));
            
            ITucsonOperation op_rdp = acc.rdp(rbac, roleSetTemplate, null);
            //System.err.println("results: " + op_rdp.getLogicTupleResult());
            roleSet = op_rdp.getLogicTupleResult().getArg(1).toList();
            
            for(int i=0;i<roleSet.size();i++)
                roleSetVector.add(roleSet.get(i).toString());
                /*LogicTuple rl = ctx.rdp(this.rbac, new LogicTuple("role_set_ssd", new Value(st.nextToken()), new Var("Y")));
                System.err.println("role_set: "+rl);
                roles = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    ro.add(roles.get(i).toString());
                }*/
            this.jList_RolesSSD.setListData(roleSetVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRolesRDSD() {
        List roleSet;
        Vector roleSetVector = new Vector();
        StringTokenizer st = new StringTokenizer(this.jList_DSD.getSelectedValue().toString(), ",");

        String temp;
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple roleSetTemplate = new LogicTuple("role_set_dsd", new Value(st.nextToken()), new Var("Y"));
            
            ITucsonOperation op_rdp = acc.rdp(rbac, roleSetTemplate, null);
            System.err.println("role_set: " + op_rdp.getLogicTupleResult());
            roleSet = op_rdp.getLogicTupleResult().getArg(1).toList();
            
            for(int i=0;i<roleSet.size();i++)
                roleSetVector.add(roleSet.get(i).toString());
                /*LogicTuple rl = ctx.rdp(this.rbac, new LogicTuple("role_set_dsd", new Value(st.nextToken()), new Var("Y")));
                System.err.println("role_set: "+rl);
                roleSet = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roleSet.size();i++)
                {
                    ro.add(roleSet.get(i).toString());
                }*/
            this.jList_RolesDSD.setListData(roleSetVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListSSD(){
        List<LogicTuple> ssdRules;
        Vector ssdRulesVector = new Vector();
        String temp;
        System.err.println("[fillListSSD@ManRBaCConstr] Method start!");
        try {
            LogicTuple ssdTemplate = new LogicTuple("ssd", new Var("X"), new Var("Z"));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdAll = acc.rdAll(rbac, ssdTemplate, null);
            ssdRules = op_rdAll.getLogicTupleListResult();
            System.err.println("ssd: " + ssdRules + " <> size: " + ssdRules.size());
            
            for(int i=0;i<ssdRules.size();i++) {
                temp = ssdRules.get(i).getArg(0).getName() + "," + ssdRules.get(i).getArg(1).intValue();
                //System.err.println("temp: " + temp);
                ssdRulesVector.add(temp);
            }
                /*LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("ssd", new Var("X"), new Var("Z")), new Var("Y")));
                System.err.println("ssd: "+rl);
                roles = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    rol = LogicTuple.parse(roles.get(i).toString());
                    temp = rol.getArg(0).getName();
                    temp += ",";
                    temp += rol.getArg(1).intValue();
                    ro.add(temp);
                    temp="";
                }*/
            this.jList_SSD.setListData(ssdRulesVector);
            acc.exit();
            System.err.println("[fillListSSD@ManRBaCConstr] Method end!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListDSD(){
        List<LogicTuple> dsdRules;
        Vector dsdRulesVector = new Vector();
        String temp;
        try {
            System.err.println("[fillListDSD@ManRBaCConstr] Method start!");
            LogicTuple dsdTemplate = new LogicTuple("dsd", new Var("X"), new Var("Z"));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdAll = acc.rdAll(rbac, dsdTemplate, null);
            dsdRules = op_rdAll.getLogicTupleListResult();
            System.err.println("dsd: " + dsdRules);
            
            for(int i=0; i<dsdRules.size(); i++) {
                temp = dsdRules.get(i).getArg(0).getName() + "," + dsdRules.get(i).getArg(1).intValue();
                dsdRulesVector.add(temp);
            }
                /*LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("dsd", new Var("X"), new Var("Z")), new Var("Y")));
                System.err.println("dsd: "+rl);
                roles = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    rol = LogicTuple.parse(roles.get(i).toString());
                    temp = rol.getArg(0).getName();
                    temp += ",";
                    temp += rol.getArg(1).intValue();
                    ro.add(temp);
                    temp="";
                }*/
            this.jList_DSD.setListData(dsdRulesVector);
            acc.exit();
            System.err.println("[fillListDSD@ManRBaCConstr] Method end!");
        } catch (Exception ex) {
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

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList_SSD = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList_RolesSSD = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_ROLES1 = new javax.swing.JList();
        jTextField_RSN_SSD = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jComboBoxSSD = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_DSD = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList_RolesDSD = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList_ROLES2 = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jComboBoxDSD = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jTextField_RSN_DSD = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 255));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(538, 343));

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(538, 343));

        jButton1.setText("DEL. SSD RULE");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList_SSD.setBackground(new java.awt.Color(255, 255, 204));
        jList_SSD.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_SSD.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_SSD.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_SSDValueChanged(evt);
            }
        });
        jList_SSD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jList_SSDFocusGained(evt);
            }
        });
        jList_SSD.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jList_SSDPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jList_SSD);

        jList_RolesSSD.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane4.setViewportView(jList_RolesSSD);

        jLabel5.setText("ROLE SET:");

        jLabel1.setText("SSD RULES:");

        jLabel3.setText("ROLE SET NAME:");

        jList_ROLES1.setBackground(new java.awt.Color(255, 255, 204));
        jList_ROLES1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList_ROLES1);

        jTextField_RSN_SSD.setBackground(new java.awt.Color(255, 255, 204));
        jTextField_RSN_SSD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RSN_SSDActionPerformed(evt);
            }
        });

        jLabel6.setText("CARD.:");

        jButton3.setText("SAVE NEW SSD RULE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jComboBoxSSD.setBackground(new java.awt.Color(255, 255, 204));
        jComboBoxSSD.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", " " }));

        jLabel2.setText("ROLES:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButton5.setText("RBAC MENU");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("MENU");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("LOGOUT");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addComponent(jButton1))
                .addGap(14, 14, 14)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jTextField_RSN_SSD, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jComboBoxSSD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton3))
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(101, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addGap(141, 141, 141))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton5, jButton6, jButton7});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane4))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTextField_RSN_SSD, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxSSD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addGap(78, 78, 78))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("SSD RULES", jPanel2);

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(538, 343));

        jLabel4.setText("DSD RULES:");

        jList_DSD.setBackground(new java.awt.Color(255, 255, 204));
        jList_DSD.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_DSD.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_DSD.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_DSDValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList_DSD);

        jButton2.setText("DEL. DSD RULE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jList_RolesDSD.setBackground(new java.awt.Color(255, 255, 204));
        jList_RolesDSD.setPreferredSize(new java.awt.Dimension(33, 80));
        jScrollPane5.setViewportView(jList_RolesDSD);

        jLabel7.setText("ROLE SET:");

        jLabel8.setText("ROLES:");

        jList_ROLES2.setBackground(new java.awt.Color(255, 255, 204));
        jList_ROLES2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(jList_ROLES2);

        jButton4.setText("SAVE NEW DSD RULE");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBoxDSD.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        jComboBoxDSD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDSDActionPerformed(evt);
            }
        });

        jLabel10.setText("CARD.:");

        jTextField_RSN_DSD.setBackground(new java.awt.Color(255, 255, 204));
        jTextField_RSN_DSD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RSN_DSDActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButton8.setText("LOGOUT");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("MENU");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("RBAC MENU");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel11.setText("ROLE SET NAME:");

        jButton23.setText("LOGOUT");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setText("LOGOUT");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(111, Short.MAX_VALUE)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addGap(131, 131, 131))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton2))
                .addGap(16, 16, 16)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_RSN_DSD, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jComboBoxDSD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton10, jButton8, jButton9});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton8)
                    .addComponent(jButton10))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3)
                                    .addComponent(jScrollPane5))
                                .addGap(18, 18, 18)
                                .addComponent(jButton2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jTextField_RSN_DSD, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxDSD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jButton4))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DSD RULES", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_RSN_DSDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RSN_DSDActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_RSN_DSDActionPerformed

    private void jTextField_RSN_SSDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RSN_SSDActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_RSN_SSDActionPerformed

    private void jList_DSDValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_DSDValueChanged
        // TODO add your handling code here:
        if(!this.jList_DSD.isSelectionEmpty() && !this.jList_DSD.getValueIsAdjusting())
            fillListRolesRDSD();
    }//GEN-LAST:event_jList_DSDValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        save_new_ssd();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        save_new_dsd();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList_SSDPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jList_SSDPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_jList_SSDPropertyChange

    private void jList_SSDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jList_SSDFocusGained
        // TODO add your handling code here:
}//GEN-LAST:event_jList_SSDFocusGained

    private void jList_SSDValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_SSDValueChanged
        // TODO add your handling code here:
        if(!this.jList_SSD.isSelectionEmpty() && !this.jList_SSD.getValueIsAdjusting())
            fillListRolesRSSD();
}//GEN-LAST:event_jList_SSDValueChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(user);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(user);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jComboBoxDSDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDSDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxDSDActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        delete_ssd_rule(this.jList_SSD.getSelectedValue().toString());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        delete_dsd_rule(this.jList_DSD.getSelectedValue().toString());
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBoxDSD;
    private javax.swing.JComboBox jComboBoxSSD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList jList_DSD;
    private javax.swing.JList jList_ROLES1;
    private javax.swing.JList jList_ROLES2;
    private javax.swing.JList jList_RolesDSD;
    private javax.swing.JList jList_RolesSSD;
    private javax.swing.JList jList_SSD;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField_RSN_DSD;
    private javax.swing.JTextField jTextField_RSN_SSD;
    // End of variables declaration//GEN-END:variables

    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }

    private void save_new_dsd() {
        String roleSetName = this.jTextField_RSN_DSD.getText();
        List<String> roles = this.jList_ROLES2.getSelectedValuesList();//.getSelectedValues();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple rsdsdTemplate = new LogicTuple("role_set_dsd", new Value(roleSetName), new Var("X"));
            
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("role_set_dsd", new Value(roleSetName), new Var("X")));
            ITucsonOperation op_rdp = acc.rdp(rbac, rsdsdTemplate, null);
            
            //if(usro == null){
            if(op_rdp.isResultFailure())
                for(int i=0;i<roles.size();i++){
                    System.err.println("[save_new_dsd@ManRBACConstr] roles.get(i): " + roles.get(i));
                    //ctx.out(this.rbac, new LogicTuple("role_set_dsd", new Value(roleSetName),new Value((String)roles[i])));
                    acc.out(rbac, new LogicTuple("role_set_dsd", new Value(roleSetName),new Value(roles.get(i))), null);
                }
            else
                javax.swing.JOptionPane.showMessageDialog(null,"Role Set already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            op_rdp = acc.rdp(rbac, new LogicTuple("dsd", new Value(roleSetName), new Value(this.jComboBoxSSD.getSelectedIndex()+1)), null);
            if(op_rdp.isResultFailure()) {
                acc.out(this.rbac, new LogicTuple("dsd", new Value(roleSetName), new Value(this.jComboBoxSSD.getSelectedIndex()+1)), null);
                fillListDSD();
            } else
                javax.swing.JOptionPane.showMessageDialog(null,"DSD Rule already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    

    private void save_new_ssd() {
        String roleSetName = this.jTextField_RSN_SSD.getText();
        List<String> roles = this.jList_ROLES1.getSelectedValuesList();//.getSelectedValues();
        
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple rsssdTemplate = new LogicTuple("role_set_ssd", new Value(roleSetName), new Var("X"));
            
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("role_set_ssd", new Value(roleSetName), new Var("X")), null);
            if(op_rdp.isResultFailure()) {
                for(int i=0;i<roles.size();i++){
                    acc.out(this.rbac, new LogicTuple("role_set_ssd", new Value(roleSetName),new Value(roles.get(i))), null);
                }
            } else
                javax.swing.JOptionPane.showMessageDialog(null,"Role Set already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            op_rdp = acc.rdp(rbac, new LogicTuple("ssd", new Value(roleSetName), new Value(this.jComboBoxSSD.getSelectedIndex()+1)), null);
            if(op_rdp.isResultFailure()) {
                acc.out(this.rbac, new LogicTuple("ssd", new Value(roleSetName), new Value(this.jComboBoxSSD.getSelectedIndex()+1)), null);
                fillListSSD();
            } else
                javax.swing.JOptionPane.showMessageDialog(null,"SSD Rule already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
