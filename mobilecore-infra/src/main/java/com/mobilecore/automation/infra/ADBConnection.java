package com.mobilecore.automation.infra;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.imageio.ImageIO;

import jsystem.framework.system.SystemObjectImpl;

import org.apache.commons.io.FileUtils;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;


//TODO - forward also automatically
public class ADBConnection extends SystemObjectImpl implements IDeviceChangeListener {

	private IDevice device;
	private AndroidDebugBridge adb;
	private File adbLocation;

	@Override
	public void init() throws Exception {
		super.init();
		AndroidDebugBridge.initIfNeeded(false);
		adbLocation = findAdbFile();
		adb = AndroidDebugBridge.createBridge(adbLocation.getAbsolutePath() + File.separator + "adb", true);
		if (adb == null) {
			throw new IllegalStateException("Failed to create ADB bridge");
		}
		AndroidDebugBridge.addDeviceChangeListener(this);
		if (adb.hasInitialDeviceList()) {
			device = adb.getDevices()[0];
		} else {			
			waitForDeviceToConnect(5000);
		}
	}

	private void waitForDeviceToConnect(int timeoutForDeviceConnection) throws Exception {
		final long start = System.currentTimeMillis();
		while (device == null) {
			if (System.currentTimeMillis() - start > timeoutForDeviceConnection) {
				throw new Exception("Cound not find conneced device");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Not important
			}
		}
	}

	public File getScreenshotWithAdb(File screenshotFile) throws Exception {
		RawImage ri = device.getScreenshot();
		return display(device.getSerialNumber(), ri, screenshotFile);
	}

	/**
	 * The close method is called in the end of the while execution.<br>
	 * This can be a good place to free resources.<br>
	 */
	@Override
	public void close() {
		super.close();
	}

	@Override
	public void deviceConnected(IDevice device) {
		this.device = device;

	}

	@Override
	public void deviceDisconnected(IDevice device) {
		// NOT IN USE
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		// NOT IN USE
	}

	private File findAdbFile() throws IOException {
		// Check if the adb file is in the current folder
		File[] adbFile = new File(".").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().equals("adb") || pathname.getName().equals("adb.exe");
			}
		});
		if (adbFile != null && adbFile.length > 0) {
			return adbFile[0].getParentFile();
		}

		final String androidHome = System.getenv("ANDROID_HOME");
		if (androidHome == null || androidHome.isEmpty()) {
			throw new IOException("ANDROID_HOME environment variable is not set");
		}

		final File root = new File(androidHome);
		if (!root.exists()) {
			throw new IOException("Android home: " + root.getAbsolutePath() + " does not exist");
		}

		try {
			// String[] extensions = { "exe" };
			Collection<File> files = FileUtils.listFiles(root, null, true);
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				// TODO: Eran - I think should be using equals as compareTo is
				// more sortedDataStructure oriented.
				if (file.getName().equals("adb.exe") || file.getName().equals("adb")) {
					return file.getParentFile();
				}
			}
		} catch (Exception e) {
			throw new IOException("Failed to find adb in " + root.getAbsolutePath());
		}
		throw new IOException("Failed to find adb in " + root.getAbsolutePath());
	}

	private static File display(String device, RawImage rawImage, File screenshotFile) throws Exception {
		BufferedImage image = new BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_RGB);
		// Dimension size = new Dimension(image.getWidth(), image.getHeight());

		int index = 0;
		int indexInc = rawImage.bpp >> 3;
		for (int y = 0; y < rawImage.height; y++) {
			for (int x = 0; x < rawImage.width; x++, index += indexInc) {
				int value = rawImage.getARGB(index);
				image.setRGB(x, y, value);
			}
		}
		if (screenshotFile == null) {
			screenshotFile = File.createTempFile("screenshot", ".png");

		}
		ImageIO.write(image, "png", screenshotFile);
		return screenshotFile;
	}
}
