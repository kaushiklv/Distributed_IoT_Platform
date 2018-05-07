 package xmlParser;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TopologyParser {
	
	public TopologyData getTopologyData(String xmlPath) {
		
		TopologyData userhandler = null;
		try {
	         File inputFile = new File(xmlPath);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         userhandler = new TopologyData();
	         saxParser.parse(inputFile, userhandler);   
	         
//	         System.out.println(userhandler.topo.gateways.size());
//	         List<IntermediateServer> ISList = userhandler.topo.getIntermediateServer();
//	         for(int i = 0; i < ISList.size(); i++){
//	        	 System.out.println(ISList.get(i).getIP());
//	        	 System.out.println(ISList.get(i).getPort());
//	         }
	         
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      }
		return userhandler;
	}
	
//	public static void main(String[] args) {
//		String xmlPath = "healthcare/topology.xml";
//		TopologyParser topoParser = new TopologyParser();
//		TopologyData data = topoParser.getTopologyData(xmlPath);
//		
//		List<Gateway> gwList = data.topo.getGateways();
//		
//		for(Gateway gate : gwList) {
//			System.out.println(gate.getUsername());
//			System.out.println(gate.getPassword());
//		}
//		
//		List<IntermediateServer> IsList = data.topo.getIntermediateServer();
//		for(IntermediateServer is : IsList) {
//			System.out.println(is.getIntermediateServerId());
//			System.out.println(is.getUsername());
//			System.out.println(is.getPassword());
//			for(Gateway gate : is.getGateways()) {
//				System.out.println(gate.getGatewayId());
//			}
//		}
//	}  
}
