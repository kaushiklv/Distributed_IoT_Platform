import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import EventActionMappingDataObjects.Events;
import EventActionMappingDataObjects.Event;
import EventActionMappingDataObjects.ERules;
import EventActionMappingDataObjects.ERule;
import EventActionMappingDataObjects.Action;

import xmlParser.*;

//import RulesDataObjects.AppData;
import RulesDataObjects.Rules;
import RulesDataObjects.Rule;

//import SensorRuleMappingDataObjects.*;
import SensorRuleMappingDataObjects.SRule;
import SensorRuleMappingDataObjects.SRules;

import org.xml.sax.SAXException;

public class StreamingRulesHandler1 {
	
	public String appName;
	public String whereAmI;
	public String eventPath;
	public String servicePath;
	public String rulePath;
	public String sensorPath;
	public String workflowPath;

	public String delim = "_";
	String message = null;
	// Declare objects for all XML handlers
	EventActionMappingHandler eventActionMappingHandler = new EventActionMappingHandler();
	RulesHandler rulesHandler = new RulesHandler();
	SensorRuleMappingHandler sensorRuleMappingHandler = new SensorRuleMappingHandler();

	public SAXParserFactory saxParserFactory_events, saxParserFactory_rules, saxParserFactory_sensors;
	public SAXParser saxParser_events, saxParser_rules, saxParser_sensors;
	

