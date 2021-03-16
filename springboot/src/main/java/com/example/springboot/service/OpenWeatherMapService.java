package com.example.springboot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class OpenWeatherMapService {
	
	private String url="http://api.openweathermap.org/data/2.5/weather?q=";
	
	private String location="Ankara";
	
	private String urlPart="&appid=";
	
	private String apikey="";
	
	private String fullUrl=url+location+urlPart+apikey;
	
	public static Map<String, Object> jsonToMap(String str) {
		Map<String, Object> map = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>() {
		}.getType());
		return map;
	}

	public String getTemperature() throws MalformedURLException {
		String temperature="";
		try {
			StringBuilder sb=new StringBuilder();
			URL url=new URL(fullUrl);
			URLConnection connection=url.openConnection();
			BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			
			while((line=br.readLine())!=null) {
				
				sb.append(line);
			}
			br.close();
			
			Map<String,Object> restMap=jsonToMap(sb.toString());
			Map<String,Object> mainMap=jsonToMap(restMap.get("main").toString());
			
			Float fromKelvinToCelcius=Float.parseFloat(mainMap.get("temp").toString())-273.15F;
			temperature=""+Math.round(fromKelvinToCelcius);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temperature;
	}
}
