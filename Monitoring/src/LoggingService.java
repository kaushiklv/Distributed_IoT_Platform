import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * LoggingService for app developers to store their logs on platform ftp server
 * @author gaurav
 *
 */
public class LoggingService {
	
	 /**
	  * API for app developers to store their string message in logs file
	  * Platform takes care to add timestamp
	  * @param appName : name of application, to be passed by app developer
	  * @param message : log message to be stored 
	  */
	 public static int addLogs(String appName, String className, String message)
	 {
		 try
		 {
			 	
			 FileWriter fileWriter = new FileWriter("./" + appName + "/logs/" + className + ".out", true);
			    PrintWriter printWriter = new PrintWriter(fileWriter);
			 	final String encoding = "UTF-8"; 
			 	Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
				String strDate = sdf.format(cal.getTime());
				
				String logMessage = "\n " + strDate + " " + message;
			    printWriter.print(logMessage);
			  
			    printWriter.close();
			 
			
				
				return 0;
		 }
		 catch(Exception e)
		 {
			 System.out.println(e.getMessage());
			 return -1;
		 }
	 }
	

}