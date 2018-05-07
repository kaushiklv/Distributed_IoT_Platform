package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CronData extends DefaultHandler{
	   boolean id = false;
	   boolean name = false;
	   boolean tasks =false;

	   public Cron cron;
	   public Task Task;
	   public Sensor Sensor;

	   class cronStruct{
	      boolean task;
	      boolean sensors;
	      boolean sensorId; 
	      boolean eventId;
	      boolean serviceId;
	      boolean workflowId;
	      boolean time;
	   }
	   cronStruct cronI;

	   @Override
	   public void startElement(
	      String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	        cron = new Cron();
	        cron.appInfo = new AppData();
	      }
	      else if (qName.equalsIgnoreCase("id")) {
	            id = true;
	      } else if (qName.equalsIgnoreCase("name")) {
	         name = true;
	      }
	      else if (qName.equalsIgnoreCase("tasks")) {
	         cron.tasks = new ArrayList<Task>();
	         tasks =true;
	         cronI = new cronStruct();
	         cronI.task = false;
	         cronI.sensors = false;
	         cronI.sensorId = false;
	         cronI.eventId = false;
	         cronI.serviceId = false;
	         cronI.workflowId = false;
	         cronI.time = false;
	      }
	      else if (qName.equalsIgnoreCase("task")) {
			  	Task = new Task();
		        cronI.task = true;
	      } 
	      else if (qName.equalsIgnoreCase("sensors")) {
	    	  Task.sensors = new ArrayList<Sensor>();
	          cronI.sensors = true;
	      }
	      else if (qName.equalsIgnoreCase("sensorId")) {
	    	 Sensor = new Sensor();
	         cronI.sensorId = true;
	      }
	      else if (qName.equalsIgnoreCase("eventId")) {
	          cronI.eventId = true;
	      } 
	      else if (qName.equalsIgnoreCase("serviceId")) {
	          cronI.serviceId = true;
	      }
	      else if (qName.equalsIgnoreCase("workflowId")) {
	          cronI.workflowId = true;
	      } 
	      else if (qName.equalsIgnoreCase("time")) {
	          cronI.time = true;
	      }
	   }

	   @Override
	   public void endElement(String uri, 
	      String localName, String qName) throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	         id = false;
	         name =false;
	      }
	      if (qName.equalsIgnoreCase("sensors")) { 
	         cronI.sensors = false;
	      }
	       if (qName.equalsIgnoreCase("sensorId")) {
	        Task.sensors.add(Sensor); 
	        Sensor = null;
	      }
	      if (qName.equalsIgnoreCase("tasks")) {
	         tasks = false;
	      }
	      if (qName.equalsIgnoreCase("task")) {
	        cron.tasks.add(Task);
	        Task = null;
	         cronI.task = false;
	      }
	   }

	   @Override
	   public void characters(char ch[], int start, int length) throws SAXException {

	      if (id) {
	        cron.appInfo.id = new String(ch, start, length);
	         id = false;
	      } else if (name) {
	        cron.appInfo.name = new String(ch, start, length);
	        name = false;
	      }
	      else if (tasks){
	         if (cronI.task){
	            if (cronI.sensors){
	               if(cronI.sensorId){
		               Sensor.sensorId = new String(ch, start, length);
		               cronI.sensorId = false;
	               }
	            }
	            if (cronI.eventId){
	                Task.eventId = new String(ch, start, length);
	                cronI.eventId = false;
	            }
	            if (cronI.serviceId){
	                 Task.serviceId = new String(ch, start, length);
	                   cronI.serviceId = false;
	            }
	            if (cronI.workflowId){
	                Task.workflowId = new String(ch, start, length);
	                cronI.workflowId = false;
	            }
	             if (cronI.time){
	              Task.time = new String(ch, start, length);

	               cronI.time = false;
	            }
	         }
	      }
	   }
}
