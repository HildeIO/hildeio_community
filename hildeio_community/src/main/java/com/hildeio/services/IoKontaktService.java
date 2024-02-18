package com.hildeio.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.hildeio.Log4Hilde;
import com.hildeio.firebase.FbConfiguration;
import com.hildeio.models.IoKontaktModel;
import com.hildeio.models.IoVariableModel;
import com.hildeio.models.Log4HildePushNotificationModel;

/***********************************************************************************************
 * Service zur Zustands-Aktualisierung der Tuer- und Fensterkontakte 
 ***********************************************************************************************/
@Service
public class IoKontaktService {

	/***********************************************************************************************
	 * KONSTANTE fuer Firestore Collections ioKontakte
	 ***********************************************************************************************/	
	final static String COLLECTION = "ioKontakte";
	
	
	/***********************************************************************************************
	 * KONSTANTE fuer Firestore Collections ioKontakte
	 ***********************************************************************************************/	
	final static String COLLECTION_VARIABLEN = "ioVariablen";
	
	/***********************************************************************************************
	 * Dependency Injection auf Log4Hilde
	 ***********************************************************************************************/	
	@Autowired
	Log4Hilde log4Hilde;
	
	/***********************************************************************************************
	 * Dependency Injection auf FCM NotificationService
	 ***********************************************************************************************/	
	@Autowired
	FbNotificationService fcmNotificationService;
	
