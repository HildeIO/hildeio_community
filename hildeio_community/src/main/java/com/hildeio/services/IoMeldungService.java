package com.hildeio.services;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.hildeio.Log4Hilde;
import com.hildeio.models.IoMeldungModel;

/***********************************************************************************************
 * Service Anzeige der Servicemeldungen aus der HomeMatic CCU 
 ***********************************************************************************************/
@Service
public class IoMeldungService {

	/***********************************************************************************************
	 * KONSTANTE fuer Firestore Collections ioMeldungen
	 ***********************************************************************************************/	
	final static String COLLECTION = "ioMeldungen";
	
	/***********************************************************************************************
	 * Dependency Injection auf Log4Hilde
	 ***********************************************************************************************/	
	@Autowired
	Log4Hilde log4Hilde;
	
	/***********************************************************************************************
	 * Neue Servicemeldungen aus der HomeMatic CCU in der Firestore Collection ioMeldungen anlegen.	
	 * Vor dem Anlegen wird die Collection ioMeldungen geleert. 
	 * 
	 * @param ioMeldungModels n-Servicemeldungen vom Typ ioMeldungModels. 
	 * @param eventId Aktuelle WorkflowId
	 * @return Erfolgsmeldung / Fehlermeldung 
	 ***********************************************************************************************/	
	public String create(List<IoMeldungModel> ioMeldungModels, String eventId) {
		
		try {
		
			Firestore dbFirestore = FirestoreClient.getFirestore();
			
			this.deleteAllDocumentsOfService(
					dbFirestore
					.collection(IoMeldungService.COLLECTION), 10, eventId
					.toString()
					);
						
			if(ioMeldungModels.size() == 0) {
				
				String message = "ioMeldungModels enthält keine Meldungen. size: " 
						+ String.valueOf(ioMeldungModels.size());
				
				this.log4Hilde.doInfoLog(
						"00-keine-Meldungen", 
						message, 
						new JSONObject(),
						eventId
						);

				return message;
			}
			
			
			for (IoMeldungModel ioMeldungModel : ioMeldungModels) {
				
				this.log4Hilde.doInfoLog(
						"01-ioMeldungModel", 
						new ObjectMapper().writeValueAsString(ioMeldungModel), 
						new JSONObject(),
						eventId
						);
						
				ApiFuture<WriteResult> collectionsApiFuture = 
							dbFirestore
								.collection(IoMeldungService.COLLECTION)
								.document(ioMeldungModel.getTimestamp())
								.set(ioMeldungModel);
				
				String executionTime = collectionsApiFuture.get().getUpdateTime().toString();
				
				this.log4Hilde.doInfoLog(
						"02-Gespeichert", 
						executionTime, 
						new JSONObject(),
						eventId
						);					
			}

			return "Neue Meldungen angelegt";
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return exception.getMessage();
		}
	}
	
	/***********************************************************************************************
	 * Löschen aller Dokumente der Collection ioMeldungen. 
	 * 
	 * @param collection Name der Collection.
	 * @param batchSize Defaultwert 
	 * @param eventId Aktuelle WorkflowId
	 ***********************************************************************************************/	
	private void deleteAllDocumentsOfService(CollectionReference collection, int batchSize, String eventId) {
		
		try {
		  
			ApiFuture<QuerySnapshot> future = collection
					.whereEqualTo("kategorie", "SERVICE")
			    	.limit(batchSize)
			    	.get();
			    
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				
				document.getReference().delete();
				
				this.log4Hilde.doInfoLog(
						"01-document gelöscht", 
						new ObjectMapper().writeValueAsString(document.toObject(IoMeldungModel.class)), 
						new JSONObject(),
						eventId
						);				
			}

		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}
	}	
}