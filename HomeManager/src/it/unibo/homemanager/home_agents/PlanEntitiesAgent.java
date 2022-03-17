/*
 * PlanEntitiesAgent.java
 *
 * Created on 26 ottobre 2006, 10.32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents;

import it.unibo.homemanager.home_agents.elab_plan.PrefControllerAgent;
import alice.logictuple.*;
import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.home_agents.exec_plan.PlanDistributorAgent;
import it.unibo.homemanager.home_agents.elab_plan.*;

import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author admin
 */
public class PlanEntitiesAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room = null;
    private TucsonTupleCentreId casa;
    private EnhancedSynchACC acc;
    private ServiceFactory sf;
    
    /**
     * Creates a new instance of PlanEntitiesAgent
     */
    public PlanEntitiesAgent(String id) throws TucsonInvalidAgentIdException {
        super(id);
    }
    
    public PlanEntitiesAgent(String id,TracePanel tp, TucsonTupleCentreId tid, TucsonTupleCentreId casa_tc, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        myTid = tid;
        this.casa = casa_tc;
	this.sf = sf;
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
        tp.appendText("-- PLAN ENTITIES AGENT STARTED."+ myTid);
        acc = this.getContext();
        
        try {
            while(true) {
                LogicTuple newPlanTemplate = new LogicTuple("new_plan",new Var("X"));
                ITucsonOperation op_in = acc.in(myTid, newPlanTemplate, Long.MAX_VALUE);
                LogicTuple tuple = op_in.getLogicTupleResult();
                tp.appendText("** ho ricevuto "+tuple+" **");
                room = tuple.getArg(0);
                // launch entities related to preferences and commands
                PrefControllerAgent pca = new PrefControllerAgent("pref_controller", myTid,room, tp, sf);
                CmdControllerAgent cca = new CmdControllerAgent("cmd_controller", myTid, room, tp, sf);
                ActControllerAgent aca = new ActControllerAgent("act_controller", myTid, room, tp);
                pca.go();
                cca.go();
                aca.go();
                // wait for retrieving ready_all_data tuple
                System.err.println("PlanEntitiesAgent waiting for the data..");
                op_in = acc.in(myTid, new LogicTuple("ready_all_data",new Var("X")), Long.MAX_VALUE);
                tuple = op_in.getLogicTupleResult();
                System.err.println("PlanEntitiesAgent found all the required data!");
                tp.appendText("PlanEntitiesAgent: "+tuple);
                // launch ConflictsManagerAgent
                ConflictsManagerAgent cma = new ConflictsManagerAgent("conflicts_manager", myTid, room, this.casa, tp, sf);
                cma.go();
                // wait for ConflictsManagerAgent to end its activity
                op_in = acc.in(myTid,new LogicTuple("conflicts_end_act",room), Long.MAX_VALUE);
                tuple = op_in.getLogicTupleResult();
                // launch PlanDistributorAgent
                PlanDistributorAgent pda = new PlanDistributorAgent("plan_distributor", this.myTid,room,tp);
                pda.go();
                // wait for PlanDistributorAgent to end its activity
                op_in = acc.in(myTid,new LogicTuple("distributor_end_act",room), Long.MAX_VALUE);
                tuple = op_in.getLogicTupleResult();
                tp.appendText("PlanEntitiesAgent: AGENTS LAUNCHED!");
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("PlanEntitiesAgent: problems in entities activation.");
        }
    }
}