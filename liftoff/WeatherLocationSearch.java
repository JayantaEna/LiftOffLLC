package com.dev3.liftoff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherLocationSearch {
	private String cityName;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String retreiveLocationKey(){
		String line1 			= "";
		String response1 		= "";
		BufferedReader bf1 		= null;
		HttpURLConnection hCon1 = null;
		String locationKey 		= null;
		
		try {
			String locationCurl = WeatherTemperature.LOCATION_CURL + WeatherTemperature.API_KEY + WeatherTemperature.CITY_PARAM + getCityName();
			URL url1 			= new URL(locationCurl);
			hCon1 	 			= (HttpURLConnection)url1.openConnection();
			hCon1.setDoInput(true);
			hCon1.setDoOutput(false);
			hCon1.setRequestMethod(WeatherTemperature.POST);
			hCon1.setRequestProperty(WeatherTemperature.ACCEPT_ENCODING, WeatherTemperature.GZIP);
			hCon1.setRequestProperty(WeatherTemperature.CONTENT_TYPE, "application/json; charset=utf-8");
			hCon1.connect();
			
			if(hCon1.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String encoding = hCon1.getContentEncoding();
				if(encoding != null && encoding.equals(WeatherTemperature.GZIP)) {
					bf1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(hCon1.getInputStream())));
				} else {
					bf1 = new BufferedReader(new InputStreamReader(hCon1.getInputStream()));
				}
				
				while((line1 = bf1.readLine()) != null){
					response1 +=line1;
				}
				
				if(response1 != null && response1.length() > 0){
					JSONParser jParser = new JSONParser();
					JSONArray  jsArr1 = (JSONArray) jParser.parse(response1);
					if(jsArr1 != null && jsArr1.size() > 0) {
						JSONObject jsObj1 = (JSONObject) jsArr1.get(0);
						locationKey = (String) jsObj1.get(WeatherTemperature.KEY);
					}
				} else
					return null;
			} else {
				bf1 = new BufferedReader(new InputStreamReader(hCon1.getErrorStream()));
				while((line1 = bf1.readLine()) != null){
					response1 += line1;
				}
			}
		} catch(IOException | ParseException ex) {
			System.err.println(ex.getMessage());
		} finally {
			if(bf1 != null) {
				try {
					bf1.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			hCon1.disconnect();
		}
		return locationKey;
	}
}
