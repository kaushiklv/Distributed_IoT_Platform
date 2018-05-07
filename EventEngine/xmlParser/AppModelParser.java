package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AppModelParser {
	
	public AppModelData getAppModelData(String xmlPath) {
		AppModelData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new AppModelData();
	         saxParser.parse(inputFile, userhandler);

	         System.out.println(userhandler.Apps.getApp());
	           // for(int i=0;i<userhandler.topo.intermediateServer.size();i++){
	           //     System.out.println(userhandler.topo.intermediateServer.get(i).IP);
	           // }

	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}

//   public static void main(String[] args) {
//	   String xmlPath = "src/resources/appModel.xml";
//	   AppModelParser appParser = new AppModelParser();
//	   AppModelData app = appParser.getAppModelData(xmlPath);      
//   }   
}
