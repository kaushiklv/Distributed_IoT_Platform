import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import xmlParser.CronData;
import xmlParser.CronParser;
import xmlParser.Gateway;
import xmlParser.Sensor;
import xmlParser.Service;
import xmlParser.ServiceData;
import xmlParser.ServiceParser;
import xmlParser.Task;
import xmlParser.TopologyData;
import xmlParser.TopologyParser;


public class CronEngine {
	
	public static TopologyData staticTopoData = null;
	public static void checkModifiedTopology(String filePath)
	
	{
		String jarPath = filePath;
		Path myDir = Paths.get(jarPath);  
		
        try {
        	boolean valid = true;
        	do {
	           WatchService watcher = myDir.getFileSystem().newWatchService();
	           myDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
	
	           WatchKey watckKey = watcher.take();
	
	           List<WatchEvent<?>> events = watckKey.pollEvents();
	           for (WatchEvent<?> event : events) {
	                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) 
	                {	                	
	                	  System.out.println("Created: " + event.context().toString());
		                  staticTopoData = new TopologyParser().getTopologyData(filePath + "/topology.xml");
	                }
	           }
	           valid = watckKey.reset();
        	}while(valid);
           
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }		
	}
	
	public void sendMessage(String appName, String message, String queueName, 
							String msgQueueServerIP, String msgQueueServerUname,
							String msgQueueServerPass, String msgQueueServerPort) throws Exception{
		/*
		 * TODO: Get the message server Queue IP & Port
		 */		
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(msgQueueServerIP);
	    factory.setPort(Integer.parseInt(msgQueueServerPort));
	    factory.setUsername(msgQueueServerUname);
	    factory.setPassword(msgQueueServerPass);
	    
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(queueName, false, false, false, null);
	    channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
	    
	    // TODO: To log
	    LoggingService.addLogs(appName, "CronEngine", " QueueName '" + queueName + "'");
	    LoggingService.addLogs(appName, "CronEngine", " [x] Sent '" + message + "'");
	    System.out.println(" [x] Sent '" + message + "'");

	    channel.close();
	    connection.close();
	}
	
	public void sendMessageToUi(String appName, String domain, String message) {
		
		try {
			URL url = new URL("http://192.168.1.106:3000/engines/cronEngine**" + message);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "text/plain");
	
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			conn.disconnect();				    					    				  
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		} 
	}
	
	private String getSensorDataFromGateway(String gatewayIp , String sensorId)
	{
		String output="no response";
		try {
			// http://10.2.132.176:8080/sensorDataApi/getSensorData/SensorID			  
		    String sendingUrl="http://" + gatewayIp + "/sensorDataApi/getSensorData/" + sensorId; 
		    System.out.println(sendingUrl + " URL");
		    URL url = new URL(sendingUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "text/plain");

			if (conn.getResponseCode() != 200) 
			{
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			System.out.println("Output from Server .... \n");
			output = br.readLine() ; 
			System.out.println(output);
			conn.disconnect();
		  }
		  catch (MalformedURLException e) {
			  System.out.println("Error in rest call...getSensorDataFromGateway");
		  }
		  catch (IOException e) {
			  System.out.println("Error in rest call ..IO exception");
		  }
		  return output;
	}

	public static void main(String[] args) throws InterruptedException,Exception {
		// TODO Auto-generated method stub
		// args[0] = appName
		// args[1] = gateway/is/pf
		// args[2] = message queue ip
		// args[3] = message queue port
		// args[4] = message queue username
		// args[5] = message queue password
	    // args[6] = xml file location
		// args[7] = loadbalancer queuename
		String appName = args[0];
		LoggingService.addLogs(appName, "CronEngine", "CRON Engine Started!!! My Wish is your Command");
		
		if(args.length < 8)
		{
			LoggingService.addLogs(appName, "CronEngine", "insufficient number of arguments");
			System.out.println("insufficient number of arguments");
			return;
		}		
		
		String domain = args[1];
		String messageQueueIp = args[2];
		String messageQueuePort = args[3];
		String messageQueueUsername = args[4];
		String messageQueuePassword = args[5];
		String xmlLocation = args[6];
		final String separator = "_";
		String loadBalancerQueueName = args[7];
		String instanceNumber = args[8];
				
		LoggingService.addLogs(appName, "CronEngine", "Started Sending HeartBeat Messages to Monitor");
		HeartBeat heartBeat = new HeartBeat(appName, domain,  
											messageQueueIp, messageQueuePort, 
											messageQueueUsername, messageQueuePassword, 
											instanceNumber);
		Thread heartBeatThread = new Thread(heartBeat);
		heartBeatThread.start();
				
		String queueName = "";
		String whoAmI = "CronEngine";
		if(!domain.equals("platform"))
			queueName = appName + separator + domain;
		else {
			queueName = loadBalancerQueueName;
//			LoggingService.addLogs(appName, whoAmI, "Started Sending HeartBeat Messages to LoadBalancer");
//			PingLB pingUsage = new PingLB(appName, whoAmI, messageQueueIp, messageQueuePort, 
//								 				messageQueueUsername, messageQueuePassword, 
//												instanceNumber);
//			Thread pingLBThread = new Thread(pingUsage);
//			pingLBThread.start();
		}
		
		if(staticTopoData == null) {
			staticTopoData = new TopologyParser().getTopologyData("./" + appName + "/topology.xml");
		}
		
		LoggingService.addLogs(appName, whoAmI, "Started thread to check if topology file changed");
		CheckTopology check = new CheckTopology(appName);
		Thread checkTopoThread = new Thread(check);
		checkTopoThread.start();
		
			
		String xmlPath = xmlLocation;
		CronParser cronParser = new CronParser();
		CronData cronData = cronParser.getCronData(xmlPath);
		
		Map<Task, Date> scheduledJobs = new HashMap<>();
		
		for(int i = 0; i < cronData.cron.tasks.size(); i++) 
		{
			String cronTime = cronData.cron.tasks.get(i).getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	        Date date = new Date();
	        String currentDay = dateFormat.format(date);
	        currentDay +=" " +  cronTime;
	        Date date1=new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(currentDay);
	        
	        System.out.println(new Date()); 
	        
	        if(new Date().getTime() > date1.getTime())
	        {
	           	Calendar cal = Calendar.getInstance(); // creates calendar
		        cal.setTime(date1); // sets calendar time/date
		        cal.add(Calendar.HOUR_OF_DAY, 24); // adds one hour
		        Date date2 = cal.getTime();
		        scheduledJobs.put(cronData.cron.tasks.get(i), date2); // returns new date object, one hour in the future
		
	        }
	        else
	        {
	        	 System.out.println(date1);
			     scheduledJobs.put(cronData.cron.tasks.get(i), date1);	
	        }
		 }
		
		while(true) 
		{
			String serviceQueueName = queueName + separator;
			String eventQueueName = queueName + separator;
			String workflowQueueName = queueName + separator;
			String delim = "#";
			
			for(Task task : scheduledJobs.keySet()) 
			{
				Date currentDate = new Date();
				Date scheduledTime = scheduledJobs.get(task);
				
				try 
				{
					CronEngine engine = new CronEngine();
					long elapsed = scheduledTime.getTime() - currentDate.getTime(); 
				    elapsed /= 60000;
				    System.out.println(elapsed);
				    String alphaNumerals = "^[a-zA-Z0-9]*$";
				    if(elapsed > -5 && elapsed < 5) 
				    {		
				    	TopologyParser topoParser = null;
		    			TopologyData topoData = null;
		    			
				    	if(task.getEventId().matches(alphaNumerals)) 
				    	{	
				    		String eventMessage = "";
				    		
				    		topoParser = new TopologyParser();
				    		if(staticTopoData == null)
				    		{
				    			System.out.println("first time read");
				    			topoData = topoParser.getTopologyData(appName + "/topology.xml");
				    			staticTopoData = topoData;
				    		}
				    		else
				    		{
				    			System.out.println("reading already exisiting map");
				    			topoData = staticTopoData;
				    		}
				    		
		    				List<Gateway> gatewayList= topoData.topo.getGateways();
		    							    			
				    		for(int j = 0; j < task.getSensors().size(); j++) 
				    		{
				    			if(domain.equals("platform")) {
					    			eventMessage = appName + "$$" + "event" + "$$";
					    			eventMessage += "sensor" + delim;
					    			eventMessage += task.getSensors().get(j).getSensorId() + delim;
				    			}
					    		else {
					    			eventMessage = "sensor" + delim;
					    			eventMessage += task.getSensors().get(j).getSensorId() + delim;					    			
					    		}
				    			
				    			String sensor_id = task.getSensors().get(j).getSensorId();
				    			String sensorDataVal="no response";
				    			String gatewayAddr = "";
				    			String messageForUI = "";
				    			try {				    				
				    				for(int i = 0; i < gatewayList.size(); i++)
				    				{
				    					Gateway gatewayObj = gatewayList.get(i);
				    					List<Sensor> listOfSensor=gatewayObj.getSensors();
				    					for(int k = 0; k < listOfSensor.size(); k++)
				    					{
				    						if(listOfSensor.get(k).getSensorId().equals(sensor_id))
				    						{
				    							gatewayAddr = gatewayObj.getIP() + ":" + gatewayObj.getPort();
				    							System.out.println("Gateway is " + gatewayAddr + " sensorId is " + sensor_id);
				    							sensorDataVal = engine.getSensorDataFromGateway(gatewayAddr, sensor_id);
				    							
				    							if(sensorDataVal.equals("no response"))
				    							{
				    								System.out.println(" No data found for "+sensor_id);				    								
				    							}
				    							else {
				    								eventMessage += sensorDataVal + "-" + "event" + delim + task.getEventId();
				    								
				    								if(!domain.equals("platform"))
				    					    			eventQueueName = eventQueueName + "eventEngine" + separator + instanceNumber;
				    					    		else
				    					    			eventQueueName = loadBalancerQueueName;
				    					    		
				    					    		LoggingService.addLogs(appName, "CronEngine", "EventMessage " + eventMessage);
				    					    		System.out.println("EventQueue: " + eventQueueName);
				    					    		System.out.println("EventMessage: " + eventMessage);
				    					    		try {
				    					    			engine.sendMessage(appName, eventMessage, eventQueueName, messageQueueIp, messageQueueUsername,
				    					    							   messageQueuePassword, messageQueuePort);
				    					    			eventQueueName = queueName + separator;
		    					    					
				    					    			messageForUI = appName + "_" + domain + "_" + "Event_No._" + task.getEventId() + "_Executing_at_" + task.getTime();
				    					    			
				    					    			engine.sendMessageToUi(appName, domain,messageForUI);
				    					    						    					    				  
				    						    	}
				    					    		catch(Exception e)
				    					    		{
				    					    			LoggingService.addLogs(appName, "CronEngine", "EventMessage " + "Message Queue Server Down");
				    					    			System.out.println(e.getMessage());
				    					    			System.out.println("Message Queue Server Down");
				    					    		} 
				    							}
				    						}
				    					}
				    				}
				    			}
				    			catch (Exception e)
				    			{
				    				System.out.println("Error in fetching data (Fetch sensor data )");
				    			}
				    		}
				    	}				    		
				    	if(task.getServiceId().matches(alphaNumerals)) 
				    	{
				    		String serviceMessage = "";
				    		String serviceJarName = "";
				    		ServiceParser serviceParser = new ServiceParser();
				    		ServiceData serviceData = serviceParser.getServiceData(appName + "/service.xml");
				    		String messageForUI = "";
				    		
				    		topoParser = new TopologyParser();
				    		if(staticTopoData == null)
				    		{
				    			System.out.println("first time read");
				    			topoData = topoParser.getTopologyData(appName + "/topology.xml");
				    			staticTopoData = topoData;
				    		}
				    		else
				    		{
				    			System.out.println("reading already exisiting map");
				    			topoData = staticTopoData;
				    		}
		    				
		    				List<Gateway> gatewayList= topoData.topo.getGateways();
		    				
				    		List<Service> serviceList = serviceData.appservice.getServices();
				    						    		
				    		for(Service ser : serviceList) {
				    			if(ser.getServiceId().equals(task.getServiceId())) {
				    				serviceJarName = ser.getServiceJarName();
				    			}
				    		}
				    		
				    		for (int j = 0; j < task.getSensors().size(); j++) {
				    			
				    			String sensor_id = task.getSensors().get(j).getSensorId();
				    			String sensorDataVal="no response";
				    			String gatewayAddr = "";
				    			try {				    				
				    				for(int i = 0; i < gatewayList.size(); i++)
				    				{
				    					Gateway gatewayObj = gatewayList.get(i);
				    					List<Sensor> listOfSensor=gatewayObj.getSensors();
				    					for(int k = 0; k < listOfSensor.size(); k++)
				    					{
				    						if(listOfSensor.get(k).getSensorId().equals(sensor_id))
				    						{
				    							gatewayAddr = gatewayObj.getIP() + ":" + gatewayObj.getPort();
				    							System.out.println("Gateway is " + gatewayAddr + " sensorId is " + sensor_id);
				    							sensorDataVal = engine.getSensorDataFromGateway(gatewayAddr, sensor_id);
				    							
				    							if(sensorDataVal.equals("no response"))
				    							{
				    								System.out.println(" No data found for "+sensor_id);				    								
				    							}
				    							else {				    			
				    								if(domain.equals("platform"))
				    									serviceMessage = appName + "$$" + serviceJarName + "$$";
				    								
				    								if(serviceJarName.endsWith("py"))
				    									serviceMessage += serviceJarName + " " + sensorDataVal;
				    								else
				    									serviceMessage += sensorDataVal;
				    								
				    								if(!domain.equals("platform"))
				    					    			serviceQueueName += serviceJarName + separator + instanceNumber;
				    					    		else {
				    					    			serviceQueueName = loadBalancerQueueName;
				    					    		}
				    					    		
				    					    		LoggingService.addLogs(appName, "CronEngine", "ServiceMessage " + serviceMessage);
				    					    		LoggingService.addLogs(appName, "CronEngine", "ServiceQueue " + serviceQueueName);
				    					    		System.out.println("ServiceQueue:" + serviceQueueName);
				    					    		System.out.println("ServiceMessage: " + serviceMessage);
				    					    		try {
				    					    			engine.sendMessage(appName, serviceMessage, serviceQueueName, messageQueueIp, messageQueueUsername,
				    					    							   messageQueuePassword, messageQueuePort);
				    					    			serviceQueueName = queueName + separator;
				    					    			
				    					    			messageForUI = appName + "_" + domain + "_Service_" + serviceJarName + "_Executing_at_" + task.getTime();
				    					    			
				    					    			engine.sendMessageToUi(appName, domain, messageForUI);
				    					    			
				    					    		}
				    					    		catch(Exception e)
				    					    		{
				    					    			LoggingService.addLogs(appName, "CronEngine", "ServiceMessage " + "Message Queue Server Down");
				    					    			System.out.println("Message Queue Server Down");
				    					    		}
				    							}
				    						}
				    					}
				    				}
				    			}
				    			catch (Exception e)
				    			{
				    				System.out.println("Error in fetching data (Fetch sensor data )");
				    			}
				    		}
				    	}	
				    	if(task.getWorkflowId().matches(alphaNumerals)) 
				    	{
				    		String workflowMessage = "";
				    		String messageForUI = "";
				    		
				    		if(!domain.equals("platform")) { 
				    			workflowQueueName += "workflowEngine" + separator + instanceNumber;
				    		}
				    		else {
				    			workflowMessage = appName + "$$" + "workflow" + "$$";
				    			workflowQueueName = loadBalancerQueueName;				    			
				    		}
				    			
				    		workflowMessage += task.getWorkflowId() + ".wf";
				    		try 
				    		{
				    			System.out.println("WorkflowQueueName: " + workflowQueueName);
					    		engine.sendMessage(appName, workflowMessage, workflowQueueName, messageQueueIp, messageQueueUsername,
		    							   messageQueuePassword, messageQueuePort);
					    		if(domain.equals("platform"))
					    			workflowQueueName = loadBalancerQueueName;
					    		else
					    			workflowQueueName = queueName + separator;
					    		
					    		messageForUI = appName + "_" + domain + "_Workflow_" + task.getWorkflowId() + "_Executing_at_" + task.getTime();
				    			
					    		engine.sendMessageToUi(appName, domain, messageForUI);				    			
				    		}
				    		catch(Exception e)
				    		{
				    			LoggingService.addLogs(appName, "CronEngine", "EventMessage " + "Message Queue Server Down");
				    			System.out.println("Message Queue Server Down");
				    		}
				    		LoggingService.addLogs(appName, "CronEngine", "workflowMessage " + workflowMessage);
				    		LoggingService.addLogs(appName, "CronEngine", "workflowQueue " + workflowQueueName);
				    		System.out.println("workflowMessage " + workflowMessage);
				    	} //if workflow
			    	
				    	Calendar cal = Calendar.getInstance(); // creates calendar
				        cal.setTime(scheduledTime); // sets calendar time/date
				        cal.add(Calendar.HOUR_OF_DAY, 24); // adds one hour
				        scheduledJobs.put(task, cal.getTime()); // returns new date object, one hour in the future
				    } // if
				} // try
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // for
			Thread.sleep(60000 * 3);
		} // while(true)
	}
}
