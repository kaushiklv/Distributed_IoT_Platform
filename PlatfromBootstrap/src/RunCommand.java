package platformBootstrap;

public class RunCommand implements Runnable{

	String command;
	
	
	
	public RunCommand(String command) {
		super();
		this.command = command;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try
		{
			java.lang.Runtime rt1 = java.lang.Runtime.getRuntime();
			System.out.println("Executing:" + command);
			
	        java.lang.Process p1 = rt1.exec(command);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}

}
