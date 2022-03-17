package it.unibo.homemanager;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.TucsonDatabase;
import it.unibo.homemanager.tablemap.ServicesInterfaces.*;
import it.unibo.homemanager.tablemap.TucsonService.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TUCSONServiceFactory extends ServiceFactory {
    
    private static TUCSONServiceFactory instance;
    private final TucsonDeviceService tucsonDeviceService;
    private final TucsonBlindService tucsonBlindService;
    private final TucsonLampService tucsonLampService;
    private final TucsonRoomService tucsonRoomService;
    private final TucsonSensorService tucsonSensorService;
    private final TucsonUserService tucsonUserService;
    private final TucsonVisitorService tucsonVisitorService;
    private final TucsonWasherProgramService tucsonWasherProgramService;
    private final TucsonWindowService tucsonWindowService;
    private final TucsonDatabase tucsonDatabase;

    public static synchronized TUCSONServiceFactory getServiceFactoryInstance() {
	if(instance == null)
	    try {
		instance = new TUCSONServiceFactory();
	} catch (Exception ex) {
	    Logger.getLogger(TUCSONServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	return instance;
    }
    
    private TUCSONServiceFactory() throws Exception {
	this.tucsonDeviceService = new TucsonDeviceService();
	this.tucsonBlindService = new TucsonBlindService();
	this.tucsonLampService = new TucsonLampService();
	this.tucsonRoomService = new TucsonRoomService();
	this.tucsonSensorService = new TucsonSensorService();
	this.tucsonUserService = new TucsonUserService();
	this.tucsonVisitorService = new TucsonVisitorService();
	this.tucsonWasherProgramService = new TucsonWasherProgramService();
	this.tucsonWindowService = new TucsonWindowService();
	this.tucsonDatabase = new TucsonDatabase();
    }
    
	@Override
	public DeviceServiceInterface getDeviceServiceInterface() {
		
		/**Sarebbe stato TucsonDeviceService() ma possiamo
		 * sfruttare le nuove caratteristiche si Java8 per
		 * ridurre il numero delle cassi */
		
		return this.tucsonDeviceService;//new TucsonDeviceService();
	}

	@Override
	public BlindServiceInterface getBlindServiceInterface() {
		
		return this.tucsonBlindService;//new TucsonBlindService();
	}

	@Override
	public LampServiceInterface getLampServiceInterface() {
		
		return this.tucsonLampService;//new TucsonLampService();
	}

	@Override
	public RoomServiceInterface getRoomServiceInterface() {
		return this.tucsonRoomService;//new TucsonRoomService();
	}

	@Override
	public SensorServiceInterface getSensorServiceInterface() {
		
		return this.tucsonSensorService;//new TucsonSensorService();
	}

	@Override
	public UserServiceInterface getUserServiceInterface() {
		return this.tucsonUserService;//new TucsonUserService();
	}

	@Override
	public VisitorServiceInterface getVisitorServiceInterface() {
	    return this.tucsonVisitorService;//new TucsonVisitorService();
	}

	@Override
	public WasherProgramServiceInterface getWasherProgramServiceInterface() {
		return this.tucsonWasherProgramService;//new TucsonWasherProgramService();
	}

	@Override
	public WindowServiceInterface getWindowServiceInterface() {
		return this.tucsonWindowService;//new TucsonWindowService();
	}
	
	@Override
	public Database getDatabaseInterface() throws Exception {
		return this.tucsonDatabase;//new TucsonDatabase();
	}

}