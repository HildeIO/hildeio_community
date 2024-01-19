package com.hildeio.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.TopicManagementResponse;
import com.hildeio.Log4Hilde;
import com.hildeio.firebase.FbConfiguration;
import com.hildeio.models.FbMessageTopicModel;

@Service
public class FbMessageTopicService {

	@Autowired
	Log4Hilde log4Hilde;
	
	/* *********************************************************************************************
	 *
	 * Hinzufügen eines MobileDevice-Tokens zu einem Topic
	 * 
	 * ********************************************************************************************/		
	public String subscribeTopic(FbMessageTopicModel fbMessageTopicModel) {
		
		try {
			
			List<String> registrationToken = Arrays.asList(fbMessageTopicModel.getRegistrationToken());
			
			TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
					registrationToken, fbMessageTopicModel.getTopicName());
			return response.getSuccessCount() + " DeviceToken " + registrationToken + " wurde dem Topic " + fbMessageTopicModel.getTopicName() + " hinzugefügt.";
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return exception.getMessage();
		}				
	}
	
	/* *********************************************************************************************
	 *
	 * Entfernen eines MobileDevice-Tokens aus einem Topic
	 * 
	 * ********************************************************************************************/		
	public String unsubscribeTopic(FbMessageTopicModel fbMessageTopicModel) {
		
		try {
			
			List<String> registrationToken = Arrays.asList(fbMessageTopicModel.getRegistrationToken());
			
			TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
					registrationToken, fbMessageTopicModel.getTopicName());
			return response.getSuccessCount() + " DeviceToken " + registrationToken + " wurde von dem Topic " + fbMessageTopicModel.getTopicName() + " entfernt.";
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return exception.getMessage();
		}		
	}
	
	/* *********************************************************************************************
	*
	* getTopicsOfDevice
	* 
	* ********************************************************************************************/		
	public String getTopicsOfDevice(String deviceToken) {

		try {
			
			URL googleapisUrl = new URL(new FbConfiguration().getDeviceTokenInfo(deviceToken));					
			HttpURLConnection httpURLConnection = (HttpURLConnection) googleapisUrl.openConnection();
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Authorization", "Bearer " + FbConfiguration.getAccessToken());
			httpURLConnection.setRequestProperty("access_token_auth", "true");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");

			new OutputStreamWriter(httpURLConnection.getOutputStream());

			BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

			return bufferedReader.readLine();
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return exception.getMessage();
		}		
	}
}
