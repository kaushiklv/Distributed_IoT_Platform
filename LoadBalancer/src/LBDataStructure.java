import java.util.HashMap;
import java.util.Map;

public class LBDataStructure 
{
	// appName#service Name -> instance number -> cpu usage
	public static Map<String,Map<String, String>> cpuUsage = new HashMap<String,Map<String, String>>();

	public static Map<String, Map<String, String>> getCpuUsage() {
		return cpuUsage;
	}

	public static void setCpuUsage(Map<String, Map<String, String>> cpuUsage) {
		LBDataStructure.cpuUsage = cpuUsage;
	}
	
	
	
	

}
