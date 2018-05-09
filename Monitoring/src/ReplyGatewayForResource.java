import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.sql.Timestamp;

public class ReplyGatewayForResource implements Runnable{

  private String RPC_QUEUE_NAME;
  private String mqServerIp;
  private String messageQueuePort;
  private String messageQueueUsername;
  private String messageQueuePassword;
  
  
  public ReplyGatewayForResource(String mqServerIp, String messageQueuePort, String messageQueueUsername, String messageQueuePassword) {
	  this.mqServerIp = mqServerIp;
	  this.messageQueuePort = messageQueuePort;
	  this.messageQueueUsername = messageQueueUsername;
	  this.messageQueuePassword = messageQueuePassword;
	  this.RPC_QUEUE_NAME = "monitoringGatewayCheckResourceAlive";
  }
  
  
private static int checkResourceInMap(String message) {
    // checkForEventEngineAtIS
	  
	System.out.println("Got Here");
	Timestamp time = new Timestamp(System.currentTimeMillis());
	for(String entry : ReadQueue.myMap.keySet()) {
		String[] detail = entry.split("##");
		  
		  //appname_domain_serviceName_instanceNumber
		String checkKey = detail[0] + "" +  detail[1] + "" + detail[3] + "_" + detail[4];
		Long value = ReadQueue.myMap.get(entry);
		
		if(checkKey.equalsIgnoreCase(message)){
			// entry of loadbalancer in monitoring map
			// Check time if > current time => service Up & running
			if(value > (Long)(time.getTime()))
				return 1;
			else
				return 0;
		}		 
	}
	System.out.println("Here again");
	return 0;
}
  	

	@Override
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();

	    try {
		    factory.setHost(mqServerIp);
		    factory.setPort(Integer.parseInt(messageQueuePort));
		    factory.setUsername(messageQueueUsername);
		    factory.setPassword(messageQueuePassword);
	    	Connection connection = factory.newConnection();
	      final Channel channel = connection.createChannel();

	      channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

	      channel.basicQos(50);

	      System.out.println(" [x] Awaiting RPC requests");

	      Consumer consumer = new DefaultConsumer(channel) {
	        @Override
	        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	          AMQP.BasicProperties replyProps = new AMQP.BasicProperties
	                  .Builder()
	                  .correlationId(properties.getCorrelationId())
	                  .build();

	          String response = "";
	          System.out.println("I am getting message");

	          try {
	            String message = new String(body,"UTF-8");

	            System.out.println(" [.] CheckGatewayResource(" + message + ")");
	            response += checkResourceInMap(message);
	            System.out.println("Resp: " + response);
	          }
	          catch (RuntimeException e){
	            System.out.println(" [.] " + e.toString());
	          }
	          finally {
	            channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
	            channel.basicAck(envelope.getDeliveryTag(), false);
	            // RabbitMq consumer worker thread notifies the RPC server owner thread 
	            synchronized(this) {
	            	this.notify();
	            }
	          }
	        }
	      };

	      channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
	      // Wait and be prepared to consume the message from RPC client.
	      while (true) {
	      	synchronized(consumer) {
	      		try {
	      			consumer.wait();
	      	    } catch (InterruptedException e) {
	      	    	e.printStackTrace();	    	
	      	    }
	      	}
	      }
	    } catch (IOException | TimeoutException e) {
	      e.printStackTrace();
	    }
		
	}
}