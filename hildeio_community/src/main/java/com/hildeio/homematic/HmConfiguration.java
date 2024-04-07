package com.hildeio.homematic;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.hildeio.HildeIoApplication;
import com.hildeio.Log4Hilde;

/***********************************************************************************************
 * HomeMatic Konfigurationen
 ***********************************************************************************************/
public class HmConfiguration {

	/***********************************************************************************************
	 * Dependency Injection auf Log4Hilde
	 ***********************************************************************************************/	
	@Autowired
	Log4Hilde log4Hilde;
	
	/***********************************************************************************************
	 * KONSTANTE fuer HomeMatic-Authentifizieurng.
	 ***********************************************************************************************/	
	private static final String HM_CONFIG_FILE = "homematic.json";
	
	/***********************************************************************************************
	 * KONSTANTE fuer Api-URL zu JSON-RPC
	 ***********************************************************************************************/	
	private static final String JSON_API = "/api/homematic.cgi";
	
	/***********************************************************************************************
	 * KONSTANTE fuer JSON-RPC zum Durchfuehren der Benutzeranmeldung.
	 ***********************************************************************************************/	
	private static final String RFC_LOGIN = "Session.login";
	
	/***********************************************************************************************
	 * KONSTANTE fuer JSON-RPC zum Beenden einer Sitzung.
	 ***********************************************************************************************/	
	private static final String RFC_LOGOUT = "Session.logout";
	
	/***********************************************************************************************
	 * KONSTANTE fuer JSON-RPC Prozedur zum setzen eines einzelnen Werts im Parameterset Values.
	 ***********************************************************************************************/	
	private static final String RFC_INTERFACE_SET_VALUE = "Interface.setValue";

	/***********************************************************************************************
	 * Laden der HomeMatic-Authentifizieurngsdatei.
	 * 
	 * @return HomeMatic-Authentifizieurngsdatei
	 ***********************************************************************************************/	
	private FileInputStream getHmConfigFile() {
		
		try {
			
			ClassLoader classLoader = HildeIoApplication.class.getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource(HM_CONFIG_FILE)).getFile());

			return new FileInputStream(file.getAbsolutePath());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}	
	
	/***********************************************************************************************
	 * Ermitteln des Wertes des uebergebenen Keys aus der Konfigdatei.
	 * 
	 * @param key Key
	 * @return KeyValue
	 * @throws IOException Erforderliche Exception.
	 ***********************************************************************************************/	
	private String getHmValue(String key) throws IOException {
		
		StringBuilder stringBuilder = new StringBuilder();
	    Reader reader = new InputStreamReader(getHmConfigFile(), "UTF-8");
	    
	    char[] buffer = new char[1024];
	    int iChars = reader.read(buffer);
	    
	    while(iChars > 0) {
	    	stringBuilder.append(buffer, 0, iChars);
	    	iChars = reader.read(buffer);
	    }		
	    
		JSONObject jsonObject = new JSONObject(stringBuilder.toString());
		return jsonObject.getString(key);
	}	
	
	/***********************************************************************************************
	 * Ermitteln des Benutzername aus der Konfigdatei.
	 * 
	 * @return Benutername
	 * @throws IOException Erforderliche Exception.
	 ***********************************************************************************************/	
	public String getUsername() throws IOException {
		
		return getHmValue("username");
	}
	
	/***********************************************************************************************
	 * Ermitteln des Passworts aus der Konfigdatei.
	 * 
	 * @return Passwort
	 * @throws IOException Erforderliche Exception.
	 ***********************************************************************************************/	
	public String getPassword() throws IOException {
		
		return getHmValue("password");
	}
	
	/***********************************************************************************************
	 * Ermitteln des Pfads zur JSON-RPC.
	 * 
	 * @return URI
	 * @throws IOException Erforderliche Exception.
	 ***********************************************************************************************/	
	public String getJsonUrl() throws IOException {
		
		return getHmValue("url") + JSON_API;
	}
	
	/***********************************************************************************************
	 * JSON-RPC zum Durchfuehren der Benutzeranmeldung
	 * 
	 * @return JSON-RPC Prozedur
	 ***********************************************************************************************/	
	public String getRfcLogin() {
		
		return RFC_LOGIN;
	}
	
	/***********************************************************************************************
	 * JSON-RPC Prozedur zum setzen eines einzelnen Werts im Parameterset Values.
	 * 
	 * @return JSON-RPC Prozedur
	 ***********************************************************************************************/	
	public String getRfcInterfaceSetValue() {
		
		return RFC_INTERFACE_SET_VALUE;
	}

	/***********************************************************************************************
	 * JSON-RPC zum Beenden einer Sitzung.
	 * 
	 * @return JSON-RPC Prozedur
	 ***********************************************************************************************/	
	public String getRfcLogout() {
		
		return RFC_LOGOUT;
	}
}
