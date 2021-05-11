package com.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Post {
	public static void main(String[] args) throws IOException {
		// User credentials
		String username = "sample123";
		String password = "black0wl!";
		String pageID = "123456";
		String spaceKey = "SAMPL";
		String pageTitle = "Sample Title";
		String htmlBody = "<p>This is a sample paragraph</p>";
		// URL of the site
		String urlStr = "https://confluence.<your_domain>.com/rest/api/content/";
		
		String auth = username+":"+password;
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(auth.getBytes()));
		
		// For bypassing certificate errors
		
		TrustManager[] trustAllCerts = new TrustManager[] {
			(TrustManager) new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// keep it blank
					
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// keep it blank
					
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				
			}
		};
		
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			String d = "{\r\n"
					+ "\"type\" : \"page\",\r\n"
					+ "\"title\" : "+pageTitle+",\r\n"
					+ "\"space\" : {\r\n"
					+ "\"key\" : "+spaceKey+"\r\n"
					+ "},\r\n"
					+ "\"ancestors\" : [\r\n"
					+ "{\r\n"
					+ "\"id\" :"+ pageID+"\r\n"
					+ "}\r\n"
					+ "],\r\n"
					+ "\"body\" : {\r\n"
					+ "\"storage\":{\r\n"
					+ "\"value\" :"+ htmlBody+",\r\n"
					+ "\"representation\":\"storage\"\r\n"
					+ "}\r\n"
					+ "}\r\n"
					+ "}";	
			
			byte[] data = d.getBytes(StandardCharsets.UTF_8);
			int length = data.length;
			connection.setFixedLengthStreamingMode(length);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestProperty("Content-Type", "application/json;charset=UFT-8");
			connection.setRequestProperty("Host", "confluence.schwab.com");
			connection.setDoOutput(true);
			
			try (OutputStream os = connection.getOutputStream()) {
				os.write(data);
			}
			if(connection.getResponseCode() == 200) {
				System.out.println("Your data is posted. HTTP response code " + connection.getResponseCode());
			}
			
			if(connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code " + connection.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String output = br.readLine();
			System.out.println("RESPONSE BODY");
			if(output != null) {
				System.out.println(output);
			}
			connection.disconnect();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
