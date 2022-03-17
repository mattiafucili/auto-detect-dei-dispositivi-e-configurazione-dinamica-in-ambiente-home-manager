/*
 * ActControllerAgent.java
 *
 * Created on 19 novembre 2006, 16.01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.elab_plan;

import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

import java.util.Vector;
import java.util.List;

import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author admin
 */
public class ActControllerAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    private EnhancedSynchACC acc;
    
    /** Creates a new instance of ActControllerAgent */
    public ActControllerAgent(String id, TucsonTupleCentreId tid, TupleArgument r, TracePanel tp) throws TucsonInvalidAgentIdException {
        super(id);
        this.myTid = tid;
        room = r;
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
        tp.appendText("-- ACT CONTROLLER AGENT STARTED.");
        acc = this.getContext();
        
        try {
            Vector acts = retrieveActs();
            System.out.println("ActControllerAgent: acts collected.");
            // load cmds in the tuple centre
            System.err.println("ActControllerAgent: acts collected!");
            for(int i=0;i<acts.size();i++) {
                System.err.println("Act " + i + ": " + (LogicTuple)acts.elementAt(i));
                acc.out(myTid,(LogicTuple)acts.elementAt(i), null);
                tp.appendText("ActControllerAgent: TUPLE SENT!");
            }
            // notify operation execution
            LogicTuple data = new LogicTuple("data_collected",room);
            acc.out(myTid,data, null);
            tp.appendText("ActControllerAgent: "+data);
            ITucsonOperation op_in = acc.in(myTid,new LogicTuple("ready_new",room),Long.MAX_VALUE);
            data = op_in.getLogicTupleResult();
            tp.appendText("ActControllerAgent: "+data);
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Vector retrieveActs() {
        Vector acts = new Vector();
        try {
            /*ITucsonOperation op_inp = acc.inp(myTid,
                    new LogicTuple("in_all",new Value("pending",
                            new Var("X"), // device
                            new Var("Y"), // command
                            new Var("W"), // energy consumption
                            new Var("Z"), // program duration
                            new Var("J")), // room
                    new Var("L")),Long.MAX_VALUE);*/
            //System.err.println("retrieveActs: "+listTuples);
            //List l = listTuples.getArg(1).toList();
            LogicTuple inAllTemplate = new LogicTuple("pending",
                            new Var("X"), // device
                            new Var("Y"), // command
                            new Var("W"), // energy consumption
                            new Var("Z"), // program duration
                            new Var("J")); // room
            ITucsonOperation op_inAll = acc.inAll(myTid, inAllTemplate, Long.MAX_VALUE);
            List l = op_inAll.getLogicTupleListResult();
            
            for(int i=0;i<l.size();i++) {
                System.err.println("[ActControllerAgent] list l("+i+"): " + (LogicTuple)l.get(i));
                LogicTuple t = LogicTuple.parse(l.get(i).toString());
                t = new LogicTuple("send_act",t.getArg(0),t.getArg(1),t.getArg(2),t.getArg(3));
                acts.add(t);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return acts;
    }
}
