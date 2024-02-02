package com.hildeio;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hildeio.firebase.FbConfiguration;
import com.hildeio.luididb.Log4HildeModusRepository;
import com.hildeio.luididb.Log4HildeRepository;
import com.hildeio.models.Log4HildeModel;
import com.hildeio.models.Log4HildeModel.Kategorie;
import com.hildeio.services.FbNotificationService;
import com.hildeio.models.Log4HildeModusModel;
import com.hildeio.models.Log4HildePushNotificationModel;


/**
 * <P>Peristierung der Logging-Informationen in einer relationale Datenbank. HildeIO verwendet den Datenbankserver MariaDB. 
 * Die Logging-Datensaetze werden in der Tabelle [log4hilde] der Datenbank [luidi_db] gespeichert.</p>   
 * Hinweis:<br>
 * Alternativ zur MariaDB kann auch ein anderes relationales Datenbanksystem verwendet werden, wie PostgreSQL oder MSSQL.   
 */
@Component
public class Log4Hilde {

	@Autowired
	Log4HildeRepository log4HildeRepository;
	
	@Autowired
	Log4HildeModusRepository log4HildeModusRepository;
		
	/***********************************************************************************************
	* 
	* <p style="color:green;font-weight:bold;size:28px">INFO-LEVEL</p>
	* 
	* Persistieren der aktuellen Objekte aus den Methoden in der Tabelle [luidi_db].[log4hilde].
	* 	* 
	* @param logpoint Logging-Punkt in der Methode
	* @param meldung Enhaelt die Werte von Variablen und/oder Model-Properties 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* 
	**********************************************************************************************/	
	public void doInfoLog(String logpoint, String meldung, JSONObject json, String eventId) {
		
		try {
			
			Log4HildeModel log4HildeModel = new Log4HildeModel();		
			log4HildeModel.setLogpoint(logpoint);
			log4HildeModel.setMeldung(meldung);
			log4HildeModel.setKategorie(Kategorie.INFO);
			log4HildeModel.setObjekte(json.toString());
			log4HildeModel.setEventId(eventId);
			
			log4HildeModel = this.doLogging(log4HildeModel, 3);
			
			if(this.checkDebugMode("debug"))
				log4HildeRepository.save(log4HildeModel);		

		} catch(Exception exception) {
			
			this.doErrorLog("EX-1", exception, new JSONObject(), eventId);
		}
	}	
	

	/***********************************************************************************************
	* 
	* Persistieren der aktuellen Exception-Objekte in der Tabelle loghHilde der [luidi_db].[log4hilde].
	*  
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird  
	* 
	**********************************************************************************************/	
	private void doErrorLog(String logpoint, Exception exception, JSONObject json, String eventId, int history) {
		
		try {
						
			Log4HildeModel log4HildeModel = this.createExceptionModel(logpoint, exception, json, eventId, history);
			log4HildeRepository.save(log4HildeModel);
			
			if(this.checkDebugMode("exceptionPush")) {
				
				FbNotificationService fcmNotificationService = new FbNotificationService();				
				Log4HildePushNotificationModel pushExceptionNotification = 
						fcmNotificationService.sendPushNotification(
								new FbConfiguration().getLoggingTopic(), 
								new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(log4HildeModel.getLogtime())  + ": " + this.getSimpleName(log4HildeModel.getException()), 
								log4HildeModel.getStackTrace().substring(0, 250) + "...",
								eventId
								);

				if(pushExceptionNotification.getReturnCode() == 0) 					
					this.doInfoLog("rc0-pushErrorNotification", pushExceptionNotification.getMessageId(), new JSONObject(), eventId);				
				else
					log4HildeRepository.save(
							this.createExceptionModel(
									"rc1-pushErrorNotification", 
									pushExceptionNotification.getException(), 
									pushExceptionNotification.getObjekte(), 
									eventId,
									5
									)
							);
			}
			
		} catch(Exception exception_doErrorLog) {

			this.printError(logpoint, exception, json, eventId, history + 1);
			this.printError("EX-1", exception_doErrorLog, new JSONObject(), eventId, 5);
		}
	}


