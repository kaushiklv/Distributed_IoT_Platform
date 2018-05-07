

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import EventActionMappingDataObjects.Action;
import EventActionMappingDataObjects.AppData;
import EventActionMappingDataObjects.Event;
import EventActionMappingDataObjects.Events;
import EventActionMappingDataObjects.ERule;
import EventActionMappingDataObjects.ERules;

public class EventActionMappingHandler extends DefaultHandler {
	private List<AppData> appList = null;
	private List<Event> eventList = null;
	private List<Action> actionList = null;
	private List<ERule> ruleList = null;
	
	private List<Events> eventsList = null;
	private Events events=null;
	
	private ERules rules = null;
	private AppData app = null;
	private Event event = null;
	private Action action = null;
	private ERule rule = null;

	public List<AppData> getAppList() {
		return appList;
	}

	public Events getEvents() {
		return events;
	}

	boolean bAppId = false;
	boolean bAppName = false;
	boolean beventID = false;
	boolean bruleMID = false;
	boolean bserviceid = false;
	boolean bworkflowid = false;

	public void startElement(String paramString1, String paramString2, 
			String paramString3, Attributes paramAttributes) throws SAXException {
		
	    if (paramString3.equalsIgnoreCase("appInfo")) {
	      app = new AppData();
	      if (appList == null)
	        appList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("name")) {
	      bAppName = true;
	    } else if (paramString3.equalsIgnoreCase("id")) {
	      bAppId = true;
	    }
	    
	    
	    if (paramString3.equalsIgnoreCase("events")) {
	       events = new Events(); 
	       if (eventsList == null)
	           eventsList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("event")) {
	      event = new Event();
	      if (eventList == null)
	        eventList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("eventID")) {
	      beventID = true;
	    } else if (paramString3.equalsIgnoreCase("rules")) {
	      rules = new ERules(); 
	      if (ruleList == null)
	          ruleList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("rule")) {
	    	rule = new ERule();
	    } else if (paramString3.equalsIgnoreCase("ruleMID")) {
	      bruleMID = true;
	    } else if (paramString3.equalsIgnoreCase("action")) {
	      action = new Action();
	      if (actionList == null)
	        actionList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("serviceid")) {
	      bserviceid = true;
	    } else if (paramString3.equalsIgnoreCase("workflowid")) {
	      bworkflowid = true;
	    } 

	}

	public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
		if (paramString3.equalsIgnoreCase("appInfo")) {
			appList.add(app);
			appList = null;
		}
		if (paramString3.equalsIgnoreCase("events")) {
			events.setEventList(eventList);
		}
		
		if (paramString3.equalsIgnoreCase("event")) {
			eventList.add(event);
		}
		if (paramString3.equalsIgnoreCase("rule")) {
			ruleList.add(rule);
		}
		if (paramString3.equalsIgnoreCase("action")) {
			actionList.add(action);
			event.setActionList(actionList);
			actionList = null;
		}
		if (paramString3.equalsIgnoreCase("rules")) {
			rules.setRuleList(ruleList);
			event.setRules(rules);
			ruleList = null;
		}

	}

	public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
		if (bAppId) {
			app.setId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bAppId = false;
		} else if (bAppName) {
			app.setName(new String(paramArrayOfChar, paramInt1, paramInt2));
			bAppName = false;
		} else if (beventID) {
			event.setEventId(new String(paramArrayOfChar, paramInt1, paramInt2));
			beventID = false;
		} else if (bruleMID) {
			rule.setRuleMId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bruleMID = false;
		} else if (bserviceid) {
			action.setServiceId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bserviceid = false;
		} else if (bworkflowid) {
			action.setWorkflowId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bworkflowid = false;
		}
	}
}