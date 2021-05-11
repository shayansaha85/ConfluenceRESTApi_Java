package com.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Get {
	public static void main(String[] args) throws IOException {
		// User credentials
		String username = "sample123";
		String password = "black0wl!";
		String pageID = "123456";
		// URL of the site
		String urlStr = "https://confluence.<your_domain>.com/rest/api/content/"+pageID;
		
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
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", basicAuth);
			/*
			 * ADD YOUR HEADERS IN THIS FORMAT
			 * connection.setRequestProperty(keyname, valuename);
			 * 
			 * */
			
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
