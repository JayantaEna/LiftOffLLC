package com.dev3.liftoff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Weather24HourTemperature {	
	final Map<String, Double> lMap1;
	
	Weather24HourTemperature() {
		lMap1 = new LinkedHashMap<>();
	}
	
	public Map<String, Double> getTemperatureTimeMap() {
		return lMap1;
	}
	
	public List<String> findMaxMinTemperatureOfDay(String locationKey) {
		String line1 			= "";
		String response1 		= "";
		BufferedReader bf1 		= null;
		HttpURLConnection hCon1 = null;
		List<String> list1 		= null;
		
		try {
			String Temp24HourCurl = WeatherTemperature.TEMP_CURL1 + locationKey + WeatherTemperature.TEMP_CURL2 + WeatherTemperature.API_KEY;
			URL url1 = new URL(Temp24HourCurl);
			hCon1 	 = (HttpURLConnection)url1.openConnection();
			hCon1.setDoInput(true);
			hCon1.setDoOutput(false);
			hCon1.setRequestMethod(WeatherTemperature.GET);
			hCon1.setRequestProperty(WeatherTemperature.ACCEPT_ENCODING, WeatherTemperature.GZIP);
			hCon1.setRequestProperty(WeatherTemperature.CONTENT_TYPE, "application/json; charset=utf-8");
			hCon1.connect();
			
			if(hCon1.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String encoding = hCon1.getContentEncoding();
				if(encoding != null && encoding.equals(WeatherTemperature.GZIP)){
					bf1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(hCon1.getInputStream())));
				} else {
					bf1 = new BufferedReader(new InputStreamReader(hCon1.getInputStream()));
				}
				
				while((line1=bf1.readLine()) != null) {
					response1 += line1;
				}
				
				if(response1 != null && response1.length() > 0){
					JSONParser jsParser1 = new JSONParser();
					JSONArray jsArr1 	 = (JSONArray) jsParser1.parse(response1);
					if(jsArr1 != null && jsArr1.size() > 0) {
						Map<String, Double> hMap1 = new HashMap<>();
						for(Object obj1 : jsArr1) {
							JSONObject jsObj1 		= (JSONObject) obj1;
							String observationTime1 = (String)jsObj1.get(WeatherTemperature.LOCAL_DATETIME);
							observationTime1 		= (String)observationTime1.subSequence(0, observationTime1.indexOf("+"));
							JSONObject jsInnerObj1 	= (JSONObject)jsObj1.get(WeatherTemperature.TEMPERATURE);
							JSONObject jsInnerObj2 	= (JSONObject)jsInnerObj1.get(WeatherTemperature.TEMPERATURE_METRIC);
							Double tempValue 		= (Double)jsInnerObj2.get(WeatherTemperature.TEMPERATURE_VALUE);
							hMap1.put(observationTime1, tempValue);
						}
						list1 = sortMapByValue(hMap1);
					}
				} else
					return null;				
			} else {
				bf1 = new BufferedReader(new InputStreamReader(hCon1.getErrorStream()));
				while((line1=bf1.readLine()) != null) {
					response1 += line1;
				}
				System.out.println(response1);
			}
		} catch(ParseException | IOException ex) {
			System.err.println(ex.getMessage());
		} finally {
			if(bf1 != null) {
				try {
					bf1.close();
				}catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			hCon1.disconnect();
		}
		return list1;
	}
	
	private List<String> sortMapByValue(Map<String, Double> map1) {
		Comparator<Map.Entry<String, Double>> cmpByValue1 	= (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
		List<Map.Entry<String, Double>> list1 				= new ArrayList<>(map1.entrySet());
		List<String> list2 									= new ArrayList<>();
		Collections.sort(list1, cmpByValue1);
		for(Map.Entry<String, Double> e1 : list1){
			list2.add(e1.getValue() + "#" + e1.getKey());
			lMap1.put(e1.getKey(), e1.getValue());
		}
		return list2;
	}
}
