package xmlParser;

import java.util.List;

public class Topology {
	public AppData appInfo;
	public List<Gateway> gateways;
	public List<IntermediateServer> intermediateServer;
	
	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<Gateway> getGateways() {
		return gateways;
	}
	public void setGateways(List<Gateway> gateways) {
		this.gateways = gateways;
	}
	public List<IntermediateServer> getIntermediateServer() {
		return intermediateServer;
	}
	public void setIntermediateServer(List<IntermediateServer> intermediateServer) {
		this.intermediateServer = intermediateServer;
	}
	
	
}
