package com.mobilecore.automation.infra.fiddler;

public class FiddlerApi {

	/**
	 * set the path of the json type we want to inject 
	 * {offerwall, stickeez, slider}
	 */
	public static String setFeedJsonPath(String feedType, String jsonPath) {
		return "{'method':'setFeedJsonPath','params':['"+ feedType +"','"+ jsonPath +"'],'id':1}";
	}
	
	//modifyFeed(string feedType, string owId, bool trimAdsToMach, bool pFilter)
	public static String modifyFeed(String feedType, String owId, boolean trimAdsToMach, boolean filter) {
		return "{'method':'modifyFeed','params':['"+ feedType +"','"+ owId +"'," + trimAdsToMach +","+ filter +"],'id':1}";
	}
	
	
	/**
	 * set session by name to null
	 * {offerwall, stickeez, slider, all}
	 */
	public static String clearInjection(String type) {
		return "{'method':'clearInjection','params':[\""+ type +"\"],'id':1}";
	}
	
	/**
	 * set session by name to null
	 *  {offerwallSession, stickeezSession, sliderSession, all}
	 */
	public static String clearSession(String sessionName) {
		return "{'method':'clearSession','params':[\""+ sessionName +"\"],'id':1}";
	}
	
	/**
	 * send shutdown signal to the fiddler program
	 */
	public static String shutdown() {
		return "{'method':'shutdown','params':[],'id':1}";
	}
	
	/**
	 * ping the jsonRpcConnection and return pong if connected.
	 */
	public static String ping() {
		return "{'method':'ping','params':[],'id':1}";
	}
}
