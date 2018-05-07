package xmlParser;

import java.util.List;

public class Task {
	List<Sensor> sensors;
	String eventId;
	String serviceId;
	String workflowId;
	String time;
	
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setTime(String time){
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	
	
}
