package com.hildeio.controller;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hildeio.models.IoKontaktModel;
import com.hildeio.services.IoKontaktService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/***********************************************************************************************
 * 
 * REST-API zur Aenderung der Stati von Tuer- und Fensterkontakten.
 *    
 ***********************************************************************************************/
@RestController
@RequestMapping("/kontakte")
@Tag(name = "Kontakte")
public class IoKontaktController {

	/***********************************************************************************************
	 * 
	 * Instanzvariable für Service
	 *    
	 ***********************************************************************************************/
	public IoKontaktService ioKontakteService;
 
	 
	/***********************************************************************************************
	 * 
	 * Konstruktor
	 * 
	 * @param ioKontakteService Logik-Instanz der API.
	 *    
	 ***********************************************************************************************/	
	public IoKontaktController(IoKontaktService ioKontakteService) {
		this.ioKontakteService = ioKontakteService;
	} 
	

	/***********************************************************************************************
	 * 
	 * Aktueller Status von Kontakt {iseId} aus Firestore-Collection ioKontakte.
	 * 
	 * @param iseId Channel-ID des Tuer- bzw. Fensterkontakts.
	 * @return Werte des aktullen Tuer- bzw. Fensterkontakts.
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 *    
	 ***********************************************************************************************/		
	@GetMapping("/get/{iseId}")
	@Operation(description = "Aktueller Status von Kontakt {iseId} aus Firestore-Collection ioKontakte")
	public IoKontaktModel getKontakt(
			@Parameter(description = "Channel-ID des Tür- bzw. Fensterkontakts", example = "58429", required = true) 
			@PathVariable 
			String iseId
			) throws InterruptedException, ExecutionException {
		return ioKontakteService.getKontakt(iseId, UUID.randomUUID().toString());
	}

	
	/***********************************************************************************************
	 * 
	 * Neuen Kontakt mit Status in Firestore-Collection ioKontakte anlegen.
	 * 
	 * @param ioKontakteModel Name, Channel-ID und Status des Kontakts.
	 * @return Erfolgsmeldung / Fehlermeldung
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 *    
	 ***********************************************************************************************/			
	@PostMapping("/create")
	@Operation(description = "Neuen Kontakt mit Status in Firestore-Collection ioKontakte anlegen")
	public String createKontakt(@RequestBody IoKontaktModel ioKontakteModel) throws InterruptedException, ExecutionException {
		return ioKontakteService.createKontakt(ioKontakteModel, UUID.randomUUID().toString());
	}
	
	
	/***********************************************************************************************
	 * 
	 * Status von Kontakt in Firestore-Collection ioKontake aktualisieren.
	 * 
	 * @param ioKontakteModel Name, Channel-ID und Status des Kontakts.
	 * @return Erfolgsmeldung / Fehlermeldung
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 *    
	 ***********************************************************************************************/			
	@PutMapping("/update")
	@Operation(description = "Status von Kontakt in Firestore-Collection ioKontake aktualisieren")
	public String updateKontakt(@RequestBody IoKontaktModel ioKontakteModel) throws InterruptedException, ExecutionException {
		return ioKontakteService.updateKontakt(ioKontakteModel, UUID.randomUUID().toString());
	}
	
	
	/***********************************************************************************************
	 * 
	 * Kontakt {iseId} aus Firestore-Collection ioKontakte loeschen.
	 * 
	 * @param iseId Channel-ID des Tuer- bzw. Fensterkontakts.
	 * @return Erfolgsmeldung / Fehlermeldung
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 *    
	 ***********************************************************************************************/				
	@DeleteMapping("/delete/{iseId}")
	@Operation(description = "Kontakt {iseId} aus Firestore-Collection ioKontakte löschen")
	public String deleteKontakt(
			@Parameter(description = "Channel-ID des Tür- bzw. Fensterkontakts", example = "58429", required = true) @PathVariable String iseId
			) throws InterruptedException, ExecutionException {
		return ioKontakteService.deleteKontakt(iseId, UUID.randomUUID().toString());
	}
	

	/***********************************************************************************************
	 * 
	 * Pruefen der aktuellen Stati und ggfs. Versenden einer Push-Nachricht.
	 * 
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 *    
	 ***********************************************************************************************/					
	@GetMapping("/checkKontakteOffen")
	@Operation(description = "Prüfen der aktuellen Stati und ggfs. Versenden einer Push-Nachricht")
	public void checkKontakteOffen() throws InterruptedException, ExecutionException {
		ioKontakteService.checkKontakteOffen(UUID.randomUUID().toString());
	}	
}
