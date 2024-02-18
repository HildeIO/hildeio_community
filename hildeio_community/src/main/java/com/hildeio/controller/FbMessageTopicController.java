package com.hildeio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hildeio.models.FbMessageTopicModel;
import com.hildeio.services.FbMessageTopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
 
/***********************************************************************************************
 * 
 * REST-API zur Administration der FCM Topics. 
 *    
 ***********************************************************************************************/
@RestController
@RequestMapping("/fcmTopic")
@Tag(name = "Firebase Messaging Topic")
public class FbMessageTopicController {

	/***********************************************************************************************
	 * 
	 * Instanzvariable für Service
	 *    
	 ***********************************************************************************************/
	public FbMessageTopicService fbMessageTopicService;

	
	/***********************************************************************************************
	 * 
	 * Konstruktor
	 * 
	 * @param fbMessageTopicService Logik-Instanz der API.
	 *    
	 ***********************************************************************************************/
	public FbMessageTopicController(FbMessageTopicService fbMessageTopicService) {
		this.fbMessageTopicService = fbMessageTopicService;
	}
	
	
	/***********************************************************************************************
	 * 
	 * DeviceToken fuer ein Topic registrieren
	 * 
	 * @param fbMessageTopicModel DeviceTokens und Topic.
	 * @return Erfolgsmeldung / Fehlermeldung
	 *    
	 ***********************************************************************************************/
	@PutMapping("/subscribeTopic")
	@Operation(description = "Hinzufügen eines MobileDevice-Tokens zu einer Topic")
	public String subscribeTopic(@RequestBody FbMessageTopicModel fbMessageTopicModel) {
		return fbMessageTopicService.subscribeTopic(fbMessageTopicModel);
	}
	
	
	/***********************************************************************************************
	 * 
	 * DeviceToken aus einem Topic entfernen.
	 * 
	 * @param fbMessageTopicModel DeviceTokens und Topic.
	 * @return Erfolgsmeldung / Fehlermeldung
	 *    
	 ***********************************************************************************************/
	@PutMapping("/unsubscribeTopic")
	@Operation(description = "Entfernen eines MobileDevice-Tokens aus einer Topic")
	public String unsubscribeTopic(@RequestBody FbMessageTopicModel fbMessageTopicModel) {
		return fbMessageTopicService.unsubscribeTopic(fbMessageTopicModel);
	}	
	
	
	/***********************************************************************************************
	 * 
	 * Information in welchen Topics ein DeviceToken registriert ist und Zeitpunkt der Registrierung.
	 * 
	 * @param deviceToken registrationToken eines MobileDevices.
	 * @return Erfolgsmeldung / Fehlermeldung
	 *    
	 ***********************************************************************************************/
	@GetMapping("/getTopicsOfDevice")
	@Operation(description = "Information in welchen Topics ein DeviceToken registriert ist")
	public String getTopicsOfDevice(
			@Parameter(description =  "DeviceToken", example = "A91b.....KqVLG", required = true) 
			@RequestParam 
			String deviceToken			
			)  {
		return fbMessageTopicService.getTopicsOfDevice(deviceToken);
	}	
}
