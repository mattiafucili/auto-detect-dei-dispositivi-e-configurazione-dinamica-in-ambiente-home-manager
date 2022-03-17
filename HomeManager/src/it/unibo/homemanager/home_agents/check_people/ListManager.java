/*
 * ListManager.java
 *
 * Created on 12 ottobre 2006, 16.21
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
import it.unibo.homemanager.userinterfaces.GeneralFrame;
import it.unibo.homemanager.userinterfaces.ApartmentPanel;
import it.unibo.homemanager.util.*;

/**
 *
 * @author admin
 */
public class ListManager extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private ApartmentPanel ap;
    
    /** Creates a new instance of ListManager */
    public ListManager(String aId, TucsonTupleCentreId tid, TracePanel tp, ApartmentPanel ap) throws TucsonInvalidAgentIdException {
        super(aId);
        this.myTid = tid;
        this.tp = tp;
        this.ap = ap;
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
        tp.appendText("-- LIST MANAGER "+myTid.getName()+" STARTED.");
        EnhancedSynchACC acc = this.getContext();
        
        try {
            while(true) {
                ITucsonOperation op_in = acc.in(myTid,
                        new LogicTuple("detect_new_move",new Var("X"), // type of move
                        new Var("Y"), // sensor
                        new Var("Z"), // person
                        new Var("W")),// room
                        Long.MAX_VALUE);
                LogicTuple detection = op_in.getLogicTupleResult();
                tp.appendText("ListManager: "+detection);
                // insert new tuple only if type of move is not exit
                // (otherwise system removes tuple automatically)
                LogicTuple tuple;
                if(!detection.getArg(0).toString().equals("exit")) {
                    tuple = new LogicTuple("new_checkPeople", detection.getArg(1),detection.getArg(2),detection.getArg(3));
                    acc.out(myTid, tuple, null);
                    
                    ap.refreshSituation();
                    tp.appendText("ListManager: list and situation UPDATED with "+tuple);
                }
                else {
                    tp.appendText("ListManager: list NOT UPDATED because of type of move");
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("ListManager: error in managing list.");
        }
    }
}
