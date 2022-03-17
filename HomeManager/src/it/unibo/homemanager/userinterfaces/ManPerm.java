/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ManPerm.java
 *
 * Created on 19-set-2009, 10.28.13
 */

package it.unibo.homemanager.userinterfaces;

import alice.logictuple.*;
import alice.tucson.api.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.User;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author Administrator
 */
public class ManPerm extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private User user;
    private String ass_to_change;
    private Vector rooms;
    private ArrayList<Device> devices;
    
    private ServiceFactory sf;
    private Database database;
    private RoomServiceInterface rm;
    private DeviceServiceInterface dev;
    
    private TucsonAgentId agent;
    
    /** Creates new form ManPerm */
    public ManPerm(MainPanel manp, TucsonTupleCentreId tc, RBACManager rm, ServiceFactory sf) {
        this.mp = manp;
        this.rbac = tc;
        this.rbac_man = rm;
	this.sf = sf;
        this.rm = sf.getRoomServiceInterface();
        this.dev = sf.getDeviceServiceInterface();
        try {
            this.agent = new TucsonAgentId("managePermissionsAgent");
	    this.database = sf.getDatabaseInterface();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }


     public void init(User u){
        this.user = u;
        fillListPerm();
        fillListRooms();
        fillListDevs();
        fillListClasses();
        fillListAssDevCla();
    }

    private void delete_ass() {
        String rol;
        rol = this.jList_DevCla.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(rol, "-");
        String clas = st.nextToken();
        String dev = st.nextToken();
        
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("ass_class_devs", new Value(clas), new Value(dev)));
            /*ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("ass_class_devs", new Value(clas), new Value(dev)), null);
            if(op_rdp.isResultFailure()) {
                javax.swing.JOptionPane.showMessageDialog(null,"Association doesn't exsist!",
                "",javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                acc.in(rbac, new LogicTuple("ass_class_devs", new Value(clas), new Value(dev)), Long.MAX_VALUE);
            //ctx.in(this.rbac, new LogicTuple("ass_class_devs", new Value(clas), new Value(dev)));
                fillListAssDevCla();
            }*/
            ITucsonOperation op_inp = acc.inp(rbac, new LogicTuple("ass_class_devs", new Value(clas), new Value(dev)), null);
            if(op_inp.isResultSuccess())
                fillListAssDevCla();
            else
                javax.swing.JOptionPane.showMessageDialog(null,"Association doesn't exsist!",
                "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void delete_perm() {
        //String role = "'"+del_role+"'";
        String rol = this.jText_Permission.getText();
        
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_inp = acc.inp(rbac, new LogicTuple("permission", new Value(rol)), null);
            if(op_inp.isResultSuccess())
                fillListPerm();
            else
                javax.swing.JOptionPane.showMessageDialog(null,"Association doesn't exsist!",
                "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
            /*LogicTuple usro = ctx.rdp(rbac, new LogicTuple("permission", new Value(rol)));
            if(usro == null){javax.swing.JOptionPane.showMessageDialog(null,"Association not exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);}
            else{
            ctx.in(this.rbac, new LogicTuple("permission",  new Value(rol)));
            fillListPerm();
            }*/
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListPerm(){
        List roles;
        Vector ro = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("permission", new Var("X")), new Var("Z")));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("permission", new Var("X")), null);
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("ass. perm: " + roles);
            //roles = rl.getArg(1).toList();
            String ass = "";

            LogicTuple rol;
            for(int i=0;i<roles.size();i++) {
                rol = LogicTuple.parse(roles.get(i).toString());

                ass = "Permesso ";
                ass += ": ";
                ass += rol.getArg(0).getName();
                ro.add(ass);
                ass = "";
            }
            this.jList_Permissions.setListData(ro);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListRooms() {
        
        String ut = "";
        try {
            this.jList_Rooms.removeAll();
            rooms = rm.getRooms(database.getDatabase());
            Vector items = new Vector();
            for(int i=0;i<rooms.size();i++) {
                Room usr = (Room)rooms.elementAt(i);
              //  ut = String.valueOf(usr.idRoom);
              //  ut += "-";
                ut = usr.name;
                items.add(ut);
                ut="";
            }
            this.jList_Rooms.setListData(items);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListDevs() {
        
        String ut = "";
        try {
            this.jList_Devices.removeAll();
            devices = dev.getDevices(database.getDatabase());
            Vector items = new Vector();
            
            for(int i=0;i<devices.size();i++) {
                //Device dev = (Device)devices.elementAt(i);
		Device dev = devices.get(i);
                ut = dev.getDeviceName();
                items.add(ut);
                ut="";
            }
            this.jList_Devices.setListData(items);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListClasses() {
        List<LogicTuple> classes;
        Vector classesVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            //LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("permission", new Var("X")), new Var("Z")));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("class", new Var("X")), null);
            classes = op_rdAll.getLogicTupleListResult();
            System.err.println("classes: " + classes);
            //roles = rl.getArg(1).toList();

            LogicTuple rol;
            for(int i=0;i<classes.size();i++) {
                //rol = LogicTuple.parse(classes.get(i).toString());
                //ro.add(rol.getArg(0).getName());
                //System.err.println("ciclo for: " + rol.getArg(0).getName() + " vs " + classes.get(i).getArg(0).getName());
                classesVector.add(classes.get(i).getArg(0).getName());
            }
            this.jList_Classes.setListData(classesVector);
            acc.exit();
                /*LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("class", new Var("X")), new Var("Y")));
                System.err.println("classes: "+rl);
                if(rl!=null){
                    roles = rl.getArg(1).toList();

                    LogicTuple rol;
                    for(int i=0;i<roles.size();i++)
                    {
                        rol = LogicTuple.parse(roles.get(i).toString());
                        ro.add(rol.getArg(0).getName());
                    }
                    this.jList_Classes.setListData(ro);
                }*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
     }

     private void fillListAssDevCla(){
        List associations;
        Vector ro = new Vector();
        String ass;
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("ass_class_devs", new Var("X"), new Var("Z")), null);
            associations = op_rdAll.getLogicTupleListResult();
            System.err.println("associations: " + associations);

            LogicTuple rol;
            for(int i=0;i<associations.size();i++) {
                rol = LogicTuple.parse(associations.get(i).toString());

                ass = rol.getArg(0).getName();
                ass +="-";
                ass +=rol.getArg(1).getName();
                ro.add(ass);
                System.err.println("ass: " + ass);
            }
            this.jList_DevCla.setListData(ro);
            acc.exit();
                /*LogicTuple rl = ctx.inp(this.rbac, new LogicTuple("rd_all", new Value("ass_class_devs", new Var("X"), new Var("Z")), new Var("Y")));
                System.err.println("classes: "+rl);
                roles = rl.getArg(1).toList();

                LogicTuple rol;
                for(int i=0;i<roles.size();i++)
                {
                    rol = LogicTuple.parse(roles.get(i).toString());
                    ass = rol.getArg(0).getName();
                    ass +="-";
                    ass +=rol.getArg(1).getName();
                    ro.add(ass);
                    ass="";
                }
            this.jList_DevCla.setListData(ro);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }

     private void save_new_association(){
        String rol;
        String temp;

        rol = this.jList_Classes.getSelectedValue().toString();
        temp = this.jList_Devices.getSelectedValue().toString();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple assClassDevsTemplate = new LogicTuple("ass_class_devs", new Var("X"), new Value(temp));
            //LogicTuple usro = ctx.rdp(rbac, new LogicTuple("ass_class_devs", new Var("X"), new Value(temp)));
            ITucsonOperation op_rdp = acc.rdp(rbac, assClassDevsTemplate, null);
            //if(usro == null){
            if(op_rdp.isResultFailure()) {
                acc.out(this.rbac, new LogicTuple("ass_class_devs", new Value(rol), new Value(temp)), null);
                fillListAssDevCla();
            }
            else
                javax.swing.JOptionPane.showMessageDialog(null,"Association already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);

            acc.exit();
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
        jList_Devices = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Classes = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_Rooms = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList_Permissions = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList_DevCla = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jText_Permission = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jButton9 = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jList_Devices.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList_Devices);

        jLabel1.setText("DEVICES:");

        jList_Classes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_Classes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_ClassesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList_Classes);

        jLabel2.setText("CLASSES:");

        jList_Rooms.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_Rooms);

        jLabel3.setText("ROOMS:");

        jList_Permissions.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_Permissions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList_Permissions.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_PermissionsValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList_Permissions);

        jLabel4.setText("PERMISSIONS:");

        jButton1.setText("SET ASS.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList_DevCla.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList_DevCla);

        jLabel6.setText("ASS. DEVICE-CLASS:");

        jButton2.setText("SET PERM.");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("SET PERM.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel5.setText("PERMISSION:");

        jButton4.setText("SAVE NEW PERM.");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("DELETE PERM.");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("RBAC MENU");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("MENU");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("LOGOUT");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("DEL. ASS.");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5))
                            .addComponent(jLabel5)
                            .addComponent(jText_Permission))
                        .addGap(119, 119, 119))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8)
                                .addGap(114, 114, 114))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(110, 110, 110))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addGap(18, 18, 18)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jButton9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(94, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton4, jButton5});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton6, jButton7, jButton8});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton6)
                        .addComponent(jButton7)
                        .addComponent(jButton8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton9)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jText_Permission, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4)
                            .addComponent(jButton5))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)))
                        .addGap(11, 11, 11))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        delete_perm();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(this.user);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        save_new_association();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.jText_Permission.setText(this.jList_Classes.getSelectedValue().toString());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.jText_Permission.setText(this.jList_Rooms.getSelectedValue().toString());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        save_new_perm();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList_PermissionsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_PermissionsValueChanged
        // TODO add your handling code here:

        if(!this.jList_Permissions.isSelectionEmpty())
        {
        this.ass_to_change = this.jList_Permissions.getSelectedValue().toString();
        StringTokenizer st = new StringTokenizer(ass_to_change, ":");
        st.nextToken();
        this.jText_Permission.setText(st.nextToken());
        }
    }//GEN-LAST:event_jList_PermissionsValueChanged

    private void jList_ClassesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_ClassesValueChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_jList_ClassesValueChanged

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        delete_ass();
    }//GEN-LAST:event_jButton9ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList_Classes;
    private javax.swing.JList jList_DevCla;
    private javax.swing.JList jList_Devices;
    private javax.swing.JList jList_Permissions;
    private javax.swing.JList jList_Rooms;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jText_Permission;
    // End of variables declaration//GEN-END:variables
protected void hidePanel() {
        if(isVisible()) setVisible(false);
    }

    private void save_new_perm() {
        String rol = this.jText_Permission.getText();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("permission", new Value(rol)), null);
            if(op_rdp.isResultFailure()){
                acc.out(this.rbac, new LogicTuple("permission", new Value(rol)), null);
                fillListPerm();
            } else
                javax.swing.JOptionPane.showMessageDialog(null,"Association already exsists!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
            
            acc.exit();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
