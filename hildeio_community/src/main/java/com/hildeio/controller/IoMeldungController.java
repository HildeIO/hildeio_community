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

@RestController
@RequestMapping("/meldung")
@Tag(name = "Meldung")
public class IoMeldungController {

	public IoMeldungService ioMeldungService;

	
	/* *********************************************************************************************
	 *
	 * IoMeldungController
	 * 
	 * ********************************************************************************************/		
	public IoMeldungController(IoMeldungService ioMeldungService) {
		this.ioMeldungService = ioMeldungService;
	}
	 

	/* *********************************************************************************************
	 *
	 * CREATE
	 * 
	 *
	 	!HomeMatic - POST:
		!Programm: Servicemeldungen (Firebase)
		
		object oTmpArray = dom.GetObject(ID_SERVICES);
		
		if(oTmpArray) {
		
			string sTmp;
			string sdev;
			string dev_type;
			integer err_value;
			string kategorie = "SERVICE";
			string nachricht;
			var name;
			var timestamp;
			string meldung = "";
								
			foreach(sTmp, oTmpArray.EnumIDs()){
			
				object oTmp = dom.GetObject(sTmp);
				
				if(oTmp){
				
					if(oTmp.IsTypeOf( OT_ALARMDP) && (oTmp.AlState() == asOncoming )){
					
						var trigDP  = dom.GetObject(oTmp.AlTriggerDP());
						var channel = dom.GetObject(trigDP.Channel());
						name = dom.GetObject(channel).Name();
						timestamp = dom.GetObject(trigDP).Timestamp();
						string s_timestamp = timestamp.ToString().Replace(' ', '%20');
						err_value   = trigDP.Value();
						var sdev    = dom.GetObject(channel.Device());
						dev_type    = channel.HssType().Substr(0, 9);
						var nachricht   = trigDP.AlDestMapDP().Name().StrValueByIndex(".", 2 );
						
						if (nachricht != "STICKY_UNREACH") {
						
							!Übersetzen der Fehlermeldung
							if (nachricht == "CONFIG_PENDING") {
								nachricht = "Konfigurationsdaten%20stehen%20zur%20Uebertragung%20an";
							}
							if (nachricht == "LOWBAT") {
								nachricht = "Batteriestand%20niedrig";
							}
							if (nachricht == "UNREACH") {
								nachricht = "Kommunikation%20zur%20Zeit%20gestoert";
							}
							if (nachricht == "DEVICE_IN_BOOTLOADER") 
							{
								nachricht = "Geraet%20startet%20neu";
							}
							if (nachricht == "UPDATE_PENDING") 
							{
								nachricht = "Update%20verfuegbar";
							}
							if (nachricht == "ERROR") 
							{
								if (err_value == 7)
								{
									nachricht = "SABOTAGE";
								}
								
							} 
							
							if(meldung != "") {
								meldung = meldung + ",";
							}
							
							meldung = meldung + "{\"kategorie\":\""# kategorie #"\",\"nachricht\":\""# nachricht#"\",\"name\":\""# name #"\",\"timestamp\":\""# timestamp #"\"}";
		
						} !if (nachricht != "STICKY_UNREACH")
						
					} !if(oTmp.IsTypeOf( OT_ALARMDP) && (oTmp.AlState() == asOncoming ))
				
				} !if(oTmp)
			
			} !foreach(sTmp, oTmpArray.EnumIDs())
			
			meldung = "[" + meldung + "]";
			
			string meldung_controller = dom.GetObject(ID_SYSTEM_VARIABLES).Get("HildeIO").State() + "meldung/create";		
			string curl = "curl -X POST -H \"Content-Type: application/json\" "# meldung_controller #" -d '" + meldung + "'";
		
			dom.GetObject("CUxD.CUX2801001:1.CMD_EXEC").State(curl);
			
		} !if(oTmpArray)

	 *
	 * ********************************************************************************************/	
	@PostMapping("/create")
	@Operation(description = "Neue Servicemeldung aus der HomeMatic in Firestore-Collection ioMeldungen anlegen")
	public String create(@RequestBody List<IoMeldungModel> ioMeldungModels) throws InterruptedException, ExecutionException {
		return ioMeldungService.create(ioMeldungModels, UUID.randomUUID().toString());
	}
}
