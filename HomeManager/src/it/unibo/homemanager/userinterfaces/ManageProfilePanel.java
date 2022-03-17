/*
 * ManageProfilePanel.java
 *
 * Created on 6 novembre 2006, 9.28
 */

package it.unibo.homemanager.userinterfaces;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.util.ActPreference;
import it.unibo.homemanager.util.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author  admin
 */
public class ManageProfilePanel extends javax.swing.JPanel {
    
    private MainPanel mp;
    private User u;
    private String[] strings;
    
    private JPanel actPanel = null;
    
    private Vector checkBoxes = null;//,devices = null;
    private ArrayList<Device> devices = null;

    private TucsonTupleCentreId rbac;
    private Database database;
    private RoomServiceInterface roomService;
    private DeviceServiceInterface deviceService;
    private UserServiceInterface userService;
    
    /** Creates new form ManageProfilePanel */
    public ManageProfilePanel(MainPanel m, TucsonTupleCentreId rbac_tc, ServiceFactory sf) {
        this.rbac = rbac_tc;
        mp = m;
	this.roomService = sf.getRoomServiceInterface();
	this.deviceService = sf.getDeviceServiceInterface();
	this.userService = sf.getUserServiceInterface();
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(ManageProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
	}
        initComponents();
    }
    
    private void addActPanel() {
        actPanel = new JPanel();
        actPanel.removeAll();
        actPanel.repaint();
        actPanel.setBackground(new java.awt.Color(102, 0, 102));
        actPanel.setLayout(new BoxLayout(actPanel,BoxLayout.Y_AXIS));
        pane.setViewportView(actPanel);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(pane);
    }
    
