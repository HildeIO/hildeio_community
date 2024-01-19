package com.hildeio.controller;

import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hildeio.Log4Hilde;
import com.hildeio.models.FbMessageModel;
import com.hildeio.models.Log4HildePushNotificationModel;
import com.hildeio.services.FbNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/fcmNotification")
@Tag(name = "Notification")
public class FbNotificationController {
	
	@Autowired
	FbNotificationService fcmNotificationService;

	@Autowired
	Log4Hilde log4Hilde;

	
	/* *********************************************************************************************
	*
	* send2Topic
	* 
	* ********************************************************************************************/
	@PostMapping("/send2Topic")
	@Operation(description = "Versenden einer Push-Nachricht an ein bestimmtes Topic")
	public ResponseEntity<String> send2Topic(@RequestBody FbMessageModel fbMessageModel) {
		
		fbMessageModel.setTokenTopicDevice("/topics/" + fbMessageModel.getTokenTopicDevice().trim());		
		return this.send(fbMessageModel);
	}	

	
	/* *********************************************************************************************
	*
	* send2Device
	* 
	* ********************************************************************************************/
	@PostMapping("/send2Device")
	@Operation(description = "Versenden einer Push-Nachricht an ein bestimmtes MobileDevice")
	public ResponseEntity<String> send2Device(@RequestBody FbMessageModel fbMessageModel) {
		
		return this.send(fbMessageModel);
	}	
			
	
	/* *********************************************************************************************
	*
	* send
	* 
	* ********************************************************************************************/
	private ResponseEntity<String> send(FbMessageModel fbMessageModel) {
		
		String eventId = UUID.randomUUID().toString();
		
		try {
				
			this.log4Hilde.doInfoLog(
					"01-Parameter", 
					"topic_device = " + fbMessageModel.getTokenTopicDevice() + ", title = " + fbMessageModel.getTitle() + ", body = " + fbMessageModel.getBody(),  
					new JSONObject(),
					eventId
					);				
			
			Log4HildePushNotificationModel pushNotification = 
					fcmNotificationService.sendPushNotification(fbMessageModel.getTokenTopicDevice(), fbMessageModel.getTitle(), fbMessageModel.getBody(), eventId);
			
			this.log4Hilde.doInfoLog(
					"02-pushNotification", 
					new ObjectMapper().writeValueAsString(pushNotification), 
					new JSONObject(),
					eventId
					);
			
			if(pushNotification.getReturnCode() == 0) {		
				
				return new ResponseEntity<>(pushNotification.getMessageId(), HttpStatus.OK);
				
			} else {
				
				return new ResponseEntity<>(pushNotification.getException().getMessage(), HttpStatus.BAD_REQUEST);
			}
			
		} catch(Exception exception) {
			
			this.log4Hilde.doErrorLog("EX-01", exception, new JSONObject(), eventId);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
		}		
	}
}