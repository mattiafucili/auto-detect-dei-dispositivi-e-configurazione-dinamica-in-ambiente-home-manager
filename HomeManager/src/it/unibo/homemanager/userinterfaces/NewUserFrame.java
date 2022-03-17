/*
 * NewUserFrame.java
 *
 * Created on 12 novembre 2006, 18.00
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.ServiceFactory;
import javax.swing.*;
import java.util.Vector;

import it.unibo.homemanager.tablemap.*;
import it.unibo.homemanager.dbmanagement.*;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.util.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  admin
 */
public class NewUserFrame extends javax.swing.JFrame {
    
    private User usr;
    
    //private JScrollPane pane = null;
    private JPanel actPanel = null;
    private ManageUsersPanel mup;
    
    private Database database;
    private RoomServiceInterface roomService;
    private DeviceServiceInterface deviceService;
    private UserServiceInterface userService;
    
    private Vector checkBoxes = null;//,devices = null;
    private ArrayList<Device> devices = null;
    
    /** Creates new form NewUserFrame */
    public NewUserFrame(String title,ManageUsersPanel mup, ServiceFactory sf) {
        super(title);
        this.mup = mup;
        this.roomService = sf.getRoomServiceInterface();
	this.deviceService = sf.getDeviceServiceInterface();
	this.userService = sf.getUserServiceInterface();
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(NewUserFrame.class.getName()).log(Level.SEVERE, null, ex);
	}
	
