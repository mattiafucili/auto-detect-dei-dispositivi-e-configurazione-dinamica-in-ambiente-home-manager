/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.RBAC;


import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;


import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Administrator
 */
public class GestoreDisattivazioneRuolo extends AbstractTucsonAgent {

    private TucsonTupleCentreId rbac,  casa;
    private int usr_id;
    private TracePanel tp;
    private EnhancedSynchACC acc;


    public GestoreDisattivazioneRuolo(String id, TracePanel tracep, TucsonTupleCentreId rbac_tc, TucsonTupleCentreId casa_tc) throws TucsonInvalidAgentIdException {
        super(id);
        this.rbac = rbac_tc;
        this.casa = casa_tc;
        tp = tracep;
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
        try{
            while(true) {
                acc = this.getContext();

                ITucsonOperation op_in = acc.in(this.casa, new LogicTuple("deactivate_req", new Var("X"), new Var("Y")), Long.MAX_VALUE);
                LogicTuple lt = op_in.getLogicTupleResult(); //in(this.casa, new LogicTuple("deactivate_req", new Var("X"), new Var("Y")));
                this.usr_id = lt.getArg(0).intValue();
                String role = lt.getArg(1).getName();

                ITucsonOperation op_inp = acc.inp(this.rbac, new LogicTuple("active_role", new Value(this.usr_id), new Value(role)), null);
                if (op_inp.isResultSuccess())
                    tp.appendText("RUOLO " + role + " disattivato.");
                else
                    tp.appendText("ERRORE: ruolo " + role + " non attivo per l'utente.");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
