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

@RestController
@RequestMapping("/kontakte")
@Tag(name = "Kontakte")
public class IoKontaktController {

	public IoKontaktService ioKontakteService;
 
	 
	/* *********************************************************************************************
	 *
	 * IoKontakteController
	 * 
	 * ********************************************************************************************/		
	public IoKontaktController(IoKontaktService ioKontakteService) {
		this.ioKontakteService = ioKontakteService;
	} 
	

	/* *********************************************************************************************
	 *
	 * GET
	 * 
	 * ********************************************************************************************/		
	@GetMapping("/get/{iseId}")
	@Operation(description = "Aktuellen Status von Kontakt {iseId} aus Firestore-Collection ioKontakte")
	public IoKontaktModel getKontakt(
			@Parameter(description = "Channel-ID des Tür- bzw. Fensterkontakts", example = "58429", required = true) 
			@PathVariable 
			String iseId
			) throws InterruptedException, ExecutionException {
		return ioKontakteService.getKontakt(iseId, UUID.randomUUID().toString());
	}

	
	/* *********************************************************************************************
	 *
	 * CREATE
	 * 
	 *
	   !HomeMatic-Beispiel - CREATE:
	 
	 	string ise_id = "21121977";
		string seriennummer = "123456";
		string bezeichnung = "bezeichnung_test";
		
		string kontakt_controller= "http://192.168.188.87:8080/channels";
		
		string curl = "curl -X POST -H \"Content-Type: application/json\" "#kontakt_controller#" -d '{\"ise_id\":"#ise_id #",\"seriennummer\":\""#seriennummer#"\",\"bezeichnung\":\""#bezeichnung#"\"}'";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
		WriteLine (curl);
	 *
	 * ********************************************************************************************/	
	@PostMapping("/create")
	@Operation(description = "Neuen Kontakt mit Status in Firestore-Collection ioKontakte anlegen")
	public String createKontakt(@RequestBody IoKontaktModel ioKontakteModel) throws InterruptedException, ExecutionException {
		return ioKontakteService.createKontakt(ioKontakteModel, UUID.randomUUID().toString());
	}
	
	
	/* *********************************************************************************************
	 *
	 * UPDATE
	 * 
	 *
	 	!HomeMatic - PUT:
	 	!Programm: Fensterkontakte (Firebase)
	 	
		object aktuellesDevice = dom.GetObject("$src$").Device();
		
		string iseId = dom.GetObject(aktuellesDevice).ID();
		string kontaktName = dom.GetObject(aktuellesDevice).Name();
		string kontaktValue = dom.GetObject(aktuellesDevice).State().ToString();
		
		string kontakt_controller= dom.GetObject(ID_SYSTEM_VARIABLES).Get("HildeIO").State() + "kontakte/update";
		
		string curl = "curl -X PUT -H \"Content-Type: application/json\" "#kontakt_controller#" -d '{\"iseId\":"#iseId #",\"kontaktName\":\""#kontaktName#"\",\"kontaktValue\":\""#kontaktValue#"\"}'";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
	 *
	 * ********************************************************************************************/	
	@PutMapping("/update")
	@Operation(description = "Status von Kontakt in Firestore-Collection ioKontake aktualisieren")
	public String updateKontakt(@RequestBody IoKontaktModel ioKontakteModel) throws InterruptedException, ExecutionException {
		return ioKontakteService.updateKontakt(ioKontakteModel, UUID.randomUUID().toString());
	}
	
	
	/* *********************************************************************************************
	 *
	 * DELETE
	 * 
	 *
 		!HomeMatic-Beispiel - DELETE:
 		
		string kontakt_controller= "http://192.168.188.87:8080/channels/3001";
		
		string curl = "curl -X DELETE -H \"Content-Type: application/json\" "#kontakt_controller#"";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
		WriteLine (curl);
	 *
	 * ********************************************************************************************/	
	@DeleteMapping("/delete/{iseId}")
	@Operation(description = "Kontakt {iseId} aus Firestore-Collection ioKontakte löschen")
	public String deleteKontakt(
			@Parameter(description = "Channel-ID des Tür- bzw. Fensterkontakts", example = "58429", required = true) @PathVariable String iseId
			) throws InterruptedException, ExecutionException {
		return ioKontakteService.deleteKontakt(iseId, UUID.randomUUID().toString());
	}
	

	/* *********************************************************************************************
	 *
	 * checkKontakteOffen
	 * 
	 	!HomeMatic - checkKontakteOffen:
	 	
		string kontakt_controller= string kontakt_controller= dom.GetObject(ID_SYSTEM_VARIABLES).Get("HildeIO").State() + "kontakte/checkKontakteOffen";
		
		string curl = "curl -X GET -H \"Content-Type: application/json\" "#kontakt_controller#"";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
		WriteLine (curl);
	 *
	 * ********************************************************************************************/		
	@GetMapping("/checkKontakteOffen")
	@Operation(description = "Prüfen der aktuellen Stati und ggfs. Versenden einer Push-Nachricht")
	public void checkKontakteOffen() throws InterruptedException, ExecutionException {
		ioKontakteService.checkKontakteOffen(UUID.randomUUID().toString());
	}	
}
