
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
	
	public void  sendMessage(String message, String queueName, String mqIpAddress) throws Exception{
		/*
		 * TODO: Get the message server Queue IP & Port
		 */
		String msgQueueServerIP = mqIpAddress;
		String msgQueueServerUname = "admin";
		String msgQueueServerPass = "admin";
		int msgQueueServerPort = 5672;
		
		
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
	    System.out.println(" [x] Sent '" + message + "'");

	    channel.close();
	    connection.close();
		
		
	}
//	public static void main(String[] args)
//	{
//		try {
//		//sendMessage("wtf2.wf", "Workflow");
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}
