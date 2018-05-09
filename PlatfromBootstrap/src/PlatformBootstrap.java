package platformBootstrap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;



public class PlatformBootstrap {
	
	static List<String> availableVmList = new ArrayList<>();
	
	static String hostOrVm;
	static String availableVm;
	static String javaPath;
	
	static String mqServerIp;
	static String messageQueuePort;
	static String messageQueueUname;
	static String messageQueuePass;
	
	static String monitoringMachineIp;
	static String mqMonitoringMachineUname;
	static String mqMonitoringMachinePass;
	
	//Details for monitoring service
	static String monitoringJar;
	static String monitoring_queue;
	static String destinationQueue;
	
	//Details for Load Balancer
	static String loadBalancerJar;
	static String serviceRequestQueueForLb;
	static String domain;
	static String queueForCpuUsage;
	static String instanceNumberLb;
	static String lbMachineIp;
	static String lbMachineUname;
	static String lbMachinePass;
	
	
	// Details for Service Manager
	static String serviceManagerJar;
	static String serviceManagerQueue;
	static String ftpServerIp;
	static String ftpServerPort;
	static String ftpServerId;
	static String ftpServerPass;
	
	
	public static void populateVmList(){

		try(BufferedReader br = new BufferedReader(new FileReader(availableVm))){
			String line = br.readLine();
			System.out.println(line);
			while(line != null) {
				availableVmList.add(line);
				line = br.readLine();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static int startMqServer(String mqServerIP, String mqServerPort, String mqServerMachineUname, String mqServerMachinePass) {
		
		List<String> commandList = new ArrayList<String>();
		
		
		commandList.add(addCommandToGiveVmDetails(mqServerIP, mqServerMachineUname, mqServerMachinePass));
		//String commandPermission = "chmod 777 " + messageQueueScriptName;
		//String commandToRunMessageQueue = "bash -f " + messageQueueScriptName; 
		//commandList.add(commandPermission);
		//commandList.add(commandToRunMessageQueue);
		//commandList.add("java");
		commandList.add(getCommandToStartMonitoringService());
		
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(mqServerMachineUname, mqServerIP, 22);
			session.setPassword(mqServerMachinePass);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected to Mq-Sever & Monitoring Machine");
			
			for(String command : commandList) {
				if(command != null) {
					Channel channel = session.openChannel("exec");
					((ChannelExec)channel).setCommand(command);
					channel.setInputStream(null);
					((ChannelExec)channel).setErrStream(System.err);

					 //InputStream in = channel.getInputStream();
//					InputStream in=channel.getInputStream();
//			        channel.connect();
//			        byte[] tmp=new byte[1024];
//			        while(true){
//			          while(in.available()>0){
//			            int i=in.read(tmp, 0, 1024);
//			            if(i<0)break;
//			            System.out.print(new String(tmp, 0, i));
//			          }
//			          if(channel.isClosed()){
//			            System.out.println("exit-status: "+channel.getExitStatus());
//			            break;
//			          }
//			          try{Thread.sleep(1000);}catch(Exception ee){}
//			        }
					

					channel.connect();
					channel.disconnect();
				}
			}
			session.disconnect();
			System.out.println("MQ-Server & Monitoring service UP");
		}
		catch (Exception e) {
			System.out.println("Error is starting MQ-server & Monitoring Service");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int startLbAndServiceManager(String lbMachineIp, String lbMachineUname, String lbMachinePass) {
		List<String> commandList = new ArrayList<String>();
		commandList.add(addCommandToGiveVmDetails(lbMachineIp, lbMachineUname, lbMachinePass));
		commandList.add(getCommandToStartLoadBalancer());
		commandList.add(getCommandToStartServiceManager());
		
		
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(lbMachineUname, lbMachineIp, 22);
			session.setPassword(lbMachinePass);
			session.setConfig(config);
			session.connect();
			
			System.out.println("Connected to Machine to start LB & Service Manager");
			
			for(String command : commandList) {
				if(command != null) {
					Channel channel = session.openChannel("exec");
					((ChannelExec)channel).setCommand(command);
					channel.setInputStream(null);
					((ChannelExec)channel).setErrStream(System.err);
					
//					InputStream in=channel.getInputStream();
//			        channel.connect();
//			        byte[] tmp=new byte[1024];
//			        while(true){
//			          while(in.available()>0){
//			            int i=in.read(tmp, 0, 1024);
//			            if(i<0)break;
//			            System.out.print(new String(tmp, 0, i));
//			          }
//			          if(channel.isClosed()){
//			            System.out.println("exit-status: "+channel.getExitStatus());
//			            break;
//			          }
//			          try{Thread.sleep(1000);}catch(Exception ee){}
//			        }

					channel.connect();
					channel.disconnect();
				}
			}
			session.disconnect();
			System.out.println("LB & Service Manager - Up & Running");
		}
		catch (Exception e) {
			System.out.println("Error is starting LB & Service Manager");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String addCommandToGiveVmDetails(String machineIp, String machineUname, String machinePass) {
		String command = "echo " + machineIp + "_" + machineUname + "_" + machinePass + " > /home/vagrant/myVMIp.txt";
		return command;
	}
	
	public static String getCommandToStartServiceManager() {
		String command = javaPath +  " -jar " + serviceManagerJar + " " + mqServerIp + " " + messageQueueUname + " " + messageQueuePass + " " +
						messageQueuePort + " " + serviceManagerQueue + " " + ftpServerIp + " " + ftpServerPort + " " +
						ftpServerId + " " + ftpServerPass;
		return command;
	}
	
	public static String getCommandToStartLoadBalancer() {
		String command = javaPath +  " -jar " +  loadBalancerJar + " " + domain + " " +
				queueForCpuUsage + " " + mqServerIp + " " + messageQueueUname + " " + messageQueuePass + " " +
				messageQueuePort + " " + serviceRequestQueueForLb + " " + instanceNumberLb;
		
		return command;
	}
	
	public static String getCommandToStartMonitoringService() {
		String command = javaPath +  " -jar " +  monitoringJar + " " + monitoring_queue + " " 
						+ mqServerIp + " " +  messageQueuePort + " " + messageQueueUname 
						+ " " + messageQueuePass + " " + destinationQueue;
		
		return command;
	}
	
	

	
	public static void checkStartup() throws IOException {
		//get the file from FTP
		
		FTPClient ftpClient = new FTPClient();
		try {
			// connect and login to the server
			ftpClient.connect(ftpServerIp, Integer.parseInt(ftpServerPort));

			ftpClient.login(ftpServerId, ftpServerPass);
			ftpClient.enterLocalPassiveMode();

			try {
				getFiles.downloadSingleFile(ftpClient, "platform/" + "appRebootDetails.txt", "/home/kush/appRebootDetails.txt");
			} catch (NullPointerException ne) {
				System.out.println("appRebootDetails.txt doesn't exist on FTP. Create new.");				
			}
			
			

			// System.out.println("Disconnected");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		
		List<String> commandList = new ArrayList<>();
		
		String checkStartupFile = "/home/kush/appRebootDetails.txt";
		try(BufferedReader br = new BufferedReader(new FileReader(checkStartupFile))){
			String line = br.readLine();
			while(line != null) {
				try {
					commandList.add(line); 
					line = br.readLine();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException e) {
			// LOG IT
			System.out.println("Startup file not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		java.lang.Runtime rt = java.lang.Runtime.getRuntime();
		
		try {
	        java.lang.Process p = rt.exec("rm " + checkStartupFile);
	        ftpClient.deleteFile("platform/appRebootDetails.txt");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        
		Thread[] arr = new Thread[commandList.size()];
		int cnt = 0;
		for(String command : commandList) {
			Thread th = new Thread(new RunCommand(command));
			th.start();
			try {
				th.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		ftpClient.logout();
		ftpClient.disconnect();
	}
	
	public static void main(String[] args) {
		//Get available hosts and store in a map
		
		if(args.length < 20) {
			System.out.println("Insufficient Parameters");
			return;
		}
		

		// Path of file containing VM details
		availableVm = args[0];
		
		// details for mq-Server
		mqServerIp = args[1];
		messageQueuePort = args[2];
	    messageQueueUname = args[3];
		messageQueuePass = args[4];
		
		
		// Details for monitoring service
		monitoringJar = args[5];
		monitoring_queue = args[6];
		destinationQueue = args[7];
		
		// Parameters of Load Balancer
		loadBalancerJar = args[8];
		serviceRequestQueueForLb = args[9];
		domain = args[10];
		queueForCpuUsage = args[11];
		instanceNumberLb = args[12];
		
		serviceManagerQueue = args[13];
		ftpServerIp = args[14];
		ftpServerPort = args[15];
		ftpServerId = args[16];
		ftpServerPass = args[17];
		
		serviceManagerJar = args[18];
		hostOrVm = args[19];
		
		
		if(hostOrVm.equalsIgnoreCase("vm")) 
			javaPath = "/home/vagrant/jre/bin/java";
		else
			javaPath = "java";
		
		populateVmList();
		
		if(availableVmList.size() >= 2) {
			// More than 2 vms provide. Starting mq-monitoring in one & LB & service-Manager in other
			
			// Machine 2 details
			String machine1 = availableVmList.get(0);
			String[] machine1_details = machine1.split("_");
			monitoringMachineIp = machine1_details[0];
			mqMonitoringMachineUname = machine1_details[1];
			mqMonitoringMachinePass = machine1_details[2];
			
			// Machine 1 details
			String machine2 = availableVmList.get(1);
			String[] machine2_details = machine2.split("_");
			lbMachineIp = machine2_details[0];
			lbMachineUname = machine2_details[1];
			lbMachinePass = machine2_details[2];
		}
		else {
			System.out.println("Insufficinent Number of hosts to start platform");
		}
		
		
		
		// Starting Services
		int valMq = startMqServer(monitoringMachineIp, messageQueuePort, mqMonitoringMachineUname, mqMonitoringMachinePass);
		int valLb = startLbAndServiceManager(lbMachineIp, lbMachineUname, lbMachinePass);
		
		
		if(valMq == 0 && valLb == 0)
			System.out.println("Platform-Services Up & Running");
		
		try {
			checkStartup();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}




