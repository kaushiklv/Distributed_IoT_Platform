import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import xmlParser.TopologyParser;

public class CheckTopology implements Runnable {

	String appName;
	
	public CheckTopology(String appName) {
		// TODO Auto-generated constructor stub
		this.appName = appName;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String filePath = "./" + appName;
		Path myDir = Paths.get(filePath);  
		
        try {
        	boolean valid = true;
        	do {
	           WatchService watcher = myDir.getFileSystem().newWatchService();
	           myDir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
	
	           WatchKey watckKey = watcher.take();
	
	           List<WatchEvent<?>> events = watckKey.pollEvents();
	           for (WatchEvent<?> event : events) {
	                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) 
	                {	                	
	                	  System.out.println("Created: " + event.context().toString());
		                  CronEngine.staticTopoData = new TopologyParser().getTopologyData(filePath + "/topology.xml");
	                }
	           }
	           valid = watckKey.reset();
        	}while(valid);
           
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }	
	}

}
