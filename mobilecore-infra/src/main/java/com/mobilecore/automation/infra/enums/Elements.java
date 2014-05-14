package com.mobilecore.automation.infra.enums;

import com.mobilecore.automation.infra.SeeTestElement;

public class Elements {

	public enum DeviceElement {
		
		ANDROID_LOCK(ZoneType.NATIVE, "contentDescription=Slide area.", 0);

		private SeeTestElement mElement;

		private DeviceElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum MarketElement {
		
		INSTALL_BUTTON(ZoneType.NATIVE, "xpath=//*[@text='INSTALL']", 0),
		ACCEPT_BUTTON(ZoneType.NATIVE, "xpath=//*[@text='ACCEPT']", 0),
		UNINSTALL_BUTTON(ZoneType.NATIVE, "xpath=//*[@text='UNINSTALL']",0),
		OPEN_BUTTON(ZoneType.NATIVE, "xpath=//*[@text='OPEN']", 0),
		CONFIRM_UNINSTALL_TEXT(ZoneType.NATIVE, "xpath=//*[@text='Do you want to uninstall this app?']",0),
		CONFIRM_OK(ZoneType.NATIVE, "xpath=//*[@text='OK']",0),
		DOWNLOADING_TEXT(ZoneType.NATIVE, "xpath=//*[@text='Downloading…']", 0),
		INSTALLING_TEXT(ZoneType.NATIVE,"xpath=//*[@text='Installing…']", 0),
		PROGRESS_BAR(ZoneType.NATIVE, "xpath=//*[@class='android.widget.ProgressBar']", 0);

		private SeeTestElement mElement;

		private MarketElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum MCTesterElement {
		
		APP_TITLE(ZoneType.NATIVE, "text=MCTester", 0),
		SHOW_IF_READY(ZoneType.NATIVE, "text=Show if ready", 0),
		SHOW_NOT_FORCE(ZoneType.DEFAULT, "SHOW_NOT_FORCE", 0),
		SHOW_STICKEE(ZoneType.NATIVE, "text=Show stickee", 0);

		private SeeTestElement mElement;

		private MCTesterElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}

	public enum OfferwallElement {
		X_BUTTON(ZoneType.WEB,"css=A" , 0),
		INNER_ITEM(ZoneType.WEB, "css=.inner_item" /*"xpath=//*[@id='downloadSection' and @width >0]"*/, 0),
		INNER_ITEM_TITTLE(ZoneType.WEB, "css=.title", 0);

		private SeeTestElement mElement;

		private OfferwallElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum StickeezElement {
		X_BUTTON(ZoneType.NATIVE, "contentDescription=stickeez-x", 0),
		STICKEEZ_HANDLE(ZoneType.NATIVE, "contentDescription=stickeez-handle", 0),
		STICKEEZ_HANDLE_CLICKABLE_AREA(ZoneType.NATIVE, "contentDescription=stickeez-handle-clickable-area", 0),
		STICKEEZ_BANNER_X(ZoneType.NATIVE, "contentDescription=stickeez-banner-x", 0),
		STICKEEZ_BANNER_APP_TITLE(ZoneType.NATIVE, "contentDescription=stickeez-banner-app-title", 0),
		STICKEEZ_BANNER_APP_DESCRIPTION(ZoneType.NATIVE, "contentDescription=stickeez-banner-app-description", 0);
	
		private SeeTestElement mElement;

		private StickeezElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum SliderElement {
		SLIDER_HANDLE(ZoneType.NATIVE, "contentDescription=slider-handle", 0);
	
		private SeeTestElement mElement;

		private SliderElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}

}