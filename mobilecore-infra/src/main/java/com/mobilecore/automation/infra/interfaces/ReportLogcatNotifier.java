package com.mobilecore.automation.infra.interfaces;

public interface ReportLogcatNotifier {
	
	public void setListener(LogcatListener listener);
	
	public void notifyListener();


}
