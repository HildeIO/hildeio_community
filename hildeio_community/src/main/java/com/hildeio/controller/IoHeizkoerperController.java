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


@RestController
@RequestMapping("/heizkoerper")
@Tag(name = "Heizkoerper")
public class IoHeizkoerperController {

	public IoHeizkoerperService ioHeizkoerperService;

	
	/* *********************************************************************************************
	 *
	 * IoHeizkoerperController
	 * 
	 * ********************************************************************************************/		
	public IoHeizkoerperController(IoHeizkoerperService ioHeizkoerperService) {
		this.ioHeizkoerperService = ioHeizkoerperService;
	}
	
	
	/* *********************************************************************************************
	 *
	 * UPDATE
	 * 
	 *
	 	!HomeMatic - PUT:
	 	!Programm: Heizkörper (Firebase)
	 	
		string iseId = dom.GetObject(26685).ID();
		string name = dom.GetObject(26685).Name().Replace("ö", "oe");
		string interfaceType ="HmIP-RF";
		string address = dom.GetObject(26685).Address();
		string type = "double";
		string valueKey = "SET_POINT_TEMPERATURE";
		
		string temperaturIst;
		string temperaturSoll;
		string  sTmp;
		object oTmpArray = dom.GetObject(iseId).DPs().EnumUsedNames();		
		
		!alle Datenpunkte des aktuellen Heizkörpers durchlaufen
		foreach(sTmp, oTmpArray.EnumIDs()){
		
		  !Objekt für aktuellen Datenpunkt erzeugen
		  object oTmp = dom.GetObject(sTmp);
		
		  !prüfen ob der aktuelle Datenpunkt SET_POINT_TEMPERATURE ist
		  if(oTmp.Name().StrValueByIndex(".", 2 ) == "SET_POINT_TEMPERATURE"){
		    temperaturSoll = oTmp.Value().ToString(1);
		  }
		
		  !prüfen ob der aktuelle Datenpunkt ACTUAL_TEMPERATURE ist
		  if(oTmp.Name().StrValueByIndex(".", 2 ) == "ACTUAL_TEMPERATURE"){
		    temperaturIst = oTmp.Value().ToString(1);
		  }
		}
		
		string heizkoerper_controller= dom.GetObject(ID_SYSTEM_VARIABLES).Get("HildeIO").State() + "heizkoerper/updateHeizkoerper2Firestore";
		
		string curl = "curl -X PUT -H \"Content-Type: application/json\" "#heizkoerper_controller#" -d '{\"iseId\":\""#iseId#"\",\"name\":\""#name#"\",\"interfaceType\":\""#interfaceType#"\",\"valueKey\":\""#valueKey#"\",\"address\":\""#address#"\",\"type\":\""#type#"\",\"temperaturIst\":\""#temperaturIst#"\",\"temperaturSoll\":\""#temperaturSoll#"\"}'";
		
		dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
	 *
	 * ********************************************************************************************/		
	@PutMapping("/updateHeizkoerper2Firestore")
	@Operation(description = "Speichert Ist-Temperatur, Soll-Temperatur und Betriebsart in Firestore-Collection ioHeizkoerper")
	public String updateHeizkoerper2Firestore(@RequestBody IoHeizkoerperModel ioHeizkoerperModel) throws InterruptedException, ExecutionException {
		return ioHeizkoerperService.updateHeizkoerper2Firestore(ioHeizkoerperModel, UUID.randomUUID().toString());
	}
	
	
	/* *********************************************************************************************
	 *
	 * checkWochenplan
	 * 
	 * ********************************************************************************************/		
	@PutMapping("/checkWochenplan")
	@Operation(description = "Für zyklischen Aufruf der Wochenplan-Logik durch die HomeMatic")
	public String checkWochenplan() throws InterruptedException, ExecutionException {
		return ioHeizkoerperService.checkWochenplan(UUID.randomUUID().toString());
	}		
}
