package com.mobilecore.automation.infra;

import com.mobilecore.automation.infra.interfaces.LogcatListener;


public class LogcatReportGetter {

	boolean mFound = false;
	LogcatListener mListener;
	String reportJson;

	public LogcatReportGetter() {
		
		mListener = new LogcatListener() {

			@Override
			public void onNotify(String reportMessage) {
				System.out.println("listener is notified");
				reportJson = reportMessage;
				mFound = true;
			}

			@Override
            public void onNotify() {
	            // TODO Auto-generated method stub
	            
            }
		};
	}
	
	public String wait(String filter, long timeout, String... messageFilters) throws Exception {

		GetReportTask task = new GetReportTask(filter, messageFilters);
		task.setListener(mListener);
		Thread t = new Thread(task);
		t.start();

		final long start = System.currentTimeMillis();

		while (!mFound) {

			if ((System.currentTimeMillis() - start > timeout)) {
				System.out.println("could not find after 10000 millis");
				task.stop();
				t.interrupt();
				break;
			}
			System.out.println("sleep 1000");
			Thread.sleep(1000);
		}
		return reportJson;
	}

}