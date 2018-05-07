package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SensorData extends DefaultHandler {
	public Sensors sensor;
	Sensor sensorInstance;
	boolean id = false;
	boolean name = false;
	boolean sensors =false;

   class sensorStruct{
   	boolean sensor;
	boolean sensorId;
	boolean sensorName;
	boolean sensorType;
	boolean sensorJarName;
	boolean sensorClassName;
   }sensorStruct sensorI;

   @Override
   public void startElement(
      String uri, String localName, String qName, Attributes attributes)
      throws SAXException {

     if(qName.equalsIgnoreCase("appInfo")){
		sensor = new Sensors();
		sensor.appInfo = new AppData();
	}else if (qName.equalsIgnoreCase("id")) {
		id = true;
	}else if (qName.equalsIgnoreCase("name")) {
        name = true;
    }else if (qName.equalsIgnoreCase("sensors")) {
		sensorInstance = new Sensor();
		if(sensor.Sensors == null)
			sensor.Sensors = new ArrayList<Sensor>();
		sensors =true;
		sensorI = new sensorStruct();
		sensorI.sensorId = false;
		sensorI.sensorType = false;
		sensorI.sensorName = false;
		sensorI.sensorClassName = false;
		sensorI.sensorJarName = false;
	}else if (qName.equalsIgnoreCase("sensor")) {
  		sensorInstance = new Sensor();
        sensorI.sensor = true;
  	}else if (qName.equalsIgnoreCase("sensorId")) {
        sensorI.sensorId = true;
	}else if (qName.equalsIgnoreCase("sensorName")) {
        sensorI.sensorName = true;
    }else if (qName.equalsIgnoreCase("sensorType")) {
        sensorI.sensorType = true;
    }else if (qName.equalsIgnoreCase("sensorJarName")) {
        sensorI.sensorJarName = true;
    }else if (qName.equalsIgnoreCase("sensorClassName")) {
        sensorI.sensorClassName = true;
    }
}

   @Override
   public void endElement(String uri, 
      String localName, String qName) throws SAXException {

      if (qName.equalsIgnoreCase("appInfo")) {
    	 id = false;
         name =false;	         
      }if (qName.equalsIgnoreCase("sensors")) {
         sensors = false;
      }if (qName.equalsIgnoreCase("sensor")) {
      	sensor.Sensors.add(sensorInstance);
         sensorI.sensor = false;
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {

      if(id){
    	 sensor.appInfo.setId(new String(ch, start, length));
         id = false;
      }else if(name){
        sensor.appInfo.setName(new String(ch, start, length));
        name = false;
      }else if (sensors){ 
         if (sensorI.sensor){
            if (sensorI.sensorId){
            	sensorInstance.setSensorId(new String(ch, start, length));
                sensorI.sensorId = false;
            }if (sensorI.sensorType){
                sensorInstance.setSensorType(new String(ch, start, length));
                sensorI.sensorType = false;
            }if (sensorI.sensorName){
                sensorInstance.setSensorName(new String(ch, start, length));
                sensorI.sensorName = false;
            }if (sensorI.sensorJarName){
                sensorInstance.setSensorJarName(new String(ch, start, length));
                sensorI.sensorJarName = false;
            }if (sensorI.sensorClassName){
                sensorInstance.setSensorClassName(new String(ch, start, length));
                sensorI.sensorClassName = false;
            }
         }
      }
   }
}
