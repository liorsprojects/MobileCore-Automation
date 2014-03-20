package com.mobilecore.automation.tests;

import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobilecore.automation.infra.SeeTestClient;

public class SeeTests extends SystemTestCase4 {

	private static SeeTestClient seeTestClient;
	
	@Before
	public void setUp() throws Exception {
	
		if(seeTestClient == null) {
			seeTestClient = (SeeTestClient) system.getSystemObject("seeTestClient");
		}
		
		seeTestClient.startSeetestService();
		
		seeTestClient.clearLogcat();
		
		seeTestClient.getClient().setDevice("adb:Galaxy Nexus");
		seeTestClient.getClient().launch("com.mobilecore.mctester/.MainActivity", true, true);
	}

	@Test
	public void testFullDownloadFlow() throws Exception {

		if (!seeTestClient.getClient().waitForElement("default", "Show if ready", 0, 5000)) {
			report.report("app not loaded", false);
		}
		report.report("app started");

		seeTestClient.waitForLogcatMessage("OfferwallManager", 15000 ,"from:LOADING , to:READY_TO_SHOW");

		seeTestClient.sleep(4000);
		
		seeTestClient.getClient().click("default", "Show if ready", 0, 1);
		
		seeTestClient.waitForLogcatMessage("OfferwallManager", 15000 ,"from:READY_TO_SHOW , to:SHOWING");
		
		if (!seeTestClient.getClient().waitForElement("WEB", "css=#noThanks", 0, 10000)) {
			report.report("offer wall didnt show or element not present (\"noThanks\")", false);
		}
		
		seeTestClient.waitForLogcatMessage("\"RS\"",10000 ,"\"RS\":\"D\"", "\"Flow\":\"offerwall\"");

		String appName = seeTestClient.getClient().elementGetText("WEB", "css=.title", 0);

		report.report("app name: " + appName);

		if (!seeTestClient.getClient().waitForElement("WEB", "css=.inner_item", 0, 5000)) {
			report.report("offer wall didnt show or element not present (\"inner_item\")", false);
		}

		seeTestClient.getClient().click("WEB", "css=.inner_item", 0, 1);
		
		if (!seeTestClient.getClient().waitForElement("default", "INSTALL", 0, 10000)) {
			report.report("could not find INSTALL element", false);
		}
		seeTestClient.getClient().click("default", "INSTALL", 0, 1);
		
		if (!seeTestClient.getClient().waitForElement("default", "ACCEPT", 0, 10000)) {
			report.report("could not find ACCEPT element", false);
		}		
		seeTestClient.getClient().click("default", "ACCEPT", 0, 1);
		
//		adb.startUiAutomatorServer();
//		uiautomatorClient = DeviceClient.getUiAutomatorClient("http://127.0.0.1:9008");
//
//		report.step("waiting for playstore");
//
//		Selector openSelector = new Selector().setText("OPEN");
//		Selector updateSelector = new Selector().setText("UPDATE");
//		Selector notCountrySelector = new Selector().setText(PlayStoreMessage.COUNTRY_SUPPORT);
//		Selector notSupporSelector = new Selector().setText(PlayStoreMessage.DEVICE_COMPATIBLE);
//
//		if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 5000)) {
//			if (uiautomatorClient.exist(openSelector) || uiautomatorClient.exist(updateSelector)) {
//				report.report("the app: " + appName + "is allready installed");
//			} else if (uiautomatorClient.exist(notCountrySelector)) {
//				report.report("the app: " + appName + "is not suppoorted in our country");
//			} else if (uiautomatorClient.exist(notSupporSelector)) {
//				report.report("the app: " + appName + "is incompatible with yor device");
//			} else {
//				throw new Exception("Did not navigated to Playstore (check internet connection or proxy settings)");
//			}
//			uiautomatorClient.pressKey("back");
//		}
//
//		Thread.sleep(2000);
//		report.report("click INSTALL");
//		uiautomatorClient.click(new Selector().setText("INSTALL"));
//
//		if (!uiautomatorClient.waitForExists(new Selector().setText("ACCEPT"), 5000)) {
//			throw new Exception("Accept page not visible");
//		}
//
//		report.report("click ACCEPT");
//		uiautomatorClient.click(new Selector().setText("ACCEPT"));
//
//		if (uiautomatorClient.waitForExists(new Selector().setText("Downloading a large app"), 2000)) {
//			uiautomatorClient.click(new Selector().setText("Proceed").setClassName("android.widget.Button"));
//		}
//
//		if (!uiautomatorClient.waitForExists(new Selector().setClassName("android.widget.ProgressBar"), 10000)) {
//			throw new Exception("Installing not started");
//
//		}
//		report.step("installing in progress...");
//
//		report.report("waiting for install to finish");
//
//		if (!uiautomatorClient.waitForExists(new Selector().setText("OPEN"), 600000)) {
//			report.report("Did not finish downloading after 10 minutes", Reporter.WARNING);
//		}
//		report.step("Install Completed");
//		Thread.sleep(2000);
//
//		report.report("about to uninstall " + appName);
//		uiautomatorClient.click(new Selector().setText("UNINSTALL"));
//
//		uiautomatorClient.registerClickUiObjectWatcher("uninstall", new Selector[] { new Selector().setText("Do you want to uninstall this app?") },
//				new Selector().setText("OK"));
//		if (!uiautomatorClient.waitForExists(new Selector().setText("INSTALL"), 120000)) {
//			report.report("uninstall didnt complete after 2 minutes", Reporter.WARNING);
//		}
	}

	@After
	public void tearDown() throws Exception {
		seeTestClient.getClient().generateReport();
		seeTestClient.stopSeetestService();
	}
}

