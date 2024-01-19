package com.hildeio.firebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.hildeio.HildeIoApplication;
import com.hildeio.Log4Hilde;
import com.hildeio.services.IoSchaltaktorService;
import com.hildeio.services.IoHeizkoerperService;

public class FbConfiguration {

	@Autowired
	Log4Hilde log4Hilde;
	
	

	/* ***************
	  GLOBALS 
	  **************** */
	
	private static final String SERVICE_ACCOUNT_FILE = "serviceAccountKey.json";
	
	private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/v1/projects/{myProjectId}/messages:send";

	private static final String FIRESTORE_URL = "https://{myProjectId}.firebaseio.com";
		
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	
	private static final String FIREBASE_DEVICE_TOKEN_INFO_URL = "https://iid.googleapis.com/iid/info/{deviceToken}?details=true";	

	private static final String[] SCOPES = { MESSAGING_SCOPE };	

	
	/* ***************
	  MESSAGE TOPICS 
	  **************** */

	private static final String LOGGING_TOPIC = "MeinIoTest";

	private static final String KONTAKT_TOPIC = "Fenster";


	/* ***************
	  MÜLLTERMIN
	  **************** */
	
	private static final String CURRENT_ICS_FILE = "/var/www/html/HILDE/files/veichtederpointweglandshut.ics";
	
	private static final String TEMP_ICS_FILE = "/var/www/html/HILDE/files/temp_veichtederpointweglandshut.ics";
	
	
	/* ***************
	  COLLECTIONS
	  **************** */
	
	private static final String COLLECTION_SCHALTAKTOREN = "ioSchaltaktoren";

	private static final String COLLECTION_HEIZKOERPER = "ioHeizkoerper";


	
	
	
	/* *********************************************************************************************
	 *
	 * getServiceAccount
	 * 
	 * ********************************************************************************************/		
	public FileInputStream getServiceAccount() {
		
		try {
			
			ClassLoader classLoader = HildeIoApplication.class.getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource(SERVICE_ACCOUNT_FILE)).getFile());

			return new FileInputStream(file.getAbsolutePath());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}
	
	/* *********************************************************************************************
	 *
	 * getProjectId
	 * 
	 * ********************************************************************************************/		
	private String getProjectId() {
						
		try {
			
			StringBuilder stringBuilder = new StringBuilder();
		    Reader reader = new InputStreamReader(getServiceAccount(), "UTF-8");
		    
		    char[] buffer = new char[1024];
		    int iChars = reader.read(buffer);
		    
		    while(iChars > 0) {
		    	stringBuilder.append(buffer, 0, iChars);
		    	iChars = reader.read(buffer);
		    }		
		    
			JSONObject jsonObject = new JSONObject(stringBuilder.toString());
			String project_id = jsonObject.getString("project_id");
	
			return project_id;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}				
	}
	
	
	/* *********************************************************************************************
	 *
	 * getLoggingTopic
	 * 
	 * ********************************************************************************************/		
	public String getLoggingTopic() {
		
		try {
			
			return "/topics/" + LOGGING_TOPIC;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}

				
	/* *********************************************************************************************
	 *
	 * getKontaktTopic
	 * 
	 * ********************************************************************************************/		
	public String getKontaktTopic() {
		
		try {
			
			return "/topics/" + KONTAKT_TOPIC;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}

					
	/* *********************************************************************************************
	 *
	 * getCurrentIcsFile
	 * 
	 * ********************************************************************************************/		
	public String getCurrentIcsFile() {
		
		try {
			
			return CURRENT_ICS_FILE;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}

			
	/* *********************************************************************************************
	 *
	 * getTempIcsFile
	 * 
	 * ********************************************************************************************/		
	public String getTempIcsFile() {
		
		try {
			
			return TEMP_ICS_FILE;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}

			
	/* *********************************************************************************************
	 *
	 * getFirebaseApiUrl
	 * 
	 * ********************************************************************************************/		
	public String getFirebaseApiUrl() {
		
		try {
			
			return FIREBASE_API_URL.replace("{myProjectId}", getProjectId());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}

		
	/* *********************************************************************************************
	 *
	 * getDeviceTokenInfo
	 * 
	 * ********************************************************************************************/		
	public String getDeviceTokenInfo(String deviceToken) {
		
		try {
			
			return FIREBASE_DEVICE_TOKEN_INFO_URL.replace("{deviceToken}", deviceToken);
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}

	
	/* *********************************************************************************************
	 *
	 * getFirestoreUrl
	 * 
	 * ********************************************************************************************/		
	public String getFirestoreUrl() {
		
		try {
			
			return FIRESTORE_URL.replace("{myProjectId}", getProjectId());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
		
	}

	
	/* *********************************************************************************************
	 *
	 * getCollectionNames
	 * 
	 * ********************************************************************************************/		
	public ArrayList<String> getCollectionNames(){
		
		try {
			
			ArrayList<String> routes = new ArrayList<String>();
			routes.add(COLLECTION_SCHALTAKTOREN);
			routes.add(COLLECTION_HEIZKOERPER);
			
			return routes;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
		
	}
	

	/* *********************************************************************************************
	 *
	 * dispatch
	 * 
	 * ********************************************************************************************/			
	public void dispatch(QueryDocumentSnapshot queryDocumentSnapshot, String collectionName, Log4Hilde log4Hilde, String eventId) {
		
		try {
			
			switch (collectionName) {
				case COLLECTION_SCHALTAKTOREN:	
					new IoSchaltaktorService().update2Homematic(queryDocumentSnapshot, log4Hilde, eventId);
					break;
				case COLLECTION_HEIZKOERPER:	
					new IoHeizkoerperService().update2Homematic(queryDocumentSnapshot, log4Hilde, eventId);
					break;
			}
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
		}		
		
	}
	
	/* *********************************************************************************************
	*
	* getAccessToken
	* 
	* ********************************************************************************************/			
	public static String getAccessToken() throws IOException {
		
		GoogleCredentials googleCredentials = GoogleCredentials
		        .fromStream(new FbConfiguration().getServiceAccount())
		        .createScoped(Arrays.asList(SCOPES));
		googleCredentials.refresh();
		return googleCredentials.getAccessToken().getTokenValue();
	}		
}
