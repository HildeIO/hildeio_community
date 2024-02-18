package com.hildeio.controller;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hildeio.models.IoSchaltaktorModel;
import com.hildeio.services.IoSchaltaktorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/***********************************************************************************************
 * REST-API zur Aenderung der Stati von Schaltaktoren.
 ***********************************************************************************************/
@RestController
@RequestMapping("/schaltaktor")
@Tag(name = "Schaltaktoren")
public class IoSchaltaktorController {

	/***********************************************************************************************
	 * Instanzvariable für Service
	 ***********************************************************************************************/
	public IoSchaltaktorService ioSchaltaktorService;

	/***********************************************************************************************
	 * Konstruktor
	 * 
	 * @param ioSchaltaktorService Logik-Instanz der API.
	 ***********************************************************************************************/	
	public IoSchaltaktorController(IoSchaltaktorService ioSchaltaktorService) {
		this.ioSchaltaktorService = ioSchaltaktorService;
	}

	/***********************************************************************************************
	 * Speichert den Schaltaktor-Zustand (ein/aus) in Firestore-Collection ioSchaltaktoren.
	 * 
	 * @param ioSchaltaktorModel Steuerungswerte von der HomeMatic CCU.
	 * @return Erfolgsmeldung / Fehlermeldung
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 ***********************************************************************************************/		
	@PutMapping("/updateSchaltaktor2Firestore")
	@Operation(description = "Speichert den Schaltaktor-Zustand (ein/aus) in Firestore-Collection ioSchaltaktoren")
	public String updateSchaltaktor2Firestore(@RequestBody IoSchaltaktorModel ioSchaltaktorModel) throws InterruptedException, ExecutionException {
		return ioSchaltaktorService.updateSchaltaktor2Firestore(ioSchaltaktorModel, UUID.randomUUID().toString());
	}
}
