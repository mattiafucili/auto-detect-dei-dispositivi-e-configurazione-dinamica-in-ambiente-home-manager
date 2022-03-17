/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.RBAC.DevsAccessManAgent;
import it.unibo.homemanager.RBAC.GestoreAttivazioneRuolo;
import it.unibo.homemanager.RBAC.GestoreDisattivazioneRuolo;
import it.unibo.homemanager.RBAC.RoomAccessManAgent;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.AgentManager;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.home_agents.PlanEntitiesAgent;
import it.unibo.homemanager.home_agents.check_people.DetectorAgent;
import it.unibo.homemanager.home_agents.check_people.ListManager;
import it.unibo.homemanager.home_agents.exec_plan.BlindAgent;
import it.unibo.homemanager.home_agents.exec_plan.BrightnessAgent;
import it.unibo.homemanager.home_agents.exec_plan.DeviceAgent;
import it.unibo.homemanager.home_agents.exec_plan.LampAgent;
import it.unibo.homemanager.home_agents.exec_plan.WindowAgent;
import it.unibo.homemanager.meteo.MeteoAgent;
import it.unibo.homemanager.tablemap.Blind;
import it.unibo.homemanager.tablemap.Lamp;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.Sensor;
import it.unibo.homemanager.tablemap.Window;
import it.unibo.homemanager.tablemap.ServicesInterfaces.BlindServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.LampServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.SensorServiceInterface;
import it.unibo.homemanager.tablemap.ServicesInterfaces.WindowServiceInterface;
import it.unibo.homemanager.userinterfaces.ApartmentPanel;
import it.unibo.homemanager.userinterfaces.TracePanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.utilities.Utils;

/**
 *
 * @author sik
 */
public class Initializer {
    
    private ServiceFactory sf;
    private Database database;
    
