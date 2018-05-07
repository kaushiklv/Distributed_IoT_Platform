package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WorkflowData extends DefaultHandler {
	boolean id = false;
	   boolean name = false;
	   boolean works =false;
	   
	   public AppWorkflow appworkflow;
	   Workflow workflow;

	  class workStruct{
	      boolean work;
	      boolean workflowId;
	      boolean workflowName;
	      boolean workflowFileName;
	   }
	   workStruct workI;


	   @Override
	   public void startElement(
	      String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	     
	     if(qName.equalsIgnoreCase("appInfo")){
	      appworkflow = new AppWorkflow();
	      appworkflow.appInfo = new AppData();
	     } 
	     else if (qName.equalsIgnoreCase("id")) {
	        id = true;
	      } else if (qName.equalsIgnoreCase("name")) {
	         name = true;
	      }
	      else if (qName.equalsIgnoreCase("workflows")) {

	         works =true;
	        appworkflow.workflows = new ArrayList<Workflow>();
	         workI = new workStruct();
	         workI.work = false;
	         workI.workflowName = false;
	         workI.workflowFileName = false;
	      }
	      else if (qName.equalsIgnoreCase("workflow")) {
	         workflow = new Workflow();
	            workI.work = true;
	      }else if (qName.equalsIgnoreCase("workflowId")) {
	            workI.workflowId = true;
	      }else if (qName.equalsIgnoreCase("workflowName")) {
	            workI.workflowName = true;
	      } else if (qName.equalsIgnoreCase("workflowFileName")) {
	            workI.workflowFileName = true;
	      }

	   }

	   @Override
	   public void endElement(String uri, 
	      String localName, String qName) throws SAXException {

	      
	      if (qName.equalsIgnoreCase("appInfo")) {
	         id = false;
	         name = false;
	      }
	      if (qName.equalsIgnoreCase("workflows")) {
	         works = false;
	      }
	      if (qName.equalsIgnoreCase("workflow")) {
	        appworkflow.workflows.add(workflow);
	        workflow = null;
	         workI.work = false;
	      }
	   }

	   @Override
	   public void characters(char ch[], int start, int length) throws SAXException {

	      if(id){
	        appworkflow.appInfo.id = new String(ch, start, length);
	         id = false;
	      }else if(name){
	        appworkflow.appInfo.name = new String(ch, start, length);
	         name = false;
	      }else if (works){
	         if (workI.work){
	            if (workI.workflowName){
	              workflow.workflowName = new String(ch, start, length);
	                workI.workflowName = false;
	            }
	            if (workI.workflowId){
	              workflow.workflowId = new String(ch, start, length);
	                workI.workflowId = false;
	            }
	            if (workI.workflowFileName){
	              workflow.workflowFileName = new String(ch, start, length);
	                workI.workflowFileName = false;
	            }
	         }
	      }
	   }
}
