/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.exec_plan;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.tablemap.Window;
import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author sik
 */
public class WindowAgent extends AbstractTucsonAgent {

    private TracePanel tp;
    private TucsonTupleCentreId room_tc;
    private Window window;
    private String state;
    
    /* Constructor */
    public WindowAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Window window) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.room_tc = tid;
        this.window = window;
        this.state = "close";
    }

    /* Asynch operation */
    @Override
    public void operationCompleted(ITucsonOperation ito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void operationCompleted(AbstractTupleCentreOperation atco) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /* Agent code */
    @Override
    protected void main() {
        tp.appendText("-- Window AGENT FOR "+window.name+" STARTED. - " + this.getTucsonAgentId() + room_tc);
        EnhancedSynchACC acc = this.getContext();
        
        try {
            while(true) {
                LogicTuple template = new LogicTuple("window_mode", new Value(window.roomId), new Var("X"));
                ITucsonOperation op_in = acc.in(room_tc, template, Long.MAX_VALUE);
                String reqState = op_in.getLogicTupleResult().getArg(1).toString();
                if(state.compareTo(reqState) != 0) { // execute only if state is really changed
                    // execute command
                    this.state = reqState;
                    // notify current state after command execution
                    LogicTuple update_state = new LogicTuple("upd_window_curr_st",new Value(window.roomId), new Value(state));
                    ITucsonOperation op_out = acc.out(room_tc, update_state, null);

                    if(op_out.isResultSuccess())
                        System.out.println("OUT udp_window_curr_st OK!");
                    else
                        System.out.println("OUT udp_window_curr_st FAIL!");
                    tp.appendText("WindowAgent "+window.name+": CURRENT STATE "+state);
                }
                else
                    System.err.println("WindowAgent "+window.name+": ALREADY "+state);
            }
            
            //acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
