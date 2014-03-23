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
		
		mobileCoreClient.report("wait 6 second just in case");
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

		//TODO - add "right application verification" (native, text=<app name> 
		
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

	@Test
	@TestProperties(name="close offerwall with X button", paramsInclude={})
	public void testOfferwallCloseWithXButton() throws Exception {
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "mReadyToShowOfferwallFromFlow to true");
		report.step("offerwall is ready to show");
		mobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");
		
		mobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL ,15000 , true, "from:READY_TO_SHOW , to:SHOWING");
		
		mobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.waitForElementAndClick(Elements.OfferwallElement.X_BUTTON.getElement(), 10000, 1);
		//TODO - verify gone...
		mobileCoreClient.waitForRS(RSType.CLOSE, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}
	
	@Test
	@TestProperties(name="close offerwall with BACK button", paramsInclude={})
	public void testOfferwallCloseWithBackButton() throws Exception {
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "from:LOADING , to:READY_TO_SHOW");
		report.step("offerwall is ready to show");
		
		//TODO - THIS IS A BUG WORKAROUND -> in this state the wall isn't really ready to show... (all tests)
		mobileCoreClient.report("wait 6 second just in case");
		mobileCoreClient.sleep(6000);

		mobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");

		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL ,15000 , true, "from:READY_TO_SHOW , to:SHOWING");
		
		mobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.getClient().sendText("{ESC}");
		mobileCoreClient.waitForRS(RSType.BACK, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@Test
	@TestProperties(name="close stickeez handle with X button", paramsInclude={})
	public void testStickeezHandleCloseWithXButton() throws Exception {
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mobileCoreClient.waitForLogcatMessage("StickeezManager", Reporter.FAIL, 15000, true, "checkAllReady switching state | called");
		report.step("stickeez is ready");
		mobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");
		
		mobileCoreClient.click(Elements.MCTesterElement.SHOW_STICKEE.getElement(), 1);
		report.step("click 'Show stickke' button");
		
		//TODO - insert logic to determine if the flow is regular stickeez-handle or stickeez-direct-handle
		//mobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		
		mobileCoreClient.waitForElementAndClick(Elements.StickeezElement.X_BUTTON.getElement(), 10000, 1);
		
		//TODO - verify gone...
		//again insert logic for direct or regular flow 
		//mobileCoreClient.waitForRS(RSType.CLOSE, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mobileCoreClient.waitForElementToVanish(Elements.StickeezElement.STICKEEZ_HANDLE.getElement(), 10000);
		
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}
	
	@Test
	@TestProperties(name="open/close slider with handle", paramsInclude={})
	public void testSliderOpenClose() throws Exception {
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");
		mobileCoreClient.sleep(3000);
		
		openSlider();
		mobileCoreClient.sleep(2000);
		
		closeSlider();
		mobileCoreClient.sleep(2000);
				
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}
	
	@After
	public void tearDown() throws Exception {
		mobileCoreClient.getClient().generateReport();
	}

	// INFRA CANIDATE FUNCTION
	
	public void openSlider() {
		mobileCoreClient.getClient().drag("NATIVE", "contentDescription=slider-handle", 0, 300, 0);
	}
	
	public void closeSlider() {
		mobileCoreClient.getClient().drag("NATIVE", "contentDescription=slider-handle", 0, -300, 0);
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

