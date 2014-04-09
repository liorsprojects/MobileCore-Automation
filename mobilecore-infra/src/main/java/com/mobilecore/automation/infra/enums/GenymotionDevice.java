package com.mobilecore.automation.infra.enums;

//TODO - add this wherever possible
public enum GenymotionDevice {

	GOOGLE_GALAXY_NEXUS_4_2_2_API_17_720X1280("Google Galaxy Nexus - 4.2.2 - API 17 - 720x1280"),
	SONY_XPERIA_TABLET_Z_4_1_1_API_16_1920X1200("Sony Xperia Tablet Z - 4.1.1 - API 16 - 1920x1200"),
	GOOGLE_NEXUS_5_4_4_2_API_19_1080X1920("Google Nexus 5 - 4.4.2 - API 19 - 1080x1920");
	
	

	private String mValue;

	private GenymotionDevice(String value) {
		this.mValue = value;
	}

	public String getValue() {
		return mValue;
	}
	
	@Override
	public String toString() {
		return mValue; 
	}
}
