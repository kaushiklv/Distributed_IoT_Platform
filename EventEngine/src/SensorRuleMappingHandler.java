

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import SensorRuleMappingDataObjects.SRule;
import SensorRuleMappingDataObjects.AppData;
import SensorRuleMappingDataObjects.SRules;
public class SensorRuleMappingHandler extends DefaultHandler {
	private SRules rules = null;
	private AppData app = null;
	private SRule rule = null;
	
	private List<SRule> ruleList = null;
	
	boolean bAppId = false;
	boolean bAppName = false;
	boolean beventID = false;
	boolean bruleMID = false;
	boolean bruleId = false;
	boolean bsensorName = false;
	boolean bdataType = false;
	boolean bthreshold = false;
	
	public AppData getApp() {
		return app;
	}
	
	public SRules getRules() {
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
		      rules = new SRules(); 
		      if (ruleList == null)
		          ruleList = new ArrayList<>();
	    } else if (paramString3.equalsIgnoreCase("rule")) {
	    	rule = new SRule();
	    } else if(paramString3.equalsIgnoreCase("ruleMID")) {
	    	bruleMID = true;
	    } else if(paramString3.equalsIgnoreCase("ruleId")) {
	    	bruleId = true;
	    } else if(paramString3.equalsIgnoreCase("sensorName")) {
	    	bsensorName = true;
	    } else if(paramString3.equalsIgnoreCase("threshold")) {
	    	bthreshold = true;
	    } else if(paramString3.equalsIgnoreCase("dataType")) {
	    	bdataType = true;
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
		} else if (bruleMID) {
			rule.setRuleMId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bruleMID = false;
		} else if (bruleId) {
			rule.setRuleId(new String(paramArrayOfChar, paramInt1, paramInt2));
			bruleId = false;
		} else if (bsensorName) {
			rule.setSensorName(new String(paramArrayOfChar, paramInt1, paramInt2));
			bsensorName = false;
		} else if (bthreshold) {
			rule.setThreshold(new String(paramArrayOfChar, paramInt1, paramInt2));
			bthreshold = false;
		} else if (bdataType) {
			rule.setDataType(new String(paramArrayOfChar, paramInt1, paramInt2));
			bdataType = false;
		} 
	}
}
