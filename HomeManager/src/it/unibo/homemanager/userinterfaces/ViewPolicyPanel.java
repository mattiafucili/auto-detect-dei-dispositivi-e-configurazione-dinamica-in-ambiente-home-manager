/*
 * ViewPolicyPanel.java
 *
 * Created on 5 novembre 2006, 10.53
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.tablemap.User;

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author  admin
 */
public class ViewPolicyPanel extends javax.swing.JPanel {
    
    private MainPanel mp;
    private User u;
    private TucsonTupleCentreId tid;
    
    private Database database;
    private DeviceServiceInterface deviceService;
    private UserServiceInterface userService;
    private TucsonAgentId agent;
    private EnhancedSynchACC acc;
    
    /** Creates new form ViewPolicyPanel */
    public ViewPolicyPanel(MainPanel mp,TucsonTupleCentreId t, ServiceFactory sf) {
        this.mp = mp;
        tid = t;
	this.deviceService = sf.getDeviceServiceInterface();
	this.userService = sf.getUserServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("viewPolicyAgent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    protected void init(User usr) {
        u = usr;
        try {
            acc = TucsonMetaACC.getContext(agent);
            
            ITucsonOperation op_rd = acc.rd(tid,new LogicTuple("temp_mode",new Var("X")), Long.MAX_VALUE);
            tTM.setText(getTempModeString(op_rd.getLogicTupleResult().getArg(0).toString()));
            op_rd = acc.rd(tid,new LogicTuple("prio_factor",new Var("X")), Long.MAX_VALUE);
            tPF.setText(getPrioFactorString(op_rd.getLogicTupleResult().getArg(0).toString()));
          //  tuple = ctx.rd(tid,new LogicTuple("max_prio",new Var("X")));
          //  tPU.setText(getMaxPrioUsrString(tuple.getArg(0).intValue()));
            //situazione temporanea
         //   tPU.setText(getMaxPrioUsrString(1));
            op_rd = acc.rd(tid,new LogicTuple("max_energy",new Var("X")), Long.MAX_VALUE);
            tE.setText("\n\t"+op_rd.getLogicTupleResult().getArg(0).toString()+" kW");
            //

            this.fillindDevs();
            this.fillunDevs();
            acc.exit();
            
            if(!isVisible())
                setVisible(true);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getMaxPrioUsrString(int mp) {
        User u = null;
        String username;
        try {
            //UserService us = new UserService();
            u = userService.getUserById(database.getDatabase(),mp);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        if(u!=null)
            username = u.username;
        else
            username = "";
        
        return "\n\t"+username;
    }
    
    private String getPrioFactorString(String pf) {
        String priority;
        
        if(pf.equals("users"))
            priority = "\n\tPriority to users and preferences.";
        else
            priority = "\n\tPriority to energy consumption.";
        
        return priority;
    }
    
    private String getTempModeString(String tm) {
        String tempMode = "";
        
        if(tm.equals("none"))
            tempMode = "\n\tNone";
        if(tm.equals("heat"))
            tempMode = "\n\tHeaters";
        if(tm.equals("ac"))
            tempMode = "\n\tAir Conditioners";
        
        return tempMode;
    }


    private void fillunDevs(){
        try{
            ITucsonOperation op_rdAll = acc.rdAll(tid, new LogicTuple("unnecessary_device", new Var("X")), null);
            List<LogicTuple> uDevs = op_rdAll.getLogicTupleListResult();

            Vector unnecessaryDevicesVector = new Vector();
                //LogicTuple rol;
            for(int i=0;i<uDevs.size();i++)
            {
                    //rol = LogicTuple.parse(devs.get(i).toString());
                Device dev = deviceService.getDeviceById(database.getDatabase(), uDevs.get(i).getArg(0).intValue());
                unnecessaryDevicesVector.add(dev.getDeviceName());
            }
            this.jList_UnnDevs.setListData(unnecessaryDevicesVector);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    private void fillindDevs(){
        try{
            ITucsonOperation op_rdAll = acc.rdAll(tid, new LogicTuple("indispensable_device", new Var("X")), null);
            List<LogicTuple> indDevs = op_rdAll.getLogicTupleListResult();
            
            Vector indispensableDevicesVector = new Vector();
                //LogicTuple rol;
            for(int i=0;i<indDevs.size();i++)
            {
                    //rol = LogicTuple.parse(indDevs.get(i).toString());
                Device dev = deviceService.getDeviceById(database.getDatabase(), indDevs.get(i).getArg(0).intValue());
                indispensableDevicesVector.add(dev.getDeviceName());
            }
            this.jList_IndDevs.setListData(indispensableDevicesVector);
        }catch(Exception ex){
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tTM = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        tPF = new javax.swing.JTextArea();
        btMenu = new javax.swing.JButton();
        btLogOut = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tE = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_UnnDevs = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList_IndDevs = new javax.swing.JList();

        setBackground(new java.awt.Color(204, 204, 255));
        setMaximumSize(new java.awt.Dimension(538, 343));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jScrollPane1.setBackground(new java.awt.Color(153, 153, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Temperature Mode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N
        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 18));

        tTM.setBackground(new java.awt.Color(153, 153, 255));
        tTM.setColumns(20);
        tTM.setFont(new java.awt.Font("Courier", 0, 18));
        tTM.setLineWrap(true);
        tTM.setRows(3);
        tTM.setWrapStyleWord(true);
        jScrollPane1.setViewportView(tTM);

        jScrollPane3.setBackground(new java.awt.Color(153, 153, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Priority Factor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N
        jScrollPane3.setFont(new java.awt.Font("Courier New", 0, 18));

        tPF.setBackground(new java.awt.Color(153, 153, 255));
        tPF.setColumns(20);
        tPF.setFont(new java.awt.Font("Courier", 0, 18));
        tPF.setLineWrap(true);
        tPF.setRows(2);
        tPF.setWrapStyleWord(true);
        jScrollPane3.setViewportView(tPF);

        btMenu.setFont(new java.awt.Font("Courier New", 1, 14));
        btMenu.setForeground(new java.awt.Color(102, 0, 102));
        btMenu.setText("MENU");
        btMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenuActionPerformed(evt);
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

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("See current policy:");

        jScrollPane4.setBackground(new java.awt.Color(153, 153, 255));
        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Max Energy Consumption", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N
        jScrollPane4.setFont(new java.awt.Font("Courier New", 0, 18));

        tE.setBackground(new java.awt.Color(153, 153, 255));
        tE.setColumns(20);
        tE.setFont(new java.awt.Font("Courier", 0, 18));
        tE.setLineWrap(true);
        tE.setRows(2);
        tE.setWrapStyleWord(true);
        jScrollPane4.setViewportView(tE);

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Unnecessary Devs.\n"));

        jList_UnnDevs.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList_UnnDevs);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(153, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Indispensable Devs."));

        jList_IndDevs.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList_IndDevs);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 170, Short.MAX_VALUE)
                        .add(btMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(14, 14, 14)
                        .add(btLogOut))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 518, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btLogOut)
                    .add(btMenu)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOutActionPerformed
        mp.logOut(evt);
    }//GEN-LAST:event_btLogOutActionPerformed

    private void btMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenuActionPerformed
        mp.loadInitRolePanel(u);
    }//GEN-LAST:event_btMenuActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLogOut;
    private javax.swing.JButton btMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList_IndDevs;
    private javax.swing.JList jList_UnnDevs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea tE;
    private javax.swing.JTextArea tPF;
    private javax.swing.JTextArea tTM;
    // End of variables declaration//GEN-END:variables
    
}
