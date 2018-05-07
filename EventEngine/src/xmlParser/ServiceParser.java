package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ServiceParser {
	
	public ServiceData getServiceData(String xmlPath) {
		ServiceData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new ServiceData();
	         saxParser.parse(inputFile, userhandler); 
	         System.out.println(userhandler.appservice.getServices().get(0).getClassName());    
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}

//   public static void main(String[] args) {
//	   String xmlPath = "src/resources/service.xml";
//	   ServiceParser serviceParser = new ServiceParser();
//	   ServiceData data = serviceParser.getServiceData(xmlPath);	   
//   }   
}
