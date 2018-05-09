
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;



public class HeartBeat implements Runnable{
	
	String messageQueueIp;
	String messageQueuePort;
	String messageQueueUsername;
	String messageQueuePassword;
	String serviceManangerQueue;
	
	HeartBeat(String messageQueueIp,String messageQueuePort,String messageQueueUsername,String messageQueuePassword,String serviceManangerQueue){
		this.messageQueueIp = messageQueueIp;
		this.messageQueuePort =messageQueuePort;
		this.messageQueueUsername = messageQueueUsername;
		this.messageQueuePassword = messageQueuePassword;
		this.serviceManangerQueue = serviceManangerQueue;
	}
	
	public String startWorkflowWrapper(String[] restartMessageArray) {
		String workflowEngineJar = "workflowEngine.jar";
		String workflowClass = "WfEngine";
		String whereToRun = restartMessageArray[1];
		String appName = restartMessageArray[0];
		String instanceCount = restartMessageArray[4];
		

		String command = "/home/vagrant/jre/bin/java -jar" + " " + "workflowWrapper.jar" + " " + appName + " " + whereToRun + " "
				+ "workflowEngine" + " " + instanceCount + " " + messageQueueIp + " " + messageQueueUsername + " "
				+ messageQueuePassword + " " + messageQueuePort + "  " + workflowEngineJar + " " + workflowClass;

		return command; 
	}
	
	public String startCRONEngine(String[] restartMessageArray){
		// cron_engine.jar app1 gateway localhost 5672 admin admin ./cron.xml
		// platform_loadBalancer_instance#";
		// whereToRun can only be one of gateway, intermediateServer or centralServer
		String cronFile;
		String whereToRun = restartMessageArray[1];
		String appName = restartMessageArray[0];
		String instanceCount = restartMessageArray[4];
		
		if(whereToRun.toLowerCase().startsWith("gateway")) {
			cronFile = "/cron_gateway.xml";
		}
		else if(whereToRun.toLowerCase().startsWith("is")) {
			cronFile = "/cron_is.xml";
		}
		else {
			cronFile = "/cron_cs.xml";
		}

		String cronFileFullPath = "./" + appName + cronFile;
		String command = "/home/vagrant/jre/bin/java -jar" + " " + "CronEngine.jar" + " " + appName + " " + whereToRun + " " + messageQueueIp
				+ " " + messageQueuePort + "  " + messageQueueUsername + " " + messageQueuePassword + " " + cronFileFullPath
				+ " " + "platform_loadBalancer" + " " + instanceCount;
		return command;
	}
	
	public String startEventEngine(String[] restartMessageArray) {
		String whereToRun = restartMessageArray[1];
		String appName = restartMessageArray[0];
		String instanceCount = restartMessageArray[4];
		
		String command = "/home/vagrant/jre/bin/java -jar" + " " + "eventEngineWrapper.jar" + " " + appName + " " + whereToRun + " "
				+ "eventEngine" + " " + instanceCount + " " + messageQueueIp + " " + messageQueueUsername + " "
				+ messageQueuePassword + " " + messageQueuePort + "  " + "eventEngine.jar" + " " + "StreamingRulesHandler1";
		return command;
	}
	
	
	private String startSensorDataApi() {
		String command = "bash startTomcat.sh";

		return command;
	}
	
