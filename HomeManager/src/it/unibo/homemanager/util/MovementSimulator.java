/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.Vector;

/**
 *
 * @author sik
 */
public class MovementSimulator extends AbstractTucsonAgent {

    private Vector tCenters;
    private TracePanel tp;
    private final LogicTuple[] movements = 
        {
            new LogicTuple("detection", new Value("entry"), new Value(1), new Value(1), new Value(1)),
            new LogicTuple("detection", new Value("move"), new Value(2), new Value(1), new Value(2)),
            new LogicTuple("detection", new Value("move"), new Value(3), new Value(1), new Value(3)),
            new LogicTuple("detection", new Value("move"), new Value(4), new Value(1), new Value(4))
        };
    
    public MovementSimulator(String agentId, Vector tCenters, TracePanel tp) throws TucsonInvalidAgentIdException {
        super(agentId);
        this.tCenters = tCenters;
        this.tp = tp;
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
        /* This simulator can send 3 type of detection_req:
         * - entry: the person is walking into the apartment
         * - move: the person is walking into the apartment, through the rooms
         * - exit: the person is walking out of the apartment
         *
         * This example is the path from the threshold to the study,
         * through the kitchen and the dining room (user: cla)
         */
        LogicTuple tuple_out;
        ITucsonOperation op_out;
        String res;
        
        EnhancedSynchACC acc = this.getContext();
        tp.appendText("Movement Simulator started!");
        this.say("User: cla (id: 1)");
        
        try {
            Thread.sleep(15000);
            // detection(typle_move, sensorID, personID, roomID) [sensorID === roomID]
            // Detection entry into the threshold
            /*tuple_out = new LogicTuple("detection", new Value("entry"),
                        new Value(1), //sensor
                        new Value(1), //person
                        new Value(1)); //room);
            op_out = acc.out((TucsonTupleCentreId) tCenters.get(0), tuple_out, null);
            if(op_out.isResultSuccess())
                this.say("Success out: detection(\"entry\",1,1,1) into ingresso_tc");
            else
                this.say("Failed out: detection(\"entry\",1,1,1) into ingresso_tc");*/
            
            for(int i=1; i<5; i++) {
                Thread.sleep(15000);
                this.say("Tuple " + i + ": " + movements[i-1].toString());
                this.say("*** Infos - tID: " + (TucsonTupleCentreId) tCenters.get(i-1) + ", tuple: " + movements[i-1].toString());
                op_out = acc.out((TucsonTupleCentreId) tCenters.get(i-1), movements[i-1], null);
                
                if(op_out.isResultSuccess())
                    res = "Success";
                else
                    res = "Failed";
                    
                this.say(res + " out: " + movements[i-1].toString() + " into " + ((TucsonTupleCentreId) tCenters.get(i-1)).getName());
            }
            
            this.say("Tuples sent, I'm done! bye!");
            acc.exit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
