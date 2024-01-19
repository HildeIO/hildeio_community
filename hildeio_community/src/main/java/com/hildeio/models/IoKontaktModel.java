package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Schema(description = "Model-Klasse der Tür- und Fensterkontakte")
public class IoKontaktModel {

	@Schema(description = "Channel-ID des Tür- und Fensterkontakts", example = "58429")
	private String iseId;
	
	@Schema(description = "Channel-Name des Tür- und Fensterkontakts", example= "Fenster_Bad")
	private String kontaktName;
	
	@Schema(description = "Kontatkstatus", example= "true (= geöffnet) | false (= geschlossen)")
	private String kontaktValue;
	
	@Schema(accessMode = AccessMode.READ_ONLY)
	private String displayName;	
}
