package xmlParser;

import java.io.File;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SensorParser {
	
	public SensorData getSensorData(String xmlPath) {
		SensorData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new SensorData();
	         saxParser.parse(inputFile, userhandler);   
	         
	         System.out.println(userhandler.sensor.Sensors.size());
	         for(int i=0;i<userhandler.sensor.Sensors.size();i++){
	  	         System.out.println(userhandler.sensor.Sensors.get(i).sensorJarName);
	         }
	         
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}
	
//	public static void main(String[] args) {
//		String xmlPath = "src/resources/sensor.xml";
//		SensorParser sensorParser = new SensorParser();
//		SensorData data = sensorParser.getSensorData(xmlPath);
//	}  
}
