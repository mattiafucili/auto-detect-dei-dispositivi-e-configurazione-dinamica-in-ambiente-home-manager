/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManLocalConstr.java
 *
 * Created on 19-set-2009, 10.29.07
 */

package it.unibo.homemanager.userinterfaces;

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.User;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class ManRoomsConstr extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private Device dev;
    private User user;
    private String ass_to_change;
    private Vector rooms;
    
    private TucsonAgentId agent;
    
    private RoomServiceInterface roomService;
    private Database database;

    /** Creates new form ManLocalConstr */
    public ManRoomsConstr(MainPanel mainp, TucsonTupleCentreId tc, RBACManager rm, ServiceFactory sf) {
        this.mp = mainp;
        this.rbac = tc;
        this.rbac_man = rm;
	this.roomService = sf.getRoomServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("manageRoomsConstraint");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

    public void init(User u){
        this.user = u;
        fillRoomConstr();
        fillListRoles();
        fillListRooms();
//        fillListPerms();
//        fillListRoles();
    }

    private void fillListRoles(){
        List<LogicTuple> roles;
        Vector rolesVector = new Vector();
        //Vector ro1 = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("role", new Var("X")), null);
            roles = op_rdAll.getLogicTupleListResult();
            //    LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("role", new Var("X")), new Var("Y")));
                System.err.println("roles: "+roles);
            //    roles = rl.getArg(1).toList();

            //    LogicTuple rol;
            rolesVector.add("_");
            for(int i=0;i<roles.size();i++)
                rolesVector.add(roles.get(i).getArg(0).getName());
                /*{
                    rol = LogicTuple.parse(roles.get(i).toString());
                    ro.add(rol.getArg(0).getName());
                    ro1.add(rol.getArg(0).getName());
                }*/
           
            this.jList1.setListData(rolesVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRooms(){
        //List roles;
        Vector ro = new Vector();
        try {
            //EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);

            rooms = roomService.getRooms(database.getDatabase());
            for(int i=0; i<rooms.size(); i++)
                ro.add(((Room)rooms.get(i)).name);
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("permission", new Var("X")), new Var("Y")));
                //System.err.println("classes: "+rl);
                //roles = rl.getArg(1).toList();

                //LogicTuple rol;
                //for(int i=0;i<roles.size();i++)
                //{
                //    rol = LogicTuple.parse(roles.get(i).toString());
                //    for(int j=0;j<rooms.size();j++)
                //    {
                //        if(rol.getArg(0).getName().equals(((Room)rooms.elementAt(j)).name))
                //                ro.add(rol.getArg(0).getName());
                //    }  
                //}
            this.jList2.setListData(ro);
            //acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }
        
    private void fillRoomConstr() {
        List<LogicTuple> constraints;
        Vector ro = new Vector();
        try {
//            TupleArgument[] ta = new TupleArgument[9];
//            for(int i=0;i<ta.length;i++){
//                ta[i] = new Var();
//            }
                //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all",
                //        new Value("room_rule", new Var("A"), new Var("B"), new Var("C"),
                //        new Var("D"), new Var("E"), new Var("F"), new Var("G")),
                //        new Var("Z")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple roomRuleTemplate = new LogicTuple("room_rule", new Var("A"), new Var("B"), new Var("C"),
                        new Var("D"), new Var("E"), new Var("F"), new Var("G"));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, roomRuleTemplate, null);
            constraints = op_rdAll.getLogicTupleListResult();
                System.err.println("room_rule: " + constraints);
                //constraints = rl.getArg(1).toList();
            String ass = "";

                //LogicTuple rol;
            for(int i=0;i<constraints.size();i++)
            {
                    //rol = LogicTuple.parse(constraints.get(i).toString());

                ass = String.valueOf(constraints.get(i).getArg(0).intValue());
                ass += "-";
                ass += constraints.get(i).getArg(1).getName();
                ro.add(ass);
                    //ass = "";
            }
            acc.exit();
            this.jList_DevRol.setListData(ro);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



     private void fillAllFields() {
        String dev_constr = this.jList_DevRol.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(dev_constr, "-");
        int val = Integer.parseInt(st.nextToken());
        try{
            //LogicTuple rl = ctx.rdp(this.rbac, new LogicTuple("room_rule", new Value(val), new Var("A")
            //        , new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //room_rule(3,'Garage','Colf','_',['_','_'],['_','_'],['Tuesday','Wednesday'])
            LogicTuple roomRule = new LogicTuple("room_rule", new Value(val), new Var("A"), new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F"));
            ITucsonOperation op_rdp = acc.rdp(rbac, roomRule, null);
            LogicTuple rl = op_rdp.getLogicTupleResult();
            System.err.println("ass. dev_rule: "+rl);
            this.jTextField_ClDev.setText(rl.getArg(1).getName());
            this.jTextField_Role.setText(rl.getArg(2).getName());
            String sta = rl.getArg(3).getName();
            this.jTextField_State.setText(sta);
            List l;
            l = rl.getArg(4).toList();
            this.jTextField_Fh.setText(l.get(0).toString());
            this.jTextField_TH.setText(l.get(1).toString());
            l = rl.getArg(5).toList();
            this.jTextField_FD.setText(l.get(0).toString());
            this.jTextField_TD.setText(l.get(1).toString());
            l = rl.getArg(6).toList();
            this.jTextField_FDay.setText(l.get(0).toString());
            this.jTextField_TDay.setText(l.get(1).toString());
            acc.exit();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListState() {
        Vector states = new Vector();
        List<LogicTuple> l;
        LogicTuple stato;
        try {
            String locale  = this.jList2.getSelectedValue().toString().toLowerCase();
            TucsonTupleCentreId tc = new TucsonTupleCentreId(locale+"_tc");
            //LogicTuple lt = ctx.inp(tc, new LogicTuple("rd_all", new Value("state", new Var("X")), new Var("Y")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(tc, new LogicTuple("state", new Var("X")), null);
            l = op_rdAll.getLogicTupleListResult();
            
            for(int i=0;i<l.size();i++){
                if(i==0)
                    states.add("_");
                stato = LogicTuple.parse(l.get(i).toString());
                states.add(stato.getArg(0).getName());
            }
            this.jList_RO.setListData(states);
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(ManRoomsConstr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


     private void delete_room_rule() {
        String dev_constr = this.jList_DevRol.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(dev_constr, "-");
        int val = Integer.parseInt(st.nextToken());
        try{
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("room_rule", new Value(val), new Var("A")
            //        , new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")));
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //Original
            ITucsonOperation op_inp = acc.in(rbac, new LogicTuple("room_rule", new Value(val), new Var("A"), new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")), null);
            //ReSpecT reactions..
            //ITucsonOperation op_inp = acc.in(rbac, new LogicTuple("room_rule", new Value(val), new Var("A"), new Var("B"), new Var("C"), new Var("D"), new Var("E"), new Var("F")), Long.MAX_VALUE);
            
            System.err.println("ass. room_rule: "+op_inp.getLogicTupleResult());
            acc.exit();
            fillRoomConstr();
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
        jList_DevRol = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Role = new javax.swing.JTextField();
        jTextField_ClDev = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_Fh = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_TD = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField_FDay = new javax.swing.JTextField();
        jTextField_TDay = new javax.swing.JTextField();
        jTextField_FD = new javax.swing.JTextField();
        jTextField_TH = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_RO = new javax.swing.JList();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField_State = new javax.swing.JTextField();

        setBackground(new java.awt.Color(204, 204, 255));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jList_DevRol.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_DevRolValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList_DevRol);

        jLabel3.setText("ROLE:");

        jTextField_Role.setEditable(false);
        jTextField_Role.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RoleActionPerformed(evt);
            }
        });

        jTextField_ClDev.setEditable(false);

        jLabel4.setText("STATE:");

        jLabel1.setText("ROOM CONSTR.:");

        jLabel2.setText("ROOM:");

        jLabel5.setText("from HOUR:");

        jTextField_Fh.setEditable(false);

        jLabel6.setText("to HOUR:");

        jLabel7.setText("from DATE:");

        jLabel8.setText("to DATE:");

        jTextField_TD.setEditable(false);

        jLabel10.setText("to DAY:");

        jLabel9.setText("from DAY:");

        jTextField_FDay.setEditable(false);

        jTextField_TDay.setEditable(false);
        jTextField_TDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_TDayActionPerformed(evt);
            }
        });

        jTextField_FD.setEditable(false);

        jTextField_TH.setEditable(false);

        jButton1.setText("DEL. DEV. CONSTR.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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

        jLabel13.setText("ROOM:");

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
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jList2);

        jList_RO.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(jList_RO);

        jLabel12.setText("STATE:");

        jLabel14.setText("from HOUR:");

        jTextField12.setText("xx:xx");
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField12FocusGained(evt);
            }
        });

        jLabel17.setText("to HOUR:");

        jTextField11.setText("xx:xx");
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField11FocusGained(evt);
            }
        });

        jLabel15.setText("from DATE:");

        jLabel18.setText("to DATE:");

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "_", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "_", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" }));

        jLabel16.setText("from DAY:");

        jLabel19.setText("to DAY:");

        jButton2.setText("SAVE CONSTR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField_State.setEditable(false);
        jTextField_State.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_StateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(126, 126, 126)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                            .addComponent(jButton1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_State, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField_Role, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel3))
                                    .addComponent(jTextField_ClDev, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_Fh, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField_TH, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jTextField_TD, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_FD, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField_FDay)
                            .addComponent(jTextField_TDay, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(layout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(jLabel13))
                                .addComponent(jScrollPane4)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(91, 91, 91)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel14)
                                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel15)
                                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING))
                                .addComponent(jLabel19))
                            .addGap(77, 77, 77))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jSeparator2)
                            .addContainerGap()))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton3, jButton4, jButton5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(6, 6, 6)
                        .addComponent(jTextField_Fh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addGap(6, 6, 6)
                        .addComponent(jTextField_TH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_FDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField_TDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField_FD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField_TD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField_ClDev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_State, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField_Role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jButton2)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(195, 195, 195)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(jLabel16))
                            .addGap(6, 6, 6)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(13, 13, 13)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList_DevRolValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_DevRolValueChanged
        // TODO add your handling code here:
        if(!this.jList_DevRol.isSelectionEmpty())
            fillAllFields();
}//GEN-LAST:event_jList_DevRolValueChanged

    private void jTextField_RoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RoleActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_RoleActionPerformed

    private void jTextField_TDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_TDayActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_TDayActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        delete_room_rule();
}//GEN-LAST:event_jButton1ActionPerformed

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

    private void jTextField12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusGained
        // TODO add your handling code here:
        this.jTextField12.setText("");
}//GEN-LAST:event_jTextField12FocusGained

    private void jTextField11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusGained
        // TODO add your handling code here:
        this.jTextField11.setText("");
}//GEN-LAST:event_jTextField11FocusGained

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
        save_new_room_rule();
}//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField_StateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_StateActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jTextField_StateActionPerformed

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        // TODO add your handling code here:
       if(!this.jList2.isSelectionEmpty())
        fillListState();
    }//GEN-LAST:event_jList2ValueChanged


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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
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
    private javax.swing.JTextField jTextField_State;
    private javax.swing.JTextField jTextField_TD;
    private javax.swing.JTextField jTextField_TDay;
    private javax.swing.JTextField jTextField_TH;
    // End of variables declaration//GEN-END:variables

    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }

    private void save_new_room_rule() {
         String clas = this.jList2.getSelectedValue().toString();
        String role = this.jList1.getSelectedValue().toString();
        String state = this.jList_RO.getSelectedValue().toString();
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

        String hour = "['"+fh+"','"+th+"']";
        System.out.println(hour);
        String date = "['"+fd+"','"+td+"']";
        System.out.println(date);
        String days = "['"+fday+"','"+tday+"']";
        System.out.println(days);

        try{
            LogicTuple lt = LogicTuple.parse("room_rule('"+clas+"','"+role+"','"+state+"',"+hour+","+date+","+days+")");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //Utilities.insertTupleInto(rbac, lt);
            acc.out(rbac, lt, null);
            acc.exit();
            fillRoomConstr();
        }catch(Exception ex){ex.printStackTrace();}
    }
}
