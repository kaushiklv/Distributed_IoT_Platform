import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ServiceListener implements Runnable 
{

	String domainName;
	String serviceRequestQueue;
	String messageQueueServerIp;
	String messageQueueUser;
	String messageQueuePasswd;
	String messageQueuePort;
	
	
	public ServiceListener(String domainName, String serviceRequestQueue, String messageQueueServerIp,
			String messageQueueUser, String messageQueuePasswd, String messageQueuePort) {
		super();
		this.domainName = domainName;
		this.serviceRequestQueue = serviceRequestQueue;
		this.messageQueueServerIp = messageQueueServerIp;
		this.messageQueueUser = messageQueueUser;
		this.messageQueuePasswd = messageQueuePasswd;
		this.messageQueuePort = messageQueuePort;
	}




	@Override
	public void run() 
	{
		try
		{	
				//listen usage queue
				String queueName = domainName + "_" + serviceRequestQueue;
				//read msgQueueIp from ftp
				String msgQueueServerIP = messageQueueServerIp;
				String msgQueueServerUname = messageQueueUser;
				String msgQueueServerPass = messageQueuePasswd;
				int msgQueueServerPort = Integer.parseInt(messageQueuePort);
				
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
			          throws IOException 
			      {
			        
			    	  	String message = new String(body, "UTF-8");
			    	  	
			    	  	StringTokenizer st= new StringTokenizer(message, "$$");
			    	  	
			    	  	if(st.countTokens() != 3)
			    	  	{
			    	  		return;
			    	  	}
			   
			    	  		String appName = st.nextToken();
			    	  		String whatToRun = st.nextToken();
			    	  		String messageToSend = st.nextToken();
			    	  		String whatToRunForMonitor = "";
			    	  		
			    	  		String queueName = "";
			    	  		String key = "";
			    	  		if(whatToRun.contains("workflow"))
			    	  		{
			    	  			key = appName + "#" + "workflowEngine";
			    	  			whatToRunForMonitor = "workflowEngine";
			    	  			queueName = appName + "_" + domainName + "_" + "workflowEngine_";
			    	  		}
			    	  		else if(whatToRun.contains("event"))
			    	  		{
			    	  			key = appName + "#" + "eventEngine";
			    	  			whatToRunForMonitor = "eventEngine";
			    	  			queueName = appName + "_" + domainName + "_" + "eventEngine_";
			    	  		}
			    	  		else if(whatToRun.contains("rule"))
			    	  		{
			    	  			key = appName + "#" + "eventEngine";
			    	  			whatToRunForMonitor = "eventEngine";
			    	  			queueName = appName + "_" + domainName + "_" + "eventEngine_";
			    	  		}
			    	  		else if(whatToRun.contains(".jar"))
			    	  		{
			    	  			key = appName + "#" + whatToRun;
			    	  			whatToRunForMonitor = whatToRun;
			    	  			queueName = appName + "_" + domainName + "_" + whatToRun + "_";
			    	  		}
			    	  		else if(whatToRun.contains(".py"))
			    	  		{
			    	  			key = appName + "#" + whatToRun;
			    	  			whatToRunForMonitor = whatToRun;
			    	  			queueName = appName + "_" + domainName + "_" + whatToRun + "_";
			    	  		}
			    	  		else
			    	  		{
			    	  			LoggingService.addLogs("platform", "loadBalancer_service_request", "can not run message" + whatToRun);
			    	  			return;
			    	  		}
			    	  		
			    	  		
									
							Map<String,Map<String, String>> serviceNameMap = LBDataStructure.getCpuUsage();
							Map<String, String> serviceInstanceMap = serviceNameMap.get(key);
							Float lowestLoad = 999.0f;
							String lowest_instance = "";
							float lowestFactor = 999.0f;
							
							if(serviceInstanceMap == null)
							{
								LoggingService.addLogs("platform", "loadBalancer_service_request", "can not run message" + whatToRun);
			    	  			return;
							}
							
							
							
							
							for(String instance : serviceInstanceMap.keySet())
							{
								float usage = Float.parseFloat(serviceInstanceMap.get(instance));
								AMQP.Queue.DeclareOk dok = channel.queueDeclare(queueName + instance, false, false, false, null);
								int count = dok.getMessageCount();
								float factor = count * 0.2f + usage * 0.8f;
								
								if(factor < lowestFactor)
								{
									lowestLoad = usage;
									lowest_instance = instance;
									lowestFactor = factor;
								}	
							}
							
							
							
							String uiMessage = "FORWARDED_" + messageToSend.replaceAll("#", "_")  + "_TO_" + lowest_instance + "_WITH_CPU_USAGE_" + lowestLoad.toString().substring(0,6);
							
							  try {

									URL url = new URL("http://" + msgQueueServerIP + ":3000/loadBalancer/forwarding**" + uiMessage);
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
								
							
							
							
							
							if(lowestLoad > 0.5)
							{
								
								System.out.println("Load Balancer Upscaling Service !!!");
								 int x = Integer.parseInt(lowest_instance.substring(8));
								 x++;
								 String instance = "instance" +x;
						
								String domain = "platform";
								String messageQueueIp = msgQueueServerIP;
								int messageQueuePort = msgQueueServerPort;
								String messageQueueUsername = msgQueueServerUname;
								String messageQueuePassword = msgQueueServerPass;
								String instanceNumber = "";
								String mQueue = domain + "_" + "monitoringQueue";
								String messageForMonitor = "start" + appName + "##" + domain + "##localhost##" + whatToRunForMonitor + "##" + instance + "##dummy1##dummy2";
										
								//trigger put in service manager queue code
								try
								{
									ConnectionFactory factory = new ConnectionFactory();
									factory.setHost(messageQueueIp);
									factory.setPort((messageQueuePort));
									factory.setUsername(messageQueueUsername);
									factory.setPassword(messageQueuePassword);
									
									Connection connection = factory.newConnection();
									Channel channel = connection.createChannel();
									
									channel.queueDeclare(mQueue, false, false, false, null);
									channel.basicPublish("", mQueue, null, messageForMonitor.getBytes("UTF-8"));
									channel.close();
									connection.close();
									
									// TODO: To log
									LoggingService.addLogs(appName, "LoadBalancer_Triggering_Service_Manager", " [x] Sent '" + message + "'");
									System.out.println(" [x] Sent '" + message + "'");
									
									
									String uiMessage2 = "SCHEDULED_UPSCALING_" + whatToRunForMonitor + "_" + instance;
									
									
									
									try {

										URL url = new URL("http://" + msgQueueServerIP + ":3000/loadBalancer/scaling**" + uiMessage2);
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
								catch(Exception e)
								{
									LoggingService.addLogs(appName, "LoadBalancer_Triggering_Service_Manager", " failed : " + e.getMessage() + "'");
									System.out.println(e.getMessage());
							
								}
								

							}
								
							queueName += lowest_instance;
						    // put in appropriate service wrapper queue
							LoggingService.addLogs("platform", "loadBalancer_service_request", " adding message " + message + " to queue " + queueName);
				    	  	System.out.println("adding " + messageToSend + " to queue " + queueName);
							Connection connection;
							try 
							{
								connection = factory.newConnection();
								Channel channel = connection.createChannel();
								channel.queueDeclare(queueName , false, false, false, null);
								channel.basicPublish("", queueName, null, messageToSend.getBytes("UTF-8"));
							} 
							catch (TimeoutException e) 
							{
								LoggingService.addLogs("platform", "loadBalancer_service_request", "exception :" + e.getMessage());
				    	  		
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	  		
			    	  	
			    	  	
			      }
			    };
			    channel.basicConsume(queueName, true, consumer);
						
		}
		catch(Exception e)
		{
			System.out.println("error occured in CpuUsageTracker "+e.getMessage());
		}

		
	}

}
