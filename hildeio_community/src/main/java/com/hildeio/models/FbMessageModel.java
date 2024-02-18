package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * Model FbMessageModel
 ***********************************************************************************************/
@Getter
@Setter
@Schema(description = "Model-Klasse für zum Erstellen eines MessageTopics bzw. hinzufügen/entfernen eines registrationTokens")
public class FbMessageModel {
	
	/***********************************************************************************************
	 * Topic oder MobileDevice-Token
	 ***********************************************************************************************/
	@Schema(description =  "Topic oder MobileDevice-Token", example = "'FensterTopic' oder 'A91b.....KqVLG'")
	private String tokenTopicDevice;
	
	/***********************************************************************************************
	 * Titel der Nachricht
	 ***********************************************************************************************/
	@Schema(description = "Titel der Nachricht", example = "Geöffnete Fenster")
	private String title;

	/***********************************************************************************************
	 * Beschreibung
	 ***********************************************************************************************/
	@Schema(description = "Beschreibung", example = "Es sind mehrere Fenster geöffnet")
	private String body;
}
