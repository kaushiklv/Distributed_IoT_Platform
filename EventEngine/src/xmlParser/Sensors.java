package xmlParser;

import java.util.List;

public class Sensors {
	AppData appInfo;
	List<Sensor> Sensors;
	
	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<Sensor> getSensor() {
		return Sensors;
	}
	public void setSensors(List<Sensor> Sensors) {
		this.Sensors = Sensors;
	}
}