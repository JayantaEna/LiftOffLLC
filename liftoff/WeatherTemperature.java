package com.dev3.liftoff;

public interface WeatherTemperature {
	public static final String LOCATION_CURL		= "http://dataservice.accuweather.com/locations/v1/cities/search";
	public static final String API_KEY 				= "?apikey=A5vf1S4NyXwv8KfuYoj9RUwJ0GOEtF80";
	public static final String CITY_PARAM			= "&q="; 
	public static final String TEMP_CURL1 			= "http://dataservice.accuweather.com/currentconditions/v1/";
	public static final String TEMP_CURL2			= "/historical/24";
	public static final String POST					= "POST";
	public static final String GET					= "GET";
	public static final String LOCAL_DATETIME 		= "LocalObservationDateTime";
	public static final String TEMPERATURE 			= "Temperature";
	public static final String TEMPERATURE_METRIC	= "Metric";
	public static final String TEMPERATURE_VALUE	= "Value";
	public static final String ACCEPT_ENCODING		= "Accept-Encoding";
	public static final String CONTENT_TYPE			= "Content-Type";
	public static final Object KEY 					= "Key";
	public static final String GZIP 				= "gzip";
	
}
 