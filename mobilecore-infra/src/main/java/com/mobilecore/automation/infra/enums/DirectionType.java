package com.mobilecore.automation.infra.enums;

//TODO - add this wherever possible
public enum DirectionType {

	UP("Up"),
	DOWN("Down"),
	LEFT("Left"),
	RIGHT("Right");

	private String mValue;

	private DirectionType(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return mValue;
	}
}
