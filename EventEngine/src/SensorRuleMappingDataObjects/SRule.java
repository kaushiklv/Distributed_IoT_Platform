/**
 * 
 */
package SensorRuleMappingDataObjects;

public class SRule {
	String ruleMid;
	String ruleId;
	String sensorName;
	String dataType;
	String threshold;
	
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getRuleMId() {
		return ruleMid;
	}

	public void setRuleMId(String ruleMid) {
		this.ruleMid = ruleMid;
	}
}
