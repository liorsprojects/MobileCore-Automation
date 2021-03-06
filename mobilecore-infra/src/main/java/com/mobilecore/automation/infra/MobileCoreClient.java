package com.mobilecore.automation.infra;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;

import com.experitest.client.Client;
import com.mobilecore.automation.infra.Utils.FormatUtils;
import com.mobilecore.automation.infra.enums.DirectionType;
import com.mobilecore.automation.infra.enums.Elements;
import com.mobilecore.automation.infra.enums.FlowType;
import com.mobilecore.automation.infra.enums.RSType;
import com.mobilecore.automation.infra.enums.SliderToggleType;
import com.mobilecore.automation.infra.fiddler.FiddlerJsonRpcClient;

public class MobileCoreClient extends SystemObjectImpl {

	// ======= SUT PARAMS =======
	private String host;
	private int port;
	private String projectBaseDirectory;
	private String seetestExecutable;
	public FiddlerJsonRpcClient fiddlerClient;
	private String apkLoc;
	
	// ==== END SUT PARAMS ======
	
	private ADBConnection adb;
	private Client mSeetestClient;

	@Override
	public void init() throws Exception {
		super.init();
		report.report("initializing MobileCoreClient...");
		startSeetestService();
		mSeetestClient = new Client(host, port);
		mSeetestClient.setProjectBaseDirectory(projectBaseDirectory);
		mSeetestClient.setReporter("xml", "reports", "mobile");
		report.report("initializing MobileCoreClient... DONE");
	}

	// TODO - retry mechanism
	public void startSeetestService() throws Exception {
		report.report("starting seetest service");
		try {
			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", seetestExecutable, "-hide");
			pb.start();
			report.report("execute command: " + seetestExecutable+ " -hide");
			Thread.sleep(4000);
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

	
	// ====== LOGCAT =======
	public boolean waitForLogcatMessage(String filter, int reportOnFail ,long timeout, boolean reportFound, String... messageFilters) throws Exception {
		boolean found = false;
		report.report("wait for logcat message filterd by " + filter + " and contains: " + FormatUtils.stringArrayToString(messageFilters, ","));
		LogcatMessageWaiter logcatMessageWaiter = new LogcatMessageWaiter();
		found = logcatMessageWaiter.wait(filter, timeout, messageFilters); 
		if(reportFound) {
			report.report((found? "" : "didn't ") + "found log contains: " + FormatUtils.stringArrayToString(messageFilters, ", "));
		}
		return found;
	}
	
	public String getMobileCoreReport(String filter, int reportOnFail ,long timeout, boolean reportFound, String... messageFilters) throws Exception {
		report.report("wait for report log message filterd by " + filter + " and contains: " + FormatUtils.stringArrayToString(messageFilters, ","));
		LogcatReportGetter logcatReportGetter = new LogcatReportGetter();
		String reportString = logcatReportGetter.wait(filter, timeout, messageFilters); 
		if(reportFound) {
			report.report((reportString != null? "" : "didn't ") + "found log contains: " + FormatUtils.stringArrayToString(messageFilters, ", "));
		}
		return reportString;
	}
	
	public void waitForRS(RSType rs, FlowType flow, int reportOnFail, long timeout) throws Exception {
		report.step("waiting for report: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"" );
		if(!waitForLogcatMessage("\"RS\"", reportOnFail ,timeout, false ,rs.getValueAsReportString(), flow.getValueAsReportString())) {
			report.report("could not find log: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"" + "after " + timeout + " milliseconds", reportOnFail);
		}
		else {
			report.step("found log: RS=\"" + rs.getValue() + "\", Flow=\"" + flow.getValue() + "\"");
		}
	}
	// ====== END LOGCAT =======
	
	
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
	
	public boolean isElementFound(SeeTestElement element) {
		return mSeetestClient.isElementFound(element.getZone().getValue(), element.getName(), element.getIndex());
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
	
	public void drag(SeeTestElement element, int xOffset, int yOffset) {
		mSeetestClient.drag(element.getZone().getValue(), element.getName(), element.getIndex(), xOffset, yOffset);
	}

	public void toggleSlider(SliderToggleType action) throws Exception {
		SeeTestElement sliderHandle = Elements.SliderElement.SLIDER_HANDLE.getElement();
		int xOffset = -1;
		if(action == SliderToggleType.OPEN) {
			xOffset = 300;
		} else if(action == SliderToggleType.CLOSE) {
			xOffset = -300;
		} else {
			throw new Exception("invalid value exception: choose OPEN/CLOSE from the SliderToggleType valuse");
		}
		
		mSeetestClient.drag(sliderHandle.getZone().getValue(), sliderHandle.getName(), sliderHandle.getIndex(), xOffset, 0);
	}
	
	public void closeSlider() {
		mSeetestClient.drag("NATIVE", "contentDescription=slider-handle", 0, -300, 0);
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
	
	public void clickBackButton() {
		mSeetestClient.sendText("{ESC}");
	}
	
	// ======== FIDDLER ========
	public void fiddlerCommand(String cmd) throws Exception {
		fiddlerClient.execute(cmd);
	}
	// ====== END FIDDLER ======

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
	
	public FiddlerJsonRpcClient getFiddlerClient() {
		return fiddlerClient;
	}

	public void setFiddlerClient(FiddlerJsonRpcClient fiddlerClient) {
		this.fiddlerClient = fiddlerClient;
	}
	

	public String getApkLoc() {
		return apkLoc;
	}

	public void setApkLoc(String apkLoc) {
		this.apkLoc = apkLoc;
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

	public ADBConnection getAdb() {
		return adb;
	}

	
	
	
	

	
}
