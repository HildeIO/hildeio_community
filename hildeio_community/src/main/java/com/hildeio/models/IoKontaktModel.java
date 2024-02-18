package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * Model IoKontaktModel
 ***********************************************************************************************/
@Getter
@Setter
@Schema(description = "Model-Klasse der Tür- und Fensterkontakte")
public class IoKontaktModel {
	
	/***********************************************************************************************
	 * Channel-ID des Tuer- und Fensterkontakts
	 ***********************************************************************************************/
	@Schema(description = "Channel-ID des Tür- und Fensterkontakts", example = "58429")
	private String iseId;
	
	/***********************************************************************************************
	 * Channel-Name des Tuer- und Fensterkontakts
	 ***********************************************************************************************/
	@Schema(description = "Channel-Name des Tür- und Fensterkontakts", example= "Fenster_Bad")
	private String kontaktName;
	
	/***********************************************************************************************
	 * Kontatkstatus
	 ***********************************************************************************************/
	@Schema(description = "Kontatkstatus", example= "true (= geöffnet) | false (= geschlossen)")
	private String kontaktValue;
	
	/***********************************************************************************************
	 * Anzeigename
	 ***********************************************************************************************/
	@Schema(accessMode = AccessMode.READ_ONLY)
	private String displayName;	
}