/*
 * PlanDistributorAgent.java
 *
 * Created on 26 ottobre 2006, 10.55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.exec_plan;

import alice.tucson.api.*;
import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.util.Command;
import it.unibo.homemanager.home_agents.PlanTupleBuilder;
import java.util.List;

/**
 *
 * @author admin
 */
public class PlanDistributorAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    
    /**
     * Creates a new instance of PlanDistributorAgent
     */
    public PlanDistributorAgent(String id, TucsonTupleCentreId tid,TupleArgument r, TracePanel tp) throws TucsonInvalidAgentIdException {
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
        tp.appendText("-- PLAN DISTRIBUTOR AGENT STARTED.");
        EnhancedSynchACC acc = this.getContext();
        
        try {
            // receiving and setting temperature
            ITucsonOperation op_in = acc.inp(myTid,new LogicTuple("new_temp",room,new Var("Y")), Long.MAX_VALUE);
            LogicTuple tuple = op_in.getLogicTupleResult();
            if(tuple != null) {
                System.err.println("PlanDistributor: received temp - "+tuple);
                acc.out(myTid, new LogicTuple("update_temp",room,tuple.getArg(1)), null);
            }
            // receiving and distributing commands
            sendCmds(getCmds(acc), acc);
            System.err.println("PlanDistributor: received and sent cmds");
            acc.out(myTid, new LogicTuple("distributor_end_act",room), null);
            tp.appendText("** PLAN DISTRIBUTOR AGENT FINISHED.");
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("PlanDistributorAgent: problems in plan distribution.");
        }
    }
    
    private List getCmds(EnhancedSynchACC acc) {
        LogicTuple cmd = null;
        List l = null;
        LogicTuple planCmdsTemplate = null;
        try {
            planCmdsTemplate = new LogicTuple("plan_cmds", new Var("X"),new Var("Y"));
        } catch (InvalidVarNameException e) {
            // cannot happen
        }
        try {
           ITucsonOperation op_inAll = acc.inAll(myTid, planCmdsTemplate, null);
                   //new LogicTuple("in_all",new Value("plan_cmds",
                   //     new Var("X"),new Var("Y")),new Var("W")), Long.MAX_VALUE);
           l = op_inAll.getLogicTupleListResult();
           System.out.println("Found " + l.size() + " commands!");
           for(int i=0; i<l.size(); i++)
                System.out.println("Command " + (i+1) + ": " + (LogicTuple)l.get(i));
           //l = cmd.getArg(1).toList();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return l;
    }
    
    private void sendCmds(List cmds, EnhancedSynchACC acc) {
        try {
            for(int i=0;i<cmds.size();i++) {
                LogicTuple lt = LogicTuple.parse(cmds.get(i).toString());
                System.err.println("[SendCmds@PlanDistributor] lt: " + lt);
                Command c = Command.getCommandForDistributor(lt);
                acc.out(myTid,PlanTupleBuilder.getPlanCmdTuple(c), null);
                System.out.println("sendCmds, cmd nr "+i+": "+c.toString());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
