package it.unibo.homemanager.detection;

public class Device {
	private int deviceId;
	private String deviceName;
	private String deviceParameters;
	private String deviceType;
	private float deviceEnergy;
	private int deviceRoomId;
	
	public Device() { }
	
	public Device(int deviceId, String deviceName, float deviceEnergy, String deviceType, String deviceParameters, int deviceRoomId) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.deviceEnergy = deviceEnergy;
		this.deviceType = deviceType;
		this.deviceParameters = deviceParameters;
		this.deviceRoomId = deviceRoomId;
	}
	
	public int getDeviceId() {
		return (deviceId);
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return (deviceName);
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceParameters() {
		return (deviceParameters);
	}

	public void setDeviceParameters(String deviceParameters) {
		this.deviceParameters = deviceParameters;
	}
	
	public String getDeviceType() {
		return (deviceType);
	}
	
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public float getDeviceEnergy() {
		return (deviceEnergy);
	}

	public void setDeviceEnergy(float deviceEnergy) {
		this.deviceEnergy = deviceEnergy;
	}

	public int getDeviceRoomId() {
		return (deviceRoomId);
	}

	public void setDeviceRoomId(int deviceRoomId) {
		this.deviceRoomId = deviceRoomId;
	}

	public String toString() {
		return ("Name: " + getDeviceName() + "\n" + "Device Type: " + getDeviceType() + "\n" + 
				"Energy Consumption: " + getDeviceEnergy() + " kW\n" + "Parameters: " + getDeviceParameters());
	}
}