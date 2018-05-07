package xmlParser;

import java.util.List;

public class AppWorkflow {
	AppData appInfo;
	List<Workflow> workflows;
	
	public AppData getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(AppData appInfo) {
		this.appInfo = appInfo;
	}
	public List<Workflow> getWorkflows() {
		return workflows;
	}
	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}
}