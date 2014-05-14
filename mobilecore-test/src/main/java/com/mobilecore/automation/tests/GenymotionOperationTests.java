package com.mobilecore.automation.tests;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.ddmlib.IDevice;
import com.mobilecore.automation.infra.ADBConnection;
import com.mobilecore.automation.infra.MobileCoreClient;
import com.mobilecore.automation.infra.OfferwallParams;
import com.mobilecore.automation.infra.SeeTestElement;
import com.mobilecore.automation.infra.enums.Elements;
import com.mobilecore.automation.infra.enums.FlowType;
import com.mobilecore.automation.infra.enums.GenymotionDevice;
import com.mobilecore.automation.infra.enums.RSType;
import com.mobilecore.automation.infra.enums.ZoneType;
import com.mobilecore.automation.infra.fiddler.FiddlerApi;
import com.mobilecore.automation.tests.utils.ImageFlowHtmlReport;

public class GenymotionOperationTests extends SystemTestCase4 {

	private static ADBConnection mAdbConnection;
	private static MobileCoreClient mMobileCoreClient;
	private static ImageFlowHtmlReport mImageFlowHtmlReport;

	// test parameters
	private String mDeviceName;
	private long mSetDeviceTimout = 60000;
	private long installReportTimeout = 120000;
	private boolean uninstallAppDownload = true;
	private GenymotionDevice genymotionDevice;
	private String appPackage;
	private boolean installApk = true;
	private OfferwallParams[] offerwalls;/*
										 * { "original offerwall",
										 * "1_bnr_avg_green", "1_bnr_avg_white",
										 * "1_bnr_full_sqr_orng",
										 * "1_bnr_ic_sqr_cntr",
										 * "1_bnr_ic_rect_cntr", "1_bnr_rect",
										 * "1_bnr_sqr", "1_bnr_sqr_cntr_header",
										 * "2_avg_green", "2_ftb", "2_ic_big",
										 * "2_ic_big_xtreme", "2_redbend",
										 * "2_run_cow", "2_sharklab", "bn_ic_2",
										 * "bn_ic_4" };
										 */

