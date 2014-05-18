package com.mobilecore.automation.tests.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import jsystem.extensions.report.html.Report;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.utils.FileUtils;

import com.mobilecore.automation.infra.ADBConnection;

public class ImageFlowHtmlReport {

	private ADBConnection mAdbConnection;
	private static final String SIMPLE_TITLE_FORMAT = "<p>%s</p>";
	private static final String SIMPLE_IMG_FORMAT = "<img class=\"screenshot\" src=\"%s\"/>";
	private static final String BLANK_ROW = "<br/>";
	private static final String style = "h3{margin: 0px;padding:0px;}.screenshot{width: 50%;-webkit-box-shadow: 7px 7px 5px 0px rgba(50, 50, 50, 0.75);-moz-box-shadow: 7px 7px 5px 0px rgba(50, 50, 50, 0.75);box-shadow: 7px 7px 5px 0px rgba(50, 50, 50, 0.75);}";

	StringBuilder htmlBody;

	public ImageFlowHtmlReport(ADBConnection adbConnection) throws URISyntaxException {
		mAdbConnection = adbConnection;
		htmlBody = new StringBuilder("<h3>Test Screenshot flow</h3>");
	}

	public void addTitledImage(String title) {
		htmlBody.append(BLANK_ROW);
		htmlBody.append(String.format(SIMPLE_TITLE_FORMAT, title));
		try {
			Reporter reporter = ListenerstManager.getInstance();
			String imageFileName = title.replace(" ", "_") + ".png";
			String imagePath = reporter.getCurrentTestFolder() + File.separator + imageFileName;
			FileUtils.copyFile(mAdbConnection.getScreenshotWithAdb(null).getAbsoluteFile().getAbsolutePath(), imagePath);
			htmlBody.append(String.format(SIMPLE_IMG_FORMAT, imageFileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		htmlBody.append(BLANK_ROW);
	}

	public String getHtmlReport() {
		return "<html><head>" + "<style>" + style + "</style>" + "</head><body>" + htmlBody.toString() + "</body></html>";
	}

}
