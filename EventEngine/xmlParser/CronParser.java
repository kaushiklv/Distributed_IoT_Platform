package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CronParser {
	
	public CronData getCronData(String xmlPath) {
		CronData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new CronData();
	         saxParser.parse(inputFile, userhandler);  
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler; 
	}

//	public static void main(String[] args) {
//		String xmlPath = "src/resources/cron.xml";
//		CronParser cron = new CronParser();
//		CronData data = cron.getCronData(xmlPath);
//		System.out.println(data.cron.getTasks().get(0).getWorkflowId());
//	}   
}
