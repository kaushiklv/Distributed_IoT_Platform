package xmlParser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RulesData extends DefaultHandler {
	public AppRule apprule;
	Rule Rule;
	boolean id = false;
	boolean name = false;
	boolean rules = false;

   class ruleStruct{
	boolean rule;
	boolean ruleId;
	boolean ruleName;
	boolean noOfParams;
   }ruleStruct ruleI;

   @Override
   public void startElement(
      String uri, String localName, String qName, Attributes attributes)
      throws SAXException {

     if(qName.equalsIgnoreCase("appInfo")){
		apprule = new AppRule();
		apprule.appInfo = new AppData();
	}else if (qName.equalsIgnoreCase("id")) {
		id = true;
	}else if (qName.equalsIgnoreCase("name")) {
        name = true;
    }else if (qName.equalsIgnoreCase("rules")) {
		Rule = new Rule();
		apprule.rules = new ArrayList<Rule>();
		rules =true;
		ruleI = new ruleStruct();
		ruleI.rule = false;
		ruleI.ruleId = false;
		ruleI.ruleName = false;
		ruleI.noOfParams = false;
	}else if (qName.equalsIgnoreCase("rule")) {
  		Rule = new Rule();
        ruleI.rule = true;
  	}else if (qName.equalsIgnoreCase("ruleId")) {
        ruleI.ruleId = true;
	}else if (qName.equalsIgnoreCase("ruleName")) {
        ruleI.ruleName = true;
    }else if (qName.equalsIgnoreCase("noOfParams")) {
        ruleI.noOfParams = true;
    }
}

   @Override
   public void endElement(String uri, 
      String localName, String qName) throws SAXException {

      if (qName.equalsIgnoreCase("appInfo")) {
    	 id = false;
         name =false;	         
      }if (qName.equalsIgnoreCase("rules")) {
         rules = false;
      }if (qName.equalsIgnoreCase("rule")) {
         apprule.rules.add(Rule);
         Rule = null;
         ruleI.rule = false;
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {

      if(id){
    	 apprule.appInfo.id = new String(ch, start, length);
         id = false;
      }else if(name){
        apprule.appInfo.name = new String(ch, start, length);
        name = false;
      }else if (rules){ 
         if (ruleI.rule){
            if (ruleI.ruleId){
            	Rule.ruleId = new String(ch, start, length);
                ruleI.rule = false;
            }if (ruleI.ruleName){
            	Rule.ruleName = new String(ch, start, length);
                ruleI.ruleName = false;
            }if (ruleI.noOfParams){
               Rule.noOfParams = new String(ch, start, length);
                ruleI.noOfParams = false;
            }
         }
      }
   }
}
