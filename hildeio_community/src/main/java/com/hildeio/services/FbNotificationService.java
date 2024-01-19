package com.hildeio.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hildeio.Log4Hilde;
import com.hildeio.firebase.FbConfiguration;
import com.hildeio.models.Log4HildePushNotificationModel;

@Service
public class FbNotificationService {

	@Autowired
	Log4Hilde log4Hilde;

	/* *********************************************************************************************
	*
	* sendPushNotification
	* 
	* ********************************************************************************************/		
	public Log4HildePushNotificationModel sendPushNotification(String target, String title, String body, String eventId) throws IOException {

		try {
			
			URL googleapisUrl = new URL(new FbConfiguration().getFirebaseApiUrl());					
			HttpURLConnection httpURLConnection = (HttpURLConnection) googleapisUrl.openConnection();
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Authorization", "Bearer " + FbConfiguration.getAccessToken());
			httpURLConnection.setRequestProperty("access_token_auth", "true");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");

			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("token", target); 

			JSONObject jsonMessageInfo = new JSONObject();
			jsonMessageInfo.put("title", title);
			jsonMessageInfo.put("body", body);
			
			jsonMessage.put("notification", jsonMessageInfo);
			
			JSONObject jsonX = new JSONObject();
			jsonX.put("message", jsonMessage);
			
			OutputStreamWriter outputStreamWriter = 
					new OutputStreamWriter(httpURLConnection.getOutputStream());
			
			JSONObject jsonLogBefore = new JSONObject();
			jsonLogBefore.put("getOutputStream", httpURLConnection.getOutputStream());			
			jsonLogBefore.put("jsonX", jsonX.toString());
			
			this.log4Hilde.doInfoLog(
					"01-vor-versenden", 
					"target = " + target + ", title = " + title + ", body = " + body,  
					jsonLogBefore,
					eventId
					);							
						
			outputStreamWriter.write(jsonX.toString());			
			outputStreamWriter.flush();

			BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
			
			JSONObject jsonLogAfter = new JSONObject();
			jsonLogAfter.put("getInputStream()", httpURLConnection.getInputStream());
			
			this.log4Hilde.doInfoLog(
					"03-nach-versenden", 
					"target = " + target + ", title = " + title + ", body = " + body,  
					jsonLogAfter,
					eventId
					);							
						
			
			Log4HildePushNotificationModel log4HildePushNotificationModel = 
					new Log4HildePushNotificationModel();
			
			log4HildePushNotificationModel.setReturnCode(0);
			log4HildePushNotificationModel.setMessageId(bufferedReader.readLine());
			
			this.log4Hilde.doInfoLog(
					"04-log4HildePushNotificationModel", 
					new ObjectMapper().writeValueAsString(log4HildePushNotificationModel), 
					new JSONObject(),
					eventId
					);
			
			return log4HildePushNotificationModel;
			
		} catch (Exception exception) {
			
			Log4HildePushNotificationModel log4HildePushNotificationModel = 
					new Log4HildePushNotificationModel();
			
			log4HildePushNotificationModel.setReturnCode(1);
			log4HildePushNotificationModel.setException(exception);
			
			this.log4Hilde.doInfoLog(
					"EX-01", 
					new ObjectMapper().writeValueAsString(log4HildePushNotificationModel), 
					new JSONObject(),
					eventId
					);
						
			return log4HildePushNotificationModel;			
		}
	}
}