	private String startAppServices(String[] restartMessageArray) {
		String whereToRun = restartMessageArray[1];
		String appName = restartMessageArray[0];
		String instanceCount = restartMessageArray[4];
		
		String command = null;

		
		String[] comps = restartMessageArray[3].split("\\.");
		if(comps[1].equalsIgnoreCase("jar")) {
			// TODO: parameters needed? sensorData servicePathName
			// "java -jar __ appResourceSavePath"


			command = "/home/vagrant/jre/bin/java -jar " + "serviceWrapper.jar" + " " + appName + " " + whereToRun + " " + "appServiceJar"
					+ " " + instanceCount + " " + messageQueueIp + " " + messageQueueUsername + " " + messageQueuePassword
					+ " " + messageQueuePort + " " + restartMessageArray[3] + " " + comps[0];
		} else {

			command = "/home/vagrant/jre/bin/java -jar " + "pythonServiceWrapper.jar" + " " + appName + " " + whereToRun + " " + "appServicePython"
					+ " " + instanceCount + " " + messageQueueIp + " " + messageQueueUsername + " " + messageQueuePassword
					+ " " + messageQueuePort + " " + restartMessageArray[3] + " " + restartMessageArray[3];
		}

		return command;
	}
	
	
	public void run(){
		while(true){
			try{
			// this service will run in a time interval
			Thread.sleep(60000);
			 } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
			// get current time
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Long time = (Long)(timestamp.getTime());
			
			ConcurrentHashMap<String,Long > monitoringMap = ReadQueue.myMap;
         //	System.out.println("Line 24: "+monitoringMap.size());
			
			if(monitoringMap == null || monitoringMap.isEmpty()) {
				continue;
			}
			
			
			for(Entry<String, Long> entry: monitoringMap.entrySet()) {
				if(entry.getValue() < time) {
					SendToMq mqObject;
					try{
						mqObject = new SendToMq();
						try {
							String restartMessage = entry.getKey();
							String splittedRestartMessage[] = restartMessage.split("##");
							String msgToUi = splittedRestartMessage[3] + "_" + splittedRestartMessage[4] + "_Dead"; 
							System.out.println("restart the service: " + msgToUi);
							//serviceDeadMsgToUi(entry.getKey());
							String invocationCommand = formRestartServiceCommand(restartMessage);
							LoggingService.addLogs("platform", "monitoring", invocationCommand);
							System.out.println("Message sent to Service Manager: " + invocationCommand);
							mqObject.sendMsg(invocationCommand,serviceManangerQueue,messageQueueIp,messageQueuePort,messageQueueUsername,messageQueuePassword);
						}
						catch(Exception e) {
							System.out.println("Message queue exception: "+ e);
						}							
						
					}
					catch(Exception e){
						System.out.println(e);
					}
				}
				else {
					String restartMessage = entry.getKey();
					String splittedRestartMessage[] = restartMessage.split("##");
					String msgToUi = splittedRestartMessage[3] + "_" + splittedRestartMessage[4] + "_Dead";
					//serviceAliveMsgToUi(msgToUi);
					
					System.out.println("upto date services: " + entry.getKey());
				}
			}
		}		 
	}


	private void serviceAliveMsgToUi(String key) {
		// TODO Auto-generated method stub
		try {
			//	URL url = new URL("http://192.168.1.106:3000/loadBalancer/cpuUsage**latest5");
				URL url = new URL("http://192.168.1.106:3000/monitor/alive**"+key);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "text/plain");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

			

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }
	}

	private void serviceDeadMsgToUi(String key) {
		try {
			//	URL url = new URL("http://192.168.1.106:3000/loadBalancer/cpuUsage**latest5");
				URL url = new URL("http://192.168.1.106:3000/monitor/dead**"+key);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "text/plain");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

			

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }

		
		
		// TODO Auto-generated method stub
		
	}

	public String formRestartServiceCommand(String restartMessage) {
		String[] restartMessageArray = restartMessage.split("##");
		StringBuffer outputMessage = new StringBuffer();
		String command = null;
		
		if(restartMessageArray[1].toLowerCase().startsWith("gateway")){
			outputMessage.append("gateway##");
		}else if(restartMessageArray[1].toLowerCase().startsWith("is")){
			outputMessage.append("is##");
		}else {
			outputMessage.append("platform##");
		}
		
		outputMessage.append(restartMessageArray[2]).append("##"); //IP Address
		outputMessage.append(restartMessageArray[5]).append("##"); //Username
		outputMessage.append(restartMessageArray[6]).append("##"); //password
		
		if(restartMessageArray[3].equalsIgnoreCase("workflowEngine")) {
			command = startWorkflowWrapper(restartMessageArray);
		}else if(restartMessageArray[3].equalsIgnoreCase("streaming_rules_gateway_heartbeat")) {
			command = startSensorDataApi();
		}else if(restartMessageArray[3].equalsIgnoreCase("CronEngine")) {
			command = startCRONEngine(restartMessageArray);
		}else if(restartMessageArray[3].equalsIgnoreCase("eventEngine")){
			command = startEventEngine(restartMessageArray);
		}else if(restartMessageArray[3].equalsIgnoreCase("Platform_LoadBalancer")){
			command = startLoadBalancer(restartMessageArray);
		}else {
			command = startAppServices(restartMessageArray);
		}
		outputMessage.append(command).append("##").append(restartMessageArray[0]);
		if(restartMessageArray[0].startsWith("start")) {
			
			SendToMq mqObj = new SendToMq();
			try {
				System.out.println("restart the service: " +outputMessage);
				LoggingService.addLogs("platform", "monitoring", outputMessage.toString());
				System.out.println("Message sent to Service Manager: " + outputMessage);
				mqObj.sendMsg(outputMessage.toString(),serviceManangerQueue,messageQueueIp,messageQueuePort,messageQueueUsername,messageQueuePassword);
			}
			catch(Exception e) {
				System.out.println("Message queue exception: "+ e);
			}							
		}
		return outputMessage.toString();
	}

	private String startLoadBalancer(String[] restartMessageArray) {
		
		String command = "/home/vagrant/jre/bin/java -jar" + " " + "loadBalancer.jar" + " " + "platform" + 
				"cpuUsage" + messageQueueIp + " " + messageQueueUsername + " "
				+ messageQueuePassword + " " + messageQueuePort + "  " + "loadBalancer" + " " + "instance1";
		return command;
	}
}