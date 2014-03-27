package com.mobilecore.automation.infra.fiddler;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONObject;

import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;



//TODO - make singleton
public class FiddlerJsonRpcClient extends SystemObjectImpl {

	private String host;
	private int port;
	private String executablePath;
	

	private Thread t;
	
	@Override
	public void init() throws Exception {
		super.init();
		report.report("initialize Fiddler-SO");
		String response = "";
		//TODO !!!
		runFiddlerExecutabe();
		try {
		    response = (String)execute(FiddlerApi.ping());
		} catch (Exception e) {
			report.report(e.getMessage());
		}
		if(!"pong".equals(response)) {
			report.report("cannont comunicate with fiddler process", Reporter.FAIL);
			throw new Exception("fiddler communication not working");
		} else {
			report.report("fiddler process communication established");
		}
	}
	
	private void runFiddlerExecutabe() {
		
		report.report("about to run IronSourceFiddler executable");
		Runnable r = new Runnable() {
			
		    public void run() {
			    try {
			    	Runtime.getRuntime().exec("cmd /c start " + executablePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		};
		
		t = new Thread(r);
		
		t.start();
	}
	
	
	public Object execute(String request) throws Exception {
		DataInputStream is = null;
		DataOutputStream os = null;
		Socket socket = null;
		Object result = true;

		try {
			socket = new Socket(InetAddress.getByName(host), port);
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			PrintWriter pw = new PrintWriter(os);
			pw.println(request);
			pw.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			JSONObject json = new JSONObject(in.readLine());
			if (!json.has("result")) {
				result = null;
			} else {
				result = json.get("result");
			}
			is.close();
			os.close();
		} catch (IOException e) {
			result = null;

		} catch (Exception e) {
			result = null;
		} finally {
			if(socket != null && !socket.isClosed()) {
				socket.close();				
			}
		}
		return result;
	}
	
	public void close() {
		try {
			shutDownFiddlerProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.close();
	}
	
	private void shutDownFiddlerProcess() throws Exception {
		report.report("about send shutdown signal to fiddler process...");
		String pingRes = (String) execute(FiddlerApi.ping());
		if("pong".equals(pingRes)) {
			report.report("ping has responded with pong...");
			String closeRes = (String) execute(FiddlerApi.shutdown());
			if("shutdown".equals(closeRes)) {
				report.report("fiddler process was shut down");
			} else {
				report.report("fiddler process was not shutdown properly", Reporter.WARNING);
			}
		} else {
			report.report("fiddler communication incountered a problam!");
		}
		t.interrupt();
		Runtime.getRuntime().exec("taskkill /F /IM IronSourceFiddler.Client.exe");
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	public String getExecutablePath() {
		return executablePath;
	}

	public void setExecutablePath(String executablePath) {
		this.executablePath = executablePath;
	}
}
