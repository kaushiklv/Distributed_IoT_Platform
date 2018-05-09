import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendToMq {

	public int sendMsg(String message, String queueName,String messageQueueIp,String messageQueuePort,String messageQueueUsername,String messageQueuePassword) throws Exception{
		/*
		 * TODO: Get the message server Queue IP & Port
		 */
		String msgQueueServerIP = messageQueueIp;
		String msgQueueServerUname = messageQueueUsername;
		String msgQueueServerPass = messageQueuePassword;
		int msgQueueServerPort = Integer.parseInt(messageQueuePort);
		
		
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(msgQueueServerIP);
	    factory.setPort(msgQueueServerPort);
	    factory.setUsername(msgQueueServerUname);
	    factory.setPassword(msgQueueServerPass);
	    
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(queueName, false, false, false, null);
	    channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
	    
	    // TODO: To log
	    /*System.out.println(" [x] Sent '" + message + "'");*/
	    LoggingService.addLogs("platform", "monitoring", " [x] Sent '" + message + "'");
		  

	    channel.close();
	    connection.close();
		
		return 0;
	}
}