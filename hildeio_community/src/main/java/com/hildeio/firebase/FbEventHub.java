package com.hildeio.firebase;

import java.util.UUID;

import org.json.JSONObject;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.database.annotations.Nullable;
import com.hildeio.Log4Hilde;
 
/***********************************************************************************************
 * Klasse FbEventHub
 ***********************************************************************************************/
public class FbEventHub implements EventListener<QuerySnapshot> {
	
	/***********************************************************************************************
	 * Firebase Konfigurationsdatei
	 ***********************************************************************************************/	
	private FbConfiguration config;
	
	/***********************************************************************************************
	 * Firestore Collection
	 ***********************************************************************************************/	
	private CollectionReference collectionReference;
	
	/***********************************************************************************************
	 * Registrierung des Listeners der Collection 
	 ***********************************************************************************************/	
	private ListenerRegistration registration;

	/***********************************************************************************************
	 * Logging-Instanz 
	 ***********************************************************************************************/	
	private Log4Hilde log4Hilde;
	
	/***********************************************************************************************
	 * Konstruktor
	 * 
	 * @param collectionReference Firbase Collection
	 * @param config Firebase Konfigurationsdatei
	 * @param log4Hilde Aktuelle Logging-Instanz.
	 ***********************************************************************************************/	
	public FbEventHub(CollectionReference collectionReference, FbConfiguration config, Log4Hilde log4Hilde) {
		
		try {
			
			this.config = config;
			this.collectionReference = collectionReference;
			
			this.log4Hilde = log4Hilde;
			
			this.log4Hilde.doInfoLog(
					"01-EventHub erfolgreich initalisiert", 
					"Collection: " + collectionReference.getId(), 
					new JSONObject(),
					null
					);
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
		}		
	}
	
	
	/***********************************************************************************************
	 * Firestore Collection für Dokumentaenderungen registrieren.
	 ***********************************************************************************************/	
    public void registerListner(){
    	
    	try {
    		
    		registration = this.collectionReference.addSnapshotListener(this);
    		
			this.log4Hilde.doInfoLog(
					"01-Collection für Listener registriert", 
					collectionReference.getId(), 
					new JSONObject(),
					null
					);

		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
		}		
    }
    
	/***********************************************************************************************
	 * Unregister der Firestore Collection bei Dokumentaenderungen.
	 ***********************************************************************************************/	
    public void unregisterListener(){
    	
    	try {
    		
    		registration.remove();
    		
			this.log4Hilde.doInfoLog(
					"01-Collection vom Listener entfernt", 
					collectionReference.getId(), 
					new JSONObject(),
					null
					);
			    		
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
		}		
    }    
    
	/***********************************************************************************************
	 * Event bei Dokumentaenderungen in der Firestore Collection.
	 * 
	 * @param querySnapshot Collection-Dokumente.
	 * @param firestoreException Enthält Exception-Objekt im Fehlerfall.
	 ***********************************************************************************************/	
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirestoreException firestoreException) {

    	try {    		
    		
			if (firestoreException != null) {
				
				this.log4Hilde.doInfoLog(
						"EX-00-firestoreException: EventHub wird neu initialisiert..", 
						firestoreException.getMessage(), 
						new JSONObject(),
						null
						);
				
				FbEventHubInitialize.createEventHub(this, collectionReference.getId(), new FbConfiguration(), log4Hilde);
			}			   
					
			this.getDocumentChanges(querySnapshot, UUID.randomUUID().toString());

		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
		}		
    }
    
	/***********************************************************************************************
	 * Geänderte Dokumente mittels dispatch() dem jeweiligen Service zustellen. 
	 * 
	 * @param snapshots Collection-Dokumente.
	 * @param eventId Aktuelle WorkflowId.
	 ***********************************************************************************************/	
    private void getDocumentChanges(@Nullable QuerySnapshot snapshots, String eventId) {

    	try {    		
    		
    		for (DocumentChange documentChange : snapshots.getDocumentChanges()) {

    			switch (documentChange.getType()) {
    			
    				case ADDED:
    					break;
    			
    				case MODIFIED: {

    					QueryDocumentSnapshot queryDocumentSnapshot = documentChange.getDocument();    
    					
    					if(queryDocumentSnapshot != null) {
    						
	    					this.log4Hilde.doInfoLog(
	    							"01-queryDocumentSnapshot", 
	    							"Dispatcher wird aufgerufen", 
	    							new JSONObject(),
	    							eventId
	    							);
	    					
	    					this.config.dispatch(documentChange.getDocument(), collectionReference.getId(), this.log4Hilde, eventId);
	    					
    					} else {
    						
	    					this.log4Hilde.doInfoLog(
	    							"02-kein-queryDocumentSnapshot", 
	    							"queryDocumentSnapshot ist null", 
	    							new JSONObject(),
	    							eventId
	    							);	    					
    					}    					
    			      break;
    				}
    				
    				case REMOVED:
    					break;
    			      
    				default:
    					break;
    			}
    		}		  				
    		
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}		
    }   
}
