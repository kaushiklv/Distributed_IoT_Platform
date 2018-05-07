package xmlParser;

import java.util.List;

public class AppModel {
	AppData appInfo;
	List<App> apps;
	
	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<App> getApp() {
		return apps;
	}
	public void setApps(List<App> apps) {
		this.apps = apps;
	}
}