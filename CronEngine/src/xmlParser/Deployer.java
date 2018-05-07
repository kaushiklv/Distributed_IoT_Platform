package xmlParser;

import java.util.List;

public class Deployer {
	AppData appInfo;
	List<DeployerApp> deployerApp;
	
	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<DeployerApp> getDeployerApp() {
		return deployerApp;
	}
	public void setDeployerApp(List<DeployerApp> apps) {
		this.deployerApp = apps;
	}
}