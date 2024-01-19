package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Model-Klasse für zum Erstellen eines MessageTopics bzw. hinzufügen/entfernen eines registrationTokens")
public class FbMessageModel {

	@Schema(description =  "Topic oder MobileDevice-Token", example = "'FensterTopic' oder 'A91b.....KqVLG'")
	private String tokenTopicDevice;
	
	@Schema(description = "Titel der Nachricht", example = "Geöffnete Fenster")
	private String title;

	@Schema(description = "Beschreibung", example = "Es sind mehrere Fenster geöffnet")
	private String body;
}
