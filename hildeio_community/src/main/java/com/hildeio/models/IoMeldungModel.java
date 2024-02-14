package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * 
 * Model IoMeldungModel
 *    
 ***********************************************************************************************/
@Getter
@Setter
@Schema(description = "Model-Klasse Servicemeldung")
public class IoMeldungModel {

	
	/***********************************************************************************************
	 * Erstellungszeitpunkt
	 ***********************************************************************************************/
	@Schema(description = "Erstellungszeitpunkt", example = "11.05.2020")
	private String timestamp;

	
	/***********************************************************************************************
	 * Channel-Name der Servicenachricht
	 ***********************************************************************************************/
	@Schema(description = "Channel-Name der Servicenachricht", example = "Heizkörperthermostat_Arbeitszimmer:1")
	private String name;

	
	/***********************************************************************************************
	 * Beschreibung
	 ***********************************************************************************************/
	@Schema(description = "Beschreibung", example = "SABOTAGE")
	private String nachricht;

	
	/***********************************************************************************************
	 * Art der Meldung (derzeit immer SERVICE)
	 ***********************************************************************************************/
	@Schema(description = "Art der Meldung (derzeit immer SERVICE)", example = "SERVICE")
	private String kategorie;
}