	/**
	 * this method runs before every method annotated as Test in this
	 * class(@Test)
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		if (mAdbConnection == null) {
			mAdbConnection = new ADBConnection();
			mAdbConnection.init();
		}

		if (mMobileCoreClient == null) {
			report.report("initialize mobileCoreClient SystemObject");
			mMobileCoreClient = (MobileCoreClient) system.getSystemObject("mobileCoreClient");
			mMobileCoreClient.report("initialize mobileCoreClient SystemObject complete");
		}
		//mImageFlowHtmlReport = new ImageFlowHtmlReport(mAdbConnection);
		//TODO - add clearing of the fiddler
	}

	/**
	 * 1. start genymotion device by device name. 2. install application on it.
	 * 3. run the application.
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "start genymotion device", paramsInclude = { "genymotionDevice", "mSetDeviceTimout", "installApk" })
	public void startGenymotionDevice() throws Exception {
		mAdbConnection.startGenymotionDevice(genymotionDevice.getValue());

		waitAddAndSetDevice(genymotionDevice.getValue());

		if(installApk) {
			mMobileCoreClient.report("installing MCTester");
			mMobileCoreClient.getClient().install(mMobileCoreClient.getApkLoc(), true, true);
		} else {
			report.report("assume that app already installed so skipping instalation");
		}
	}

	@Test
	@TestProperties(name = "start genymotion device dynamic", paramsInclude = { "mDeviceName", "mSetDeviceTimout, installApk" })
	public void startGenymotionDynamicDevice() throws Exception {
		report.step("before givinig the start device command seetest devices:" + mMobileCoreClient.getClient().getConnectedDevices());
		mAdbConnection.startGenymotionDevice(mDeviceName);
		
		waitAddAndSetDevice(mDeviceName);
		if(installApk) {			
			mMobileCoreClient.report("installing MCTester");
			mMobileCoreClient.getClient().install(mMobileCoreClient.getApkLoc(), true, true);
		} else { 
			report.report("assume that app already installed so skipping instalation");
		}
		mMobileCoreClient.getClient().sendText("{UNLOCK}");

	}

	@Test
	@TestProperties(name = "shutdown all genymotion devices", paramsInclude = {"mDeviceName"})
	public void shutdownGenymotionDevices() throws Exception {
		try{
			releaseDevice(mDeviceName);
		} catch(Exception e) {
			report.report(e.getMessage(), Reporter.WARNING);
		}
		mAdbConnection.shutDownAllGenyMotionDevices();
		Thread.sleep(4000);
	}

	// TODO - move to infra
	private void releaseDevice(String deviceName) {
		mMobileCoreClient.getClient().release(deviceName, false, true, false);
		report.report("release device '" + mDeviceName + "'");
	}

	@Test
	@TestProperties(name = "display all offerwalls in all devices", paramsInclude = { "appPackage", "offerwalls" })
	public void testDisplayOfferwallTypes() throws Exception {
		mMobileCoreClient.clearLogcat();

		report.report("set first add to show");
		report.report("set fiddler json");
		mMobileCoreClient.fiddlerCommand(FiddlerApi.setFeedJsonPath("offerwall", "C:\\automation\\static_json\\offerwall.json"));
		report.report("set desired ow_id in fiddler");
		mMobileCoreClient.fiddlerCommand(FiddlerApi.modifyFeed("offerwall", offerwalls[0].getOwId(), offerwalls[0].getOwAdCount(), false, offerwalls[0].isFilter()));

		mMobileCoreClient.report("launching MCTester");
		mMobileCoreClient.getClient().launch(appPackage, true, true);
		mMobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");
		mMobileCoreClient.sleep(3000);

		if (!mMobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "mReadyToShowOfferwallFromFlow to true")) {
			throw new Exception("didnt find log that conatains: 'mReadyToShowOfferwallFromFlow to true' after 15000 milliseconds");
		}
		report.step("offerwall is ready to show");
		mMobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");
		boolean offerwallIsReady = true;
		for (int i = 0; i < offerwalls.length; i++) {
			if (i == 0) {
				report.report("skipping ready state check, this is the first iteration so it is checked out side the loop");
			} else {
				offerwallIsReady = mMobileCoreClient.waitForLogcatMessage("WebViewWithLoadState", Reporter.FAIL, 10000, true,
						"setState | from:LOADING , to:READY");
			}

			if (!offerwallIsReady) {
				report.report("showing: " + offerwalls[i].getOwId() + " fail: offerwall not switch to state ready", Reporter.FAIL);
			} else {
				report.step("found ready state: click 'Show if ready' button");
				mMobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
				report.step("showing: " + offerwalls[i].getOwId());
			}
			report.report("set fiddler json");
			mMobileCoreClient.fiddlerClient.execute(FiddlerApi.clearInjection("offerwall"));
			mMobileCoreClient.fiddlerCommand(FiddlerApi.setFeedJsonPath("offerwall", "C:\\automation\\static_json\\offerwall.json"));

			if ((i + 1) == offerwalls.length) {
				report.report("this wall is the last in the list so we are not setting the next one to anything");
			} else {
				report.report("set desired ow_id in fiddler");
				mMobileCoreClient
						.fiddlerCommand(FiddlerApi.modifyFeed("offerwall", offerwalls[i + 1].getOwId(), offerwalls[i + 1].getOwAdCount(), false, offerwalls[i + 1].isFilter()));
			}
			mMobileCoreClient.clearLogcat();
			mMobileCoreClient.sleep(2000);

			mImageFlowHtmlReport.addTitledImage(offerwalls[i].getOwId());

			if (!offerwallIsReady) {
				continue;
			}

			SeeTestElement el1 = new SeeTestElement(ZoneType.NATIVE, "contentDescription=offerwall-webview-1", 0);
			SeeTestElement el2 = new SeeTestElement(ZoneType.NATIVE, "contentDescription=offerwall-webview-2", 0);
			if (mMobileCoreClient.isElementFound(el1)) {
				report.report("offerwall webview-1 is present about to click back button");
				mMobileCoreClient.clickBackButton();
				report.report("click back button");
				try {
					report.report("waiting for offerwall webview-1 to vanish");
					mMobileCoreClient.waitForElementToVanish(el1, 2000);
					report.report("webview-1 vanish");
				} catch (Exception e) {

				}
			}
			if (mMobileCoreClient.isElementFound(el2)) {
				report.report("offerwall webview-2 is present about to click back button");
				mMobileCoreClient.clickBackButton();
				report.report("click back button");
				try {
					report.report("waiting for offerwall webview-2 to vanish");
					mMobileCoreClient.waitForElementToVanish(el2, 2000);
					report.report("webview-2 vanish");
				} catch (Exception e) {

				}
			}
			mMobileCoreClient.sleep(2000);
		}

		report.report("Offerwall's for \"" + mDeviceName + "\"", mImageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);

		// TODO - move to after...
		// mMobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		// mMobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		// mMobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	// TODO - run this test and verify stability
	@Test
	@TestProperties(name = "close offerwall with X button", paramsInclude = { "appPackage" })
	public void testOfferwallCloseWithXButton() throws Exception {
		mImageFlowHtmlReport.addTitledImage("before app launch");
		mMobileCoreClient.clearLogcat();
		mMobileCoreClient.report("launching MCTester");
		mMobileCoreClient.getClient().launch(appPackage, true, true);
		mMobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");
		mImageFlowHtmlReport.addTitledImage("app started");

		mMobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "mReadyToShowOfferwallFromFlow to true");
		report.step("offerwall is ready to show");
		mMobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");

		mMobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");
		mImageFlowHtmlReport.addTitledImage("click 'Show if ready' button");

		mMobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mMobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);

		mMobileCoreClient.waitForElementAndClick(Elements.OfferwallElement.X_BUTTON.getElement(), 10000, 1);
		// TODO - verify gone...
		mMobileCoreClient.waitForRS(RSType.CLOSE, FlowType.OFFERWALL, Reporter.FAIL, 10000);

		mMobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	// TODO - run this test and verify stability
	@Test
	@TestProperties(name = "close offerwall with BACK button", paramsInclude = { "appPackage" })
	public void testOfferwallCloseWithBackButton() throws Exception {
		mMobileCoreClient.clearLogcat();
		mMobileCoreClient.report("launching MCTester");
		mMobileCoreClient.getClient().launch(appPackage, true, true);
		mMobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mMobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "from:LOADING , to:READY_TO_SHOW");
		report.step("offerwall is ready to show");

		// TODO - THIS IS A BUG WORKAROUND -> in this state the wall isn't
		// really ready to show... (all tests)
		mMobileCoreClient.report("wait 6 second just in case");
		mMobileCoreClient.sleep(6000);

		mMobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);
		report.step("click 'Show if ready' button");

		mMobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "from:READY_TO_SHOW , to:SHOWING");

		mMobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mMobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);

		mMobileCoreClient.clickBackButton();
		mMobileCoreClient.waitForRS(RSType.BACK, FlowType.OFFERWALL, Reporter.FAIL, 10000);

		mMobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@Test
	@TestProperties(name = "offerwall full download flow", paramsInclude = { "appPackage", "installReportTimeout", "uninstallAppDownload", "offerwalls"})
	public void testOfferwallFullDownloadFlow() throws Exception {
		//mImageFlowHtmlReport.addTitledImage("before launche");
		mMobileCoreClient.clearLogcat();
		mMobileCoreClient.report("launching MCTester");
		mMobileCoreClient.getClient().launch(appPackage, true, true);

		mMobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");
		//mImageFlowHtmlReport.addTitledImage("app started");
		mMobileCoreClient.waitForLogcatMessage("OfferwallManager", Reporter.FAIL, 15000, true, "mReadyToShowOfferwallFromFlow to true");
		report.step("offerwall is ready to show");
		mMobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");

		report.step("click 'Show if ready' button");
		mMobileCoreClient.sleep(3000);
		mMobileCoreClient.click(Elements.MCTesterElement.SHOW_IF_READY.getElement(), 1);

		mMobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		mMobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		//mImageFlowHtmlReport.addTitledImage("after click on show offerwall");

		String appName = mMobileCoreClient.elementGetText(Elements.OfferwallElement.INNER_ITEM_TITTLE.getElement());
		
		mMobileCoreClient.click(Elements.OfferwallElement.INNER_ITEM.getElement(), 1);

		report.step("click on application item: " + appName);
		mMobileCoreClient.waitForRS(RSType.CLICK, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		//mImageFlowHtmlReport.addTitledImage("after click on offer with title : " + appName);

		mMobileCoreClient.waitForRS(RSType.STORE, FlowType.OFFERWALL, Reporter.FAIL, 10000);
		report.step("navigated to the market");

		//TODO - add abstraction level to ho 
		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.INSTALL_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click on INSTALL");

		//mImageFlowHtmlReport.addTitledImage("after click install");
		
		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.ACCEPT_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click on ACCEPT");

		//mImageFlowHtmlReport.addTitledImage("accept page");
		mMobileCoreClient.waitForElement(Elements.MarketElement.DOWNLOADING_TEXT.getElement(), 10000);
		mMobileCoreClient.report("start downloading...");
		mMobileCoreClient.waitForElement(Elements.MarketElement.INSTALLING_TEXT.getElement(), 600000);
		mMobileCoreClient.report("finish downloading");
		mMobileCoreClient.report("start instaling...");
		mMobileCoreClient.waitForElement(Elements.MarketElement.OPEN_BUTTON.getElement(), 600000);
		mMobileCoreClient.report("finish instaling");

		mMobileCoreClient.waitForRS(RSType.INSATLL, FlowType.OFFERWALL, Reporter.WARNING, installReportTimeout);

		// TODO - abstraction: make function in Helper class
		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.UNINSTALL_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click UNINSTALL button");

		mMobileCoreClient.waitForElement(Elements.MarketElement.CONFIRM_UNINSTALL_TEXT.getElement(), 3000);
		mMobileCoreClient.click(Elements.MarketElement.CONFIRM_OK.getElement(), 1);
		mMobileCoreClient.report("click OK button");

		mMobileCoreClient.waitForElement(Elements.MarketElement.INSTALL_BUTTON.getElement(), 60000);
		mMobileCoreClient.report("uninstall finished");
		// end todo

		mMobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@Test
	@TestProperties(name = "stickeez click", paramsInclude = { "appPackage" })
	public void testStickeezClick() throws Exception {
		mMobileCoreClient.clearLogcat();
		mMobileCoreClient.fiddlerCommand(FiddlerApi.setFeedJsonPath("stickeez", "C:\\automation\\static-json\\stickeez.json"));
		mMobileCoreClient.report("launching MCTester");
		mMobileCoreClient.getClient().launch(appPackage, true, true);

		mMobileCoreClient.waitForElement(Elements.MCTesterElement.APP_TITLE.getElement(), 10000);
		report.step("app started");

		mMobileCoreClient.waitForLogcatMessage("StickeezManager", Reporter.FAIL, 25000, true, "setState | from:STATE_INIT , to:STATE_READY_TO_SHOW");
		report.step("sticckez is ready to show");
		mMobileCoreClient.waitForLogcatMessage("MobileCoreReport", Reporter.FAIL, 15000, true, "ftue_shown");

		report.step("click 'Show stickeez' button");
		mMobileCoreClient.sleep(5000);
		mMobileCoreClient.click(Elements.MCTesterElement.SHOW_STICKEE.getElement(), 1);
		mMobileCoreClient.waitForElementAndClick(Elements.StickeezElement.STICKEEZ_HANDLE_CLICKABLE_AREA.getElement(), 1, 10000);
		mMobileCoreClient.waitForElementAndClick(Elements.StickeezElement.STICKEEZ_BANNER_APP_TITLE.getElement(), 1, 10000);

		// TODO - verify reports for stickeez
		// mMobileCoreClient.waitForRS(RSType.WALL, FlowType.OFFERWALL,
		// Reporter.FAIL, 10000);
		// mMobileCoreClient.waitForRS(RSType.IMPRESSION, FlowType.OFFERWALL,
		// Reporter.FAIL, 10000);
		// String appName =
		// mMobileCoreClient.elementGetText(Elements.OfferwallElement.INNER_ITEM_TITTLE.getElement());

		// mMobileCoreClient.waitForElementAndClick(Elements.OfferwallElement.INNER_ITEM.getElement(),
		// 10000, 1);
		// report.step("click on application item: " + appName);
		// mMobileCoreClient.waitForRS(RSType.CLICK, FlowType.OFFERWALL,
		// Reporter.FAIL, 10000);
		//
		// mMobileCoreClient.waitForRS(RSType.STORE, FlowType.OFFERWALL,
		// Reporter.FAIL, 10000);
		// report.step("navigated to the market");
		//
		// //TODO - add "right application verification" (native, text=<app
		// name>)
		//
		// //TODO - abstraction: make function in Helper class
		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.INSTALL_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click on INSTALL");

		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.ACCEPT_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click on ACCEPT");

		mMobileCoreClient.waitForElement(Elements.MarketElement.DOWNLOADING_TEXT.getElement(), 10000);
		mMobileCoreClient.report("start downloading...");
		mMobileCoreClient.waitForElement(Elements.MarketElement.INSTALLING_TEXT.getElement(), 600000);
		mMobileCoreClient.report("finish downloading");
		mMobileCoreClient.report("start instaling...");
		mMobileCoreClient.waitForElement(Elements.MarketElement.OPEN_BUTTON.getElement(), 600000);
		mMobileCoreClient.report("finish instaling");
		// end todo

		mMobileCoreClient.waitForRS(RSType.INSATLL, FlowType.OFFERWALL, Reporter.WARNING, installReportTimeout);

		// TODO - abstraction: make function in Helper class
		mMobileCoreClient.waitForElementAndClick(Elements.MarketElement.UNINSTALL_BUTTON.getElement(), 10000, 1);
		mMobileCoreClient.report("click UNINSTALL button");

		mMobileCoreClient.waitForElement(Elements.MarketElement.CONFIRM_UNINSTALL_TEXT.getElement(), 3000);
		mMobileCoreClient.click(Elements.MarketElement.CONFIRM_OK.getElement(), 1);
		mMobileCoreClient.report("click OK button");

		mMobileCoreClient.waitForElement(Elements.MarketElement.INSTALL_BUTTON.getElement(), 60000);
		mMobileCoreClient.report("uninstall finished");
		// end todo

		mMobileCoreClient.getClient().applicationClearData("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.mobilecore.mctester");
		mMobileCoreClient.getClient().applicationClose("com.android.vending");
	}

	@After
	public void tearDown() throws Exception {
		if (!isPass()) {
			mImageFlowHtmlReport.addTitledImage("Failed Here");
		}
		report.report("screen flow", mImageFlowHtmlReport.getHtmlReport(), Reporter.PASS, false, true, false, false);
		mMobileCoreClient.getClient().generateReport();
	}

	// private helper methods (candidate to move to infra)

	private void waitAddAndSetDevice(String deviceName) throws Exception {
		report.report("waiting for device to connect for " + mSetDeviceTimout + "milliseconds");
		IDevice device = null;
		boolean done = false;
		long start = System.currentTimeMillis();
		while (!done) {

			if ((System.currentTimeMillis() - start) > mSetDeviceTimout) {
				throw new Exception("could not set device after " + mSetDeviceTimout + "milliseconds");
			}

			device = mAdbConnection.getConnectedDevice();
			if (device == null) {
				Thread.sleep(2000);
				continue;
			}
			report.report("DEBUG: found conneted device");
			try {
				report.report("before adding device: " + mMobileCoreClient.getClient().getConnectedDevices());
				mMobileCoreClient.getClient().addDevice(device.getSerialNumber(), deviceName);
				report.report("after adding device: " + mMobileCoreClient.getClient().getConnectedDevices());
				mMobileCoreClient.getClient().setDevice("adb:" + deviceName);
				report.report("after setting device: " + mMobileCoreClient.getClient().getConnectedDevices());
			} catch (Exception e) {
				report.report("ERROR SETTING DEVICE: " + e.getMessage());
				report.report("not yet plase wait...");
				Thread.sleep(1000);
				continue;
			}
			report.report("DEBUG: device has been selected successfully and ready to use");
			try {
				mMobileCoreClient.waitForElement(Elements.DeviceElement.ANDROID_LOCK.getElement(), 10000);
				String x = mMobileCoreClient.getClient().elementGetProperty("NATIVE", "contentDescription=Slide area.", 0, "x");
				String y = mMobileCoreClient.getClient().elementGetProperty("NATIVE", "contentDescription=Slide area.", 0, "y");
				String w = mMobileCoreClient.getClient().elementGetProperty("NATIVE", "contentDescription=Slide area.", 0, "width");
				String h = mMobileCoreClient.getClient().elementGetProperty("NATIVE", "contentDescription=Slide area.", 0, "height");
				report.report("x: " + x + " y: " + y + " w: " + w + " h: " + h);
				mMobileCoreClient.getClient().dragCoordinates(Integer.parseInt(w) / 2, Integer.parseInt(y) + Integer.parseInt(h) / 2,
						Integer.parseInt(w), Integer.parseInt(y) + Integer.parseInt(h) / 2, 1);
				// mMobileCoreClient.drag(Elements.DeviceElement.ANDROID_LOCK.getElement(),
				// 200, 0);

			} catch (Exception e) {
				mImageFlowHtmlReport.addTitledImage("unlock screen fial");
				report.report("didn't release lock screen of the device");
			}
			done = true;
			report.report("device connected");
		}
	}

	public static MobileCoreClient getMobileCoreClient() {
		return mMobileCoreClient;
	}

	public static void setMobileCoreClient(MobileCoreClient mobileCoreClient) {
		GenymotionOperationTests.mMobileCoreClient = mobileCoreClient;
	}

	public String getmDeviceName() {
		return mDeviceName;
	}

	public void setmDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}

	public long getmSetDeviceTimout() {
		return mSetDeviceTimout;
	}

	public void setmSetDeviceTimout(long mSetDeviceTimout) {
		this.mSetDeviceTimout = mSetDeviceTimout;
	}

	public long getInstallReportTimeout() {
		return installReportTimeout;
	}

	public void setInstallReportTimeout(long installReportTimeout) {
		this.installReportTimeout = installReportTimeout;
	}

	public boolean isUninstallAppDownload() {
		return uninstallAppDownload;
	}

	public void setUninstallAppDownload(boolean uninstallAppDownload) {
		this.uninstallAppDownload = uninstallAppDownload;
	}

	public GenymotionDevice getGenymotionDevice() {
		return genymotionDevice;
	}

	public void setGenymotionDevice(GenymotionDevice genymotionDevice) {
		this.genymotionDevice = genymotionDevice;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	public OfferwallParams[] getOfferwalls() {
		return offerwalls;
	}

	public boolean isInstallApk() {
		return installApk;
	}

	public void setInstallApk(boolean installApk) {
		this.installApk = installApk;
	}

	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setOfferwalls(OfferwallParams[] offerwalls) {
		this.offerwalls = offerwalls;
	}

}