    protected void init(User usr) {
        try {
            u = usr;
            // load data
            tName.setText(u.firstname);
            tSName.setText(u.surname);
            lbRole.setText("Role: " + u.getRoleChar());
            tUn.setText(u.username);
            fPwd.setText(u.pwd);
            setTemperatures(u.temp_ac, u.temp_heat);
            addActPanel();
            fillActPanel();
            //
            if (!isVisible()) {
                setVisible(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    private void fillActPanel() {
        this.actPanel.revalidate();
        strings = Utilities.getSinglesActPreferences(u.activate);
        checkBoxes = new Vector();
        devices = new ArrayList<>();
        try {
            TucsonAgentId agent = new TucsonAgentId("manageProfilePanel");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            Database db = database.getDatabase();
            Vector rooms = roomService.getRooms(db);
            //LogicTuple lt = ctx.rd(rbac, new LogicTuple("ass_user_roles", new Value(this.u.idUser), new Var("X")));
            ITucsonOperation op_rd = acc.rd(rbac, new LogicTuple("ass_user_roles", new Value(this.u.idUser), new Var("X")), Long.MAX_VALUE);
            System.out.println(op_rd.getLogicTupleResult());
            LogicTuple lt = op_rd.getLogicTupleResult();
            ITucsonOperation op_rdAll = acc.rdAll(rbac, new LogicTuple("ass_roles_perm", lt.getArg(1), new Var("X")), Long.MAX_VALUE);
            List<LogicTuple> list = op_rdAll.getLogicTupleListResult();
            System.out.println(list);
            
            for(int i=0;i<rooms.size();i++) {
                Room r = (Room)rooms.elementAt(i);
                ArrayList<Device> devs = deviceService.getDevicesInRoom(db,r.idRoom);
                //QUI NON E' STATO INSERITO UN CICLO PER VERIFICARE TUTTI I RUOLI RIVESTITI DALL'UTENTE PERCHE' NELLA
                //CASA DI HOME MANAGER UN UTENTE PUO' RIVESTIRE UN UNICO RUOLO
               
                if(devs.size() > 0) {
                    actPanel.add(getRoomLabel(r));
                    for(int j=0;j<devs.size();j++) {
                        Device dev = devs.get(i);//(Device)devs.elementAt(j);
                        //lt = ctx.rdp(rbac, new LogicTuple("ass_class_devs", new Var("X"), new Value(dev.name)));
                        ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("ass_class_devs", new Var("X"), new Value(dev.getDeviceName())), null);
                        lt = op_rdp.getLogicTupleResult();
                        System.out.println(lt);
                        if(lt!=null && isDeviceUsePermitted(list, lt.getArg(0))){
                            JCheckBox cb = getCbDev(dev);
                            checkBoxes.add(cb);
                            devices.add(dev);
                            actPanel.add(cb);
                        }
                    }
                }
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private void fillRoles(){
        try{
            TucsonAgentId agent = new TucsonAgentId("manageProfilePanel");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple lt = ctx.inp(rbac, new LogicTuple("rd_all", new Value("ass_user_roles", new Value(this.u.idUser), new Var("Y")), new Var("X")));
            List l = lt.getArg(1).toList();
            acc.exit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }*/

    private JLabel getRoomLabel(Room r) {
        JLabel lb = new JLabel(" "+r.name);
        lb.setFont(new java.awt.Font("Courier New",java.awt.Font.BOLD,12));
        lb.setForeground(java.awt.Color.WHITE);
        lb.setSize(this.getWidth(),18);
        return lb;
    }
    
    private JCheckBox getCbDev(Device d) {
        JCheckBox cb  = null;
        cb = new JCheckBox(" "+d.getDeviceName());
        cb.setBackground(new java.awt.Color(102, 0, 102));
        cb.setFont(new java.awt.Font("Courier New",java.awt.Font.PLAIN,12));
        cb.setForeground(java.awt.Color.WHITE);
        cb.setSize(this.getWidth(),13);
        if(isPreferred(d)) cb.setSelected(true);
        return cb;
    }
    
    private boolean isPreferred(Device d) {
        boolean res = false;
        int i = 0;
        
        while(i<strings.length && !res) {
            ActPreference ap = new ActPreference(strings[i]);
            if(ap.getDev() == d.getDeviceId()) res = true;
            i++;
        }
        
        return res;
    }
    
    private void setTemperatures(int ac,int heat) {
        cbTempHeat.removeAllItems();
        cbTempAc.removeAllItems();
        for(int i=15;i<31;i++) {
            cbTempHeat.addItem(i+"");
            cbTempAc.addItem(i+"");
        }
        cbTempHeat.setSelectedItem(""+heat);
        cbTempAc.setSelectedItem(""+ac);
    }


    private boolean isDeviceUsePermitted(List l, TupleArgument arg){
       List devs_perm = l;
       int i = 0;
       boolean res = false;
       LogicTuple dev = null;
       while(i<devs_perm.size() && !res){
            try {
                dev = LogicTuple.parse(devs_perm.get(i).toString());
            } catch (Exception ex) {
                Logger.getLogger(ManageProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (dev.getArg(1).getName().equals(arg.getName())) {
                    res = true;
                }
            } catch (Exception ex) {
                Logger.getLogger(ManageProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
          i++;
       }
        return res;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        tempPanel = new javax.swing.JPanel();
        cbTempHeat = new javax.swing.JComboBox();
        cbTempAc = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lbRole = new javax.swing.JLabel();
        btMenu = new javax.swing.JButton();
        btLogOut = new javax.swing.JButton();
        btSave = new javax.swing.JButton();
        pane = new javax.swing.JScrollPane();

        setBackground(new java.awt.Color(204, 204, 255));
        setMaximumSize(new java.awt.Dimension(538, 343));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));

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
                .addContainerGap()
                .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel1)
                    .add(tName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .add(jLabel2)
                    .add(tSName))
                .addContainerGap(18, Short.MAX_VALUE))
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
                .addContainerGap(23, Short.MAX_VALUE))
        );

        authPanel.setBackground(new java.awt.Color(51, 0, 102));
        authPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(226, 226, 251));
        jLabel12.setText("Username");

        tUn.setEditable(false);
        tUn.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(226, 226, 251));
        jLabel13.setText("Password");

        fPwd.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N

        org.jdesktop.layout.GroupLayout authPanelLayout = new org.jdesktop.layout.GroupLayout(authPanel);
        authPanel.setLayout(authPanelLayout);
        authPanelLayout.setHorizontalGroup(
            authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel12)
                    .add(jLabel13)
                    .add(tUn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .add(fPwd))
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(fPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tempPanel.setBackground(new java.awt.Color(153, 153, 255));
        tempPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cbTempHeat.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        cbTempAc.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 51));
        jLabel16.setText("Heater temperature:");

        jLabel17.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 51));
        jLabel17.setText("Air Conditioner temperature:");

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
                .addContainerGap(22, Short.MAX_VALUE))
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
        jLabel18.setText("Manage your profile: modify your data or your preferences");

        lbRole.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        lbRole.setForeground(new java.awt.Color(0, 0, 102));

        btMenu.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btMenu.setForeground(new java.awt.Color(153, 0, 153));
        btMenu.setText("MENU");
        btMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenuActionPerformed(evt);
            }
        });

        btLogOut.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btLogOut.setForeground(new java.awt.Color(153, 0, 153));
        btLogOut.setText("LOG OUT");
        btLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogOutActionPerformed(evt);
            }
        });

        btSave.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        btSave.setForeground(new java.awt.Color(51, 0, 153));
        btSave.setText("SAVE");
        btSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveActionPerformed(evt);
            }
        });

        pane.setBackground(new java.awt.Color(102, 0, 102));
        pane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel18)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(lbRole, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(authPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(namesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(tempPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(btMenu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(btSave, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(btLogOut)))
                            .add(pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 305, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel18)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, tempPanel, 0, 115, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, authPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(btMenu)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btLogOut)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSave, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pane)
                    .add(namesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
                .add(lbRole, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        try {
            if(!fieldsInserted()) {
                JOptionPane.showMessageDialog(null,"Please compile all the fields!",
                        "MISSING DATA",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // User is the one currently logged in.
            u.pwd = String.copyValueOf(fPwd.getPassword());
            u.firstname = tName.getText();
            u.surname = tSName.getText();
            u.temp_heat = Integer.parseInt(cbTempHeat.getSelectedItem().toString());
            u.temp_ac = Integer.parseInt(cbTempAc.getSelectedItem().toString());
            u.activate = getActivateString();
            
            //u.update(DbService.getDatabase());
	    userService.updateUser(database.getDatabase(), u);
            init(u);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
    }//GEN-LAST:event_btSaveActionPerformed

    private boolean fieldsInserted() {
        boolean res = true;
        if(Utilities.isEmpty(String.copyValueOf(fPwd.getPassword())) ||
                Utilities.isEmpty(tName.getText()) || Utilities.isEmpty(tSName.getText()))
            res = false;
        return res;
    }
    
    private String getActivateString() {
        String str = "";
        for(int i=0;i<checkBoxes.size();i++) {
            JCheckBox cb = (JCheckBox)checkBoxes.elementAt(i);
            if(cb.isSelected()) {
                Device dev = devices.get(i);//(Device)devices.elementAt(i);
                str += dev.getDeviceRoomId()+"-"+dev.getDeviceId()+",";
            }
        }
        // take away comma
        str = str.substring(0,str.length()-1);
        return str;
    }
    
    private void btLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOutActionPerformed
        this.remove(this.pane);
        mp.logOut(evt);
    }//GEN-LAST:event_btLogOutActionPerformed

    private void btMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenuActionPerformed
        this.remove(this.pane);
        mp.loadInitRolePanel(u);
    }//GEN-LAST:event_btMenuActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel authPanel;
    private javax.swing.JButton btLogOut;
    private javax.swing.JButton btMenu;
    private javax.swing.JButton btSave;
    private javax.swing.JComboBox cbTempAc;
    private javax.swing.JComboBox cbTempHeat;
    private javax.swing.JPasswordField fPwd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lbRole;
    private javax.swing.JPanel namesPanel;
    private javax.swing.JScrollPane pane;
    private javax.swing.JTextField tName;
    private javax.swing.JTextField tSName;
    private javax.swing.JTextField tUn;
    private javax.swing.JPanel tempPanel;
    // End of variables declaration//GEN-END:variables
    
}
