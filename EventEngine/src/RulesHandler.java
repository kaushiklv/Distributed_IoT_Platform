

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import RulesDataObjects.AppData;
import RulesDataObjects.Rule;
import RulesDataObjects.Rules;

public class RulesHandler extends DefaultHandler {
	private Rules rules = null;
	private AppData app = null;
	private Rule rule = null;
	
	private List<Rule> ruleList = null;
	
	boolean bAppId = false;
	boolean bAppName = false;
	boolean beventID = false;
	boolean bruleId = false;
	boolean bruleName = false;
	boolean bnoOfParams = false;
	
	public AppData getApp() {
		return app;
	}
	
	public Rules getRules() {
		return rules;
	}
	
	public void startElement(String paramString1, String paramString2, 
			String paramString3, Attributes paramAttributes) throws SAXException {
		
	    if (paramString3.equalsIgnoreCase("appInfo")) {
	      app = new AppData();	      
	    } else if (paramString3.equalsIgnoreCase("name")) {
	      bAppName = true;
	    } else if (paramString3.equalsIgnoreCase("id")) {
	      bAppId = true;
	    }
	    
	    if (paramString3.equalsIgnoreCase("rules")) {
		      rules = new Rules(); 
		      if (ruleList == null)
		          ruleList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("rule")) {
	    	rule = new Rule();
	    } else if(paramString3.equalsIgnoreCase("ruleId")) {
	    	bruleId = true;
	    } else if(paramString3.equalsIgnoreCase("ruleName")) {
	    	bruleName = true;
	    } else if(paramString3.equalsIgnoreCase("noOfParams")) {
	    	bnoOfParams = true;
	    }
	}
	
	public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
		if (paramString3.equalsIgnoreCase("rules")) {
			rules.setRuleList(ruleList);
			ruleList = null;
		}
		if (paramString3.equalsIgnoreCase("rule")) {
			ruleList.add(rule);
		}
	}
	
	public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
		if (bAppId) {
			app.setId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bAppId = false;
		} else if (bAppName) {
			app.setName(new String(paramArrayOfChar, paramInt1, paramInt2));
			bAppName = false;
		} else if (bruleId) {
			rule.setRuleId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bruleId = false;
		} else if (bruleName) {
			rule.setRuleName(new String(paramArrayOfChar, paramInt1, paramInt2));
			bruleName = false;
		} else if (bnoOfParams) {
			rule.setNoOfParams(new String(paramArrayOfChar, paramInt1, paramInt2));
			bnoOfParams = false;
		}
	}
}
