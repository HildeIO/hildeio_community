package com.hildeio.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hildeio.models.IoMeldungModel;
import com.hildeio.services.IoMeldungService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/***********************************************************************************************
 * REST-API zum Anzeigen von Servicemeldungn der HomeMatic CCU.
 ***********************************************************************************************/
@RestController
@RequestMapping("/meldung")
@Tag(name = "Meldung")
public class IoMeldungController {

	/***********************************************************************************************
	 * Instanzvariable für Service
	 ***********************************************************************************************/
	public IoMeldungService ioMeldungService;

	/***********************************************************************************************
	 * Konstruktor
	 * 
	 * @param ioMeldungService Logik-Instanz der API. 
	 ***********************************************************************************************/	
	public IoMeldungController(IoMeldungService ioMeldungService) {
		this.ioMeldungService = ioMeldungService;
	}

	/***********************************************************************************************
	 * Neue Servicemeldung aus der HomeMatic in Firestore-Collection ioMeldungen anlegen.
	 * 
	 * @param ioMeldungModels Name, Nachricht, Kategorie und Timestamp.
	 * @return Erfolgsmeldung / Fehlermeldung
	 * @throws InterruptedException Erforderliche Exception.
	 * @throws ExecutionException Erforderliche Exception.
	 ***********************************************************************************************/			
	@PostMapping("/create")
	@Operation(description = "Neue Servicemeldung aus der HomeMatic in Firestore-Collection ioMeldungen anlegen")
	public String create(@RequestBody List<IoMeldungModel> ioMeldungModels) throws InterruptedException, ExecutionException {
		return ioMeldungService.create(ioMeldungModels, UUID.randomUUID().toString());
	}
}
