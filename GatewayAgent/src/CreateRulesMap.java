package sensorDataApi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CreateRulesMap {
	
	private static List<Map<String, List<String>>> ruleMapList = new ArrayList<>();
	
	/*
	private static Map<String, List<String>> streamingRulesMapGateway;
	private static Map<String, List<String>> eventRulesMapIS;
	private static Map<String, List<String>> eventRulesMapPlatform;
	*/
	
	private static Map<String, String> sensorValueMap;
	static String msgQueueName;
	static String applicationName;
	static String connectionIS;
	
	static String ftpIp;
	static String ftpUser;
	static String ftpPass;

	static 
	{
		sensorValueMap = new HashMap<>();
	}


	public static String getFileLocation(String fileName) {
		/*
		 * TODO: implement FTP connection here.
		 */
		//String location = "./" + applicationName + "/" + fileName;
		String location = "/home/kush/" + applicationName + "/" + fileName;
		return location;
	}
	
	public static void createRulesMap() {
		/*
		List<String> ruleFileNameList = new ArrayList<>();
		ruleFileNameList.add("streamingRulesGateway.txt");
		ruleFileNameList.add("streamingRulesIS.txt");
		ruleFileNameList.add("streamingRulesPlatform.txt");
		*/
		
		String ruleFileName = "streamingRulesEvents.txt";
		/*
		String ruleFileNameIS = "streamingRulesIS.txt";
		String ruleFileNamePlatform = "streamingRulesPlatform.txt";
		*/
		
		String splitChar = "-";
		/*
		List<String> ruleFileLocationList = new ArrayList<>();
		ruleFileLocationList.add(getFileLocation(ruleFileNameList.get(0)));
		ruleFileLocationList.add(getFileLocation(ruleFileNameList.get(1)));
		ruleFileLocationList.add(getFileLocation(ruleFileNameList.get(2)));
		*/
		
		String ruleFileLocation = getFileLocation(ruleFileName);
		/*
		String ruleFileLocationIS = getFileLocation(ruleFileNameIS);
		String ruleFileLocationPlatform = getFileLocation(ruleFileNamePlatform);
		*/
		
		ruleMapList.add(new HashMap<String, List<String>>());
		ruleMapList.add(new HashMap<String, List<String>>());
		ruleMapList.add(new HashMap<String, List<String>>());
		
		/*
		streamingRulesMapGateway = new HashMap<>();
		eventRulesMapIS = new HashMap<>();
		eventRulesMapPlatform = new HashMap<>();
		*/
		
		try(BufferedReader br = new BufferedReader(new FileReader(ruleFileLocation))){
			String line = br.readLine();
			while(line != null) {
				
				int posForSensitivity = line.indexOf(splitChar);
				String sensitivity = line.substring(0, posForSensitivity);
				System.out.println("Sensitivity:" + sensitivity);
				
				// Now line contains whole rule/event detail
				line = line.substring(posForSensitivity + 1);
				
				int splitIndex = line.indexOf(splitChar);
				
				String sensorID = line.substring(0, splitIndex);
				int splitSensorName = sensorID.indexOf("#");
				sensorID = sensorID.substring(splitSensorName+1);
				
				// Map: 0-Gateway; 1-IS; 2-Platform
				List<String> listOfLines;
				if(sensitivity.equalsIgnoreCase("high"))
					listOfLines = ruleMapList.get(0).get(sensorID);
				else if(sensitivity.equalsIgnoreCase("medium"))
					listOfLines = ruleMapList.get(1).get(sensorID);
				else
					listOfLines = ruleMapList.get(2).get(sensorID);
				
				if(listOfLines == null) {
					listOfLines = new ArrayList<>();				
				}
				
				listOfLines.add(line);
				System.out.println("Sid:" + sensorID);
				
				
				if(sensitivity.equalsIgnoreCase("high"))
					ruleMapList.get(0).put(sensorID, listOfLines);
				else if(sensitivity.equalsIgnoreCase("medium"))
					ruleMapList.get(1).put(sensorID, listOfLines);
				else
					ruleMapList.get(2).put(sensorID, listOfLines);
					
				line = br.readLine();
			}
			
		} catch (FileNotFoundException e) {
			// LOG IT
			System.out.println("Streaming files not found!");
			try {
				Receiver.receiveMessage(msgQueueName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();			

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		//reading streamingRules.txt from specified location
		createRulesMap();
		showRulesMap();
		createLatestValueMap();
		
		
		try {
			Receiver.receiveMessage(msgQueueName);
			System.out.println("Waiting for msgs");
			LoggingService.addLogs(applicationName, "gateway_create_rule_map", "Waiting for sensor data on queue:" + msgQueueName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public static void createLatestValueMap() {
		sensorValueMap = new HashMap<>();
	}
	
	/*
	 * Method to update sensor value in the map
	 * */
	public static void updateSensorValueMap(String sensorId, String newSensorValue) {
		System.out.println("Value inserted in map: " + sensorId + ":" + newSensorValue);
		LoggingService.addLogs(applicationName, "gateway_update_sensor_val", "Value inserted in map: " + sensorId + ":" + newSensorValue);
		sensorValueMap.put(sensorId, newSensorValue);
	}
	
	public static String getSensorValueMap(String sensorId) {
		System.out.println("Func called");
		return sensorValueMap.get(sensorId);
	}
	
	private static void showRulesMap() {
		for(int i = 0; i < ruleMapList.size(); i++) {
			for(Entry<String, List<String>> entry : ruleMapList.get(i).entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
		}
	}
	
	public static ArrayList<List<String>> getApplicableRules(String sensorID){
		List<List<String>> rulesForAllCompnents = new ArrayList<>();
		for(int i = 0; i < ruleMapList.size(); i++) {
			List<String> applicableRules = (ruleMapList.get(i)).get(sensorID);
			rulesForAllCompnents.add(applicableRules);
			}
		return (ArrayList<List<String>>) rulesForAllCompnents;
		}
	
}
