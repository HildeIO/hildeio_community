package com.hildeio.firebase;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.hildeio.Log4Hilde;

import javax.annotation.PostConstruct;

@Service
public class FirebaseInitialize {

	@Autowired
	Log4Hilde log4Hilde;

	
	/* *********************************************************************************************
	 *
	 * initialize
	 * 
	 * ********************************************************************************************/		
	@PostConstruct
	public void initialize() {
		
		try {
			
			FirebaseOptions options = FirebaseOptions.builder()
					  .setCredentials(GoogleCredentials.fromStream(new FbConfiguration().getServiceAccount()))
					  .setDatabaseUrl(new FbConfiguration().getFirestoreUrl())
					  .build();
			
			FirebaseApp.initializeApp(options);

			
			FbConfiguration config = new FbConfiguration();			
			
			for(String collectionName : config.getCollectionNames()) {
				
				FbEventHubInitialize.createEventHub(null, collectionName, config, log4Hilde);
				log4Hilde.doInfoLog("01-" + collectionName, "EventHub initialisiert", new JSONObject(), null);
			}
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
		}
	}	
}
