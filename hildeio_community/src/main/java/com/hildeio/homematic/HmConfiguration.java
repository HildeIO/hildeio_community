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

public class HmConfiguration {

	@Autowired
	Log4Hilde log4Hilde;

	private static final String HM_CONFIG_FILE = "homematic.json";
	
	private static final String JSON_API = "/api/homematic.cgi";
	
	private static final String RFC_LOGIN = "Session.login";
	
	private static final String RFC_LOGOUT = "Session.logout";
	
	private static final String RFC_INTERFACE_SET_VALUE = "Interface.setValue";
	
	private static final String RFC_SYS_VAR_SET_BOOL = "SysVar.setBool";

	
	/* *********************************************************************************************
	 *
	 * 	getHmConfigFile
	 * 
	 * ********************************************************************************************/			
	public FileInputStream getHmConfigFile() {
		
		try {
			
			ClassLoader classLoader = HildeIoApplication.class.getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource(HM_CONFIG_FILE)).getFile());

			return new FileInputStream(file.getAbsolutePath());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}	
	
	
	/* *********************************************************************************************
	 *
	 * 	getHmValue
	 * 
	 * ********************************************************************************************/			
	public String getHmValue(String key) throws IOException {
		
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
	
	
	/* *********************************************************************************************
	 *
	 * 	getUsername
	 * 
	 * ********************************************************************************************/			
	public String getUsername() throws IOException {
		
		return getHmValue("username");
	}

	
	/* *********************************************************************************************
	 *
	 * 	getPassword
	 * 
	 * ********************************************************************************************/			
	public String getPassword() throws IOException {
		
		return getHmValue("password");
	}
	
	
	/* *********************************************************************************************
	 *
	 * 	getJsonUrl
	 * 
	 * ********************************************************************************************/			
	public String getJsonUrl() throws IOException {
		
		return getHmValue("url") + JSON_API;
	}
	
	
	/* *********************************************************************************************
	 *
	 * 	getRfcLogin
	 * 
	 * ********************************************************************************************/			
	public String getRfcLogin() {
		
		return RFC_LOGIN;
	}
	
	
	/* *********************************************************************************************
	 *
	 * 	getRfcInterfaceSetValue
	 * 
	 * ********************************************************************************************/			
	public String getRfcInterfaceSetValue() {
		
		return RFC_INTERFACE_SET_VALUE;
	}


	/* *********************************************************************************************
	 *
	 * 	getRfcSysVarSetBool
	 * 
	 * ********************************************************************************************/			
	public String getRfcSysVarSetBool() {
		
		return RFC_SYS_VAR_SET_BOOL;
	}

	
	/* *********************************************************************************************
	 *
	 * 	getRfcLogout
	 * 
	 * ********************************************************************************************/			
	public String getRfcLogout() {
		
		return RFC_LOGOUT;
	}
}
