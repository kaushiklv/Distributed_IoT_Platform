
public class LoadBalancer {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		if(args.length < 8)
		{
			System.out.println("insufficient number of arguments ");
			return;
		}
		String domain = args[0];
		String cpuUsageQueue = args[1];
		String messageQueueServerIp = args[2];
		String messageQueueUser = args[3];
		String messageQueuePasswd = args[4];
		String messageQueuePort = args[5];
		String serviceRequestQueue = args[6];
		String instanceNumber = args[7];
		

		Thread threadOne = new Thread(new CpuUsageTracker(domain, cpuUsageQueue, messageQueueServerIp, messageQueueUser, messageQueuePasswd, messageQueuePort));
		threadOne.start();
		
		// services will 
		Thread threadTwo = new Thread(new ServiceListener(domain, serviceRequestQueue, messageQueueServerIp, messageQueueUser, messageQueuePasswd, messageQueuePort));
		threadTwo.start();
		
		
		Thread threadThree = new Thread(new HeartBeat("platform", domain, messageQueueServerIp, messageQueuePort, messageQueueUser, messageQueuePasswd, instanceNumber ));
		threadThree.start();
	
		
		
		
		
		
	//	Thread threadThree = new Thread(I am alive)
		
		

	}

}
