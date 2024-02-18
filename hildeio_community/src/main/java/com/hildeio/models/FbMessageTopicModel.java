package com.hildeio.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/***********************************************************************************************
 * Model FbMessageTopicModel
 ***********************************************************************************************/
@Getter
@Setter
@Schema(description = "Model-Klasse für zum Erstellen eines MessageTopics bzw. hinzufügen/entfernen eines registrationTokens")
public class FbMessageTopicModel {
	
	/***********************************************************************************************
	 * Name des Topics
	 ***********************************************************************************************/
	@Schema(description = "Name des Topics", example = "MeinSchaltaktoren")
	private String topicName;
	
	/***********************************************************************************************
	 * Token des MobileDevice
	 ***********************************************************************************************/
	@Schema(description = "Token des MobileDevice", example= "A91b.....KqVLG")
	private String registrationToken;
}