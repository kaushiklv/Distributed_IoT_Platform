import java.sql.Timestamp;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadQueue implements Runnable {
	//static HashMap<String, Long> myMap = new HashMap<String, Long>();
	static ConcurrentHashMap<String, Long> myMap = new ConcurrentHashMap<>();
	// static final Map<String,String > myMap = new HashMap<String,String>();
	String message;
	String startService;

	// public int getMap() {
	// return myMap.size();
	// }

	public void run() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		/*
		 * TODO: Parse the message and check which rules to apply
		 */

		/*
		 * TODO: Get the name of the message queue of the ruleEngine
		 */
		try {
			Timestamp time = new Timestamp(System.currentTimeMillis());
			// myMap.put(this.message,"a");
			String dataToBeMonitor = (this.message);
			String scaleUpService = (this.startService);

			if (dataToBeMonitor.startsWith("start")) {
				// if service needs to be started immediately
				String queueInfo[] = scaleUpService.split("##");
				System.out.println(queueInfo[0] + " , " + queueInfo[1] + " , " + queueInfo[2] + " , " + queueInfo[3]
						+ " , " + queueInfo[4]);
				// HeartBeat(String messageQueueIp,String messageQueuePort,String
				// messageQueueUsername,String messageQueuePassword,String
				// serviceManangerQueue){
				HeartBeat objHeartBeat = new HeartBeat(queueInfo[0], queueInfo[1], queueInfo[2], queueInfo[3],
						queueInfo[4]);
				objHeartBeat.formRestartServiceCommand(dataToBeMonitor);
			}

			else {
				if (myMap.get(dataToBeMonitor) == null) {
					myMap.put(dataToBeMonitor, (Long) (time.getTime() + 120000));
				} else {
					myMap.put(dataToBeMonitor, (Long) (time.getTime() + 120000));
				}

			}
			// System.out.println("message: "+this.message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ReadQueue(String message, String startService) {

		this.message = message;
		this.startService = startService;
	}

	public void receiveMessage(String queueName, String messageQueueIp, String messageQueuePort,
			String messageQueueUsername, String messageQueuePassword, String serviceManangerQueue) throws Exception {
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

		// HeartBeat(String messageQueueIp,String messageQueuePort,String ,String
		// messageQueuePassword,String serviceManangerQueue){
		String startService = messageQueueIp + "##" + messageQueuePort + "##" + messageQueueUsername + "##"
				+ messageQueuePassword + "##" + serviceManangerQueue;

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queueName, false, false, false, null);
		System.out.println("started listening to queue");
		LoggingService.addLogs("platform", "monitoring", "started listening to queue");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				String message = new String(body, "UTF-8");
				new Thread(new ReadQueue(message, startService)).start();

				// TODO: Log message
				System.out.println(" [x] Received '" + message + "'");
				LoggingService.addLogs("platform", "monitoring", " [x] Received '" + message + "'");

			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
}
