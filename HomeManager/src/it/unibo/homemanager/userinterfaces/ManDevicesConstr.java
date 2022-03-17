/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManDevicesConstr.java
 *
 * Created on 19-set-2009, 10.29.25
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.util.Utilities;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
/**
 *
 * @author Administrator
 */
public class ManDevicesConstr extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private Device dev;
    private User user;
    private String ass_to_change;
    
    TucsonAgentId agent;

    /** Creates new form ManDevicesConstr */
    public ManDevicesConstr(MainPanel mainp, TucsonTupleCentreId tc, RBACManager rm) {
        this.mp = mainp;
        this.rbac = tc;
        this.rbac_man = rm;
        try {
            agent = new TucsonAgentId("manDevConstrAgent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

    public void init(User u){
        this.user = u;
        fillDevConstr();
        fillListRoles();
        fillListClasses();
//        fillListPerms();
//        fillListRoles();
    }

    private void delete_dev_rule() {
        String dev_constr = this.jList_DevRol.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(dev_constr, "-");
        int val = Integer.parseInt(st.nextToken());
        
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //LogicTuple rl = acc.inp(this.rbac, new LogicTuple("dev_rule", new Value(val), new Var("A")
            //        , new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")));
            //Original
            ITucsonOperation op_inp = acc.inp(rbac, new LogicTuple("dev_rule", new Value(val), new Var("A"), new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")), null);
            //ReSpecT reactions..
            //ITucsonOperation op_inp = acc.in(rbac, new LogicTuple("dev_rule", new Value(val), new Var("A"), new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")), Long.MAX_VALUE);
            
            System.err.println("ass. dev_rule: " + op_inp.getLogicTupleResult());
            fillDevConstr();
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillAllFields() {
        String dev_constr = this.jList_DevRol.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(dev_constr, "-");
        int val = Integer.parseInt(st.nextToken());
        
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple rl = ctx.rdp(this.rbac, new LogicTuple("dev_rule", new Value(val), new Var("A")
            //        , new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")));
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("dev_rule", new Value(val), new Var("A")
                    , new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")), null);
            LogicTuple rl  = op_rdp.getLogicTupleResult();
            System.err.println("ass. dev_rule: "+rl);
            this.jTextField_ClDev.setText(rl.getArg(1).getName());
            this.jTextField_Role.setText(rl.getArg(2).getName());
            List l = rl.getArg(3).toList();
            Vector vec = new Vector();
            for(int i=0;i<l.size();i++){
                vec.add(l.get(i));
            }
            this.jList_Roles.setListData(vec);
            l = rl.getArg(4).toList();
            this.jTextField_Fh.setText(l.get(0).toString());
            this.jTextField_TH.setText(l.get(1).toString());
            l = rl.getArg(5).toList();
            this.jTextField_FD.setText(l.get(0).toString());
            this.jTextField_TD.setText(l.get(1).toString());
            l = rl.getArg(6).toList();
            this.jTextField_FDay.setText(l.get(0).toString());
            this.jTextField_TDay.setText(l.get(1).toString());
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillDevConstr() {
        List roles;
        Vector ro = new Vector();
        try {
//          TupleArgument[] ta = new TupleArgument[9];
//          for(int i=0;i<ta.length;i++){
//              ta[i] = new Var();
//          }
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", 
            //        new Value("dev_rule", new Var("A"), new Var("B"), new Var("C"),
            //        new Var("D"), new Var("E"), new Var("F"), new Var("G")),
            //        new Var("Z")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("dev_rule", new Var("A"), new Var("B"), new Var("C"),
                    new Var("D"), new Var("E"), new Var("F"), new Var("G")), null);
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("ass. roles-perm: " + roles);
            //roles = rl.getArg(1).toList();
            String ass = "";

            LogicTuple rol;
            for(int i=0;i<roles.size();i++) {
                rol = LogicTuple.parse(roles.get(i).toString());

                ass = String.valueOf(rol.getArg(0).intValue());
                ass += "-";
                ass += rol.getArg(1).getName();
                ro.add(ass);
                ass = "";
            }
            
            this.jList_DevRol.setListData(ro);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRoles(){
        List roles;
        Vector ro = new Vector();
        Vector ro1 = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("role", new Var("X")), new Var("Y")));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("role", new Var("X")), null);
            System.err.println("roles: "+ op_rdAll.getLogicTupleListResult());
            roles = op_rdAll.getLogicTupleListResult();

            LogicTuple rol;
            ro1.add("_");
            for(int i=0;i<roles.size();i++) {
                rol = LogicTuple.parse(roles.get(i).toString());
                ro.add(rol.getArg(0).getName());
                ro1.add(rol.getArg(0).getName());
                
                this.jList_RO.setListData(ro1);
                this.jList1.setListData(ro);
            }
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     private void save_new_dev_rule(){
        String clas = this.jList2.getSelectedValue().toString();
        String role = this.jList1.getSelectedValue().toString();
        Object[] roles = this.jList_RO.getSelectedValues();
        String fh = this.jTextField11.getText();
        if(fh.matches("xx:xx")|| fh.equals("")) fh = "_";
        String th = this.jTextField12.getText();
        if(th.matches("xx:xx")|| th.equals("")) th = "_";
        String fd = this.jTextField13.getText();
        if(fd.matches("xx/xx/xxxx")|| fd.equals("")) fd = "_";
        String td = this.jTextField15.getText();
        if(td.matches("xx/xx/xxxx")|| td.equals("")) td = "_";
        String fday = this.jComboBox1.getSelectedItem().toString();
        String tday = this.jComboBox2.getSelectedItem().toString();
        String rp = "[";
        for(int i=0;i<roles.length;i++){
            rp += "'";
            rp += roles[i].toString();
            rp += "'";
            if(i != roles.length-1)
            rp += ",";
        }
        rp +="]";
        System.out.println(rp);
        String hour = "['"+fh+"','"+th+"']";
        System.out.println(hour);
        String date = "['"+fd+"','"+td+"']";
        System.out.println(date);
        String days = "['"+fday+"','"+tday+"']";
        System.out.println(days);
        
        LogicTuple lt;
        try {
            lt = LogicTuple.parse("dev_rule('"+clas+"','"+role+"',"+rp+","+hour+","+date+","+days+")");
            Utilities.insertTupleInto(rbac, lt);
            fillDevConstr();
        } catch (InvalidLogicTupleException ex) {
            Logger.getLogger(ManDevicesConstr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     private void fillListClasses(){
        List roles;
        Vector ro = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("class", new Var("X")), new Var("Y")));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("class", new Var("X")), null);
            System.err.println("classes: "+op_rdAll.getLogicTupleListResult());
            //if(rl!=null){
            if(op_rdAll.isResultSuccess()) {
                //roles = rl.getArg(1).toList();
                roles = op_rdAll.getLogicTupleListResult();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    rol = LogicTuple.parse(roles.get(i).toString());
                    ro.add(rol.getArg(0).getName());
                }
                this.jList2.setListData(ro);
            }
            acc.exit();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList_DevRol = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jTextField_ClDev = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_Role = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField_TH = new javax.swing.JTextField();
        jTextField_Fh = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Roles = new javax.swing.JList();
        jTextField_FD = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_TD = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField_FDay = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_TDay = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_RO = new javax.swing.JList();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();

        setBackground(new java.awt.Color(204, 204, 255));
        setMinimumSize(new java.awt.Dimension(559, 343));
        setPreferredSize(new java.awt.Dimension(559, 343));

        jList_DevRol.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_DevRol.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_DevRolValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList_DevRol);

        jLabel1.setText("DEV. CONSTR.:");

        jTextField_ClDev.setEditable(false);

        jLabel2.setText("CLASS DEV.:");

        jTextField_Role.setEditable(false);
        jTextField_Role.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RoleActionPerformed(evt);
            }
        });

        jLabel3.setText("ROLE:");

        jLabel4.setText("PRESENCE:");

        jTextField_TH.setEditable(false);

        jTextField_Fh.setEditable(false);

        jLabel5.setText("from HOUR:");

        jLabel6.setText("to HOUR:");

        jScrollPane2.setViewportView(jList_Roles);

        jTextField_FD.setEditable(false);

        jLabel7.setText("from DATE:");

        jTextField_TD.setEditable(false);

        jLabel8.setText("to DATE:");

        jTextField_FDay.setEditable(false);

        jLabel9.setText("from DAY:");

        jTextField_TDay.setEditable(false);
        jTextField_TDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_TDayActionPerformed(evt);
            }
        });

        jLabel10.setText("to DAY:");

        jButton1.setText("DEL. DEV. CONSTR.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList_RO.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_RO);

        jLabel12.setText("PRESENCE:");

        jLabel13.setText("CLASS DEV.:");

        jLabel14.setText("from HOUR:");

        jLabel15.setText("from DATE:");

        jLabel16.setText("from DAY:");

        jTextField11.setText("xx:xx");
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField11FocusGained(evt);
            }
        });

        jTextField12.setText("xx:xx");
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField12FocusGained(evt);
            }
        });

        jLabel17.setText("to HOUR:");

        jTextField13.setText("xx/xx/xxxx");
        jTextField13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField13FocusGained(evt);
            }
        });

        jTextField15.setText("xx/xx/xxxx");
        jTextField15.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField15FocusGained(evt);
            }
        });

        jLabel18.setText("to DATE:");

        jLabel19.setText("to DAY:");

        jButton2.setText("SAVE CONSTR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "_", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "_", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(jList1);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel13)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane4)
                            .addComponent(jScrollPane5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel18)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jTextField_ClDev, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField_Role, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jTextField_Fh, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jTextField_FD, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(17, 17, 17)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel10)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jTextField_FDay, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(jTextField_TH, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(20, 20, 20)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jTextField_TD, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_TDay, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton3, jButton4, jButton5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jButton5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField_Fh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_FDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_FD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField_TH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField_TDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField_TD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField_ClDev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_Role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel13)
                                .addGap(2, 2, 2)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_RoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RoleActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_RoleActionPerformed

    private void jTextField_TDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_TDayActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_TDayActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jList_DevRolValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_DevRolValueChanged
        // TODO add your handling code here:
        if(!this.jList_DevRol.isSelectionEmpty())
            fillAllFields();
}//GEN-LAST:event_jList_DevRolValueChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(this.user);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        delete_dev_rule();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusGained
        // TODO add your handling code here:
       this.jTextField11.setText("");
    }//GEN-LAST:event_jTextField11FocusGained

    private void jTextField12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusGained
        // TODO add your handling code here:
        this.jTextField12.setText("");
    }//GEN-LAST:event_jTextField12FocusGained

    private void jTextField13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusGained
        // TODO add your handling code here:
        this.jTextField13.setText("");
    }//GEN-LAST:event_jTextField13FocusGained

    private void jTextField15FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField15FocusGained
        // TODO add your handling code here:
        this.jTextField15.setText("");
    }//GEN-LAST:event_jTextField15FocusGained

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        save_new_dev_rule();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList_DevRol;
    private javax.swing.JList jList_RO;
    private javax.swing.JList jList_Roles;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField_ClDev;
    private javax.swing.JTextField jTextField_FD;
    private javax.swing.JTextField jTextField_FDay;
    private javax.swing.JTextField jTextField_Fh;
    private javax.swing.JTextField jTextField_Role;
    private javax.swing.JTextField jTextField_TD;
    private javax.swing.JTextField jTextField_TDay;
    private javax.swing.JTextField jTextField_TH;
    // End of variables declaration//GEN-END:variables

    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }

}