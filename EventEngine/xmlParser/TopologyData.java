package xmlParser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TopologyData extends DefaultHandler {
	public Topology topo;
	public Gateway gateway = null;
	public IntermediateServer intermediateServer = null;
	private Sensor sensor = null;
	private Gateway gate = null;
	
	boolean id = false;
	boolean name = false;
	boolean gateways =false;
	boolean intermediateServers =false;
	
	List<Gateway> gatewayIsList;

   class gatewayStruct{
	boolean gateway;
	boolean gatewayId;
	boolean ip;
	boolean port;
	boolean username;
	boolean password;
	boolean sensors;
	boolean sensorId;
   }gatewayStruct gatewayI;

   class intermediateServersStruct{
	boolean intermediateServerId;
	boolean intermediateServer;
	boolean gatewaysIs;
	boolean gatewayIsId;
	boolean ip;
	boolean port;
	boolean username;
	boolean password;
   }intermediateServersStruct intermediateServersStructI;

   @Override
   public void startElement(
      String uri, String localName, String qName, Attributes attributes)
      throws SAXException {

     if(qName.equalsIgnoreCase("appInfo")){
		topo = new Topology();
		topo.appInfo = new AppData();
	}else if (qName.equalsIgnoreCase("id")) {
		id = true;
	}else if (qName.equalsIgnoreCase("name")) {
        name = true;
    }else if (qName.equalsIgnoreCase("gateways")) {
		//gateway = new Gateway();
		if(topo.gateways == null)
			topo.gateways = new ArrayList<Gateway>();
		gateways =true;
		gatewayI = new gatewayStruct();
		gatewayI.gateway = false;
		gatewayI.gatewayId = false;
		gatewayI.ip = false;
		gatewayI.port = false;
		gatewayI.username = false;
		gatewayI.password = false;
		gatewayI.sensors = false;
		gatewayI.sensorId = false;
	}else if (qName.equalsIgnoreCase("gateway")) {
  		gateway = new Gateway();
        gatewayI.gateway = true;
  	}else if (qName.equalsIgnoreCase("gatewayId") && !intermediateServers) {
        gatewayI.gatewayId = true;
	}else if (qName.equalsIgnoreCase("ip") && gateways) {
        gatewayI.ip = true;
    }else if (qName.equalsIgnoreCase("port") && gateways) {
        gatewayI.port = true;
    }else if (qName.equalsIgnoreCase("username") && gateways) {
        gatewayI.username = true;
    }else if (qName.equalsIgnoreCase("password") && gateways) {
        gatewayI.password = true;
    }else if (qName.equalsIgnoreCase("sensors")) {
    	  if(gateway.sensors == null)
    		  gateway.sensors = new ArrayList<>();
        gatewayI.sensors = true;
    }else if (qName.equalsIgnoreCase("sensorId")) {
		sensor = new Sensor();
        gatewayI.sensorId = true;
	}else if (qName.equalsIgnoreCase("intermediateServers")) {
		//intermediateServer = new IntermediateServer();
    	if(topo.intermediateServer == null)
			topo.intermediateServer = new ArrayList<IntermediateServer>();
		intermediateServers = true;
		intermediateServersStructI = new intermediateServersStruct();
		intermediateServersStructI.intermediateServerId = false;
		intermediateServersStructI.gatewaysIs = false;
		intermediateServersStructI.gatewayIsId = false;
		intermediateServersStructI.intermediateServer =false;
		intermediateServersStructI.ip = false;
		intermediateServersStructI.port = false;
		intermediateServersStructI.username = false;
		intermediateServersStructI.password = false;
	}else if (qName.equalsIgnoreCase("intermediateServer")) {
		intermediateServer = new IntermediateServer();
		intermediateServersStructI.intermediateServer = true;
	}else if (qName.equalsIgnoreCase("ip") && intermediateServers) {
	 	intermediateServersStructI.ip = true;
	}else if (qName.equalsIgnoreCase("port") && intermediateServers) {
	 	intermediateServersStructI.port = true;
	}else if (qName.equalsIgnoreCase("username") && intermediateServers) {
	 	intermediateServersStructI.username = true;
	}else if (qName.equalsIgnoreCase("password") && intermediateServers) {
	 	intermediateServersStructI.password = true;
	}else if (qName.equalsIgnoreCase("intermediateServerId") && intermediateServers ) {
	 	intermediateServersStructI.intermediateServerId = true;
	}else if (qName.equalsIgnoreCase("gatewaysIs") && intermediateServers) {
		if(intermediateServer.gateways == null)
	  		  intermediateServer.gateways = new ArrayList<Gateway>();
	    intermediateServersStructI.gatewaysIs = true;
	}
	else if (qName.equalsIgnoreCase("gatewayId") && intermediateServers) {
	 	gate = new Gateway(); 
		intermediateServersStructI.gatewayIsId = true;
	}
}

   @Override
   public void endElement(String uri, 
      String localName, String qName) throws SAXException {

      if (qName.equalsIgnoreCase("appInfo")) {
    	 id = false;
         name =false;	         
      }if (qName.equalsIgnoreCase("gateways") && !intermediateServers) {
         gateways = false;
      }if (qName.equalsIgnoreCase("sensorId")) {
         gateway.sensors.add(sensor);
         sensor = null;
      }if (qName.equalsIgnoreCase("sensors")) {
         gatewayI.sensors = false;
      }if (qName.equalsIgnoreCase("gateway")) {
    	 topo.gateways.add(gateway);
         gatewayI.gateway = false;
      }if (qName.equalsIgnoreCase("intermediateServers")) {
         intermediateServers = false;
      }if (qName.equalsIgnoreCase("intermediateServer")) {
    	 topo.intermediateServer.add(intermediateServer);
         intermediateServersStructI.intermediateServer = false;
         intermediateServer = null;
      }if (qName.equalsIgnoreCase("gatewaysIs") && intermediateServers){
         intermediateServersStructI.gatewaysIs = false;
      }if (qName.equalsIgnoreCase("gatewayId") && intermediateServers) {
          intermediateServer.gateways.add(gate);
          gate = null;
       }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {

      if(id){
    	 topo.appInfo.setId(new String(ch, start, length));
         id = false;
      }else if(name){
        topo.appInfo.setName(new String(ch, start, length));
        name = false;
      }else if (gateways){ 
         if (gatewayI.gateway){
            if (gatewayI.gatewayId){
            	gateway.setGatewayId(new String(ch, start, length));
                gatewayI.gatewayId = false;
            }if (gatewayI.ip){
                gateway.setIP(new String(ch, start, length));
                gatewayI.ip = false;
            }if (gatewayI.port){
                gateway.setPort(new String(ch, start, length));
                gatewayI.port = false;
            }if (gatewayI.username){
                gateway.setUsername(new String(ch, start, length));
                gatewayI.username = false;
            }if (gatewayI.password){
                gateway.setPassword(new String(ch, start, length));
                gatewayI.password = false;
            }if (gatewayI.sensors){
               if(gatewayI.sensorId){
            	  sensor.setSensorId(new String(ch, start, length));
                  gatewayI.sensorId = false; 
               }  
            }
         }
      }else if(intermediateServers){
         if(intermediateServersStructI.intermediateServer){
        	 if(intermediateServersStructI.intermediateServerId){
                 intermediateServer.setIntermediateServerId(new String(ch, start, length));
                 intermediateServersStructI.intermediateServerId = false; 
        	 }if(intermediateServersStructI.ip){
               intermediateServer.setIP(new String(ch, start, length));
               intermediateServersStructI.ip = false; 
            }if(intermediateServersStructI.port){
            	intermediateServer.setPort(new String(ch, start, length));
               intermediateServersStructI.port = false; 
            }if(intermediateServersStructI.username){
            	intermediateServer.setUsername(new String(ch, start, length));
                intermediateServersStructI.username = false; 
             }if(intermediateServersStructI.password){
             	intermediateServer.setPassword(new String(ch, start, length));
                intermediateServersStructI.password = false; 
             }if(intermediateServersStructI.gatewaysIs){
            	if(intermediateServersStructI.gatewayIsId){
                   gate.setGatewayId(new String(ch, start, length));
                   intermediateServersStructI.gatewayIsId = false; 
                }
            }
         }
      }
   }
}
