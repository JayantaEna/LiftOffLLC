package com.dev3.liftoff;

import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class WeatherTemperatureMain implements WeatherTemperature {
	final WeatherLocationSearch wls1;
	final Weather24HourTemperature wht1;
	static String locationKey = null;
	static List<String> list1 = null;
	
	WeatherTemperatureMain() {
		this.wls1 = new WeatherLocationSearch();
		this.wht1 = new Weather24HourTemperature();
	}
	
	public static void main(String[] args) {
		WeatherTemperatureMain wTImpl1 	= new WeatherTemperatureMain();
		Scanner sc1 					= new Scanner(System.in);
		
		do {
			System.out.println("Enter name of city : ");
			String cityName = sc1.next();
			wTImpl1.fetchLocationKey(cityName);
			if(locationKey != null && locationKey.length() > 0){
				wTImpl1.fetchMaxMinTemperature(locationKey);
				if(list1 != null && list1.size() > 0){
					wTImpl1.createHtmlToDisplay();
					wTImpl1.plotTemperatureTimeGraph();
				}
			} else {
				System.out.println("Please enter a valid location.");
			}
			System.out.println("Do you want to search for another city ? Y/N");
			
		} while(sc1.next().equalsIgnoreCase("Y"));
		sc1.close();
	}

	private void fetchLocationKey(String cityName) {
		System.out.println("City : " + cityName);
		wls1.setCityName(cityName); 
		locationKey = wls1.retreiveLocationKey();
	}
	
	private void fetchMaxMinTemperature(String locationKey) {
		list1 = wht1.findMaxMinTemperatureOfDay(locationKey);
	}
	
	private void createHtmlToDisplay() {
		StringBuilder htmlStrBldr1 = new StringBuilder();
		htmlStrBldr1.append("<html>");
		htmlStrBldr1.append("<head><title>Temperature Report</title></head>");
		htmlStrBldr1.append("<body>");
		htmlStrBldr1.append("<table border=\"1\" bordercolor=\"#000000\">");
		htmlStrBldr1.append("<tr><td><b></b></td><td><b>Temperature</b></td><td><b>Time of the Day</b></td></tr>");
		htmlStrBldr1.append("<tr><td><b>Maximum</b></td><td><b>");
		String temperature = list1.get(list1.size()-1);
		String[] str1 = temperature.split("#");
		htmlStrBldr1.append(str1[0]+" C</b></td><td><b>"+str1[1]+"</b></td></tr>");
		temperature = list1.get(0);
		str1 = temperature.split("#");
		htmlStrBldr1.append("<tr><td><b>Minimum</b></td><td><b>");
		htmlStrBldr1.append(str1[0]+" C</b></td><td><b>"+str1[1]+"</b></td></tr>");
		htmlStrBldr1.append("</table></body></html>");
		writeToFile(htmlStrBldr1.toString(), "TempUpdate.html");
	}

	private void writeToFile(String htmlContent, String htmlFileName) {
		String pPath 	= System.getProperty("user.dir");
		String htmlFile = pPath + File.separator + htmlFileName; 
		File f1 		= new File(htmlFile);
		try {
			if(f1.exists()){
				File bckUpFileName = new File(pPath + File.separator + "backUp_"+htmlFileName);
				f1.renameTo(bckUpFileName);
				f1.createNewFile();
			}
		}catch (IOException e) {
			System.err.println(e.getMessage());
		}

		try(FileWriter fw1 = new FileWriter(f1.getAbsoluteFile());
			BufferedWriter bf1 = new BufferedWriter(fw1)) {
			bf1.write(htmlContent);
		}catch (IOException e) {
			System.err.println(e.getMessage());
		}	
	}
	
	private void plotTemperatureTimeGraph() {
		int nHours = 24;
		final double[][] dTempArr1 	= new double[2][nHours];
		Map<String, Double> lMap1 	= wht1.getTemperatureTimeMap();
		String[] timeOfDay 			= new String[nHours];
		int i=0, j=0, k=0;
		
		for(Map.Entry<String, Double> e1 : lMap1.entrySet()){
			String s1 = e1.getKey();
			s1 = (String) s1.subSequence(s1.indexOf("T")+1, s1.length());
			timeOfDay[k] 	= s1; 
			dTempArr1[0][i] = e1.getValue();
			dTempArr1[1][i] = j++;
			i++;k++;
		}
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Temperature in Increasing Order", dTempArr1);
		ValueAxis xAxis 		= new NumberAxis("Temperature");
		ValueAxis yAxis 		= new SymbolAxis("Time Of Day", timeOfDay);
		XYItemRenderer renderer = new XYLineAndShapeRenderer();
		XYPlot plot11 			= new XYPlot(dataset, xAxis, yAxis, renderer);
		JFreeChart chart11 		= new JFreeChart("Temperature Report", new Font("Tahoma", 0, 18), plot11, true);
		ChartFrame frame 		= new ChartFrame("Weather", chart11);
		frame.pack();
		frame.setVisible(true);
	}
}
