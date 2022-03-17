/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.userinterfaces;

import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.ServiceFactory;
import java.util.Vector;

/**
 *
 * @author sik
 */
public class GeneralFrame extends javax.swing.JFrame {

    private Vector tCenters;
    private int roomId, sensId;
    private TucsonTupleCentreId casa_tc, rbac_tc;
    private ServiceFactory sf;
    
    /**
     * Creates new form GeneralForm
     */
    public GeneralFrame() {
        initComponents();
    }

    public GeneralFrame(String title, Vector tuplesCenters, int roomId, int sensId, TucsonTupleCentreId casa_tc, TucsonTupleCentreId rbac_tc, ServiceFactory sf) {
        super(title);
        this.tCenters = tuplesCenters;
        this.roomId = roomId;
        this.sensId = sensId;
        this.casa_tc = casa_tc;
        this.rbac_tc = rbac_tc;
        this.sf = sf;
        initComponents();
    }
    
    public TracePanel getTracePanel() {
        return this.tp;
    }
    
    public ApartmentPanel getApPanel() {
        return this.apartmentPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tp = new it.unibo.homemanager.userinterfaces.TracePanel();
        apartmentPanel = new it.unibo.homemanager.userinterfaces.ApartmentPanel(tCenters, sf);
        mainPanel = new it.unibo.homemanager.userinterfaces.MainPanel(tp, tCenters, roomId, sensId, casa_tc, rbac_tc, sf);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setMaximumSize(new java.awt.Dimension(559, 342));
        mainPanel.setMinimumSize(new java.awt.Dimension(559, 342));
        mainPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(apartmentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(tp, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(tp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(apartmentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private it.unibo.homemanager.userinterfaces.ApartmentPanel apartmentPanel;
    private it.unibo.homemanager.userinterfaces.MainPanel mainPanel;
    private it.unibo.homemanager.userinterfaces.TracePanel tp;
    // End of variables declaration//GEN-END:variables
}
