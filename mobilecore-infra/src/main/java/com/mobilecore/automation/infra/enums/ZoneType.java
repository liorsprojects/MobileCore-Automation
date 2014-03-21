package com.mobilecore.automation.infra.enums;

public enum ZoneType {
	NATIVE("NATIVE"),
	WEB("WEB"),
	DEFAULT("default"),
	TEXT("TEXT");

	private String mZone;

	private ZoneType(String zone) {
		this.mZone = zone;
	}

	public String getValue() {
		return mZone;
	}

	public static ZoneType convert(String zone) {
		for (ZoneType zoneType : ZoneType.values()) {
			if (zoneType.getValue().equals(zone)) {
				return zoneType;
			}
		}
		return null;
	}
}
