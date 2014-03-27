package com.mobilecore.automation.infra.enums;

import com.mobilecore.automation.infra.SeeTestElement;

public class Elements {

	public enum DeviceElement {
		
		ANDROID_LOCK(ZoneType.DEFAULT, "ANDROID_LOCK", 0);

		private SeeTestElement mElement;

		private DeviceElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum MarketElement {
		
		INSTALL_BUTTON(ZoneType.DEFAULT, "INSTALL", 0),
		ACCEPT_BUTTON(ZoneType.DEFAULT, "ACCEPT", 0),
		UNINSTALL_BUTTON(ZoneType.DEFAULT, "UNINSTALL",0),
		OPEN_BUTTON(ZoneType.DEFAULT, "OPEN", 0),
		CONFIRM_UNINSTALL_TEXT(ZoneType.DEFAULT, "CONFIRM_UNINSTALL_TEXT", 0),
		CONFIRM_OK(ZoneType.DEFAULT, "CONFIRM_OK", 0),
		DOWNLOADING_TEXT(ZoneType.DEFAULT, "DOWNLOADING_TEXT", 0),
		INSTALLING_TEXT(ZoneType.DEFAULT,"INSTALLING_TEXT", 0),
		PROGRESS_BAR(ZoneType.DEFAULT, "PROGRESS_BAR", 0);

		private SeeTestElement mElement;

		private MarketElement(ZoneType zone, String name, int index) {
			this.mElement = new SeeTestElement(zone, name, index);
		}
		
		public SeeTestElement getElement() {
			return mElement;
		}
	}
	
	public enum MCTesterElement {
		
		APP_TITLE(ZoneType.DEFAULT, "APP_TITLE", 0),
		SHOW_IF_READY(ZoneType.DEFAULT, "SHOW_IF_READY", 0),
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
		INNER_ITEM(ZoneType.WEB, "css=.inner_item", 0),
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
		STICKEEZ_HANDLE(ZoneType.NATIVE, "contentDescription=stickeez-handle", 0);
	
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