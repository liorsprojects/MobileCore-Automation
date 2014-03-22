package com.mobilecore.automation.infra.enums;

//TODO - add this wherever possible
public enum RSType {

	WALL("W"),
	IMPRESSION("D"),
	CLICK("C"),
	BACK("Q"),
	CLOSE("-"),
	INSATLL("+"),
	STORE("S"),
	AI("AI"),
	ERROR("E");

	private String mValue;

	private RSType(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return mValue;
	}
	
	public String getValueAsReportString() {
		return "\"RS\":\""+ mValue +"\"";
	}
}
