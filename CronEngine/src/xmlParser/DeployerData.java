package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeployerData extends DefaultHandler {
	   public Deployer apps;
	   DeployerApp app;
	   boolean id = false;
	   boolean name = false;
	   boolean Apps =false;
	   class appStruct{
	      boolean App;
	      boolean appName;
	      boolean sensitivity;
	   }
	   appStruct appI;

	   @Override
	   public void startElement(
	      String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	         apps = new Deployer();
	         if(apps.deployerApp == null)
	         apps.deployerApp = new ArrayList<DeployerApp>();
	      apps.appInfo = new AppData();
	            id = true;
	      }
	     else if (qName.equalsIgnoreCase("id")) {
	            id = true;
	      } else if (qName.equalsIgnoreCase("name")) {
	         name = true;
	      }
	      else if (qName.equalsIgnoreCase("apps")) {
	         Apps =true;
	         appI = new appStruct();
	         appI.App = false;
	         appI.appName = false;
	         appI.sensitivity = false;
	      }
	      else if (qName.equalsIgnoreCase("app")) {
	         app = new DeployerApp();
	         appI.App = true;
	      }
	      else if (qName.equalsIgnoreCase("appName")) {
	            appI.appName = true;
	      } else if (qName.equalsIgnoreCase("sensitivity")) {
	          appI.sensitivity = true;
	      }
	   }

	   @Override
	   public void endElement(String uri, 
	      String localName, String qName) throws SAXException {
	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	         id = false;
	         name =false;
	      }
	      if (qName.equalsIgnoreCase("apps")) {
	         Apps = false;
	      }
	      if (qName.equalsIgnoreCase("app")) {
	         apps.deployerApp.add(app);
	         app =null;
	         appI.App = false;
	      }
	   }

	   @Override
	   public void characters(char ch[], int start, int length) throws SAXException {

	      if (id) {
	         // System.out.println("id: " + new String(ch, start, length));
	         apps.appInfo.id =new String(ch, start, length);
	         id = false;
	      } else if (name) {
	         // System.out.println("name: " + new String(ch, start, length));
	         apps.appInfo.name =new String(ch, start, length);
	         name = false;
	      }
	      else if (Apps){
	         if (appI.App){
	            if (appI.appName){
	                // System.out.println("appName: " + new String(ch, start, length));
	               app.appName = new String(ch, start, length);
	               appI.appName = false;
	            }
	            if (appI.sensitivity){
	                // System.out.println("app sensitivity: " + new String(ch, start, length));
	                app.sensitivity = new String(ch, start, length);
	                appI.sensitivity = false;
	            }
	         }
	      }
	   }
}
