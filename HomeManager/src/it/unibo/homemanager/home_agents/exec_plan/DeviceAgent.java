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
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.ServicesInterfaces.WasherProgramServiceInterface;
import it.unibo.homemanager.tablemap.WasherProgram;
import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sik
 */
public class DeviceAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId home_room;
    private TracePanel tp;
    private Device dev;
    private String state;
    private ServiceFactory sf;
    private Database database;
    
    public DeviceAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Device d, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.home_room = tid;
        this.dev = d;
        this.state = "off";
	this.sf = sf;
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(DeviceAgent.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public DeviceAgent(String id, TracePanel tp, TucsonTupleCentreId tid, Device d, String state, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.tp = tp;
        this.home_room = tid;
        this.dev = d;
        this.state = state;
	this.sf = sf;
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(DeviceAgent.class.getName()).log(Level.SEVERE, null, ex);
	}
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
        tp.appendText("-- DEVICE AGENT FOR "+dev.getDeviceName()+" STARTED.");
        EnhancedSynchACC acc = this.getContext();
        
        try {
            while(true) {
                System.out.println("Dev "+dev.getDeviceId()+": INSIDE WHILE.");
                // wait for command
                ITucsonOperation op_in = acc.in(home_room, new LogicTuple("plan_cmd",new Value(dev.getDeviceId()),new Var("X")), Long.MAX_VALUE);
                LogicTuple cmd = op_in.getLogicTupleResult();
                String st = cmd.getArg(1).toString();
//                System.out.println("DeviceAgent "+dev.name+": "+cmd+
//                        ", st = "+st+" / state = "+state+" / dev.type = "+dev.type);
                // execute command (only if state is changed)
                if(state.compareTo(st) != 0) {
                    // if device is a washer, then we have to consider also program duration
                    if(dev.getDeviceType() == "W") {
                        // if command is not "off" (i.e. this is a program)
                        if(!st.equals("off")) {
                            // if current state is not "off" (i.e. this is another program):
                            // this new program is pending
                            if(state.compareTo("off") != 0) {
                                acc.out(home_room,new LogicTuple("pending",new Value(dev.getDeviceId()),
                                        cmd.getArg(1),new Value(dev.getDeviceEnergy()),
                                        new Value(getDuration(st)),new Value(dev.getDeviceRoomId())), null);
                                System.out.println("DeviceAgent "+dev.getDeviceName()+": pending "+st);
                            }
                            // if currently device is turned off, start the washer program
                            else {
                                this.state = st;
                                // notify current state after command execution
                                acc.out(home_room,new LogicTuple("upd_dev_curr_st",new Value(dev.getDeviceId()),
                                        new Value(state)), null);
                                checkWasherProgram(state, acc);
                                tp.appendText("DeviceAgent "+dev.getDeviceName()+": CURRENT STATE "+state);
                            }
                        }
                        // if command is "off" (so currently washer is doing a program)
                        else {
                            // interrupt current program
                            System.out.println("DeviceAgent "+dev.getDeviceName()+": finished "+state);
                            this.state = st;
                            // notify current state after command execution
                            acc.out(home_room,new LogicTuple("upd_dev_curr_st",new Value(dev.getDeviceId()),
                                    new Value(state)), null);
                            tp.appendText("DeviceAgent "+dev.getDeviceName()+": CURRENT STATE "+state);
                        }
                    }
                    else {
                        this.state = st;
                        // notify current state after command execution
                        acc.out(home_room,new LogicTuple("upd_dev_curr_st",new Value(dev.getDeviceId()),
                                new Value(state)), null);
                        tp.appendText("DeviceAgent "+dev.getDeviceName()+": CURRENT STATE "+state);
                    }
                }
                else System.err.println("DeviceAgent "+dev.getDeviceName()+": ALREADY "+state);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("DeviceAgent "+dev.getDeviceName()+": error in device activity.");
        }
    }
    
    private void checkWasherProgram(String st, EnhancedSynchACC acc) {
        try {
            WasherProgramServiceInterface ws = sf.getWasherProgramServiceInterface();
            WasherProgram wp = ws.getWasherProgram(database.getDatabase(),
                    st,dev.getDeviceId());
            acc.out(home_room,new LogicTuple("dev_curr_act",new Value(dev.getDeviceId()),
                    new Value(wp.duration),new Value(dev.getDeviceEnergy()),new Value(dev.getDeviceRoomId())), null);
            tp.appendText("checkWasherProgram: start program "+st+" of device" +
                    dev.getDeviceName()+" for "+(wp.duration/1000)+" seconds.");
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private int getDuration(String st) {
        int duration = 0;
        try {
	    WasherProgramServiceInterface ws = sf.getWasherProgramServiceInterface();
            WasherProgram wp = ws.getWasherProgram(database, st, dev.getDeviceId());
            duration = wp.duration;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return duration;
    }
   
}
