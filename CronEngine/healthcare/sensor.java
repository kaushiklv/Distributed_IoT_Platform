package resources;

class sensor implements sensorType {
	String sensorName;
	String sensorId;

	public Object getSensorData() {
		return sensorId;
		// can use any method to get the value of sensor
	}

	@Override
	public void setSensorData(String sensorName) {
		// TODO Auto-generated method stub
		
	}
};