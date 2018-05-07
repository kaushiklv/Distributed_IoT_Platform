package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ServiceData extends DefaultHandler {
	   boolean id = false;
	   boolean name = false;
	   boolean Services =false;
	   public AppService appservice;
	   Service Service;

	   class serviceStruct{
	      boolean service;
	      boolean serviceId;
	      boolean serviceName; 
	      boolean serviceJarName;
	      boolean dependencies;
	      boolean input;
	      boolean output;
	      boolean className;
	      boolean methodName;
	   }
	   serviceStruct serviceI;

	   @Override
	   public void startElement(
	      String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	        appservice =  new AppService();
	        appservice.appInfo =  new AppData();
	        if(appservice.services == null)
	          appservice.services = new ArrayList<Service>();
	      }
	      else if (qName.equalsIgnoreCase("id")) {
	            id = true;
	      } else if (qName.equalsIgnoreCase("name")) {
	         name = true;
	      }
	      else if (qName.equalsIgnoreCase("services")) {
	         Services =true;
	         serviceI = new serviceStruct();
	         serviceI.service= false;
	         serviceI.serviceName = false;
	         serviceI.serviceId = false;
	         serviceI.serviceJarName = false;
	         serviceI.dependencies = false;
	         serviceI.input = false;
	         serviceI.output = false;
	         serviceI.className = false;
	         serviceI.methodName = false;
	      }
	      else if (qName.equalsIgnoreCase("service")) {
	            Service = new Service();
	            serviceI.service = true;
	      } 
	      else if (qName.equalsIgnoreCase("serviceId")) {
	         serviceI.serviceId = true;
	      }else if (qName.equalsIgnoreCase("serviceName")) {
	          serviceI.serviceName = true;
	      } else if (qName.equalsIgnoreCase("serviceJarName")) {
	          serviceI.serviceJarName = true;
	      }else if (qName.equalsIgnoreCase("dependencies")) {
	             serviceI.dependencies = true;
	      }else if (qName.equalsIgnoreCase("input")) {
	             serviceI.input = true;
	      } else if (qName.equalsIgnoreCase("output")) {
	          serviceI.output = true;
	      }else if (qName.equalsIgnoreCase("className")) {
	             serviceI.className = true;
	      } else if (qName.equalsIgnoreCase("methodName")) {
	          serviceI.methodName = true;
	      }
	   }

	   @Override
	   public void endElement(String uri, 
	      String localName, String qName) throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	         id = false;
	         name =false;
	      }
	      if (qName.equalsIgnoreCase("services")) {

	         Services = false;
	      }
	      if (qName.equalsIgnoreCase("service")) {
	        appservice.services.add(Service);
	        Service = null;
	         serviceI.service = false;
	      }
	   }

	   @Override
	   public void characters(char ch[], int start, int length) throws SAXException {

	      if (id) {
	        appservice.appInfo.id = new String(ch, start, length);
	         id = false;
	      } else if (name) {
	        appservice.appInfo.name = new String(ch, start, length);
	         name = false;
	      }
	      else if (Services){
	         if (serviceI.service){
	               if(serviceI.serviceId){
	               Service.serviceId = new String(ch, start, length);
	               serviceI.serviceId = false;
	               }
	            if (serviceI.serviceName){
	              Service.serviceName = new String(ch, start, length);
	              serviceI.serviceName = false;
	            }
	            if (serviceI.serviceName){
	               Service.serviceName = new String(ch, start, length);
	                serviceI.serviceName = false;
	            }if (serviceI.serviceJarName){
	               Service.serviceJarName = new String(ch, start, length);
	                serviceI.serviceJarName = false;
	            }
	            if (serviceI.dependencies){
	                Service.dependencies = new String(ch, start, length);
	                serviceI.dependencies = false;
	            }
	             if (serviceI.input){
	               Service.input = new String(ch, start, length);
	                serviceI.input = false;
	            }
	            if (serviceI.output){
	                Service.output = new String(ch, start, length);
	                serviceI.output = false;
	            }
	             if (serviceI.className){
	               Service.className = new String(ch, start, length);
	                serviceI.className = false;
	            }
	            if (serviceI.methodName){
	               Service.methodName = new String(ch, start, length);
	                serviceI.methodName = false;
	            }
	         }
	      }
	   }
}
