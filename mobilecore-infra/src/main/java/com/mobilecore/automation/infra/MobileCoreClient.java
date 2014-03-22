package com.mobilecore.automation.infra;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;

import com.experitest.client.Client;
import com.mobilecore.automation.infra.Utils.FormatUtils;
import com.mobilecore.automation.infra.enums.FlowType;
import com.mobilecore.automation.infra.enums.RSType;

public class MobileCoreClient extends SystemObjectImpl {

	private Client mSeetestClient;
	private String host;
	private int port;
	private String projectBaseDirectory;
	private String seetestExecutable;

	@Override
	public void init() throws Exception {
		super.init();
		startSeetestService();
		mSeetestClient = new Client(host, port);
		mSeetestClient.setProjectBaseDirectory(projectBaseDirectory);
		mSeetestClient.setReporter("xml", "reports", "mobile");
	}

	// TODO - retry mechanism
	public void startSeetestService() throws Exception {
		report.report("starting seetest service");
		try {
			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", seetestExecutable, "-hide");
			pb.start();
			report.report("execute connad: " + seetestExecutable+ " -hide");
			Thread.sleep(2000);
			String line;

			Process p = Runtime.getRuntime().exec("tasklist.exe");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.contains("studio.exe")) {
					report.report("service begin");
				}
			}
			input.close();

		} catch (Exception e) {
			report.report("fail to run seetest in hide mode");
		}
		report.report("waiting for seetest to load");
		Thread.sleep(8000);

	}

	public void stopSeetestService() throws Exception {
		
		boolean success = false;
		Process pr =  Runtime.getRuntime().exec("taskkill /IM " + "studio.exe " + "/t /F");
		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.contains("SUCCESS")) {
				success = true;
			}
		}
		pr.waitFor();
		in.close();
		report.report("stop Seetest service " + (success ? "succeed " : "fail"));
	}

	public boolean waitForLogcatMessage(String filter, int reportOnFail ,int timeout, boolean reportFound, String... messageFilters) throws Exception {
		boolean found = false;
		report.report("wait for logcat message filterd by " + filter + " and contains: " + FormatUtils.stringArrayToString(messageFilters, ","));
		LogcatMessageWaiter logcatMessageWaiter = new LogcatMessageWaiter();
		StringBuffer sb = null;
		found = logcatMessageWaiter.wait(filter, timeout, messageFilters); 
		if(reportFound) {
			report.report((found? "" : "didn't ") + "found log contains: " + FormatUtils.stringArrayToString(messageFilters, ", "));
		}
		return found;
	}
	
	public void waitForRS(RSType rs, FlowType flow, int reportOnFail, long timeout) throws Exception {
		report.step("waiting for report: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"" );
		if(!waitForLogcatMessage("\"RS\"", reportOnFail ,120000, false ,rs.getValueAsReportString(), flow.getValueAsReportString())) {
			report.step("could not find log: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"" + "after " + timeout + " milliseconds");
		}
		else {
			report.step("found log: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"");
		}
	}
	
	public void sleep(int timeInMillis) {
		report.report("waiting for " + timeInMillis + "millis");
		mSeetestClient.sleep(timeInMillis);
	}

	public Client getClient() {
		return mSeetestClient;
	}
	
	public void waitForElement(SeeTestElement element, int waitTimeout) throws Exception {
		if(!mSeetestClient.waitForElement(element.getZone().getValue(), element.getName(), element.getIndex(), waitTimeout)) {
			throw new Exception("Timeout: Element " + element.getName() + " not found.");
		}
		report.report("Element " + element.getName() + " found");
	}
	
	public void waitForElementToVanish(SeeTestElement element, int waitTimeout) throws Exception {
		if(!mSeetestClient.waitForElementToVanish(element.getZone().getValue(), element.getName(), element.getIndex(), waitTimeout)) {
			throw new Exception("Timeout: Element " + element.getName() + " did not vanish.");
		}
	}
	
	public String elementGetText(SeeTestElement element) throws Exception {
		String text = null;
		if(!mSeetestClient.isElementFound(element.getZone().getValue(), element.getName(), element.getIndex())) {
			throw new Exception("element " + element.getName() + " not found");
		}
		text = mSeetestClient.elementGetText(element.getZone().getValue(), element.getName(), element.getIndex());
		if(text == null) {
			throw new Exception("element " + element.getName() + " does not have text property");
		}
		return text;
	}
	
	public void click(SeeTestElement element, int count) {
		mSeetestClient.verifyElementFound(element.getZone().getValue(), element.getName(), element.getIndex());
		mSeetestClient.click(element.getZone().getValue(), element.getName(), element.getIndex(), count);
		report.report("click on element " + element.getName());
	}
	
	/**
	 * 
	 * @param element - string identifier or well formed selector
	 * @param waitTimeout - how long to wait for an element in millis 
	 * @param count - click count
	 * @return
	 * @throws Exception 
	 */
	public void waitForElementAndClick(SeeTestElement element, int waitTimeout, int count) throws Exception {

		waitForElement(element, waitTimeout);
		mSeetestClient.click(element.getZone().getValue(), element.getName(), element.getIndex(), count);
		report.report("click on element " + element.getName());
	}
	
	public void report(String message) {
		report.report(message, true);
		mSeetestClient.report(message, true);
	}
	
	public void report(String message, boolean status) {
		report.report(message, status);
		mSeetestClient.report(message, status);
	}
	
	public void clearLogcat() {
		report.report("clear logcat");
		mSeetestClient.run("logcat -c");
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProjectBaseDirectory() {
		return projectBaseDirectory;
	}

	public void setProjectBaseDirectory(String projectBaseDirectory) {
		this.projectBaseDirectory = projectBaseDirectory;
	}

	public String getSeetestExecutable() {
		return seetestExecutable;
	}

	public void setSeetestExecutable(String seetestExecutable) {
		this.seetestExecutable = seetestExecutable;
	}

	@Override
	public void close() {
		report.report("closing seetest system object");
		try {
			stopSeetestService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.close();
	}

	// ===================== Example use =====================
	public static void main(String[] args) throws Exception {

		MobileCoreClient client = new MobileCoreClient();
		// client.waiter.wait("\"RS\"",10000 ,"\"RS\":\"D\"",
		// "\"Flow\":\"offerwall\"");
		//client.waitForLogcatMessage("OfferwallManager", Reporter.FAIL ,10000, "from:READY_TO_SHOW , to:SHOWING");
	}

	@Deprecated
	public void waitForReport(String rs, long timeout) throws Exception {
		boolean found = false;
		final long start = System.currentTimeMillis();
		while (!found) {
			if (System.currentTimeMillis() - start > timeout) {
				throw new Exception("Cound not find report " + rs + " in " + timeout + " millis");
			}
			ProcessBuilder ps = new ProcessBuilder("adb logcat -s \"MobileCore\"");
			ps.redirectErrorStream(true);
			Process pr = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("SUCCESS")) {
					found = true;
				}
			}
		}

	}

	

	
}
