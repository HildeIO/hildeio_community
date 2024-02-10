package com.hildeio.controller;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hildeio.models.IoHeizkoerperModel;
import com.hildeio.services.IoHeizkoerperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/***********************************************************************************************
 * 
 * REST-API zur Heizkoerper-Steuerung.
 *    
 ***********************************************************************************************/
@RestController
@RequestMapping("/heizkoerper")
@Tag(name = "Heizkoerper")
public class IoHeizkoerperController {

	public IoHeizkoerperService ioHeizkoerperService;

	
	/***********************************************************************************************
	 * 
	 * Konstruktor
	 * 
	 * @param ioHeizkoerperService Logik-Instanz der API. 
	 *    
	 ***********************************************************************************************/
	public IoHeizkoerperController(IoHeizkoerperService ioHeizkoerperService) {
		this.ioHeizkoerperService = ioHeizkoerperService;
	}
	
	
	/***********************************************************************************************
	 * 
	 * DeviceToken fuer ein Topic registrieren.
	 * 
	 * @param ioHeizkoerperModel Steuerungswerte von der HomeMatic CCU.
	 *    
	 ***********************************************************************************************/		
	@PutMapping("/updateHeizkoerper2Firestore")
	@Operation(description = "Speichert Ist-Temperatur, Soll-Temperatur und Betriebsart in Firestore-Collection ioHeizkoerper")
	public String updateHeizkoerper2Firestore(@RequestBody IoHeizkoerperModel ioHeizkoerperModel) throws InterruptedException, ExecutionException {
		return ioHeizkoerperService.updateHeizkoerper2Firestore(ioHeizkoerperModel, UUID.randomUUID().toString());
	}
	
	
	/***********************************************************************************************
	 * 
	 * Zyklischer Aufruf der Wochenplan-Logik durch die HomeMatic CCU.
	 * 
	 ***********************************************************************************************/		
	@PutMapping("/checkWochenplan")
	@Operation(description = "Für zyklischen Aufruf der Wochenplan-Logik durch die HomeMatic")
	public String checkWochenplan() throws InterruptedException, ExecutionException {
		return ioHeizkoerperService.checkWochenplan(UUID.randomUUID().toString());
	}		
}
