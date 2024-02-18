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

/***********************************************************************************************
 * Firebase Konfigurationen
 ***********************************************************************************************/
public class FbConfiguration {

	/***********************************************************************************************
	 * Dependency Injection auf Log4Hilde
	 ***********************************************************************************************/	
	@Autowired
	Log4Hilde log4Hilde;
	
	/***********************************************************************************************
	 * KONSTANTE fuer Firebase-Authentifizieurng
	 ***********************************************************************************************/	
	private static final String SERVICE_ACCOUNT_FILE = "serviceAccountKey.json";
	
	/***********************************************************************************************
	 * KONSTANTE fuer FCM PushNotifications
	 ***********************************************************************************************/	
	private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/v1/projects/{myProjectId}/messages:send";
	
	/***********************************************************************************************
	 * KONSTANTE zur Firebase
	 ***********************************************************************************************/	
	private static final String FIRESTORE_URL = "https://{myProjectId}.firebaseio.com";
	
	/***********************************************************************************************
	 * KONSTANTE fuer Firebase Messaging
	 ***********************************************************************************************/	
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	
	/***********************************************************************************************
	 * KONSTANTE fuer FCM Topic Info 
	 ***********************************************************************************************/	
	private static final String FIREBASE_DEVICE_TOKEN_INFO_URL = "https://iid.googleapis.com/iid/info/{deviceToken}?details=true";	
	
	/***********************************************************************************************
	 * KONSTANTE fuer Firebase Messaging Scopes
	 ***********************************************************************************************/	
	private static final String[] SCOPES = { MESSAGING_SCOPE };	
	
	/***********************************************************************************************
	 * KONSTANTE fuer FCM Topic
	 ***********************************************************************************************/	
	private static final String LOGGING_TOPIC = "MeinIoTest";
	
	/***********************************************************************************************
	 * KONSTANTE fuer FCM Topic
	 ***********************************************************************************************/	
	private static final String KONTAKT_TOPIC = "Fenster";

	/***********************************************************************************************
	 * KONSTANTE zur Firebase Collection
	 ***********************************************************************************************/	
	private static final String COLLECTION_SCHALTAKTOREN = "ioSchaltaktoren";
	
	/***********************************************************************************************
	 * KONSTANTE zur Firebase Collection
	 ***********************************************************************************************/	
	private static final String COLLECTION_HEIZKOERPER = "ioHeizkoerper";
	
	/***********************************************************************************************
	 * Laden der Firebase-Authentifizieurngsdatei.
	 * 
	 * @return Firebase-Authentifizieurngsdatei
	 ***********************************************************************************************/	
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

	/***********************************************************************************************
	 * 
	 * Ermitteln der ProjectId aus der Firebase-Authentifizieurngsdatei.
	 * 
	 * @return ProjectId
	 ***********************************************************************************************/	
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
	
	/***********************************************************************************************
	 * FCM Logging-Topic
	 * 
	 * @return Vollstaendige URI
	 ***********************************************************************************************/	
	public String getLoggingTopic() {
		
		try {
			
			return "/topics/" + LOGGING_TOPIC;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		

	}
				
	/***********************************************************************************************
	 * Kontakt-Topic
	 * 
	 * @return Vollstaendige URI
	 ***********************************************************************************************/	
	public String getKontaktTopic() {
		
		try {
			
			return "/topics/" + KONTAKT_TOPIC;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}
				
	/***********************************************************************************************
	 * FCM PushNotification 
	 * 
	 * @return Vollstaendige URI
	 ***********************************************************************************************/	
	public String getFirebaseApiUrl() {
		
		try {
			
			return FIREBASE_API_URL.replace("{myProjectId}", getProjectId());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}
		
	/***********************************************************************************************
	 * DeviceToken Info
	 *  
	 * @param deviceToken Token von SmartDevice
	 * @return Vollstaendige URI
	 ***********************************************************************************************/	
	public String getDeviceTokenInfo(String deviceToken) {
		
		try {
			
			return FIREBASE_DEVICE_TOKEN_INFO_URL.replace("{deviceToken}", deviceToken);
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}
	
	/***********************************************************************************************
	 * Firestore
	 *  
	 * @return Vollstaendige URI
	 ***********************************************************************************************/	
	public String getFirestoreUrl() {
		
		try {
			
			return FIRESTORE_URL.replace("{myProjectId}", getProjectId());
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);
			return null;
		}		
	}

	/***********************************************************************************************
	 * Registrieren der Firebase Collections für den EventHub. 
	 * 
	 * @return Liste der definierten Collectionsnamen.
	 ***********************************************************************************************/	
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
	
	/***********************************************************************************************
	 * Verteilen der Objektdaten an jeweilige Service-Instanz. 
	 * 
	 * @param queryDocumentSnapshot Geaendertes Collection-Dokument.
	 * @param collectionName Name der Firebase Collection.
	 * @param log4Hilde Aktuelle Logging-Instanz.
	 * @param eventId Aktuelle WorkflowId.
	 ***********************************************************************************************/	
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
	
	/***********************************************************************************************
	 * Generierung Bearer-Token.
	 * 
	 * @return Token
	 * @throws IOException Erforderliche Exception.
	 ***********************************************************************************************/	
	public static String getAccessToken() throws IOException {
		
		GoogleCredentials googleCredentials = GoogleCredentials
		        .fromStream(new FbConfiguration().getServiceAccount())
		        .createScoped(Arrays.asList(SCOPES));
		googleCredentials.refresh();
		return googleCredentials.getAccessToken().getTokenValue();
	}		
}
