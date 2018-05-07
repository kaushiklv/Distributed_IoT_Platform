package xmlParser;
import java.util.List;

public class App {
	String appName;
	List<String> artifacts;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public List<String> getArtifacts() {
		return artifacts;
	}
	public void setArtifacts(List<String> artifact) {
		this.artifacts = artifact;
	}
}
