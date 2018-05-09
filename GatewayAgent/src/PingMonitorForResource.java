package sensorDataApi;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


public class PingMonitorForResource implements Runnable{

  private Connection connection;
  private Channel channel;
  private String requestQueueName;
  private String replyQueueName;
  
  static String checkISEventEngine;
  static String platformLoadBalancer;
  
  public static Map<String, String> availableResources = new HashMap<>();

  public PingMonitorForResource() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(Receiver.msgQueueServerIP);
    factory.setPort(Receiver.msgQueueServerPort);
    factory.setUsername(Receiver.msgQueueServerUname);
    factory.setPassword(Receiver.msgQueueServerPass);

    requestQueueName = "monitoringGatewayCheckResourceAlive";
    connection = factory.newConnection();
    channel = connection.createChannel();
    
    channel.queueDeclare(requestQueueName, false, false, false, null);
    
    replyQueueName = channel.queueDeclare().getQueue();
  }

  public String call(String message) throws IOException, InterruptedException {
    final String corrId = UUID.randomUUID().toString();

    AMQP.BasicProperties props = new AMQP.BasicProperties
            .Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .build();

    channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
    
    System.out.println("Here: " + message);
    final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

    channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (properties.getCorrelationId().equals(corrId)) {
          response.offer(new String(body, "UTF-8"));
        }
      }
    });

    return response.take();
  }

  public void close() throws IOException {
    connection.close();
  }

@Override
public void run() {
		
	PingMonitorForResource pingMonitor1 = null;
	PingMonitorForResource pingMonitor2 = null;
	  while(true) {
		  try {		  
			  String response = null;
			  
			  pingMonitor1 = new PingMonitorForResource();
			  System.out.println(" [x] Requesting " + checkISEventEngine + ":on:" + requestQueueName);
			  response = pingMonitor1.call(checkISEventEngine);
			  System.out.println(" [.] Got '" + checkISEventEngine + ":" + response + "'");
			  availableResources.put("isEventEngine", response);
			  LoggingService.addLogs(CreateRulesMap.applicationName, "Ping_Monitor_For_Resource", "IS availability response :" + response);
			  
			  pingMonitor2 = new PingMonitorForResource();
			  System.out.println(" [x] Requesting " + platformLoadBalancer);
			  response = pingMonitor2.call(platformLoadBalancer);
			  System.out.println(" [.] Got '" + platformLoadBalancer + ":" + response + "'");
			  availableResources.put("platformLb", response);
			  LoggingService.addLogs(CreateRulesMap.applicationName, "Ping_Monitor_For_Resource", "Platform LB availability response :" + response);
			  
			  Thread.sleep(10000);
			  pingMonitor1 = null;
			  pingMonitor2 = null;
			}
		  catch(IOException | TimeoutException | InterruptedException e) {
			  e.printStackTrace();
			}
	  	}
	}
}