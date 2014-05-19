package com.mobilecore.automation.infra;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.mobilecore.automation.infra.interfaces.LogcatListener;
import com.mobilecore.automation.infra.interfaces.ReportLogcatNotifier;

public class GetReportTask implements Runnable, ReportLogcatNotifier {

	private String mFilter;
	private String mSearchParams[];
	private LogcatListener mLogcatListener;
	private Process mProccess;

	public GetReportTask(String filter, String... searchParams) {
		this.mFilter = filter;
		this.mSearchParams = searchParams;
	}

	@Override
	public void run() {
		try {
			ProcessBuilder ps = new ProcessBuilder("adb", "shell", "logcat | grep -F '" + mFilter + "'");

			ps.redirectErrorStream(true);
			mProccess = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(mProccess.getInputStream()));
			String line;

			while (!(Thread.currentThread().isInterrupted()) & (line = in.readLine()) != null) {
				
				System.out.println(line);
				if (stringContainsAllItems(line)) {
					System.out.println("========Found report=========");
					mProccess.destroy();
					System.out.println("========Proccess destroy=========");
					notifyListener(line);
					System.out.println("========After notify listener=========");
				}
			}
		} catch (Exception e) {

		}
	}

	private boolean stringContainsAllItems(String logLine)
	{
		boolean contains = true;
	    for (String str : mSearchParams) {
			if(!logLine.contains(str)) {
				contains = false;
				break;
			}
		}       
	    return contains;
	}
	
	@Override
	public void setListener(LogcatListener listener) {
		this.mLogcatListener = listener;
	}

	@Override
	public void notifyListener() {
		//not implemented
	}
	
	public void stop() {
		mProccess.destroy();
	}

	@Override
    public void notifyListener(String report) {
	    mLogcatListener.onNotify(report);
	    
    }

}