        initComponents();
        this.rolePanel.setVisible(false);
        fillTempCheckBoxes();
        addActPanel();
        fillActPanel();
    }
    
    private void fillTempCheckBoxes() {
        for(int i=15;i<31;i++) {
            cbTempHeat.addItem(i+"");
            cbTempAc.addItem(i+"");
        }
    }
    
    private void addActPanel() {
        //pane = new JScrollPane();
        actPanel = new JPanel();
        //pane.setBackground(new java.awt.Color(102, 0, 102));
        //pane.setBorder(BorderFactory.createEtchedBorder());
        actPanel.setBackground(new java.awt.Color(102, 0, 102));
        actPanel.setLayout(new BoxLayout(actPanel,BoxLayout.Y_AXIS));
        pane1.setViewportView(actPanel);
        pane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane1.setBounds(190,180,342,150);
        this.genPanel.add(pane1);
    }
    
    private void fillActPanel() {
        actPanel.removeAll();
        
        checkBoxes = new Vector();
        devices = new ArrayList<>();
        try {
            Database db = database.getDatabase();
            Vector rooms = roomService.getRooms(db);
            for(int i=0;i<rooms.size();i++) {
                Room r = (Room)rooms.elementAt(i);
                ArrayList<Device> devs = deviceService.getDevicesInRoom(db,r.idRoom);
                if(devs.size() > 0) {
                    actPanel.add(getRoomLabel(r));
                    for(int j=0;j<devs.size();j++) {
                        Device dev = devs.get(j);//(Device)devs.elementAt(j);
                        JCheckBox cb = getCbDev(dev);
                        checkBoxes.add(cb);
                        devices.add(dev);
                        actPanel.add(cb);
                    }
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private JLabel getRoomLabel(Room r) {
        JLabel lb = new JLabel(" "+r.name);
        lb.setFont(new java.awt.Font("Courier New",java.awt.Font.BOLD,12));
        lb.setForeground(java.awt.Color.WHITE);
        lb.setSize(getWidth(),18);
        return lb;
    }
    
    private JCheckBox getCbDev(Device d) {
        JCheckBox cb = new JCheckBox(" "+d.getDeviceName());
        cb.setBackground(new java.awt.Color(102, 0, 102));
        cb.setFont(new java.awt.Font("Courier New",java.awt.Font.PLAIN,12));
        cb.setForeground(java.awt.Color.WHITE);
        cb.setSize(this.getWidth(),13);
        return cb;
    }
    
    private boolean fieldsInserted() {
        boolean res = true;
        if(Utilities.isEmpty(tName.getText()) || Utilities.isEmpty(tSName.getText()) ||
                Utilities.isEmpty(tUn.getText()) || !rightPwds())
            res = false;
        return res;
    }
    
    private boolean rightPwds() {
        String pwd1 = String.copyValueOf(fPwd.getPassword());
        String pwd2 = String.copyValueOf(fPwdConf.getPassword());
        if(Utilities.isEmpty(pwd1) || Utilities.isEmpty(pwd2))
            return false;
        if(pwd1.compareTo(pwd2) != 0) return false;
        return true;
    }
    
    private String getActivateString() {
        String str = "";
        for(int i=0;i<checkBoxes.size();i++) {
            JCheckBox cb = (JCheckBox)checkBoxes.elementAt(i);
            if(cb.isSelected()) {
                Device dev = devices.get(i);//Device)devices.elementAt(i);
                str += dev.getDeviceRoomId()+"-"+dev.getDeviceId()+";";
            }
        }
        System.err.println("str: " + str);
        // take away comma
        str = str.substring(0,str.length()-1);
        System.err.println("new str: " + str);
        return str;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genPanel = new javax.swing.JPanel();
        namesPanel = new javax.swing.JPanel();
        tName = new javax.swing.JTextField();
        tSName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        authPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        tUn = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        fPwd = new javax.swing.JPasswordField();
        fPwdConf = new javax.swing.JPasswordField();
        jLabel14 = new javax.swing.JLabel();
        tempPanel = new javax.swing.JPanel();
        cbTempHeat = new javax.swing.JComboBox();
        cbTempAc = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        rolePanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        cbRole = new javax.swing.JComboBox();
        btSave1 = new javax.swing.JButton();
        pane1 = new javax.swing.JScrollPane();
       
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 255));
        setResizable(false);
        
        pane2 = new JScrollPane(actPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        genPanel.setBackground(new java.awt.Color(204, 204, 255));
        genPanel.setMaximumSize(new java.awt.Dimension(538, 343));
        genPanel.setMinimumSize(new java.awt.Dimension(538, 343));
        genPanel.setPreferredSize(new java.awt.Dimension(538, 360));

        namesPanel.setBackground(new java.awt.Color(102, 0, 153));
        namesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tName.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        tSName.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(226, 226, 251));
        jLabel1.setText("Firstname");

        jLabel2.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(226, 226, 251));
        jLabel2.setText("Surname");

        org.jdesktop.layout.GroupLayout namesPanelLayout = new org.jdesktop.layout.GroupLayout(namesPanel);
        namesPanel.setLayout(namesPanelLayout);
        namesPanelLayout.setHorizontalGroup(
            namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namesPanelLayout.createSequentialGroup()
                .add(11, 11, 11)
                .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(tSName)
                    .add(tName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        namesPanelLayout.setVerticalGroup(
            namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tSName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        authPanel.setBackground(new java.awt.Color(51, 0, 102));
        authPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(226, 226, 251));
        jLabel12.setText("Username");

        tUn.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(226, 226, 251));
        jLabel13.setText("Password");

        fPwd.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N

        fPwdConf.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(226, 226, 251));
        jLabel14.setText("Retype Password");

        org.jdesktop.layout.GroupLayout authPanelLayout = new org.jdesktop.layout.GroupLayout(authPanel);
        authPanel.setLayout(authPanelLayout);
        authPanelLayout.setHorizontalGroup(
            authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(fPwdConf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .add(jLabel13)
                    .add(jLabel12)
                    .add(tUn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .add(fPwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                .addContainerGap())
        );
        authPanelLayout.setVerticalGroup(
            authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tUn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fPwdConf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        tempPanel.setBackground(new java.awt.Color(153, 153, 255));
        tempPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cbTempHeat.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        cbTempAc.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 51));
        jLabel16.setText("Heater temperature");

        jLabel17.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 51));
        jLabel17.setText("Air Conditioner temp.");

        org.jdesktop.layout.GroupLayout tempPanelLayout = new org.jdesktop.layout.GroupLayout(tempPanel);
        tempPanel.setLayout(tempPanelLayout);
        tempPanelLayout.setHorizontalGroup(
            tempPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tempPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tempPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel16)
                    .add(cbTempHeat, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cbTempAc, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tempPanelLayout.setVerticalGroup(
            tempPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tempPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel16)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTempHeat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel17)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTempAc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel18.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 51));
        jLabel18.setText("Insert New Users");

        rolePanel.setBackground(new java.awt.Color(234, 234, 251));
        rolePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel19.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 51));
        jLabel19.setText("Role:");

        cbRole.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        cbRole.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ordinary User", "Administrator" }));

        org.jdesktop.layout.GroupLayout rolePanelLayout = new org.jdesktop.layout.GroupLayout(rolePanel);
        rolePanel.setLayout(rolePanelLayout);
        rolePanelLayout.setHorizontalGroup(
            rolePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rolePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(rolePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbRole, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        rolePanelLayout.setVerticalGroup(
            rolePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, rolePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel19)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbRole, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btSave1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btSave1.setForeground(new java.awt.Color(51, 0, 153));
        btSave1.setText("INSERT");
        btSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSave1ActionPerformed(evt);
            }
        });

        pane1.setBackground(new java.awt.Color(102, 0, 102));
        pane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout genPanelLayout = new org.jdesktop.layout.GroupLayout(genPanel);
        genPanel.setLayout(genPanelLayout);
        genPanelLayout.setHorizontalGroup(
            genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel18)
                    .add(genPanelLayout.createSequentialGroup()
                        .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(authPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(namesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pane1)
                            .add(genPanelLayout.createSequentialGroup()
                                .add(tempPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(rolePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btSave1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        genPanelLayout.setVerticalGroup(
            genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel18)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(genPanelLayout.createSequentialGroup()
                            .add(rolePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(btSave1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(tempPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(namesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 540, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSave1ActionPerformed
        try {
            if(!fieldsInserted()) {
                JOptionPane.showMessageDialog(null,"Please compile all the fields "+
                        "and check that the two passwords you inserted are the same!",
                        "MISSING DATA",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // User creation.
            usr = userService.insertUser(database.getDatabase(),
                    tName.getText(),tSName.getText(),tUn.getText(),
                    String.copyValueOf(fPwd.getPassword()),
                    Utilities.getCharFromStringRole(cbRole.getSelectedItem().toString())+"",
                    Integer.parseInt(cbTempHeat.getSelectedItem().toString()),
                    Integer.parseInt(cbTempAc.getSelectedItem().toString()),
                    getActivateString());
            mup.updatePanel();
            this.dispose();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btSave1ActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel authPanel;
    private javax.swing.JButton btSave1;
    private javax.swing.JComboBox cbRole;
    private javax.swing.JComboBox cbTempAc;
    private javax.swing.JComboBox cbTempHeat;
    private javax.swing.JPasswordField fPwd;
    private javax.swing.JPasswordField fPwdConf;
    private javax.swing.JPanel genPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel namesPanel;
    private javax.swing.JScrollPane pane1;
    private javax.swing.JScrollPane pane2;
    private javax.swing.JPanel rolePanel;
    private javax.swing.JTextField tName;
    private javax.swing.JTextField tSName;
    private javax.swing.JTextField tUn;
    private javax.swing.JPanel tempPanel;
    // End of variables declaration//GEN-END:variables
    
}
