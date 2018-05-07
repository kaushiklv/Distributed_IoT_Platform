

public class Test 
{
	public static void main(String[] arg) {
		StreamingRulesHandler1 stream = new StreamingRulesHandler1();
		
		
		if(arg.length < 7)
		{
			System.out.println("insufficient number of arguments !");
			return;
		}
		
	//	stream.processMessage("sensor#Honeywell123#15-event#1",
		//		"WorkflowEngine", "192.168.43.38",
			//	appName, "gateway", "service1.jar", "instance1");
		String message = arg[0]; // sensor#Honeywell123#15-event#1
		String workflowEngine = arg[1];
		String messageQueueIp = arg[2];
		String appName = arg[3];
		String location = arg[4];
		String jar = arg[5];
		String instance = arg[6];
		stream.processMessage(message, workflowEngine, messageQueueIp, appName, location, jar, instance);
	}

}
