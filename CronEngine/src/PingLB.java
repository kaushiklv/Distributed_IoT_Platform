import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

class PingLB implements Runnable
{
	String appName;
	String whoAmI;
	String messageQueueIp;
	String messageQueuePort;
	String messageQueueUsername;
	String messageQueuePassword;
	String instanceNumber;
	
	public PingLB(String appName, String whoAmI, String messageQueueIp, String messageQueuePort,
			String messageQueueUsername, String messageQueuePassword, String instanceNumber)
	{
		// Do something 
		this.appName = appName;
		this.whoAmI = whoAmI;
		this.messageQueueIp = messageQueueIp;
		this.messageQueuePort = messageQueuePort;
		this.messageQueueUsername = messageQueueUsername;
		this.messageQueuePassword = messageQueuePassword;
		this.instanceNumber = instanceNumber;
	}
	
	public void sendMessage(String message, String queueName) throws Exception{
		/*
		* TODO: Get the message server Queue IP & Port
		*/		
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(messageQueueIp);
		factory.setPort(Integer.parseInt(messageQueuePort));
		factory.setUsername(messageQueueUsername);
		factory.setPassword(messageQueuePassword);
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(queueName, false, false, false, null);
		channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
		
		// TODO: To log
		LoggingService.addLogs(appName, "CronEngine", " [x] Sent '" + message + "'");
		System.out.println(" [x] Sent '" + message + "'");
		
		channel.close();
		connection.close();
	}


	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		// Ping load balancer 
		String pingMessage = "";
		String delim = "##";
		String cpuUsageQueue = "";
		
		  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) 
		  {
			  method.setAccessible(true);
			  if (method.getName().startsWith("getSystemCpuLoad")&& Modifier.isPublic(method.getModifiers())) 
		  {
		      Object value;
		      while(true) 
		      {
		          try 
		          {
		            value = method.invoke(operatingSystemMXBean);
		            // send message to LB
		            System.out.println("Ping LB");
		            
		            pingMessage = appName + delim + whoAmI + delim + value + delim + instanceNumber;
		            cpuUsageQueue = "platform_cpuUsage";
		            
		            LoggingService.addLogs(appName, whoAmI + "Ping_LB", "CPU Usage Queue: " + cpuUsageQueue);
		            LoggingService.addLogs(appName, whoAmI + "Ping_LB", "CPU Usage: " + pingMessage);
		            sendMessage(pingMessage, cpuUsageQueue);
		            
		            Thread.sleep(1000 * 30); // sleep for 1000 ms 
		          } 
		          catch (Exception e) 
		          {
		            value = e;
		          } // try			        
		      }
		     //System.out.println(method.getName() + " = " + value);
		  } // if
	  } // for		
	}
	
}

