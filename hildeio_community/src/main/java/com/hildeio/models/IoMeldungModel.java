package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Model-Klasse Servicemeldung")
public class IoMeldungModel {

	@Schema(description = "Erstellungszeitpunkt", example = "11.05.2020")
	private String timestamp;

	@Schema(description = "Channel-Name der Servicenachricht", example = "Heizkörperthermostat_Arbeitszimmer:1")
	private String name;
	
	@Schema(description = "Beschreibung", example = "SABOTAGE")
	private String nachricht;

	@Schema(description = "Art der Meldung (derzeit immer SERVICE)", example = "SERVICE")
	private String kategorie;
}

