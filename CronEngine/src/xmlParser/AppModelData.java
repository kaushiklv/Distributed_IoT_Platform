package xmlParser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import xmlParser.AppModelData.appStruct;

public class AppModelData extends DefaultHandler {
	boolean id = false;
	   boolean name = false;
	   boolean apps =false;
	   List<String> artifacts;
	   public AppModel Apps;
	   App app;

	   class appStruct{
	      boolean App;
	      boolean appName;
	      boolean artifacts;
	      boolean events;
	      boolean artifactName;
	      boolean event;
	   }
	   appStruct appI;


	   @Override
	   public void startElement(
	      String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	     
	     if(qName.equalsIgnoreCase("appInfo")){
	      Apps = new AppModel();
	      Apps.appInfo = new AppData();
	     } 
	     else if (qName.equalsIgnoreCase("id")) {
	                             // System.out.println("aaa");

	            id = true;
	      } else if (qName.equalsIgnoreCase("name")) {
	                               // System.out.println("a");

	         name = true;
	      }
	      else if (qName.equalsIgnoreCase("apps")) {

	         apps =true;
	         Apps.apps = new ArrayList<App>();
	         appI = new appStruct();
	         appI.App = false;
	         appI.appName = false;
	         appI.artifacts = false;
	      }
	      else if (qName.equalsIgnoreCase("app")) {
	         app = new App();
	            appI.App = true;
	      }else if (qName.equalsIgnoreCase("appName")) {
	            appI.appName = true;
	      } else if (qName.equalsIgnoreCase("artifacts")) {
	            artifacts =new ArrayList<String>();   
	            appI.artifacts = true;
	      }else if (qName.equalsIgnoreCase("artifactName")) {
	            appI.artifactName = true;
	      }else if (qName.equalsIgnoreCase("events")) {
	            appI.events = true;
	      }else if (qName.equalsIgnoreCase("event")) {
	            appI.event = true;
	      }

	   }

	   @Override
	   public void endElement(String uri, 
	      String localName, String qName) throws SAXException {

	      if (qName.equalsIgnoreCase("apps")) {
	         apps = false;
	      }
	      if (qName.equalsIgnoreCase("appInfo")) {
	         id = false;
	         name = false;
	      }
	      if (qName.equalsIgnoreCase("app")) {
	        Apps.apps.add(app);
	        app = null;
	         appI.App = false;
	      }
	      if (qName.equalsIgnoreCase("artifacts")) {
	        app.artifacts = artifacts;
	        artifacts = null;
	         appI.artifacts = false;
	      }
	      if (qName.equalsIgnoreCase("artifactName")) {
	         appI.artifactName = false;
	      }
	      if (qName.equalsIgnoreCase("events")) {
	         appI.events = false;
	      }
	      if (qName.equalsIgnoreCase("event")) {
	         appI.event = false;
	      }
	   }

	   @Override
	   public void characters(char ch[], int start, int length) throws SAXException {

	      if(id){
	         // System.out.println("id: " + new String(ch, start, length));
	        Apps.appInfo.id = new String(ch, start, length);
	         id = false;
	      }else if(name){
	         // System.out.println("name: " + new String(ch, start, length));
	        Apps.appInfo.name = new String(ch, start, length);
	         name = false;
	      }else if (apps){
	         if (appI.App){
	            if (appI.appName){
	                // System.out.println("appName: " + new String(ch, start, length));
	              app.appName = new String(ch, start, length);
	                appI.appName = false;
	            }
	            if (appI.artifacts){
	              if(appI.artifactName){
	                // System.out.println("artifactName: " + new String(ch, start, length));
	                artifacts.add(new String(ch, start, length));
	                appI.artifactName = false;
	            }
	          }
	            if (appI.events){
	              if(appI.event){
	                // System.out.println("event: " + new String(ch, start, length));
	                appI.event = false;
	            }
	          }
	         }
	      }
	   }
}
