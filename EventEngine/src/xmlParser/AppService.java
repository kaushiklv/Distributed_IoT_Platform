package xmlParser;

import java.util.List;


public class AppService {
	public  AppData appInfo;
	public List<Service> services;

	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> service) {
		this.services = service;
	}
}