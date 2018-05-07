package EventActionMappingDataObjects;

import java.util.List;

public class Event {
	String eventId;
	ERules rules;
	List<Action> actionList;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public ERules getRules() {
		return rules;
	}

	public void setRules(ERules rules) {
		this.rules = rules;

	}

	public List<Action> getActionList() {
		return actionList;
	}

	public void setActionList(List<Action> actionList) {
		this.actionList = actionList;

	}

}