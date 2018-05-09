package sensorDataApi;

import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
	
	public static void main(String args[]) throws Exception
	{
		while(true) {
			
			Random rand =  new Random();
			int n = rand.nextInt(800) + 200;
			String fs1 = "Nikon123##" + n; 
			sendMessage(fs1, "healthcare_gateway1", "192.168.1.106");
			n = rand.nextInt(800) + 200;
			String fs2 = "Honeywell123##" + n;
			sendMessage(fs2, "healthcare_gateway1", "192.168.1.106");
		
			n = rand.nextInt(800) + 200;
			String fs3 = "Nikon234##" + n;
			sendMessage(fs3, "healthcare_gateway1", "192.168.1.106");
		
			n = rand.nextInt(800) + 200;
			String fs4 = "Honeywell234##" + n;
			sendMessage(fs4, "healthcare_gateway1", "192.168.1.106");
			
			n = rand.nextInt(800) + 1000;
			String fs5 = "Soil123##" + n;
			sendMessage(fs5, "agriculture_gateway1", "192.168.1.106");
			
			n = rand.nextInt(800) + 1000;
			String fs6 = "Rain234##" + n;
			sendMessage(fs6, "agriculture_gateway1", "192.168.1.106");
			
			n = rand.nextInt(800) + 1000;
			String fs7 = "Soil123##" + n;
			sendMessage(fs7, "agriculture_gateway2", "192.168.1.106");
			
			n = rand.nextInt(800) + 1000;
			String fs8 = "Rain234##" + n;
			sendMessage(fs8, "agriculture_gateway2", "192.168.1.106");
			
			n = rand.nextInt(800) + 200;
			String fs9 = "Nikon123##" + n; 
			sendMessage(fs1, "healthcare_gateway2", "192.168.1.106");
			
			n = rand.nextInt(800) + 200;
			String fs10 = "Honeywell123##" + n;
			sendMessage(fs10, "healthcare_gateway2", "192.168.1.106");
		
			n = rand.nextInt(800) + 200;
			String fs11 = "Nikon234##" + n;
			sendMessage(fs11, "healthcare_gateway2", "192.168.1.106");
		
			n = rand.nextInt(800) + 200;
			String fs12 = "Honeywell234##" + n;
			sendMessage(fs4, "healthcare_gateway2", "192.168.1.106");
			
		Thread.sleep(10000);
		}
//		sendMessage("Honeywell234##777", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Nikon123##189", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Honeywell123##233", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Nikon234##276", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Honeywell234##777", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Nikon123##189", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Honeywell123##233", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Nikon234##276", "healthcare_gateway1", "192.168.1.106");
//		sendMessage("Honeywell234##777", "healthcare_gateway1", "192.168.1.106");
	}
	
	public static int sendMessage(String message, String queueName, String mqsIP) throws Exception{
		/*
		 * TODO: Get the message server Queue IP & Port
		 */
		String msgQueueServerIP = mqsIP;
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
		
		return 0;
	}
}
