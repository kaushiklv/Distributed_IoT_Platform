/**
 * 
 */
package RulesDataObjects;


public class Rule {
	String ruleId;
	String ruleName;
	String noOfParams;
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getNoOfParams() {
		return noOfParams;
	}

	public void setNoOfParams(String noOfParams) {
		this.noOfParams = noOfParams;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

}
