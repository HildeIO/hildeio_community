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

@RestController
@RequestMapping("/schaltaktor")
@Tag(name = "Schaltaktoren")
public class IoSchaltaktorController {

	public IoSchaltaktorService ioSchaltaktorService;

	
	/* *********************************************************************************************
	 *
	 * IoSchaltaktorController
	 * 
	 * ********************************************************************************************/		
	public IoSchaltaktorController(IoSchaltaktorService ioSchaltaktorService) {
		this.ioSchaltaktorService = ioSchaltaktorService;
	}
	

	/* *********************************************************************************************
	 *
	 * UPDATE: Homematic -> Firestore
	 * 
	 *
	 *
	 	!HomeMatic - PUT:
	 	!Programm: Schaltaktoren (Firebase)
	 	
		object aktuellesDevice = dom.GetObject("$src$").Device();
		
		string iseId = dom.GetObject(aktuellesDevice).ID();
		string name = dom.GetObject(aktuellesDevice).Name().Replace("ü", "ue");
		string interfaceType = "";
		string value = "";
		string address = dom.GetObject(aktuellesDevice).Address();
		string type;
		string valueKey = "STATE";
		
		if(dom.GetObject(aktuellesDevice).Interface() == 1011){
			interfaceType = "HmIP-RF";
			type = "boolean";
			value = dom.GetObject(aktuellesDevice).DPByHssDP("STATE").Value();
		} else {
			interfaceType = "BidCos-RF";
			type = "string";
			value = dom.GetObject(aktuellesDevice).State().ToString();
		}
		
		string schaltaktor_controller= dom.GetObject(ID_SYSTEM_VARIABLES).Get("HildeIO").State() + "schaltaktor/updateSchaltaktor2Firestore";
		
		string curl = "curl -X PUT -H \"Content-Type: application/json\" "#schaltaktor_controller#" -d '{\"iseId\":\""#iseId#"\",\"name\":\""#name#"\",\"interfaceType\":\""#interfaceType#"\",\"valueKey\":\""#valueKey#"\",\"address\":\""#address#"\",\"type\":\""#type#"\",\"value\":\""#value#"\"}'";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
	 *
	 * ********************************************************************************************/	
	@PutMapping("/updateSchaltaktor2Firestore")
	@Operation(description = "Speichert den Schaltaktor-Zustand (ein/aus) in Firestore-Collection ioSchaltaktoren")
	public String updateSchaltaktor2Firestore(@RequestBody IoSchaltaktorModel ioSchaltaktorModel) throws InterruptedException, ExecutionException {
		return ioSchaltaktorService.updateSchaltaktor2Firestore(ioSchaltaktorModel, UUID.randomUUID().toString());
	}
}