	/***********************************************************************************************
	 * Neuen Tuer- bzw. Fensterkontakt anlegen.	 
	 * 
	 * @param ioKontakteModel Aktuelle Werte des Kontakts von der HomeMatic CCU. 
	 * @param eventId Aktuelle WorkflowId
	 * @return Aenderungsdatum (String) des Kontakt-Dokuments in ioKontakte. 
	 ***********************************************************************************************/	
	public String createKontakt(IoKontaktModel ioKontakteModel, String eventId) {
		
		try {
			
			if(ioKontakteModel == null) {
				
				String message = "ioKontakteModel ist null";
				
				this.log4Hilde.doInfoLog(
						"00-kein-ioKontakteModel", 
						message, 
						new JSONObject(),
						eventId
						);
				
				return message;
			}
			
			this.log4Hilde.doInfoLog(
					"01-ioKontakteModel", 
					new ObjectMapper().writeValueAsString(ioKontakteModel), 
					new JSONObject(),
					eventId
					);
				
			Firestore dbFirestore = FirestoreClient.getFirestore();
			ApiFuture<WriteResult> collectionsApiFuture = 
						dbFirestore
							.collection(IoKontaktService.COLLECTION)
							.document(ioKontakteModel.getIseId())
							.set(ioKontakteModel);
			
			String executionTime = collectionsApiFuture.get().getUpdateTime().toString();

			this.log4Hilde.doInfoLog(
					"02-Gespeichert", 
					executionTime, 
					new JSONObject(),
					eventId
					);
							
			return executionTime;
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), null);			
			return exception.getMessage();
		}
	}
	
	/***********************************************************************************************
	 * Rueckgabe der Werte des Tuer- bzw. Fensterkontakts.	 
	 * 
	 * @param iseId Channel-ID Kontakts
	 * @param eventId Aktuelle WorkflowId
	 * @return ioKontaktModel Werte des Tuer- bzw. Fensterkontakts 
	 ***********************************************************************************************/	
	public IoKontaktModel getKontakt(String iseId, String eventId) {

		try {

			this.log4Hilde.doInfoLog(
					"01-iseId", 
					iseId, 
					new JSONObject(),
					eventId
					);

			Firestore dbFirestore = FirestoreClient.getFirestore();
			DocumentReference documentReference = 
					dbFirestore
						.collection(IoKontaktService.COLLECTION)
						.document(iseId);
			
			ApiFuture<DocumentSnapshot> future = documentReference.get();
			DocumentSnapshot document = future.get();
			
			IoKontaktModel ioKontakteModel;
			if(document.exists()) {
				
				ioKontakteModel = document.toObject(IoKontaktModel.class);
				
				this.log4Hilde.doInfoLog(
						"02-ioKontakteModel return", 
						new ObjectMapper().writeValueAsString(ioKontakteModel), 
						new JSONObject(),
						eventId
						);

				return ioKontakteModel;
			}
			
			return null;
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return null;
		}		
	}

	/***********************************************************************************************
	 * Tuer- bzw. Fensterkontakt aktualisieren.	 
	 * 
	 * @param ioKontakteModel Werte des Kontakts. 
	 * @param eventId Aktuelle WorkflowId
	 * @return Aenderungsdatum (String) des Kontakt-Dokuments in ioKontakte. 
	 ***********************************************************************************************/	
	public String updateKontakt(IoKontaktModel ioKontakteModel, String eventId) {
		
		try {
			
			if(ioKontakteModel == null) {
				
				String message = "ioKontakteModel ist null";
				
				this.log4Hilde.doInfoLog(
						"00-kein-ioKontakteModel", 
						message, 
						new JSONObject(),
						eventId
						);
				
				return message;
			}
			
			ioKontakteModel = this.updateModel(ioKontakteModel, eventId);

			this.log4Hilde.doInfoLog(
					"01-ioKontakteModel", 
					new ObjectMapper().writeValueAsString(ioKontakteModel), 
					new JSONObject(),
					eventId
					);
				
			Firestore dbFirestore = FirestoreClient.getFirestore();
			ApiFuture<WriteResult> collectionsApiFuture = 
					dbFirestore
						.collection(IoKontaktService.COLLECTION)
						.document(ioKontakteModel.getIseId())
						.set(ioKontakteModel);
			
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
	 * Ermitteln der aktuellen Werte des zu aenderden Dokuments. Bestimmte Felder werden in
	 * dem ioKontakteModel wieder gesetzt.
	 * 
	 * @param ioKontakteModel Werte des KontaktAktors. 
	 * @param eventId Aktuelle WorkflowId
	 * @return aktualisiertes ioKontaktModel. 
	 ***********************************************************************************************/	
	private IoKontaktModel updateModel(IoKontaktModel ioKontakteModel, String eventId) {

		try {

			Firestore dbFirestore = FirestoreClient.getFirestore();
			DocumentReference documentReference = 
					dbFirestore
						.collection(IoKontaktService.COLLECTION)
						.document(ioKontakteModel.getIseId());
			
			ApiFuture<DocumentSnapshot> future = documentReference.get();
			DocumentSnapshot document = future.get();
			
			IoKontaktModel ioKontakteModelSnapshot;
			if(document.exists()) {
				ioKontakteModelSnapshot = document.toObject(IoKontaktModel.class);
				
				this.log4Hilde.doInfoLog(
						"01-ioKontakteModelSnapshot", 
						new ObjectMapper().writeValueAsString(ioKontakteModelSnapshot), 
						new JSONObject(),
						eventId
						);

				ioKontakteModel.setDisplayName(ioKontakteModelSnapshot.getDisplayName());
				
				return ioKontakteModel;
			}
			
			return null;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return null;
		}		
	}		
	
	/***********************************************************************************************
	 * Rueckgabe der Werte des Tuer- bzw. Fensterkontakts.	 
	 * 
	 * @param iseId Channel-ID Kontakts
	 * @param eventId Aktuelle WorkflowId
	 * @return Loeschdatum (String) des Kontakt-Dokuments in ioKontakte. 
	 ***********************************************************************************************/	
	public String deleteKontakt(String iseId, String eventId) {
	
		try {

			this.log4Hilde.doInfoLog(
					"01-iseId", 
					iseId, 
					new JSONObject(),
					eventId
					);
						
			Firestore dbFirestore = FirestoreClient.getFirestore();
			dbFirestore
				.collection(IoKontaktService.COLLECTION)
				.document(iseId)
				.delete();
			
			String message = "iseId gelöscht: " + iseId;
			
			this.log4Hilde.doInfoLog(
					"01-Kontakt gelöscht", 
					message, 
					new JSONObject(),
					eventId
					);				

			return message;
		
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return exception.getMessage();
		}			
	}
	
	
	/***********************************************************************************************
	 * 
	 * Senden einer FCM PushNotification bei Abwesenheit und offenen Tueren/Fenstern.
	 * 
	 * @param eventId Aktuelle WorkflowId
	 *    
	 ***********************************************************************************************/	
	public void checkKontakteOffen(String eventId) {

		try {
		
			DateTime dateTimeCurrent = new DateTime();
			DateTime dateTimeFromFirstore = new DateTime();

			Firestore dbFirestore = FirestoreClient.getFirestore();
						
			DocumentReference dPushFensterReference = 
					dbFirestore
						.collection(IoKontaktService.COLLECTION_VARIABLEN)
						.document("dPushFenster");
			
			ApiFuture<DocumentSnapshot> future = dPushFensterReference.get();
			DocumentSnapshot dPushFenster = future.get();
			
			IoVariableModel ioVariablenModel;
			if(dPushFenster.exists()) {
				
				ioVariablenModel = dPushFenster.toObject(IoVariableModel.class);
				
				this.log4Hilde.doInfoLog(
						"01-ioVariablenModel", 
						new ObjectMapper().writeValueAsString(ioVariablenModel), 
						new JSONObject(),
						eventId
						);
				
				dateTimeFromFirstore = new DateTime(ioVariablenModel.getVariableTimestampValue());

				this.log4Hilde.doInfoLog(
						"02-dateTimeFromFirstore", 
						dateTimeFromFirstore.toString(), 
						new JSONObject(),
						eventId
						);				
			}			
			
			ApiFuture<QuerySnapshot> collectionsApiFuture = 
					dbFirestore
					.collection(IoKontaktService.COLLECTION)
					.whereEqualTo("kontaktValue", "true")
					.get();
	
			List<QueryDocumentSnapshot> documents = collectionsApiFuture.get().getDocuments();
			
			String title = "HildeIO - Fenster geöffnet !";
			String body  = "";
			
			for (DocumentSnapshot document : documents)
				body = body +  document.toObject(IoKontaktModel.class).getKontaktName() + " ";
				
			Duration duration = new Duration(dateTimeFromFirstore, dateTimeCurrent);
			
			this.log4Hilde.doInfoLog(
					"03-body", 
					body, 
					new JSONObject(),
					eventId
					);
			
			if((body != "") && (duration.getStandardMinutes() > 60)) {
				
				Log4HildePushNotificationModel pushNotification = 
						fcmNotificationService.sendPushNotification(
								new FbConfiguration().getKontaktTopic(), 
								title, 
								body, 
								eventId
								);
				
				if(pushNotification.getReturnCode() == 0) {
					
					this.log4Hilde.doInfoLog(
							"04-pushNotification-OK", 
							new ObjectMapper().writeValueAsString(pushNotification),
							new JSONObject(),
							eventId
							);

					IoVariableModel ioVariablenModelSet = new IoVariableModel();
					ioVariablenModelSet.setVariableTimestampValue(dateTimeCurrent.toString());
					dbFirestore
						.collection(IoKontaktService.COLLECTION_VARIABLEN)
						.document("dPushFenster")
						.set(ioVariablenModelSet);
					
					this.log4Hilde.doInfoLog(
							"05-ioVariablenModelSet", 
							new ObjectMapper().writeValueAsString(ioVariablenModelSet),
							new JSONObject(),
							eventId
							);
				} else {
					
					this.log4Hilde.doInfoLog(
							"06-pushNotification-ERR", 
							new ObjectMapper().writeValueAsString(pushNotification),
							new JSONObject(),
							eventId
							);
				}
			}
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}					
	}
}