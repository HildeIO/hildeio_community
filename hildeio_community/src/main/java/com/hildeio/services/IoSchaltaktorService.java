package com.hildeio.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.hildeio.Log4Hilde;
import com.hildeio.homematic.HmConfiguration;
import com.hildeio.homematic.HmEventManager;
import com.hildeio.models.IoSchaltaktorModel;

/***********************************************************************************************
 * 
 * Service zur Zustands-Aktualisierung der Schaltaktoren 
 *    
 ***********************************************************************************************/
@Service
public class IoSchaltaktorService {

	/***********************************************************************************************
	 * 
	 * KONSTANTE fuer Firestore Collections ioSchaltaktoren
	 *    
	 ***********************************************************************************************/	
	final static String COLLECTION = "ioSchaltaktoren";
	
	
	/***********************************************************************************************
	 * 
	 * Dependency Injection auf Log4Hilde
	 *    
	 ***********************************************************************************************/	
	@Autowired
	Log4Hilde log4Hilde;
	
	
	/***********************************************************************************************
	 * 
	 * Geaenderte Werte von der HomeMatic CCU an => Firestore-Collection ioSchaltaktorModel uebertragen.
	 * 
	 * 
	 * @param ioSchaltaktorModel Werte des Schaltaktors 
	 * @param eventId Aktuelle WorkflowId
	 * @return Aenderungsdatum (String) des Schaltaktor-Dokuments in ioSchaltaktoren. 
	 *    
	 ***********************************************************************************************/	
	public String updateSchaltaktor2Firestore(IoSchaltaktorModel ioSchaltaktorModel, String eventId) {
		
		try {
			
			if(ioSchaltaktorModel == null) {
				
				String message = "ioSchaltaktorModel ist null";
				
				this.log4Hilde.doInfoLog(
						"00-kein-ioSchaltaktorModel", 
						message, 
						new JSONObject(),
						eventId
						);
				
				return message;
			}
			
			ioSchaltaktorModel = this.updateModel(ioSchaltaktorModel, eventId);
			
			this.log4Hilde.doInfoLog(
					"01-ioSchaltaktorModel", 
					new ObjectMapper().writeValueAsString(ioSchaltaktorModel), 
					new JSONObject(),
					eventId
					);
				
			Firestore dbFirestore = FirestoreClient.getFirestore();
			ApiFuture<WriteResult> collectionsApiFuture = 
					dbFirestore
						.collection(IoSchaltaktorService.COLLECTION)
						.document(ioSchaltaktorModel.getIseId())
						.set(ioSchaltaktorModel);
			
			String executionTime = collectionsApiFuture.get().getUpdateTime().toString();
			
			this.log4Hilde.doInfoLog(
					"02-Gespeichert", 
					executionTime, 
					new JSONObject(),
					eventId
					);
				
			return executionTime;
		
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return exception.getMessage();
		}		
	}
	
	
	/***********************************************************************************************
	 * 
	 * Ermitteln der aktuellen Werte des zu aenderden Dokuments. Bestimmte Felder werden in
	 * dem ioSchaltaktorModel wieder gesetzt.
	 * 
	 * @param ioSchaltaktorModel Werte des Schaltaktors. 
	 * @param eventId Aktuelle WorkflowId
	 * @return aktualisiertes ioSchaltaktorModel. 
	 *    
	 ***********************************************************************************************/	
	private IoSchaltaktorModel updateModel(IoSchaltaktorModel ioSchaltaktorModel, String eventId) {

		try {

			Firestore dbFirestore = FirestoreClient.getFirestore();
			DocumentReference documentReference = 
					dbFirestore
						.collection(IoSchaltaktorService.COLLECTION)
						.document(ioSchaltaktorModel.getIseId());
			
			ApiFuture<DocumentSnapshot> future = documentReference.get();
			DocumentSnapshot document = future.get();
			
			IoSchaltaktorModel ioSchaltaktorModelSnapshot;
			if(document.exists()) {
				ioSchaltaktorModelSnapshot = document.toObject(IoSchaltaktorModel.class);
				
				this.log4Hilde.doInfoLog(
						"01-ioSchaltaktorModelSnapshot", 
						new ObjectMapper().writeValueAsString(ioSchaltaktorModelSnapshot), 
						new JSONObject(),
						eventId
						);

				ioSchaltaktorModel.setDisplayName(ioSchaltaktorModelSnapshot.getDisplayName());
				ioSchaltaktorModel.setOrder(ioSchaltaktorModelSnapshot.getOrder());
				
				return ioSchaltaktorModel;
			}
			
			return null;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return null;
		}		
	}
	
	
	/***********************************************************************************************
	 * 
	 * Geaenderte Werte aus der Firestore-Collection ioSchaltaktoren werden an => die HomeMatic CCU  
	 * uebertragen.
	 * 
	 * @param document Werte des ioSchaltaktoren-Dokuments.
	 * @param log4Hilde Logging-Instanz 
	 * @param eventId Aktuelle WorkflowId
	 *    
	 ***********************************************************************************************/	
	public void update2Homematic(QueryDocumentSnapshot document, Log4Hilde log4Hilde, String eventId) {
		
		try {

			if(document == null) {
				
				String message = "document ist null";
				
				log4Hilde.doInfoLog(
						"00-kein-ioSchaltaktorModel", 
						message, 
						new JSONObject(),
						null
						);
				
				return;
			}
			
			IoSchaltaktorModel ioSchaltaktorModel = document.toObject(IoSchaltaktorModel.class);

			log4Hilde.doInfoLog(
					"01-ioSchaltaktorModel", 
					new ObjectMapper().writeValueAsString(ioSchaltaktorModel), 
					new JSONObject(),
					eventId
					);

			
			HmEventManager hmControllerInterface = new HmEventManager(log4Hilde);
			hmControllerInterface.save(
					new HmConfiguration().getRfcInterfaceSetValue(), 
					ioSchaltaktorModel,
					eventId
					);

		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}		
	}	
}
