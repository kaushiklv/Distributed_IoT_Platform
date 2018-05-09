package sensorDataApi;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/getSensorData")
public class GetSensorData {
	
	@GET
	@Path("{sensorId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String receiveSensorDataRequest(@PathParam("sensorId") String sensorId) {
		
		System.out.println("Request received " + sensorId);
		
		LoggingService.addLogs(CreateRulesMap.applicationName, "gateway_get_sensor_data", "request received " + sensorId);
		
		String response = "";
		
		try {
			response = CreateRulesMap.getSensorValueMap(sensorId);
			
			System.out.println("Response Sent: " + response);
			LoggingService.addLogs(CreateRulesMap.applicationName, "gateway_send_sensor_value", "Response Sent: " + response);
			
			if(response == null || response.trim().isEmpty())
				return "No response";
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return "In catch";
		}
		
	}
	
	
	@GET
	@Path("startGatewayAgent/{app_name}/{messageQueueIp}/{messageQueueName}/{instanceIS}/{ftpIp}/{ftpUser}/{ftpPass}")
	@Produces(MediaType.TEXT_PLAIN)
	public String startGatewayAgent(@PathParam("app_name") String app_name, @PathParam("messageQueueIp") String messageQueueIp, @PathParam("messageQueueName") String messageQueueName, @PathParam("instanceIS") String instanceIS, @PathParam("ftpIp") String ftpIp, @PathParam("ftpUser") String ftpUser, @PathParam("ftpPass") String ftpPass) throws IOException, TimeoutException 
	{
		System.out.println("Req rec");
		String delimeter = "_";
		
		CreateRulesMap.applicationName = app_name;
		CreateRulesMap.msgQueueName = app_name + "_" + messageQueueName;
		CreateRulesMap.connectionIS = instanceIS;
		
		
		System.out.println(CreateRulesMap.msgQueueName);
		Receiver.msgQueueServerIP = messageQueueIp;
		
		
		CreateRulesMap.ftpIp = ftpIp;
		CreateRulesMap.ftpUser = ftpUser;
		CreateRulesMap.ftpPass = ftpPass;
		
		// Assign the message to be sent to monitoring to check IS_Event_Engine / LoadBalancer is active & Start Monitoring Service
		PingMonitorForResource.checkISEventEngine = app_name + delimeter + "is" + delimeter + "eventEngine" + delimeter + instanceIS;
		PingMonitorForResource.platformLoadBalancer = "platform_platform_Platform_LoadBalancer_instance1";
		
		LoggingService.addLogs(CreateRulesMap.applicationName, "Gateway Agent", "Started pinging the Monitoring service to check for IS & Platform Services");
		new Thread(new PingMonitorForResource()).start();
		
		
		
		LoggingService.addLogs(app_name, "gateway_start_gateway_agent", "Request to start gateway agent. MQ server: " + messageQueueName + ":" + messageQueueIp );
		
		// Sending heartbeat messages
		LoggingService.addLogs(CreateRulesMap.applicationName, "Gateway Agent", "Started Sending HeartBeat Messages to Monitor");
		HeartBeat heartBeat = new HeartBeat(CreateRulesMap.applicationName, messageQueueName, 
							messageQueueIp, "5672", 
							"admin", "admin", 
							"instance1");
		Thread heartBeatThread = new Thread(heartBeat);
		heartBeatThread.start();
		
		CreateRulesMap.main(null);

		MessageToUI.sendMessageToUi(messageQueueIp, "Streaming_Rules_Engine_Started!");
		return "started";
	}	
}
