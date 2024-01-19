package com.hildeio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
	* Peristierung der Logging-Informationen in der Tabelle [luidi_db].[log4hilde].
	* Ueber das Property checkDebugMode (Model: Log4HildeModusRepository) ist in der Tabelle 
	* log4Hilde_modus_model festgelegt ob diese Informationen geloggt werden. 
	* 
	* Existiert in der Tabelle log4hilde_modus_model ein debug-Datensatz mit einem Wert true, wird 
	* das Logging durchgefuehrt. Beim Wert false bzw. ist kein Datensatz vorhanden wird das 
	* Logging ausgefuehrt.   
	* 
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
	* Peristierung der Exception-Informationen in der Tabelle loghHilde der luidi_db.
	* ueber das Property checkDebugMode (Model: Log4HildeModusRepository) ist in der Tabelle 
	* log4Hilde_modus_model festgelegt ob diese Exceptions auch per Push-Nachricht verschickt werden
	* 
	* Existiert in der Tabelle log4hilde_modus_model ein exceptionPush-Datensatz mit einem Wert true, 
	* werden die Exceptions an eine definierte Topic zugestellt. Beim Wert false bzw. ist kein Datensatz    
	* vorhanden wird die Exception nur geloggt und es wird keine Push-Nachricht verschickt.
	* 
	* Tritt waehrend der Ausfuehrung dieser Methode eine Exception (z. B. kein Zugriff auf die DB) auf, 
	* wird versucht die Logging-Informationen in eine Log-Datei zu scheiben.
	* 
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird  
	* 
	**********************************************************************************************/	
	public void doErrorLog(String logpoint, Exception exception, JSONObject json, String eventId, int history) {
		
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

			this.write2File(logpoint, exception, json, eventId, history + 1);
			this.write2File("EX-1", exception_doErrorLog, new JSONObject(), eventId, 5);
		}
	}


	/***********************************************************************************************
	* 
	* Ruft die ueberladene Methode doErrorLog auf. Der history-Paramter wird fix gesetzt.
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
	* Setzen der zentralen Logging-Informationen im log4HildeModel.
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
	* Entscheidung ob DebugModus bzw. Verwsnden einer PushNachricht bei Exception.
	* Feld 'modusName' muss derzeit einen folgenden Werte enthalten:
	* 
	*    debug         = DebugModus (INFO wird geloggt)
	*    exceptionPush = Versenden einer PushNachricht  
	* 
	* @param modusName Name Steuerungsdatensatz
	* @return Value Steuerungsdatensatz
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
	* Fehlermeldung in Log-Datei schreiben, wenn DB nicht verfuegbar ist.
	* 
	* @param logpoint Ort in der Methode an dem die Exception aufgetreten ist
	* @param exception Enhaelt die Exception-Message der Exception 
	* @param json Zusaetzliches Logging fuer weitere JSON-Models
	* @param eventId Alle Loggings eines Workflows erhalten eine eindeutige Id
	* @param history Legt fest wie weit im StackTrace zurueckgegangen wird  
	* 
	**********************************************************************************************/	
	private void write2File(String logpoint, Exception exception, JSONObject json, String eventId, int history) {
		
		try {
			
			String loggingFile = "\\\\192.168.188.73\\hilde\\files\\logging.log";
			
			Log4HildeModel log4HildeModel = this.createExceptionModel(logpoint, exception, json, eventId, history);
			
			FileWriter fileWriter = new FileWriter(loggingFile, true);
		    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		    
		    bufferedWriter.write("Logtime: " + log4HildeModel.getLogtime().toString());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Logpoint: " + log4HildeModel.getLogpoint());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Klasse: " + log4HildeModel.getKlasse());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Methode: " + log4HildeModel.getMethode());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Meldung: " + log4HildeModel.getMeldung());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Exception: " + log4HildeModel.getException());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Stacktrace: " + log4HildeModel.getStackTrace());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Kategorie: " + log4HildeModel.getKategorie().toString());
		    bufferedWriter.newLine();

		    bufferedWriter.write("EventId: " + log4HildeModel.getEventId().toString());
		    bufferedWriter.newLine();

		    bufferedWriter.write("Objects: " + log4HildeModel.getObjekte());	    
		    bufferedWriter.newLine();
		    
		    bufferedWriter.write("------------------------------    ENDE    ------------------------------");
		    bufferedWriter.newLine();
		    
		    bufferedWriter.close();

		} catch (IOException e) {
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
