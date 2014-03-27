package com.mobilecore.automation.infra;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import jsystem.framework.system.SystemObjectImpl;

import org.apache.commons.io.FileUtils;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;

//TODO - forward also automatically
public class ADBConnection extends SystemObjectImpl implements IDeviceChangeListener {

	private IDevice mDevice;
	private AndroidDebugBridge mAdb;
	private File mAdbLocation;

	@Override
	public void init() throws Exception {
		super.init();
		report.report("initializing ADBConnection...");
		AndroidDebugBridge.initIfNeeded(false);
		mAdbLocation = findAdbFile();
		mAdb = AndroidDebugBridge.createBridge(mAdbLocation.getAbsolutePath() + File.separator + "adb", true);
		if (mAdb == null) {
			throw new IllegalStateException("Failed to create ADB bridge");
		}
		AndroidDebugBridge.addDeviceChangeListener(this);
		if (mAdb.hasInitialDeviceList()) {
			mDevice = mAdb.getDevices()[0];
		} else {
			waitForDeviceToConnect(5000);
		}
		report.report("initializing ADBConnection... DONE");
		
	}

	private void waitForDeviceToConnect(int timeoutForDeviceConnection) throws Exception {
		final long start = System.currentTimeMillis();
		while (mDevice == null) {
			if (System.currentTimeMillis() - start > timeoutForDeviceConnection) {
				report.report("there are no connected devices");
				return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Not important
			}
		}
	}

	public File getScreenshotWithAdb(File screenshotFile) throws Exception {
		RawImage ri = mDevice.getScreenshot();
		return display(mDevice.getSerialNumber(), ri, screenshotFile);
	}

	/**
	 * The close method is called in the end of the while execution.<br>
	 * This can be a good place to free resources.<br>
	 */
	@Override
	public void close() {
		super.close();
	}

	private boolean connectDevice() {
		boolean connected = false;
		try {
			ProcessBuilder pb = new ProcessBuilder("adb", "connect", "192.168.56.102");
			Process p = pb.start();
			report.report("adb connect 192.168.56.102");
			Thread.sleep(2000);
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (line.contains("connected") || line.contains("already")) {
					report.report("device connected");
					connected = true;
				}
			}
			input.close();

		} catch (Exception e) {
			report.report("fail to connect");
		}
		return connected;
	}

	// IDeviceChangeListener
	@Override
	public void deviceConnected(IDevice device) {
		report.report("new deviece connect with name: " + device.getName() + " and serial: " + device.getSerialNumber());
		this.mDevice = device;
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		report.report("deviece disconnect with name: " + device.getName() + " and serial: " + device.getSerialNumber());
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		report.report("device changed");
	}

	// end IDeviceChangeListener

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

	// new capabilities
	public void startGenymotionDevice(String deviceName) throws IOException {
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("player.exe --vm-name " + "\"" + deviceName + "\"");
			System.out.println("Started ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutDownAllGenyMotionDevices() {
		try {
			ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", "player.exe");
			report.report("sending kill all genymotion command");
			Process p = pb.start();
			Thread.sleep(2000);
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				report.report(line);
			}
			input.close();

		} catch (Exception e) {
			System.out.println("fail to connect");
		}
		
		for (String vmName : getVmNames(true)) {
			try {
				ProcessBuilder pb = new ProcessBuilder("VBoxManage", "controlvm", vmName, "poweroff");
				report.report("sending kill all genymotin command");
				Process p = pb.start();
				Thread.sleep(2000);
				String line;
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					if (line.contains("100%")) {
						report.report("device with name: " + vmName + " has shutdown");
					}
				}
				input.close();

			} catch (Exception e) {
				System.out.println("fail to connect");
			}
		}
		disconnectDevice();
	}

	public List<String> getVmNames(boolean running) {
		List<String> deviceNames = new ArrayList<String>();
		ProcessBuilder pb = null;
		try {
			if (running) {
				pb = new ProcessBuilder("VBoxManage", "list", "runningvms");
			} else {
				pb = new ProcessBuilder("VBoxManage", "list", "vms");
			}
			Process p = pb.start();
			Thread.sleep(2000);
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				deviceNames.add(line.substring(0, line.indexOf("{") - 1));
			}
			input.close();

		} catch (Exception e) {
			
		}
		if (deviceNames.size() == 0) {
			return null;
		}
		return deviceNames;
	}

	public List<String> getVmNames() {
		return getVmNames(false);
	}

	public List<String> getNotRunningVMs() {
		List<String> allDevices = getVmNames();
		List<String> runningDevices = getVmNames(true);
		List<String> notRunningDevices = allDevices;
		notRunningDevices.removeAll(runningDevices);
		return notRunningDevices;
	}

	public void disconnectDevice() {
		String serialNo = mDevice.getSerialNumber().replace(":5555", "");	
		try {
			ProcessBuilder pb = new ProcessBuilder("adb", "disconnect", serialNo);
			Process p = pb.start();
			Thread.sleep(2000);
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {}
			input.close();

		} catch (Exception e) {
			System.out.println("fail to connect");
		}
		IDevice[] devices = mAdb.getDevices();
		for (IDevice device : devices) {
			if(device.getSerialNumber().equals(serialNo)) {
				report.report("fail to disconnect device with serial: " + serialNo, false);
				return;
			}
		}
		this.mDevice = null;
	}
	
	public IDevice getConnectedDevice() {
		if(mDevice != null) {
			return mDevice;
		}
		return null;
	}
	
	
}
