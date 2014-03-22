package com.mobilecore.automation.infra.enums;

public enum FlowType {

	OFFERWALL("offerwall"),
	STICKEEZ("stickeez"),
	STICKEEZ_DIRECT("stickeez_direct"),
	STICKEEZ_HANDLE("stickeez_handle"),
	STICKEEZ_DIRECT_HANDLE("stickeez_direct_handle"),
	SLIDER("slider"),
	DIRECT_TO_MARKET("direct_to_market");

	private String mValue;

	private FlowType(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return mValue;
	}
	
	public String getValueAsReportString() {
		return  "\"Flow\":\""+ mValue +"\"";
	}
	
}
