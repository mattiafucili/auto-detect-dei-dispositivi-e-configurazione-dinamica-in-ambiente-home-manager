/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.elab_plan;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.dbexceptions.NotFoundDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.ResultSetDbException;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.home_agents.PlanTupleBuilder;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.UserServiceInterface;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sik
 */
public class PrefControllerAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    private Device device;
    private ServiceFactory sf;
    private UserServiceInterface userService;
    private Database database;
    private DeviceServiceInterface deviceService;
    private EnhancedSynchACC acc;
    
    public PrefControllerAgent(String id, TucsonTupleCentreId myTid, TupleArgument room, TracePanel tp, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.myTid = myTid;
        this.room = room;
        this.tp = tp;
	this.sf = sf;
	this.userService = sf.getUserServiceInterface();
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(PrefControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
	}
	this.deviceService = sf.getDeviceServiceInterface();
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
        tp.appendText("-- PREF CONTROLLER AGENT STARTED.");
        acc = this.getContext();
        
        try {
            // collect preferences
            Vector prefTuples = retrievePrefs();
            // load preferences in the tuple centre
            for(int i=0;i<prefTuples.size();i++) {
                acc.out(myTid,(LogicTuple)prefTuples.elementAt(i),null);
                tp.appendText("PrefControllerAgent: TUPLE " + (LogicTuple)prefTuples.elementAt(i) + " SENT to " + myTid.getName() + "!");
            }
            // notify operation execution
            LogicTuple data = new LogicTuple("data_collected",room);
            acc.out(myTid,data,null);
            tp.appendText("PrefControllerAgent: "+data);
            ITucsonOperation op_in = acc.in(myTid,new LogicTuple("ready_new",room), Long.MAX_VALUE);
            data = op_in.getLogicTupleResult();
            tp.appendText("PrefControllerAgent: "+data);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            tp.appendText("PrefControllerAgent: error in retrieving preferences.");
        }
    }
    
    private Vector retrievePrefs() {
        Vector prefs = new Vector();
        // find users currently in room
        Vector users = retrieveUsers();
        // detect preferences
        LogicTuple lt;
        for(int i=0;i<users.size();i++){
            lt = buildPrefTuple((User)users.elementAt(i));
            prefs.add(lt);
            System.err.println("[retrievePrefs@PrefController] lt: " + lt);
        }
        return prefs;
    }
    
    private LogicTuple buildPrefTuple(User u) {
        LogicTuple lt = PlanTupleBuilder.getPrefTuple(u);
        LogicTuple lt1 = verify_permissions(lt,u);
        return lt1;
    }
    
    /**
     * Finds users in the room a new user has entered.
     */
    private Vector retrieveUsers() {
        Vector users = new Vector();
        try {
            LogicTuple userListTemplate = new LogicTuple("list_checkPeople",new Var("Y"), new Var("Z"),room,new Var("W"));
            ITucsonOperation op_rdAll = acc.rdAll(myTid, userListTemplate, Long.MAX_VALUE);
            List l = op_rdAll.getLogicTupleListResult();
            //List l = t.getArg(1).toList();
            for(int i=0;i<l.size();i++) {
                LogicTuple tt = LogicTuple.parse(l.get(i).toString());
                int idUser = tt.getArg(1).intValue();
                System.err.println("[PrefControllerAgent] retrieveUsers: "+l.get(i)+" || ID: " + idUser);
                if(idUser > 0) { // it's a user
                    User u = userService.getUserById(database.getDatabase(),idUser);
                    users.add(u);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }
    
    private Vector getAllUsers() {
        Vector users = new Vector();
        try {
            users = userService.getUsers(database.getDatabase());
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }

    private LogicTuple verify_permissions(LogicTuple lt, User u) {
        LogicTuple preferences=null;
        String pref="";
	System.err.println("[verify_permissions@PCA]lt: " + lt);
        try {
	    String readPrefs = lt.getArg(3).getName();
	    System.err.println("readPrefs: " + readPrefs);
	    if(readPrefs.contains("`")) {
		readPrefs = readPrefs.substring(1, readPrefs.length()-1);
		System.err.println("readPrefs: " + readPrefs);
	    }
            StringTokenizer st = new StringTokenizer(readPrefs, ";");//lt.getArg(3).getName(), ";");
            String[] prefs = new String[st.countTokens()];
            int i=0;
            while(st.hasMoreElements()){
                prefs[i] = st.nextToken();
                System.err.println("prefs["+i+"]:" + prefs[i]);
                StringTokenizer st2 = new StringTokenizer(prefs[i], "-");
		int roomPrefs = Integer.parseInt(st2.nextToken().substring(1));
		int devPrefs = Integer.parseInt(st2.nextToken());
                if(this.room.intValue() == roomPrefs) {//Integer.parseInt(st2.nextToken())){
                    System.err.println("Sono entrato nell'if con la preferenza " + i + ": " + prefs[i]);
		    try {
                        device = deviceService.getDeviceById(database.getDatabase(), devPrefs);//Integer.parseInt(st2.nextToken()));
			System.err.println("device found!!\n" + device);
                    } catch (NotFoundDbException | ResultSetDbException ex) {
                        Logger.getLogger(PrefControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(verify_role_dev(u, device.getDeviceName())){
                        pref += prefs[i];

                        if(i<prefs.length-1)
                            pref += ",";

                    }
                }
                i++;
            }
            System.out.println("Preferenze ammesse per l'utente " + u.username + ": "+ pref);
        } catch (Exception ex) {
            Logger.getLogger(PrefControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if(pref.equals("")){
                pref = this.room.intValue()+"- ";
            }
            preferences = new LogicTuple("user_pref", lt.getArg(0), lt.getArg(1), lt.getArg(2), new Value(pref));
        } catch (InvalidOperationException ex) {
            Logger.getLogger(PrefControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preferences;
    }

    private boolean verify_role_dev(User u, String nextToken) {
        boolean res = false;
        LogicTuple response;
        try {
            LogicTuple deviceReqTemplate = new LogicTuple("device_req", new Value(this.myName()),new Value(u.idUser), new Value(nextToken));
            acc.out(this.myTid, deviceReqTemplate, null);
            LogicTuple deviceRespTemplate = new LogicTuple("device_resp", new Value(this.myName()), new Value(u.idUser), new Value(nextToken), new Var("X"));
            ITucsonOperation op_in = acc.in(myTid, deviceRespTemplate, Long.MAX_VALUE);
            response = op_in.getLogicTupleResult();
                
            if(response.getArg(3).intValue() == 1)
                res = true;
        } catch (Exception ex) {
            Logger.getLogger(PrefControllerAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}