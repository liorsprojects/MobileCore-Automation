package com.mobilecore.automation.tests;

import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobilecore.automation.infra.SeeTestClient;
import com.mobilecore.automation.infra.SeeTestElement;
import com.mobilecore.automation.infra.enums.Elements;
import com.mobilecore.automation.infra.enums.ZoneType;

public class SeeTests extends SystemTestCase4 {

	private static SeeTestClient seeTestClient;	
	
	@Before
	public void setUp() throws Exception {
		report.step("setup");
		if(seeTestClient == null) {
			report.report("initialize seeTestClient SystemObject");
			seeTestClient = (SeeTestClient) system.getSystemObject("seeTestClient");
		}
				
		seeTestClient.clearLogcat();
		
		seeTestClient.getClient().setDevice("adb:Galaxy Nexus");
		seeTestClient.getClient().launch("com.mobilecore.mctester/.MainActivity", true, true);
	}

	@Test
	public void testOfferwallFullDownloadFlow() throws Exception {

		seeTestClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		seeTestClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000 ,"from:LOADING , to:READY_TO_SHOW");
		report.step("offerwall is ready to show");
		
		seeTestClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");
		
		seeTestClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL ,15000 ,"from:READY_TO_SHOW , to:SHOWING");
		report.step("waiting for report \"RS\":\"W\"");
		seeTestClient.waitForLogcatMessage("\"RS\"", 10000 ,Reporter.FAIL, "\"RS\":\"W\"", "\"Flow\":\"offerwall\"");
		report.step("waiting for report \"RS\":\"D\"");
		seeTestClient.waitForLogcatMessage("\"RS\"", 10000, Reporter.FAIL, "\"RS\":\"D\"", "\"Flow\":\"offerwall\"");
		
		String appName = seeTestClient.elementGetText(Elements.OfferwallElement.INNER_ITEM_TITTLE.getElement());

		seeTestClient.waitForElementAndClick(Elements.OfferwallElement.INNER_ITEM.getElement(), 10000, 1);
		report.step("click on application item: " + appName);
		report.step("waiting for report \"RS\":\"C\"");
		seeTestClient.waitForLogcatMessage("\"RS\"", Reporter.FAIL, 10000, "\"RS\":\"C\"", "\"Flow\":\"offerwall\"");

		report.step("waiting for report \"RS\":\"S\"");
		report.step("navigated to the market");
		seeTestClient.waitForLogcatMessage("\"RS\"", Reporter.FAIL, 30000, "\"RS\":\"S\"", "\"Flow\":\"offerwall\"");
		
		seeTestClient.waitForElementAndClick(Elements.MarketElement.INSTALL_BUTTON.getElement(), 10000, 1);
		report.report("click on INSTALL");
	
		seeTestClient.waitForElementAndClick(Elements.MarketElement.ACCEPT_BUTTON.getElement(), 10000, 1);
		report.report("click on ACCEPT");
		
		seeTestClient.waitForElement(Elements.MarketElement.DOWNLOADING_TEXT.getElement(), 10000);
		report.report("start downloading...");
		seeTestClient.waitForElement(Elements.MarketElement.INSTALLING_TEXT.getElement(), 600000);		
		report.report("finish downloading");
		report.report("start instaling...");
		seeTestClient.waitForElement(Elements.MarketElement.OPEN_BUTTON.getElement(), 600000);
		report.report("finish instaling");
			
		report.step("waiting for report \"RS\":\"+\"");
		seeTestClient.waitForLogcatMessage("\"RS\"", Reporter.WARNING ,120000 ,"\"RS\":\"+\"", "\"Flow\":\"offerwall\"");
		
		seeTestClient.waitForElementAndClick(Elements.MarketElement.UNINSTALL_BUTTON.getElement(), 10000, 1);
		report.report("click UNINSTALL button");
		
		seeTestClient.waitForElement(Elements.MarketElement.CONFIRM_UNINSTALL_TEXT.getElement(), 3000);
		seeTestClient.click(Elements.MarketElement.CONFIRM_OK.getElement(), 1);
		report.report("click OK button");
		
		seeTestClient.waitForElement(Elements.MarketElement.INSTALL_BUTTON.getElement(), 60000);
		
		seeTestClient.getClient().applicationClearData("com.mobilecore.mctester/.MainActivity");
		seeTestClient.getClient().applicationClose("com.mobilecore.mctester/.MainActivity");
		seeTestClient.getClient().applicationClose("com.android.vending");
	}

	@After
	public void tearDown() throws Exception {
		seeTestClient.getClient().generateReport();
	}
}

