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
import it.unibo.homemanager.tablemap.Blind;
import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author sik
 */
public class BlindAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId room_tc;
    private TracePanel tp;
    private Blind blind;
    private String state;
    
    public BlindAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Blind blind) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.room_tc = tid;
        this.blind = blind;
        this.state = "down";
    }

    public BlindAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Blind blind, String state) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.room_tc = tid;
        this.blind = blind;
        this.state = state;
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
        tp.appendText("-- Blind AGENT FOR "+blind.name+" STARTED. - " + this.getTucsonAgentId() + room_tc);
        EnhancedSynchACC acc = this.getContext();
        
        try {
            //while(true) {
                // wait for command
                LogicTuple template = new LogicTuple("blind_mode",new Value(blind.roomId),new Var("X"));
                ITucsonOperation op_in = acc.in(room_tc, template, Long.MAX_VALUE);
                
                String reqState = op_in.getLogicTupleResult().getArg(1).toString();
                if(state.compareTo(reqState) != 0) { // execute only if state is really changed
                    // execute command
                    this.state = reqState;
                    // notify current state after command execution
                    LogicTuple template_out = new LogicTuple("upd_blind_curr_st",new Value(blind.roomId), new Value(state));
                    ITucsonOperation op_out = acc.out(room_tc, template_out, null);
                    tp.appendText("BlindAgent "+blind.name+": CURRENT STATE "+state);
                }
                else System.err.println("BlindAgent "+blind.name+": ALREADY "+state);
            //}
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("BlindAgent "+blind.name+": error in blinds activity.");
        }
    }
    
}
