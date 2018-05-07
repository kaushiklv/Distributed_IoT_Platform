package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RulesParser {
	
	public RulesData getRulesData(String xmlPath) {
		RulesData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new RulesData();
	         saxParser.parse(inputFile, userhandler);   
	         
	         System.out.println(userhandler.apprule.getAppInfo().getId());
	         // for(int i=0;i<userhandler.topo.intermediateServer.size();i++){
	  	      //    System.out.println(userhandler.topo.intermediateServer.get(i).IP);
	         // }
	         
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}
	
//	public static void main(String[] args) {
//		String xmlPath = "src/resources/rules.xml";
//		RulesParser rulesParser = new RulesParser();
//		RulesData ruleData = rulesParser.getRulesData(xmlPath);
//	}  
}

