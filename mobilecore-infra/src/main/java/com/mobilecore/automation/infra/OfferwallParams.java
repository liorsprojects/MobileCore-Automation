package com.mobilecore.automation.infra;

public class OfferwallParams {

	private String owId;
	private int owAdCount;
	private boolean filter = true;

	public String getOwId() {
		return owId;
	}

	public void setOwId(String owId) {
		this.owId = owId;
	}

	public int getOwAdCount() {
		return owAdCount;
	}

	public void setOwAdCount(int owAdCount) {
		this.owAdCount = owAdCount;
	}

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

}
