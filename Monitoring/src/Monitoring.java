	public class Monitoring
{
	public static void main(String args[]){
		
		
		
		if(args.length < 6)
		{
			System.out.println("insufficient number of arguments !");
			LoggingService.addLogs("platform", "monitoring", "insufficient number of arguments !");
			return;
		}
		
		String monitoringQueue = args[0];
		String messageQueueIp = args[1];
		String messageQueuePort = args[2];
		String messageQueueUsername = args[3];
		String messageQueuePassword = args[4];
		String serviceManangerQueue = args[5];	
		
		
		
		HeartBeat objHeartBeat = new HeartBeat(messageQueueIp,messageQueuePort,messageQueueUsername,messageQueuePassword,serviceManangerQueue);
		Thread t2 = new Thread(objHeartBeat);
		t2.start();
		
//		 Thread listen for services availability messages from gateway
		new Thread(new ReplyGatewayForResource(messageQueueIp, messageQueuePort, messageQueueUsername, messageQueuePassword)).start();

		
		ReadQueue objReadQueue = new ReadQueue(monitoringQueue,"dummy");
		try {
			objReadQueue.receiveMessage(monitoringQueue, messageQueueIp, messageQueuePort, messageQueueUsername, messageQueuePassword,serviceManangerQueue);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}