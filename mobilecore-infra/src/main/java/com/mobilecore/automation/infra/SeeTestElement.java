package com.mobilecore.automation.infra;

import com.mobilecore.automation.infra.enums.ZoneType;

public class SeeTestElement {
	
	private ZoneType mZone;
	private String mName;
	private int mIndex;
	
	public SeeTestElement(ZoneType zone, String name, int index) {
		this.mZone = zone;
		this.mName = name;
		this.mIndex = index;
	}
	
	public ZoneType getZone() {
		return mZone;
	}
	
	public void setZone(ZoneType zone) {
		this.mZone = zone;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public void setIndex(int index) {
		this.mIndex = index;
	}
}
