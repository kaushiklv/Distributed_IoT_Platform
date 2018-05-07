package xmlParser;

import java.util.List;

public class IntermediateServer {
	String intermediateServerId;
	String IP;
	String port;
	String username;
	String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	List<Gateway> gateways;
	
	public String getIntermediateServerId() {
		return intermediateServerId;
	}
	public void setIntermediateServerId(String intermediateServerId) {
		this.intermediateServerId = intermediateServerId;
	}
	public List<Gateway> getGateways() {
		return gateways;
	}
	public void setGateways(List<Gateway> gateways) {
		this.gateways = gateways;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
}
