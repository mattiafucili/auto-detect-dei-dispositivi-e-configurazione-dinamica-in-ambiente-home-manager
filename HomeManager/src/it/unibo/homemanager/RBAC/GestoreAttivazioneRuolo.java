/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.homemanager.RBAC;

import alice.logictuple.*;
import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import alice.tuprolog.Term;
import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class GestoreAttivazioneRuolo extends AbstractTucsonAgent {

    private TucsonTupleCentreId rbac,  casa;
    //private int usr_id;
    private TracePanel tp;
    private EnhancedSynchACC acc;

    public GestoreAttivazioneRuolo(String id, TracePanel tracep, TucsonTupleCentreId rbac_tc, TucsonTupleCentreId casa_tc) throws TucsonInvalidAgentIdException {
        super(id);
        this.rbac = rbac_tc;
        this.casa = casa_tc;
        this.tp = tracep;
    }
    
    @Override
    public void operationCompleted(ITucsonOperation ito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void operationCompleted(AbstractTupleCentreOperation atco) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void main() {
        //System.out.println("GestoreAttivazioneRuolo attivo!");
        acc = this.getContext();
        
        while(true){
            try {
                //e.g.: activate_req(3, default)
                LogicTuple activateReqTemplate = new LogicTuple("activate_req", new Var("X"), new Var("Y"));
                ITucsonOperation op_in = acc.in(this.casa, activateReqTemplate, Long.MAX_VALUE);
                LogicTuple lt = op_in.getLogicTupleResult();
                //this.usr_id = lt.getArg(0).intValue();
                int usr_id = lt.getArg(0).intValue();
                String role = lt.getArg(1).getName();
                System.err.println("[GAR] usr_id: " + usr_id + " - role: " + role);

                /*if(!role.equals("default")){
                    ITucsonOperation op_rdp = acc.rdp(this.rbac, new LogicTuple("active_role", new Value(this.usr_id), new Value(role)), null);
                    if(op_rdp.isResultSuccess())
                        tp.appendText("RUOLO " + role + " già attivo per l'utente.");
                    else {
                        LogicTuple assUserRolesTemplate = new LogicTuple("ass_user_roles", new Value(this.usr_id), new Var("X"));
                        ITucsonOperation op_rdAll = acc.rdAll(this.rbac, assUserRolesTemplate, null);
                        List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
                        
                        boolean res = false;
                        for (int i = 0; i < l.size(); i++) {
                            //LogicTuple r = LogicTuple.parse(l.get(i).toString());
                            //if (r.getArg(1).getName().equals(role))
                            if(l.get(i).getArg(1).getName().equals(role))
                                res = true;
                            
                            //break;
                        }
                        
                        if (res)
                            activate_role(role);
                    }
                } else {
                    LogicTuple defaultActivateTemplate = new LogicTuple("default_activate", new Value(this.usr_id), new Var("Y"));
                    ITucsonOperation op_rdp = acc.rdp(this.rbac, defaultActivateTemplate, null);
                    LogicTuple lt3 = null;
                    if(op_rdp.isResultSuccess()) {
                        lt3 = op_rdp.getLogicTupleResult();
                        role = lt3.getArg(1).getName();
                        System.err.println("[GAR] default role = " + role);
                    //if(lt3!=null){
                        //LogicTuple lt1 = null;//rdp(this.rbac, new LogicTuple("active_role", new Value(this.usr_id), new Value(lt3.getArg(1).getName())));
                        //if(lt1==null)
                        op_rdp = acc.rdp(this.rbac, new LogicTuple("active_role", new Value(this.usr_id), new Value(lt3.getArg(1).getName())), null);
                        if(op_rdp.isResultFailure())
                            activate_role(role);
                    }
                }*/
                
                if(role.equals("default"))
                    role = findDefaultRole(usr_id);
                
                if(canActivate(usr_id, role))
                    activate_role(usr_id, role);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private String findDefaultRole(int usrId) {
        String defaultRole = null;
        try {
            LogicTuple dActTemplate = new LogicTuple("default_activate", new Value(usrId), new Var("X"));
            ITucsonOperation op_rdp = acc.rdp(this.rbac, dActTemplate, null);
            
            defaultRole = op_rdp.getLogicTupleResult().getArg(1).getName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return defaultRole;
    }

    private boolean canActivate(int user_id, String role) {
        boolean result = false;
        try {
            ITucsonOperation op_rdp = acc.rdp(this.rbac, new LogicTuple("ass_user_roles", new Value(user_id), new Value(role)), null);
            //result = op_rdp.isResultSuccess();
            boolean canHave = op_rdp.isResultSuccess();
            
            op_rdp = acc.rdp(this.rbac, new LogicTuple("active_role", new Value(user_id), new Value(role)), null);
            boolean alreadyActive = op_rdp.isResultSuccess();
            
            result = canHave && !alreadyActive;
            System.err.println("canHave: " + canHave + " *** alreadyActive: " + alreadyActive + " -> result: " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    //private void activate_role(String role) {
    private void activate_role(int user_id, String role) {
        try {
            System.err.println("Activate Role called!");
            //LogicTuple lt = inp(rbac, new LogicTuple("rd_all", new Value("active_role", new Value(this.usr_id), new Var("X")), new Var("Y")));
            LogicTuple activeRolesTemplate = new LogicTuple("active_role", new Value(user_id), new Var("X"));
            ITucsonOperation op_rdAll = acc.rdAll(rbac, activeRolesTemplate, Long.MAX_VALUE);
            System.err.println("[GAR@activate_role] list of active roles: " + op_rdAll.getLogicTupleListResult());
            //if (lt != null) {
            if(op_rdAll.isResultSuccess()) {
                //Lista dei ruoli attivi dell'utente
                List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
                System.err.println("Active roles for userId " + user_id + ": " + l.size());
                List<String> rolesActive = new ArrayList<>();
                for (int i = 0; i < l.size(); i++) {
                    //LogicTuple ra = LogicTuple.parse(l.get(i).toString());
                    //LogicTuple ra = l.get(i);
                    //System.err.println("[ActivateRole@GAR] ra: " + ra);
                    //String role_a = ra.getArg(1).getName();
                    //LogicTuple lt = LogicTuple.parse(l.get(i).toString());
                    LogicTuple lt = l.get(i);
                    System.err.println("[ActivateRole@GAR] lt: " + lt + " // arg: " + lt.getArg(1).getName() + " <> role: " + role);
                    if (lt.getArg(1).getName().equals(role)) {
                        //Controllo inutile: se il ruolo è già attivo, non arrivo qui
                        javax.swing.JOptionPane.showMessageDialog(null, "ERROR: selected role " + role + " is already active!",
                                "", javax.swing.JOptionPane.ERROR_MESSAGE);
                        break;
                    } else {
                        rolesActive.add(lt.getArg(1).getName());
                    }
                }


                //lt = inp(rbac, new LogicTuple("rd_all", new Value("role_set_dsd", new Var("Z"), new Var("X")), new Var("Y")));
                LogicTuple roleSetDSDTemplate = new LogicTuple("role_set_dsd", new Var("Z"), new Var("X"));
                op_rdAll = acc.rdAll(rbac, roleSetDSDTemplate, null);
                //lista dei role_set_ssd
                //if(lt != null){
                if(op_rdAll.isResultSuccess()) {
                //List l1 = lt.getArg(1).toList();
                    List<LogicTuple> roleSetDSDList = op_rdAll.getLogicTupleListResult();
                    System.err.println("[GAR@role_activate] role_set_dsd: " + roleSetDSDList);
                    for (int j = 0; j < roleSetDSDList.size(); j++) {
                        //LogicTuple rs_dsd = LogicTuple.parse(roleSetDSDList.get(j).toString());
                        LogicTuple rs_dsd = roleSetDSDList.get(j);
                        String rs_name = rs_dsd.getArg(0).getName();
                        
                        List<Term> termList = rs_dsd.getArg(1).toList();
                        List<String> roleStringList = new ArrayList<>();
                        for(Term s:termList)
                            roleStringList.add(s.toString());
                        //if (l3.contains(role)) {
                        if(roleStringList.contains("'"+role+"'")) {
                            System.err.println("[ActivateRole@GAR] " + role + " found!");
                            //int h = 0;
                            int k = 1;
                            //while (h < rolesActive.size()) {
                            for(String activeRole:rolesActive) {
                                //System.err.println("Does " + roleStringList + " contain " + rolesActive.get(h) + "?");
                                //if (roleStringList.contains("'"+rolesActive.get(h)+"'")) {
                                if (roleStringList.contains("'"+activeRole+"'")) {
                                    //System.err.println("YES!");
                                    k++;
                                } //else
                                    //System.err.println("NOPE!");
                                
                                //h++;
                            }
                            //lt = rdp(this.rbac, new LogicTuple("dsd", new Value(rs_name), new Var("Y")));
                            ITucsonOperation op_rdp = acc.rdp(rbac, new LogicTuple("dsd", new Value(rs_name), new Var("Y")), null);
                            int card = op_rdp.getLogicTupleResult().getArg(1).intValue();
                            System.err.println("h: " + /*h*/"0" + ", k: " + k + ", card: " + card);
                            if (k > card) {
                                javax.swing.JOptionPane.showMessageDialog(null, "ERROR: DSD RULE " + rs_name + " violated: ACTIVE ROLES > " + card,
                                        "", javax.swing.JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        } else
                            System.out.println("Ruolo " + role + " non presente nella lista " + termList);
                    }
                    acc.out(rbac, new LogicTuple("active_role", new Value(user_id), new Value(role)), null);
                }else{
                    acc.out(rbac, new LogicTuple("active_role", new Value(user_id), new Value(role)), null);
                }
            } else
                System.err.println("Non sono presenti ruoli attivi per l'utente " + user_id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
