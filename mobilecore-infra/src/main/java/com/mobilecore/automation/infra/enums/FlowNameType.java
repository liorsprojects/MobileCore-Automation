package com.mobilecore.automation.infra.enums;

public enum FlowNameType {

	OFFERWALL("offerWall"),
	SLIDER("Slider"),
	STICKEEZ_ANIM_PRESENT_DIRECT("stickeez_anim_present_direct"),
	STICKEEZ_ANIM_APP_OF_THE_DAY_DIRECT("stickeez_anim_app_of_day_direct"),
	STICKEEZ_ANIM_CRAB_AUTO_OPEN("stickeez_anim_crab_auto_open");


	private String mValue;

	private FlowNameType(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return mValue;
	}
	
	public String getValueAsReportString() {
		return  "\"FlowName\":\""+ mValue +"\"";
	}
}
