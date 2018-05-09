package sensorDataApi;

import java.net.HttpURLConnection;
import java.net.URL;

public class MessageToUI {
	public static void sendMessageToUi(String ipUI, String message) {
	try {
		
		message = message.replace("#", "_");
		message = message.replace(" ", "_");
		String urlToSend = "http://" + ipUI + ":3000" + "/engines/streamingRuleEngine**" + message;
		System.out.println("Url Ip: " + urlToSend);
		URL url = new URL(urlToSend);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/plain");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		conn.disconnect();

	  }catch (Exception e) {
		e.printStackTrace();
	  }

	}
}
