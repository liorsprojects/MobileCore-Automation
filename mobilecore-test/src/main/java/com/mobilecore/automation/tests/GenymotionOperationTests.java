package com.mobilecore.automation.tests;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.ddmlib.IDevice;
import com.mobilecore.automation.infra.ADBConnection;
import com.mobilecore.automation.infra.MobileCoreClient;
import com.mobilecore.automation.infra.SeeTestElement;
import com.mobilecore.automation.infra.enums.Elements;
import com.mobilecore.automation.infra.enums.ZoneType;
import com.mobilecore.automation.infra.fiddler.FiddlerApi;
import com.mobilecore.automation.tests.utils.ImageFlowHtmlReport;

public class GenymotionOperationTests extends SystemTestCase4 {

	private static ADBConnection adbConnection;
	private static String deviceName;
	private static MobileCoreClient mobileCoreClient;
	

	private static ImageFlowHtmlReport imageFlowHtmlReport;
	
	@Before
	public void setup() throws Exception {
		if (adbConnection == null) {
			adbConnection = new ADBConnection();
			adbConnection.init();
		}
		
		if(mobileCoreClient == null) {
			report.report("initialize mobileCoreClient SystemObject");
			mobileCoreClient = (MobileCoreClient) system.getSystemObject("mobileCoreClient");
			mobileCoreClient.report("initialize mobileCoreClient SystemObject complete");
		}
		imageFlowHtmlReport = new ImageFlowHtmlReport();
	}

	@Test
	@TestProperties(name = "start ${deviceName} genymotion device" , paramsInclude={"deviceName"})
	public void startGenymotionDevice() throws Exception {
		adbConnection.startGenymotionDevice(deviceName);
		Thread.sleep(45000);
		IDevice device = adbConnection.getConnectedDevice();
		if(device == null) {
			report.report("device is null", false);
		}
		mobileCoreClient.getClient().addDevice(device.getSerialNumber(), device.getName());
		mobileCoreClient.getClient().setDevice("adb:" + deviceName);
		
		mobileCoreClient.waitForElement(Elements.DeviceElement.ANDROID_LOCK.getElement(), 30000);
		mobileCoreClient.drag(Elements.DeviceElement.ANDROID_LOCK.getElement(), 200, 0);
		
		mobileCoreClient.report("installing MCTester");
		mobileCoreClient.getClient().install("C:/Users/lior_g/Downloads/MCTester/bin/MCTester.apk", true);
		
		
		mobileCoreClient.clearLogcat();
		mobileCoreClient.report("launching MCTester");
		mobileCoreClient.getClient().launch("com.mobilecore.mctester.lior/.MainActivity", true, true);
	}

	@Test
	@TestProperties(name = "shutdown all genymotion devices" , paramsInclude={})
	public void shutdownGenymotionDevices() throws InterruptedException {
		adbConnection.shutDownAllGenyMotionDevices();
		Thread.sleep(10000);
	}

	
	@Test
	@TestProperties(name="display all offerwalls in all devices", paramsInclude={"deviceName"})
	public void testDisplayOfferwallTypes() throws Exception {
		mobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");
		mobileCoreClient.sleep(3000);
		
		String[] owIds = {"original offerwall", "1_bnr_avg_green", "1_bnr_avg_white", "1_bnr_full_sqr_orng", "1_bnr_ic_sqr_cntr","1_bnr_ic_rect_cntr","1_bnr_rect","1_bnr_sqr","1_bnr_sqr_cntr_header","2_avg_green", "2_ftb", "2_ic_big", "2_ic_big_xtreme", "2_redbend", "2_run_cow", "2_sharklab", "bn_ic_2", "bn_ic_4"};
		
		mobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "mReadyToShowOfferwallFromFlow to true");
		report.step("offerwall is ready to show");
		mobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");
		
		for(int i=0; i<owIds.length; i++) {
			
			mobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
			report.step("click 'Show if ready' button");
			report.step("showing: " + owIds[i]);
			mobileCoreClient.fiddlerCommand(FiddlerApi.setFeedJsonPath("offerwall", "C:\\Fiddler\\ow_id.json"));
			
			if((i+1) == owIds.length){
				report.report("next wall is the last in the list so we are not setting the next one to anything");
			} else {
				mobileCoreClient.fiddlerCommand(FiddlerApi.modifyFeed("offerwall", owIds[i+1], false, true));			
			}
			mobileCoreClient.clearLogcat();
			mobileCoreClient.sleep(2000);
			
			imageFlowHtmlReport.addTitledImage(owIds[i], adbConnection.getScreenshotWithAdb(null));
			
			SeeTestElement el1 = new SeeTestElement(ZoneType.NATIVE, "contentDescription=webview-1", 0);
			SeeTestElement el2 = new SeeTestElement(ZoneType.NATIVE, "contentDescription=webview-2", 0);
			if(mobileCoreClient.isElementFound(el1)) {
				mobileCoreClient.clickBackButton();
				try{
					mobileCoreClient.waitForElementToVanish(el1, 2000);
					report.report("webview-1 vanish");
				}catch (Exception e) {
					
				}
			}
			if(mobileCoreClient.isElementFound(el2)) {
				mobileCoreClient.clickBackButton();
				try{
					mobileCoreClient.waitForElementToVanish(el2, 2000);
					report.report("webview-2 vanish");
				}catch (Exception e) {
					
				}				
			}
			mobileCoreClient.sleep(3000);
		}
		
		
		report.report("Offerwall's for \"" + deviceName + "\"", imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		
		// TODO - move to after...
		mobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@After
	public void tearDown() throws Exception {
		if (!isPass()) {
			imageFlowHtmlReport.addTitledImage("Failed Here", adbConnection.getScreenshotWithAdb(null));
			report.report("screen flow", imageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		}
		mobileCoreClient.getClient().generateReport();
	}
	
	public static MobileCoreClient getMobileCoreClient() {
		return mobileCoreClient;
	}

	public static void setMobileCoreClient(MobileCoreClient mobileCoreClient) {
		GenymotionOperationTests.mobileCoreClient = mobileCoreClient;
	}
	
	public static String getDeviceName() {
		return deviceName;
	}
	
	public static void setDeviceName(String deviceName) {
		GenymotionOperationTests.deviceName = deviceName;
	}
}