    public Initializer(ServiceFactory sf) {
	this.sf = sf;
	try {
	    this.database = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    public void initializeReSpecT(Vector tupleCenters, TucsonTupleCentreId casa_tc, TucsonTupleCentreId rbac_tc) {
        System.out.println("Injecting ReSpecT specifications..");
        setReSpecT(tupleCenters, casa_tc, rbac_tc);
    }
    
    public void initializeRBAC(TucsonTupleCentreId rbac) {
        System.out.println("Reading the content from file..");
        List<LogicTuple> tuples = readTuplesFromFile("snapshot_rbac_tc.dat");
        System.out.println("Loading tuples into RBAC_tc..");
        insertTupleListInto(rbac, tuples);
    }
    
    private List<LogicTuple> readTuplesFromFile(String fileName) {
        List<LogicTuple> list = new ArrayList<>();
        
        try {
            File dir = new File(".");
	    File fin = new File(dir.getCanonicalPath() + File.separator + "data" + File.separator + fileName);
            //File fin = new File(dir.getCanonicalPath() + File.separator + "data" + File.separator + "snapshot_rbac_tc.dat");
            //System.out.println("dir: " + dir.getCanonicalPath() + File.separator + "data" + File.separator + "snapshot_rbac_tc.dat");
            FileInputStream fis = new FileInputStream(fin);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            
            while ((line = br.readLine()) != null) {
                try {
                    list.add(LogicTuple.parse(line));
                } catch (InvalidLogicTupleException ex) {
                    ex.printStackTrace();
                }
            }

            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public void initializeDatabaseTc() {
	System.out.println("Reading the content from file..");
        List<LogicTuple> tuples = readTuplesFromFile("snapshot_database_tc.dat");
        System.out.println("Loading tuples into tuple center db..");
	try {
	    insertTupleListInto(new TucsonTupleCentreId("db", "localhost", "20504"), tuples);
	} catch (TucsonInvalidTupleCentreIdException ex) {
	    Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    public void initializeCasaTc(TucsonTupleCentreId casa_tc) {
        System.out.println("Initializing temp mode..");
        initTempMode(casa_tc);
        System.out.println("Initializing prio factor..");
        initPrioFactor(casa_tc);
        System.out.println("Initializing max energy val..");
        initMaxEnergyVal(casa_tc);
        System.out.println("Initializing max energy cons");
        initMaxEnergyCons(casa_tc);
        System.out.println("Initializing indispensable devices..");
        initIndispensableDevice(casa_tc);
        System.out.println("Initializing external temp..");
        initExternalTemp(casa_tc);
        System.out.println("Initializing external brightness..");
        initExternalBrightness(casa_tc);
        System.out.println("Initializing incompatible device..");
        initIncompatibleDevice(casa_tc);
        System.out.println("Initializing unnecessary device..");
        initUnnecessaryDevice(casa_tc);
    }
    
    private void initTempMode(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initTempMode");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updTempModeTemplate = new LogicTuple("upd_temp_mode",new Value("heat"));
            
            acc.out(casa_tc, updTempModeTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initPrioFactor(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initPrioFactor");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updPrioFactorTemplate = new LogicTuple("upd_prio_factor",new Value("energy"));
            
            acc.out(casa_tc, updPrioFactorTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initMaxEnergyVal(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initMaxEnergyVal");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updPrioFactorTemplate = new LogicTuple("upd_max_energy",new Value(1000));
            
            acc.out(casa_tc, updPrioFactorTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initMaxEnergyCons(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initMaxEnergyCons");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updPrioFactorTemplate = new LogicTuple("total_energy_cons",new Value(100));
            
            acc.out(casa_tc, updPrioFactorTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initIndispensableDevice(TucsonTupleCentreId casa_tc) {
       try {
            TucsonAgentId agent = new TucsonAgentId("initIndispensableDevice");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            LogicTuple indispensableDeviceTemplate = new LogicTuple("indispensable_device",new Value(7));
            acc.out(casa_tc, indispensableDeviceTemplate, null);
            indispensableDeviceTemplate = new LogicTuple("indispensable_device",new Value(6));
            acc.out(casa_tc, indispensableDeviceTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initExternalTemp(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initExternalTemp");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple externalTempTemplate = new LogicTuple("external_temp",new Value(30));
            
            acc.out(casa_tc, externalTempTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initExternalBrightness(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initExternalBrightness");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple externalBrightnessTemplate = new LogicTuple("external_brightness",new Value(80));
            
            acc.out(casa_tc, externalBrightnessTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initIncompatibleDevice(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initIncompatibleDevice");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            LogicTuple incompatibleDeviceTemplate = new LogicTuple("incompatible_device",new Value(1));
            acc.out(casa_tc, incompatibleDeviceTemplate, null);
            incompatibleDeviceTemplate = new LogicTuple("incompatible_device",new Value(2));
            acc.out(casa_tc, incompatibleDeviceTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initUnnecessaryDevice(TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("initUnecessaryDevice");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            LogicTuple unnecessaryDeviceTemplate = new LogicTuple("unnecessary_device",new Value(1));
            acc.out(casa_tc, unnecessaryDeviceTemplate, null);
            unnecessaryDeviceTemplate = new LogicTuple("unnecessary_device",new Value(4));
            acc.out(casa_tc, unnecessaryDeviceTemplate, null);
            unnecessaryDeviceTemplate = new LogicTuple("unnecessary_device",new Value(3));
            acc.out(casa_tc, unnecessaryDeviceTemplate, null);
            unnecessaryDeviceTemplate = new LogicTuple("unnecessary_device",new Value(5));
            acc.out(casa_tc, unnecessaryDeviceTemplate, null);
            unnecessaryDeviceTemplate = new LogicTuple("unnecessary_device",new Value(2));
            acc.out(casa_tc, unnecessaryDeviceTemplate, null);
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean initializeData(Vector tupleCenters, TucsonTupleCentreId casa_tc, TucsonTupleCentreId rbac_tc, TracePanel tracePanel, ApartmentPanel apPanel) {
        boolean res = true;
        MovementSimulator sim;
        
        try {
            System.out.println("Populating the tuple centers..");
            initUpdBarrierSize(tupleCenters, 3);
            initRoomsStates(tupleCenters);
            initMaxPrio(tupleCenters);
            initRoomsBrightness(tupleCenters);
            Vector rooms = getRooms();
            initReadyData(tupleCenters, rooms);
            initTemp(tupleCenters, rooms);
            initEnergy(tupleCenters, rooms, casa_tc);
            System.out.println("Tuple centers populated!");
            System.out.println("Initializing GestoreAttivazioneRuolo..");
            GestoreAttivazioneRuolo gar = new GestoreAttivazioneRuolo("gestoreAttivazioneRuolo", tracePanel, rbac_tc, casa_tc);
            gar.go();
            System.out.println("Initializing GestoreDisattivazioneRuolo..");
            GestoreDisattivazioneRuolo gdr = new GestoreDisattivazioneRuolo("gestoreDisattivazioneRuolo", tracePanel, rbac_tc, casa_tc);
            gdr.go();
            System.out.println("Initializing PlanEntitiesAgents..");
            initPlanEntitiesAgents(tupleCenters, tracePanel, casa_tc);
            System.out.println("Initializing DevsAccessMan agents..");
            initDevsAccessManAgents(tupleCenters, tracePanel, rbac_tc);
            System.out.println("Initializing DetectorAgents..");
            initDetectorAgents(tupleCenters, tracePanel);
            System.out.println("Initializing RoomAccessManagerAgents..");
            initRoomAccessManagerAgents(tupleCenters, tracePanel, rbac_tc, casa_tc);
            System.out.println("Initializing ListManager agents..");
            initListManagerAgents(tupleCenters, tracePanel, apPanel);
            System.out.println("Initializing BrightnessAgents..");
            initBrightnessAgents(tupleCenters, tracePanel, casa_tc);
            //submitted by Alessandro Celi
            System.out.println("Initializing MeteoAgents..");
            //initMeteoAgents(tupleCenters, tracePanel, casa_tc); V1.0
            initAgent();
            //
            System.out.println("Initializing Blinds..");
            initBlinds(tupleCenters, tracePanel);
            System.out.println("Initializing Lamps..");
            initLamps(tupleCenters, tracePanel);
            System.out.println("Initializing Windows..");
            initWindows(tupleCenters, tracePanel);
            System.out.println("Initializing Devices..");
            initDevices(tupleCenters, tracePanel);
            //System.out.println("10 sec to initialize the movement simulator..");
            //Thread.sleep(10000);
            //System.out.println("Starting Movement Simulator..");
            //sim = new MovementSimulator("movementSimulatorAgent", tupleCenters, tracePanel);
            //sim.go();
        } catch (Exception ex) {
            res = false;
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    

	public void setReSpecT(Vector tuplecenters, TucsonTupleCentreId casa_tc, TucsonTupleCentreId rbac_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("setReSpecTAgent");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            String theory = Utils.fileToString("it/unibo/homemanager/theory_room.rsp");
            System.out.println("Writing ReSpecT into " + ((TucsonTupleCentreId) tuplecenters.get(2)).getName());
            
           
            for(int i=0; i<tuplecenters.size()-2; i++) {
           	System.out.println(tuplecenters.get(i).toString());
                ITucsonOperation op_set = acc.setS((TucsonTupleCentreId) tuplecenters.get(i), theory, null);
               // System.out.println(((TucsonTupleCentreId) tuplecenters.get(i)).getName() + " - Respect initialization: " + String.valueOf(op_set.isResultSuccess()));
            }
            
            
           

            theory = Utils.fileToString("it/unibo/homemanager/theory_casa.rsp");
            acc.setS(casa_tc, theory, null);
            
            theory = Utils.fileToString("it/unibo/homemanager/theory_rbac.rsp");
            acc.setS(rbac_tc, theory, null);
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void initWindows(Vector tupleCenters, TracePanel tracePanel) {
        WindowServiceInterface ws = sf.getWindowServiceInterface();
        try {
            Vector windows = ws.getWindows(database.getDatabase());
            TucsonAgentId agent = new TucsonAgentId("insertWindowsDataAgent");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0;i<windows.size();i++) {
                Window w = (Window)windows.elementAt(i);
                //System.err.println("initWindows - w: " + w.toString());
                WindowAgent wa = new WindowAgent("window_"+w.idL, tracePanel, (TucsonTupleCentreId) tupleCenters.elementAt(w.roomId-1),w);
                initWindow((TucsonTupleCentreId)tupleCenters.elementAt(w.roomId-1),w, acc);
                wa.go();
                Thread.sleep(250);
            }
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initWindow(TucsonTupleCentreId tid, Window w, EnhancedSynchACC acc) {
        try {
            LogicTuple init = new LogicTuple("upd_window_curr_st", new Value(w.roomId), new Value("close"));
            //System.out.println("Writing " + init + " into " + tid.getName());    
            ITucsonOperation op = acc.out(tid, init, null);
            if(op.isResultFailure())
                System.err.println("initWindow FAILED! window " + w.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initBlinds(Vector tupleCenters, TracePanel tracePanel) {
        BlindServiceInterface bs = sf.getBlindServiceInterface();
        try {
            Vector blinds = bs.getBlinds(database.getDatabase());
            System.out.println("Blinds: " + blinds.size());
            TucsonAgentId agent = new TucsonAgentId("insertBlindsDataAgent");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0;i<blinds.size();i++) {
                Blind b = (Blind)blinds.elementAt(i);
                BlindAgent ba = new BlindAgent("blind_"+b.idL,tracePanel,(TucsonTupleCentreId)tupleCenters.elementAt(b.roomId-1),b);
                initBlind((TucsonTupleCentreId)tupleCenters.elementAt(b.roomId-1), b, acc);
                ba.go();
                Thread.sleep(250);
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initBlind(TucsonTupleCentreId tid, Blind b, EnhancedSynchACC acc) {
        try {
            LogicTuple init = new LogicTuple("upd_blind_curr_st",new Value(b.roomId),new Value("down"));
            ITucsonOperation op = acc.out(tid, init, null);
            if(op.isResultFailure())
                System.err.println("initBind FAILED! blind " + b.toString());
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initBrightnessAgents(Vector tupleCenters, TracePanel tracePanel, TucsonTupleCentreId casa_tc) {
        SensorServiceInterface ss = sf.getSensorServiceInterface();
        try {
            Vector sensors = ss.getSensors(database.getDatabase());
            //System.err.println("sensors: " + sensors);
	    
            for(int i=0;i<sensors.size();i++) {
                Sensor s = (Sensor)sensors.elementAt(i);
                BrightnessAgent ba = new BrightnessAgent("brightnessAgent_"+String.valueOf(/*s.roomId*/s.idSens),tracePanel,(TucsonTupleCentreId)tupleCenters.elementAt(s.roomId-1), casa_tc,s.roomId);
                ba.go();
                Thread.sleep(250);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //submitted by Alessandro Celi 
    /*V1.0
     * private void initMeteoAgents(Vector tupleCenters, TracePanel tracePanel,
			TucsonTupleCentreId casa_tc) {
    	try {
    		for(int i=0;i<tupleCenters.size();i++)
    		{
    			if(((TucsonTupleCentreId)tupleCenters.elementAt(i)).getName().equals("meteo_tc"))
    			{
    				MeteoAgent m=new MeteoAgent("meteoAgent_1",tracePanel,(TucsonTupleCentreId)tupleCenters.elementAt(i),casa_tc);  //+((TucsonTupleCentreId)tupleCenters.elementAt(i)).getName()
    				m.go();
    				Thread.sleep(250);
    			}
    		}
    	 }
        catch(Exception ex) {
            ex.printStackTrace();
        }
	}*/
    //V1.2
    public void initAgent(){
        AgentManager.getInstance().goAgents();
    }
    //
    
    public void initLamps(Vector tc, TracePanel tp) {
        LampServiceInterface ls = sf.getLampServiceInterface();
        try {
            Vector lamps = ls.getLamps(database.getDatabase());
            TucsonAgentId agent = new TucsonAgentId("insertLampsDataAgent");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0;i<lamps.size();i++) {
                Lamp l = (Lamp)lamps.elementAt(i);
                LampAgent la = new LampAgent("lamp_"+l.idL, tp, (TucsonTupleCentreId)tc.elementAt(l.sensId-1),l);
                initLamp((TucsonTupleCentreId)tc.elementAt(l.sensId-1), l, acc);
                la.go();
                Thread.sleep(250);
            }
            
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initLamp(TucsonTupleCentreId tid, Lamp l, EnhancedSynchACC acc) {
        try {
            LogicTuple init = new LogicTuple("upd_light_curr_st",new Value(l.sensId),new Value("off"));
            acc.out(tid, init, null);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initDetectorAgents(Vector tCenters, TracePanel tp) {
        SensorServiceInterface ss = sf.getSensorServiceInterface();
        try {
            Vector sensors = ss.getSensors(database.getDatabase());
            for(int i=0;i<sensors.size();i++) {
                Sensor s = (Sensor)sensors.elementAt(i);
                DetectorAgent da = new DetectorAgent("detector_"+s.idSens,tp,(TucsonTupleCentreId)tCenters.elementAt(s.roomId-1),s);
                da.go();
                Thread.sleep(250);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initRoomAccessManagerAgents(Vector tCenters, TracePanel tp, TucsonTupleCentreId rbac_tc, TucsonTupleCentreId casa_tc) {
        SensorServiceInterface ss = sf.getSensorServiceInterface();
        try {
            Vector sensors = ss.getSensors(database.getDatabase());
            for(int i=0;i<sensors.size();i++) {
                Sensor s = (Sensor)sensors.elementAt(i);
                RoomAccessManAgent ram = new RoomAccessManAgent("roomManAcc_"+s.idSens, tp, (TucsonTupleCentreId)tCenters.get(s.roomId-1), rbac_tc, s, casa_tc, sf);
                ram.go();
                Thread.sleep(250);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initListManagerAgents(Vector tCenters, TracePanel tp, ApartmentPanel ap) {
        try {
            for(int i=0; i<tCenters.size();i++)
            {
                ListManager lm = new ListManager("listManager_"+((TucsonTupleCentreId)tCenters.elementAt(i)).getName(), (TucsonTupleCentreId)tCenters.elementAt(i), tp, ap);
                lm.go();
                Thread.sleep(250);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initPlanEntitiesAgents(Vector tupleCenters, TracePanel tracePanel, TucsonTupleCentreId casa_tc) {
        try {
            for(int i=0; i<tupleCenters.size();i++) {
                PlanEntitiesAgent pea = new PlanEntitiesAgent("planEntitiesManager_" + (i+1), tracePanel, (TucsonTupleCentreId)tupleCenters.get(i), casa_tc, sf);
                pea.go();
                Thread.sleep(250);
            }
        } catch (TucsonInvalidAgentIdException ex) {
                Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void initDevsAccessManAgents(Vector tc, TracePanel tp, TucsonTupleCentreId rbac) {
        SensorServiceInterface ss = sf.getSensorServiceInterface();
        try {
            for(int i=0;i<tc.size();i++) {
                DevsAccessManAgent da = new DevsAccessManAgent("roomDevAcc_"+ ((TucsonTupleCentreId)tc.elementAt(i)).getName(),(TucsonTupleCentreId)tc.elementAt(i),rbac, tp);
                da.go();
                Thread.sleep(250);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void insertTupleListInto(TucsonTupleCentreId tid, List<LogicTuple> list) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertTupleList");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(LogicTuple tuple:list) {
                System.out.println("Inserting " + tuple + " into " + tid.toString());
                acc.out(tid, tuple, null);
            }
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initUpdBarrierSize(Vector tupleCenters, int size) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertUpdBarrierSize");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple barrier = new LogicTuple("upd_barrier_size",new Value(size));
            
            for(int i=0; i<tupleCenters.size(); i++)
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), barrier, null);
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initRoomsStates(Vector tupleCenters) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertRoomsStates");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            for(int i=0; i<tupleCenters.size(); i++) {
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), new LogicTuple("state", new Value(new String("free"))), null);
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), new LogicTuple("state", new Value(new String("present"))), null);
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), new LogicTuple("current_state", new Value(new String("free"))), null);
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), new LogicTuple("transition", new Value(new String("free")), new Value(new String("present")), new Value(new String("enter"))), null);
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), new LogicTuple("transition", new Value(new String("present")), new Value(new String("free")), new Value(new String("exit"))), null);
            }
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Vector getRooms() {
        Vector rooms = new Vector();
                    
        try {
            rooms = sf.getRoomServiceInterface().getRooms(database.getDatabase());
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rooms;
    }

    private void initMaxPrio(Vector tupleCenters) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertMaxPrio");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updMaxPrioTemplate = new LogicTuple("upd_max_prio",new Value(1));
            
            for(int i=0; i<tupleCenters.size(); i++)
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), updMaxPrioTemplate, null);
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initRoomsBrightness(Vector tupleCenters) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertBrightness");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple brightnessTemplate = new LogicTuple("brightness",new Value(30));
            
            for(int i=0; i<tupleCenters.size(); i++)
                acc.out((TucsonTupleCentreId)tupleCenters.get(i), brightnessTemplate, null);
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initReadyData(Vector tupleCenters, Vector rooms) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertReadyData");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updReadyDataTemplate;
            
            for(int i=0; i<rooms.size(); i++) {
                updReadyDataTemplate = new LogicTuple("upd_ready_data", new Value(((Room)rooms.elementAt(i)).idRoom),new Value(0));
                acc.out((TucsonTupleCentreId)tupleCenters.get(((Room)rooms.elementAt(i)).idRoom-1), updReadyDataTemplate, null);
            }
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTemp(Vector tupleCenters, Vector rooms) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertTemp");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple tempTemplate;
            
            for(int i=0; i<rooms.size(); i++) {
                tempTemplate = new LogicTuple("update_temp", new Value(((Room)rooms.elementAt(i)).idRoom),new Value(20));
                acc.out((TucsonTupleCentreId)tupleCenters.get(((Room)rooms.elementAt(i)).idRoom-1), tempTemplate, null);
            }
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initEnergy(Vector tupleCenters, Vector rooms, TucsonTupleCentreId casa_tc) {
        try {
            TucsonAgentId agent = new TucsonAgentId("insertEnergy");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            LogicTuple updEnergyTemplate;
            
            ITucsonOperation op_rd = acc.rd(casa_tc, new LogicTuple("temp_mode",new Var("X")), null);
            String temp_mode = op_rd.getLogicTupleResult().getArg(0).getName();
            
            //for(int i=0; i<tupleCenters.size(); i++) {
	    for(int i=0; i<rooms.size(); i++) {
                if(temp_mode.equalsIgnoreCase("ac"))
                    updEnergyTemplate = new LogicTuple("upd_energy", new Value(((Room)rooms.elementAt(i)).idRoom),new Value(0));
                else {
                    Room r = (Room)rooms.elementAt(i);
                    double cons = 1.2 * r.ac;
                    updEnergyTemplate = new LogicTuple("upd_energy", new Value(r.idRoom),new Value(cons));
                }
                acc.out((TucsonTupleCentreId)tupleCenters.get(((Room)rooms.elementAt(i)).idRoom-1), updEnergyTemplate, null);
            }
            
            acc.exit();
        } catch (Exception ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initDevices(Vector tc,TracePanel tp) {
        DeviceServiceInterface ds = sf.getDeviceServiceInterface();
        
        try {
            TucsonAgentId agent = new TucsonAgentId("insertDevicesAgent");
            EnhancedSynchACC acc = TucsonMetaACC.getContext(agent);
            
            /*Vector*/ArrayList<Device> devs = ds.getDevices(database.getDatabase());
            for(int i=0;i<devs.size();i++) {
                //Device d = (Device)devs.elementAt(i);
		Device d = devs.get(i);
                DeviceAgent da = new DeviceAgent("device_"+d.getDeviceId(),tp,(TucsonTupleCentreId)tc.elementAt(d.getDeviceRoomId()-1),d, sf);
                initDevice((TucsonTupleCentreId)tc.elementAt(d.getDeviceRoomId()-1),acc,d);
                da.go();
                Thread.sleep(250);
            }
            acc.exit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void initDevice(TucsonTupleCentreId tid,EnhancedSynchACC acc,Device d) {
        try {
            LogicTuple init = new LogicTuple("upd_dev_curr_st",new Value(d.getDeviceId()),new Value("off"));
            acc.out(tid,init, null);
            //device che hanno una temperatura
            if ((d.getDeviceId()>=5) && (d.getDeviceId()<=7))
                {
            	   LogicTuple init_temp = new LogicTuple("temp_curr_dev", new Value(d.getDeviceId()), new Value(0));
                   acc.out(tid,init_temp,null);
                }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
