/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.exec_plan;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.tablemap.Lamp;
import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author sik
 */
public class LampAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId room_tc;
    private TracePanel tp;
    private Lamp l;
    private String state;
    
    public LampAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Lamp lamp) throws TucsonInvalidAgentIdException {
        super(id);
        this.room_tc = tid;
        this.tp = tp;
        this.l = lamp;
        this.state = "off";
    }

    public LampAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Lamp lamp, String state) throws TucsonInvalidAgentIdException {
        super(id);
        this.room_tc = tid;
        this.tp = tp;
        this.l = lamp;
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
        LogicTuple template_in = null;
        try {
            template_in = new LogicTuple("light_mode",new Value(l.sensId),new Var("X"));
        } catch (InvalidVarNameException e) {
            // cannot happen
        }
        tp.appendText("-- LAMP AGENT FOR "+l.name+" STARTED.");

        try {
            EnhancedSynchACC acc = this.getContext();
            //while(true) {
                // wait for command
                ITucsonOperation op_in = acc.in(room_tc, template_in, Long.MAX_VALUE);
                System.out.println("LAMPAGENT "+l.name+": " + op_in.getLogicTupleResult());
                String reqState = op_in.getLogicTupleResult().getArg(1).toString();
                if(state.compareTo(reqState) != 0) { // execute only if state is really changed
                    // execute command
                    this.state = reqState;
                    LogicTuple template_out = new LogicTuple("upd_light_curr_st",new Value(l.sensId), new Value(state));
                    // notify current state after command execution
                    ITucsonOperation op_out = acc.out(room_tc, template_out, null);
                    
                    if(op_out.isResultSuccess())
                        tp.appendText("LAMPAGENT "+l.name+": CURRENT STATE "+state);
                    else
                        this.say("FAIL: op_out ha fallito!");
                }
                else System.err.println("LampAgent "+l.name+": ALREADY "+state);
            //}
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("LampAgent "+l.name+": error in lights activity.");
        }
    }
    
}
