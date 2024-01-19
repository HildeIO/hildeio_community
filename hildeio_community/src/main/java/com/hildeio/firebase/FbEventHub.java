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

public class FbEventHub implements EventListener<QuerySnapshot> {
	
	private FbConfiguration config;
	private CollectionReference collectionReference;
	private ListenerRegistration registration;
	private Log4Hilde log4Hilde;
	
	/* *********************************************************************************************
	 *
	 * EventHub
	 * 
	 * ********************************************************************************************/		
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
	
	
	/* *********************************************************************************************
	 *
	 * registerListner
	 * 
	 * ********************************************************************************************/		
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
    
    
	/* *********************************************************************************************
	 *
	 * unregisterListener
	 * 
	 * ********************************************************************************************/		
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
    
    
	/* *********************************************************************************************
	 *
	 * onEvent
	 * 
	 * ********************************************************************************************/		
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

    
	/* *********************************************************************************************
	 *
	 * getDocumentChanges
	 * 
	 * ********************************************************************************************/		
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
