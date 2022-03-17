/*
 * ApartmentPanel.java
 *
 * Created on 13 novembre 2006, 17.00
 */

package it.unibo.homemanager.userinterfaces;


import alice.logictuple.*;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuprolog.Agent;
import it.unibo.homemanager.ServiceFactory;

//import it.unibo.homemanager.tablemap.User;
//import it.unibo.homemanager.tablemap.UserService;
//import it.unibo.homemanager.tablemap.Room;
//import it.unibo.homemanager.tablemap.RoomService;
//import it.unibo.homemanager.tablemap.Sensor;
//import it.unibo.homemanager.tablemap.SensorService;
import it.unibo.homemanager.dbmanagement.*;
import it.unibo.homemanager.tablemap.*;
import java.awt.Font;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author  admin
 */

public class ApartmentPanel extends javax.swing.JPanel {
    
 //   private TupleCentreId tid;
    //private TucsonContext ctx;
    private JScrollPane pane = null;
    private JTextArea tPos;
    private Vector rooms;
    private Vector tids;
    private ApartmentPlan ap;
    private ServiceFactory sf;
    
    /** Creates new form ApartmentPanel */
    public ApartmentPanel() {
        //tid = t;
        pane = new JScrollPane();
        this.tids = null;
        tPos = new JTextArea();
        initComponents();
        addPositions();
	//this.refreshSituation();
    }
    
    public ApartmentPanel(Vector tid, ServiceFactory sf) {
        //tid = t;
	this.sf = sf;
        pane = new JScrollPane();
        this.tids = tid;
        tPos = new JTextArea();
        initComponents();
        addPositions();
        this.refreshSituation();
    }
    
    private void addPositions() {
        this.remove(this.pane);
        pane = new JScrollPane();
        pane.setBackground(new java.awt.Color(204,204,255));
        tPos.setBackground(new java.awt.Color(204,204,255));
        tPos.setFont(new java.awt.Font("Courier New",Font.PLAIN,12));
        tPos.setMargin(new java.awt.Insets(5,5,5,5));
        tPos.setEditable(false);
        pane.setViewportView(tPos);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setBounds(310,25,220,305);
        this.add(pane);
    }
    
    private void fillTPos() {
        String str = "";
        try {
            Database db = sf.getDatabaseInterface().getDatabase();
            rooms = sf.getRoomServiceInterface().getRooms(db);
            // read list_checkPeople
            TucsonAgentId agent = new TucsonAgentId("apartmentPanelTupleReader");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            LogicTuple lt = new LogicTuple("list_checkPeople", new Var("Y"), new Var("Z"), new Var("W"), new Var("X"));
            List listResults = new ArrayList();
            
            for(int i=0;i<this.tids.size();i++){
                ITucsonOperation op_rdAll = acc.rdAll((TucsonTupleCentreId)this.tids.elementAt(i), lt, null);
                listResults.addAll(op_rdAll.getLogicTupleListResult());
            }
            
            for(int i=0;i<rooms.size();i++) {
                Room r = (Room)rooms.elementAt(i);
                str += getRoomString(r);
                str += addUsersNames(listResults,r.idRoom,db);
            }
            tPos.setText(str);
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getRoomString(Room r) {
        String str = "\n"+r.name+"\n";
        return str.toUpperCase();
    }
    
    private String addUsersNames(List l,int idR,Database db) {
        String str = "";
        
        try {
            for(int i=0;i<l.size();i++) {
                LogicTuple lt = LogicTuple.parse(l.get(i).toString());
                if(lt.getArg(2).intValue() == idR) {
                    Sensor s = sf.getSensorServiceInterface().getSensorById(db,lt.getArg(0).intValue());
                    if(lt.getArg(1).intValue() > 0) {
                        User u = sf.getUserServiceInterface().getUserById(db,lt.getArg(1).intValue());
                        str += "   "+u.username+": "+s.name+"\n";
                    }
                    else str += "   Visitor"+": "+s.name+"\n";
                }
            }
            if(str.equals("")) str += "   empty\n";
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return str;
    }
    
    public void refreshSituation() {
        fillTPos();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(204, 204, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 0, 102)), "View Apartment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier New", 1, 12), new java.awt.Color(0, 0, 51))); // NOI18N
        setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        setMaximumSize(new java.awt.Dimension(559, 392));
        setMinimumSize(new java.awt.Dimension(559, 392));
        setPreferredSize(new java.awt.Dimension(559, 392));

        jLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/apt3d.jpg"))); // NOI18N
        jLabel.setToolTipText("Click me to view Apartament plan");
        jLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLabelFocusGained(evt);
            }
        });
        jLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 303, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(244, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(49, 49, 49)
                .add(jLabel)
                .addContainerGap(133, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLabelFocusGained
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jLabelFocusGained

    private void jLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelMouseClicked
        // TODO add your handling code here:
         this.ap = new ApartmentPlan();
        this.ap.setVisible(true);
    }//GEN-LAST:event_jLabelMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel;
    // End of variables declaration//GEN-END:variables
    
}
