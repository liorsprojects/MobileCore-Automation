package com.mobilecore.automation.infra.enums;

public enum SliderToggleType {
	OPEN, CLOSE;

	public String getValueAsString() {
		return this.name().toLowerCase();
	}
}
