package com.hildeio.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.hildeio.Log4Hilde;
import com.hildeio.homematic.HmConfiguration;
import com.hildeio.homematic.HmEventManager;
import com.hildeio.models.IoHeizkoerperModel;
import com.hildeio.models.IoWochenplanModel;

@Service
public class IoHeizkoerperService {

	final static String COLLECTION_HEIZKOERPER = "ioHeizkoerper";
	final static String COLLECTION_WOCHENPLAN = "ioWochenplan";
	
	@Autowired
	Log4Hilde log4Hilde;
	
//ToDo: in HildeApp muss setChangedBy("MOBILE") immer gesetzt werden!!!
	
	/* *********************************************************************************************
	 *
	 * UPDATE: Homematic -> Firestore
	 * 
	 * ********************************************************************************************/		
	public String updateHeizkoerper2Firestore(IoHeizkoerperModel ioHeizkoerperModel, String eventId) {
		
		try {
			
			if(ioHeizkoerperModel == null) {
				
				String message = "ioHeizkoerperModel ist null";
				
				this.log4Hilde.doInfoLog(
						"00-kein-ioHeizkoerperModel", 
						message, 
						new JSONObject(),
						eventId
						);
				
				return message;
			}
			
			
			ioHeizkoerperModel = this.updateModel(ioHeizkoerperModel, eventId);
			
			
			this.log4Hilde.doInfoLog(
					"01-ioHeizkoerperModel", 
					new ObjectMapper().writeValueAsString(ioHeizkoerperModel), 
					new JSONObject(),
					eventId
					);
			
			Firestore dbFirestore = FirestoreClient.getFirestore();
			
			ApiFuture<WriteResult> collectionsApiFuture = 
					dbFirestore
						.collection(IoHeizkoerperService.COLLECTION_HEIZKOERPER)
						.document(ioHeizkoerperModel.getIseId())
						.set(ioHeizkoerperModel);
			
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
	
	
	/* *********************************************************************************************
	 *
	 * updateModel
	 * 
	 * ********************************************************************************************/		
	private IoHeizkoerperModel updateModel(IoHeizkoerperModel ioHeizkoerperModel, String eventId) {

		try {

			Firestore dbFirestore = FirestoreClient.getFirestore();
			DocumentReference documentReference = 
					dbFirestore
						.collection(IoHeizkoerperService.COLLECTION_HEIZKOERPER)
						.document(ioHeizkoerperModel.getIseId());
			
			ApiFuture<DocumentSnapshot> future = documentReference.get();
			DocumentSnapshot document = future.get();
			
			IoHeizkoerperModel ioHeizkoerperModelSnapshot;
			if(document.exists()) {
				ioHeizkoerperModelSnapshot = document.toObject(IoHeizkoerperModel.class);
				
				this.log4Hilde.doInfoLog(
						"01-ioHeizkoerperModelSnapshot", 
						new ObjectMapper().writeValueAsString(ioHeizkoerperModelSnapshot), 
						new JSONObject(),
						eventId
						);

				if(ioHeizkoerperModelSnapshot.getTemperaturSoll().equals("4.5"))
					ioHeizkoerperModel.setBetriebsart("wochenplan");
				
				else if(ioHeizkoerperModelSnapshot.getTemperaturSoll().equals(ioHeizkoerperModel.getTemperaturSoll()))
					ioHeizkoerperModel.setBetriebsart(ioHeizkoerperModelSnapshot.getBetriebsart());
				
				else
					ioHeizkoerperModel.setBetriebsart("manuell");
				

				ioHeizkoerperModel.setChangedBy("HM");
				ioHeizkoerperModel.setDisplayName(ioHeizkoerperModelSnapshot.getDisplayName());

				return ioHeizkoerperModel;
			}
			
			return null;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return null;
		}		
	}
	
	/* *********************************************************************************************
	 *
	 * UPDATE: Firestore -> Homematic
	 * 
	 * ********************************************************************************************/		
	public void update2Homematic(QueryDocumentSnapshot document, Log4Hilde log4Hilde, String eventId) {
		
		try {
			
			if(document == null) {
				
				String message = "document ist null";
				
				log4Hilde.doInfoLog(
						"00-kein-ioHeizkoerperModel", 
						message, 
						new JSONObject(),
						null
						);
				
				return;
			}
			
			String temperaturSoll;
			
			IoHeizkoerperModel ioHeizkoerperModel = document.toObject(IoHeizkoerperModel.class);

			log4Hilde.doInfoLog(
					"01-ioHeizkoerperModel", 
					new ObjectMapper().writeValueAsString(ioHeizkoerperModel), 
					new JSONObject(),
					eventId
					);

			if(ioHeizkoerperModel.getChangedBy().equals("HM"))
				return;
				
			if(ioHeizkoerperModel.getTemperaturSoll().equals("4.5")) {
				
				temperaturSoll = this.getTemperaturSollWochenplan(
						ioHeizkoerperModel.getIseId(), 
						log4Hilde, 
						eventId
						);
				
				log4Hilde.doInfoLog(
						"02-Solltemperatur Wochenplan gesetzt", 
						"temperaturSoll = " + temperaturSoll, 
						new JSONObject(),
						eventId
						);

				ioHeizkoerperModel.setTemperaturSoll(temperaturSoll);
			}

			HmEventManager hmControllerInterface = new HmEventManager(log4Hilde);
			hmControllerInterface.save(
					new HmConfiguration().getRfcInterfaceSetValue(), 
					ioHeizkoerperModel,
					eventId
					);			
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
		}		
	}	

	
	/* *********************************************************************************************
	 *
	 * getTemperaturSollWochenplan
	 * 
	 * ********************************************************************************************/		
	private String getTemperaturSollWochenplan(String iseId, Log4Hilde log4Hilde, String eventId) {
		
		try {
			
			String temperaturSoll = "null";
			
			LocalDate heute = LocalDate.now(ZoneId.of("Europe/Berlin"));
	        String wochentag = heute
	        		.getDayOfWeek()
	        		.getDisplayName(TextStyle.FULL, Locale.GERMAN)
	        		;
	        
			Firestore dbFirestore = FirestoreClient.getFirestore();
			Query query = dbFirestore
					.collection(COLLECTION_WOCHENPLAN)
					.whereEqualTo("wochentag", wochentag)
					.whereEqualTo("iseId", iseId)
					;
			
			ApiFuture<QuerySnapshot> querySnapshot = query.get();
			
			for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {

				IoWochenplanModel ioWochenplanModel;
				if(document.exists()) {
					ioWochenplanModel = document.toObject(IoWochenplanModel.class);

					log4Hilde.doInfoLog(
							"01-ioWochenplanModel", 
							new ObjectMapper().writeValueAsString(ioWochenplanModel), 
							new JSONObject(),
							eventId
							);
					
					SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
					Date von = parser.parse(ioWochenplanModel.getVon());
					Date bis = parser.parse(ioWochenplanModel.getBis());				
					Date now = parser.parse(
							new SimpleDateFormat("HH:mm")
								.format(Calendar.getInstance().getTime())
							);
					
					if (now.after(von) && now.before(bis)) 		
						temperaturSoll = ioWochenplanModel.getTemperaturSoll();				    				
				} 			
			}			
			
			log4Hilde.doInfoLog(
					"02-Solltemerparur Wochenplan", 
					"temperaturSoll = " + temperaturSoll, 
					new JSONObject(),
					eventId
					);
			
			return temperaturSoll;
			
		} catch(Exception exception) {
			
			log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);
			return exception.getMessage();
		}						
	}
	
	
	/* *********************************************************************************************
	 *
	 * checkWochenplan
	 * 
	 * ********************************************************************************************/		
	public String checkWochenplan(String eventId) {
		
		try {
			
			Firestore dbFirestore = FirestoreClient.getFirestore();
			Query query = dbFirestore
					.collection(COLLECTION_HEIZKOERPER)
					.whereEqualTo("betriebsart", "wochenplan")
					;

			ApiFuture<QuerySnapshot> querySnapshot = query.get();
			
			for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
				
				IoHeizkoerperModel ioHeizkoerperModel;
				if(document.exists()) {
					ioHeizkoerperModel = document.toObject(IoHeizkoerperModel.class);

					log4Hilde.doInfoLog(
							"01-ioHeizkoerperModel", 
							new ObjectMapper().writeValueAsString(ioHeizkoerperModel), 
							new JSONObject(),
							eventId
							);
					
					ioHeizkoerperModel.setTemperaturSoll("4.5");
					ioHeizkoerperModel.setChangedBy("WP");
					
					ApiFuture<WriteResult> collectionsApiFuture = 
							dbFirestore
								.collection(IoHeizkoerperService.COLLECTION_HEIZKOERPER)
								.document(ioHeizkoerperModel.getIseId())
								.set(ioHeizkoerperModel);
					
					String executionTime = collectionsApiFuture.get().getUpdateTime().toString();
					
					log4Hilde.doInfoLog(
							"02-ioHeizkoerperModel gespeichert: " + executionTime, 
							new ObjectMapper().writeValueAsString(ioHeizkoerperModel), 
							new JSONObject(),
							eventId
							);
				}								
			}
			
			return "checkWochenplan erledigt";
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);			
			return exception.getMessage();
		}		

	}
	
}