	/***********************************************************************************************
	* 
	* <p style="color:red;font-weight:bold;size:28px">ERROR-LEVEL</p>
	* 
	* Öffentliche doErrorLog()-Methode im Falle einer Exception. 
	*  
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* 
	**********************************************************************************************/	
	public void doErrorLog(String logpoint, Exception exception, JSONObject json, String eventId) {
		
		this.doErrorLog(logpoint, exception, json, eventId, 5);
	}
	
	
	/***********************************************************************************************
	* 
	* Zusammenstellen der Logging-Informationen im log4HildeModel.
	*  
	* @param Zu befuellendes log4HildeModel   
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird
	* @return Vorgefuelltes log4HildeModel   
	* 
	**********************************************************************************************/	
	private Log4HildeModel doLogging(Log4HildeModel log4HildeModel, int history){
		
		log4HildeModel.setLogtime(new Timestamp(System.currentTimeMillis()));
		log4HildeModel.setKlasse(this.getSimpleName(Thread.currentThread().getStackTrace()[history].getClassName()));
		log4HildeModel.setMethode(Thread.currentThread().getStackTrace()[history].getMethodName());
		
		return log4HildeModel;
	}

	
	/***********************************************************************************************
	* 
	* <p>Enthaelt der Parameter modusName den Wert 'debug', wird geprueft, ob INFO-LEVEL aktiv ist.
	* Standardmaessig ist das INFO-LEVEL aktiv. Um das INFO-LEVEL zu deaktivieren muss in 
	* der Tabelle [luidi_db].[log4hilde_modus_model] folgender Datensatz existieren:</p>  
	* <p>
	* <table>
	*  <tr>
	*   <td style="background-color:#e9ecee;">modusName</td>
	*   <td style="background-color:#e9ecee;">modus_value</td>
	*  </tr>
	*  <tr>
	*   <td style="border: 1px solid #e9ecee;font-width:bold;">debug</td>
	*   <td style="border: 1px solid #e9ecee;font-width:bold;">0</td>
	*  </tr>
	* </table>
	* </p>
	* 
	* <p>Enthaelt der Parameter modusName den Wert 'exceptionPush' wird bei einer Exception geprueft,
	* ob PushNotification verschicken wird. Standardmaessig ist bei einer Exception die PushNotification aktiv.
	* Um das das Versenden einer PushNotification zu deaktivieren muss in der Tabelle 
	* [luidi_db].[log4hilde_modus_model] folgender Datensatz existieren:</p>
	* <p>
	* <table>
	*  <tr>
	*   <td style="background-color:#e9ecee;">modusName</td>
	*   <td style="background-color:#e9ecee;">modus_value</td>
	*  </tr>
	*  <tr>
	*   <td style="border: 1px solid #e9ecee;font-width:bold;">exceptionPush</td>
	*   <td style="border: 1px solid #e9ecee;font-width:bold;">0</td>
	*  </tr>
	* </table>
	* </p>
	* 
	* 
	* @param modusName debug | exceptionPush
	* @return true | false
	* 
	**********************************************************************************************/	
	private Boolean checkDebugMode(String modusName) {
		
		Optional<Log4HildeModusModel> modus = log4HildeModusRepository.findByModusName(modusName);
		if(!modus.isPresent())
			return true;
		
		Log4HildeModusModel modusInstance = modus.get();
		return modusInstance.getModusValue();
	}

	
	/***********************************************************************************************
	* 
	* Formattierung des Methoden-Namens. Package-Namen werden entfernt.
	* 
	* @param Vollstaendiger Methoden-Name
	* @return Gekuerzter Methoden-Name
	* 
	**********************************************************************************************/	
	private String getSimpleName(String klasse) {
		
		return klasse.substring((klasse.lastIndexOf('.') + 1), klasse.length());
	}
	
	
	/***********************************************************************************************
	* 
	* Fehlermeldung ausgeben.
	* 
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird  
	* 
	**********************************************************************************************/	
	private void printError(String logpoint, Exception exception, JSONObject json, String eventId, int history) {
		
		try {
						
			Log4HildeModel log4HildeModel = this.createExceptionModel(logpoint, exception, json, eventId, history);
			
		    System.out.println("Logtime: " + log4HildeModel.getLogtime().toString());
		    System.out.println("Logpoint: " + log4HildeModel.getLogpoint());
		    System.out.println("Klasse: " + log4HildeModel.getKlasse());
		    System.out.println("Methode: " + log4HildeModel.getMethode());
		    System.out.println("Meldung: " + log4HildeModel.getMeldung());
		    System.out.println("Exception: " + log4HildeModel.getException());
		    System.out.println("Stacktrace: " + log4HildeModel.getStackTrace());
		    System.out.println("Kategorie: " + log4HildeModel.getKategorie().toString());
		    System.out.println("EventId: " + log4HildeModel.getEventId().toString());
		    System.out.println("Objects: " + log4HildeModel.getObjekte());	    

		} catch (Exception e) {
			e.printStackTrace();
		}	    
	}
	
	
	/***********************************************************************************************
	* 
	* Zusamenstellen der Fehlerinforamtionen.
	* 
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird  
	* @return Befuellen log4HildeModel   
	* 
	**********************************************************************************************/	
	private Log4HildeModel createExceptionModel(String logpoint, Exception exception, JSONObject json, String eventId, int history) {
		
		StringWriter stringWriter = new StringWriter();
		
		String stackTrace = stringWriter.toString(); 
		
		Log4HildeModel log4HildeModel = new Log4HildeModel();		
		log4HildeModel.setLogpoint(logpoint);
		log4HildeModel.setMeldung(exception.getMessage());
		log4HildeModel.setException(exception.getClass().getCanonicalName());
		log4HildeModel.setStackTrace(stackTrace);
		log4HildeModel.setKategorie(Kategorie.ERROR);
		log4HildeModel.setObjekte(json.toString());
		log4HildeModel.setEventId(eventId);
		
		log4HildeModel = this.doLogging(log4HildeModel, history);
		
		return log4HildeModel;		
	}
	
}
