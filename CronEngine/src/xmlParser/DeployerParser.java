package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DeployerParser {
	
	public DeployerData getDeployerData(String xmlPath) {
		DeployerData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new DeployerData();
	         saxParser.parse(inputFile, userhandler);
	         System.out.println(userhandler.apps.getDeployerApp().get(0).getAppName());
	         System.out.println(userhandler.apps.getDeployerApp().get(0).getSensitivity());
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}

//    public static void main(String[] args) {
//    	String xmlPath = "src/resources/deployer.xml";
//    	DeployerParser deployerParser = new DeployerParser();
//    	DeployerData data = deployerParser.getDeployerData(xmlPath);
//   }   
}

