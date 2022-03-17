/*
 * ViewPlanPanel.java
 *
 * Created on 5 novembre 2006, 9.18
 */

package it.unibo.homemanager.userinterfaces;

import it.unibo.homemanager.tablemap.*;

import java.util.Vector;
import java.util.List;

import alice.tucson.api.*;
import alice.logictuple.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.SensorServiceInterface;

/**
 *
 * @author  admin
 */
public class ViewPlanPanel extends javax.swing.JPanel {
    
    private MainPanel mp;
    private User u;
    //private DeviceService ds;
    //private RoomService rs;
    //private SensorService ss;
    private Vector tid;
    
    private Database database;
    private RoomServiceInterface roomService;
    private DeviceServiceInterface deviceService;
    private SensorServiceInterface sensorService;
    private TucsonAgentId agent;
    
    /** Creates new form ViewPlanPanel */
    public ViewPlanPanel(MainPanel m,Vector t, ServiceFactory sf) {
        mp = m;
        tid = t;
	this.roomService = sf.getRoomServiceInterface();
	this.deviceService = sf.getDeviceServiceInterface();
	this.sensorService = sf.getSensorServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("viewPlanAgent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }
    
    protected void init(User usr) {
        u = usr;
        
        try {
            // lights
            tLights.setText("");
            List<LogicTuple> l = getLightsStates();
            for(int i=0;i<l.size();i++) {
                for(int j=0;j<((List)l.get(i)).size();j++) {
                LogicTuple lt = LogicTuple.parse(((List)l.get(i)).get(j).toString());
                tLights.setText(tLights.getText()+getLightString(lt)+
                        (i==l.size()-1?"":"\n"));
                }
            }
            // devices
            tDevs.setText("");
            l = getDevStates();
            for(int i=0;i<l.size();i++) {
                for(int j=0;j<((List)l.get(i)).size();j++) {
                LogicTuple lt = LogicTuple.parse(((List)l.get(i)).get(j).toString());
                tDevs.setText(tDevs.getText()+getDevString(lt)+
                        (i==l.size()-1?"":"\n"));
                }
            }
            // temperatures
            tTemps.setText("");
            l = getCurrTemps();
            for(int i=0;i<l.size();i++) {
                for(int j=0;j<((List)l.get(i)).size();j++) {
                LogicTuple lt = LogicTuple.parse(((List)l.get(i)).get(j).toString());
                tTemps.setText(tTemps.getText()+getTempString(lt)+
                        (i==l.size()-1?"":"\n"));
                }
            }
            //
            if(!isVisible())
                setVisible(true);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getTempString(LogicTuple lt) {
        String str = "Temperature in ";
        try {
            //rs = new RoomService();
            Room r = roomService.getRoomById(database.getDatabase(),lt.getArg(0).intValue());
            str += r.name+" : "+lt.getArg(1).toString()+" °C";
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }
    
    private String getDevString(LogicTuple lt) {
        String str = "Device ";
        try {
            //ds = new DeviceService();
            Device d = deviceService.getDeviceById(database.getDatabase(),lt.getArg(0).intValue());
            str += d.getDeviceName()+" : currently "+lt.getArg(1).toString();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }
    
    private String getLightString(LogicTuple lt) {
        String str = "Light related to sensor ";
        try {
            //ss = new SensorService();
            Sensor s = sensorService.getSensorById(database.getDatabase(),lt.getArg(0).intValue());
            str += s.name+" : currently "+lt.getArg(1).toString();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }
    
    private Vector getDevStates() {
        List<LogicTuple> l = null;
        Vector dev = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            for(int i=0; i<tid.size();i++)
            {
                ITucsonOperation op_rdAll = acc.rdAll((TucsonTupleCentreId)tid.elementAt(i),
                        new LogicTuple("dev_curr_st",
                                new Var("Y"),
                                new Var("Z")),
                        null);
                l = op_rdAll.getLogicTupleListResult();
                dev.add(l);
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return dev;
    }
    
    private Vector getLightsStates() {
        List<LogicTuple> l = null;
        Vector light = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0; i<tid.size();i++)
            {
                ITucsonOperation op_rdAll = acc.rdAll((TucsonTupleCentreId)tid.elementAt(i),
                        new LogicTuple("light_curr_st",
                                new Var("Y"),
                                new Var("Z")),
                        null);
                l = op_rdAll.getLogicTupleListResult();
                light.add(l);
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return light;
    }
    
    private Vector getCurrTemps() {
        List<LogicTuple> l = null;
        Vector temps = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0;i<tid.size();i++)
            {
                ITucsonOperation op_rdAll =  acc.rdAll((TucsonTupleCentreId)tid.elementAt(i),
                        new LogicTuple("curr_temp",
                                new Var("Y"),
                                new Var("Z")),
                        null);
                l = op_rdAll.getLogicTupleListResult();
                temps.add(l);
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return temps;
    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tLights = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tDevs = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        tTemps = new javax.swing.JTextArea();
        btMenu = new javax.swing.JButton();
        btLogOut = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btRefresh = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 255));
        setFont(new java.awt.Font("Courier New", 0, 12));
        setMaximumSize(new java.awt.Dimension(538, 343));
        setMinimumSize(new java.awt.Dimension(538, 343));
        setPreferredSize(new java.awt.Dimension(538, 343));
        jScrollPane1.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 11), new java.awt.Color(0, 0, 51)));
        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 12));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(152, 78));
        tLights.setBackground(new java.awt.Color(255, 255, 204));
        tLights.setColumns(20);
        tLights.setFont(new java.awt.Font("Courier", 0, 11));
        tLights.setLineWrap(true);
        tLights.setRows(3);
        tLights.setWrapStyleWord(true);
        jScrollPane1.setViewportView(tLights);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Devices", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 11), new java.awt.Color(0, 0, 51)));
        jScrollPane2.setFont(new java.awt.Font("Courier New", 0, 12));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(152, 62));
        tDevs.setBackground(new java.awt.Color(255, 255, 204));
        tDevs.setColumns(20);
        tDevs.setFont(new java.awt.Font("Courier", 0, 11));
        tDevs.setRows(2);
        jScrollPane2.setViewportView(tDevs);

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Temperature in rooms", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 11), new java.awt.Color(0, 0, 51)));
        jScrollPane3.setFont(new java.awt.Font("Courier New", 0, 12));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(152, 70));
        tTemps.setBackground(new java.awt.Color(255, 255, 204));
        tTemps.setColumns(20);
        tTemps.setFont(new java.awt.Font("Courier", 0, 11));
        tTemps.setRows(3);
        jScrollPane3.setViewportView(tTemps);

        btMenu.setFont(new java.awt.Font("Courier New", 1, 14));
        btMenu.setForeground(new java.awt.Color(153, 0, 153));
        btMenu.setText("MENU");
        btMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenuActionPerformed(evt);
            }
        });

        btLogOut.setFont(new java.awt.Font("Courier New", 1, 14));
        btLogOut.setForeground(new java.awt.Color(153, 0, 153));
        btLogOut.setText("LOG OUT");
        btLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogOutActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 13));
        jLabel1.setForeground(new java.awt.Color(0, 0, 102));
        jLabel1.setText("See current plan:");

        btRefresh.setFont(new java.awt.Font("Courier New", 1, 14));
        btRefresh.setForeground(new java.awt.Color(0, 0, 153));
        btRefresh.setText("REFRESH");
        btRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRefreshActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .add(297, 297, 297))
                            .add(layout.createSequentialGroup()
                                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btRefresh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btMenu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btLogOut))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(btMenu)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btLogOut))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(btRefresh)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRefreshActionPerformed
        init(u);
    }//GEN-LAST:event_btRefreshActionPerformed

    private void btLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOutActionPerformed
        mp.logOut(evt);
    }//GEN-LAST:event_btLogOutActionPerformed

    private void btMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenuActionPerformed
        mp.loadInitRolePanel(u);
    }//GEN-LAST:event_btMenuActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLogOut;
    private javax.swing.JButton btMenu;
    private javax.swing.JButton btRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea tDevs;
    private javax.swing.JTextArea tLights;
    private javax.swing.JTextArea tTemps;
    // End of variables declaration//GEN-END:variables
    
}
