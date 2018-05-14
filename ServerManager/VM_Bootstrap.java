package vagrantconfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class VM_Bootstrap 
{

	/**
	 * JSch Example Tutorial
	 * Java SSH Connection Program
	 */
	public static void main(String[] args) throws Exception
	{
		

		 // #$1--alias $2--ip $3--client_path $4--host_vagrant_folder $5--password
		String alias = args[0];
		System.out.println(alias);
	    String ip = args[1];
	    String clientPath = args[2];
	    String hostVagrantFolder = args[3];
	    String password = args[4];
	    String boxNumber = args[5];
	    Runtime r = Runtime.getRuntime();
	    String[] commands = {"bash", hostVagrantFolder + "central_up.sh", alias, ip, clientPath, hostVagrantFolder, password, boxNumber};
	    try {
	        Process p = r.exec(commands);

	        p.waitFor();
	        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line = "";

	        while ((line = b.readLine()) != null) {
	            System.out.println(line);
	        }

	        b.close();
	       
	    } catch (Exception e) {
	        System.err.println("Failed to execute bash with command: ");
	        e.printStackTrace();
	    }
	  
	    java.util.Properties config = new java.util.Properties(); 
	    config.put("StrictHostKeyChecking", "no");
	   
	    JSch jsch = new JSch();
	    Session session = jsch.getSession(alias, ip);
	    session.setPassword(password);
	    session.setConfig(config);
	    session.connect();

	    ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
	    sftpChannel.connect();

	    sftpChannel.put(hostVagrantFolder + "run.sh", clientPath + "/run.sh");
	    sftpChannel.put(hostVagrantFolder + "bootstrap.sh", clientPath + "/bootstrap.sh");
	    sftpChannel.put(hostVagrantFolder + "getvmip.sh", clientPath + "/getvmip.sh");
	    sftpChannel.put(hostVagrantFolder + "Vagrantfile", clientPath + "/Vagrantfile");
	    
	    
	    
	    String command1="bash " + args[2] + "/run.sh";
	    try{
	    	
	    /*	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	Session session=jsch.getSession(user, host, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect();*/
	    	System.out.println("Connected");
	    	
	    	Channel channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command1);
	        channel.setInputStream(null);
	        ((ChannelExec)channel).setErrStream(System.err);
	        
	        InputStream in=channel.getInputStream();
	        channel.connect();
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)break;
	            
	            String temp = new String(tmp, 0, i);
	            if(temp.contains("192.168."))
	            {
	            	//System.out.println("milali re !");
	            	String vm_ip = temp;
	            }
	            System.out.print(new String(tmp, 0, i));
	          }
	          if(channel.isClosed()){
	            System.out.println("exit-status: "+channel.getExitStatus());
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        channel.disconnect();
	        session.disconnect();
	        System.out.println("DONE");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }

	}

}