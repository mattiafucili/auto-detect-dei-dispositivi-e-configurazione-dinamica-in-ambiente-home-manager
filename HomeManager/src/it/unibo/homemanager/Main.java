/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.service.TucsonNodeService;
import it.unibo.homemanager.detection.DeviceManagerAgent;
import it.unibo.homemanager.usages.UsageManagerAgent;
import it.unibo.homemanager.userinterfaces.GeneralFrame;
import it.unibo.homemanager.util.Initializer;

/**
 *
 * @author sik
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            int NUM = ServiceFactory.TUCSON;
            ServiceFactory sf = ServiceFactory.getServiceFactory(NUM);
            
            if(TucsonNodeService.isInstalled(20504))
                System.out.println("Service already active");
            else {
                System.out.println("Service not running! :(");
                System.exit(-1);
            }
            
            System.out.println("Initializing Home Manager!");
            
            TucsonTupleCentreId ingresso_tc = new TucsonTupleCentreId("ingresso_tc", "localhost", "20504");
            TucsonTupleCentreId cucina_tc = new TucsonTupleCentreId("cucina_tc", "localhost", "20504");
            TucsonTupleCentreId sala_tc = new TucsonTupleCentreId("sala_tc", "localhost", "20504");
            TucsonTupleCentreId studio_tc = new TucsonTupleCentreId("studio_tc", "localhost", "20504");
            TucsonTupleCentreId corridoio_tc = new TucsonTupleCentreId("corridoio_tc", "localhost", "20504");
            TucsonTupleCentreId bagno_tc = new TucsonTupleCentreId("bagno_tc", "localhost", "20504");
            TucsonTupleCentreId camera_tc = new TucsonTupleCentreId("camera_tc", "localhost", "20504");
            TucsonTupleCentreId ripostiglio_tc = new TucsonTupleCentreId("ripostiglio_tc", "localhost", "20504");
            TucsonTupleCentreId camera_doppia_tc = new TucsonTupleCentreId("camera_doppia_tc", "localhost", "20504");
            TucsonTupleCentreId bagno_priv_tc = new TucsonTupleCentreId("bagno_privato_tc", "localhost", "20504");
            TucsonTupleCentreId garage_tc = new TucsonTupleCentreId("garage_tc", "localhost", "20504");
            //submitted by Sara Bevilacqua
            System.out.println("Creo twitter_tc");
            TucsonTupleCentreId twitter_tc = new TucsonTupleCentreId("twitter_tc", "localhost", "20504");
            //submitted by Alessandro Celi
            TucsonTupleCentreId meteo_tc = new TucsonTupleCentreId("meteo_tc", "localhost", "20504");
            
            // submitted by Mattia Fucili
            TucsonTupleCentreId deviceManager_tc = new TucsonTupleCentreId("device_manager_tc", "localhost", "20504");
            // submitted by Luca Scalzotto
            TucsonTupleCentreId mixer_tc = new TucsonTupleCentreId("mixer_tc", "localhost", "20504");
            TucsonTupleCentreId mixerContainer_tc = new TucsonTupleCentreId("mixer_container_tc", "localhost", "20504");
            TucsonTupleCentreId fridge_tc = new TucsonTupleCentreId("fridge_tc","localhost","20504");
            TucsonTupleCentreId oven_tc = new TucsonTupleCentreId("oven_tc","localhost","20504");
            // submitted by Erika Gardini
            TucsonTupleCentreId pantry_tc = new TucsonTupleCentreId("pantry_tc","localhost","20504");
            TucsonTupleCentreId usageManager_tc = new TucsonTupleCentreId("usage_manager_tc", "localhost", "20504");
            
            // RC replaced rbac_tc with rbac
            System.out.println("Creo rbac");
            TucsonTupleCentreId rbac_tc = new TucsonTupleCentreId("rbac", "localhost", "20504");
            TucsonTupleCentreId casa_tc = new TucsonTupleCentreId("casa_tc", "localhost", "20504");
            
            System.out.println("TCenters created.. 5 sec to connect the Inspector..");
            Thread.sleep(5000);
            
            Vector<TucsonTupleCentreId> tupleCenters = new Vector<TucsonTupleCentreId>();
            tupleCenters.add(0, ingresso_tc);
            tupleCenters.add(1, sala_tc);
            tupleCenters.add(2, camera_tc);
            tupleCenters.add(3, studio_tc);
            tupleCenters.add(4, cucina_tc);
            tupleCenters.add(5, bagno_tc);
            tupleCenters.add(6, corridoio_tc);
            tupleCenters.add(7, ripostiglio_tc);
            tupleCenters.add(8, camera_doppia_tc);
            tupleCenters.add(9, bagno_priv_tc);
            tupleCenters.add(10, garage_tc);
            //subamitted by Alessandro Celi
            tupleCenters.add(11, meteo_tc); 
            //subamitted by Sara Bevilacqua
            tupleCenters.add(12,twitter_tc);
            
            // submitted by Mattia Fucili   
            tupleCenters.add(13, deviceManager_tc);
            // submitted by Luca Scalzotto
            tupleCenters.add(14, mixer_tc);
            tupleCenters.add(15, mixerContainer_tc);
            tupleCenters.add(16, fridge_tc);
            tupleCenters.add(17, oven_tc);
            // submitted by Erika Gardini
            tupleCenters.add(18, pantry_tc);
            tupleCenters.add(19, usageManager_tc);
            
            // init deviceManager
            DeviceManagerAgent deviceManagerAgent = DeviceManagerAgent.getInstance();
            deviceManagerAgent.go();
            
            // init usageManager
            UsageManagerAgent usageManagerAgent = UsageManagerAgent.getInstance();
            usageManagerAgent.go();
            
            JFrame.setDefaultLookAndFeelDecorated(true);
            GeneralFrame gf = new GeneralFrame("HOME MANAGER", tupleCenters, 3, 5, casa_tc, rbac_tc, sf);
            gf.setVisible(true);
            
            gf.getTracePanel().appendText("Initializing agents and data...");
            
            //Initializer init = new Initializer();
            Initializer init = new Initializer(sf);
            //Utilities.insertTupleInto(casa_tc, new LogicTuple("external_brightness", new Value(80)));
            //tns.enablePersistence(rbac_tc);
            //Thread.sleep(5000);
            //Utilities.insertTupleInto(casa_tc, new LogicTuple("temp_mode",new Value("heat")));
            //ass_class_devs('Home_Entertainment','Stereo Philips MCM 190/22')
            //Utilities.insertTupleInto(rbac_tc, new LogicTuple("ass_class_devs", new Value("Home_Entertainment"), new Value("Stereo Philips MCM 190/22")));
            //Thread.sleep(5000);
            //active_role(userID,"default")
            //Utilities.insertTupleInto(rbac_tc, new LogicTuple("active_role", new Value(1), new Value("Genitore")));
            //Thread.sleep(12500);
            //Utilities.insertTupleInto(rbac_tc, new LogicTuple("active_role", new Value(3), new Value("Bambino")));
            init.setReSpecT(tupleCenters, casa_tc, rbac_tc);  
            //init.initializeRBAC(rbac_tc);
            //init.initializeDatabaseTc();
            init.initializeCasaTc(casa_tc);
            
            boolean init_result = init.initializeData(tupleCenters, casa_tc, rbac_tc, gf.getTracePanel(), gf.getApPanel());
            
            if(init_result) {
                System.out.println("Initialization correctly done!");
                gf.getTracePanel().appendText("Objects and data initialized correctly!");
            }
            else {
                System.err.println("Initialization failed!");
                gf.getTracePanel().appendText("Initialization of objects and data failed!");
                System.exit(-1);
            }
            
            /*Thread.sleep(10000);
            System.out.println("Sending tuple detection(\"entry\",3,1,3) to sala_tc..");
            Utilities.insertTupleInto(sala_tc, new LogicTuple("detection", new Value("entry"), new Value(3), new Value(1), new Value(3)));
            System.out.println("Tuple sent!");
            Thread.sleep(10000);
            System.out.println("Sending tuple detection(\"entry\",3,3,3) to sala_tc..");
            Utilities.insertTupleInto(sala_tc, new LogicTuple("detection", new Value("entry"), new Value(3), new Value(3), new Value(3)));
            System.out.println("Tuple sent!");*/
            
            /*Thread.sleep(10000);
            System.out.println("Sending tuple detection(\"move\",4,1,4) to studio_tc..");
            Utilities.insertTupleInto(studio_tc, new LogicTuple("detection", new Value("move"), new Value(4), new Value(1), new Value(4)));
            System.out.println("Tuple sent!");*/
            
            //Thread.sleep(5000);
            //LogicTuple activationTest = new LogicTuple("activate_req", new Value(1), new Value("default"));
            //LogicTuple activationTest = new LogicTuple("activate_req", new Value(1), new Value("Bambino"));
            //LogicTuple activationTest = new LogicTuple("activate_req", new Value(3), new Value("default"));
            //LogicTuple activationTest = new LogicTuple("activate_req", new Value(3), new Value("Bambino"));
            //Utilities.insertTupleInto(casa_tc, activationTest);
            //Thread.sleep(10000);
            //activationTest = new LogicTuple("activate_req", new Value(3), new Value("Figlio"));
            //Utilities.insertTupleInto(casa_tc, activationTest);
            
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}