/*
 * DetectorAgent.java
 *
 * Created on 12 ottobre 2006, 10.44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.check_people;

import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.tablemap.*;

/**
 *
 * @author admin
 */
public class DetectorAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private Sensor sens;
    
    /**
     * Creates a new instance of DetectorAgent
     */
    public DetectorAgent(String id) throws TucsonInvalidAgentIdException {
        super(id);
    }
    
    public DetectorAgent(String id,TracePanel tp,TucsonTupleCentreId tid,Sensor s) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        myTid = tid;
        sens = s;
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
        tp.appendText("-- DETECTOR "+sens.name+" STARTED.");
        EnhancedSynchACC acc = this.getContext();
        
        try {
            while(true) {
                ITucsonOperation op_in = acc.in(myTid,
                        new LogicTuple("detection",new Var("X"), // type of move
                        new Value(sens.idSens), // sensor
                        new Var("Y"), // person
                        new Var("Z")),// room
                        Long.MAX_VALUE);
                LogicTuple detection = op_in.getLogicTupleResult();
                tp.appendText("Detector "+sens.name+": "+detection);
                tp.appendText("Mio centro di tuple:"+this.myTid.getName());
                // check list
                ITucsonOperation op_rdp = acc.rdp(myTid,
                        new LogicTuple("list_checkPeople",detection.getArg(1), // sensor
                        detection.getArg(2), // person
                        detection.getArg(3)),// room
                        null);
                
                boolean checkList_res = op_rdp.isResultSuccess();
               // list is updated:
                    // if it's not an exit notification and there are no tuples about the user
                    // or if it's an exit notification and there are still tuples about the user
                // THERE ARE TUPLES
                if(checkList_res) {
                     // it's not an exit notification and there is the tuple about the user
                     if(!detection.getArg(0).toString().equals("exit"))
                         tp.appendText("List ALREADY UPDATED according to detection.--1--");
                     // it's an exit notification and there are still tuples about the user
                     else
                        acc.out(myTid,new LogicTuple("detect_new_move",
                                detection.getArg(0),new Value(sens.idSens),
                                detection.getArg(2),detection.getArg(3)), null);
                }
                // THERE ARE NO TUPLES
                else {
                    if(!detection.getArg(0).toString().equals("exit"))
                        // if it's not an exit notification and
                        // list is not updated, then notify System about detection
                        acc.out(myTid,new LogicTuple("detect_new_move",
                                detection.getArg(0),new Value(sens.idSens),
                                detection.getArg(2),detection.getArg(3)),null);
                    // if it's an exit notification and there are no tuples about
                    // the user, then list is updated
                    else tp.appendText("List ALREADY UPDATED according to detection.--2--");
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("Detector "+sens.name+": error in processing the request.");
        }
    }
    
}
