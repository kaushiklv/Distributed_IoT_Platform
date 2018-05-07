import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class CpuUsageTracker implements Runnable {

	String domainName;
	String cpuUsageQueue;
	String messageQueueServerIp;
	String messageQueueUser;
	String messageQueuePasswd;
	String messageQueuePort;

	/**
	 * 
	 * @param domainName
	 * @param queueName
	 */
	public CpuUsageTracker(String domainName, String cpuUsageQueue, String messageQueueServerIp,
			String messageQueueUser, String messageQueuePasswd, String messageQueuePort) {
		super();
		this.domainName = domainName;
		this.cpuUsageQueue = cpuUsageQueue;
		this.messageQueueServerIp = messageQueueServerIp;
		this.messageQueueUser = messageQueueUser;
		this.messageQueuePasswd = messageQueuePasswd;
		this.messageQueuePort = messageQueuePort;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// listen usage queue
			String queueName = domainName + "_" + cpuUsageQueue;
			// read msgQueueIp from ftp
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
			System.out.println("started listening on queue " +  queueName);
			LoggingService.addLogs("platform", "loadBalancer", "started listening on queue ");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					String message = new String(body, "UTF-8");
					
					System.out.println("received message " +  message);
					LoggingService.addLogs("platform", "loadBalancer_cpu_usage", "received message " +  message);
					
					

					String[] temp = message.split("##");
					if(temp.length == 4)
					{
						String appName = temp[0];
						String serviceName = temp[1];
						String cpuUsage = temp[2];
						String instanceNumber = temp[3];
						
						// decode message

						// decode message

						// get serviceName, serviceInstance
						
						// map of appName#serviceName -> instance number -> cpuUsage
						
						Map<String, Map<String, String>> serviceNameMap = LBDataStructure.getCpuUsage();
						Map<String, String> serviceInstanceMap = serviceNameMap.get(appName + "#" + serviceName);

						if (serviceInstanceMap == null) {
							serviceInstanceMap = new HashMap<>();
						}

						serviceInstanceMap.put(instanceNumber, cpuUsage);
						serviceNameMap.put(appName + "#" + serviceName, serviceInstanceMap);
						LBDataStructure.setCpuUsage(serviceNameMap);
						
						
						String uiMessage = "" + appName + "_" + serviceName + "_" + instanceNumber + "_" + cpuUsage.substring(0,6);
						
						  try {

								URL url = new URL("http://" + msgQueueServerIP + ":3000/loadBalancer/cpuUsage**" + uiMessage);
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
					
				}
			};
			channel.basicConsume(queueName, true, consumer);

		} catch (Exception e) 
		{
			System.out.println("error occured in CpuUsageTracker " + e.getMessage());
			LoggingService.addLogs("platform", "loadBalancer_cpu_usage", "erorr listening in cpuUsage queue" +  e.getMessage());
			
		}

	}

}
