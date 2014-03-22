package com.mobilecore.automation.infra;

import com.mobilecore.automation.infra.interfaces.LogcatListener;


public class LogcatMessageWaiter {

	boolean mFound = false;
	LogcatListener mListener;

	public LogcatMessageWaiter() {
		
		mListener = new LogcatListener() {

			@Override
			public void onNotify() {
				System.out.println("listener is notified");
				mFound = true;
			}
		};
	}

	public boolean wait(String filter, long timeout, String... messageFilters) throws Exception {

		FindReportTask task = new FindReportTask(filter, messageFilters);
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
		return mFound;
	}

}