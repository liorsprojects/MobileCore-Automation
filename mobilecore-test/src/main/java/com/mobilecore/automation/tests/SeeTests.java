package com.mobilecore.automation.tests;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobilecore.automation.infra.MobileCoreClient;
import com.mobilecore.automation.infra.enums.Elements;
import com.mobilecore.automation.infra.enums.FlowType;
import com.mobilecore.automation.infra.enums.RSType;

public class SeeTests extends SystemTestCase4 {

	private static MobileCoreClient mobileCoreClient;
	
	// test parameters
	private long installReportTimeout = 120000;
	private boolean uninstallAppDownload = true;
	
	//TODO - ask ohad... maby enum? - after fiddler support
	private String ow_id = "not impemented yet";

	@Before
	public void setUp() throws Exception {
		report.step("setup");
		if(mobileCoreClient == null) {
			report.report("initialize mobileCoreClient SystemObject");
			mobileCoreClient = (MobileCoreClient) system.getSystemObject("mobileCoreClient");
			mobileCoreClient.report("initialize mobileCoreClient SystemObject complete");
		}

		mobileCoreClient.report("setting device adb:Galaxy Nexus");
		mobileCoreClient.getClient().setDevice("adb:Galaxy Nexus");
		
		mobileCoreClient.clearLogcat();
		
		mobileCoreClient.report("launching MCTester");
		mobileCoreClient.getClient().launch("com.mobilecore.mctester/.MainActivity", true, true);
	}

	@Test
	@TestProperties(name="offerwall full download flow", paramsInclude = {"installReportTimeout", "uninstallAppDownload"})
	public void testOfferwallFullDownloadFlow() throws Exception {
	
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "from:LOADING , to:READY_TO_SHOW");
		report.step("offerwall is ready to show");
		
		mobileCoreClient.report("wait 3 second just in case");
		mobileCoreClient.sleep(6000);

		mobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL ,15000 , true, "from:READY_TO_SHOW , to:SHOWING");
		
		mobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		String appName = mobileCoreClient.elementGetText(Elements.OfferwallElement.INNER_ITEM_TITTLE.getElement());

		mobileCoreClient.waitForElementAndClick(Elements.OfferwallElement.INNER_ITEM.getElement(), 10000, 1);
		report.step("click on application item: " + appName);
		mobileCoreClient.waitForRS(RSType.CLICK, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.waitForRS(RSType.STORE, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		report.step("navigated to the market");

		//TODO - abstraction: make function in Helper class
		mobileCoreClient.waitForElementAndClick(Elements.MarketElement.INSTALL_BUTTON.getElement(), 10000, 1);
		mobileCoreClient.report("click on INSTALL");

		mobileCoreClient.waitForElementAndClick(Elements.MarketElement.ACCEPT_BUTTON.getElement(), 10000, 1);
		mobileCoreClient.report("click on ACCEPT");

		mobileCoreClient.waitForElement(Elements.MarketElement.DOWNLOADING_TEXT.getElement(), 10000);
		mobileCoreClient.report("start downloading...");
		mobileCoreClient.waitForElement(Elements.MarketElement.INSTALLING_TEXT.getElement(), 600000);		
		mobileCoreClient.report("finish downloading");
		mobileCoreClient.report("start instaling...");
		mobileCoreClient.waitForElement(Elements.MarketElement.OPEN_BUTTON.getElement(), 600000);
		mobileCoreClient.report("finish instaling");
		// end todo
		
		mobileCoreClient.waitForRS(RSType.INSATLL, FlowType.OFFERWALL, Reporter.WARNING, installReportTimeout);

		//TODO - abstraction: make function in Helper class
		mobileCoreClient.waitForElementAndClick(Elements.MarketElement.UNINSTALL_BUTTON.getElement(), 10000, 1);
		mobileCoreClient.report("click UNINSTALL button");

		mobileCoreClient.waitForElement(Elements.MarketElement.CONFIRM_UNINSTALL_TEXT.getElement(), 3000);
		mobileCoreClient.click(Elements.MarketElement.CONFIRM_OK.getElement(), 1);
		mobileCoreClient.report("click OK button");

		mobileCoreClient.waitForElement(Elements.MarketElement.INSTALL_BUTTON.getElement(), 60000);
		mobileCoreClient.report("uninstall finished");
		//end todo
		
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@After
	public void tearDown() throws Exception {
		mobileCoreClient.getClient().generateReport();
	}

	public long getInstallReportTimeout() {
		return installReportTimeout;
	}

	@ParameterProperties(description="how long to wait for the \"+\"", section = "RS")
	public void setInstallReportTimeout(long installReportTimeout) {
		this.installReportTimeout = installReportTimeout;
	}

	public boolean isUninstallAppDownload() {
		return uninstallAppDownload;
	}

	@ParameterProperties(description="uninstall application after test")
	public void setUninstallAppDownload(boolean uninstallAppDownload) {
		this.uninstallAppDownload = uninstallAppDownload;
	}
	
	
}

