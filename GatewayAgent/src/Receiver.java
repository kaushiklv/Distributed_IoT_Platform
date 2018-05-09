package sensorDataApi;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Receiver implements Runnable {
	
	String message;
	static String msgQueueServerIP;
	static String msgQueueServerUname = "admin";
	static String msgQueueServerPass = "admin";
	static int msgQueueServerPort = 5672;
	static String messageToUi = "";

	
	// Each thread will receive a message on gateway queue & passes to rules engine
	public void run() {
		
		String sensorMessageSplitChar = "##";
		int splitIndex = this.message.indexOf(sensorMessageSplitChar);
		String sensorID = this.message.substring(0, splitIndex);
		String sensorValue = this.message.substring(splitIndex+2);
		
		/*
		 * Adding the sensor value to the latestValueMap
		 **/

		CreateRulesMap.updateSensorValueMap(sensorID, sensorValue);
		
		System.out.println("ID:" + sensorID );
		System.out.println("Val:" + sensorValue);
		
		
		ArrayList<List<String>> listOfRules = CreateRulesMap.getApplicableRules(sensorID);
		for(int i = 0; i < listOfRules.size(); i++) {
			List<String> applicableRule = listOfRules.get(i);
			if(applicableRule == null) {
				System.out.println("No rule applicable");
				continue;
			}
			if(i == 0) {
				// Rules for gateway
				messageToUi = "";
				executeAtGateway(applicableRule, sensorValue);
			}
			else if(i == 1) {
				// Rules applicable for IS
				
				// Check if IS is available to take messages
				String checkIs = "isEventEngine";				
				if(PingMonitorForResource.availableResources.get(checkIs).equalsIgnoreCase("1")) {
					executeAtIs(applicableRule, sensorValue);
				}
				else {
					messageToUi = "IS not available: Executing on GATEWAY";
					executeAtGateway(applicableRule, sensorValue);
				}
			}
			else {
				String checkLb = "platformLb";				
				if(PingMonitorForResource.availableResources.get(checkLb).equalsIgnoreCase("1")) {
					executeAtPlatform(applicableRule, sensorValue);
				}
				else {
					messageToUi = "Platform not Available: Executing on GATEWAY";
					executeAtGateway(applicableRule, sensorValue);
				}
			}
		}
	}
	
	public static void executeAtGateway(List<String> applicableRule, String sensorValue) {
		for(String rule : applicableRule) {
			try {
				// Add the received queue data message to RuleEngineQueue
				String RuleEngineQueue = CreateRulesMap.msgQueueName + "_eventEngine_instance1";
				
				String messagePrefix = rule.substring(0, rule.indexOf("-"));
				String messageSuffix = rule.substring(rule.indexOf("-"));
				String messageToSend = messagePrefix + "#" + sensorValue + messageSuffix; 
				
				Sender.sendMessage(messageToSend, RuleEngineQueue, msgQueueServerIP);
				System.out.println(messageToSend + " sent to:" + RuleEngineQueue);
				

				messageToUi += messageToSend + " sent to:" + RuleEngineQueue + "at Gateway";
				MessageToUI.sendMessageToUi(Receiver.msgQueueServerIP, messageToUi);
				
				LoggingService.addLogs(CreateRulesMap.applicationName, "receive_sensor_data", messageToSend + " sent to:" + RuleEngineQueue);
				messageToUi = "";
				} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void executeAtIs(List<String> applicableRule, String sensorValue) {
		for(String rule : applicableRule) {
			try {
				// Add the received queue data message to RuleEngineQueue
				String RuleEngineQueue = CreateRulesMap.applicationName + "_"+ CreateRulesMap.connectionIS + "_eventEngine_instance1";
				
				String messagePrefix = rule.substring(0, rule.indexOf("-"));
				String messageSuffix = rule.substring(rule.indexOf("-"));
				String messageToSend = messagePrefix + "#" + sensorValue + messageSuffix; 
				
				Sender.sendMessage(messageToSend, RuleEngineQueue, msgQueueServerIP);
				System.out.println(messageToSend + " sent to:" + RuleEngineQueue);
				
				messageToUi += messageToSend + " sent to:" + RuleEngineQueue + "at IS";
				MessageToUI.sendMessageToUi(Receiver.msgQueueServerIP, messageToUi);
				
				LoggingService.addLogs(CreateRulesMap.applicationName, "receive_sensor_data", messageToSend + " sent to:" + RuleEngineQueue);
				
				messageToUi = "";
				} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void executeAtPlatform(List<String> applicableRule, String sensorValue) {
		// Rules applicable for platform (In this case, send the message to load balancer queue)
		for(String rule : applicableRule) {
			try {
				// Add the received queue data message to RuleEngineQueue
				String RuleEngineQueue = "platform_loadBalancer";
				
				String messagePrefix = rule.substring(0, rule.indexOf("-"));
				String messageSuffix = rule.substring(rule.indexOf("-"));
				
				// Checking whether event or rule so as to inform the LB accordingly
				String ruleOrEvent = messageSuffix.substring(1,messageSuffix.indexOf("#"));
				
				
				// Of the type: "event#sensor#Honeywell123#15-event#1"
				String messageToSend = CreateRulesMap.applicationName + "$$" + ruleOrEvent + "$$" + messagePrefix + "#" + sensorValue + messageSuffix; 
				
				Sender.sendMessage(messageToSend, RuleEngineQueue, msgQueueServerIP);
				System.out.println(messageToSend + " sent to:" + RuleEngineQueue);
				
				messageToUi += messageToSend + " sent to:" + RuleEngineQueue + "at Platform";
				MessageToUI.sendMessageToUi(Receiver.msgQueueServerIP, messageToUi);
				messageToUi = "";
				
				LoggingService.addLogs(CreateRulesMap.applicationName, "receive_sensor_data", messageToSend + " sent to:" + RuleEngineQueue);
				} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	Receiver(String message){
		this.message = message;
	}
	
	public static void receiveMessage(String queueName) throws Exception {
		/*
		 * TODO: Get the message server Queue IP & Port
		 */
				
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(msgQueueServerIP);
	    factory.setPort(msgQueueServerPort);
	    factory.setUsername(msgQueueServerUname);
	    factory.setPassword(msgQueueServerPass);
	    
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	
	    channel.queueDeclare(queueName, false, false, false, null);
	    
	    
	    Consumer consumer = new DefaultConsumer(channel) {
	      @Override
	      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	          throws IOException {
	        
	    	  String message = new String(body, "UTF-8");
	    	  
	    	  // Creating a new thread to handle the incoming message
	    	  new Thread(new Receiver(message)).start();
	    	  
	    	  // TODO: Log message
	    	  System.out.println(" [x] Received '" + message + "'");
	      }
	    };
	    channel.basicConsume(queueName, true, consumer);
  }
	
	public static void main(String[] args) throws IOException{
		String queueName = "gatewayQueue";
		try {
			receiveMessage(queueName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}