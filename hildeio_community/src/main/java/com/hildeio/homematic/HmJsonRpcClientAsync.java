package com.hildeio.homematic;

import java.util.concurrent.Future;

import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hildeio.Log4Hilde;
import com.hildeio.models.HmRequestModel;
import com.hildeio.models.HmResponseModel;


public class HmJsonRpcClientAsync {

	
	/* *********************************************************************************************
	*
	* doRequest
	* 
	* ********************************************************************************************/
	public static HmResponseModel doRequest(final HmRequestModel hmRequestModel, Log4Hilde log4Hilde, String eventId) throws Exception {

		final CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		    
		HmResponseConsumer hmResponseConsumer = new HmResponseConsumer(log4Hilde, eventId);

		try {
			
			log4Hilde.doInfoLog(
					"01-hmRequestModel", 
					new ObjectMapper().writeValueAsString(hmRequestModel), 
					new JSONObject(),
					eventId
					);		    

			final Future<Boolean> future = httpClient.execute(
						HttpAsyncMethods.createPost(
								new HmConfiguration().getJsonUrl(), 
								new Gson().toJson(hmRequestModel), 
								ContentType.APPLICATION_JSON
								),
						hmResponseConsumer, 
						null
					);
		      
			final Boolean result = future.get();
		      		      
			log4Hilde.doInfoLog(
					"02-future.get()", 
					result.toString(), 
					new JSONObject(),
					eventId
					);		    
			
			if (result != null && result.booleanValue()) {

				if(hmResponseConsumer.getHmResponseModel().getError() != null) {
					
					log4Hilde.doInfoLog(
							"03-hmResponseModel", 
							new ObjectMapper().writeValueAsString(hmResponseConsumer.getHmResponseModel()), 
							new JSONObject(),
							eventId
							);		    					
				}
				
			} else {

		    	log4Hilde.doInfoLog(
		    			"02-kein-ResponseModel", 
		    			"Beim Aufruf der HomeMatic JSON-RPC ist ein Fehler aufgetreten.", 
		    			new JSONObject(),
		    			eventId
		    			);
			}
			
			return hmResponseConsumer.getHmResponseModel();
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);		
			
		} finally {
			
		    httpClient.close();
		}
		
		return hmResponseConsumer.getHmResponseModel();		
	}
}

