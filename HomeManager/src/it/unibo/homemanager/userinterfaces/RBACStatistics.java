/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RBACStatistics.java
 *
 * Created on 30-set-2009, 15.44.27
 */

package it.unibo.homemanager.userinterfaces;


import alice.logictuple.*;
import alice.tucson.api.*;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.ServicesInterfaces.RoomServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.tablemap.User;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
/**
 *
 * @author Administrator
 */
public class RBACStatistics extends javax.swing.JPanel {

    private MainPanel mp;
    private TucsonTupleCentreId rbac;
    private RBACManager rbac_man;
    private User user;
    private String ass_to_change;
    private Vector users;
    private String role_set;
    private int cardinality;

    private TucsonAgentId agent;
    private UserServiceInterface userService;
    private RoomServiceInterface roomService;
    private Database database;
    
    /** Creates new form RBACStatistics */
    public RBACStatistics(MainPanel manp, TucsonTupleCentreId tc, RBACManager rm, ServiceFactory sf) {
              this.mp = manp;
        this.rbac = tc;
        this.rbac_man = rm;
        this.userService = sf.getUserServiceInterface();
	this.roomService = sf.getRoomServiceInterface();
        try {
	    this.database = sf.getDatabaseInterface();
            agent = new TucsonAgentId("viewRBACStats");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initComponents();
    }

    public void init(User u){
        this.user = u;
//        fillListAss();
//        fillUsersList();
        fillListRoles();
    }

    private void fillDate(String activation) {
        StringTokenizer st = new StringTokenizer(activation, " ");
        int uid = Integer.parseInt(st.nextToken());
        String role = this.jList_Roles.getSelectedValue().toString();
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdp = acc.rdp(this.rbac,
                    new LogicTuple("act_role_times",
                            new Value(uid),
                            new Value(role),
                            new Var("X"),
                            new Var("Y")),
                    null);
            
            int millis = op_rdp.getLogicTupleResult().getArg(3).intValue();
            //Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"),Locale.ITALY);
            Calendar cal = Calendar.getInstance(TimeZone.getDefault(),Locale.getDefault());
       
            //cal.setTimeInMillis(millis);
            cal.set(Calendar.MILLISECOND, millis);
            //String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR)+
            //        " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
            //this.jText_Date.setText(date);
            //DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.getDefault());
            //String data = f.format(cal.getTime());
            String date = formatDate(cal.getTime());
            this.jText_Date.setText(date);
            acc.exit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void fillEvents(String activation) {
        StringTokenizer st = new StringTokenizer(activation, " ");
        int uid = Integer.parseInt(st.nextToken());
        String role = this.jList_Roles.getSelectedValue().toString();
        Vector evt = new Vector();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"),Locale.ITALY);
       
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            //DEVICES
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,
                    new LogicTuple("dev_acts",
                        new Value(uid),
                        new Value(role),
                        new Var("A"),
                        new Var("B"),
                        new Var("C")),
                    null);
            List<LogicTuple> readList = op_rdAll.getLogicTupleListResult();
            for(int i=0;i<readList.size();i++){
                LogicTuple devs = LogicTuple.parse(readList.get(i).toString());
         //       cal.setTimeInMillis(devs.getArg(4).longValue());
                cal.set(Calendar.MILLISECOND, devs.getArg(4).intValue());
                //String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR)+
                //    " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
                String date = formatDate(cal.getTime());
                if(devs.getArg(3).intValue() == 1)
                    evt.add(devs.getArg(2).getName()+" ACCESSO CONSENTITO IN DATA: "+date);
                else
                    evt.add(devs.getArg(2).getName()+" ACCESSO NEGATO IN DATA: "+date);
            }

            //ROOMS
            op_rdAll = acc.rdAll(this.rbac,
                    new LogicTuple("room_acts",
                        new Var("D"), //tipo movimento
                        new Value(uid), //user
                        new Value(role),//role
                        new Var("B"),//locale
                        new Var("C")), //tempo
                    null);
            
            readList = op_rdAll.getLogicTupleListResult();
            for(int i=0;i<readList.size();i++){
                LogicTuple devs = LogicTuple.parse(readList.get(i).toString());
                cal.setTimeInMillis(devs.getArg(4).longValue());
                //String date = cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR)+
                //    " "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
                String date = formatDate(cal.getTime());
                Room r = roomService.getRoomById(database.getDatabase(), devs.getArg(3).intValue());
                evt.add(r.name+" EVENTO "+devs.getArg(0).getName()+" IN DATA: "+date);
            }
            acc.exit();
            
            //if(evt!=null)
            this.jList_Events.setListData(evt);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void fillListRoles(){
        List<LogicTuple> roles;
        Vector rolesVector = new Vector();
        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac, new LogicTuple("role", new Var("X")), Long.MAX_VALUE);
            roles = op_rdAll.getLogicTupleListResult();
            System.err.println("roles: " + roles);
            
            //LogicTuple rol;
            for(int i=0;i<roles.size();i++)
            //{
                //rol = LogicTuple.parse(roles.get(i).toString());
                rolesVector.add(roles.get(i).getArg(0).getName());
            //}
            this.jList_Roles.setListData(rolesVector);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillUserTimes(String role){
        List<LogicTuple> actRoleTimes;
        Vector ro = new Vector();

        try {
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,
                    new LogicTuple("act_role_times",
                        new Var("X"), //user
                        new Value(role), //role
                        new Var("Z"), //n di volte attivato
                        new Var("K")), // tempo
                    Long.MAX_VALUE);
            actRoleTimes = op_rdAll.getLogicTupleListResult();
            System.err.println("act_role_times: " + actRoleTimes);

            LogicTuple rol;
            for(int i=0;i<actRoleTimes.size();i++)
            {
                rol = actRoleTimes.get(i);//LogicTuple.parse(actRoleTimes.get(i).toString());
                User user = userService.getUserById(database.getDatabase(), rol.getArg(0).intValue());
                ro.add(user.idUser+" "+user.firstname+" "+user.surname+" ACTS: "+rol.getArg(2).intValue());
            }
            this.jList_Activations.setListData(ro);
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected void hidePanel() {
        if(isVisible())
            setVisible(false);
    }
    
    private void remove_statistics() {
        try{
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            acc.inAll(this.rbac,
                    new LogicTuple("act_role_times",
                            new Var("X"), //user
                            new Var("D"), //role
                            new Var("Z"), //n di volte attivato
                            new Var("K")),// tempo
                    null);

            acc.inAll(this.rbac,
                    new LogicTuple("dev_acts",
                        new Var("W"),
                        new Var("Q"),
                        new Var("A"),
                        new Var("B"),
                        new Var("C")),
                    null);

            acc.inAll(this.rbac,
                    new LogicTuple("room_acts",
                        new Var("D"),   //tipo movimento
                        new Var("E"),   //user
                        new Var("Q"),   //role
                        new Var("B"),   //locale
                        new Var("C")),  //tempo
                    null); 
            acc.exit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private String formatDate(Date data) {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.getDefault());
        return f.format(data);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList_Roles = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Activations = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jText_Date = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_Events = new javax.swing.JList();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();

        jToggleButton1.setText("jToggleButton1");

        setBackground(new java.awt.Color(204, 204, 255));
        setPreferredSize(new java.awt.Dimension(538, 343));

        jPanel1.setBackground(new java.awt.Color(153, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Roles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 102, 204))); // NOI18N

        jList_Roles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_Roles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_RolesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList_Roles);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("RBAC STATISTICS");

        jButton1.setText("RBAC MENU");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("MENU");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("LOGOUT");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(153, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Activation(s)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 102, 204))); // NOI18N

        jList_Activations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_Activations.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_ActivationsValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList_Activations);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(153, 255, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Last Activation Time", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 102, 204))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jText_Date, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jText_Date, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(153, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Activities", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 102, 204))); // NOI18N
        jPanel4.setForeground(new java.awt.Color(0, 102, 204));

        jScrollPane3.setViewportView(jList_Events);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToggleButton2.setText("REFRESH STATISTICS");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jToggleButton3.setText("CLEAR STATISTICS");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(jToggleButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToggleButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addComponent(jToggleButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList_RolesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_RolesValueChanged
        // TODO add your handling code here:
        if(!this.jList_Roles.isSelectionEmpty() && !this.jList_Roles.getValueIsAdjusting())
            fillUserTimes(this.jList_Roles.getSelectedValue().toString());
            
    }//GEN-LAST:event_jList_RolesValueChanged

    private void jList_ActivationsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_ActivationsValueChanged
        // TODO add your handling code here:
        if(!this.jList_Activations.isSelectionEmpty() && !this.jList_Activations.getValueIsAdjusting()){
            fillDate(this.jList_Activations.getSelectedValue().toString());
            fillEvents(this.jList_Activations.getSelectedValue().toString());
        }
    }//GEN-LAST:event_jList_ActivationsValueChanged

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        // TODO add your handling code here:
        this.fillListRoles();
        this.jList_Activations.removeAll();
        this.jList_Events.removeAll();
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
        // TODO add your handling code here:
        remove_statistics();
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.mp.loadManRBACPanel();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.mp.loadInitRolePanel(user);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.mp.logOut(evt);
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList_Activations;
    private javax.swing.JList jList_Events;
    private javax.swing.JList jList_Roles;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jText_Date;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    // End of variables declaration//GEN-END:variables

}
