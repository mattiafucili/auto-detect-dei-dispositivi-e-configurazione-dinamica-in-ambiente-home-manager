/*
 * CmdControllerAgent.java
 *
 * Created on 17 ottobre 2006, 15.41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.elab_plan;

import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.*;
import it.unibo.homemanager.home_agents.PlanTupleBuilder;

import java.util.Vector;
import java.util.List;

import it.unibo.homemanager.userinterfaces.TracePanel;

/**
 *
 * @author admin
 *
 * This agent takes all the commands sent by users and collects them in order to
 * choose which will be executed in next plan.
 */
public class CmdControllerAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    private EnhancedSynchACC acc;
    private ServiceFactory sf;
    
    /**
     * Creates a new instance of CmdControllerAgent
     */
    public CmdControllerAgent(String id, TucsonTupleCentreId tid, TupleArgument r, TracePanel tp, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.myTid = tid;
        room = r;
        this.tp = tp;
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
        tp.appendText("-- CMD CONTROLLER AGENT STARTED.");
        acc = this.getContext();
        
        try {
            System.err.println("[CMDControllerAgent] Retrieving commands..");
            // take user commands regarding the indicated room
            Vector cmds = retrieveCmds();
            System.out.println("CmdControllerAgent: cmds collected.");
            // load cmds in the tuple centre
            for(int i=0;i<cmds.size();i++) {
                acc.out(myTid,(LogicTuple)cmds.elementAt(i),null);
                tp.appendText("CmdControllerAgent: TUPLE SENT!");
            }
            // notify operation execution
            LogicTuple data = new LogicTuple("data_collected",room);
            acc.out(myTid,data,null);
            tp.appendText("CmdControllerAgent: "+data);
            ITucsonOperation op_in = acc.in(myTid,new LogicTuple("ready_new",room),Long.MAX_VALUE);
            data = op_in.getLogicTupleResult();
            tp.appendText("CmdControllerAgent: "+data);
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("CmdControllerAgent: error in retrieving commands.");
        }
    }
    
    /**
     * Finds commands regarding the indicated room.
     */
    private Vector retrieveCmds() {
        Vector cmds = new Vector();
        try {
            /*LogicTuple listTuples = inp(myTid,
                    new LogicTuple("in_all",new Value("send_cmd",new Var("W"), // user id
                            room, // room
                            new Var("Y"), // device
                            new Var("Z")), // command
                            new Var("X")));
            System.err.println("retrieveCmds: "+listTuples);
            List l = listTuples.getArg(1).toList();*/
            LogicTuple inAllTemplate = new LogicTuple("send_cmd",
                            new Var("W"), // user id
                            room,         // room
                            new Var("Y"), // device
                            new Var("Z")); // command
            ITucsonOperation op_inAll = acc.inAll(myTid, inAllTemplate, Long.MAX_VALUE);
            List l = op_inAll.getLogicTupleListResult();
            System.err.println("[RetrieveCmds@CmdController] Found " + l.size() + " tuples into " + myTid.getName());
            for(int i=0;i<l.size();i++) {
                System.err.println("[CmdControllerAgent] retrieveCmds("+i+"): " + (LogicTuple)l.get(i));
                if(verify_command(LogicTuple.parse(l.get(i).toString()))){
                    LogicTuple t = PlanTupleBuilder.getCmdTuple(LogicTuple.parse(l.get(i).toString()));
                    cmds.add(t);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return cmds;
    }

    private boolean verify_command(LogicTuple parse) {
        boolean result = false;
        try{
            Device dev = sf.getDeviceServiceInterface().getDeviceById(sf.getDatabaseInterface().getDatabase(), parse.getArg(2).intValue());
            LogicTuple outTemplate = new LogicTuple("device_req", new Value(this.myName()), parse.getArg(0), new Value(dev.getDeviceName().substring(1, dev.getDeviceName().length()-1)));
            acc.out(this.myTid, outTemplate, null);
            LogicTuple inTemplate = new LogicTuple("device_resp", new Value(this.myName()), parse.getArg(0), new Value(dev.getDeviceName().substring(1, dev.getDeviceName().length()-1)), new Var("X"));
            ITucsonOperation op_in = acc.in(myTid, inTemplate, Long.MAX_VALUE);
            LogicTuple response = op_in.getLogicTupleResult();
            if(response.getArg(3).intValue() == 1)
                result = true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}
