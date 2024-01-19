package com.hildeio.firebase;

import org.json.JSONObject;

import com.google.firebase.cloud.FirestoreClient;
import com.hildeio.Log4Hilde;

public class FbEventHubInitialize {

	/* *********************************************************************************************
	 *
	 * createEventHub
	 * 
	 * ********************************************************************************************/		
	public static void createEventHub(FbEventHub eventHub, String collectionName, FbConfiguration config, Log4Hilde log4Hilde) {
		
		try {
			
			if(eventHub != null) {
				eventHub.unregisterListener();
				eventHub = null;
				System.gc();
				
				log4Hilde.doInfoLog("00-" + collectionName, "Aufräumarbeiten alter EventHub abgeschlossen", new JSONObject(), null);
			}

			FbEventHub eventHubNew = new FbEventHub(FirestoreClient.getFirestore().collection(collectionName), config, log4Hilde);
			eventHubNew.registerListner();

			log4Hilde.doInfoLog("01-" + collectionName, "EventHub initialisiert", new JSONObject(), null);
						
		} catch(Exception exception) {

			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
		}		
	}		
}
