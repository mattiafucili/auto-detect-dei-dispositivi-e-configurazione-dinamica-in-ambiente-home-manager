package it.unibo.homemanager;

import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.ServicesInterfaces.*;

public abstract class ServiceFactory {

	public static final int TUCSON = 0;

	public static ServiceFactory getServiceFactory(int wichFactory) {
		switch (wichFactory) {
		case TUCSON:
			return TUCSONServiceFactory.getServiceFactoryInstance();
		default:
			return null;
		}
	}

	public abstract DeviceServiceInterface getDeviceServiceInterface();

	public abstract BlindServiceInterface getBlindServiceInterface();

	public abstract LampServiceInterface getLampServiceInterface();

	public abstract RoomServiceInterface getRoomServiceInterface();

	public abstract SensorServiceInterface getSensorServiceInterface();

	public abstract UserServiceInterface getUserServiceInterface();

	public abstract VisitorServiceInterface getVisitorServiceInterface();

	public abstract WasherProgramServiceInterface getWasherProgramServiceInterface();

	public abstract WindowServiceInterface getWindowServiceInterface();

	public abstract Database getDatabaseInterface() throws Exception;
}
