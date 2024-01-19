package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Model-Klasse für zum Erstellen eines MessageTopics bzw. hinzufügen/entfernen eines registrationTokens")
public class FbMessageTopicModel {
	
	@Schema(description = "Name des Topics", example = "MeinSchaltaktoren")
	private String topicName;
	
	@Schema(description = "Token des MobileDevice", example= "A91b.....KqVLG")
	private String registrationToken;
}