	public void init(String applicationName) {
		// To run all Handlers and get all XML objects in memory- parser.java
		try {
			appName = applicationName;
			eventPath = appName + "/" + "eventsActionMapping.xml";
			servicePath = appName + "/" + "service.xml";
			rulePath = appName + "/" + "rules.xml";
			sensorPath = appName + "/" + "sensorRuleMapping.xml";
			workflowPath = appName + "/" + "workflow.xml";
			// EventActionMappingHandler
			String xmlFilePath_events = eventPath;
			saxParserFactory_events = SAXParserFactory.newInstance();
			saxParser_events = saxParserFactory_events.newSAXParser();
			saxParser_events.parse(new File(xmlFilePath_events), eventActionMappingHandler);

			// RulesHandler
			String xmlFilePath_rules = rulePath;
			saxParserFactory_rules = SAXParserFactory.newInstance();
			saxParser_rules = saxParserFactory_rules.newSAXParser();
			saxParser_rules.parse(new File(xmlFilePath_rules), rulesHandler);

			// SensorRuleMappingHandler
			String xmlFilePath_sensors = sensorPath;
			saxParserFactory_sensors = SAXParserFactory.newInstance();
			saxParser_sensors = saxParserFactory_sensors.newSAXParser();
			saxParser_sensors.parse(new File(xmlFilePath_sensors), sensorRuleMappingHandler);

			// Events test = getEventsData();
			// System.out.println("DOne");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	// ****FORMAT OF MESSAGE RECIEVED ON MESSAGE QUEUE IS GIVEN IN
	// streamingRules.txt************/
	public void processMessage(String message, String engine, String mqIpAddress,
							   String applicationName, String domain, String whatFileToRun, String instance) {
		init(applicationName);
		appName = applicationName;
		whereAmI = domain;
		this.message = message;
		String[] streamingRuleTokens = message.split("-");
		boolean result = false;
		String sensorData = getSensorData(streamingRuleTokens[0]);
		String ruleOrEvent = streamingRuleTokens[1];
		String rule_event = ruleOrEvent.split("#")[0];
		/// RULE ENGINE *******************/
		if (rule_event.equalsIgnoreCase("rule")) {

			String msgRuleid1 = ruleOrEvent.split("#")[1];
			result = processRule(msgRuleid1, sensorData);
			
			System.out.println("Rule Result: " + result);
			if (result) {
				String serviceOrWorkflow = streamingRuleTokens[2];
				String service = serviceOrWorkflow.split("#")[1];
				serviceOrWorkflow = serviceOrWorkflow.split("#")[0];
				
				if (serviceOrWorkflow.equalsIgnoreCase("service")) {
					
					// TODO Service invocation with sensor data
					
					System.out.println("Calling Service id: " + service);
					
					ServiceParser serviceParser = new ServiceParser();
					ServiceData serviceData = serviceParser.getServiceData(servicePath);
					
					List<Service> serviceList = serviceData.appservice.getServices();
					
					for(Service ser : serviceList) {
						if(ser.getServiceId().equals(service)) {
							whatFileToRun = ser.getServiceJarName();
						}
					}
					
					String serviceMessage;
					if(whatFileToRun.endsWith("py"))
						serviceMessage = whatFileToRun + " " + sensorData;
					else
						serviceMessage = sensorData;
					
					Sender sender = new Sender();
					try {
						LoggingService.addLogs(appName, "RuleEngine", "Message: Service Data " + serviceMessage);
						System.out.println("Message: " + serviceMessage);
						System.out.println("QueueName: " + appName + delim + domain + delim + whatFileToRun + delim + instance);
						sender.sendMessage(serviceMessage, 
										   appName + delim + domain + delim + whatFileToRun + delim + instance, 
										   mqIpAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (serviceOrWorkflow.equalsIgnoreCase("workflow")) {
					// TODO Workflow invocation with sensor data
					
					String workflow = service;
					
					WorkflowParser workflowParser = new WorkflowParser();
					WorkflowData workflowData = workflowParser.getWorkflowData(workflowPath);
					
					List<Workflow> workflowList = workflowData.appworkflow.getWorkflows();
					
					for(Workflow wf : workflowList) {
						if(wf.getWorkFlowId().equals(workflow))
							whatFileToRun = wf.getWorkFlowFileName();
					}					
					
					Sender sender = new Sender();
					try {
						LoggingService.addLogs(appName, "RuleEngine", "Message: Workflow " + workflow);
						System.out.println("Call Workflow");
						System.out.println("Message: " + workflow);
						System.out.println("QueueName: " + appName + delim + domain + delim + whatFileToRun + delim + instance);
						sender.sendMessage(workflow, 
										   appName + delim + domain + delim + whatFileToRun + delim + instance,
										   mqIpAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		//// EVENT ENGINE*********************/
		else if (rule_event.equalsIgnoreCase("event")) {
			// String event = ruleOrEvent.split("-")[1];
			ArrayList<Boolean> result1 = processEvent(ruleOrEvent, sensorData);
			if (result1.contains(false))
				result = false;
			else
				result = true;
			System.out.println("Event Result: " + result);

			if (result) {
				String service = " ";
				String workflow = " ";
				Events eventList = eventActionMappingHandler.getEvents();
				String msgeventid = ruleOrEvent.split("#")[1];
				for (Event event1 : eventList.getEventList()) {
					// System.out.println(event1.getEventId());
					String eventid = event1.getEventId();
					if (eventid.equalsIgnoreCase(msgeventid)) {
						List<Action> eventaction = event1.getActionList();
						for (Action action : eventaction) {
							service = action.getServiceId();
							workflow = action.getWorkflowId();
						}
					}
				}

				if (service != " ") {					
					// TODO Service invocation with sensor data
					ServiceParser serviceParser = new ServiceParser();
					ServiceData serviceData = serviceParser.getServiceData(servicePath);
					
					List<Service> serviceList = serviceData.appservice.getServices();
					
					for(Service ser : serviceList) {
						if(ser.getServiceId() == service) {
							whatFileToRun = ser.getServiceJarName();
						}
					}		
					
					String serviceMessage;
					if(whatFileToRun.endsWith("py"))
						serviceMessage = whatFileToRun + " " + sensorData;
					else
						serviceMessage = sensorData;
					
					Sender sender = new Sender();
					try {
						LoggingService.addLogs(appName, "EventEngine", "Message: Service " + whatFileToRun);
						System.out.println("Call Service");
						System.out.println("Action Message: " + serviceMessage);
						System.out.println("Action Service: " + appName + delim + domain + delim + whatFileToRun + delim + instance);
						sender.sendMessage(serviceMessage,
										   appName + delim + domain + delim + whatFileToRun + delim + instance,
										   mqIpAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (workflow != " ") {
					// TODO Workflow invocation with sensor data
					
					WorkflowParser workflowParser = new WorkflowParser();
					WorkflowData workflowData = workflowParser.getWorkflowData(workflowPath);
					
					List<Workflow> workflowList = workflowData.appworkflow.getWorkflows();
					
					for(Workflow wf : workflowList) {
						if(wf.getWorkFlowId().equals(workflow))
							whatFileToRun = wf.getWorkFlowFileName();
					}
					
					Sender sender = new Sender();
					try {
						LoggingService.addLogs(appName, "EventEngine", "Message: Workflow " + whatFileToRun);
						System.out.println("Call Workflow");
						System.out.println("Action Message: " + whatFileToRun);
						System.out.println("Action Service: " + appName + delim + domain + delim + whatFileToRun + delim + instance);
						sender.sendMessage(whatFileToRun, 
										   appName + delim + domain + delim + whatFileToRun + delim + instance,
										   mqIpAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void sendMessageToUi(String message) {
		URL url;
		try {
			url = new URL("http://192.168.1.106:3000/execution/event**" + (new Date()).toString().replaceAll("[ :]", "_") + "_" + message);					
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "text/plain");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String getSensorData(String sensorMessage) {
		// TODO Get Data from Sensor fron rest API from kushagra
        // sensor##Honeywell##123
		String send = sensorMessage.split("#")[2];
		return send;
	}

	/// *****PROCESSING RULE ENGINE :-- rules.xml,
	/// sensor_rule_mapping.xml********************/
	boolean processRule(String msgRuleid1, String data) {
		// TODO Use rule handler to get rule and check rule condition on data
		/// got as a message

		SRules sensorRulesList = sensorRuleMappingHandler.getRules();
		// Variables from sensors mapping file
		String datatype = " ";
		String threshold = " ";
		String ruleid = " ";
		for (SRule item : sensorRulesList.getRuleList()) {
			String rulemid = item.getRuleMId(); /// ruleMID from sensors mapping file

			if (rulemid.equalsIgnoreCase(msgRuleid1)) {
				
				ruleid = item.getRuleId();
				datatype = item.getDataType();
				threshold = item.getThreshold();
			}
		}
		System.out.println("datatype: " + datatype);
		System.out.println("Data: " + data);		
//		System.out.println("threshold: " + threshold);
		Rules rulesList = rulesHandler.getRules();

		for (Rule item : rulesList.getRuleList()) {
			String Rulesruleid = item.getRuleId();

			if (Rulesruleid.equalsIgnoreCase(ruleid)) {
				String rulename1 = item.getRuleName();
				System.out.println("RULE: " + rulename1);
				sendMessageToUi("RULE_" + rulename1);
				if (datatype.equalsIgnoreCase("INT")) {
					int ruledata = Integer.parseInt(data.split("#")[0]);
					System.out.println("SensorData: " + ruledata);
					sendMessageToUi("SensorData_" + ruledata);
					int thresh = Integer.parseInt(threshold);
					System.out.println("Threshold: " + thresh);
					sendMessageToUi("Threshold_" + thresh);
					if (rulename1.equalsIgnoreCase("upper")) {
						if (ruledata > thresh)
							return true;
					} else if (rulename1.equalsIgnoreCase("lower")) {
						if (ruledata < thresh)
							return true;
					} else if (rulename1.equalsIgnoreCase("equal")) {
						if (ruledata == thresh)
							return true;
					}

				} else if (datatype.equalsIgnoreCase("String")) {
					int ruledata = Integer.parseInt(data.split("#")[0]);
					System.out.println("SensorData: " + ruledata);
					sendMessageToUi("SensorData_" + ruledata);
					int thresh = Integer.parseInt(threshold);
					System.out.println("Threshold: " + thresh);
					sendMessageToUi("Threshold_" + thresh);
					if (rulename1.equalsIgnoreCase("upper")) {
						if (ruledata > thresh)
							return true;
					} else if (rulename1.equalsIgnoreCase("lower")) {
						if (ruledata < thresh)
							return true;
					} else if (rulename1.equalsIgnoreCase("equal")) {
						if (ruledata == thresh)
							return true;
					}

				}

			}
		}
		return false;
	}
	
	

	/// *****PROCESSING EVENT ENGINE :--eventsActionMapping.xml, rules.xml,
	/// sensorRuleMapping.xml********************/
	ArrayList<Boolean> processEvent(String event, String data) {
		// TODO Use EventAction Mapping handler to get list of rules to be applied
		// and check rule condition on data(Call processRule for each rule)
		System.out.println(event);
		String msgeventid = event.split("#")[1];
		Events eventList = eventActionMappingHandler.getEvents();
		ArrayList<Boolean> returnresult = new ArrayList<Boolean>();

		for (Event event1 : eventList.getEventList()) {
			// System.out.println(event1.getEventId());
			String eventid = event1.getEventId();
			if (eventid.equalsIgnoreCase(msgeventid)) {
				ERules eventrules = event1.getRules();
				String messageForUI = appName + "_" + whereAmI + "_EventEngine" + "_Executing_Event_" + eventid;
    			sendMessageToUi(messageForUI);
				for (ERule rules : eventrules.getRuleList()) {
					// System.out.println(rules.getRuleMId());
					String msgRuleid1 = rules.getRuleMId();
					System.out.println("Rule id:" + rules.getRuleMId());
					LoggingService.addLogs(appName, "EventEngine", "id:" + rules.getRuleMId());
					boolean res = processRule(msgRuleid1, data);
					sendMessageToUi("Rule_Result_" + res);
					returnresult.add(res);
					
					System.out.println("Event Rules Results----------------------------");
					for (Boolean a : returnresult) {
						System.out.println("Rule Result: " + a);
						String msg = "Rule_Result_" + a;						
					}
					System.out.println("-----------------------------------------------");
					
				}
			}
		}
		return returnresult;
	}

}
