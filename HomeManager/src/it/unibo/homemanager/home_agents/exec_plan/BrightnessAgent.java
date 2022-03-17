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
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author sik
 */
public class BrightnessAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId room_tc, home;
    private TracePanel tp;
    private int room;
    
    public BrightnessAgent(String id,TracePanel tp, TucsonTupleCentreId tid, TucsonTupleCentreId casa_tc ,int room) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.room_tc = tid;
        this.home = casa_tc;
        this.room = room;
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
        tp.appendText("-- BRIGHTNESS AGENT FOR "+this.room+" STARTED. - " + this.getTucsonAgentId() + room_tc);
        EnhancedSynchACC acc = this.getContext();
        
        try{
            while(true) {
                LogicTuple template = new LogicTuple("check_lights",new Value(this.room));
                ITucsonOperation op_in = acc.in(room_tc, template, Long.MAX_VALUE);
                   
                LogicTuple template_rdp = new LogicTuple("external_brightness", new Var("Y"));
                ITucsonOperation op_rdp = acc.rdp(this.home, template_rdp, Long.MAX_VALUE);
                int i = op_rdp.getLogicTupleResult().getArg(0).intValue();

                if(i>70){
                    if(this.room != 2){
                        acc.out(room_tc, new LogicTuple("blind_mode", new Value(this.room), new Value("up")), null);
                        tp.appendText("-- BRIGHTNESS AGENT FOR "+this.room+" BLINDS UP.");
                    }
                    else
                        acc.out(room_tc, new LogicTuple("light_mode", new Value(this.room), new Value("on")), null);
                }
                else
                    acc.out(room_tc, new LogicTuple("light_mode", new Value(this.room), new Value("on")), null);
            }
                
            //acc.exit();
          }catch(Exception ex){
              ex.printStackTrace();
              tp.appendText("BrightnessAgent "+this.room+": error in activity.");
          }
    }
    
}
