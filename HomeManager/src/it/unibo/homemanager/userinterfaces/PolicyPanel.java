/*
 * PolicyPanel.java
 *
 * Created on 31 ottobre 2006, 22.45
 */

package it.unibo.homemanager.userinterfaces;

/**
 *
 * @author  admin
 */

import alice.logictuple.*;
import alice.tucson.api.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.util.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PolicyPanel extends javax.swing.JPanel {
    
    private MainPanel mp;
    private User u;
    private TucsonTupleCentreId casa;
    //private Vector tid;
    private int idUser = 0;
    private String tempMode = "",prioFactor = "";
    private double energy = 0;
    
    private TucsonAgentId agent;
    private UserServiceInterface userService;
    private DeviceServiceInterface deviceService;
    private Database database;
    
    /** Creates new form PolicyPanel */
    public PolicyPanel(MainPanel mp,TucsonTupleCentreId t, ServiceFactory sf) {
        initComponents();
        this.mp = mp;
        casa = t;
	this.userService = sf.getUserServiceInterface();
	this.deviceService = sf.getDeviceServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("policyPanel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
//    private void fillCbUsers(int idUser) {
//        try {
//            cbUsers.removeAllItems();
//            Vector users = us.getUsers(DbService.getDatabase());
//            for(int i=0;i<users.size();i++) {
//                User usr = (User)users.elementAt(i);
//                cbUsers.addItem(usr.username);
//                if(idUser == usr.idUser)
//                    cbUsers.setSelectedIndex(i);
//            }
//        }
//        catch(Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    protected void init(User u) {
        this.u = u;
        
        btGroup.add(this.rbNone);
        btGroup.add(this.rbHeat);
        btGroup.add(this.rbAc);
        
        lbRes.setText("");

        try {
            this.fillIndsDevs();
            this.fillUnnDevs();

            // user with maximum priority
//            LogicTuple tuple = ctx.rd(tid,new LogicTuple("max_prio",new Var("X")));
//            idUser = tuple.getArg(0).intValue();
//            fillCbUsers(idUser);
            // current temperature mode
            /*if(tempMode.equals("none")) {
                this.rbNone.setSelected(true);
                this.rbAc.setSelected(false);
                this.rbHeat.setSelected(false);
            }
            else if(tempMode.equals("heat")) {
                this.rbNone.setSelected(false);
                this.rbAc.setSelected(false);
                this.rbHeat.setSelected(true);
            }
            else {
                this.rbNone.setSelected(false);
                this.rbAc.setSelected(true);
                this.rbHeat.setSelected(false);
            }*/
            findTempMode();
            switch(tempMode) {
                case "none":
                    this.rbNone.setSelected(true);
                    this.rbAc.setSelected(false);
                    this.rbHeat.setSelected(false);
                    break;
                case "heat":
                    this.rbNone.setSelected(false);
                    this.rbAc.setSelected(false);
                    this.rbHeat.setSelected(true);
                    break;
                case "ac":
                    this.rbNone.setSelected(false);
                    this.rbAc.setSelected(true);
                    this.rbHeat.setSelected(false);
                    //break;
            }
            // current priority factor
            //tuple = ctx.rd(tid,new LogicTuple("prio_factor",new Var("X")));
            //prioFactor = tuple.getArg(0).toString();
            findPrioFactor();
            System.err.println("** prioFactor = "+prioFactor+" / tempMode = "+tempMode);
            if(prioFactor.equals("users"))
                this.listPriority.setSelectedIndex(0);
            else
                this.listPriority.setSelectedIndex(1);
            // current max energy consumption
            //tuple = ctx.rd(tid,new LogicTuple("max_energy",new Var("X")));
            //energy = tuple.getArg(0).doubleValue();
            findMaxEnergy();
            this.tEnergy.setText(energy+"");
            //
            if(!isVisible())
                setVisible(true);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findTempMode() {
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rd = acc.rd(casa,new LogicTuple("temp_mode",new Var("X")), Long.MAX_VALUE);
            tempMode = op_rd.getLogicTupleResult().getArg(0).toString();
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findPrioFactor() {
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rd = acc.rd(casa,new LogicTuple("prio_factor",new Var("X")), Long.MAX_VALUE);
            prioFactor = op_rd.getLogicTupleResult().getArg(0).toString();
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findMaxEnergy() {
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rd = acc.rd(casa,new LogicTuple("max_energy",new Var("X")), Long.MAX_VALUE);
            energy = op_rd.getLogicTupleResult().getArg(0).doubleValue();
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void fillIndsDevs(){
        try{
            String deviceName = "";
            //System.out.println(casa.getName());
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(casa, new LogicTuple("indispensable_device", new Var("X")), Long.MAX_VALUE);
            //LogicTuple lt = ctx.inp(tid, new LogicTuple("rd_all", new Value("indispensable_device", new Var("X")), new Var("Y")));
            //List l = lt.getArg(1).toList();
            List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
            
            this.jList_IndDevices.removeAll();
            ArrayList<Device> devices = deviceService.getDevices(database.getDatabase());
            Vector indispensableDevices = new Vector();
            int j=0;
            boolean found;
            for(int i=0;i<devices.size();i++) {
                Device device = /*(Device)*/devices.get(i);//.elementAt(i);
                deviceName = device.getDeviceName();
                j=0;
                found=false;
                
                while(j<l.size() && !found){
                    //LogicTuple dev = LogicTuple.parse(l.get(j).toString());
                    if(l.get(j).getArg(0).intValue()==device.getDeviceId())
                        found = true;
                    j++;
                }
                
                if(!found)
                    //if(!found)
                    indispensableDevices.add(deviceName);
                deviceName="";
            }
            this.jList_IndDevices.setListData(indispensableDevices);
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
        
    private void fillUnnDevs(){
        try{
            String deviceName = "";
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(casa, new LogicTuple("unnecessary_device", new Var("X")), Long.MAX_VALUE);
            //LogicTuple lt = ctx.inp(tid, new LogicTuple("rd_all", new Value("unnecessary_device", new Var("X")), new Var("Y")));
            //List l = lt.getArg(1).toList();
            List<LogicTuple> unnecessaryList = op_rdAll.getLogicTupleListResult();
            System.err.println("unnecessary_device: " + unnecessaryList);
            //lt = ctx.inp(tid, new LogicTuple("rd_all", new Value("indispensable_device", new Var("X")), new Var("Y")));
            op_rdAll = acc.rdAll(casa, new LogicTuple("indispensable_device", new Var("X")), Long.MAX_VALUE);
            List<LogicTuple> indispensableList = op_rdAll.getLogicTupleListResult();
            System.err.println("indispensable_device: " + indispensableList);
            this.jList_UnnDevices.removeAll();
            ArrayList<Device> devices = deviceService.getDevices(database.getDatabase());
            Vector unnecessaryDevicesVector = new Vector();
            List<Integer> unnecessaryIds = new ArrayList<Integer>();
            
            for(LogicTuple device:unnecessaryList)
                unnecessaryIds.add(device.getArg(0).intValue());
            for(LogicTuple device:indispensableList)
                unnecessaryIds.add(device.getArg(0).intValue());
            
            System.err.println("unnecessaryIds: " + unnecessaryIds);
            
            for(int i=0; i<devices.size(); i++) {
                Device dev = (Device)devices.get(i);
                if(!unnecessaryIds.contains(dev.getDeviceId())) {
                    System.err.println("ID " + dev.getDeviceId() + " not found, adding to the list..");
                    unnecessaryDevicesVector.add(dev.getDeviceName());
                }
            }   
            
            /*for(int i=0; i<unnecessaryList.size();i++) {
                Device uDevice = DeviceService.getDeviceById(DbService.getDatabase(), unnecessaryList.get(i).getArg(0).intValue());
                //System.err.println("unnecessary: " + uDevice);
                unnecessaryDevicesVector.add(uDevice.name);
            }*/
            /*int j=0;
            boolean res;
            for(int i=0;i<devices.size();i++) {
                
                Device device = (Device)devices.elementAt(i);
                deviceName = device.name;
                
                j=0;
                res=false;
                //Restituisce tutti(?) gli elementi che non sono né indispensabili né non necessari
                while(j<unnecessaryList.size() && j<indispensableList.size() && ! res){
                    LogicTuple dev = LogicTuple.parse(unnecessaryList.get(j).toString());
                    LogicTuple dev1 = LogicTuple.parse(indispensableList.get(j).toString());
                    System.err.println("dev: " + dev + ", dev1: " + dev1);
                    if(dev.getArg(0).intValue()==device.idDev || dev1.getArg(0).intValue()==device.idDev) {
                        System.out.println("Found!!!" + "ID: " + device.idDev + " - " + dev + " || " + dev1);
                        res = true;
                    }
                    j++;
                }
                if(!res)
                    unnecessaryDevicesVector.add(deviceName);
                //ut="";
            }*/
            this.jList_UnnDevices.setListData(unnecessaryDevicesVector);
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btGroup = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        tempModePanel = new javax.swing.JPanel();
        rbNone = new javax.swing.JRadioButton();
        rbHeat = new javax.swing.JRadioButton();
        rbAc = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        btLogOut = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listPriority = new javax.swing.JList();
        btSavePolicy = new javax.swing.JButton();
        lbRes = new javax.swing.JLabel();
        maxEnergyPanel = new javax.swing.JPanel();
        tEnergy = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btMenu = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_IndDevices = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList_UnnDevices = new javax.swing.JList();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        setBackground(new java.awt.Color(204, 204, 255));
        setMaximumSize(new java.awt.Dimension(538, 343));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));

        tempModePanel.setBackground(new java.awt.Color(153, 153, 255));
        tempModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 51, 153)), "Temperature Mode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N

        rbNone.setBackground(new java.awt.Color(153, 153, 255));
        rbNone.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        rbNone.setForeground(new java.awt.Color(0, 0, 102));
        rbNone.setText("None");
        rbNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbNone.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rbHeat.setBackground(new java.awt.Color(153, 153, 255));
        rbHeat.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        rbHeat.setForeground(new java.awt.Color(0, 0, 102));
        rbHeat.setText("Heater");
        rbHeat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbHeat.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rbAc.setBackground(new java.awt.Color(153, 153, 255));
        rbAc.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        rbAc.setForeground(new java.awt.Color(0, 0, 102));
        rbAc.setText("Air Conditioner");
        rbAc.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAc.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout tempModePanelLayout = new org.jdesktop.layout.GroupLayout(tempModePanel);
        tempModePanel.setLayout(tempModePanelLayout);
        tempModePanelLayout.setHorizontalGroup(
            tempModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tempModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tempModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rbNone)
                    .add(rbHeat)
                    .add(rbAc))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        tempModePanelLayout.setVerticalGroup(
            tempModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tempModePanelLayout.createSequentialGroup()
                .add(rbNone)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbHeat)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAc)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Please choose policy for plan creation!");

        btLogOut.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btLogOut.setForeground(new java.awt.Color(102, 0, 102));
        btLogOut.setText("LOG OUT");
        btLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogOutActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 51, 153)), "Assign priority to a factor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N

        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        listPriority.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        listPriority.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Priority to preferences and commands", "Priority to energy consumption" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listPriority);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 369, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btSavePolicy.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btSavePolicy.setForeground(new java.awt.Color(102, 0, 102));
        btSavePolicy.setText("SAVE POLICY");
        btSavePolicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSavePolicyActionPerformed(evt);
            }
        });

        lbRes.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        lbRes.setForeground(new java.awt.Color(0, 0, 51));
        lbRes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        maxEnergyPanel.setBackground(new java.awt.Color(153, 153, 255));
        maxEnergyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 51, 153)), "Max Energy Consumption", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N

        tEnergy.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
        jLabel2.setText("kW");

        org.jdesktop.layout.GroupLayout maxEnergyPanelLayout = new org.jdesktop.layout.GroupLayout(maxEnergyPanel);
        maxEnergyPanel.setLayout(maxEnergyPanelLayout);
        maxEnergyPanelLayout.setHorizontalGroup(
            maxEnergyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(maxEnergyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        maxEnergyPanelLayout.setVerticalGroup(
            maxEnergyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(maxEnergyPanelLayout.createSequentialGroup()
                .add(33, 33, 33)
                .add(maxEnergyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btMenu.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btMenu.setForeground(new java.awt.Color(102, 0, 102));
        btMenu.setText("MENU");
        btMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenuActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(153, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Indispensable Dev.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(0, 51, 51))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(51, 51, 255));

        jList_IndDevices.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList_IndDevices.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_IndDevicesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList_IndDevices);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(153, 153, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Unnecessary Devs."));

        jList_UnnDevices.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList_UnnDevices);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(104, 104, 104)
                                .add(lbRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(tempModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(maxEnergyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(26, 26, 26)
                                .add(btMenu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(btLogOut))))
                    .add(layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(btSavePolicy)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(btMenu)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btLogOut))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tempModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(maxEnergyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(53, 53, 53)
                        .add(btSavePolicy)))
                .add(18, 18, 18)
                .add(lbRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btSavePolicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSavePolicyActionPerformed
        if(!checkFields()) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Please choose temperature mode, user with maximum priority, "+
                    "priority factor and maximum energy consumption.",
                    "MISSING SELECTION",javax.swing.JOptionPane.ERROR_MESSAGE);
            //return;
        }
        else {
            double en = 0;
            try {
                en = Double.parseDouble(tEnergy.getText());
            }
            catch(Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Please insert a NUMBER for maximum energy consumption.",
                    "WRONG INSERTION",javax.swing.JOptionPane.ERROR_MESSAGE);
                tEnergy.setText(""+energy);
                return;
            }
            if(Utilities.getPrioFactor(listPriority.getSelectedIndex()).compareTo("users") != 0)
                checkNewEnergy(en);
            System.out.println("Sending policy tuples..");
            try {
                String selectedTempMode = tempMode;
                EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
                // max prio
//                ctx.out(tid,new LogicTuple("upd_max_prio",
//                        new Value(getIdUser(cbUsers.getSelectedItem().toString()))));
                // prio factor
                acc.out(casa,new LogicTuple("upd_prio_factor",new Value(Utilities.getPrioFactor(listPriority.getSelectedIndex()))), null);
                // max energy
                acc.out(casa,new LogicTuple("upd_max_energy",new Value(en)), null);
                // temp mode
                if(rbNone.isSelected())
                    selectedTempMode = "none";
                if(rbHeat.isSelected())
                    selectedTempMode = "heat";
                if(rbAc.isSelected())
                    selectedTempMode = "ac";
                
                acc.out(casa,new LogicTuple("upd_temp_mode",new Value(selectedTempMode)), null);
               
                //unnecessary devices
                if(!this.jList_UnnDevices.isSelectionEmpty()){
                    for(int i=0;i<this.jList_UnnDevices.getSelectedValues().length;i++){
                        acc.out(casa, new LogicTuple("unnecessary_device", new Value(this.jList_UnnDevices.getSelectedValues()[i].toString())), null);
                    }
                }
                 if(!this.jList_IndDevices.isSelectionEmpty()){
                    for(int i=0;i<this.jList_IndDevices.getSelectedValues().length;i++){
                        acc.out(casa, new LogicTuple("indispensable_device", new Value(this.jList_IndDevices.getSelectedValues()[i].toString())), null);
                    }
                }
                lbRes.setText("Policy correctly saved!");
                acc.exit();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btSavePolicyActionPerformed

    private void checkNewEnergy(double en) {
        try {
//            LogicTuple lt = ctx.inp(tid,new LogicTuple("rd_all",
//                    new Value("energy",new Var("X"),new Var("Y")),
//                    new Var("L")));
//            System.err.println("-- checkNewEnergy: "+lt);
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdp = acc.rdp(casa,new LogicTuple("total_energy_cons", new Var("X")), Long.MAX_VALUE);
            acc.exit();
            double te = op_rdp.getLogicTupleResult().getArg(0).doubleValue();
            if(te > en)
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Please turn off some devices in order to respect the new consumption limit " +
                        "(current total energy: "+te+" kW vs. "+en+" kW limit).",
                    "LIMIT ALERT",javax.swing.JOptionPane.WARNING_MESSAGE);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private int getIdUser(String un) {
        int id = 0;
        try {
            User usr = userService.getUser(database.getDatabase(),un);
            id = usr.idUser;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }
    
    private boolean checkFields() {
        boolean res = false;
        if(//cbUsers.getSelectedItem() == null ||
                listPriority.getSelectedIndex() == -1 ||
                Utilities.isEmpty(tEnergy.getText())) {
            System.err.println("Missing selections");
        } else
            res = true;
        return res;
    }
    
    private void btLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOutActionPerformed
        mp.logOut(evt);
    }//GEN-LAST:event_btLogOutActionPerformed

    private void btMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenuActionPerformed
        mp.loadInitRolePanel(u);
    }//GEN-LAST:event_btMenuActionPerformed

    private void jList_IndDevicesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_IndDevicesValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jList_IndDevicesValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btGroup;
    private javax.swing.JButton btLogOut;
    private javax.swing.JButton btMenu;
    private javax.swing.JButton btSavePolicy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList_IndDevices;
    private javax.swing.JList jList_UnnDevices;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lbRes;
    private javax.swing.JList listPriority;
    private javax.swing.JPanel maxEnergyPanel;
    private javax.swing.JRadioButton rbAc;
    private javax.swing.JRadioButton rbHeat;
    private javax.swing.JRadioButton rbNone;
    private javax.swing.JTextField tEnergy;
    private javax.swing.JPanel tempModePanel;
    // End of variables declaration//GEN-END:variables
    
}
